package io.zhuliang.appchooser.action;

import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.io.File;

import io.zhuliang.appchooser.AppChooser;
import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.ui.view.ViewFragment;
import io.zhuliang.appchooser.util.FileUtils;

/**
 * @author Zhu Liang
 */

public class ViewAction {

    private static final String FRAGMENT_TAG = BuildConfig.APPLICATION_ID + ".fragment.tag.VIEW";

    private AppChooser mAppChooser;
    private ActionConfig mActionConfig = new ActionConfig();

    public ViewAction(AppChooser appChooser) {
        mAppChooser = appChooser;
        mActionConfig.actionName = Intent.ACTION_VIEW;
    }

    public ViewAction file(@NonNull File file) {
        FileUtils.checkFile(file);
        mActionConfig.pathname = file.getAbsolutePath();
        return this;
    }

    public ViewAction requestCode(int requestCode) {
        mActionConfig.requestCode = requestCode;
        return this;
    }

    public ViewAction excluded(ComponentName... componentNames) {
        mActionConfig.excluded = componentNames;
        return this;
    }

    public ViewAction authority(String authority) {
        mActionConfig.authority = authority;
        mActionConfig.isUriExposed = false;
        return this;
    }

    public void load() {
        FragmentActivity activity = mAppChooser.getActivity();
        if (activity == null) {
            return;
        }
        Fragment fragment = mAppChooser.getFragment();
        if (fragment != null) {
            mActionConfig.fromActivity = false;
            ViewFragment.newInstance(mActionConfig).show(fragment.getFragmentManager(),
                    FRAGMENT_TAG);
        } else {
            mActionConfig.fromActivity = true;
            ViewFragment.newInstance(mActionConfig).show(activity.getSupportFragmentManager(),
                    FRAGMENT_TAG);
        }
    }
}
