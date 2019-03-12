package com.dzh.filemanagement.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dzh.filemanagement.R;
import com.dzh.filemanagement.entity.Video;
import java.util.HashSet;
import java.util.List;
import androidx.annotation.Nullable;

/**
 * Created by ddd on 2016/12/7.
 */

public class VideoAdapter extends BaseQuickAdapter<Video, BaseViewHolder> {

    private Context mContext;
    private HashSet<Integer> mSelected;

    public VideoAdapter(@Nullable List<Video> data, Context context) {
        super(R.layout.item_video, data);
        mContext = context;
        mSelected = new HashSet<Integer>();
    }

    @Override
    protected void convert(BaseViewHolder helper, Video item) {
        if (mSelected.contains(helper.getLayoutPosition())) {
            helper.setBackgroundRes(R.id.view_background, R.drawable.shape_album_select_sel);
        } else {
            helper.setBackgroundRes(R.id.view_background, R.drawable.shape_album_select_cancle);
        }
        helper.setText(R.id.item_video_name, item.getName());
        Glide.with(mContext)
                .load(item.getPath())
                .placeholder(R.mipmap.file_image)
                .error(R.mipmap.file_video)
                .into((ImageView) helper.getView(R.id.file_video_icon));
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
}

