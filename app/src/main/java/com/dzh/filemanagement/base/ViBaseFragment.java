package com.dzh.filemanagement.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;


/**
 * 父类Fragment
 * Created by DELL on 2017/1/17.
 */

public abstract class ViBaseFragment extends Fragment {

    protected RelativeLayout titleBar;
    private FragmentManager fragmentManager;
    //当前正在展示的Fragment
    private Fragment showFragment;
    private Dialog dialog;
    public View mRootView;
    protected boolean isDestory = false;
    protected String TAG = getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(container.getContext()).inflate(getContentView(), container, false);
        ButterKnife.bind(this, mRootView);
        fragmentManager = getChildFragmentManager();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    /**
     * 初始化view
     *
     * @param view
     */
    protected void initView(View view) {
    }

    @Override
    public void onStop() {
        super.onStop();
        isDestory = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isDestory = false;
    }

    /**
     * 获取布局id
     *
     * @return
     */
    protected abstract int getContentView();

    /**
     * 切换fragment
     *
     * @param resId
     * @param fragment
     */
    public void showFragment(int resId, Fragment fragment, int enterAnimation, int exitAnimation) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                enterAnimation, exitAnimation, enterAnimation, exitAnimation);
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
        fragmentTransaction.commit();
    }

    /**
     * 切换fragment
     *
     * @param resId
     * @param fragment
     */
    public void showFragment(int resId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
        fragmentTransaction.commit();
    }

    /**
     * 切换 嵌套fragment
     *
     * @param resId
     * @param fragment
     */
    public void showChildFragment(int resId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        //隐藏正在暂时的Fragment
        if (showFragment != null) {
            fragmentTransaction.hide(showFragment);
        }
        //展示需要显示的Fragment对象
        Fragment mFragment = getChildFragmentManager().findFragmentByTag(fragment.getClass().getName());
        if (mFragment != null) {
            fragmentTransaction.show(mFragment);
            showFragment = mFragment;
        } else {
            fragmentTransaction.add(resId, fragment, fragment.getClass().getName());
            showFragment = fragment;
        }
        fragmentTransaction.commit();
    }


    protected void onBackPressed() {

    }

    /**
     * 跳转activity
     *
     * @param activity
     */
    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestory = true;
    }

    /**
     * @author Lynn
     * created at 2017/2/17  13:59
     * 处理 socket 回调
     */
    protected void dealMsg() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isDestory = isHidden();
    }

    @Override
    public void onPause() {
        super.onPause();
        isDestory = true;
    }


}
