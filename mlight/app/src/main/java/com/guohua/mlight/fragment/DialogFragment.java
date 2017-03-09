package com.guohua.mlight.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.AppContext;
import com.guohua.mlight.R;
import com.guohua.mlight.view.adapter.GroupAdapter;
import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.communication.BLEActivity;
import com.guohua.mlight.communication.BLEConstant;
import com.guohua.mlight.view.dialog.SettingsDialog;
import com.guohua.mlight.common.util.ToolUtils;
import com.guohua.mlight.view.TitleView;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenu;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenuCreator;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenuItem;
import com.guohua.mlight.view.widget.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

/**
 * @author Leo
 *         #time 2016-04-19
 *         #time 设备管理页
 */
public class DialogFragment extends android.support.v4.app.DialogFragment {
    public static final String TAG = DialogFragment.class.getSimpleName();
    private Activity mContext;
    /**
     * 音例模式
     */
    private volatile static DialogFragment dialogFragment = null;

    public static DialogFragment getInstance() {
        if (dialogFragment == null) {
            synchronized (Scene1Fragment.class) {
                if (dialogFragment == null) {
                    dialogFragment = new DialogFragment();
                }
            }
        }
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setCancelable(false);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.dialog_group_main, container, false);
        init();
//        getDialog().setCanceledOnTouchOutside(false);
        return rootView;
    }

    private View rootView;
    private TitleView title;//标题
    private SwipeMenuListView devices;//设备列表
    public GroupAdapter mGroupAdapter;//适配器
    private TextView open;//全开
    private TextView close;//全关
    private boolean isSelectedAll;//是否全选

    private void init() {
        isSelectedAll = true;//默认是选择所有
        mContext = getActivity();
        findViewsByIds();
        mGroupAdapter = new GroupAdapter(mContext);
        mGroupAdapter.setOperatorClickListener(new GroupAdapter.OnOperatorClickListener() {
            @Override
            public void onOperatorClick(int position, Device device) {
                if (device.isConnected()) {
                    mProgressDialog.show();
                    AppContext.getInstance().disonnect(device.getDeviceAddress(), false);
                    showToast(R.string.dialog_toast_disconnecting);
                } else {
                    mProgressDialog.show();
                    AppContext.getInstance().connect(device.getDeviceAddress());
                    showToast(R.string.dialog_toast_connecting);
                }
            }
        });
        mGroupAdapter.setDatas(AppContext.getInstance().devices);
        devices.setAdapter(mGroupAdapter);
        initSwipeMenu();
        initConnectDialog();
    }

    private void findViewsByIds() {
        title = (TitleView) rootView.findViewById(R.id.tv_title_dialog);
        devices = (SwipeMenuListView) rootView.findViewById(R.id.lv_devices_group_dialog);
        open = (TextView) rootView.findViewById(R.id.tv_open_dialog);
        close = (TextView) rootView.findViewById(R.id.tv_close_dialog);
        // 绑定监听器
        open.setOnClickListener(mOnClickListener);
        close.setOnClickListener(mOnClickListener);
        title.setOnRightClickListener(mOnRightClickListener);
        title.setOnLeftClickListener(mOnLeftClickListener);
    }

    /**
     * 标题右边右边点击事件
     */
    private TitleView.OnRightClickListener mOnRightClickListener = new TitleView.OnRightClickListener() {
        @Override
        public void onRightClick(View v) {
            selectTheDevice();
        }
    };

    /**
     * 标题左边点击事件
     */
    private TitleView.OnLeftClickListener mOnLeftClickListener = new TitleView.OnLeftClickListener() {
        @Override
        public void onLeftClick(View v) {
            if (isSelectedAll) {
                mGroupAdapter.selectAll(false);
                isSelectedAll = false;
                title.setLeftTitle(getString(R.string.dialog_select_all));
            } else {
                isSelectedAll = true;
                title.setLeftTitle(getString(R.string.dialog_cancel_all));
                mGroupAdapter.selectAll(true);
            }
            mGroupAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 点击事伯
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tv_open_dialog: {
                    AppContext.getInstance().connectAll();
                    Toast.makeText(mContext, R.string.dialog_toast_connecting, Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.tv_close_dialog: {
                    AppContext.getInstance().disonnectAll();
                    Toast.makeText(mContext, R.string.dialog_toast_disconnecting, Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
            }
        }
    };

    private void selectTheDevice() {
        Intent intent = new Intent(mContext, BLEActivity.class);
        mContext.startActivityForResult(intent, BLEConstant.REQUEST_DEVICE_SCAN);
    }

    public void onResult(Device device) {
        System.out.println("onResult callback…………");
        mGroupAdapter.addDevice(device);
        mGroupAdapter.notifyDataSetChanged();
    }

    public void onResult(ArrayList<Device> deviceList) {
        System.out.println("onResult callback…………");
        for (int i = 0; i < deviceList.size(); i++) {
            mGroupAdapter.addDevice(deviceList.get(i));
        }
        AppContext.getInstance().connectAll();
        mGroupAdapter.notifyDataSetChanged();
    }

    private void initSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // 修改灯名
                SwipeMenuItem nameItem = new SwipeMenuItem(mContext);
                nameItem.setBackground(new ColorDrawable(getResources().getColor(R.color.main)));
                nameItem.setWidth(ToolUtils.dp2px(mContext, 70));
                nameItem.setTitle(getString(R.string.settings_name));
                nameItem.setTitleSize(14);
                nameItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(nameItem);
                // 修改密码
                SwipeMenuItem passwordItem = new SwipeMenuItem(mContext);
                passwordItem.setBackground(new ColorDrawable(getResources().getColor(R.color.main)));
                passwordItem.setWidth(ToolUtils.dp2px(mContext, 70));
                passwordItem.setTitle(getString(R.string.settings_password));
                passwordItem.setTitleSize(14);
                passwordItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(passwordItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.main)));
                deleteItem.setWidth(ToolUtils.dp2px(mContext, 70));
                deleteItem.setTitle(getString(R.string.settings_color));
                deleteItem.setTitleSize(14);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        devices.setMenuCreator(creator);

        devices.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Device device = mGroupAdapter.getDevice(position);
                if (!device.isConnected()) {
                    Toast.makeText(mContext, R.string.dialog_state_tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (index) {
                    case 0: {
                        SettingsDialog.showChangeAccount(mContext, position);
                    }
                    break;
                    case 1: {
                        SettingsDialog.showChangePassword(mContext, device.getDeviceAddress());
                    }
                    break;
                    case 2: {
                        SettingsDialog.showCurrentColor(mContext, device.getDeviceAddress());
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
        devices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= AppContext.getInstance().devices.size()) {
                    return false;
                }
                Device device = AppContext.getInstance().devices.remove(position);
                if (device != null) {
                    AppContext.getInstance().disonnect(device.getDeviceAddress(), true);
                }
                mGroupAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    public void updateAdapter() {
        if (mGroupAdapter != null)
            mGroupAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = dm.widthPixels;
        layoutParams.height = dm.heightPixels * 3 / 4;
        layoutParams.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(layoutParams);
    }

    /**
     * 显示Toast
     */
    private Toast toast;

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showToast(int id) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(mContext, id, Toast.LENGTH_SHORT);
        toast.show();
    }

    /*连接对话框*/
    public ProgressDialog mProgressDialog;

    private void initConnectDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(R.string.connect_dialog_title);
        mProgressDialog.setMessage(getString(R.string.connect_dialog_message));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }
}
