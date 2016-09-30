package com.guohua.mlight.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.activity.PalletActivity;
import com.guohua.mlight.activity.SceneModeActivity;
import com.guohua.mlight.adapter.StyleAdapter;
import com.guohua.mlight.bean.SceneListInfo;
import com.guohua.mlight.bean.StyleBean;
import com.guohua.mlight.service.SceneSunGradientRampService;
import com.guohua.mlight.util.Constant;

/**
 * Created by Aladdin on 2016-8-24.
 */
public class SceneListFragment extends Fragment {

    public static final String TAG = PalletFragment.class.getSimpleName();

    /**
     * 音例模式
     */
    private volatile static SceneListFragment sceneListFragment = null;

    public static SceneListFragment getInstance() {
        if (sceneListFragment == null) {
            synchronized (SceneListFragment.class) {
                if (sceneListFragment == null) {
                    sceneListFragment = new SceneListFragment();
                }
            }
        }
        return sceneListFragment;
    }

    private MainActivity mContext = null;//获取所Attach的Activity
    private View rootView = null;//根布局
    private RecyclerView style = null;//列表
    private StyleAdapter mStyleAdapter = null;//适配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_scene_list, container, false);
        init();//初始化控件及数据
        return rootView;
    }

    private void init() {
        mContext = (MainActivity) getActivity();

        mStyleAdapter = new StyleAdapter();
        mStyleAdapter.addStyle(new StyleBean(0, "日出日落", "sunrise and sunset", R.drawable.scene_sun));
//        mStyleAdapter.addStyle(new StyleBean(1, "月圆月缺", "wax and wane", R.drawable.scene_moon));
        mStyleAdapter.addStyle(new StyleBean(1, "红绿蓝波纹", "rgb wave", R.drawable.scene_rgbwave));
        mStyleAdapter.addStyle(new StyleBean(2, "炫彩渐变", "gradient ramp", R.drawable.scene_gradient));
        mStyleAdapter.addStyle(new StyleBean(3, "存储模式测试", "mode diy", R.drawable.scene_moon));
//        mStyleAdapter.addStyle(new StyleBean(3, "七彩色盘", "color pallete", R.drawable.scene_colorful));
        //mStyleAdapter.addStyle(new StyleBean(3, "浪漫模式", "漫漫长路，柔情似水", R.drawable.scene_romantic));
        //mStyleAdapter.addStyle(new StyleBean(4, "阅读模式", "不一样的灯光，不一样的阅读", R.drawable.scene_reading));
        mStyleAdapter.setOnCustomClickListener(onCustomClickListener);

        style = (RecyclerView) rootView.findViewById(R.id.lv_style_scene);
        style.setLayoutManager(new LinearLayoutManager(mContext));
        style.setItemAnimator(new DefaultItemAnimator());
        style.setHasFixedSize(true);
        style.setAdapter(mStyleAdapter);
    }

    private StyleAdapter.OnCustomClickListener onCustomClickListener = new StyleAdapter.OnCustomClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            switch (postion) {
                case 0: {

/*
                    Intent intent = new Intent(mContext, SceneModeActivity.class);
                    intent.putExtra("curSceneInfo", Constant.sunScene);
                    startActivity(intent);
*/

                }
                break;
                /*case 1: {
                    Intent intent = new Intent(mContext, SceneModeActivity.class);
                    intent.putExtra("curSceneInfo", Constant.moonScene);
                    startActivity(intent);
                }
                break;*/
                case 1: {
                    Intent intent = new Intent(mContext, SceneModeActivity.class);
                    intent.putExtra("curSceneInfo", Constant.rgbScene);
                    startActivity(intent);
                }
                break;
                case 2: {
                    Intent intent = new Intent(mContext, SceneModeActivity.class);
                    intent.putExtra("curSceneInfo", new SceneListInfo.SceneInfo());
                    startActivity(intent);
                }
                break;
                case 3: {
                    Intent intent = new Intent(mContext, PalletActivity.class);
                    startActivity(intent);
                }
                break;
                /*case 3:
                    Toast.makeText(mContext, "有待开发", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(mContext, "有待开发", Toast.LENGTH_SHORT).show();
                    break;*/
                default:
                    Toast.makeText(mContext, "有待开发", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onButtonClick(View view, int position) {
            int state = mStyleAdapter.getState(position);
            switch (position) {
                case 0: {
                    if (state == StyleBean.STATE_OFF) {
                       // startVisualizerService();

                        mStyleAdapter.changeState(position, StyleBean.STATE_ON);
                        ((ImageButton) view).setImageResource(R.drawable.pause);

                        Intent intent = new Intent(mContext, SceneSunGradientRampService.class);
                        mContext.startService(intent);
                    } else {
                        // stopVisualizerService();

                        mStyleAdapter.changeState(position, StyleBean.STATE_OFF);
                        ((ImageButton) view).setImageResource(R.drawable.play);

                        Intent intent = new Intent(mContext, SceneSunGradientRampService.class);
                        mContext.stopService(intent);
                    }
                }
                break;
                case 3: {
                    if (state == StyleBean.STATE_OFF) {
                        // startVisualizerService();

                        mStyleAdapter.changeState(position, StyleBean.STATE_ON);
                        ((ImageButton) view).setImageResource(R.drawable.pause);

//                        Intent intent = new Intent(mContext, SceneModeSaveDiyGradientRampService.class);
//                        mContext.startService(intent);
                    } else {
                        // stopVisualizerService();

                        mStyleAdapter.changeState(position, StyleBean.STATE_OFF);
                        ((ImageButton) view).setImageResource(R.drawable.play);

//                        Intent intent = new Intent(mContext, SceneModeSaveDiyGradientRampService.class);
//                        mContext.stopService(intent);
                    }
                }
                break;
                case 1: {
                    /*Intent service = new Intent(mContext, ShakeService.class);
                    if (state == StyleBean.STATE_OFF) {
                        mContext.startService(service);
                    } else {
                        mContext.stopService(service);
                    }*/
                }
                break;
                default:
//                    Toast.makeText(mContext, "有待开发", Toast.LENGTH_SHORT).show();
                    break;
            }

            /*if (state == StyleBean.STATE_ON) {
                mStyleAdapter.changeState(position, StyleBean.STATE_OFF);
                ((ImageButton) view).setImageResource(R.drawable.play);
            } else {
                mStyleAdapter.changeState(position, StyleBean.STATE_ON);
                ((ImageButton) view).setImageResource(R.drawable.pause);
            }*/
        }
    };


    private void startRunSceneRgbGradientRamp() {

    }

/*    private void startVisualizerService() {
        //stopVisualizerService();
        Intent service = new Intent(getContext(),
                VisualizerService.class);
        getContext().startService(service);
        startMusicPlayer();
    }

    private void startMusicPlayer() {
//        if (FileUtils.copyFileFromAssets2Sdcard(mContext, "welcome.mp3", "welcome.mp3")) {
//            Toast.makeText(mContext, R.string.default_text, Toast.LENGTH_SHORT).show();
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse(*//*FileUtils.getRootPath() + *//*"/sdcard/welcome.mp3");
//        intent.setDataAndType(uri, "audio/mp3");
//        startActivity(intent);

        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/mp3");
        startActivity(intent);
        Toast.makeText(mContext, "error_open_music", Toast.LENGTH_SHORT).show();
    }

    private void stopVisualizerService() {
        Intent service = new Intent(getContext(),
                VisualizerService.class);
        mContext.stopService(service);
    }*/

}
