package com.guohua.mlight.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.util.AntiShake;
import com.guohua.mlight.common.util.CameraUtils;
import com.guohua.mlight.lwble.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelfieActivity extends BaseActivity {
    @BindView(R.id.iv_flash_selfie)
    ImageView mFlashView;
    @BindView(R.id.iv_set_selfie)
    ImageView mSetView;
    @BindView(R.id.cv_preview_selfie)
    CameraView mPreviewView;
    @BindView(R.id.civ_album_selfie)
    CircleImageView mAlbumView;
    @BindView(R.id.civ_camera_selfie)
    CircleImageView mCameraView;
    @BindView(R.id.civ_switch_selfie)
    CircleImageView mSwitchView;

    private String currentPicturePath;
    /*处理快速切换和快速拍照*/
    private AntiShake mAntiShake; /*专业防抖过滤*/
    private boolean canCapture; /*是否可以拍照*/
    private boolean isCameraOpened; /*摄像头似乎已打开*/

    @Override
    protected int getContentViewId() {
        return R.layout.activity_selfie;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        canCapture = true;
        mAntiShake = new AntiShake();/*专业防抖20年*/
        mPreviewView.setJpegQuality(100);
        mPreviewView.setCameraListener(mCameraListener);
        mPreviewView.setFlash(CameraKit.Constants.FLASH_AUTO);
        mPreviewView.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);
    }

    private CameraListener mCameraListener = new CameraListener() {
        @Override
        public void onPictureTaken(byte[] jpeg) {
            super.onPictureTaken(jpeg);
            Bitmap source = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
            mAlbumView.setImageBitmap(source);
            currentPicturePath = CameraUtils.saveBitmap2Album(getApplicationContext(), source);
            source.recycle();
            canCapture = true;
        }

        @Override
        public void onCameraOpened() {
            super.onCameraOpened();
            isCameraOpened = true;
        }

        @Override
        public void onCameraClosed() {
            super.onCameraClosed();
            isCameraOpened = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mPreviewView.start();
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.iv_flash_selfie, R.id.iv_set_selfie, R.id.civ_album_selfie, R.id.civ_camera_selfie, R.id.civ_switch_selfie})
    public void onClick(View view) {
        if (!isCameraOpened) return;
        if (!mAntiShake.check(view.getId(), 1000)) {
            toast("亲，慢一点^_^");
            return;
        }

        switch (view.getId()) {
            case R.id.iv_flash_selfie: {
                /*設置閃光模式*/
                int flash = mPreviewView.toggleFlash();
                switch (flash) {
                    case CameraKit.Constants.FLASH_OFF:
                        mFlashView.setImageResource(R.drawable.icon_flash_on);
                        break;
                    case CameraKit.Constants.FLASH_ON:
                        mFlashView.setImageResource(R.drawable.icon_flash_auto);
                        break;
                    case CameraKit.Constants.FLASH_AUTO:
                        mFlashView.setImageResource(R.drawable.icon_flash_off);
                        break;
                    default:
                        mFlashView.setImageResource(R.drawable.icon_flash_off);
                        break;
                }
            }
            break;
            case R.id.iv_set_selfie: {
                /*退出*/
                finish();
            }
            break;
            case R.id.civ_album_selfie: {
                /*查看相冊*/
                CameraUtils.viewPictureByPath(SelfieActivity.this, currentPicturePath);
            }
            break;
            case R.id.civ_switch_selfie: {
                /*摄像头的前后切换*/
                mPreviewView.toggleFacing();
            }
            break;
            case R.id.civ_camera_selfie: {
                /*拍照拍照*/
                takePicture();
            }
            break;
            default:
                break;
        }
    }

    private void takePicture() {
        if (canCapture) {
            canCapture = false;
            mPreviewView.captureImage();
        }
    }

    /*接收处理由EventBus发送来的消息*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.what == MessageEvent.WHAT_DATA) {
            String data = event.data;
            if (data != null && data.contains("pres")) {
                takePicture();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreviewView.stop();
        EventBus.getDefault().unregister(this);
    }
}
