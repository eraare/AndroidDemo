package com.guohua.mlight.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-06
 * 自定义的照相机视图
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private Camera mCamera; /*摄像头*/
    private SurfaceHolder mHolder; /*生命周期控制*/

    private int mViewWidth; /*视图宽度*/
    private int mViewHeight; /*视图高度*/

    private int mCurrentCameraId; /*当前照相机位置0位前置1位后置*/

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial();/*初始化数据*/
    }

    /*初始化数据*/
    private void initial() {
        setKeepScreenOn(true);
        mHolder = getHolder();
        mHolder.addCallback(this);
        /*3.0前需要*/
       /*mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);*/
        mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        /*打开照相机*/
        openCamera();
    }

    /**
     * 打开照相机并设置参数
     */
    private void openCamera() {
        mCamera = openCamera(mCurrentCameraId);
        if (null != mCamera) {
            setupCameraParameters();
        }
    }

    /**
     * 打开照相机的具体函数
     *
     * @param cameraId
     * @return
     */
    private Camera openCamera(int cameraId) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraId) {
                return Camera.open(i);
            }
        }
        return null;
    }

    /**
     * 参数配置
     */
    private void setupCameraParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG); /*图片类型*/
        parameters.setJpegQuality(100); /*图片质量*/
//        setupPictureSize(parameters); /*设置图片尺寸*/
//        setupPreviewSize(parameters); /*设置预览尺寸*/
        setupFocusMode(parameters); /*设置对焦模式*/
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90); /*显示方向*/
//        mCamera.autoFocus(mAutoFocusCallback);
        mCamera.setPreviewCallback(mPreviewCallback);
        /*显示到SurfaceView上*/
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            releaseCamera();
            e.printStackTrace();
        }
    }

    /**
     * 图片尺寸设置
     *
     * @param parameters
     */
    private void setupPictureSize(Camera.Parameters parameters) {
        int maxViewSize = Math.max(mViewWidth, mViewHeight);
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size pictureSize = getProperSize(pictureSizes, maxViewSize);
        if (pictureSize == null) {
            pictureSize = parameters.getPictureSize();
        }
        int pictureHeight = pictureSize.height;
        int pictureWidth = pictureSize.width;
        parameters.setPictureSize(pictureWidth, pictureHeight);
        int layoutWidth = mViewHeight * pictureHeight / pictureWidth;
        int layoutHeight = mViewWidth * pictureWidth / pictureHeight;
        FrameLayout.LayoutParams params;
        if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            params = new FrameLayout.LayoutParams(layoutWidth, mViewHeight);
        } else {
            params = new FrameLayout.LayoutParams(mViewWidth, layoutHeight);
        }
        setLayoutParams(params);
    }

    /**
     * 图片预览尺寸
     *
     * @param parameters
     */
    private void setupPreviewSize(Camera.Parameters parameters) {
        int maxViewSize = Math.max(mViewWidth, mViewHeight);
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = getProperSize(previewSizes, maxViewSize);
        if (previewSize == null) {
            previewSize = parameters.getPreviewSize();
        }
        int previewHeight = previewSize.height;
        int previewWidth = previewSize.width;
        parameters.setPreviewSize(previewWidth, previewHeight);
    }

    /**
     * 获取最适合的尺寸
     *
     * @param sizes
     * @param maxViewSize
     * @return
     */
    private Camera.Size getProperSize(List<Camera.Size> sizes, int maxViewSize) {
        int size = sizes.size();
        Camera.Size cameraSize;
        int maxCameraSize;
        for (int i = 0; i < size; i++) {
            cameraSize = sizes.get(i);
            maxCameraSize = Math.max(cameraSize.width, cameraSize.height);
            if (maxViewSize <= maxCameraSize) {
                return cameraSize;
            }
        }
        return null;
    }

    /**
     * 设置对焦模式
     *
     * @param parameters
     */
    private void setupFocusMode(Camera.Parameters parameters) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.cancelAutoFocus();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    /*释放摄像头*/
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 自动对焦回调函数
     */
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                System.out.println("auto focus");
            }
        }
    };

    /**
     * 预览时的视频帧
     */
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            System.out.println("onPreviewFrame");
        }
    };

    /**
     * 拍照在这里
     *
     * @param shutter
     * @param raw
     * @param jpeg
     */
    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
//        setupCameraParameters();
        mCamera.takePicture(shutter, raw, jpeg);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        releaseCamera(); /*首先释放相机*/
        /*切换要显示的摄像头位置*/
        if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        /*开启摄像头并显示预览到SurfaceView*/
        mCamera = openCamera(mCurrentCameraId);

        if (mCamera == null) return;
        setupCameraParameters();
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    /**
     * 切换闪光灯模式
     *
     * @return
     */
    public String switchFlashMode() {
        if (mCamera == null || mCamera.getParameters() == null
                || mCamera.getParameters().getSupportedFlashModes() == null
                || mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return null;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        String flashMode = mCamera.getParameters().getFlashMode();
        List<String> supportedModes = mCamera.getParameters().getSupportedFlashModes();
        if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {//关闭状态
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(parameters);
            return Camera.Parameters.FLASH_MODE_ON;
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {//开启状态
            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
                return Camera.Parameters.FLASH_MODE_AUTO;
            } else if (supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                return Camera.Parameters.FLASH_MODE_OFF;
            }
        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            return Camera.Parameters.FLASH_MODE_OFF;
        }
        return null;
    }

    /**
     * 修正图片的旋转角度
     *
     * @param source
     * @return
     */
    public Bitmap correctPicture(Bitmap source) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCurrentCameraId, cameraInfo);
        int degree = cameraInfo.orientation - 360;
        Matrix m = new Matrix();
        m.preRotate(degree);
        int width = source.getWidth();
        int height = source.getHeight();
        return Bitmap.createBitmap(source, 0, 0, width, height, m, false);
    }
}
