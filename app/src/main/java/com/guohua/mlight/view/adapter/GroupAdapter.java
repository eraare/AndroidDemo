package com.guohua.mlight.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.communication.BluetoothUtil;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Leo
 * @time 2016-04-19
 * @describe 组设备适配器
 */
public class GroupAdapter extends BaseAdapter {
    private ArrayList<Device> datas;
    private LayoutInflater mInflater;
    private Context mContext;

    public GroupAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        datas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
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

            convertView = mInflater.inflate(R.layout.item_group_main, null);
            viewHolder.deviceState = (ImageView) convertView.findViewById(R.id.iv_state_devices);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.tv_name_devices);
            viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.tv_address_devices);
            viewHolder.deviceOperator = (ImageView) convertView.findViewById(R.id.tv_operator_devices);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Device device = datas.get(position);

        String dName = device.getDeviceName();
        if (dName == null || TextUtils.equals(dName, "")) {
            dName = "Unknown Name";
        }

        if (AppContext.getInstance().isConnect(device.getDeviceAddress())) {
            device.setConnected(true);
            viewHolder.deviceOperator.setImageResource(R.drawable.icon_device_on);
            viewHolder.deviceName.setText(dName + " " + mContext.getString(R.string.device_online));
        } else {
            device.setConnected(false);
            viewHolder.deviceOperator.setImageResource(R.drawable.icon_device_off);
            viewHolder.deviceName.setText(dName + " " + mContext.getString(R.string.device_offline));
        }

//        viewHolder.deviceName.setText(dName);
        viewHolder.deviceAddress.setText(device.getDeviceAddress());

        if (device.isSelected()) {
            viewHolder.deviceState.setImageResource(R.drawable.icon_device_checked);
        } else {
            viewHolder.deviceState.setImageResource(R.drawable.icon_device_unchecked);
        }

        final int tempPosition = position;
        viewHolder.deviceOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOperatorClickListener.onOperatorClick(tempPosition, device);
            }
        });

        /*处理设备的选择*/
        viewHolder.deviceState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (device.isSelected()) {
                    device.setSelected(false);
                } else {
                    device.setSelected(true);
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    /**
     * 列表显示控件类
     *
     * @author Leo
     */
    private class ViewHolder {
        public ImageView deviceState;
        public TextView deviceName;
        public TextView deviceAddress;
        public ImageView deviceOperator;
    }

    public void setDatas(ArrayList<Device> datas) {
        this.datas = datas;
    }

    public ArrayList<Device> getDatas() {
        return this.datas;
    }

    public void addDevice(Device device) {
        for (Device temp : datas) {
            if (TextUtils.equals(temp.getDeviceAddress(), device.getDeviceAddress())) {
                return;
            }
        }
        this.datas.add(device);
        notifyDataSetChanged();
    }

    /**
     * 选择所有和取消所有选择
     *
     * @param select
     */
    public void selectAll(boolean select) {
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            datas.get(i).setSelected(select);
        }
    }

    public void removeDevice(int index) {
        Device device = datas.get(index);
        this.datas.remove(device);
        BluetoothUtil.unpairDevice(device.getDeviceAddress());
    }

    public void setSelectState(int position) {
        boolean isSelected = this.datas.get(position).isSelected();
        if (isSelected) {
            this.datas.get(position).setSelected(false);
        } else {
            this.datas.get(position).setSelected(true);
        }
    }

    public interface OnOperatorClickListener {
        void onOperatorClick(int position, Device device);
    }

    public void clear() {
        this.datas.clear();
    }

    public void clearUnselected() {
        Iterator<Device> iterator = this.datas.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (!device.isSelected()) {
                iterator.remove();
            }
        }
    }

    public Device getDevice(int position) {
        return this.datas.get(position);
    }

    private OnOperatorClickListener mOperatorClickListener;

    public OnOperatorClickListener getOperatorClickListener() {
        return mOperatorClickListener;
    }

    public void setOperatorClickListener(OnOperatorClickListener mOperatorClickListener) {
        this.mOperatorClickListener = mOperatorClickListener;
    }
}
