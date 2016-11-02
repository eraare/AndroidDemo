package com.guohua.mlight.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.adapter.OptionsAdapter;
import com.guohua.mlight.bean.Option;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.ToolUtils;

/**
 * @author Leo
 * @time 2016-01-08
 * @detail 设置页面
 */
public class SettingsActivity extends AppCompatActivity {
    private ListView optionsListView = null;
    private OptionsAdapter mOptionsAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        findViewsByIds();
    }

    private void findViewsByIds() {
        optionsListView = (ListView) findViewById(R.id.lv_options_settings);
        mOptionsAdapter = new OptionsAdapter(this);
        //mOptionsAdapter.addOption(new Option(R.drawable.icon_password, getString(R.string.settings_password)));
        mOptionsAdapter.addOption(new Option(R.drawable.icon_rename, getString(R.string.settings_name)));
        mOptionsAdapter.addOption(new Option(R.drawable.icon_color, getString(R.string.settings_color)));
        mOptionsAdapter.addOption(new Option(R.drawable.icon_music, getString(R.string.settings_music)));
        mOptionsAdapter.addOption(new Option(R.drawable.icon_shake, getString(R.string.settings_shake)));
        //mOptionsAdapter.addOption(new Option(R.drawable.icon_camera, getString(R.string.settings_camera)));
        mOptionsAdapter.addOption(new Option(R.drawable.icon_about_app, getString(R.string.personal_about_app)));
        //中性版本
        //mOptionsAdapter.addOption(new Option(R.drawable.icon_about_us, getString(R.string.personal_about_us)));

        optionsListView.setAdapter(mOptionsAdapter);
        optionsListView.setOnItemClickListener(onItemClickListener);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                /*case 0:
                    changePassword();
                    break;*/
                case 0:
                    changeAccount();
                    break;
                case 1:
                    currentColor();
                    break;
                case 2: {
                    ToolUtils.requestPermissions(SettingsActivity.this, Manifest.permission.RECORD_AUDIO, Constant.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    ToolUtils.requestPermissions(SettingsActivity.this, Manifest.permission.MODIFY_AUDIO_SETTINGS, Constant.MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS);

                    if(ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent(SettingsActivity.this, VisualizerActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SettingsActivity.this, R.string.prompt_recordvideo_permission, Toast.LENGTH_LONG).show();
                    }
                }
                break;
                case 3: {
                    Intent intent = new Intent(SettingsActivity.this, ShakeActivity.class);
                    startActivity(intent);
                }
                break;
                case 4:/* {
                    Intent intent = new Intent(SettingsActivity.this, SelfieActivity.class);
                    startActivity(intent);
                }
                break;
                case 5:*/ {
                    Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                break;

                case 5: {
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
     * 更改当前密码
     */
    private void changePassword() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_password_settings, null);
        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle(R.string.settings_password).setView(view)
                .setPositiveButton(R.string.settings_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText newPassword = (EditText) view.findViewById(R.id.et_new_password);
                        String newString = newPassword.getText().toString().trim();
                        if (newString == null || newString.length() < 4) {
                            Toast.makeText(getApplicationContext(), R.string.settings_password_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_PASSWORD, new String[]{newString});
                        saveThePassword(newString);
                        ThreadPool.getInstance().addTask(new SendRunnable(data));
                        CodeUtils.setPassword(newString);
                        Toast.makeText(getApplicationContext(), R.string.settings_warning, Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.settings_negative, null).show();
    }

    /**
     * 保存密码
     *
     * @param password
     */
    private void saveThePassword(String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String deviceAddress = sp.getString(Constant.KEY_DEVICE_ADDRESS, null);
        sp.edit().putString(deviceAddress, password).apply();
    }

    private void saveTheName(String deviceName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constant.KEY_DEVICE_NAME, deviceName).apply();
    }

    /**
     * 更改灯名
     */
    private void changeAccount() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_account_settings, null);
        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle(R.string.settings_name).setView(view)
                .setPositiveButton(R.string.settings_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText lightName = (EditText) view.findViewById(R.id.et_name_account);
                        String nameString = lightName.getText().toString().trim();
                        if (nameString == null || nameString.length() <= 0) {
                            Toast.makeText(getApplicationContext(), R.string.settings_name_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_NAME, new String[]{nameString});

                        ThreadPool.getInstance().addTask(new SendRunnable(data));
                        saveTheName(nameString);
                        //Toast.makeText(getApplicationContext(), R.string.settings_warning, Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.settings_negative, null).show();
    }

    /**
     * 设置当前颜色为开机颜色
     */
    private void currentColor() {
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_COLOR, null);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
        Toast.makeText(this, R.string.settings_color_tip, Toast.LENGTH_SHORT).show();
    }

    public void back(View v) {
        this.finish();
    }
}
