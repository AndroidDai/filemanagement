package com.dzh.filemanagement.activity;

import android.app.Dialog;
import android.os.Bundle;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.fragment.ApkFragment;
import com.dzh.filemanagement.fragment.FileNameFragment;
import com.dzh.filemanagement.fragment.FileTypeFragment;
import com.dzh.filemanagement.fragment.ImageFragment;
import com.dzh.filemanagement.fragment.MusicFragment;
import com.dzh.filemanagement.fragment.VideoFragment;
import com.dzh.filemanagement.fragment.WordFragment;
import com.dzh.filemanagement.fragment.ZipFragment;
import com.dzh.filemanagement.utils.UiUtil;
import com.umeng.analytics.MobclickAgent;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class ShowActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String aClass = getIntent().getStringExtra("class");
        switch (aClass) {
            case "image":
                transaction.add(R.id.show_detial, new ImageFragment());
                break;
            case "music":
                transaction.add(R.id.show_detial, new MusicFragment());
                break;
            case "video":
                transaction.add(R.id.show_detial, new VideoFragment());
                break;
            case "word":
                transaction.add(R.id.show_detial, new WordFragment());
                break;
            case "apk":
                transaction.add(R.id.show_detial, new ApkFragment());
                break;
            case "zip":
                transaction.add(R.id.show_detial, new ZipFragment());
                break;
            case "filename":
                transaction.add(R.id.show_detial,new FileNameFragment());
                break;
            case "filetype":
                transaction.add(R.id.show_detial,new FileTypeFragment());
                break;
        }
        transaction.commit();
    }
    private Dialog mProgressDialog = null;
    public void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = UiUtil.createLoadingDialog(this, msg);
            mProgressDialog.show();
        }
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
