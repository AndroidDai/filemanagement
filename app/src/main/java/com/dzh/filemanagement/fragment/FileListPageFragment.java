package com.dzh.filemanagement.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.activity.MainActivity;
import com.dzh.filemanagement.adapter.FileListAdapter;
import com.dzh.filemanagement.adapter.FileListAdapter.OnCheckBoxChangedListener;
import com.dzh.filemanagement.core.common.FileComparator;
import com.dzh.filemanagement.core.common.FileType;
import com.dzh.filemanagement.core.engine.ResourceManager;
import com.dzh.filemanagement.dao.DaoFactory;
import com.dzh.filemanagement.dao.impl.FavoriteDao;
import com.dzh.filemanagement.entity.Favorite;
import com.dzh.filemanagement.entity.NaviInfo;
import com.dzh.filemanagement.entity.SimpleFileInfo;
import com.dzh.filemanagement.utils.FmFileUtils;
import com.dzh.filemanagement.utils.OpenFileUtil;
import com.dzh.filemanagement.utils.SharedPreferenceUtil;
import com.dzh.filemanagement.utils.SysShareUtils;
import com.dzh.filemanagement.utils.ToastUtils;
import com.dzh.filemanagement.utils.UiUtil;
import com.dzh.filemanagement.view.CopyBottomChooseBar;
import com.dzh.filemanagement.view.DeleteFileDialog;
import com.dzh.filemanagement.view.FileInfoDialog;
import com.dzh.filemanagement.view.FileListBottomOperatorMenu;
import com.dzh.filemanagement.view.FileListBottomToolBar;
import com.dzh.filemanagement.view.IOnDialogBtnClickListener;
import com.dzh.filemanagement.view.IOnMenuItemClickListener;
import com.dzh.filemanagement.view.MoveBottomChooseBar;
import com.dzh.filemanagement.view.NewFileDialog;
import com.dzh.filemanagement.view.RenameDialog;
import com.dzh.filemanagement.view.SortDialog;
import com.dzh.filemanagement.view.SwipListView;
import com.dzh.filemanagement.view.SwipListView.OnSwipListItemRemoveListener;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;


/**
 * 文件目录浏览的Fragment
 *
 * @author Administrator
 */
public class FileListPageFragment extends Fragment implements OnSwipListItemRemoveListener, OnItemClickListener, IOnBackPressed, OnItemLongClickListener {

    private static final String TAG = "FileListPageFragment";
    private static final int MSG_UPDATE_DATA = 0x1001;
    private static final int MSG_FINISH = 0x1002;
    private static final int MSG_REFRESH = 0x1003;
    private static final int MSG_PRE_LOAD = 0x1004;
    private static final int MSG_GO_TO_SELECT = 0x1005;
    private static final int MSG_SHOW_PROGRESS = 0x1006;

    private List<SimpleFileInfo> mGlobalSimpleFileList = new ArrayList<SimpleFileInfo>();

    private List<SimpleFileInfo> mFileItems = new ArrayList<SimpleFileInfo>();
    private Stack<NaviInfo> mTopNaviInfoStack = new Stack<NaviInfo>();
    //选中的文件
    private List<String> mCheckedList = new ArrayList<String>();
    private List<String> mOperatorList = new ArrayList<String>();

    public String mRootPath = ResourceManager.mExternalStoragePath;

    private View mView = null;
    private MainActivity mActivity = null;
    private ViewPageFragment mViewPageFragment = null;
    private LinearLayout mLinearTopNavi = null;// 顶部目录导航
    private HorizontalScrollView mTopNaviScroll = null;// 顶部滚动部件
    private SwipListView mListView = null;
    private FileListBottomOperatorMenu mBottomMenu = null;// 底部弹出式操作菜单
    private FileListBottomToolBar mBottomToolBar = null;// 底部操作工具条
    private CopyBottomChooseBar mCopyBottomChooseBar = null;// 底部确认取消按钮
    private MoveBottomChooseBar mMoveBottomChooseBar = null;
    private Dialog mProgressDialog = null;
    private View mNothingView = null;

