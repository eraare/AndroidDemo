package com.guohua.mlight.view.adapter;

import android.support.v7.widget.RecyclerView;
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
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalViewHolder holder, final int position) {
        final Device device = mDatas.get(position);
        holder.deviceName.setText(device.getDeviceName());
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

        public LocalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 添加设备
     */
    public void addDevice(Device device) {
        mDatas.add(device);
    }

    /**
     * 设置数据源
     *
     * @param devices
     */
    public void setData(List<Device> devices) {
        mDatas = devices;
    }
}
