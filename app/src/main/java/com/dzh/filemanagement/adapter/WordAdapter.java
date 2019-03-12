package com.dzh.filemanagement.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dzh.filemanagement.R;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.Nullable;


/**
 * Created by ddd on 2016/12/7.
 */

public class WordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private Context mContext;
    private HashSet<Integer> mSelected;

    public WordAdapter(@Nullable List<String> data, Context context) {
        super(R.layout.item_word, data);
        mContext = context;
        mSelected = new HashSet<Integer>();
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if (mSelected.contains(helper.getLayoutPosition())) {
            helper.setBackgroundRes(R.id.view_background, R.drawable.shape_album_select_sel);
        } else {
            helper.setBackgroundRes(R.id.view_background, R.drawable.shape_album_select_cancle);
        }
        File file = new File(item);
        helper.setText(R.id.item_word_name , file.getName());
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

