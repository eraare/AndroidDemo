package com.guohua.mlight.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.SceneBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景模式中的情景适配器
 */
public class SceneAdapter1 extends RecyclerView.Adapter<SceneAdapter1.SceneViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<SceneBean> mDatas;

    public SceneAdapter1(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
    }

    @Override
    public SceneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_scene, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, v.getTag());
                }
            }
        });
        return new SceneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SceneViewHolder holder, final int position) {
        final SceneBean mScene = mDatas.get(position);
        holder.itemView.setTag(mScene.tag);
        holder.title.setText(mScene.title);
        holder.detail.setText(mScene.detail);
        holder.picture.setImageResource(mScene.picture);
        if (mScene.isRunning) {
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setText(R.string.scene_state_on);
        } else {
            holder.state.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class SceneViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView detail;
        public ImageView picture;
        public TextView state;

        public SceneViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title_scene_scene);
            detail = (TextView) itemView.findViewById(R.id.tv_detail_scene_scene);
            picture = (ImageView) itemView.findViewById(R.id.iv_picture_scene_scene);
            state = (TextView) itemView.findViewById(R.id.tv_state_scene_scene);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, Object tag);
    }

    public void addScene(SceneBean sceneBean) {
        this.mDatas.add(sceneBean);
        notifyDataSetChanged();
    }

    public String getTitle(int position) {
        return this.mDatas.get(position).title;
    }

    public void setTitle(String title, int position) {
        this.mDatas.get(position).title = title;
        notifyDataSetChanged();
    }

    public void setState(boolean isRunning, int position) {
        this.mDatas.get(position).isRunning = isRunning;
        notifyDataSetChanged();
    }

    /**
     * 改变当前状态
     *
     * @param tag
     * @return
     */
    public boolean changeState(Object tag) {
        // 默认为false
        boolean flag = false;
        for (SceneBean s : mDatas) {
            if (tag == null) {
                s.isRunning = false;
            } else {
                // tag为空则全为false 不为空则改变当前状态
                if (tag == s.tag) {
                    s.isRunning = !s.isRunning;
                    flag = s.isRunning;
                } else {
                    s.isRunning = false;
                }
            }
        }
        return flag;
    }
}
