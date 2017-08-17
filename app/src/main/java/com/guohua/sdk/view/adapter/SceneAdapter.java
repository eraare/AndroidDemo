package com.guohua.sdk.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.sdk.R;
import com.guohua.sdk.bean.Scene;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景模式中的情景适配器
 */
public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Scene> mDatas;

    public SceneAdapter(Context context) {
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
        final Scene mScene = mDatas.get(position);
        holder.itemView.setTag(mScene.tag);
        holder.title.setText(mScene.title);
        holder.picture.setImageResource(mScene.picture);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class SceneViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title_scene_scene)
        TextView title;
        @BindView(R.id.iv_picture_scene_scene)
        ImageView picture;

        public SceneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, Object tag);
    }

    public void addScene(Scene sceneBean) {
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
        for (Scene s : mDatas) {
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
