package com.guohua.mlight.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.StyleBean;

import java.util.ArrayList;

/**
 * @author Leo
 * @detail RecyclerView的适配器 继承自RecyclerView.Adapter
 * @time 2015-11-04
 */
public class StyleAdapter extends RecyclerView.Adapter {
    private ArrayList<StyleBean> datas;//数据源

    public StyleAdapter() {
        datas = new ArrayList<>();//new
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_style_scene, parent, false);//布局每一块部局
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        /**
         * 为每一个条目绑定内容
         */
        final StyleBean style = datas.get(position);

        ViewHolder mHolder = (ViewHolder) holder;
        mHolder.picture.setImageResource(style.drawableId);
        mHolder.title.setText(style.title);
        mHolder.subtitle.setText(style.subtitle);
    }

    @Override
    public int getItemCount() {
        return datas.size();//长度
    }

    /**
     * 条目布局对应的类
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView picture;
        public TextView title;
        public TextView subtitle;
        public ImageButton control;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            picture = (ImageView) itemView.findViewById(R.id.iv_picture_style);
            title = (TextView) itemView.findViewById(R.id.tv_title_style);
            subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle_style);
            control = (ImageButton) itemView.findViewById(R.id.btn_switch_style);
            control.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onCustomClickListener != null) {
                int id = v.getId();
                if (id == R.id.btn_switch_style) {
                    onCustomClickListener.onButtonClick(v, getPosition());
                } else {
                    onCustomClickListener.onItemClick(v, getPosition());
                }
            }
        }
    }

    public interface OnCustomClickListener {
        void onItemClick(View view, int postion);

        void onButtonClick(View view, int position);
    }

    private OnCustomClickListener onCustomClickListener;

    public void setOnCustomClickListener(OnCustomClickListener onCustomClickListener) {
        this.onCustomClickListener = onCustomClickListener;
    }

    /**
     * 添加情景
     *
     * @param style
     */
    public void addStyle(StyleBean style) {
        datas.add(style);
    }

    public StyleBean getStyle(int position) {
        return datas.get(position);
    }

    public int getState(int position) {
        return datas.get(position).state;
    }

    public void changeState(int position, int state) {
        datas.get(position).state = state;
    }

    public void clear() {
        datas.clear();
    }
}
