package com.dzh.filemanagement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.dzh.filemanagement.R;
import com.dzh.filemanagement.utils.ACache;

import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.text.DecimalFormat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.free_number)
    TextView mFreeView;
    @BindView(R.id.total_number)
    TextView mTotalView;
    @BindView(R.id.file_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static boolean isNight;
    private long exitTime = 0;
    private GuillotineAnimation mAnimation;
    private SharedPreferences mTable;
    private String mFreeS;
    private String mToalS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.file_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mTable = getSharedPreferences("table", MODE_PRIVATE);
        //断头台菜单设置
        ImageView menus = (ImageView) findViewById(R.id.content_hamburger);
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        ((DrawerLayout) findViewById(R.id.activity_file)).addView(guillotineMenu);
        mAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), menus)
                .setStartDelay(250)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        mTable.edit().putBoolean("menuOpen", true).apply();
                    }

                    @Override
                    public void onGuillotineClosed() {
                        mTable.edit().putBoolean("menuOpen", false).apply();
                    }
                })
                .build();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        findViewById(R.id.file_bottom).setOnClickListener(this);

        LinearLayout image = (LinearLayout) findViewById(R.id.file_image);
        LinearLayout music = (LinearLayout) findViewById(R.id.file_music);
        LinearLayout video = (LinearLayout) findViewById(R.id.file_video);
        LinearLayout word = (LinearLayout) findViewById(R.id.file_word);
        LinearLayout apk = (LinearLayout) findViewById(R.id.file_apk);
        LinearLayout zip = (LinearLayout) findViewById(R.id.file_zip);
        zip.setOnClickListener(this);
        apk.setOnClickListener(this);
        video.setOnClickListener(this);
        music.setOnClickListener(this);
        image.setOnClickListener(this);
        word.setOnClickListener(this);

        //MaterialDesign水波纹效果
        MaterialRippleLayout.on(image).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(apk).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(zip).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(video).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(word).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(music).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();

        LinearLayout clear = (LinearLayout) findViewById(R.id.menu_clear);
        LinearLayout check = (LinearLayout) findViewById(R.id.menu_check);
        LinearLayout about = (LinearLayout) findViewById(R.id.menu_about);
        LinearLayout quit = (LinearLayout) findViewById(R.id.menu_quit);
        RelativeLayout title = (RelativeLayout) findViewById(R.id.menu_title);

        check.setOnClickListener(this);
        clear.setOnClickListener(this);
        about.setOnClickListener(this);
        quit.setOnClickListener(this);
        title.setOnClickListener(this);

        MaterialRippleLayout.on(check).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(clear).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(about).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(quit).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(title).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();

        //底边栏存储空间显示
        getFreeSpace();
        getTotalSpace();

   }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (v.getId()) {
            case R.id.file_image:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "image");
                startActivity(intent);
                break;
            case R.id.file_music:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "music");
                startActivity(intent);
                break;
            case R.id.file_video:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "video");
                startActivity(intent);
                break;
            case R.id.file_word:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "word");
                startActivity(intent);
                break;
            case R.id.file_apk:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "apk");
                startActivity(intent);
                break;
            case R.id.file_zip:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "zip");
                startActivity(intent);
                break;
            case R.id.file_bottom:
                intent.setClass(this, FileBrowseActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_clear:
                ACache mCatch = ACache.get(MainActivity.this);
                mCatch.clear();
                SharedPreferences table = getSharedPreferences("table", MODE_PRIVATE);
                SharedPreferences.Editor edit = table.edit();
                edit.putBoolean("firstImage", true);
                edit.putBoolean("firstMusic", true);
                edit.putBoolean("firstVideo", true);
                edit.putBoolean("firstWord", true);
                edit.putBoolean("firstApk", true);
                edit.putBoolean("firstZip", true);
                edit.apply();
                Toast.makeText(this, "清理缓存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_check:
                Toast.makeText(this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.menu_quit:
                finish();
                break;
            case R.id.menu_title:

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    //    获取总的内存空间并控制显示
    public void getTotalSpace() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        float i = 1024 * 1024 * 1024;
        float bytes = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytes = sf.getTotalBytes() / i;
        } else {
            Toast.makeText(this, "您的手机版本太低，暂时不支持内存查询", Toast.LENGTH_SHORT).show();
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        mToalS = df.format(bytes);
        mTotalView.setText(mToalS);
    }

    //    获取剩余的内存空间并控制显示
    public void getFreeSpace() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        float i = 1024 * 1024 * 1024;
        float bytes = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytes = sf.getFreeBytes() / i;
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        mFreeS = df.format(bytes);
        mFreeView.setText(mFreeS);
    }

    /**
     * 双击返回键退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            boolean open = mTable.getBoolean("menuOpen", false);
            if (open) {
                mAnimation.close();
                return true;
            }


            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTotalSpace();
                        getFreeSpace();
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "内存信息更新完成。", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public void onResume() {
        super.onResume();

    }

    public void onPause() {
        super.onPause();

    }
}
