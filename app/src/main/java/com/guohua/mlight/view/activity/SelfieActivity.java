package com.guohua.mlight.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.view.View;
import android.widget.ImageView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.util.CameraUtils;
import com.guohua.mlight.lwble.MessageEvent;
import com.guohua.mlight.view.widget.CameraView;

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

    @OnClick({R.id.iv_flash_selfie, R.id.iv_set_selfie, R.id.civ_album_selfie, R.id.civ_camera_selfie, R.id.civ_switch_selfie})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_flash_selfie: {
                String mode = mPreviewView.switchFlashMode();
                switch (mode) {
                    case Camera.Parameters.FLASH_MODE_ON:
                        mFlashView.setImageResource(R.drawable.icon_flash_on);
                        break;
                    case Camera.Parameters.FLASH_MODE_AUTO:
                        mFlashView.setImageResource(R.drawable.icon_flash_auto);
                        break;
                    case Camera.Parameters.FLASH_MODE_OFF:
                        mFlashView.setImageResource(R.drawable.icon_flash_off);
                        break;
                    default:
                        mFlashView.setImageResource(R.drawable.icon_flash_off);
                        break;
                }
            }
            break;
            case R.id.iv_set_selfie: {
//                startService(new Intent(this, TestService.class));
                finish();
            }
            break;
            case R.id.civ_album_selfie: {
                CameraUtils.viewPictureByPath(SelfieActivity.this, currentPicturePath);
            }
            break;
            case R.id.civ_switch_selfie: {
                mPreviewView.switchCamera();
            }
            break;
            case R.id.civ_camera_selfie: {
                takePicture();
            }
            break;
            default:
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePicture() {
        mPreviewView.takePicture(null, null, mPictureCallback);
    }

    /**
     * 拍照后的数据回调接口
     */
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap source = BitmapFactory.decodeByteArray(data, 0, data.length);
            source = mPreviewView.correctPicture(source);
            mAlbumView.setImageBitmap(source);
            currentPicturePath = CameraUtils.saveBitmap2Album(getApplicationContext(), source);
            source.recycle();
            camera.stopPreview();
            camera.startPreview();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
//        mPreviewView.releaseCamera();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPreviewView.switchCamera();
        EventBus.getDefault().register(this);
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
}
