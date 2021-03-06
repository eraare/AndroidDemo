package com.guohua.sdk.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.sdk.R;
import com.guohua.sdk.bean.Device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Leo
 * @version 1
 * @since 2016-08-25
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.LocalViewHolder> {
    private List<Device> data; /*数据源*/

    public DeviceAdapter() {
        data = new CopyOnWriteArrayList<>();
    }

    @Override
    public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载Item布局文件*/
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_device, parent, false);
        view.setOnClickListener(mOnClickListener); /*单击事件*/
        view.setOnLongClickListener(mOnLongClickListener); /*长按事件*/
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalViewHolder holder, final int position) {
        holder.itemView.setTag(position); /*标志位置*/
        holder.deviceIcon.setTag(position); /*标志位置*/
        holder.deviceIcon.setOnClickListener(mOnClickListener); /*绑定图标事件*/

        /*取得数据*/
        final Device device = data.get(position);
        holder.deviceName.setText(device.name == null ? "Unknown Name" : device.name);
        holder.deviceAddress.setText(device.address);
        if (!device.connect) {
            holder.deviceState.setText(R.string.activity_offline_device);
            holder.deviceIcon.setImageResource(R.drawable.icon_light_off_line);
        } else {
            holder.deviceState.setText(R.string.activity_online_device);
            if (device.select) {
                holder.deviceIcon.setImageResource(R.drawable.icon_light_on_selected);
            } else {
                holder.deviceIcon.setImageResource(R.drawable.icon_light_on_unselected);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_device_icon_device)
        ImageView deviceIcon;
        @BindView(R.id.tv_device_name_device)
        TextView deviceName;
        @BindView(R.id.tv_device_address_device)
        TextView deviceAddress;
        @BindView(R.id.tv_device_state_device)
        TextView deviceState;

        public LocalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 添加设备
     */
    public boolean addDevice(Device device) {
        for (Device temp : data) {
            if (TextUtils.equals(temp.address, device.address)) {
                return false;
            }
        }

        if (data.add(device)) {
            notifyItemInserted(data.size() - 1);
            return true;
        }

        return false;
    }

    public Device getDevice(int position) {
        return data.get(position);
    }

    public Device removeDevice(int position) {
        Device device = data.remove(position);
        notifyItemRemoved(position);
        return device;
    }

    /**
     * 设置数据源
     *
     * @param devices
     */
    public void setData(List<Device> devices) {
        data = devices;
        notifyDataSetChanged();
    }

    /*单击事件*/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_device_icon_device: {
                    if (mOnIconClickListener != null) {
                        mOnIconClickListener.onIconClick(v, (Integer) v.getTag());
                    }
                }
                break;
                default: {
                    Log.d("Hello World", "mOnClickListener");
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
                    }
                }
                break;
            }
        }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            /*长按删除设备*/
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v, (Integer) v.getTag());
                return true;
            }
            return false;
        }
    };

    /*Section: Item单击事件接口*/
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /*Section: Item长按事件*/
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /*左边的图标点击事件*/
    public interface OnIconClickListener {
        void onIconClick(View view, int position);
    }

    private OnIconClickListener mOnIconClickListener;

    public void setOnIconClickListener(OnIconClickListener onIconClickListener) {
        this.mOnIconClickListener = onIconClickListener;
    }
}
