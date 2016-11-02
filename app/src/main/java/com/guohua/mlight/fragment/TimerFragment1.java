package com.guohua.mlight.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.guohua.mlight.AppContext;
import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.adapter.GroupAdapter;
import com.guohua.mlight.bean.Device;
import com.guohua.mlight.communication.BLEActivity;
import com.guohua.mlight.communication.BLEConstant;
import com.guohua.mlight.util.ToolUtils;
import com.guohua.mlight.view.swipemenulistview.SwipeMenu;
import com.guohua.mlight.view.swipemenulistview.SwipeMenuCreator;
import com.guohua.mlight.view.swipemenulistview.SwipeMenuItem;
import com.guohua.mlight.view.swipemenulistview.SwipeMenuListView;

/**
 * Created by Leo on 2016/4/19.
 */
public class TimerFragment1 extends android.support.v4.app.DialogFragment {
    public static final String TAG = TimerFragment1.class.getSimpleName();
    private MainActivity mContext;
    private SharedPreferences sp;
    /**
     * 音例模式
     */
    private volatile static TimerFragment1 timerFragment = null;

    public static TimerFragment1 getInstance() {
        if (timerFragment == null) {
            synchronized (Scene1Fragment.class) {
                if (timerFragment == null) {
                    timerFragment = new TimerFragment1();
                }
            }
        }
        return timerFragment;
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
    private ImageView add;
    private SwipeMenuListView timers;
    private GroupAdapter mGroupAdapter;

    private void init() {
        mContext = (MainActivity) getActivity();
        findViewsByIds();
        mGroupAdapter = new GroupAdapter(mContext);
        mGroupAdapter.setOperatorClickListener(new GroupAdapter.OnOperatorClickListener() {
            @Override
            public void onOperatorClick(int position, Device device) {
                if (device.isConnected()) {
                    AppContext.getInstance().disonnect(device.getDeviceAddress(), false);
                    showToast(R.string.dialog_toast_disconnecting);
                } else {
                    AppContext.getInstance().connect(device.getDeviceAddress());
                    showToast(R.string.dialog_toast_connecting);
                }
            }
        });

        sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
        String timerListJson = sp.getString("Timers", "");

        /*saga
        Gson gson = new Gson();
        timerInfos = (TimerInfos) gson
                .fromJson(timerListJson, TimerInfos.class);
        if (timerInfos != null) {
            timerInfoList = timerInfos.timerInfoList;
        } else {
            timerInfos = new TimerInfos();
            timerInfoList = new ArrayList<TimerInfo>();
            timerInfos.timerInfoList = timerInfoList;
        }

        mGroupAdapter.setDatas(AppContext.getInstance().timers);
        devices.setAdapter(mGroupAdapter);*/

        initSwipeMenu();
    }

    private void findViewsByIds() {
        //add = (ImageView) rootView.findViewById(R.id.iv_add_group_dialog);
        //saga
//        devices = (SwipeMenuListView) rootView.findViewById(R.id.lv_devices_group_dialog);
        //add.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectTheDevice();
        }
    };

    private void selectTheDevice() {
        Intent intent = new Intent(mContext, BLEActivity.class);
        mContext.startActivityForResult(intent, BLEConstant.REQUEST_DEVICE_SCAN);
    }

    public void onResult(Device device) {
        mGroupAdapter.addDevice(device);
        mGroupAdapter.notifyDataSetChanged();
    }

    private void initSwipeMenu() {
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
        /*saga
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
        });*/
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
        layoutParams.width = dm.widthPixels * 3 / 4;
        layoutParams.height = dm.heightPixels * 3 / 4;
        layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
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
}
