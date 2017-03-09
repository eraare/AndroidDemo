package com.guohua.mlight;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.communication.BLEConstant;
import com.guohua.mlight.communication.BLERecord;
import com.guohua.mlight.view.fragment.CenterFragment;
import com.guohua.mlight.view.fragment.DialogFragment;
import com.guohua.mlight.view.fragment.MainFragment;
import com.guohua.mlight.view.fragment.SceneFragment;
import com.guohua.mlight.view.fragment.TimerFragment;
import com.guohua.mlight.guard.WatcherKingService;
import com.guohua.mlight.guard.WatcherQueenService;
import com.guohua.mlight.library.BluetoothConstant;
import com.guohua.mlight.upgrade.UpgradeManager;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.util.Constant;
import com.guohua.mlight.common.util.ToastUtill;
import com.guohua.mlight.common.util.ToolUtils;
import com.guohua.mlight.view.TabBarView;
import com.guohua.mlight.view.TitleView;

import java.util.ArrayList;

/**
 * @author Leo
 * @detail 项目框架主界面 具有滑动切换功能 各类功能在各自Fragment里
 * @time 2015-10-29
 */
@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {
    private ImageView add, settings;//重新选择设备 和 设置界面
    private TitleView title;//界面里的标头 用于显示当前设备的名称

    private String deviceName;//设备名称
    private String deviceAddress;
    private String password;//设备的控制密码

    /*添加标签框架用到的属性*/
    private TabBarView tabBar;//标签导航栏
    private int currentTab = 0;//当前标签页

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;// 循环标志位
    private static final long SCAN_PERIOD = 10000;// 扫描10s

    private Handler mHandler;//用于两个类之前的消息传递
    private UpgradeManager upgradeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();//初始化数据和控件
    }

    /**
     * 初始化操作
     */
    private void init() {
        initValues();
        findViewsByIds();
        registerTheReceiver();
        CodeUtils.setPassword(password);
        // 点击一次设置当前tab 同时会触发切换fragment
        tabBar.clickTab(currentTab, null);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new android.os.Handler();
        scanLeDevice(true);

        //用于版本更新
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case UpgradeManager.WHAT_UPDATE_TRUE: {
                        System.out.println("mainactivity serverVersion WHAT_UPDATE_TRUE ");
                        ToastUtill.showToast(MainActivity.this, getString(R.string.soft_upgrade_yes), Constant.TOASTLENGTH).show();
                        new AlertDialog.Builder(MainActivity.this).setIcon(R.mipmap.ic_launcher).setTitle(R.string.soft_update_title).setMessage(R.string.soft_update_info)
                                .setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (upgradeManager != null) {
                                            upgradeManager.update();
                                        }
                                    }
                        }).setNegativeButton(R.string.settings_negative, null).show();
                    }
                    break;
                    case UpgradeManager.WHAT_UPDATE_FALSE: {
                        System.out.println("mainactivity serverVersion WHAT_UPDATE_FALSE ");
                        ToastUtill.showToast(MainActivity.this, getString(R.string.soft_upgrade_no), Constant.TOASTLENGTH).show();
                    }
                    break;
                    case UpgradeManager.WHAT_UPDATE_DOWNLOAD: {
                        System.out.println("mainactivity serverVersion WHAT_UPDATE_DOWNLOAD ");
                        if (upgradeManager != null) {
                            upgradeManager.setProgress();
                        }
//                        ToastUtill.showToast(MainActivity.this, getString(R.string.soft_updating), Constant.TOASTLENGTH).show();
                    }
                    break;
                    case UpgradeManager.WHAT_UPDATE_FINISH: {
                        System.out.println("mainactivity serverVersion WHAT_UPDATE_FINISH ");
                        if (upgradeManager != null) {
                            upgradeManager.installApk();
//                            ToastUtill.showToast(MainActivity.this, getString(R.string.soft_updating), Constant.TOASTLENGTH).show();
                        }
                    }
                    default:
                        break;
                }
            }
        };
        upgradeManager = new UpgradeManager(this, mHandler);
        checkUpgrade();
    }

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
        deviceName = sp.getString(Constant.KEY_DEVICE_NAME, getString(R.string.app_name));
        deviceAddress = sp.getString(Constant.KEY_DEVICE_ADDRESS, "a9:87:65:43:21:00");
        password = sp.getString(deviceAddress, Constant.DEFAULT_PASSWORD);
    }

    /**
     * 初始化所有控件并绑定相关事件
     */
    private void findViewsByIds() {
        /*标题控件及点击事件绑定*/
        title = (TitleView) findViewById(R.id.tv_title_main);
        title.setOnLeftClickListener(mOnLeftClickListener);
        /*取得标签栏控件并绑定标签点击事件*/
        tabBar = (TabBarView) findViewById(R.id.tbv_bar_main);
        tabBar.setOnTabCheckedListener(mOnTabCheckedListener);
    }

    private TitleView.OnLeftClickListener mOnLeftClickListener = new TitleView.OnLeftClickListener() {
        @Override
        public void onLeftClick(View v) {
            showDialogFragment(DialogFragment.TAG);
        }
    };

    /**
     * tab点击事件
     */
    private TabBarView.OnTabCheckedListener mOnTabCheckedListener = new TabBarView.OnTabCheckedListener() {
        @Override
        public void onTabChecked(int index, View view) {
            switchFragment(index);
        }
    };

    /**
     * 切换标签页
     *
     * @param index
     */
    private void switchFragment(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment mainFragment = fragmentManager.findFragmentByTag(MainFragment.TAG);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            fragmentTransaction.add(R.id.fl_container_main, mainFragment, MainFragment.TAG);
        }

        Fragment sceneFragment = fragmentManager.findFragmentByTag(SceneFragment.TAG);
        if (sceneFragment == null) {
            sceneFragment = SceneFragment.newInstance();
            fragmentTransaction.add(R.id.fl_container_main, sceneFragment, SceneFragment.TAG);
        }

        Fragment centerFragment = fragmentManager.findFragmentByTag(CenterFragment.TAG);
        if (centerFragment == null) {
            centerFragment = CenterFragment.newInstance();
            fragmentTransaction.add(R.id.fl_container_main, centerFragment, CenterFragment.TAG);
        }

        switch (index) {
            case 0: {
                fragmentTransaction.show(mainFragment);
                fragmentTransaction.hide(sceneFragment);
                fragmentTransaction.hide(centerFragment);
                title.setTitle(MainFragment.TITLE);
                title.hiddenLeft(false);
            }
            break;
            case 1: {
                fragmentTransaction.show(sceneFragment);
                fragmentTransaction.hide(mainFragment);
                fragmentTransaction.hide(centerFragment);
                title.setTitle(SceneFragment.TITLE);
                title.hiddenLeft(true);
            }
            break;
            case 2: {
                fragmentTransaction.show(centerFragment);
                fragmentTransaction.hide(sceneFragment);
                fragmentTransaction.hide(mainFragment);
                title.setTitle(CenterFragment.TITLE);
                title.hiddenLeft(true);
            }
            break;
            default:
                break;
        }
        fragmentTransaction.commit();
        currentTab = index;
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
            new AlertDialog.Builder(this).setTitle(R.string.dialog_title).setIcon(R.mipmap.ic_launcher).setMessage(R.string.dialog_message).setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    background();
                }
            }).setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    exit();
                }
            }).setNeutralButton(R.string.dialog_neutral, null).show();
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
        Intent intent = new Intent(Constant.ACTION_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //stopGuard();
        AppContext.getInstance().disonnectAll();
        AppContext.getInstance().closeBLEService();
        if (!ToolUtils.readBluetoothState(this)) {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constant.KEY_SELFIE_RUN, false).apply();
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
     * 启动守护进程
     */
    private void startGuard() {
        Intent kingService = new Intent(this, WatcherKingService.class);
        startService(kingService);

        Intent queenService = new Intent(this, WatcherQueenService.class);
        startService(queenService);
    }

    /**
     * 关闭守护进程
     */
    private void stopGuard() {
        Intent kingService = new Intent(this, WatcherKingService.class);
        stopService(kingService);

        Intent queenService = new Intent(this, WatcherQueenService.class);
        stopService(queenService);
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

    private ArrayList<String> selectedScanDeviceList = new ArrayList<String>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BluetoothConstant.REQUEST_DEVICE_SCAN) {

//                deviceName = data.getStringExtra(BluetoothConstant.EXTRA_DEVICE_NAME);
//                deviceAddress = data.getStringExtra(BluetoothConstant.EXTRA_DEVICE_ADDRESS);
//                DialogFragment.getInstance().onResult(new Device(deviceName, deviceAddress, true));

                selectedScanDeviceList = data.getStringArrayListExtra(BLEConstant.EXTRA_DEVICE_LIST);
                ArrayList<Device> resultDevList = new ArrayList<Device>();
                String addrAndName = "";
                System.out.println("mainactivity onActivityResult selectedScanDeviceList------------selectedScanDeviceList.size()-------------- " + selectedScanDeviceList.size());
                for (int i = 0; i < selectedScanDeviceList.size(); i++) {
                    addrAndName = selectedScanDeviceList.get(i);
                    int splitPos = addrAndName.indexOf(";");
                    resultDevList.add(new Device(addrAndName.substring(splitPos+1), addrAndName.substring(0, splitPos), true));
                    System.out.println("addrAndName: " + addrAndName + "; splitPos: " + splitPos +
                            "; addr: " + addrAndName.substring(0, splitPos) + "        name: " + addrAndName.substring(splitPos+1));
                }
                System.out.println("mainactivity onActivityResult selectedScanDeviceList--------------------------");
                DialogFragment.getInstance().onResult(resultDevList);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        AppContext.getInstance().exitApplication();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //系统自带的蓝牙状态判断
//            if (TextUtils.equals(action, BluetoothDevice.ACTION_ACL_CONNECTED)) {
//                Snackbar.make(title, R.string.main_state_online, Snackbar.LENGTH_LONG).show();
//            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
//                Snackbar.make(title, R.string.main_state_offline, Snackbar.LENGTH_SHORT).show();
//            } else

            if (TextUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                //startGuard();
            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                Snackbar.make(title, R.string.main_state_bond, Snackbar.LENGTH_LONG).show();
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                Snackbar.make(title, R.string.main_state_equipment, Snackbar.LENGTH_LONG).show();
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Snackbar.make(title, R.string.main_state_bluetooth, Snackbar.LENGTH_LONG).show();
            } else if (TextUtils.equals(action, BLEConstant.ACTION_BLE_CONNECTED)) {
                if (DialogFragment.getInstance().mProgressDialog != null) {
                    DialogFragment.getInstance().mProgressDialog.dismiss();
                }
            } else if (TextUtils.equals(action, BLEConstant.ACTION_BLE_DISCONNECTED)) {
                if (DialogFragment.getInstance().mProgressDialog != null) {
                    DialogFragment.getInstance().mProgressDialog.dismiss();
                }
            }/*else if (TextUtils.equals(action, BluetoothConstant.ACTION_CONNECT_SUCCESS)) {
                //连接成功
            } else if (TextUtils.equals(action, BluetoothConstant.ACTION_CONNECT_ERROR)) {
                //连接失败
            }*/
            DialogFragment.getInstance().updateAdapter();
        }
    };

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
//        mIntentFilter.addAction(BluetoothConstant.ACTION_CONNECT_ERROR);
//        mIntentFilter.addAction(BluetoothConstant.ACTION_CONNECT_SUCCESS);

        mIntentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    public void checkUpgrade() {
        if (!ToolUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.about_no_network, Toast.LENGTH_SHORT).show();
            return;
        }
        if (upgradeManager != null) {
            System.out.println("start checkUpgrade........................");
            upgradeManager.check();
            System.out.println("finished checkUpgrade........................");
        }
    }
}
