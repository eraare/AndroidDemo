package com.guohua.mlight.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.DatetimeBean;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.view.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.guohua.mlight.view.fragment.HomeFragment.currentOpenTime;

/**
 * @author Leo
 *         #time 2016-09-02
 *         #detail 定时开灯的对话框
 */
public class TimerFragment extends android.support.v4.app.DialogFragment {
    public static final String TAG = TimerFragment.class.getSimpleName();
    /**
     * 音例模式
     */
    private volatile static TimerFragment timerFragment = null;

    public static TimerFragment getInstance() {
        if (timerFragment == null) {
            synchronized (TimerFragment.class) {
                if (timerFragment == null) {
                    timerFragment = new TimerFragment();
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
        rootView = inflater.inflate(R.layout.dialog_timer, container, false);
        init();
//        getDialog().setCanceledOnTouchOutside(false);
        return rootView;
    }

    private MainActivity mContext;//上下文
    private View rootView;//根视图
    private TextView mTitle;//标题
    private TextView mConfirm;//确定
    private TextView mCancel;//取消
    private DatePicker mDatePicker;//日期选择器
    private TimePicker mTimerPicker;//时间选择器
    private DatetimeBean mDatetime;//选择的时间

    private void init() {
        mContext = (MainActivity) getActivity();
        initValues();
        findViewsByIds();
        showDatetime();
    }

    private void initValues() {
        mDatetime = new DatetimeBean();
    }

    private void findViewsByIds() {
        mTitle = (TextView) rootView.findViewById(R.id.tv_title_timer);
        mConfirm = (TextView) rootView.findViewById(R.id.tv_confirm_timer);
        mCancel = (TextView) rootView.findViewById(R.id.tv_cancel_timer);
        mDatePicker = (DatePicker) rootView.findViewById(R.id.dp_date_timer);
        mTimerPicker = (TimePicker) rootView.findViewById(R.id.tp_time_timer);

        mCancel.setOnClickListener(mOnClickListener);
        mConfirm.setOnClickListener(mOnClickListener);
        mTimerPicker.setOnTimeChangedListener(mOnTimeChangedListener);
        mDatePicker.init(mDatetime.year, mDatetime.month, mDatetime.day, mOnDateChangedListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tv_confirm_timer: {
                    if (mDatetime.getTimeInMillis() != 0) {//当天24:00之前的定时
                        long curTime = System.currentTimeMillis();
                        long mdateTime = mDatetime.getTimeInMillis();

                        if(mdateTime < 0){//第二天当前时间点之前的定时
                            System.out.println(String.format("%d", curTime) + "  -------11111---------turn on:" + mdateTime);
                            mdateTime = mDatetime.getNextDayTimeInMillis();
                        }

                        System.out.println(String.format("%d", curTime) + "  --------22222--------turn on:" + mdateTime);

                        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_OPEN, new Object[]{mdateTime / 1000});
//                        ThreadPool.getInstance().addOtherTask(new SendRunnable(data));

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sp.edit();
                        currentOpenTime = (int) mdateTime /(60*1000);
                        long tmp = currentOpenTime*60*1000;
                        long resTimer = curTime + tmp;
                        System.out.println(String.format("%d", curTime) + " + " + tmp + " = resTimer is:     " +
                                String.format("%d", resTimer));

                        editor.putLong(Constants.KEY_TIMER_OPEN, resTimer).apply();
                        editor.putBoolean(Constants.EXIST_TIMER_OPEN, true).apply();

                        Intent intent = new Intent(Constants.ACTION_OPENLIGHT_TIMER);
                        intent.putExtra(Constants.ACTION_OPENLIGHT_TIMER, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(resTimer)));
                        mContext.sendBroadcast(intent);

                        System.out.println(String.format("%d", curTime) + "   " + currentOpenTime + "  timerfragment icon_light currentOpenTime " +
                                String.format("%d", resTimer) + ";  " + String.format("%d", sp.getLong(Constants.KEY_TIMER_OPEN, 0)));
                        dismiss();
                    } else{
                        showToast(getString(R.string.timeearly_thannow_warning));
                    }
                }
                break;
                case R.id.tv_cancel_timer: {
                    dismiss();
                }
                break;
                default:
                    break;
            }
        }
    };

    /**
     * 日期选择监听器
     */
    private DatePicker.OnDateChangedListener mOnDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mDatetime.year = year;
            mDatetime.month = monthOfYear;
            mDatetime.day = dayOfMonth;
            showDatetime();
        }
    };

    /**
     * 时间选择监听器
     */
    private TimePicker.OnTimeChangedListener mOnTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            mDatetime.hour = hourOfDay;
            mDatetime.minute = minute;
            showDatetime();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = dm.widthPixels;
        layoutParams.height = dm.heightPixels / 2;
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

    private void showDatetime() {
        if(mDatetime.getTimeInMillis() < 0){//选择的时间小于当前时间，跳至第二天
            mDatetime.tmpDay = mDatetime.day + 1;
        }else if(mDatetime.day < mDatetime.tmpDay){//同一天内，此时刻之后的时间
            mDatetime.tmpDay = mDatetime.day;
        }
        mTitle.setText(mDatetime.toString());
    }
}
