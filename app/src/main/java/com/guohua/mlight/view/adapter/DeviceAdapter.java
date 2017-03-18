package com.guohua.mlight.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.Device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Leo
 * @version 1
 * @since 2016-08-25
 * 情景模式中的情景适配器
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.LocalViewHolder> {
    private List<Device> mDatas; /*数据源*/

    public DeviceAdapter() {
        mDatas = new CopyOnWriteArrayList<>();
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
        final Device device = mDatas.get(position);
        final String name = device.getDeviceName();
        final String address = device.getDeviceAddress();
        final boolean isConnected = device.isConnected();
        holder.deviceName.setText(name == null ? "Unknown Name" : name);
        holder.deviceState.setText(isConnected ? "在线" : "离线");
        holder.deviceAddress.setText(device.getDeviceAddress());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
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
        for (Device temp : mDatas) {
            if (TextUtils.equals(temp.getDeviceAddress(), device.getDeviceAddress())) {
                return false;
            }
        }
        mDatas.add(0, device);
        notifyItemInserted(0);
        return true;
    }

    public Device getDevice(int position) {
        return mDatas.get(position);
    }

    public void removeDevice(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 设置数据源
     *
     * @param devices
     */
    public void setData(List<Device> devices) {
        mDatas = devices;
        notifyDataSetChanged();
    }

    /*单击事件*/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Hello World", "mOnClickListener");
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
            }
        }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            /*长按删除设备*/
            removeDevice((Integer) v.getTag());
            return true;
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

}
