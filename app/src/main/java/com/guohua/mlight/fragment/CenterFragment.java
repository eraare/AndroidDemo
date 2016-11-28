package com.guohua.mlight.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.guohua.ios.dialog.ActionSheetDialog;
import com.guohua.ios.dialog.AlertDialog;
import com.guohua.mlight.AppContext;
import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.activity.AboutActivity;
import com.guohua.mlight.activity.SceneModeActivity;
import com.guohua.mlight.activity.ShakeActivity;
import com.guohua.mlight.activity.TemperatureActivity;
import com.guohua.mlight.activity.VisualizerActivity;
import com.guohua.mlight.adapter.OptionAdapter;
import com.guohua.mlight.bean.Device;
import com.guohua.mlight.bean.OptionBean;
import com.guohua.mlight.bean.SceneListInfo;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.ToastUtill;
import com.guohua.mlight.util.ToolUtils;
import com.guohua.mlight.view.RecyclerViewDivider;

import java.util.ArrayList;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景模式视图
 */
public class CenterFragment extends Fragment {
    public static final String TAG = CenterFragment.class.getSimpleName();
    public static String TITLE = "";
    // 单例模式获取此Fragment
    private static CenterFragment sceneFragment = null;

    /**
     * 单例模式保证自始至终只有一个实例
     *
     * @return
     */
    public static CenterFragment newInstance() {
        if (sceneFragment == null) {
            synchronized (CenterFragment.class) {
                if (sceneFragment == null) {
                    sceneFragment = new CenterFragment();
                }
            }
        }
        return sceneFragment;
    }

    public CenterFragment() {
        // Required empty public constructor
    }

