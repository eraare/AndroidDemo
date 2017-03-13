package com.guohua.mlight.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.util.CameraUtils;
import com.guohua.mlight.communication.BLEConstant;
import com.guohua.mlight.view.widget.CameraSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelfieActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    @BindView(R.id.iv_flash_selfie)
    ImageView mFlashView;
    @BindView(R.id.iv_set_selfie)
    ImageView mSetView;
    @BindView(R.id.csv_preview_selfie)
    CameraSurfaceView mPreviewView;
    @BindView(R.id.civ_album_selfie)
    CircleImageView mAlbumView;
    @BindView(R.id.civ_camera_selfie)
    CircleImageView mCameraView;
    @BindView(R.id.civ_switch_selfie)
    CircleImageView mSwitchView;

    private String currentPicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);
        mUnbinder = ButterKnife.bind(this);
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
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPreviewView.switchCamera();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BLEConstant.ACTION_RECEIVED_SELFIE);
        filter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 接收拍照的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, BLEConstant.ACTION_RECEIVED_SELFIE)) {
                takePicture();
            }
        }
    };
}
