package com.dzh.filemanagement.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dzh.filemanagement.R;
import com.dzh.filemanagement.activity.ShowActivity;
import com.dzh.filemanagement.adapter.MusicAdapter;
import com.dzh.filemanagement.base.ViBaseFragment;
import com.dzh.filemanagement.core.common.MediaResourceManager;
import com.dzh.filemanagement.entity.Audio;
import com.dzh.filemanagement.utils.OpenFileUtil;
import com.dzh.filemanagement.utils.TimeUtils;
import com.dzh.filemanagement.utils.ToastUtils;
import com.dzh.filemanagement.view.DeleteFileDialog;
import com.dzh.filemanagement.view.IOnDialogBtnClickListener;
import com.snail.commons.entity.ZipHelper;
import com.snail.commons.interfaces.Callback;
import com.snail.commons.utils.SysShareUtils;
import com.yalantis.taurus.PullToRefreshView;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends ViBaseFragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private List<Audio> datas;
    private MusicAdapter mAdapter;
    private ImageView mLoading;
    private TextView mLoadingText;
    private TextView mSelectPrompt;
    private RelativeLayout rl_bottom;
    private PullToRefreshView mPullToRefreshView;
    private Context mContext;
    private ShowActivity mActivity;
    private Boolean isSelect = false;
    public static final int MSG_LOAD_SUEECSS = 0x1000;
    protected static final int MSG_REFRESH_SUEECSS = 0x1001;
    protected static final int MSG_FAILURE = 0x1002;
    protected static final int MSG_SHOW_ZIPTOAST = 0x1003;
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_SUEECSS:
                    mAdapter.setNewData(datas);
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mPullToRefreshView.setVisibility(View.VISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    break;
                case MSG_REFRESH_SUEECSS:
                    mAdapter.setNewData(datas);
                    mPullToRefreshView.setVisibility(View.VISIBLE);
                    mPullToRefreshView.setRefreshing(false);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    break;
                case MSG_FAILURE:
                    datas.clear();
                    mAdapter.setNewData(datas);
                    mLoading.setVisibility(View.VISIBLE);
                    break;
                case MSG_SHOW_ZIPTOAST:
                    mAdapter.deselectAll();
                    Bundle bundle = msg.getData();
                    String path = bundle.getString("path");
                    ToastUtils.showToast(mContext, "压缩后文件目录为：" + path);
                    mActivity.hideProgress();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void initView(View view) {
        super.initView(view);
        mContext = getActivity();
        TextView title = (TextView) mRootView.findViewById(R.id.title);
        title.setText("音乐");
        mLoading = (ImageView) mRootView.findViewById(R.id.loading_gif);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
        mLoadingText = (TextView) mRootView.findViewById(R.id.loading_text);
        mPullToRefreshView = (PullToRefreshView) mRootView.findViewById(R.id.pull_to_refresh);
        rl_bottom = mRootView.findViewById(R.id.rl_file_select);
        mSelectPrompt = mRootView.findViewById(R.id.file_select);
        Glide.with(getContext()).load(R.drawable.loading)
                .asGif().into(mLoading);
        datas = new ArrayList<>();
        mAdapter = new MusicAdapter(datas, mContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager( new GridLayoutManager(mContext, 3));
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        initData(true);
        initListener();
    }

    public void initListener() {

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(false);
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isSelect) {
                    mAdapter.toggleSelection(position);
                } else {
                    OpenFileUtil.openFile(datas.get(position).getPath() , mActivity);
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (!isSelect) {
                    isSelect = true;
                    mSelectPrompt.setText("取消");
                    rl_bottom.setVisibility(View.VISIBLE);
                }
                mAdapter.toggleSelection(position);
                return true;
            }
        });
        mRootView.findViewById(R.id.file_delete).setOnClickListener(this);
        mRootView.findViewById(R.id.file_zip).setOnClickListener(this);
        mRootView.findViewById(R.id.file_share).setOnClickListener(this);
        mRootView.findViewById(R.id.return_index).setOnClickListener(this);
        mSelectPrompt.setOnClickListener(this);
    }

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentView() {
        return R.layout.fragment_image;
    }


    private void initData(final boolean isLoad) {
        //开线程初始化数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Audio> imagesFromMedia = MediaResourceManager.getAudiosFromMedia();
                datas.clear();
                datas.addAll(imagesFromMedia);
                Message message = new Message();
                if (isLoad) {
                    message.what = MSG_LOAD_SUEECSS;
                } else {
                    message.what = MSG_REFRESH_SUEECSS;
                }

                myHandler.sendMessage(message);
            }
        }).start();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.file_delete:
                delect();
                break;
            case R.id.file_zip:
                tozip();
                break;
            case R.id.file_share:
                HashSet<Integer> selection = mAdapter.getSelection();
                int size = selection.size();
                if (size == 1) {
                    String filePath = datas.get(0).getPath();
                    SysShareUtils.INSTANCE.shareFile(mContext, "文件分享", new File(filePath));
                } else if (size == 1) {
                    ToastUtils.showToast(mContext, "请选择要分享的文件");
                } else {
                    ToastUtils.showToast(mContext, "分享文件个数不能超过1个");
                }
                break;
            case R.id.return_index:
                getActivity().finish();
                break;
            case R.id.file_select:
                if (isSelect) {
                    mSelectPrompt.setText("开始选择");
                    mAdapter.deselectAll();
                    rl_bottom.setVisibility(View.GONE);
                } else {
                    mSelectPrompt.setText("取消");
                    rl_bottom.setVisibility(View.VISIBLE);
                }
                isSelect = !isSelect;
                break;
            default:
                break;
        }
    }

    /**
     * 压缩
     */
    private void tozip() {
        HashSet<Integer> selection = mAdapter.getSelection();
        int size = selection.size();
        if (size == 0) {
            ToastUtils.showToast(mContext, "请先选择一个要压缩的文件");
            return;
        }
        List<File> files = new ArrayList<>();
        for (Integer integer : selection) {
            File file = new File(datas.get(integer).getPath());
            files.add(file);
        }
        final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/filemanagement/zip");
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = TimeUtils.getWorkTime(System.currentTimeMillis());
        mActivity.showProgress("正在拼命压缩");
        ZipHelper.INSTANCE.zip().addSourceFiles(files).setTarget(file.getAbsolutePath(), fileName).execute(new Callback<File>() {
            @Override
            public void onCallback(@Nullable File obj) {
                Message msg = new Message();
                msg.what = MSG_SHOW_ZIPTOAST;
                Bundle bundle = new Bundle();
                bundle.putString("path", file.getAbsolutePath());
                msg.setData(bundle);
                myHandler.sendMessage(msg);

            }
        });

    }

    /**
     * 删除文件
     */
    private void delect() {
        if (datas.size() == 0) {
            ToastUtils.showToast(mContext, "请先选择一个要删除的文件");
            return;
        }
        HashSet<Integer> selection = mAdapter.getSelection();
        List<String> mSelectList = new ArrayList<>();
        for (Integer integer : selection) {
            mSelectList.add(datas.get(integer).getPath());
        }
        DeleteFileDialog dialog = new DeleteFileDialog(mContext, getActivity(), mSelectList);
        dialog.setOnDialogBtnClickListener(new IOnDialogBtnClickListener() {
            @Override
            public void onOkClick(View view, String result) {
                mAdapter.deselectAll();
                initData(false);
            }

            @Override
            public void onCancelClick(View view) {

            }
        });
        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        mActivity = (ShowActivity) getActivity();
        super.onAttach(context);
    }
}
