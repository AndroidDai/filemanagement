package com.dzh.filemanagement.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.dzh.filemanagement.R;
import com.dzh.filemanagement.utils.ViUIUtils;

/**
 * 父类Activity
 * Created by DELL on 2017/1/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected TextView tv_title_bar_left, tv_title_bar_title, tv_title_bar_right;
    protected ImageView iv_title_bar_left, iv_title_bar_right;
    private FragmentManager fragmentManager;
    //当前正在展示的Fragment
    private Fragment showFragment;
    public boolean isDestory = false;
    protected String TAG = "ViBaseActivity";
    //接收DV端主动发过来的消息
    public int screenHeight;
    public int screenWidth;
    public int statusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ViAppActivityManager.getInstance().addActivity(this);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        screenHeight = ViUIUtils.getScreenHeight(this);
        screenWidth = ViUIUtils.getScreenWidth(this);
        statusBarHeight = getStatusBarHeight();
        //全屏
        if (isFullScreen()) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        //获得fragmentManager对象
        fragmentManager = getSupportFragmentManager();
        initTitleBar();

        initView();
        Resources res = this.getResources();
    }



    /**
     * 获取状态栏的高度
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置是否全屏
     *
     * @param enable
     */
    private void setFullScreen(boolean enable) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (enable) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().setAttributes(lp);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 初始化titleBar
     */
    protected void initTitleBar() {
        tv_title_bar_left = findViewById(R.id.tv_title_bar_left);
        tv_title_bar_title = findViewById(R.id.tv_title_bar_title);
        tv_title_bar_right = findViewById(R.id.tv_title_bar_right);
        iv_title_bar_left = findViewById(R.id.iv_title_bar_left);
        iv_title_bar_right = findViewById(R.id.iv_title_bar_right);
    }

    /**
     * titleBar相关状态
     *
     * @param title_bar_left       左侧TextView的文字
     * @param title_bar_title      居中的title文字
     * @param title_bar_right      右侧的Textview的文字
     * @param drawable_right       右侧的Imageview的图片
     * @param drawable_left        左侧的Imageview的图片
     * @param leftImageVisibility  左侧Imageview是否显示隐藏
     * @param leftTextVisibility   左侧TextView是否显示隐藏
     * @param rightImageVisibility 右侧的Imageview是否显示隐藏
     * @param rightTextVisibility  右侧的TextView的是否显示隐藏
     */
    protected void titleBarStatus(String title_bar_title, String title_bar_left, int leftTextVisibility,
                                  int rightTextVisibility, String title_bar_right,
                                  int drawable_left, int leftImageVisibility,
                                  int drawable_right, int rightImageVisibility) {

        iv_title_bar_left.setVisibility(leftImageVisibility);
        tv_title_bar_left.setVisibility(leftTextVisibility);
        tv_title_bar_left.setText(title_bar_left);
        tv_title_bar_title.setText(title_bar_title);
        iv_title_bar_left.setImageResource(drawable_left);

        tv_title_bar_right.setVisibility(rightTextVisibility);
        tv_title_bar_right.setText(title_bar_right);
        iv_title_bar_right.setVisibility(rightImageVisibility);
        iv_title_bar_right.setImageResource(drawable_right);

    }

    protected void setTitle(String title_bar_title) {
        tv_title_bar_title.setText(title_bar_title);
    }

    protected void activityFinsh() {
        iv_title_bar_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDestory = false;
    }


    /**
     * 初始化View
     */
    protected void initView() {
    }

    /**
     * 布局id
     *
     * @return
     */
    protected abstract int getContentView();


    /**
     * 是否全屏
     *
     * @return
     */
    protected abstract boolean isFullScreen();


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 重新加载activity
     */
    public void reLoad() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }


    /**
     * 切换fragment
     *
     * @param resId
     * @param fragment
     */

    public void showFragment(int resId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // fragmentTransaction.setCustomAnimations(R.anim.anim_activity_bottom_in, R.anim.anim_activity_bottom_out);
        //隐藏正在暂时的Fragment
        if (showFragment != null) {
            fragmentTransaction.hide(showFragment);
        }
        //展示需要显示的Fragment对象
        Fragment mFragment = fragmentManager.findFragmentByTag(fragment.getClass().getName());
        if (mFragment != null) {
            fragmentTransaction.show(mFragment);
            showFragment = mFragment;
        } else {
            fragmentTransaction.add(resId, fragment, fragment.getClass().getName());
            showFragment = fragment;
        }
        fragmentTransaction.commitAllowingStateLoss();


    }


    /**
     * 删除fragment
     *
     * @param fragment
     */
    public void deleteFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        //展示需要显示的Fragment对象
        Fragment mFragment = fragmentManager.findFragmentByTag(fragment.getClass().getName());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mFragment != null) {
            fragmentTransaction.remove(mFragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
            fragmentTransaction.commit();
        }
    }

    /**
     * 跳转activity
     *
     * @param activity
     */
    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestory = true;
        ViAppActivityManager.getInstance().removeActivity(this);

    }

}
