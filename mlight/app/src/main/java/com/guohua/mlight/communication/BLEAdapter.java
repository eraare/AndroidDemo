package com.guohua.mlight.communication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.guohua.mlight.R;

import java.util.ArrayList;

/**
 * @date 2015-07-22
 * @detail 设备列表适配器
 * @author Leo
 * 
 */
@SuppressLint("InflateParams")
public class BLEAdapter extends BaseAdapter {
	private ArrayList<BluetoothDevice> mLeDevices;//设备
	private ArrayList<Boolean> mLeDevicesIsselected;//设备是否被勾选
	private LayoutInflater mInflater;//布局

	public BLEAdapter(Context context) {
		mLeDevices = new ArrayList<>();//初始化
		mLeDevicesIsselected = new ArrayList<>();
		mInflater = LayoutInflater.from(context);
	}

	public ArrayList<Boolean> getMLeDevicesIsselected() {
		return mLeDevicesIsselected;
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
			viewHolder.cb_select_scandev = (CheckBox) view.findViewById(R.id.cb_select_scandev);

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
		viewHolder.cb_select_scandev.setChecked(mLeDevicesIsselected.get(position));

		viewHolder.cb_select_scandev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
					// TODO Auto-generated method stub
					mLeDevicesIsselected.set(position, isChecked);
			}
		});

		return view;
	}

	/**
	 * 列表显示控件类
	 * 
	 * @author Leo
	 * 
	 */
	private class ViewHolder {
		public TextView deviceName;
		public TextView deviceAddress;
		public CheckBox cb_select_scandev;
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
			mLeDevicesIsselected.add(true);
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
		mLeDevicesIsselected.remove(position);
	}

	/**
	 * 清空设备
	 */
	public void clear() {
		mLeDevices.clear();
		mLeDevicesIsselected.clear();
	}

	/**
	 * 点击设备复选框
	 *
	 * @param position
	 */
	public void setDeviceCheckState(int position, boolean isChecked) {
		mLeDevicesIsselected.set(position, isChecked);
	}

	/**
	 * 获取设备复选框状态
	 *
	 * @param position
	 */
	public boolean getDeviceCheckState(int position) {
		return mLeDevicesIsselected.get(position);
	}

	/**
	 * 全选设备复选框切换状态
	 *
	 * @param isChecked
	 */
	public void setAllDeviceCheckState(boolean isChecked) {
		for (int i = 0; i < getCount(); i++) {
			mLeDevicesIsselected.set(i, isChecked);
		}
	}

}


