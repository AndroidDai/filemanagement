package com.dzh.filemanagement.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.adapter.VideoAdapter;
import com.dzh.filemanagement.utils.ACache;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.taurus.PullToRefreshView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private VideoAdapter mAdapter;
    private PullToRefreshView mPullToRefreshView;
    private Gson mGson;
    private ImageView mLoading;
    private TextView mLoadingText;
    private ACache mCatch;
    private SharedPreferences mPreferences;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecyclerView.setAdapter(mAdapter = new VideoAdapter(getContext(), mFiles));
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mPullToRefreshView.setVisibility(View.VISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.setOnItemClickLitener(new VideoAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_video, container, false);
        
        TextView title = (TextView) ret.findViewById(R.id.title);
        title.setText("视频");
        ImageView reicon = (ImageView)ret.findViewById(R.id.return_index);
        reicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        
        mLoading = (ImageView) ret.findViewById(R.id.loading_gif);
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mLoadingText = (TextView) ret.findViewById(R.id.loading_text);
        mPullToRefreshView = (PullToRefreshView) ret.findViewById(R.id.pull_to_refresh);
        Glide.with(getContext()).load(R.drawable.loading)
                .asGif().into(mLoading);
        mFiles = new ArrayList<>();
        mGson = new Gson();
        mCatch = ACache.get(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".mp4");
                        addCatch();
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mAdapter.notifyDataSetChanged();
                                    mPullToRefreshView.setRefreshing(false);
                                    Toast.makeText(getContext(), "刷新完成", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (Exception e){
                            
                        }
                    }
                }).start();

            }
        });
        initDate();
        return ret;
    }

    private void initDate() {
        //开线程初始化数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                judge();
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        }).start();
    }

    private void judge() {
        try {
            mPreferences = getContext().getSharedPreferences("table", Context.MODE_PRIVATE);
        } catch (Exception e) {
            //子线程未销毁可能时执行
        }

        boolean first = mPreferences.getBoolean("firstVideo", true);
        int num = mPreferences.getInt("numVideo", 0);

        long time = mPreferences.getLong("VideoTime", 0);
        long cha = System.currentTimeMillis() - time;
        //判断缓存时间是否过期

        if (!first && time != 0 & cha < 86400000) {
            for (int i = 0; i < num; i++) {
                String s = String.valueOf(i);
                String string = mCatch.getAsString(s + "video");
                if (string!=null) {
                    Log.d("aaa", "judge: " +string);
                    File file = mGson.fromJson(string, File.class);
                    mFiles.add(file);
                }
            }
        } else {
    
            mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".mp4");
            addCatch();
        }
    }

    private void addCatch() {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < mFiles.size(); i++) {
            String s = mGson.toJson(mFiles.get(i));
            strings.add(s);
        }
        for (int i = 0; i < strings.size(); i++) {
            String s = String.valueOf(i);
            mCatch.put(s + "video", strings.get(i), ACache.TIME_DAY);
        }


        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("firstVideo", false);
        edit.putInt("numVideo", strings.size());
        edit.putLong("VideoTime", System.currentTimeMillis());
        edit.commit();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Video_Fragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Video_Fragment");
    }
}