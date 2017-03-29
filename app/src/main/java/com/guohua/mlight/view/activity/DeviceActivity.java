package com.guohua.mlight.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.lwble.BLEConstant;
import com.guohua.mlight.model.bean.LightInfo;
import com.guohua.mlight.view.fragment.DeviceFragment;

public class DeviceActivity extends BaseActivity {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_device;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return DeviceFragment.getInstance();
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_container_device;
    }

    private static final int REQUEST_CODE_BLE = 520;

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        /*配置标题栏*/
        setToolbarTitle(R.string.activity_title_device);
        setForwardVisibility(View.VISIBLE);
        setForwardTitle(R.string.activity_add_device);
        setOnForwardClickListener(mOnClickListener);
    }

    /**
     * 去添加设备
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DeviceActivity.this, ScanActivity.class);
            startActivityForResult(intent, REQUEST_CODE_BLE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLE) {
            if (resultCode == RESULT_OK) {
                /*接收设备数据并加入到全局缓存切进行连接操作*/
                String deviceAddress = data.getStringExtra(BLEConstant.EXTRA_DEVICE_ADDRESS);
                String deviceName = data.getStringExtra(BLEConstant.EXTRA_DEVICE_NAME);
                DeviceFragment.getInstance().onResult(new LightInfo(deviceName, deviceAddress));
            }
        }
    }
}
