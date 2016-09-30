package com.guohua.mlight.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.activity.PalletActivity;
import com.guohua.mlight.activity.SceneModeActivity;
import com.guohua.mlight.adapter.SceneAdapter;
import com.guohua.mlight.bean.SceneBean;
import com.guohua.mlight.bean.SceneListInfo;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.service.GradientRampService;
import com.guohua.mlight.service.SceneSunGradientRampService;
import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.SceneModeSaveDiyGradientRamp;
import com.guohua.mlight.util.ToastUtill;
import com.guohua.mlight.view.RecyclerViewDivider;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景模式视图
 */
public class SceneFragment extends Fragment {
    public static final String TAG = SceneFragment.class.getSimpleName();
    public static String TITLE = "";
    // 单例模式获取此Fragment
    private static SceneFragment sceneFragment = null;

    /**
     * 单例模式保证自始至终只有一个实例
     *
     * @return
     */
    public static SceneFragment newInstance() {
        if (sceneFragment == null) {
            synchronized (SceneFragment.class) {
                if (sceneFragment == null) {
                    sceneFragment = new SceneFragment();
                }
            }
        }
        return sceneFragment;
    }

    public SceneFragment() {
        // Required empty public constructor
    }

    private MainActivity mContext;
    private View rootView;// 布局根视图
    private RecyclerView mSceneView;//情景模式
    public static SceneAdapter mSceneAdapter;//情景适配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_scene, container, false);
        init();//一些初始化
        return rootView;
    }

    private void init() {
        mContext = (MainActivity) getActivity();
        TITLE = getString(R.string.scene_mode);
        findViewsByIds();
        initScenes();
    }

    private void initScenes() {
        mSceneAdapter = new SceneAdapter(mContext);
        mSceneAdapter.addScene(new SceneBean(0, getString(R.string.colorpallet), getString(R.string.colorpallet_description), R.drawable.scene_colorful));
        mSceneAdapter.addScene(new SceneBean(1, getString(R.string.warmnight), getString(R.string.warmnight_description), R.drawable.scene_moon));
        mSceneAdapter.addScene(new SceneBean(2, getString(R.string.sunmode), getString(R.string.sunmode_description), R.drawable.scene_sun));
        mSceneAdapter.addScene(new SceneBean(3, getString(R.string.defaultmode), getString(R.string.defaultmode_description), R.drawable.tencolor));
        mSceneAdapter.addScene(new SceneBean(4, getString(R.string.rgbwave), getString(R.string.rgbwave_description), R.drawable.scene_rgbwave));
        mSceneAdapter.addScene(new SceneBean(5, getString(R.string.diyshine), getString(R.string.diyshine_description), R.drawable.scene_gradient));
/*
        mSceneAdapter.addScene(new SceneBean(0, "“七彩调色盘”", "随心所欲，调制您的颜料，漾出一片色空", R.drawable.scene_colorful));
        mSceneAdapter.addScene(new SceneBean(1, "“温馨小夜灯”", "一键调出温馨色调，暖暖的进入梦乡", R.drawable.scene_moon));
        mSceneAdapter.addScene(new SceneBean(2, "“太阳升又落”", "模拟太阳东升西落的光线变化", R.drawable.scene_sun));
//        mSceneAdapter.addScene(new SceneBean("“月圆月缺”", "wax and wane", R.drawable.scene_moon));
        mSceneAdapter.addScene(new SceneBean(3, "“十面埋伏”", "给你的生活绣上十面百色花", R.drawable.tencolor));
        mSceneAdapter.addScene(new SceneBean(4, "“三色渐渐变”", "红绿蓝三原色同时渐变", R.drawable.scene_rgbwave));
        mSceneAdapter.addScene(new SceneBean(5, "“炫彩渐渐变”", "赤橙黄绿青蓝紫，谁持彩练当空舞", R.drawable.scene_gradient));
*/


//        mSceneAdapter.addScene(new SceneBean("“浪漫模式”", "color 漫漫长路，柔情似水", R.drawable.scene_romantic));
//        mSceneAdapter.addScene(new SceneBean("“阅读模式”", "为您快乐阅读保驾护航", R.drawable.scene_read));
        mSceneView.setAdapter(mSceneAdapter);
        mSceneAdapter.setOnItemClickListener(new SceneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Object tag) {
                int id = (int) tag;
                switch (id) {
                    case 0: {
                        mSceneAdapter.changeState(null);
                        Intent intent = new Intent(mContext, PalletActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 1: {
                        if (mSceneAdapter.changeState(tag)) {
                            SceneFragment.mSceneAdapter.setState(true, 1);
                            // 暖黄色
                            String data = CodeUtils.transARGB2Protocol(Constant.COLORMOONMODE);

                            //多发几次，保证发送成功
                            for(int i = 0; i < 10; i++){
                                ThreadPool.getInstance().addTask(new SendRunnable(data));
                            }

                        } else {
                            SceneFragment.mSceneAdapter.setState(false, 1);
                        }
                    }
                    break;
                    case 2: {
                        if (mSceneAdapter.changeState(tag)) {
                            SceneSunGradientRampService.isRunning = true;
                            Intent intent = new Intent(mContext, SceneSunGradientRampService.class);
                            mContext.startService(intent);
                        } else {
                            SceneSunGradientRampService.isRunning = false;
                            Intent intent = new Intent(mContext, SceneSunGradientRampService.class);
                            mContext.stopService(intent);
                        }
                    }
                    break;
                    case 3: {
                        if (mSceneAdapter.changeState(tag)) {
                            SceneModeSaveDiyGradientRamp.start(0);
                        } else {
                            SceneModeSaveDiyGradientRamp.destroy();
                        }
                    }
                    break;
                    case 4: {
                        if (mSceneAdapter.changeState(tag)) {
                            //如果开启了Diy模式，还需先关闭Diy
                            if(SceneModeActivity.isSceneDiyModeOn){
                                Intent service = new Intent(mContext, GradientRampService.class);
                                mContext.stopService(service);
                                SceneModeActivity.isSceneDiyModeOn = false;
                                String musicOff = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
                                ThreadPool.getInstance().addMusicOffTask(new SendRunnable(musicOff));
                            }
                            Intent intent = new Intent(mContext, SceneModeActivity.class);
                            Constant.rgbScene.SceneName = getString(R.string.rgbwave);
                            intent.putExtra("curSceneInfo", Constant.rgbScene);
                            startActivity(intent);
                        } else {
                            Intent service = new Intent(mContext, GradientRampService.class);
                            mContext.stopService(service);
                            SceneModeActivity.isSceneRgbModeOn = false;
                            String musicOff = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
                            ThreadPool.getInstance().addMusicOffTask(new SendRunnable(musicOff));
                        }
                    }
                    break;
                    case 5: {
                        if (mSceneAdapter.changeState(tag)) {
                            //如果开启了RGB模式，还需先关闭RGB WAVE
                            if(SceneModeActivity.isSceneRgbModeOn){
                                Intent service = new Intent(mContext, GradientRampService.class);
                                mContext.stopService(service);
                                SceneModeActivity.isSceneRgbModeOn = false;
                                String musicOff = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
                                ThreadPool.getInstance().addMusicOffTask(new SendRunnable(musicOff));
                            }
                            Intent intent = new Intent(mContext, SceneModeActivity.class);
                            SceneListInfo.SceneInfo ss = new SceneListInfo.SceneInfo();
                            ss.SceneName = getString(R.string.diyshine);
                            intent.putExtra("curSceneInfo", ss);
                            startActivity(intent);
                        } else {
                            Intent service = new Intent(mContext, GradientRampService.class);
                            mContext.stopService(service);
                            SceneModeActivity.isSceneDiyModeOn = false;
                            String musicOff = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
                            ThreadPool.getInstance().addMusicOffTask(new SendRunnable(musicOff));
                        }
                    }
                    break;
                    default:
                        ToastUtill.showToast(mContext, getString(R.string.default_text), Constant.TOASTLENGTH).show();
                        break;
                }
                mSceneAdapter.notifyDataSetChanged();
            }
        });
    }

    private void findViewsByIds() {
        mSceneView = (RecyclerView) rootView.findViewById(R.id.rv_scene_scene);
        mSceneView.setLayoutManager(new LinearLayoutManager(mContext));
        mSceneView.setItemAnimator(new DefaultItemAnimator());
        mSceneView.addItemDecoration(new RecyclerViewDivider(mContext, OrientationHelper.VERTICAL));
    }
}
