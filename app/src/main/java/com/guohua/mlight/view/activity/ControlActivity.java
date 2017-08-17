package com.guohua.mlight.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.eraare.ble.OnDataReceivedListener;
import com.guohua.mlight.R;
import com.guohua.mlight.bean.Device;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.view.fragment.DeviceFragment;
import com.guohua.socket.DeviceManager;

import butterknife.BindView;
import butterknife.OnClick;

public class ControlActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_control;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @BindView(R.id.iv_all)
    ImageView mSwitchAll;
    @BindView(R.id.iv_usb_l)
    ImageView mUsbSwitch1;
    @BindView(R.id.iv_usb_r)
    ImageView mUsbSwitch2;
    @BindView(R.id.iv_socket_l_k1)
    ImageView mSocketOneSwitch1;
    @BindView(R.id.iv_socket_r_k1)
    ImageView mSocketOneSwitch2;
    @BindView(R.id.iv_socket_l_k2)
    ImageView mSocketTwoSwitch1;
    @BindView(R.id.iv_socket_r_k2)
    ImageView mSocketTwoSwitch2;
    @BindView(R.id.iv_socket_l_k3)
    ImageView mSocketThreeSwitch1;
    @BindView(R.id.iv_socket_r_k3)
    ImageView mSocketThreeSwitch2;
    @BindView(R.id.iv_socket_l_k4)
    ImageView mSocketFourSwitch1;
    @BindView(R.id.iv_socket_r_k4)
    ImageView mSocketFourSwitch2;

    private Device mDevice;

    /*记录各个孔的状态 最后一位为全开全关*/
    private boolean[] states = new boolean[6];

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        /*接收传递过来的饿设备信息*/
        if (intent != null) {
            String deviceAddress = intent.getStringExtra(DeviceFragment.EXTRA_DEVICE_ADDRESS);
            mDevice = AppContext.getInstance().findDevice(deviceAddress);
        }
        /*验证数据有没有传递过来*/
        if (mDevice == null) {
            toast("设备已失效");
            removeFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceManager.getInstance().addOnDataReceivedListener(mOnDataReceivedListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DeviceManager.getInstance().removeOnDataReceivedListener(mOnDataReceivedListener);
    }

    private OnDataReceivedListener mOnDataReceivedListener = new OnDataReceivedListener() {
        @Override
        public void onDataReceived(String address, byte[] bytes) {
            if (TextUtils.equals(mDevice.address, address)) {
                System.out.println(new String(bytes));
            }
        }
    };


    @OnClick({R.id.iv_all, R.id.iv_usb_l, R.id.iv_usb_r, R.id.iv_socket_l_k1, R.id.iv_socket_r_k1,
            R.id.iv_socket_l_k2, R.id.iv_socket_r_k2, R.id.iv_socket_l_k3, R.id.iv_socket_r_k3,
            R.id.iv_socket_l_k4, R.id.iv_socket_r_k4})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.iv_all:
                controlAll();
                break;
            case R.id.iv_usb_l:
            case R.id.iv_usb_r:
                control(4);
                break;
            case R.id.iv_socket_l_k1:
            case R.id.iv_socket_r_k1:
                control(0);
                break;
            case R.id.iv_socket_l_k2:
            case R.id.iv_socket_r_k2:
                control(1);
                break;
            case R.id.iv_socket_l_k3:
            case R.id.iv_socket_r_k3:
                control(2);
                break;
            case R.id.iv_socket_l_k4:
            case R.id.iv_socket_r_k4:
                control(3);
                break;
            default:
                break;
        }
    }

    private void controlAll() {
        int length = states.length;
        if (states[length - 1]) {
            DeviceManager.getInstance().turnOff(mDevice.address);
            states[length - 1] = false;
        } else {
            DeviceManager.getInstance().turnOn(mDevice.address);
            states[length - 1] = true;
        }

        for (int i = 0; i < length; i++) {
            states[i] = states[length - 1];
        }

        toast("K-all" + ": " + states[length - 1]);
    }

    private void control(int which) {
        states[which] = !states[which];
        DeviceManager.getInstance().control(mDevice.address, which, states[which]);

        toast("K-" + which + ": " + states[which]);
    }

}
