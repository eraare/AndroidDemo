package com.guohua.mlight.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.common.util.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TesterActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv_red_tester)
    TextView mTvRedTester;
    @BindView(R.id.sb_red_tester)
    SeekBar mSbRedTester;
    @BindView(R.id.tv_green_tester)
    TextView mTvGreenTester;
    @BindView(R.id.sb_green_tester)
    SeekBar mSbGreenTester;
    @BindView(R.id.tv_blue_tester)
    TextView mTvBlueTester;
    @BindView(R.id.sb_blue_tester)
    SeekBar mSbBlueTester;
    @BindView(R.id.btn_open_tester)
    Button mBtnOpenTester;
    @BindView(R.id.btn_close_tester)
    Button mBtnCloseTester;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        mUnbinder = ButterKnife.bind(this);

        mSbRedTester.setOnSeekBarChangeListener(this);
        mSbGreenTester.setOnSeekBarChangeListener(this);
        mSbBlueTester.setOnSeekBarChangeListener(this);
        mSbRedTester.setProgress(128);
        mSbGreenTester.setProgress(128);
        mSbBlueTester.setProgress(128);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        switch (id) {
            case R.id.sb_red_tester: {
                if (progress <= 0) {
                    mTvRedTester.setText("红光已关闭");
                } else {
                    float percent = progress / 255f;
                    mTvRedTester.setText("红光 " + percent * 100 + "%");
                }
            }
            break;
            case R.id.sb_green_tester: {
                if (progress <= 0) {
                    mTvRedTester.setText("绿光已关闭");
                } else {
                    float percent = progress / 255f;
                    mTvRedTester.setText("绿光 " + percent * 100 + "%");
                }
            }
            break;
            case R.id.sb_blue_tester: {
                if (progress <= 0) {
                    mTvRedTester.setText("蓝光已关闭");
                } else {
                    float percent = progress / 255f;
                    mTvRedTester.setText("蓝光 " + percent * 100 + "%");
                }
            }
            break;
            default:
                break;
        }
        changeColor();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        int progress = seekBar.getProgress();
        switch (id) {
            case R.id.sb_red_tester: {
                if (progress <= 0) {
                    mTvRedTester.setText("红光已关闭");
                } else {
                    float percent = progress / 255f;
                    mTvRedTester.setText("红光 " + percent * 100 + "%");
                }
            }
            break;
            case R.id.sb_green_tester: {
                if (progress <= 0) {
                    mTvRedTester.setText("绿光已关闭");
                } else {
                    float percent = progress / 255f;
                    mTvRedTester.setText("绿光 " + percent * 100 + "%");
                }
            }
            break;
            case R.id.sb_blue_tester: {
                if (progress <= 0) {
                    mTvRedTester.setText("蓝光已关闭");
                } else {
                    float percent = progress / 255f;
                    mTvRedTester.setText("蓝光 " + percent * 100 + "%");
                }
            }
            break;
            default:
                break;
        }
        changeColor();
    }

    private void changeColor() {
        int alpha = 0;
        int red = mSbRedTester.getProgress();
        int green = mSbGreenTester.getProgress();
        int blue = mSbBlueTester.getProgress();
        int color = Color.argb(alpha, red, green, blue);
        String data = CodeUtils.transARGB2Protocol(color);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
    }

    @OnClick({R.id.btn_open_tester, R.id.btn_close_tester})
    public void onClick(View view) {
        String data = null;
        switch (view.getId()) {
            case R.id.btn_open_tester: {
                data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_OPEN});
            }
            break;
            case R.id.btn_close_tester: {
                data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE});
            }
            break;
            default:
                break;
        }
        ThreadPool.getInstance().addTask(new SendRunnable(data));
    }
}
