package com.dzh.filemanagement.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.utils.SharedPreferenceUtil;

public class StartUI extends Activity {
    boolean mIsFirstTimeUse = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start);
        mIsFirstTimeUse = SharedPreferenceUtil.getFirstTimeUse();
        
        if (mIsFirstTimeUse) {
            Intent intent = new Intent(StartUI.this, WelcomActivity.class);
            startActivity(intent);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent mainIntent = new Intent(StartUI.this, FileBrowseActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    finish();
                }
            }, 1200);
        }

    }
}
