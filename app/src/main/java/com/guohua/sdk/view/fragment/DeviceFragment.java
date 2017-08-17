package com.guohua.sdk.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.guohua.sdk.R;
import com.guohua.sdk.bean.Device;
import com.guohua.sdk.common.base.AppContext;
import com.guohua.sdk.common.base.BaseFragment;
import com.guohua.sdk.view.activity.ControlActivity;
import com.guohua.sdk.view.activity.ScanActivity;
import com.guohua.sdk.view.adapter.DeviceAdapter;
import com.guohua.sdk.view.widget.LocalRecyclerView;
import com.guohua.socket.DeviceManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-15
 * 设备管理界面
 */
public class DeviceFragment extends BaseFragment {
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";

    private volatile static DeviceFragment deviceFragment = null;

    public static DeviceFragment newInstance() {
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
    LocalRecyclerView mDeviceView;
    @BindView(R.id.tv_empty_view_device)
    TextView mEmptyView;

    private DeviceAdapter mDeviceAdapter;


    @Override
    protected void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);

        setupRefreshView(); /*配置刷新控件*/
        setupDeviceView(); /*配置设备显示控件*/
        loadDevice(); /*加载设备列表*/
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
        mDeviceView.setEmptyView(mEmptyView);
        mDeviceAdapter = new DeviceAdapter();
        mDeviceAdapter.setOnIconClickListener(mOnIconClickListener);
        mDeviceAdapter.setOnItemClickListener(mOnItemClickListener);
        mDeviceAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        mDeviceView.setAdapter(mDeviceAdapter);
    }

    private DeviceAdapter.OnItemClickListener mOnItemClickListener = new DeviceAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Device device = mDeviceAdapter.getDevice(position);
            if (device == null) return;

            if (!device.connect) {
                DeviceManager.getInstance().connect(device.address, true);
            } else {
                Intent intent = new Intent(getContext(), ControlActivity.class);
                intent.putExtra(EXTRA_DEVICE_NAME, device.name);
                intent.putExtra(EXTRA_DEVICE_ADDRESS, device.address);
                startActivity(intent);
            }
        }
    };

    /*长按删除设备*/
    private DeviceAdapter.OnItemLongClickListener mOnItemLongClickListener = new DeviceAdapter.OnItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, int position) {
            Device device = mDeviceAdapter.removeDevice(position);
            DeviceManager.getInstance().disconnect(device.address, true);
        }
    };

    private DeviceAdapter.OnIconClickListener mOnIconClickListener = new DeviceAdapter.OnIconClickListener() {
        @Override
        public void onIconClick(View view, int position) {
        }
    };

    /**
     * 加载设备列表
     */
    private void loadDevice() {
        mDeviceAdapter.setData(AppContext.getInstance().devices);
    }

    @OnClick(R.id.tv_empty_view_device)
    public void onClick(View v) {
        Intent intent = new Intent(mContext, ScanActivity.class);
        getActivity().startActivityForResult(intent, 520);
    }

    public void addDevice(Device device) {
        mDeviceAdapter.addDevice(device);
    }

    public void update() {
        mDeviceAdapter.notifyDataSetChanged();
    }
}
