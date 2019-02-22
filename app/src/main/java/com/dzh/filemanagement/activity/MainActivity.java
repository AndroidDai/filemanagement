package com.dzh.filemanagement.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.SystemClock;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.base.BaseActivity;
import com.dzh.filemanagement.fragment.IOnBackPressed;
import com.dzh.filemanagement.fragment.LeftMenuFragment;
import com.dzh.filemanagement.fragment.ViewPageFragment;
import com.dzh.filemanagement.view.SlidingMenu;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends BaseActivity {

    SlidingMenu mSlidingMenu = null;
    LeftMenuFragment mLeftFragment = null;
    ViewPageFragment mViewPageFragment = null;
    ActionBar mActionBar = null;
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
        return R.layout.activity_main;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    private void init() {
        mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);// 主布局(左中右)

        View lefMenuView = getLayoutInflater().inflate(R.layout.left_frame, null);
        View centerView = getLayoutInflater().inflate(R.layout.center_frame, null);
        mSlidingMenu.addViews(lefMenuView, centerView);

        // 替换左中右三页布局为Fragement
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        mLeftFragment = new LeftMenuFragment();
        transaction.replace(R.id.left_frame, mLeftFragment);

        mViewPageFragment = new ViewPageFragment();
        transaction.replace(R.id.center_frame, mViewPageFragment);

        transaction.commit();

    }

    public ViewPageFragment getViewPageFragment() {
        return mViewPageFragment;
    }

    private void initListener() {
        mViewPageFragment.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // Toast.makeText(MainActivity.this, "" + position, 0).show();
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

    public void showLeft() {
        mSlidingMenu.showLeftView();
    }
    
    public void hideLeft() {
        mSlidingMenu.hideLeft();
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
            
            if(mSlidingMenu.isShow()) {
                mSlidingMenu.hideLeft();
                return true;
            }

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
                	System.arraycopy(mHits, 1, mHits, 0, mHits.length -1);
                	mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                	if(mHits[0] > ( SystemClock.uptimeMillis() - 500)) {
                		MainActivity.this.finish();
                	} else {
                		Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                	}
                } else {
                    return true;
                }
            }
        }
        return true;
    }

}
