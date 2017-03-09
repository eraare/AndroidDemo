package com.guohua.mlight.view.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guohua.mlight.AppContext;
import com.guohua.mlight.ConnectActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.view.adapter.GroupAdapter;
import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.communication.BLERecord;
import com.guohua.mlight.common.util.Constant;
import com.guohua.mlight.common.util.ToolUtils;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenu;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenuCreator;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenuItem;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("NewApi")
public class GroupFragment extends Fragment {
    public static final int ID = 1;

    public static final String TAG = GroupFragment.class.getSimpleName();
    /**
     * 单例模式
     */
    private volatile static GroupFragment groupFragment = null;

    public static GroupFragment getInstance() {
        if (groupFragment == null) {
            synchronized (Scene1Fragment.class) {
                if (groupFragment == null) {
                    groupFragment = new GroupFragment();
                }
            }
        }
        return groupFragment;
    }

    private ConnectActivity mContext;//用于和ConnectActivity通信

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group, container, false);
        init();
        return rootView;
    }

    private View rootView;
    private SwipeMenuListView devices;
    private SwipeRefreshLayout refresh;
    private GroupAdapter mGroupAdapter;
    private LinearLayout empty;
    private TextView emptyTip;
    private TextView words;
    private ImageView light;

    public static final String ACTION_RECEIVED_WORDS = "action.RECEIVED_WORDS";
    public static final String KEY_EXTRA_WORDS = "key_extra_words";

    private BluetoothAdapter mBluetoothAdapter;

    private void init() {
        mContext = (ConnectActivity) getActivity();
        mHandler = new android.os.Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        findViewsByIds();
        initRefreshLayout();
        initListViewListener();
    }

    private void initRefreshLayout() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mBluetoothAdapter != null) {
//                    mGroupAdapter.clear();
                    mGroupAdapter.clearUnselected();
                    mGroupAdapter.notifyDataSetChanged();

                    //android6.0 运行时申请蓝牙权限
                    ToolUtils.requestPermissions(mContext, Manifest.permission.BLUETOOTH, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH);

                    //android6.0 运行时申请操作蓝牙权限
                    ToolUtils.requestPermissions(mContext, Manifest.permission.BLUETOOTH_ADMIN, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN);

                    //android6.0 运行时申请定位权限（android6.0 下，要使用蓝牙需同时开启定位权限）
                    ToolUtils.requestPermissions(mContext, Manifest.permission.ACCESS_COARSE_LOCATION, Constant.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


                    scanLeDevice(true);
                }
            }
        });
    }

    private void initBondedDevices() {
        Set<BluetoothDevice> mBluetoothDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice mDevice : mBluetoothDevices) {
            mGroupAdapter.addDevice(new Device(mDevice.getName(), mDevice.getAddress(), false, true));
        }
    }

    private void initListViewListener() {
        mGroupAdapter = new GroupAdapter(mContext);

        initBondedDevices();

        mGroupAdapter.setOperatorClickListener(new GroupAdapter.OnOperatorClickListener() {
            @Override
            public void onOperatorClick(int position, Device device) {
                if (device.isConnected()) {
                    AppContext.getInstance().disonnect(device.getDeviceAddress(), false);
                } else {
                    scanLeDevice(false);
                    device.setSelected(true);
                    addSelectedDevices2Cache();
                    if (AppContext.getInstance().connect(device.getDeviceAddress())) {
                        mContext.showConnectDialog();
                    }
                }
                mGroupAdapter.notifyDataSetChanged();
            }
        });

        devices.setAdapter(mGroupAdapter);
        devices.setVisibility(View.VISIBLE);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.main)));
                deleteItem.setWidth(ToolUtils.dp2px(mContext, 80));
                deleteItem.setTitle(getString(R.string.group_device_detele));
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        devices.setMenuCreator(creator);

        devices.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0: {
                        mGroupAdapter.removeDevice(position);
                        mGroupAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        });

        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGroupAdapter.setSelectState(position);
                mGroupAdapter.notifyDataSetChanged();
            }
        });

        devices.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    refresh.setEnabled(true);
                } else {
                    refresh.setEnabled(false);
                }
            }
        });
    }

    private void findViewsByIds() {
        light = (ImageView) rootView.findViewById(R.id.iv_light_group);
        words = (TextView) rootView.findViewById(R.id.tv_words_group);
        refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.sfl_refresh_group);
        devices = (SwipeMenuListView) rootView.findViewById(R.id.lv_devices_group);

        empty = (LinearLayout) rootView.findViewById(R.id.ll_empty_group);
        emptyTip = (TextView) rootView.findViewById(R.id.tv_empty_tip_group);
        devices.setEmptyView(empty);

        words.setOnClickListener(mOnClickListener);

        light.setOnClickListener(mOnClickListener);
        light.setBackgroundResource(R.drawable.animation_light);
        AnimationDrawable animationDrawable = (AnimationDrawable) light.getBackground();
        animationDrawable.start();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tv_words_group: {
                    //弹出对话框显示全部内容
                    new AlertDialog.Builder(mContext).setMessage(words.getText().toString()).show();
                }
                break;
                case R.id.iv_light_group: {
                    if (addSelectedDevices2Cache()) {
                        mContext.showConnectDialog();
                        AppContext.getInstance().connectAll();
                    } else {
                        Snackbar.make(v, R.string.connect_select_tip, Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    };


    /**
     * 监听器
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (TextUtils.equals(action, ACTION_RECEIVED_WORDS)) {
                String wordsContent = intent.getStringExtra(KEY_EXTRA_WORDS);
                words.setText(wordsContent);
            }
        }
    };

    /**
     * 注册监听器
     */
    private void registerTheReceiver() {
        //注册监听器监听设备扫描以及扫描完成
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVED_WORDS);
        filter.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerTheReceiver();
        scanLeDevice(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBluetoothAdapter != null && mScanning) {
            scanLeDevice(false);
            mHandler.removeCallbacks(mStopRunnable);
        }
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler = null;
        }
    }

    public boolean addSelectedDevices2Cache() {
        AppContext.getInstance().devices.clear();
        ArrayList<Device> mDevices = mGroupAdapter.getDatas();
        for (Device d : mDevices) {
            if (d.isSelected()) {
                AppContext.getInstance().devices.add(d);
            }
        }
        if (AppContext.getInstance().devices.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyDown() {
        if (mBluetoothAdapter != null && mScanning) {
            scanLeDevice(false);
            return true;
        }
        return false;
    }

    //    private android.os.Handler mHandler = new android.os.Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            int what = msg.what;
//            switch (what) {
//                case WHAT_SCAN_ENDED:{
//                    refresh.setRefreshing(false);
//                    if(!GroupFragment.this.isDetached()){
//                        emptyTip.setText(getString(R.string.group_empty_info));
//                    }
//                }break;
//            }
//        }
//    };// 用于postDelay
    private android.os.Handler mHandler = null;
    private boolean mScanning = false;// 循环标志位

    private static final long SCAN_PERIOD = 10000;// 扫描10s


    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            refresh.setRefreshing(false);
            try {
                emptyTip.setText(getString(R.string.group_empty_info));
            } catch (Exception e) {
                System.out.println("error-GroupFragment:mStopRunnable");
            }
        }
    };

    /**
     * 扫描BLE设备
     *
     * @param enable
     */
    @SuppressLint("NewApi")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(mStopRunnable, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            emptyTip.setText(getString(R.string.group_empty_on));
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            refresh.setRefreshing(false);
            emptyTip.setText(getString(R.string.group_empty_info));
        }
    }

    /**
     * BLE扫描回调函数，设备保存在remoteDevice里面
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            // TODO Auto-generated method stub
            if (!BLERecord.isOurDevice(scanRecord)) {
                return;
            }
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    mGroupAdapter.addDevice(new Device(deviceName, deviceAddress));
                    mGroupAdapter.notifyDataSetChanged();
                }
            });
        }
    };

}
