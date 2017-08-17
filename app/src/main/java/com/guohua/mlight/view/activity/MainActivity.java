package com.guohua.mlight.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.eraare.ble.BLECenter;
import com.eraare.ble.BLEScanner;
import com.eraare.ble.BLEUtils;
import com.eraare.ble.OnStateChangedListener;
import com.guohua.ios.dialog.AlertDialog;
import com.guohua.mlight.R;
import com.guohua.mlight.bean.Device;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.permission.PermissionListener;
import com.guohua.mlight.common.permission.PermissionManager;
import com.guohua.mlight.common.util.FilterUtils;
import com.guohua.mlight.view.adapter.FragmentAdapter;
import com.guohua.mlight.view.fragment.CenterFragment;
import com.guohua.mlight.view.fragment.DeviceFragment;
import com.guohua.mlight.view.fragment.SceneFragment;
import com.guohua.socket.DeviceManager;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * @author Leo
 * @version 1
 * @since 2015-10-29
 * 项目框架主界面 具有滑动切换功能 各类功能在各自Fragment里
 */
@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {
    /*绑定控件*/
    @BindView(R.id.bnb_bar_main)
    BottomNavigationBar mBarView;
    @BindView(R.id.vp_pager_main)
    ViewPager mPagerView;
    /*绑定数组*/
    @BindArray(R.array.activity_titles_main)
    String[] mTitles; // 显示在底部标签栏的标题
    @BindArray(R.array.fragment_alias_main)
    String[] mAlias; // 显示在顶部导航栏的别名
    /*底部标签导航的ICON图像*/
    private int[] mNormalIconIds = {
            R.drawable.tab_home_normal,
            R.drawable.tab_scene_normal,
            R.drawable.tab_center_normal,
    };
    /*底部标签导航被选中的ICON图像*/
    private int[] mSelectedIconIds = {
            R.drawable.tab_home_checked,
            R.drawable.tab_scene_checked,
            R.drawable.tab_center_checked,
    };

    /*默认页面的位置*/
    private int lastSelectedPosition = 0;

    /*刚进来则进行蓝牙设备的扫描*/
    private boolean isFirstTime = true;
    /*蓝牙权限申请*/
    private PermissionManager mHelper;
    /*蓝牙设备的操作*/
    private DeviceManager mDeviceManager;

    public static final int PERMISSION_REQUEST_CODE = 101;
    public static final long DEFAULT_SCAN_DURATION = 4000;

    private static final int REQUEST_CODE_BLE = 520;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        setShowBack(false);
        initial();
    }

    /**
     * 初始化操作
     */
    private void initial() {
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.initial(getApplicationContext());

        /*1 初始化底部导航条*/
        initBottomNavigationBar();
        /*2 初始化ViewPager*/
        initViewPager();
        /*3 初始化顶部导航栏标题*/
        showOrHideForward(lastSelectedPosition);
        setToolbarTitle(mAlias[lastSelectedPosition]);
        /* 添加右边前进键单机事件*/
        setForwardTitle(R.string.activity_title_scan);
        setOnForwardClickListener(mOnClickListener);
        /*配置BLEScanner*/
        BLEScanner.getInstance().setCallback(mCallback);
        DeviceManager.getInstance().addOnStateChangedListener(mOnStateChangedListener);
    }

    /**
     * 设置蓝牙扫描回调
     */
    private BLEScanner.Callback mCallback = new BLEScanner.Callback() {
        @Override
        public void onStateChanged(boolean state) {
            System.out.println("Hello world.....................");
            if (state) {
                String title = getString(R.string.activity_dialog_title_main);
                String content = getString(R.string.activity_dialog_content_main);
                showProgressDialog(title, content);
            } else {
                dismissProgressDialog();
                if (AppContext.getInstance().devices.size() > 0) {
                    toast(R.string.activity_start_connect_main);
                    //RxLightService.getInstance().connect(getApplicationContext(), true);
                    String address = AppContext.getInstance().devices.get(0).address;
                    DeviceManager.getInstance().connect(address, true);
                } else {
                    toast(R.string.activity_no_device_main);
                }
            }
        }

        @Override
        public void onDeviceDiscovered(final BluetoothDevice device, int i, byte[] bytes) {
            if (!FilterUtils.filter(bytes)) return; /*过滤自家设备*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    DeviceFragment.newInstance().addDevice(new Device(deviceName, deviceAddress));
                }
            });
        }
    };

    /**
     * 跳转到设备扫描页
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivityForResult(intent, REQUEST_CODE_BLE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLE) {
            if (resultCode == RESULT_OK) {
                /*接收设备数据并加入到全局缓存切进行连接操作*/
                String deviceAddress = data.getStringExtra(ScanActivity.EXTRA_DEVICE_ADDRESS);
                String deviceName = data.getStringExtra(ScanActivity.EXTRA_DEVICE_NAME);
                DeviceFragment.newInstance().addDevice(new Device(deviceName, deviceAddress));
            }
        }
    }

    /**
     * 初始化ViewPager显示Fragments
     */
    private void initViewPager() {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(DeviceFragment.newInstance());
        adapter.addFragment(SceneFragment.newInstance());
        adapter.addFragment(CenterFragment.newInstance());
        mPagerView.setAdapter(adapter);
        mPagerView.setOffscreenPageLimit(2);
        mPagerView.addOnPageChangeListener(mOnPageChangeListener);
        mPagerView.setCurrentItem(lastSelectedPosition, true);
    }

    /**
     * ViewPager滑动事件
     */
    private ViewPager.SimpleOnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            /*切换底部导航栏*/
            mBarView.selectTab(position, false);
            /*设置顶部导航栏标题*/
            setToolbarTitle(mAlias[position]);
            /*显示隐藏Forward按钮*/
            showOrHideForward(position);
        }
    };

    private void showOrHideForward(int position) {
        if (position == 0) {
            setForwardVisibility(View.VISIBLE);
        } else {
            setForwardVisibility(View.GONE);
        }
    }

    /**
     * 初始化底部导航栏
     */
    private void initBottomNavigationBar() {
        /*添加标签页*/
        int length = mTitles.length;
        for (int i = 0; i < length; i++) {
            BottomNavigationItem item = new BottomNavigationItem(mSelectedIconIds[i], mTitles[i]);
            item.setInactiveIconResource(mNormalIconIds[i]);
            mBarView.addItem(item);
        }
        mBarView.setFirstSelectedPosition(lastSelectedPosition > length ? length : lastSelectedPosition);
        mBarView.initialise();
        mBarView.setTabSelectedListener(mOnTabSelectedListener);
    }

    /**
     * 标签选择事件
     */
    private BottomNavigationBar.SimpleOnTabSelectedListener mOnTabSelectedListener = new BottomNavigationBar.SimpleOnTabSelectedListener() {
        @Override
        public void onTabSelected(int position) {
            /*切换ViewPager的显示页面*/
            mPagerView.setCurrentItem(position, true);
            /*切换顶部导航栏的标题*/
            setToolbarTitle(mAlias[position]);
            /*显示隐藏Forward按钮*/
            showOrHideForward(position);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstTime) {
            isFirstTime = false;
            requestPermission();
        }
    }

    private void requestPermission() {
        /*是否有定位权限使用蓝牙操作进行扫描*/
        if (PermissionManager.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            BLEScanner.getInstance().startScan(DEFAULT_SCAN_DURATION);
        } else {
            mHelper = PermissionManager.with(this)
                    .permissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .setPermissionsListener(mPermissionListener)
                    .addRequestCode(PERMISSION_REQUEST_CODE)
                    .request();
        }
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onGranted() {
            BLEScanner.getInstance().startScan(DEFAULT_SCAN_DURATION);
        }

        @Override
        public void onDenied() {
            toast("必须有模糊定位权限才能使用蓝牙");
        }

        @Override
        public void onShowRationale(String[] permissions) {
            Snackbar.make(mBarView, "需要模糊定位权限使用蓝牙", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHelper.setIsPositive(true);
                            mHelper.request();
                        }
                    }).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            mHelper.onPermissionResult(permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BLEScanner.getInstance().isScanning()) {
            BLEScanner.getInstance().stopScan();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog(this).builder()
                    .setCancelable(true)
                    .setTitle(getString(R.string.dialog_title))
                    .setMsg(getString(R.string.dialog_message))
                    .setPositiveButton(getString(R.string.dialog_positive), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            background();
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_negative), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exit();
                        }
                    }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出程序关闭所有
     */
    private void exit() {
        /*还原用户设置*/
        if (!AppContext.getInstance().isBluetoothEnabled) {
            BLEUtils.closeBluetooth();
        }
        finish();
    }

    /**
     * 后台运行，关闭MainActivity 但不关闭后台服务 不关闭蓝牙
     */
    private void background() {
        //moveTaskToBack(false);//此句代码可代替下面所有代码
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startActivitySafely(startIntent);
        Toast.makeText(getApplicationContext(), R.string.exit_background, Toast.LENGTH_SHORT).show();
    }

    /**
     * 安全启动Activity
     *
     * @param intent
     */
    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d("Leo", "MainActivity startActivitySafely() error");
        } catch (SecurityException e) {
            Log.d("Leo", "MainActivity startActivitySafely() error");
        }
    }

    @Override
    protected void suicide() {
        super.suicide();
        DeviceManager.getInstance().removeOnStateChangedListener(mOnStateChangedListener);
        DeviceManager.getInstance().suicide();
        AppContext.getInstance().exitApplication();
    }

    /**
     * 设备状态回调
     */
    private OnStateChangedListener mOnStateChangedListener = new OnStateChangedListener() {
        @Override
        public void onStateChanged(String address, int state) {
            switch (state) {
                case STATE_DISCONNECTED: {
                    //toast("设备已离线：" + address);
                    Device device = AppContext.getInstance().findDevice(address);
                    if (device != null) {
                        device.connect = false;
                    }
                }
                break;
                case STATE_CONNECTED: {
                    //toast("设备已在线：" + address);
                    Device device = AppContext.getInstance().findDevice(address);
                    if (device != null) {
                        device.connect = true;
                    }
                }
                break;
                case STATE_SERVICING: {
                    Device device = AppContext.getInstance().findDevice(address);
                    if (device != null) {
                        /*验证密码*/
                        DeviceManager.getInstance().validatePassword(address, device.password);
                    }
                }
                break;
                default:
                    break;
            }
            DeviceFragment.newInstance().update();
        }
    };

}
