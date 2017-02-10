package com.guohua.mlight.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.OptionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 *         #time 2016-08-26
 *         #detail 数据源适配器
 */
public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ItemViewHolder> {
    private List<OptionBean> mDatas; // 数据源
    private LayoutInflater mInflater; // 布局器

    public OptionAdapter(Context context) {
        // 初始化
        mInflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_option, parent, false);
        view.setOnClickListener(mOnClickListener);// 触发事件
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        OptionBean optionBean = mDatas.get(position);
        holder.itemView.setTag(optionBean.tag);
        // insert code here
        holder.title.setText(optionBean.title);
        holder.icon.setImageResource(optionBean.icon);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 自定义的ViewHolder用于优化缓存每一项
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title_option);
            icon = (ImageView) itemView.findViewById(R.id.iv_icon_option);
        }
    }

    /**
     * 为每一项添加点击事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, v.getTag());
            }
        }
    };

    /**
     * 添加数据项接口
     *
     * @param optionBean
     */
    public void addItem(OptionBean optionBean) {
        this.mDatas.add(optionBean);
        notifyDataSetChanged();
    }

    /**
     * 添加数据项接口
     *
     * @param optionBean
     */
    public void addItem(int position, OptionBean optionBean) {
        this.mDatas.add(position, optionBean);
        notifyItemInserted(position);
    }

    /**
     * 根据索引删除某项数据
     *
     * @param position
     */
    public void removeItem(int position) {
        this.mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 根据内容删除某项数据
     *
     * @param itemBean
     */
    public void removeItem(Object itemBean) {
        this.mDatas.remove(itemBean);
        notifyDataSetChanged();
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View v, Object tag);
    }

    /**
     * 设置事件监听器
     *
     * @param mOnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}