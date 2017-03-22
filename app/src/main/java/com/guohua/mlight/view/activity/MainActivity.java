package com.guohua.mlight.view.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.guohua.mlight.lwble.BLEController;
import com.guohua.mlight.lwble.BLEFilter;
import com.guohua.mlight.lwble.BLEScanner;
import com.guohua.mlight.lwble.MessageEvent;
import com.guohua.mlight.model.bean.LightInfo;
import com.guohua.mlight.model.impl.LightService;
import com.guohua.mlight.model.impl.RxLightService;
import com.guohua.mlight.view.adapter.FragmentAdapter;
import com.guohua.mlight.view.fragment.CenterFragment;
import com.guohua.mlight.view.fragment.HomeFragment;
import com.guohua.mlight.view.fragment.SceneFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * @author Leo
 * @detail 项目框架主界面 具有滑动切换功能 各类功能在各自Fragment里
 * @time 2015-10-29
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

    /*刚进来则进行蓝牙设备的扫描*/
    private boolean isFirstTime = true;

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
        EventBus.getDefault().register(this);
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
        showOrHideForward(lastSelectedPosition);
        setToolbarTitle(mAlias[lastSelectedPosition]);
        /* 添加右边前进键单机事件*/
        setForwardTitle("设备管理");
        setOnForwardClickListener(mOnClickListener);
        /*配置BLEScanner*/
        BLEScanner.getInstance().setStateCallback(mStateCallback);
        BLEScanner.getInstance().setDeviceDiscoveredListener(mDeviceDiscoveredListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, DeviceActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstTime) {
            isFirstTime = false;
            BLEScanner.getInstance().startScan(5000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BLEScanner.getInstance().stopScan();
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
        EventBus.getDefault().unregister(this);
        AppContext.getInstance().exitApplication();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.state) {
            case BLEController.STATE_DISCONNECTED: {
                toast("设备已离线：" + event.address);
                LightInfo lightInfo = AppContext.getInstance().findLight(event.address);
                if (lightInfo != null) {
                    lightInfo.connect = false;
                }
            }
            break;
            case BLEController.STATE_CONNECTED: {
                toast("设备已在线：" + event.address);
                LightInfo lightInfo = AppContext.getInstance().findLight(event.address);
                if (lightInfo != null) {
                    lightInfo.connect = true;
                }
            }
            break;
            case BLEController.STATE_SERVICING: {
                LightInfo lightInfo = AppContext.getInstance().findLight(event.address);
                if (lightInfo != null) {
                    /*地址和密码*/
                    String address = lightInfo.address;
                    String password = lightInfo.password;
                    /*如果密码为空则使用系统全局密码*/
                    if (TextUtils.isEmpty(password)) {
                        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        password = sp.getString(Constants.KEY_GLOBAL_PASSWORD, Constants.DEFAULT_GLOBAL_PASSWORD);
                    }
                    /*验证密码*/
                    LightService.getInstance().validatePassword(address, password);
                }
            }
            break;
            default:
                break;
        }
    }

    /**
     * 发现设备是添加到列表
     */
    private BLEScanner.DeviceDiscoveredListener mDeviceDiscoveredListener = new BLEScanner.DeviceDiscoveredListener() {
        @Override
        public void onDiscovered(final BluetoothDevice device, int rssi, byte[] bytes) {
            if (!BLEFilter.filter(bytes)) return; /*过滤自家设备*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    AppContext.getInstance().addLight(new LightInfo(deviceName, deviceAddress));
                }
            });
        }
    };

    /**
     * 扫描状态改变回调函数
     */
    private BLEScanner.StateCallback mStateCallback = new BLEScanner.StateCallback() {
        @Override
        public void onStateChanged(boolean state) {
            if (state) {
                showProgressDialog("搜索设备", "请稍后，正在搜索设备...");
            } else {
                dismissProgressDialog();
                if (AppContext.getInstance().lights.size() > 0) {
                    toast("开始连接设备");
                    RxLightService.getInstance().connect(getApplicationContext(), true);
                } else {
                    toast("未找到相关设备");
                }
            }
        }
    };
}
