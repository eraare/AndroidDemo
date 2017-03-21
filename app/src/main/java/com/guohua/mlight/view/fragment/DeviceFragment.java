package com.guohua.mlight.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.lwble.BLEController;
import com.guohua.mlight.lwble.MessageEvent;
import com.guohua.mlight.model.bean.LightInfo;
import com.guohua.mlight.model.impl.LightService;
import com.guohua.mlight.view.adapter.DeviceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-15
 * 设备管理界面
 */
public class DeviceFragment extends BaseFragment {
    private volatile static DeviceFragment deviceFragment = null;

    public static DeviceFragment getInstance() {
        if (deviceFragment == null) {
            synchronized (DeviceFragment.class) {
                if (deviceFragment == null) {
                    deviceFragment = new DeviceFragment();
                }
            }
        }
        return deviceFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device;
    }

    @BindView(R.id.srl_refresh_device)
    SwipeRefreshLayout mRefreshView;
    @BindView(R.id.rv_device_device)
    RecyclerView mDeviceView;

    private DeviceAdapter mDeviceAdapter;

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
        setupRefreshView(); /*配置刷新控件*/
        setupDeviceView(); /*配置设备显示控件*/
        loadDevice(); /*加载设备列表*/
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.state) {
            case BLEController.STATE_CONNECTING: {
                mContext.showProgressDialog("连接设备", "拼命连接中...");
            }
            break;
            case BLEController.STATE_CONNECTED: {
                mContext.toast("连接成功");
                mContext.dismissProgressDialog();
                LightInfo lightInfo = AppContext.getInstance().findLight(event.address);
                if (lightInfo != null) {
                    lightInfo.connect = true;
                }
                mDeviceAdapter.notifyDataSetChanged();
            }
            break;
            case BLEController.STATE_DISCONNECTING: {
                mContext.showProgressDialog("断开设备", "拼命断开中...");
            }
            break;
            case BLEController.STATE_DISCONNECTED: {
                mContext.toast("断开成功");
                mContext.dismissProgressDialog();
                LightInfo lightInfo = AppContext.getInstance().findLight(event.address);
                if (lightInfo != null) {
                    lightInfo.connect = false;
                }
                mDeviceAdapter.notifyDataSetChanged();
            }
            break;
            case BLEController.STATE_SERVICING: {
                LightInfo lightInfo = AppContext.getInstance().findLight(event.address);
                if (lightInfo != null) {
                    LightService.getInstance().validatePassword(lightInfo.address, lightInfo.password);
                }
                mContext.toast("可以进行玩耍了");
            }
            break;
            default:
                break;
        }
    }

    /**
     * 下拉刷新控件配置
     */
    private void setupRefreshView() {
        mRefreshView.setOnRefreshListener(mOnRefreshListener);
    }

    private final SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mRefreshView.setRefreshing(false);
        }
    };

    /**
     * 设备显示视图配置
     */
    private void setupDeviceView() {
        mDeviceView.setLayoutManager(new LinearLayoutManager(mContext));
        mDeviceView.setItemAnimator(new DefaultItemAnimator());
        mDeviceAdapter = new DeviceAdapter();
        mDeviceAdapter.setOnItemClickListener(mOnItemClickListener);
        mDeviceView.setAdapter(mDeviceAdapter);
    }

    private DeviceAdapter.OnItemClickListener mOnItemClickListener = new DeviceAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d("Hello World", "mOnItemClickListener");
            final LightInfo light = mDeviceAdapter.getLight(position);
            if (light.connect) {
                mContext.showProgressDialog("连接设备", "拼命连接中...");
                LightService.getInstance().disconnect(light.address, false);
            } else {
                mContext.showProgressDialog("断开设备", "拼命断开中...");
                LightService.getInstance().connect(getContext(), light.address, true);
            }
        }
    };

    /**
     * 加载设备列表
     */
    private void loadDevice() {
        /*mDeviceAdapter.addDevice(new Device("魔小灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("全彩照明", "33:93:A6:26:2B:0A"));
        mDeviceAdapter.addDevice(new Device("智能插排", "A6:88:22:33:93:A6"));
        mDeviceAdapter.addDevice(new Device("卧室灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("餐厅灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("运动智能", "00:23:93:A6:88:22"));*/
        mDeviceAdapter.setData(AppContext.getInstance().lights);
    }

    /**
     * @param light
     */
    public void onResult(LightInfo light) {
        if (mDeviceAdapter.addLight(light)) {
            /*添加后进行自动连接*/
            mContext.showProgressDialog("连接设备", "拼命连接中...");
            LightService.getInstance().connect(getContext(), light.address, true);
        }
    }

    @Override
    protected void suicide() {
        super.suicide();
        EventBus.getDefault().unregister(this);
    }
}
