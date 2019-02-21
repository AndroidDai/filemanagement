package com.dzh.filemanagement.utils;

import android.util.Log;


/**
 * Logcat统一管理类
 * Created by DELL on 2017/9/27.
 */

public class ViLogUtils {
    static String className;//类名
    static String content;
    static String methodName;//方法名
    static int lineNumber;//行数

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(content).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return unicodeToUTF_8(buffer.toString());
        //settings_return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        content = sElements[1].getFileName();
        className = TAG;
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    private ViLogUtils() {
            /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "daizhihao";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(msg));
    }

    public static void d(String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.d(className, createLog(msg));
    }

    public static void e(String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(msg));
    }

    public static void v(String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.v(className, createLog(msg));
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, createLog(msg));
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(msg));
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.e(tag, createLog(msg));
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            getMethodNames(new Throwable().getStackTrace());
        Log.v(tag, createLog(msg));
    }

    public static String unicodeToUTF_8(String src) {
        if (null == src) {
            return null;
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < src.length(); ) {
            char c = src.charAt(i);
            if (i + 6 < src.length() && c == '\\' && src.charAt(i + 1) == 'u') {
                String hex = src.substring(i + 2, i + 6);
                try {
                    out.append((char) Integer.parseInt(hex, 16));
                } catch (NumberFormatException nfe) {
                    nfe.fillInStackTrace();
                }
                i = i + 6;
            } else {
                out.append(src.charAt(i));
                ++i;
            }
        }
        return out.toString();

    }
}