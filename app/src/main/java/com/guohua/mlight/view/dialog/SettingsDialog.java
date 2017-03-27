package com.guohua.mlight.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.model.bean.Device;

/**
 * @author Leo
 *         #time 2016-09-05
 *         #detail 设置对话框为了简单复用
 */
public class SettingsDialog {
    /**
     * 更改当前密码
     */
    public static void showChangePassword(final Context context, final String deviceAddress) {
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_password_settings, null);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher).setTitle(R.string.settings_password).setView(view)
                .setPositiveButton(R.string.settings_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText newPassword = (EditText) view.findViewById(R.id.et_new_password);
                        String newString = newPassword.getText().toString().trim();
                        if (newString == null || newString.length() < 4) {
                            Toast.makeText(context, R.string.settings_password_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_PASSWORD, new String[]{sp.getString(deviceAddress, CodeUtils.password), newString});
                        saveThePassword(context, deviceAddress, newString);
                        CodeUtils.setPassword(newString);
//                        ThreadPool.getInstance().addTask(new SendRunnable(deviceAddress, data));
                        Toast.makeText(context, R.string.settings_warning, Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.settings_negative, null).show();
    }

    /**
     * 保存密码
     *
     * @param password
     */
    private static void saveThePassword(Context context, String deviceAddress, String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(deviceAddress, password).apply();
    }

    private static void saveTheName(Context context, String deviceName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.KEY_DEVICE_NAME, deviceName).apply();
    }

    /**
     * 更改灯名
     */
    public static void showChangeAccount(final Context context, final int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_account_settings, null);
        new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher).setTitle(R.string.settings_name).setView(view)
                .setPositiveButton(R.string.settings_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText lightName = (EditText) view.findViewById(R.id.et_name_account);
                        String nameString = lightName.getText().toString().trim();
                        if (nameString == null || nameString.length() <= 0) {
                            Toast.makeText(context, R.string.settings_name_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        changeAccount(position, nameString);
                    }
                }).setNegativeButton(R.string.settings_negative, null).show();
    }

    private static void changeAccount(int position, String nameString) {
        Device device = AppContext.getInstance().devices.get(position);
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_NAME, new String[]{nameString});
        if (device != null) {
//            ThreadPool.getInstance().addTask(new SendRunnable(device.getDeviceAddress(), data));
            device.setDeviceName(nameString);
        } else {
//            ThreadPool.getInstance().addTask(new SendRunnable(data));
        }
        Toast.makeText(AppContext.getInstance(), R.string.settings_warning, Toast.LENGTH_LONG).show();
    }

    /**
     * 设置当前颜色为开机颜色
     */
    public static void showCurrentColor(final Context context, final String deviceAddress) {
        new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher).setTitle(R.string.settings_color).setMessage(R.string.settings_color_message)
                .setPositiveButton(R.string.settings_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_COLOR, null);
//                        ThreadPool.getInstance().addTask(new SendRunnable(deviceAddress, data));

                        //需启动底层的预置灯色模式，与上次发数据保持一定时间间隔
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SAVE_DIY_START, new Object[]{1});
//                                ThreadPool.getInstance().addTask(new SendRunnable(data));
                            }
                        }, 200);
                        Toast.makeText(context, R.string.settings_color_tip, Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(R.string.settings_negative, null).show();
    }
}
