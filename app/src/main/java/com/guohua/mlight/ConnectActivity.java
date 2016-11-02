package com.guohua.mlight;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.communication.BLEConstant;
import com.guohua.mlight.fragment.GroupFragment;
import com.guohua.mlight.fragment.PersonalFragment;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.EverydayWords;
import com.guohua.mlight.util.ToolUtils;

/**
 * @author Leo
 * @detail 连接蓝牙的配置界面
 * @time 2016-01-08
 */
public class ConnectActivity extends AppCompatActivity {
    private TextView title;//标题

    private LinearLayout personal;//个人中心
    private ImageView personalIcon;//个人中心图标
    private TextView personalTitle;//个人中心标题

    private LinearLayout group;//组设备模式
    private ImageView groupIcon;//组模式图标
    private TextView groupTitle;//组模式标题

    private int currentFragment;//记录显示的是哪一个Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        init();//进行初始化
    }

    /**
     * 初始化数据的控件等等
     */
    private void init() {
        findViewsByIds();
        switchFragment(R.id.ll_group_connect);
        switchStatus(R.id.ll_group_connect);
        initConnectDialog();
        if (ToolUtils.isNetworkAvailable(this)) {
            new EverydayWords(this).execute();
        }
    }

    /**
     * 切换布局文件
     *
     * @param id
     */
    private void switchFragment(int id) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();

        Fragment personalFragment = mFragmentManager.findFragmentByTag(PersonalFragment.TAG);
        if (personalFragment == null) {
            personalFragment = PersonalFragment.getInstance();
            mTransaction.add(R.id.fl_container_connect, personalFragment, PersonalFragment.TAG);
        }

        Fragment groupFragment = mFragmentManager.findFragmentByTag(GroupFragment.TAG);
        if (groupFragment == null) {
            groupFragment = GroupFragment.getInstance();
            mTransaction.add(R.id.fl_container_connect, groupFragment, GroupFragment.TAG);
        }

        switch (id) {
            case R.id.ll_personal_connect: {
                mTransaction.hide(groupFragment);
                mTransaction.show(personalFragment);
                currentFragment = PersonalFragment.ID;
            }
            break;
            default: {
                mTransaction.hide(personalFragment);
                mTransaction.show(groupFragment);
                currentFragment = GroupFragment.ID;
            }
            break;
        }
        mTransaction.commit();
    }

    /**
     * 切换状态
     *
     * @param id
     */
    private void switchStatus(int id) {
        switch (id) {
            case R.id.ll_personal_connect: {
                title.setText(R.string.connect_personal_title);

                personalIcon.setImageResource(R.drawable.icon_device_selected);
                personalTitle.setTextColor(getResources().getColor(R.color.main));

                groupIcon.setImageResource(R.drawable.icon_group_normal);
                groupTitle.setTextColor(getResources().getColor(R.color.greyd));
            }
            break;
            default: {
                title.setText(R.string.connect_device_title);

                personalIcon.setImageResource(R.drawable.icon_device_normal);
                personalTitle.setTextColor(getResources().getColor(R.color.greyd));

                groupIcon.setImageResource(R.drawable.icon_group_selected);
                groupTitle.setTextColor(getResources().getColor(R.color.main));
            }
            break;
        }
    }

    /**
     * 根据ID获取控件并绑定事件
     */
    private void findViewsByIds() {
        title = (TextView) findViewById(R.id.tv_title_connect);

        personal = (LinearLayout) findViewById(R.id.ll_personal_connect);
        personalIcon = (ImageView) findViewById(R.id.iv_personal_icon_connect);
        personalTitle = (TextView) findViewById(R.id.tv_personal_title_connect);

        group = (LinearLayout) findViewById(R.id.ll_group_connect);
        groupIcon = (ImageView) findViewById(R.id.iv_group_icon_connect);
        groupTitle = (TextView) findViewById(R.id.tv_group_title_connect);

        personal.setOnClickListener(mOnClickListener);
        group.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*int id = v.getId();
            switch (id) {
                case R.id.ll_group_connect: {
                    if (GroupFragment.getInstance().addSelectedDevices2Cache()) {
                        showConnectDialog();
                        AppContext.getInstance().connectAll();
                    } else {
                        Snackbar.make(v, R.string.connect_select_tip, Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            }*/

            //android6.0 运行时申请蓝牙权限
            ToolUtils.requestPermissions(ConnectActivity.this, Manifest.permission.BLUETOOTH, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH);

            //android6.0 运行时申请操作蓝牙权限
            ToolUtils.requestPermissions(ConnectActivity.this, Manifest.permission.BLUETOOTH_ADMIN, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN);

            //android6.0 运行时申请定位权限（android6.0 下，要使用蓝牙需同时开启定位权限）
            ToolUtils.requestPermissions(ConnectActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, Constant.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


            int id = v.getId();
            switchFragment(id);
            switchStatus(id);
        }
    };

    private void startActivityAndFinish() {
        mProgressDialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /*if (TextUtils.equals(action, BluetoothConstant.ACTION_CONNECT_ERROR)) {
                mProgressDialog.dismiss();
                Toast.makeText(ConnectActivity.this, R.string.connect_toast_failed, Toast.LENGTH_LONG).show();
            } else if (TextUtils.equals(action, Constant.ACTION_RECEIVED_STATUS)) {
                startActivityAndFinish();
            } else if(TextUtils.equals(action, Constant.ACTION_FIRMWARE_VERSION)) {
                startActivityAndFinish();
            } *//*else if(TextUtils.equals(action, BluetoothConstant.ACTION_CONNECT_SUCCESS)) {
                String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_VERSION, null);
                ThreadPool.getInstance().addTask(new SendRunnable(data));
            }*//* else */

//            if(TextUtils.equals(action, BluetoothDevice.ACTION_ACL_CONNECTED)) {
//                System.out.println("ConnectActivity ACTION_ACL_CONNECTED");
//                startActivityAndFinish();
//            } else

            if(TextUtils.equals(action, BLEConstant.ACTION_BLE_CONNECTED)) {
                mProgressDialog.dismiss();
                System.out.println("ConnectActivity ACTION_BLE_CONNECTED");
                startActivityAndFinish();
            } else if(TextUtils.equals(action, BLEConstant.ACTION_BLE_DISCONNECTED)) {
                mProgressDialog.dismiss();
                Toast.makeText(ConnectActivity.this, R.string.connect_toast_failed, Toast.LENGTH_LONG).show();
            }
//            else if(TextUtils.equals(action, BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
//                mProgressDialog.dismiss();
//                Toast.makeText(ConnectActivity.this, R.string.connect_toast_failed, Toast.LENGTH_LONG).show();
//            }
        }
    };

    private void registerTheReceiver() {
        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction(BluetoothConstant.ACTION_CONNECT_ERROR);
//        mFilter.addAction(BluetoothConstant.ACTION_CONNECT_SUCCESS);
//        mFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        mFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        mFilter.addAction(BLEConstant.ACTION_BLE_DISCONNECTED);
        mFilter.addAction(BLEConstant.ACTION_BLE_CONNECTED);
        mFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerTheReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    private ProgressDialog mProgressDialog;

    private void initConnectDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.connect_dialog_title);
        mProgressDialog.setMessage(getString(R.string.connect_dialog_message));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    //private static final long SHOW_TIME = 12000;

    public void showConnectDialog() {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        }, SHOW_TIME);*/
        mProgressDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (GroupFragment.getInstance().onKeyDown()) {
                return true;
            }
            if (!ToolUtils.readBluetoothState(this)) {
                BluetoothAdapter.getDefaultAdapter().disable();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
