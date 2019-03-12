package com.dzh.filemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzh.filemanagement.R;
import com.dzh.filemanagement.core.common.FileType;
import com.dzh.filemanagement.utils.FmFileUtils;

import java.io.File;
import java.util.List;

public class SimpleFileListAdapter extends BaseAdapter {

    private List<String> mFileNames = null;
    private Context mContext = null;

    public SimpleFileListAdapter(Context context, List<String> fileNames) {
        mContext = context;
        mFileNames = fileNames;
    }

    @Override
    public int getCount() {
        return mFileNames.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ImageView imageView = null;
        TextView textView = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.simple_file_list_item, parent, false);
        }

        imageView = (ImageView) view.findViewById(R.id.mImageSimpleFileType);
        textView = (TextView) view.findViewById(R.id.mTvSimpleFileName);

        String path = mFileNames.get(position);

        int fileType = FileType.TYPE_UNKNOWN;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            fileType = FileType.TYPE_FOLDER;
        } else {
            fileType = FileType.getFileType(path.toLowerCase());
        }

        imageView.setImageResource(FileType.getResourceIdByType(fileType));

        textView.setText(FmFileUtils.getFileName(path));

        return view;
    }

}