    private BottomMenuOnclickListener mMenuOnclickListener = null;
    private FileListAdapter mAdapter = null;
    private LoadCurrentPageFilelistThrad mLoadCurrPageThread = null;
    private int mPosition = -1;
    private int mLastTimePosition = 0;
    private List<String> noClickPath;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_SHOW_PROGRESS) {
                if (mProgressDialog == null) {
                    mProgressDialog = UiUtil.createLoadingDialog(mView.getContext(), "正在玩命加载...");
                    mProgressDialog.show();
                }
            } else if (msg.what == MSG_PRE_LOAD) {
                mAdapter.notifyDataSetInvalidated();
            } else if (msg.what == MSG_UPDATE_DATA) {
                mFileItems.add((SimpleFileInfo) msg.obj);
            } else if (msg.what == MSG_FINISH) {

                for (int i = 0; i < mGlobalSimpleFileList.size(); i++) {
                    mFileItems.add(mGlobalSimpleFileList.get(i));
                }

                if (mFileItems.size() == 0) {
                    mNothingView.setVisibility(View.VISIBLE);
                } else {
                    mNothingView.setVisibility(View.GONE);
                }

                Collections.sort(mFileItems, new FileComparator());
                mAdapter.notifyDataSetChanged();
                if (mLastTimePosition != 0) {
                    mListView.setSelection(mLastTimePosition);
                    mLastTimePosition = 0;
                }
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

            } else if (msg.what == MSG_REFRESH) {
                refresh();
            } else if (msg.what == MSG_GO_TO_SELECT) {
                String targetPath = (String) msg.obj;
                int position = FmFileUtils.getPositionInFileList(mFileItems, targetPath);
                mListView.setSelection(position);
            }
        }
    };

    public void startAnim() {
        mListView.startLayoutAnimation();
    }

    // 获取上一次的位置
    private int getLastTimePostion() {
        int lastTimePosition = 0;
        NaviInfo info = mTopNaviInfoStack.peek();
        if (info != null) {
            lastTimePosition = info.getPosition();
        }

        return lastTimePosition;
    }

    public SwipListView getListView() {
        return mListView;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.file_list_frame, null);


        mAdapter = new FileListAdapter(mView.getContext(), mFileItems);
        noClickPath = new ArrayList<>();
        noClickPath.add("/storage/emulated");
        noClickPath.add("/storage");
        initView();

        mAdapter.setOnScrollListenerToListView(mListView);

        autoDeploymentTopNavisStack(mRootPath);

        setListeners();

        return mView;
    }

    private void setListeners() {
        mAdapter.OnCheckBoxChangedListener(new OnCheckBoxChangedListener() {

            @Override
            public void onCheckChanged(int position, boolean isChecked) {
                if (isChecked) {
                    if (position < mFileItems.size()) {
                        mCheckedList.add(mFileItems.get(position).getPath());
                    }
                } else {
                    mCheckedList.remove(mFileItems.get(position).getPath());
                }
                FileListPageFragmentHelper.hideShowBottomOperatorMenu(mCheckedList, mBottomMenu);

                if (mBottomMenu.hasSelecteAll(mFileItems, mCheckedList)) {
                    mBottomMenu.setSelectNothing();
                } else {
                    mBottomMenu.setSelecteAll();
                }
            }
        });

        mListView.setOnItemLongClickListener(this);

        mBottomMenu.setOnItemClickListener(mMenuOnclickListener);

        mBottomToolBar.setOnItemClickListener(mMenuOnclickListener);
    }

    private void initView() {

        mActivity = (MainActivity) getActivity();
        mViewPageFragment = mActivity.getViewPageFragment();

        mLinearTopNavi = (LinearLayout) mView.findViewById(R.id.mLinearTopNavi);
        mTopNaviScroll = (HorizontalScrollView) mView.findViewById(R.id.mTopNaviScroll);
        mMenuOnclickListener = new BottomMenuOnclickListener(mCheckedList, mView, this, mAdapter);
        mBottomMenu = (FileListBottomOperatorMenu) mView.findViewById(R.id.mBottomMenu);
        mBottomToolBar = (FileListBottomToolBar) mView.findViewById(R.id.mBottomToolBar);
        mCopyBottomChooseBar = (CopyBottomChooseBar) mView.findViewById(R.id.mBottomChooseBar);
        mMoveBottomChooseBar = (MoveBottomChooseBar) mView.findViewById(R.id.mBottomMoveChooseBar);
        mNothingView = mView.findViewById(R.id.nothing);

        mListView = (SwipListView) mView.findViewById(R.id.fileList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemRemoveListener(this);
        mListView.setOnItemClickListener(this);
        ((MainActivity) getActivity()).setOnBackPressedListener(this);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 滑动删除
     */
    @Override
    public void removeItem(int position) {
        mMenuOnclickListener.deleteFile(mFileItems.get(position).getPath(), position);
    }

    /**
     * 扫描当前文件夹下的异步任务
     *
     * @author Administrator
     */
    class LoadCurrentPageFilelistThrad extends Thread {

        private static final int PROGRESS_SHOW_MIN = 200;
        private String path = "/";
        private String mTargetPath = null;
        private boolean mIsNeedSetAppName = false;

        public LoadCurrentPageFilelistThrad(String path) {
            if (FmFileUtils.isLegalPath(path)) {
                this.path = path;
            } else {
                Log.e(TAG, "非法的文件路径！");
            }
        }

        public LoadCurrentPageFilelistThrad(String path, String targetPath) {
            if (FmFileUtils.isLegalPath(path)) {
                this.path = path;
            } else {
                Log.e(TAG, "非法的文件路径！");
            }
            mTargetPath = targetPath;
        }

        @Override
        public void run() {
            boolean showHideFile = SharedPreferenceUtil.getShowHideFiles();
            mHandler.sendEmptyMessage(MSG_PRE_LOAD);
            File file = new File(path);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }
            File[] files = new File(path).listFiles();
            SimpleFileInfo fileInfo = null;
            mGlobalSimpleFileList.clear();

            if (files != null) {
                // for
                if (files.length > PROGRESS_SHOW_MIN) {
                    mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
                }
                if (!file.getAbsoluteFile().equals(ResourceManager.mExternalStoragePath) && !file.getAbsoluteFile().equals("/")) {
                    mIsNeedSetAppName = true;
                }
                for (int i = 0; i < files.length; i++) {
                    try {
                        if (!showHideFile && FmFileUtils.isHideFile(files[i].getName())) {
                            continue;
                        }

                        String canonicalPath = files[i].getCanonicalPath();
                        if (files[i].isDirectory()) {
                            // 文件夹
                            fileInfo = new SimpleFileInfo(canonicalPath, FileType.TYPE_FOLDER);
                        } else if (files[i].isFile()) {
                            // 填充文件属性
                            fileInfo = new SimpleFileInfo(canonicalPath, files[i].length());
                        }
                        fileInfo.setCreateTime(files[i].lastModified());
                        fileInfo.setFileSize(FmFileUtils.getChildCount(files[i]));
                        if (!mIsNeedSetAppName) {
                            fileInfo.setAppName("");
                        }
                        mGlobalSimpleFileList.add(fileInfo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }// end for
            }

            mHandler.sendEmptyMessage(MSG_FINISH);

            if (mTargetPath != null) {
                Message msg = new Message();
                msg.what = MSG_GO_TO_SELECT;
                msg.obj = mTargetPath;
                mHandler.sendMessage(msg);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mLoadCurrPageThread);
    }

    // 点击回退键触发
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mPosition == position) {
            mPosition = -1;
            return;
        }

        // 如果在选择事件的模式
        if (mBottomMenu.isShow() && !mCopyBottomChooseBar.isShow() && !mMoveBottomChooseBar.isShow()) {
            SimpleFileInfo info = mFileItems.get(position);
            if (info.isChecked()) {
                info.setChecked(false);
                mCheckedList.remove(mFileItems.get(position).getPath());
            } else {
                info.setChecked(true);
                mCheckedList.add(mFileItems.get(position).getPath());
            }

            if (mCheckedList.size() == 0) {
                mBottomMenu.hide();
            }
            // 改变全选图标
            if (mBottomMenu.hasSelecteAll(mFileItems, mCheckedList)) {
                mBottomMenu.setSelectNothing();
            } else {
                mBottomMenu.setSelecteAll();
            }

            mAdapter.notifyDataSetChanged();
            return;
        }

        String path = mFileItems.get(position).getPath();

        if (mOperatorList.contains(path)) {
            ToastUtils.showToast(mView.getContext(), "你在开玩笑吗？");
            return;
        }
        File file = new File(path);

        if (file.isFile()) {
            OpenFileUtil.openFile(path, mActivity);
        } else {
            mFileItems.clear();
            mCheckedList.clear();
            mBottomMenu.setSelecteAll();
            addToNaviList(path);
            mLoadCurrPageThread = new LoadCurrentPageFilelistThrad(path);
            mLoadCurrPageThread.start();
        }
    }


    private void autoMoveToBottom() {
        mTopNaviScroll.post(new Runnable() {
            @Override
            public void run() {
                mTopNaviScroll.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

    /* 设置底部导航条 */
    private void addToNaviList(String path) {
        View naviItemView = LayoutInflater.from(mView.getContext()).inflate(R.layout.file_navi_item, null);
        final TextView tv = (TextView) naviItemView.findViewById(R.id.mTvNaviItem);
        String text = FmFileUtils.getFileName(path);
        if (text.endsWith(FmFileUtils.getFileName(mRootPath))) {
            if (text.length() >= 1) {
                text = "内部存储";
            }
        } else if (text == null || text.equals("") || text.equals("/")) {
            text = "Root";
        }
        tv.setText(text);
        naviItemView.setTag(path);

        naviItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (noClickPath.contains(tag) || "/".equals(tag)) {
                    return;
                }
                while (!getCurrentPath().equals(tag)) {
                    mTopNaviInfoStack.pop();
                }
                reloadNaviViews(tag);

            }
        });
        mAdapter.setFirstTimeLoad(true);

        mTopNaviInfoStack.push(new NaviInfo(naviItemView, mListView.getFirstVisiblePosition()));
        mLastTimePosition = 0;
        mLinearTopNavi.addView(naviItemView);
        autoMoveToBottom();
    }

    public void refresh() {
        refresh(null);
    }

    public void refresh(String targetPath) {
        mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
        mAdapter.setFirstTimeLoad(true);
        String path = getCurrentPath();
        mFileItems.clear();
        mCheckedList.clear();
        mOperatorList.clear();
        if (targetPath != null) {
            mLoadCurrPageThread = new LoadCurrentPageFilelistThrad(path, targetPath);
        } else {
            mLoadCurrPageThread = new LoadCurrentPageFilelistThrad(path);
        }
        mLastTimePosition = mListView.getFirstVisiblePosition();
        mLoadCurrPageThread.start();
        startAnim();
    }

    public void gotoSelecttion(String path) {
        int position = FmFileUtils.getPositionInFileList(mFileItems, path);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(position);
    }

    public String getCurrentPath() {
        String path = "/";
        if (mTopNaviInfoStack.size() > 1) {
            path = (String) mTopNaviInfoStack.peek().getView().getTag();
        }
        return path;
    }

    public void reloadNaviViews(String path) {
        mLinearTopNavi.removeAllViews();
        mCheckedList.clear();
        mBottomMenu.setSelecteAll();
        mFileItems.clear();
        mAdapter.notifyDataSetChanged();
        for (int i = 0; i < mTopNaviInfoStack.size(); i++) {
            mLinearTopNavi.addView(mTopNaviInfoStack.get(i).getView());
        }
        mAdapter.setFirstTimeLoad(true);
        mLoadCurrPageThread = new LoadCurrentPageFilelistThrad(path);
        mLoadCurrPageThread.start();
    }

    // 自动生成顶部导航
    public void autoDeploymentTopNavisStack(String path) {
        mLinearTopNavi.removeAllViews();
        mTopNaviInfoStack.clear();
        mCheckedList.clear();
        mFileItems.clear();
        List<String> pathStackList = FmFileUtils.generatePathStack(path);
        for (int i = 0; i < pathStackList.size(); i++) {
            addToNaviList(pathStackList.get(i));
        }
        refresh(path);
    }

    @Override
    public boolean onBackPressed() {

        // 没在当前页
        if (mViewPageFragment.getCurrentPageIndex() != 0) {
            return true;
        }

        String curPath = getCurrentPath();
        if (mBottomMenu.isShow()) {// 隐藏操作栏
            mBottomMenu.hide();
            FmFileUtils.selecteAll(mFileItems, false);
            mBottomMenu.setSelecteAll();
            mCheckedList.clear();
            mAdapter.notifyDataSetChanged();
            return false;
        } else if (mTopNaviInfoStack.size() > 1) {// 不退出
            if (curPath.equals(ResourceManager.mExternalStoragePath) || curPath.equals("/")) {
                return true;
            }
            mLastTimePosition = getLastTimePostion();
            mTopNaviInfoStack.pop();
            reloadNaviViews(getCurrentPath());
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void cancalRemove(int position) {
        mPosition = position;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPosition == position) {
            return false;
        }
        mBottomMenu.show();
        SimpleFileInfo info = mFileItems.get(position);
        info.setChecked(true);
        mAdapter.notifyDataSetChanged();
        if (!mCheckedList.contains(info.getPath())) {
            mCheckedList.add(info.getPath());
        }
        if (mBottomMenu.hasSelecteAll(mFileItems, mCheckedList)) {
            mBottomMenu.setSelectNothing();
        } else {
            mBottomMenu.setSelecteAll();
        }
        return true;
    }

    class BottomMenuOnclickListener implements IOnMenuItemClickListener, IOnDialogBtnClickListener {

        private List<String> mOperatorPaths = null;
        private View mView = null;
        private FileListPageFragment mFragment = null;
        private FileListAdapter mAdapter = null;
        private SwipListView mListView = null;

        public BottomMenuOnclickListener(List<String> mOperatorPaths, View mView, FileListPageFragment fragment, FileListAdapter adapter) {
            this.mOperatorPaths = mOperatorPaths;
            this.mView = mView;
            this.mFragment = fragment;
            mListView = mFragment.getListView();
            mAdapter = adapter;
        }

        @Override
        public void onItemClick(View rootView, View view) {
            switch (view.getId()) {
                case R.id.mOpCopy:// 压缩
                    tozip();
                    mBottomMenu.hide();
                    break;

                case R.id.mOpDel:
                    deleteFiles();// 删除
                    break;

                case R.id.mOpMove:// 解压
                    unzip();
                    break;
                case R.id.mOpSelectAll:// 选择所有

                    break;

                case R.id.mOpAddToFavorite:
                    addToFavorite();
                    break;

                case R.id.mOpReName:
                    rename();// 重命名
                    break;
                case R.id.mOpShare:
                    share();// 分享
                    break;

                case R.id.mToolBarNew:// 新建文件
                    newFile();
                    break;

                case R.id.mOpFileInfo:// 查看详情
                    showDetail();
                    break;

                case R.id.mToolBarRefresh:
                    refresh();
                    break;

                case R.id.mToolBarSetting:
                    mActivity.showLeft();
                    break;

                case R.id.mToolBarSort:
                    sort();
                    break;
                default:
                    break;
            }
        }

        private void addToFavorite() {
            if (mCheckedList.size() == 0) {
                ToastUtils.showToast(mView.getContext(), "请先选择收藏的文件");
                return;
            }
            FavoriteDao dao = DaoFactory.getFavoriteDao(mView.getContext());
            List<Favorite> favorites = new ArrayList<Favorite>();
            for (int i = 0; i < mCheckedList.size(); i++) {
                if (null != dao.findFavoriteByFullPath(mCheckedList.get(i))) {
                    continue;
                }
                Favorite favorite = FmFileUtils.generateFavorateByPath(mCheckedList.get(i));
                favorites.add(favorite);
            }
            dao.insertFavorites(favorites);
            if (favorites.size() == mCheckedList.size()) {
                ToastUtils.showToast(mActivity, "成功添加 " + favorites.size() + " 条记录到收藏!");
            } else {
                ToastUtils.showToast(mActivity, "添加到收藏成功" + favorites.size() + "个，" + (mCheckedList.size() - favorites.size()) + "个已经存在了.");
            }
            reset();
        }

        private void reset() {
            mCheckedList.clear();
            FmFileUtils.selecteAll(mFileItems, false);
            mAdapter.notifyDataSetChanged();
            mBottomMenu.hide();
        }

        private void share() {
            if (!checkOperatorPath("请选择一个要分享的文件", "暂不支持分享多个文件")) {
                return;
            }
            String filePath = mOperatorPaths.get(0);
            SysShareUtils.INSTANCE.shareFile(mActivity, "文件分享", new File(filePath));
            reset();

        }

        private boolean checkOperatorPath(String empty, String multi) {
            if (mOperatorPaths.size() > 1) {
                ToastUtils.showToast(mView.getContext(), empty);
                return false;
            } else if (mOperatorPaths.size() == 0) {
                ToastUtils.showToast(mView.getContext(), multi);
                return false;
            }
            return true;
        }

        // 查看详情
        private void showDetail() {
            if (!checkOperatorPath("请选择一个要查看的文件", "暂不支持查看多个文件")) {
                return;
            }
            FileInfoDialog dialog = new FileInfoDialog(mView.getContext(), mOperatorPaths.get(0));
            dialog.show();
            reset();
        }

        // 排序
        private void sort() {
            SortDialog dialog = new SortDialog(mView.getContext(), mFileItems, mAdapter);
            dialog.show();
        }

        // 新建
        private void newFile() {
            NewFileDialog dialog = new NewFileDialog(mView.getContext(), mFileItems, getCurrentPath(), mFragment);
            dialog.show();
        }

        // 解压
        private void unzip() {

            if (mCheckedList.size() == 0) {
                ToastUtils.showToast(mView.getContext(), "请先选择一个要解压的文件");
                return;
            }
            mMoveBottomChooseBar.show();
            mOperatorList = new ArrayList<String>(mCheckedList);
            mCheckedList.clear();
            FmFileUtils.selecteAll(mFileItems, false);
            mAdapter.notifyDataSetChanged();
        }

        // 压缩
        private void tozip() {

            if (mCheckedList.size() == 0) {
                ToastUtils.showToast(mView.getContext(), "请先选择一个要压缩的文件");
                return;
            }
            mCopyBottomChooseBar.show();
            mOperatorList = new ArrayList<String>(mCheckedList);
            FmFileUtils.selecteAll(mFileItems, false);
            mAdapter.notifyDataSetChanged();
        }

        // 删除文件
        public void deleteFiles() {
            if (mOperatorPaths.size() == 0) {
                ToastUtils.showToast(mView.getContext(), "请先选择一个要删除的文件");
                return;
            }

            DeleteFileDialog dialog = new DeleteFileDialog(mView.getContext(), getActivity(), mOperatorPaths);
            dialog.setOnDialogBtnClickListener(new IOnDialogBtnClickListener() {

                @Override
                public void onOkClick(View view, String result) {
                    mCheckedList.clear();
                    mBottomMenu.hide();
                    refresh();
                }

                @Override
                public void onCancelClick(View view) {
                    mCheckedList.clear();
                    mBottomMenu.setSelecteAll();
                    mAdapter.notifyDataSetChanged();
                }
            });
            dialog.show();
        }

        public void deleteFile(String name, final int position) {
            List<String> temp = new ArrayList<String>();
            temp.add(name);
            DeleteFileDialog dialog = new DeleteFileDialog(mView.getContext(), getActivity(), temp);
            dialog.setOnDialogBtnClickListener(new IOnDialogBtnClickListener() {

                @Override
                public void onOkClick(View view, String result) {
                    mBottomMenu.hide();
                    if (mCheckedList.contains(mFileItems.get(position).getPath())) {
                        mCheckedList.remove(mFileItems.get(position).getPath());
                        FileListPageFragmentHelper.hideShowBottomOperatorMenu(mCheckedList, mBottomMenu);
                    }

                    mFileItems.remove(position);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelClick(View view) {
                }
            });
            dialog.show();
        }

        private void rename() {
            if (mOperatorPaths.size() > 1) {
                ToastUtils.showToast(mView.getContext(), "不支持对多个文件命名");
                return;
            } else if (mOperatorPaths.size() == 0) {
                ToastUtils.showToast(mView.getContext(), "请先选择一个要操作的文件");
                return;
            }
            String path = mOperatorPaths.get(0);
            RenameDialog dialog = new RenameDialog(mView.getContext(), mFileItems, path);
            dialog.setOnDialogBtnClickListener(this);
            dialog.show();

        }

        @Override
        public void onOkClick(View view, String newPath) {
            boolean result = FmFileUtils.rename(mOperatorPaths.get(0), newPath);
            if (result) {
                ToastUtils.showToast(mView.getContext(), "重命名成功！");
                refresh();
                if (this.mListView != null) {
                    int position = FmFileUtils.getPositionInFileList(mFileItems, newPath) + 4;
                    mListView.setSelection(position);
                }
                mOperatorPaths.clear();
            } else {
                ToastUtils.showToast(mView.getContext(), "重命名失败！");
                mOperatorPaths.clear();
            }
        }

        @Override
        public void onCancelClick(View view) {
        }

        @Override
        public void onSelecteAll(View view, boolean selecteAll) {
            FmFileUtils.selecteAll(mFileItems, !selecteAll);
            FileListPageFragmentHelper.refreshCheckList(mFileItems, mCheckedList);
            if (mBottomMenu.hasSelecteAll(mFileItems, mCheckedList)) {
                mBottomMenu.setSelectNothing();
            } else {
                mBottomMenu.setSelecteAll();
            }
            mAdapter.notifyDataSetChanged();
            mBottomMenu.show();
        }
    }


}
