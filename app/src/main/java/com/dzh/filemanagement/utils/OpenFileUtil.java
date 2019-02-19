package com.dzh.filemanagement.utils;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import io.zhuliang.appchooser.AppChooser;


public class OpenFileUtil {


    private static ComponentName[] excluded = new ComponentName[]{
            new ComponentName("nutstore.android", "nutstore.android.SendToNutstoreIndex"),
            new ComponentName("nutstore.android.debug", "nutstore.android.SendToNutstoreIndex"),
    };

    public static void openFile(String filePath , AppCompatActivity context) {
        File file = new File(filePath);
        AppChooser.from(context)
                .file(file)
                .excluded(excluded)
                .authority("com.dzh.filemanagement.fileprovider")
                .load();
    }
}
