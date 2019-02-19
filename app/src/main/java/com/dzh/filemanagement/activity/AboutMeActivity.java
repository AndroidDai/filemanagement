package com.dzh.filemanagement.activity;



import android.os.Bundle;
import android.view.View;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.base.BaseActivity;

public class AboutMeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_about_me;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    public void back(View view) {
        finish();
    }
}
