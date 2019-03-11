package com.dzh.filemanagement.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dzh.filemanagement.R;
import com.dzh.filemanagement.entity.Audio;
import com.dzh.filemanagement.utils.FmFileUtils;

import java.util.HashSet;
import java.util.List;
import androidx.annotation.Nullable;

/**
 * Created by 齐泽威 on 2016/12/7.
 */

public class MusicAdapter extends BaseQuickAdapter<Audio, BaseViewHolder> {
    private Context mContext;
    private HashSet<Integer> mSelected;

    public MusicAdapter(@Nullable List<Audio> data, Context context) {
        super(R.layout.item_music, data);
        mContext = context;
        mSelected = new HashSet<Integer>();
    }

    @Override
    protected void convert(BaseViewHolder helper, Audio item) {
        if (mSelected.contains(helper.getLayoutPosition())) {
            helper.setBackgroundRes(R.id.view_background, R.drawable.shape_album_select_sel);
        } else {
            helper.setBackgroundRes(R.id.view_background, R.drawable.shape_album_select_cancle);
        }
        helper.setText(R.id.item_music_filename, FmFileUtils.getFileName(item.getPath()));
        Bitmap bm = getAlbumArt(item.getAlbumId());
        Glide.with(mContext)
                .load(bm)
                .placeholder(R.mipmap.file_music)
                .error(R.mipmap.file_music)
                .into((ImageView) helper.getView(R.id.img_file_music));
    }

    public HashSet<Integer> getSelection() {
        return mSelected;
    }

    public void deselectAll() {
        // 退出选择界面
        for (Integer integer : mSelected) {
            notifyItemChanged(integer);
        }
        mSelected.clear();
    }

    public void selectAll() {
        mSelected.clear();
        //全选
        for (int i = 0; i < getItemCount(); i++) {
            mSelected.add(i);
        }
        notifyDataSetChanged();
    }

    /**
     * 用于推拽多选图片
     *
     * @param pos
     */
    public void toggleSelection(int pos) {
        //  需要加上头布局的个数
        int headerLayoutCount = getHeaderLayoutCount();
        pos = pos + headerLayoutCount;
        if (mSelected.contains(pos)) {
            mSelected.remove(pos);
        } else {
            mSelected.add(pos);
        }
        notifyItemChanged(pos);
    }

    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param album_id 专辑ID
     * @return
     */
    private Bitmap getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = mContext.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        }
        return bm;
    }
}

