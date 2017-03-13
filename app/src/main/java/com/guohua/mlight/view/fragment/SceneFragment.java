package com.guohua.mlight.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.util.ToastUtill;
import com.guohua.mlight.model.bean.SceneBean;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.view.activity.PalletActivity;
import com.guohua.mlight.view.activity.SelfieActivity;
import com.guohua.mlight.view.activity.ShakeActivity;
import com.guohua.mlight.view.activity.TemperatureActivity;
import com.guohua.mlight.view.activity.VisualizerActivity;
import com.guohua.mlight.view.adapter.SceneAdapter;
import com.guohua.mlight.view.dialog.SettingsDialog;

import butterknife.BindView;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景模式视图
 */
public class SceneFragment extends BaseFragment {
    public static final String TAG = SceneFragment.class.getSimpleName();
    // 单例模式获取此Fragment
    private static SceneFragment sceneFragment = null;

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

    @BindView(R.id.rv_scene_scene)
    RecyclerView mSceneView;//情景模式
    private SceneAdapter mSceneAdapter;//情景适配器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        initSceneView(); /*初始化情景视图*/
        loadScenes(); /*加载各个情景模式*/
    }

    /**
     * 加载情景模式
     */
    private void loadScenes() {
        mSceneAdapter.addScene(new SceneBean(0, getString(R.string.scene_color_pallet), getString(R.string.colorpallet_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(1, getString(R.string.scene_colorful_gradient), getString(R.string.warmnight_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(2, getString(R.string.scene_warm_light), getString(R.string.defaultmode_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(3, getString(R.string.scene_set_password), getString(R.string.defaultmode_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(4, getString(R.string.scene_change_name), getString(R.string.defaultmode_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(5, getString(R.string.scene_preset_color), getString(R.string.defaultmode_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(6, getString(R.string.scene_music_rythm), getString(R.string.defaultmode_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(7, getString(R.string.scene_shake_shake), getString(R.string.defaultmode_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(8, getString(R.string.scene_temperature), getString(R.string.defaultmode_description), R.drawable.icon_music_center));
        mSceneAdapter.addScene(new SceneBean(9, getString(R.string.scene_selfie), getString(R.string.defaultmode_description), R.drawable.icon_music_center));

    }

    /**
     * 初始化情景视图
     */
    private void initSceneView() {
        mSceneView.setHasFixedSize(true);
        mSceneView.setItemAnimator(new DefaultItemAnimator());
        mSceneView.setLayoutManager(new GridLayoutManager(mContext, 3));
//        mSceneView.addItemDecoration(new RecyclerViewDivider(mContext, OrientationHelper.VERTICAL));
        mSceneAdapter = new SceneAdapter(mContext);
        mSceneAdapter.setOnItemClickListener(mOnItemClickListener);
        mSceneView.setAdapter(mSceneAdapter);
    }

    /**
     * 情境项单机事件
     */
    private SceneAdapter.OnItemClickListener mOnItemClickListener = new SceneAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, Object tag) {
            int id = (int) tag;
            switch (id) {
                case 0: {
                    startActivity(new Intent(mContext, PalletActivity.class));
                }
                break;
                case 1: {
                    String musicOff = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
                    ThreadPool.getInstance().addMusicOffTask(new SendRunnable(musicOff));
                }
                break;
                case 2: {
                    String data = CodeUtils.transARGB2Protocol(Constants.COLORMOONMODE);
                    ThreadPool.getInstance().addTask(new SendRunnable(data));
                }
                break;
                case 3: {
                    SettingsDialog.showChangePassword(mContext, null);
                }
                break;
                case 4: {
                    SettingsDialog.showChangeAccount(mContext, -1);
                }
                break;
                case 5: {
                    SettingsDialog.showCurrentColor(mContext, null);
                }
                break;
                case 6: {
                    startActivity(new Intent(mContext, VisualizerActivity.class));
                }
                break;
                case 7: {
                    startActivity(new Intent(mContext, ShakeActivity.class));
                }
                break;
                case 8: {
                    startActivity(new Intent(mContext, TemperatureActivity.class));
                }
                break;
                case 9: {
                    startActivity(new Intent(mContext, SelfieActivity.class));
                }
                break;
                default:
                    ToastUtill.showToast(mContext, getString(R.string.default_text), Constants.TOASTLENGTH).show();
                    break;
            }
            mSceneAdapter.notifyDataSetChanged();
        }
    };
}