    private MainActivity mContext;
    private View rootView;// 布局根视图
    private RecyclerView mOptionsView;
    private OptionAdapter mOptionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_center, container, false);
        init();//一些初始化
        return rootView;
    }

    /**
     * 初始化数据
     */
    private void init() {
        mContext = (MainActivity) getActivity();
        TITLE = getString(R.string.action_settings);
        findViewsByIds();
        initOptions();
    }

    /**
     * 初始化View
     */
    private void findViewsByIds() {
        mOptionsView = (RecyclerView) rootView.findViewById(R.id.rv_options_center);
        // 固定尺寸
        mOptionsView.setHasFixedSize(true);
        // 网格布局 3列
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        mOptionsView.setLayoutManager(gridLayoutManager);
        // 默认动画
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mOptionsView.setItemAnimator(itemAnimator);
        // 分割线
        mOptionsView.addItemDecoration(new RecyclerViewDivider(mContext, OrientationHelper.VERTICAL));
        // 适配器适配数据源
        mOptionAdapter = new OptionAdapter(mContext);
        mOptionsView.setAdapter(mOptionAdapter);
        mOptionAdapter.setOnItemClickListener(mOnItemClickListener);
    }

    /**
     * 选项单击接口
     */
    private OptionAdapter.OnItemClickListener mOnItemClickListener = new OptionAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, Object tag) {
            int id = (int) tag;
            switch (id) {
                case 0:
                    changePassword();
                    break;
                case 1:
                    changeAccount();
                    break;
                case 2:
                    currentColor();
                    break;
                case 3: {
                    ToolUtils.requestPermissions(mContext, Manifest.permission.RECORD_AUDIO, Constant.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    ToolUtils.requestPermissions(mContext, Manifest.permission.MODIFY_AUDIO_SETTINGS, Constant.MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS);

                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(mContext, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(mContext, VisualizerActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, R.string.prompt_recordvideo_permission, Toast.LENGTH_LONG).show();
                    }
                }
                break;
                case 4: {
                    Intent intent = new Intent(mContext, ShakeActivity.class);
                    startActivity(intent);
                }
                break;
                case 5: {
                    Intent intent = new Intent(mContext, TemperatureActivity.class);
                    startActivity(intent);
                }
                break;
                case 6: {
                    //setCallReminderColor();
                    showTelphonyDialog();
                }
                break;
                case 7: {
                    Intent intent = new Intent(mContext, AboutActivity.class);
                    startActivity(intent);
                }
                break;
                case 8: {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.OFFICIAL_WEBSITE));
                    startActivity(intent);
                }
                break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置选项
     */
    private void initOptions() {
        mOptionAdapter.addItem(new OptionBean(0, getString(R.string.settings_password), R.drawable.icon_password_center));
        mOptionAdapter.addItem(new OptionBean(1, getString(R.string.settings_name), R.drawable.icon_rename_center));
        mOptionAdapter.addItem(new OptionBean(2, getString(R.string.settings_color), R.drawable.icon_color_center));
        mOptionAdapter.addItem(new OptionBean(3, getString(R.string.settings_music), R.drawable.icon_music_center));
        mOptionAdapter.addItem(new OptionBean(4, getString(R.string.settings_shake), R.drawable.icon_shake_center));
        mOptionAdapter.addItem(new OptionBean(5, getString(R.string.settings_temperature), R.drawable.icon_temperature_center));
        mOptionAdapter.addItem(new OptionBean(6, getString(R.string.personal_phone_func), R.drawable.icon_phone));
        mOptionAdapter.addItem(new OptionBean(7, getString(R.string.personal_about_app), R.drawable.icon_app_center));
//        mOptionAdapter.addItem(new OptionBean(7, getString(R.string.personal_about_us), R.drawable.icon_about_us));
//        mOptionAdapter.addItem(new OptionBean(8, getString(R.string.personal_feedback), R.drawable.icon_feedback_center));
    }

    /**
     * 更改当前密码
     */
    private void changePassword() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_password_settings, null);
        new android.support.v7.app.AlertDialog.Builder(mContext).setIcon(R.mipmap.ic_launcher).setTitle(R.string.settings_password).setView(view)
                .setPositiveButton(R.string.settings_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText newPassword = (EditText) view.findViewById(R.id.et_new_password);
                        String newString = newPassword.getText().toString().trim();
                        if (newString == null || newString.length() < 4) {
                            Toast.makeText(mContext, R.string.settings_password_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveThePassword(newString);
                        CodeUtils.setPassword(newString);
                        Toast.makeText(mContext, R.string.settings_warning, Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.settings_negative, null).show();
    }

    /**
     * 保存密码
     *
     * @param password
     */
    private void saveThePassword(String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        ArrayList<Device> devices = AppContext.getInstance().devices;
        String deviceAddress;
        for (Device device : devices) {

            deviceAddress = device.getDeviceAddress();

            final String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_PASSWORD, new String[]{sp.getString(deviceAddress, CodeUtils.password), password});
            //多发几次
            final String finalDeviceAddress = deviceAddress;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        ThreadPool.getInstance().addTask(new SendRunnable(finalDeviceAddress, data));
                        try {
                            Thread.sleep(Constant.HANDLERDELAY / 3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            System.out.println(" centerfragment changePassword deviceAddress: " + deviceAddress + "; data:  " + data);
            sp.edit().putString(deviceAddress, password).apply();
        }
    }

    private void saveTheName(String deviceName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constant.KEY_DEVICE_NAME, deviceName).apply();
    }

    /**
     * 更改灯名
     */
    private void changeAccount() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_account_settings, null);
        new android.support.v7.app.AlertDialog.Builder(mContext).setIcon(R.mipmap.ic_launcher).setTitle(R.string.settings_name).setView(view)
                .setPositiveButton(R.string.settings_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText lightName = (EditText) view.findViewById(R.id.et_name_account);
                        String nameString = lightName.getText().toString().trim();
                        if (nameString == null || nameString.length() <= 0) {
                            Toast.makeText(mContext, R.string.settings_name_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_NAME, new String[]{nameString});
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < 5; i++) {
                                    ThreadPool.getInstance().addTask(new SendRunnable(data));
                                    try {
                                        Thread.sleep(Constant.HANDLERDELAY / 3);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();

                        saveTheName(nameString);
                        //Toast.makeText(getApplicationContext(), R.string.settings_warning, Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.settings_negative, null).show();
    }

    /**
     * 设置当前颜色为开机颜色
     */
    private void currentColor() {
        new AlertDialog(mContext).builder()
                .setTitle(getString(R.string.settings_color))
                .setMsg(getString(R.string.settings_color_message))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.settings_positive), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_COLOR, null);
                        ThreadPool.getInstance().addTask(new SendRunnable(data));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < 5; i++) {
                                    ThreadPool.getInstance().addTask(new SendRunnable(data));
                                    try {
                                        Thread.sleep(Constant.HANDLERDELAY / 3);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();

                        Toast.makeText(mContext, R.string.settings_color_tip, Toast.LENGTH_SHORT).show();

//                        String data2 = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SAVE_DIY_START, new Object[]{1});
//                        ThreadPool.getInstance().addTask(new SendRunnable(data2));

                        //需启动底层的预置灯色模式，与上次发数据保持一定时间间隔
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                final String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SAVE_DIY_START, new Object[]{1});
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < 5; i++) {
                                            ThreadPool.getInstance().addTask(new SendRunnable(data));
                                            try {
                                                Thread.sleep(Constant.HANDLERDELAY / 3);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).start();
                            }
                        }, Constant.HANDLERDELAY * 3);
                    }
                })
                .setNegativeButton(getString(R.string.settings_negative), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    private void showTelphonyDialog() {
        new ActionSheetDialog(mContext)
                .builder()
                .setTitle(getString(R.string.choose_call_reminder))
                .addSheetItem(getString(R.string.choose_call_reminder_red), ActionSheetDialog.SheetItemColor.Blue, mOnSheetItemClickListener)
                .addSheetItem(getString(R.string.choose_call_reminder_green), ActionSheetDialog.SheetItemColor.Blue, mOnSheetItemClickListener)
                .addSheetItem(getString(R.string.choose_call_reminder_blue), ActionSheetDialog.SheetItemColor.Blue, mOnSheetItemClickListener)
                .addSheetItem(getString(R.string.choose_call_reminder_diy), ActionSheetDialog.SheetItemColor.Red, mOnSheetItemClickListener)
                .show();
    }

    private ActionSheetDialog.OnSheetItemClickListener mOnSheetItemClickListener = new ActionSheetDialog.OnSheetItemClickListener() {
        @Override
        public void onClick(int which) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            switch (which) {
                case 1:
                    sp.edit().putInt(Constant.CALL_REMINDER_SHINEMODE, 0).apply();
                    ToastUtill.showToast(mContext, getString(R.string.choose_call_reminder_red), Constant.TOASTLENGTH).show();
                    break;
                case 2:
                    sp.edit().putInt(Constant.CALL_REMINDER_SHINEMODE, 1).apply();
                    ToastUtill.showToast(mContext, getString(R.string.choose_call_reminder_green), Constant.TOASTLENGTH).show();
                    break;
                case 3:
                    sp.edit().putInt(Constant.CALL_REMINDER_SHINEMODE, 2).apply();
                    ToastUtill.showToast(mContext, getString(R.string.choose_call_reminder_blue), Constant.TOASTLENGTH).show();
                    break;
                case 4:
                    //自定义模式
                    getDiyCallReminderMOde();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 选择来电提醒的提示闪烁色
     */
    private void setCallReminderColor() {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_call_reminder, null);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(mContext).setIcon(R.mipmap.ic_launcher).setTitle(R.string.choose_call_reminder).setView(view).setNegativeButton(
                R.string.settings_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        ListView funcList = (ListView) view.findViewById(R.id.lv_phone_func);
        String[] funcDesc = {getString(R.string.choose_call_reminder_red), getString(R.string.choose_call_reminder_green),
                getString(R.string.choose_call_reminder_blue), getString(R.string.choose_call_reminder_diy)};
        ArrayAdapter<String> funcAdapter = new ArrayAdapter<String>(mContext, R.layout.dialog_call_reminder_item, R.id.tv_phone_func, funcDesc);
        funcList.setAdapter(funcAdapter);

        dialog.show();
        funcList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sp.edit().putInt(Constant.CALL_REMINDER_SHINEMODE, 0).apply();
                        ToastUtill.showToast(mContext, getString(R.string.choose_call_reminder_red), Constant.TOASTLENGTH).show();
                        break;
                    case 1:
                        sp.edit().putInt(Constant.CALL_REMINDER_SHINEMODE, 1).apply();
                        ToastUtill.showToast(mContext, getString(R.string.choose_call_reminder_green), Constant.TOASTLENGTH).show();
                        break;
                    case 2:
                        sp.edit().putInt(Constant.CALL_REMINDER_SHINEMODE, 2).apply();
                        ToastUtill.showToast(mContext, getString(R.string.choose_call_reminder_blue), Constant.TOASTLENGTH).show();
                        break;
                    case 3:
                        //自定义模式
                        getDiyCallReminderMOde();
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    private void getDiyCallReminderMOde() {
        Intent intent = new Intent(mContext, SceneModeActivity.class);
        SceneListInfo.SceneInfo ss = new SceneListInfo.SceneInfo();

        //获取保存过的自定义来电提醒模式值
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        int[] sendIntDatas;
        String[] s = sp.getString(Constant.CALL_REMINDER_SHINEMODE_VALUE, "124;111;0;0;10;0;0;121").split(";");
        sendIntDatas = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            sendIntDatas[i] = Integer.parseInt(s[i]);
        }
        ss.SceneInfoId = Constant.CALLREMINDERMODEID;
        ss.SceneName = getString(R.string.scene_gradient);
        ss.SceneGradientRampGradientGap = new int[]{0, sendIntDatas[1], sendIntDatas[2], sendIntDatas[3]};
        ss.SceneGradientRampStopGap = new int[]{0, sendIntDatas[4], sendIntDatas[5], sendIntDatas[6]};
        ss.SceneCurClickColorImgOnOff = new int[]{0, ((sendIntDatas[0] - 0x78) & 0x04) >> 2, ((sendIntDatas[0] - 0x78) & 0x02) >> 1, ((sendIntDatas[0] - 0x78) & 0x01)};
        ss.SceneGradientGapRedCBChecked = ss.SceneCurClickColorImgOnOff[1] == 1 ? true : false;
        ss.SceneGradientGapGreenCBChecked = ss.SceneCurClickColorImgOnOff[2] == 1 ? true : false;
        ss.SceneGradientGapBlueCBChecked = ss.SceneCurClickColorImgOnOff[3] == 1 ? true : false;

        intent.putExtra("curSceneInfo", ss);
        startActivity(intent);
    }
}
