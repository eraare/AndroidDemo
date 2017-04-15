package com.guohua.mlight.view.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.lwble.BLEUtils;
import com.guohua.mlight.view.activity.MainActivity;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import butterknife.BindView;

/**
 * @file SplashFragment.java
 * @author Leo
 * @version 1
 * @detail 默认的欢迎界面
 * @since 2016/12/19 16:17
 */

/**
 * 文件名：SplashFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/19 16:17
 * 描  述：默认的欢迎界面
 */
public class SplashFragment extends BaseFragment implements SplashADListener {
    /*单例模式*/
    private volatile static SplashFragment singleton = null;

    public static SplashFragment getInstance() {
        if (singleton == null) {
            synchronized (SplashFragment.class) {
                if (singleton == null) {
                    singleton = new SplashFragment();
                }
            }
        }
        return singleton;
    }

    @BindView(R.id.fl_ad_container_splash)
    FrameLayout mContainerView;
    @BindView(R.id.tv_skip_splash)
    TextView mSkipView;
    @BindView(R.id.iv_holder_splash)
    ImageView mHolderView;

    private SplashAD mSplashAD;
    public boolean canJump = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_splash;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
        fetchSplashAD(mContext, mContainerView, mSkipView, Constants.APP_ID, Constants.SPLASH_POS_ID, this, 0);
    }

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param appId         应用ID
     * @param posId         广告位ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        mSplashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    @Override
    public void onADDismissed() {
        Log.i("SplashFragment", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(int errorCode) {
        Log.i("SplashFragment", "LoadSplashADFail, eCode=" + errorCode);
        /* 如果加载广告失败，则直接跳转 */
        go2Next();
    }

    @Override
    public void onADPresent() {
        Log.i("SplashFragment", "SplashADPresent");
        mHolderView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onADClicked() {
        Log.i("SplashFragment", "SplashADClicked");
    }

    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("SplashFragment", "SplashADTick " + millisUntilFinished + "ms");
        mSkipView.setText(String.format(getString(R.string.fragment_skip_splash), Math.round(millisUntilFinished / 1000F)));
    }

    private void next() {
        if (canJump) {
            go2Next();
        } else {
            canJump = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*广告是否可以跳转*/
        if (canJump) {
            next();
        }
        canJump = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        canJump = false;
    }

    /**
     * 启动主Activity
     */
    private void go2Next() {
        if (BLEUtils.isBluetoothEnabled()) {
            startTheActivity();
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH);
        }
    }

    private void startTheActivity() {
        /*BmobUser currentUser = BmobUser.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
        } else {
            startActivity(new Intent(mContext, MainActivity.class));
        }
        removeFragment();*/
        startActivity(new Intent(mContext, MainActivity.class));
        removeFragment();
    }

    /*请求打开蓝牙*/
    private static final int REQUEST_BLUETOOTH = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                mContext.toast(R.string.fragment_denied_tip_splash);
            }
            startTheActivity();
        }
    }
}
