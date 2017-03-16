package com.r00kie.lwble;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-16
 * 设备列表适配器
 */
public class BLEAdapter extends RecyclerView.Adapter<BLEAdapter.LocalViewHolder> {
    private List<BluetoothDevice> mDatas; /*数据源*/

    public BLEAdapter() {
        mDatas = new CopyOnWriteArrayList<>();
    }

    @Override
    public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载Item布局文件*/
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_device, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocalViewHolder holder, int position) {
        holder.itemView.setTag(position);
        final BluetoothDevice device = mDatas.get(position);
        holder.deviceName.setText(device.getName());
        holder.deviceAddress.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        ImageView deviceIcon;
        TextView deviceName;
        TextView deviceAddress;

        public LocalViewHolder(View itemView) {
            super(itemView);
            deviceIcon = (ImageView) itemView.findViewById(R.id.iv_device_icon_device);
            deviceName = (TextView) itemView.findViewById(R.id.tv_device_name_device);
            deviceAddress = (TextView) itemView.findViewById(R.id.tv_device_address_device);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, v.getTag());
            }
        }
    };

    /*单击事件接口*/
    public interface OnItemClickListener {
        void onItemClick(View view, Object position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addDevice(BluetoothDevice device) {
        mDatas.add(0, device);
        notifyItemInserted(0);
    }

    public BluetoothDevice getDevice(int position) {
        return mDatas.get(position);
    }

    public void removeDevice(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }
}
