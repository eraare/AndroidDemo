package com.guohua.sdk.view.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.sdk.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-16
 * 设备列表适配器
 */
public class BLEAdapter extends RecyclerView.Adapter<BLEAdapter.LocalViewHolder> {
    private List<BluetoothDevice> mDatas; /*数据源*/

    public BLEAdapter() {
        mDatas = new ArrayList<>();
    }

    @Override
    public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载Item布局文件*/
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_ble, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocalViewHolder holder, int position) {
        holder.itemView.setTag(position);
        final BluetoothDevice device = mDatas.get(position);
        holder.deviceName.setText(device.getName() == null ? "Unknown Name" : device.getName());
        holder.deviceAddress.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /*单击事件接口*/
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
            }
        }
    };

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_device_icon_ble)
        ImageView deviceIcon;
        @BindView(R.id.tv_device_name_ble)
        TextView deviceName;
        @BindView(R.id.tv_device_address_ble)
        TextView deviceAddress;

        public LocalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addDevice(BluetoothDevice device) {
        if (mDatas.contains(device)) return;
        mDatas.add(device);
        notifyItemInserted(mDatas.size() - 1);
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
