package com.guohua.mlight.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.activity.DriveModeActivity;
import com.guohua.mlight.activity.GradientRampActivity;
import com.guohua.mlight.service.ShakeService;
import com.guohua.mlight.service.VisualizerService;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.ToolUtils;

/**
 * @author Leo
 * @time 2016-01-08
 * @detail 情景模式 音乐律动和摇一摇功能
 */
public class Scene1Fragment extends Fragment {
    /**
     * 音例模式
     */
    private volatile static Scene1Fragment sceneFragment = null;

    public static Scene1Fragment getInstance() {
        if (sceneFragment == null) {
            synchronized (Scene1Fragment.class) {
                if (sceneFragment == null) {
                    sceneFragment = new Scene1Fragment();
                }
            }
        }
        return sceneFragment;
    }

    private MainActivity mContext = null;//获取所Attach的Activity
    private View rootView = null;//根布局
    /*
        private LinearLayout music = null, shake = null, drive;
        private ImageView musicIcon = null, shakeIcon = null;
        private TextView musicTitle = null, shakeTitle = null, driveTitle;
    */

    private TextView musicTitle = null, driveTitle;
    private LinearLayout music = null,  drive, pallet, gradient;
    private ImageView musicIcon = null;

    private boolean isVisualizerOn = false;//音乐律动服务是否已开启
    private boolean isShakeOn = false;//摇一摇服务是否已开启

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_scene1, container, false);
        init();//初始化控件及数据
        return rootView;
    }

    /**
     * 初始化所有数据
     */
    private void init() {
        mContext = (MainActivity) getActivity();
        isVisualizerOn = ToolUtils.isServiceRunning(getContext(), VisualizerService.class.getName());
        isShakeOn = ToolUtils.isServiceRunning(getContext(), ShakeService.class.getName());
        findViewsByIds();
    }

    /**
     * 得到所有的控件并绑定监听器
     */
    private void findViewsByIds() {
        music = (LinearLayout) rootView.findViewById(R.id.ll_music_scene);


        pallet = (LinearLayout) rootView.findViewById(R.id.ll_pallet_scene);
        gradient = (LinearLayout) rootView.findViewById(R.id.ll_gradient_scene);

//        shake = (LinearLayout) rootView.findViewById(R.id.ll_shake_scene);
        drive = (LinearLayout) rootView.findViewById(R.id.ll_drive_scene);


        musicIcon = (ImageView) rootView.findViewById(R.id.iv_music_scene);

//        shakeIcon = (ImageView) rootView.findViewById(R.id.iv_shake_scene);

        musicTitle = (TextView) rootView.findViewById(R.id.tv_music_scene);

//        shakeTitle = (TextView) rootView.findViewById(R.id.tv_shake_scene);
        driveTitle = (TextView) rootView.findViewById(R.id.tv_drive_scene);

        music.setOnClickListener(mOnClickListener);

        pallet.setOnClickListener(mOnClickListener);
        gradient.setOnClickListener(mOnClickListener);

//        shake.setOnClickListener(mOnClickListener);
        drive.setOnClickListener(mOnClickListener);

        changeUiState();
    }

    /**
     * 点击音乐律动或者摇一摇时背景的变化效果
     */
    private void changeUiState() {
        if (isVisualizerOn) {
            musicIcon.setImageResource(R.drawable.icon_music_selected);
            music.setBackgroundColor(getResources().getColor(R.color.greyb));
            musicTitle.setTextColor(getResources().getColor(R.color.main));
        } else {
            musicIcon.setImageResource(R.drawable.icon_music_normal);
            music.setBackgroundColor(getResources().getColor(R.color.greye));
            musicTitle.setTextColor(getResources().getColor(R.color.greyd));
        }

        /*if (isShakeOn) {
            shakeIcon.setImageResource(R.drawable.icon_shake_selected);
            shake.setBackgroundColor(getResources().getColor(R.color.greyb));
            shakeTitle.setTextColor(getResources().getColor(R.color.main));
        } else {
            shakeIcon.setImageResource(R.drawable.icon_shake_normal);
            shake.setBackgroundColor(getResources().getColor(R.color.greye));
            shakeTitle.setTextColor(getResources().getColor(R.color.greyd));
        }*/
    }

    // 点击时明暗切换效果
    private Animation Anim_Alpha;

    /**
     * 单击事件处理器
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Anim_Alpha = AnimationUtils.loadAnimation(mContext,
                    R.anim.alpha_action);
            v.startAnimation(Anim_Alpha);
            switch (id) {
                case R.id.ll_music_scene: {

                    ToolUtils.requestPermissions(mContext, Manifest.permission.RECORD_AUDIO, Constant.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    ToolUtils.requestPermissions(mContext, Manifest.permission.MODIFY_AUDIO_SETTINGS, Constant.MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS);

                    if (isVisualizerOn) {
                        stopVisualizerService();
                        isVisualizerOn = false;
                    } else if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(mContext, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED){
                        startVisualizerService();
                        isVisualizerOn = true;
                        isShakeOn = false;
                    }else{
                        Toast.makeText(mContext, R.string.prompt_recordvideo_permission, Toast.LENGTH_LONG).show();
                    }
                }
                break;
                /*case R.id.ll_shake_scene: {
                    ToolUtils.requestPermissions(mContext, Manifest.permission.VIBRATE, Constant.MY_PERMISSIONS_REQUEST_VIBRATE);
                    if (isShakeOn) {
                        stopShakeService();
                        isShakeOn = false;
                    } else {
                        startShakeService();
                        isShakeOn = true;
                        isVisualizerOn = false;
                    }
                }
                break;*/

                case R.id.ll_pallet_scene: {
                    Intent intent = new Intent(mContext, DriveModeActivity.class);
                    startActivity(intent);
                }
                break;

                case R.id.ll_gradient_scene: {
                    Intent intent = new Intent(mContext, GradientRampActivity.class);
                    startActivity(intent);
                }
                break;

                case R.id.ll_drive_scene: {
                    Intent intent = new Intent(mContext, DriveModeActivity.class);
                    startActivity(intent);
                }
                break;
                default:
                    break;
            }
            changeUiState();
        }
    };

    private void startShakeService() {
        Intent service = new Intent(getContext(), ShakeService.class);
        getContext().startService(service);
    }

    private void stopShakeService() {
        Intent service = new Intent(getContext(), ShakeService.class);
        getContext().stopService(service);
    }

    private void startVisualizerService() {
        //stopVisualizerService();
        Intent service = new Intent(getContext(),
                VisualizerService.class);
        getContext().startService(service);
        startMusicPlayer();
    }

    private void startMusicPlayer() {
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/mp3");
        startActivity(intent);
        Toast.makeText(mContext, R.string.scene_music_error, Toast.LENGTH_LONG).show();
    }

    private void stopVisualizerService() {
        Intent service = new Intent(getContext(),
                VisualizerService.class);
        mContext.stopService(service);
    }
}
