package com.dzh.filemanagement.base;

import android.app.Activity;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

/**
 * 管理Activity
 * Created by DELL on 2017/1/17.
 */

public class ViAppActivityManager {
    private static Stack<Activity> mActivityStack;
    private static ViAppActivityManager mAppManager;

    private ViAppActivityManager() {
    }

    /**
     * 单一实例
     */
    public static ViAppActivityManager getInstance() {
        if (mAppManager == null) {
            mAppManager = new ViAppActivityManager();
        }
        return mAppManager;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }

        mActivityStack.add(activity);
    }

    /**
     * 获取栈顶Activity
     */
    public Activity getTopActivity() {
        Activity activity = mActivityStack.lastElement();
        return activity;
    }

    /**
     * 结束栈顶Activity
     */
    public void killTopActivity() {
        Activity activity = mActivityStack.lastElement();
        killActivity(activity);
    }

    /**
     * 移除当前Activity
     */
    public void removeActivity(Activity activity) {
        mActivityStack.remove(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void killActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void killActivity(Class<?> cls) {
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void killAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 结束所有继承BaseCamera的Activity
     */
    public void killCameraActivity() {
        int size = mActivityStack.size();
        if (size == 0)
            return;
        for (int i = 0; i < size; i++) {
            if (null != mActivityStack.get(i) && i < size) {
                mActivityStack.get(i).finish();
                mActivityStack.remove(mActivityStack.get(i));
                size--;
            }
        }
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            killAllActivity();
            android.app.ActivityManager activityMgr = (android.app.ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {

        }
    }

    /**
     * 获取当前栈的size
     */
    public int getActivitySize() {
        return mActivityStack.size();
    }

}
