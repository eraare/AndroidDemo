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
public class Scene1Adapter extends RecyclerView.Adapter<Scene1Adapter.SceneViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<SceneBean> mDatas;

    public Scene1Adapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
    }

    @Override
    public SceneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_scene1, parent, false);
        return new SceneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SceneViewHolder holder, final int position) {
        SceneBean mScene = mDatas.get(position);
        holder.title.setText(mScene.title);
        holder.detail.setText(mScene.detail);
        holder.picture.setImageResource(mScene.picture);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class SceneViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView detail;
        public ImageView picture;

        public SceneViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title_scene_scene);
            detail = (TextView) itemView.findViewById(R.id.tv_detail_scene_scene);
            picture = (ImageView) itemView.findViewById(R.id.iv_picture_scene_scene);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void addScene(SceneBean sceneBean) {
        this.mDatas.add(sceneBean);
        notifyDataSetChanged();
    }

    public String getTitle(int position) {
        return this.mDatas.get(position).title;
    }
}
