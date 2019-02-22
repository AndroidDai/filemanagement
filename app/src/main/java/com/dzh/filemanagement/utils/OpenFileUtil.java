package com.dzh.filemanagement.utils;

import java.io.File;

import android.content.ComponentName;

import androidx.appcompat.app.AppCompatActivity;

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
