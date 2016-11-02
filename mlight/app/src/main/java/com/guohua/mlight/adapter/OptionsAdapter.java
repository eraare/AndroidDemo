package com.guohua.mlight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.bean.Option;

import java.util.ArrayList;

/**
 * Created by Leo on 2016/1/8.
 */
public class OptionsAdapter extends BaseAdapter {
    private ArrayList<Option> options;
    private LayoutInflater mInflater;

    public OptionsAdapter(Context context) {
        options = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (null == convertView) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.item_options_settings, null);
            viewHolder.optionIcon = (ImageView) convertView
                    .findViewById(R.id.iv_icon_options);
            viewHolder.optionTitle = (TextView) convertView
                    .findViewById(R.id.tv_title_options);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Option option = options.get(position);

        viewHolder.optionIcon.setImageResource(option.id);
        viewHolder.optionTitle.setText(option.title);

        return convertView;
    }

    /**
     * 列表显示控件类
     *
     * @author Leo
     */
    private class ViewHolder {
        public ImageView optionIcon;
        public TextView optionTitle;
    }


    public void addOption(Option option) {
        this.options.add(option);
    }
}
