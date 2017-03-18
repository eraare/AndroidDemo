package com.guohua.mlight.view.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.guohua.ios.dialog.AlertDialog;
import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.util.ToolUtils;
import com.guohua.mlight.communication.BLEConstant;
import com.guohua.mlight.communication.BLERecord;
import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.view.adapter.FragmentAdapter;
import com.guohua.mlight.view.fragment.CenterFragment;
import com.guohua.mlight.view.fragment.DialogFragment;
import com.guohua.mlight.view.fragment.HomeFragment;
import com.guohua.mlight.view.fragment.SceneFragment;
import com.guohua.mlight.view.fragment.TimerFragment;
import com.guohua.mlight.view.widget.TitleView;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * @author Leo
 * @detail 项目框架主界面 具有滑动切换功能 各类功能在各自Fragment里
 * @time 2015-10-29
 */
@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {
    private String deviceName; //设备名称
    private String deviceAddress; //设备MAC地址
    private String password; //设备的控制密码

    /*扫描用到的辅助变量*/
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;// 循环标志位
    private static final long SCAN_PERIOD = 10000;// 扫描10s
    private Handler mHandler;//用于两个类之前的消息传递

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
            R.drawable.icon_home_normal,
            R.drawable.icon_scene_normal,
            R.drawable.icon_center_normal,
    };
    /*底部标签导航被选中的ICON图像*/
    private int[] mSelectedIconIds = {
            R.drawable.icon_home_checked,
            R.drawable.icon_scene_checked,
            R.drawable.icon_center_checked,
    };

    /*默认页面的位置*/
    private int lastSelectedPosition = 0;
    /*ViewPager加载Fragment所需要的Adapter*/
    private FragmentAdapter mFragmentAdapter;

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
        /*1 初始化底部导航条*/
        initBottomNavigationBar();
        /*2 初始化ViewPager*/
        initViewPager();
        /*3 初始化顶部导航栏标题*/
        setToolbarTitle(mAlias[lastSelectedPosition]);
        showOrHideForward(lastSelectedPosition);
        /* 添加右边前进键单机事件*/
        setOnForwardClickListener(mOnClickListener);
        /*4 初始化其他*/
        initValues();
        registerTheReceiver();
        CodeUtils.setPassword(password);
        // 点击一次设置当前tab 同时会触发切换fragment
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new android.os.Handler();
        scanLeDevice(true);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            showDialogFragment(DialogFragment.TAG);
            startActivity(new Intent(MainActivity.this, DeviceActivity.class));
        }
    };

    private TitleView.OnLeftClickListener mOnLeftClickListener = new TitleView.OnLeftClickListener() {
        @Override
        public void onLeftClick(View v) {
            showDialogFragment(DialogFragment.TAG);
        }
    };

    /**
     * 初始化ViewPager显示Fragments
     */
    private void initViewPager() {
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mFragmentAdapter.addFragment(HomeFragment.newInstance());
        mFragmentAdapter.addFragment(SceneFragment.newInstance());
        mFragmentAdapter.addFragment(CenterFragment.newInstance());
        mPagerView.setAdapter(mFragmentAdapter);
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

    private Runnable mStopRunnable = new Runnable() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
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
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    AppContext.getInstance().addDevice(new Device(deviceName, deviceAddress, true));
                    if (DialogFragment.getInstance().mGroupAdapter != null) {
                        DialogFragment.getInstance().mGroupAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    /**
     * 初始化Preference里的存储值
     */
    private void initValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        deviceName = sp.getString(Constants.KEY_DEVICE_NAME, getString(R.string.app_name));
        deviceAddress = sp.getString(Constants.KEY_DEVICE_ADDRESS, "a9:87:65:43:21:00");
        password = sp.getString(deviceAddress, Constants.DEFAULT_PASSWORD);
    }

    /**
     * 显示对话框Fragment
     *
     * @param tag
     */
    public void showDialogFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            if (TextUtils.equals(tag, DialogFragment.TAG)) {
                fragment = DialogFragment.getInstance();
            } else if (TextUtils.equals(tag, TimerFragment.TAG)) {
                fragment = TimerFragment.getInstance();
            }
            fragmentTransaction.add(fragment, tag);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commit();
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
        /**
         * 关闭所有后台服务
         */
        Intent intent = new Intent(Constants.ACTION_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //stopGuard();
        AppContext.getInstance().disonnectAll();
        AppContext.getInstance().closeBLEService();
        if (!ToolUtils.readBluetoothState(this)) {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.KEY_SELFIE_RUN, false).apply();
        finish();
    }

    /**
     * 后台运行，关闭MainActivity 但不关闭后台服务 不关闭蓝牙
     */
    private void background() {
        //startGuard();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BLEConstant.REQUEST_DEVICE_SCAN) {
                deviceName = data.getStringExtra(BLEConstant.EXTRA_DEVICE_NAME);
                deviceAddress = data.getStringExtra(BLEConstant.EXTRA_DEVICE_ADDRESS);
                DialogFragment.getInstance().onResult(new Device(deviceName, deviceAddress, true));
            }
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (TextUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                //startGuard();
            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                //showSnackbar(title, R.string.main_state_bond);
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                //showSnackbar(title, R.string.main_state_equipment);
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                //showSnackbar(title, R.string.main_state_bluetooth);
            } else if (TextUtils.equals(action, BLEConstant.ACTION_BLE_CONNECTED)) {
                if (DialogFragment.getInstance().mProgressDialog != null) {
                    DialogFragment.getInstance().mProgressDialog.dismiss();
                }
            } else if (TextUtils.equals(action, BLEConstant.ACTION_BLE_DISCONNECTED)) {
                if (DialogFragment.getInstance().mProgressDialog != null) {
                    DialogFragment.getInstance().mProgressDialog.dismiss();
                }
            }
            DialogFragment.getInstance().updateAdapter();
        }
    };

    private Snackbar snackbar;

    private void showSnackbar(View view, int id) {
        if (snackbar != null) {
            snackbar.dismiss();
        }
        snackbar = Snackbar.make(view, id, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.main));
        snackbar.show();
    }

    private void registerTheReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        mIntentFilter.addAction(BLEConstant.ACTION_BLE_CONNECTED);
        mIntentFilter.addAction(BLEConstant.ACTION_BLE_DISCONNECTED);

        mIntentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void suicide() {
        super.suicide();
        AppContext.getInstance().exitApplication();
    }
}
