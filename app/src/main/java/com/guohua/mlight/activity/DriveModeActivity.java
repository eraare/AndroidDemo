package com.guohua.mlight.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.AppContext;
import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.service.DriveModeService;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.VibrateUtil;
import com.guohua.mlight.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * @author
 * @detail 摇一摇界面设计实现 可以切换摇一摇模式 开关或者随机变色
 * @time 2015-11-11
 */
public class DriveModeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = DriveModeActivity.class.getSimpleName();
    // 请求码
    public static final int REQUEST_SELECT_LAMP = 1;
    public static final int REQUEST_SELECT_COLOR = 2;
    // 骑行颜色值
    private static final int POSITION_OF_DRIVERED = 0;
    private static final int POSITION_OF_DRIVEGREEN = 1;
    private static final int POSITION_OF_DRIVEBLUE = 2;
    private static final int POSITION_OF_DRIVEMIX = 3;
    private static final int POSITION_OF_DRIVEDIY = 4;

    /*绑定控件*/
    @BindView(R.id.smart_mode_list)
    ListView mSamrtList;
    @BindView(R.id.iv_back_drive)
    ImageView iv_back_drive;
    @BindView(R.id.tv_show_freq_gap)
    TextView valueShow;
    @BindView(R.id.sb_drivemode_freq)
    SeekBar freqGap;

    private boolean isDiveDiyChecked = false;
    SmartAdapter adapter;
    int currentPos;
    SmartItem currentItem;
    SharedPreferences settings;
    SmartObj sObjs[];
    String mSums[];

    int colors[] = {Constant.RED, Constant.GREEN, Constant.BLUE, 1, 2, 3, 4/*-1*/};
    private String data;

    //何种情景模式的广播
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            AppContext.driveModeCheckedPos = what;

            switch (what) {
                case -1:
                    mService.stopBicycling();
                    stopDriveModeService();
                case 0:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_RED_CODE);
                    break;
                case 1:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_GREEN_CODE);
                    break;
                case 2:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_BLUE_CODE);
                    break;
                case 3:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_REDGREEN_CODE);
                    break;
                case 4:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_REDBLUE_CODE);
                    break;
                case 5:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_BLUEGREEN_CODE);
                    break;
                case 6:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_MIX_CODE);
                    break;
                case 7:
                    mService.setCurrentShineColor(Constant.DRIVEMODE_DIY_CODE);
                    break;
                default:
                    break;
            }
        }
    };

    private Unbinder unbinder;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.drive_mode);
        unbinder = ButterKnife.bind(this);
        init();
        setTitle(R.string.drive_title);

        freqGap.setProgress(AppContext.driveModeFreqGap);
        valueShow.setText(getString(R.string.frequency_gap) + AppContext.driveModeFreqGap);
        freqGap.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        sObjs = new SmartObj[]{new SmartObj(getString(R.string.red_warning)), new SmartObj(getString(R.string.green_clear)), // 255,245,221
                new SmartObj(getString(R.string.blue_caution)), new SmartObj(getString(R.string.red_green)), new SmartObj(getString(R.string.red_blue)),
                new SmartObj(getString(R.string.blue_green)), new SmartObj(getString(R.string.red_blue_green))//, new SmartObj("自由组合")
        };

        mSums = new String[]{getString(R.string.red_warning), getString(R.string.green_clear), getString(R.string.blue_caution),
                getString(R.string.red_green), getString(R.string.red_blue), getString(R.string.blue_green), getString(R.string.red_blue_green)//, "自定义颜色组合闪烁"
        };

        for (int i = 0; i < sObjs.length; i++) {
            sObjs[i].mTitleSum = mSums[i];
        }

        adapter = new SmartAdapter(this);
        mSamrtList.setAdapter(adapter);
        adapter.addAll(sObjs);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            changeFreqGap(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void changeFreqGap(int progress) {
        saveValues(progress);
        valueShow.setText(getString(R.string.frequency_gap) + progress);
        mService.changeShineGap(progress);
    }

    private void saveValues(int progress) {
        AppContext.driveModeFreqGap = progress;
    }

    /**
     * 相关的单击事件监听器
     */
    @OnClick(R.id.iv_back_drive)
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_back_drive: {
                Intent intent = new Intent(DriveModeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            default:
                break;
        }
    }

    class SmartObj {
        public String mLabel;
        public String mTitleSum;

        public SmartObj(String label) {
            this.mLabel = label;
        }
    }

    class SmartItem {
        SwitchButton sbtn;
    }

    public class SmartAdapter extends ArrayAdapter<SmartObj> {
        Context mContext;
        SharedPreferences settings;
        LayoutInflater inflater;

        public SmartAdapter(Context context) {
            super(context, 0);
            mContext = context;
            inflater = LayoutInflater.from(mContext);
            settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            SmartItem item;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.drivemode_list_item, null);
                item = new SmartItem();
                item.sbtn = (SwitchButton) convertView
                        .findViewById(R.id.drivemode_listitem);
                convertView.setTag(item);
            } else {
                item = (SmartItem) convertView.getTag();
            }
            SmartObj obj = (SmartObj) getItem(position);
            item.sbtn.setText(obj.mLabel);

            manClick = false;
            item.sbtn.setChecked(driveModeOnOff[position]);
            manClick = true;

            convertView.setTag(item);

            item.sbtn.setOnCheckedChangeListener(DriveModeActivity.this);

            item.sbtn.setTag(position);

            if (currentPos == position) {
                currentItem = item;
            }

            return convertView;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }


    private boolean[] driveModeOnOff = {false, false, false, false, false, false, false};
    private boolean manClick = true;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!manClick)
            return;
        SwitchButton sbtn = (SwitchButton) buttonView;
        int pos = (Integer) sbtn.getTag();
        if (isChecked) {
            if (AppContext.driveModeCheckedPos != -1) {
                driveModeOnOff[AppContext.driveModeCheckedPos] = false;
            } else {
                startDriveModeService();
            }
            driveModeOnOff[pos] = true;
            handler.sendEmptyMessage(pos);
        } else {
            driveModeOnOff[pos] = false;
            handler.sendEmptyMessage(-1);
        }

        VibrateUtil.vibrate(this, 100);
        adapter.notifyDataSetChanged();
    }


    @OnItemClick(R.id.smart_mode_list)
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        SmartItem item = (SmartItem) view.getTag();
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = settings.edit();
        edit.putBoolean("item_" + position, item.sbtn.isChecked());
        edit.commit();

        currentItem = item;
        currentPos = position;
        if (currentPos == POSITION_OF_DRIVEDIY) {
            Intent intent = new Intent();
            intent.setClass(DriveModeActivity.this, SelectColorActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_COLOR);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_SELECT_LAMP == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
            }
        } else if (REQUEST_SELECT_COLOR == requestCode) {
            if (isDiveDiyChecked) {
            }
            Toast.makeText(DriveModeActivity.this, "DriveMode onActivityResult", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private ThreadPool pool = null;//线程池 向蓝牙设备发送控制数据等

    /**
     * 初始化数据和控件
     */
    private void init() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        pool = ThreadPool.getInstance();
        if (AppContext.driveModeCheckedPos != -1) {
            driveModeOnOff[AppContext.driveModeCheckedPos] = true;
        }
        Intent service = new Intent(this, DriveModeService.class);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void startDriveModeService() {
        //stopVisualizerService();
        Intent service = new Intent(this, DriveModeService.class);
        startService(service);
    }

    private void stopDriveModeService() {
        //stopVisualizerService();
        Intent service = new Intent(this, DriveModeService.class);
        stopService(service);
    }

    private DriveModeService.IDriveModeService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (DriveModeService.IDriveModeService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void back(View v) {
        this.finish();
    }
}
