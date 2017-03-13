package com.guohua.mlight.communication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guohua.mlight.R;

import java.util.ArrayList;

/**
 * @author Leo
 * @date 2015-07-22
 * @detail 设备列表适配器
 */
@SuppressLint("InflateParams")
public class BLEAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;//设备
    private LayoutInflater mInflater;//布局

    public BLEAdapter(Context context) {
        mLeDevices = new ArrayList<>();//初始化
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        if (mLeDevices.size() <= arg0) {
            return null;
        }
        return mLeDevices.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;

        if (null == view) {
            viewHolder = new ViewHolder();

            view = mInflater.inflate(R.layout.item_ble, null);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(position);

        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText("unknown name");
        String deviceAddress = device.getAddress();
        viewHolder.deviceAddress.setText(deviceAddress);

        return view;
    }

    /**
     * 列表显示控件类
     *
     * @author Leo
     */
    private class ViewHolder {
        public TextView deviceName;
        public TextView deviceAddress;
    }

    /**
     * 设置数据源
     *
     * @param mDevices
     */
    public void setData(ArrayList<BluetoothDevice> mDevices) {
        this.mLeDevices = mDevices;
    }

    /**
     * 添加设备
     *
     * @param mDevice
     */
    public void addDevice(BluetoothDevice mDevice) {
        if (!mLeDevices.contains(mDevice)) {
            mLeDevices.add(mDevice);
        }
    }

    /**
     * 得到设备
     *
     * @param position
     * @return
     */
    public BluetoothDevice getDevice(int position) {
        if (mLeDevices.size() <= position) {
            return null;
        }
        return mLeDevices.get(position);
    }

    public void removeDevice(int position) {
        mLeDevices.remove(position);
    }

    /**
     * 清空设备
     */
    public void clear() {
        mLeDevices.clear();
    }
}


