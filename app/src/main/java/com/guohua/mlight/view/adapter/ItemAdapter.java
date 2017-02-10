package com.guohua.mlight.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.ItemInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @file ConfigAdapter.java
 * @author Leo
 * @version 1
 * @detail 用于个人中心的配置选项适配器
 * @since 2016/12/19 17:59
 */

/**
 * 文件名：ConfigAdapter.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/19 17:59
 * 描  述：用于个人中心的配置选项适配器
 */
public class ItemAdapter extends BaseAdapter {
    /*存储数据源*/
    private List<ItemInfo> mDatas;
    /*加载数据项的布局*/
    private LayoutInflater mInflater;

    public ItemAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 定义一个ViewHolder
        final ViewHolder holder;

        /*若convertView为空则为首次加载*/
        if (convertView == null) {
            // 加载item的布局视图
            convertView = mInflater.inflate(R.layout.item_item_me, null);
            // 生成ViewHolder
            holder = new ViewHolder(convertView);
            // 把item缓存起来
            convertView.setTag(holder);
        } else {
            // 获取缓存视图
            holder = (ViewHolder) convertView.getTag();
        }

        /*设置信息内容*/
        ItemInfo info = mDatas.get(position);
        holder.title.setText(info.title);
        holder.content.setText(info.content);

        return convertView;
    }

    /**
     * 用于缓存视图控件
     */
    static class ViewHolder {
        /*Icon 和 Title*/
        @BindView(R.id.tv_title_item)
        TextView title;
        @BindView(R.id.tv_content_item)
        TextView content;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 添加选项的接口
     *
     * @param itemInfo
     */
    public void addItem(ItemInfo itemInfo) {
        mDatas.add(itemInfo);
    }
}
