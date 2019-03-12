package com.dzh.filemanagement.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.base.BaseActivity;
import com.dzh.filemanagement.fragment.ViewPageFragment;
import com.dzh.filemanagement.utils.IOnBackPressed;

import androidx.viewpager.widget.ViewPager;

public class FileBrowseActivity extends BaseActivity {

    ViewPageFragment mViewPageFragment = null;
    IOnBackPressed mOnBackPressed = null;// 点击回退键触发
    long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        init();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_file_browse;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    private void init() {

        mViewPageFragment = new ViewPageFragment();
        showFragment(R.id.center_frame , mViewPageFragment);

    }

    public ViewPageFragment getViewPageFragment() {
        return mViewPageFragment;
    }

    private void initListener() {
        mViewPageFragment.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // Toast.makeText(FileBrowseActivity.this, "" + position, 0).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    public void setOnBackPressedListener(IOnBackPressed listener) {
        this.mOnBackPressed = listener;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Process.killProcess(Process.myPid());

    }

    public void goToPage(int position) {
        mViewPageFragment.setPage(position);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mViewPageFragment.getCurrentPageIndex() == 2) {
                goToPage(1);
                return true;
            } else if (mViewPageFragment.getCurrentPageIndex() == 1) {
                goToPage(0);
                return true;
            } 

            if (mOnBackPressed != null) {
                boolean needExit = mOnBackPressed.onBackPressed();
                System.out.println(needExit);
                if (needExit) {
                    FileBrowseActivity.this.finish();
                } else {
                    return true;
                }
            }
        }
        return true;
    }

}
