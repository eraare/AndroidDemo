package com.guohua.mlight.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.bean.Scene;
import com.guohua.mlight.view.adapter.SceneAdapter;

import butterknife.BindView;

/**
 * @author Leo
 * @version 1
 * @since 2016-08-25
 * 情景模式视图
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
        mSceneAdapter.addScene(new Scene(0, getString(R.string.scene_color_pallet), getString(R.string.colorpallet_description), R.drawable.icon_pallet_scene));
        mSceneAdapter.addScene(new Scene(1, getString(R.string.scene_colorful_gradient), getString(R.string.warmnight_description), R.drawable.icon_gradient_scene));
        mSceneAdapter.addScene(new Scene(2, getString(R.string.scene_warm_light), getString(R.string.defaultmode_description), R.drawable.icon_light_scene));
        mSceneAdapter.addScene(new Scene(3, getString(R.string.scene_set_password), getString(R.string.defaultmode_description), R.drawable.icon_password_scene));
        mSceneAdapter.addScene(new Scene(4, getString(R.string.scene_change_name), getString(R.string.defaultmode_description), R.drawable.icon_rename_scene));
        mSceneAdapter.addScene(new Scene(5, getString(R.string.scene_preset_color), getString(R.string.defaultmode_description), R.drawable.icon_color_scene));
        mSceneAdapter.addScene(new Scene(6, getString(R.string.scene_music_rythm), getString(R.string.defaultmode_description), R.drawable.icon_music_scene));
        mSceneAdapter.addScene(new Scene(7, getString(R.string.scene_shake_shake), getString(R.string.defaultmode_description), R.drawable.icon_shake_scene));
        mSceneAdapter.addScene(new Scene(9, getString(R.string.scene_selfie), getString(R.string.defaultmode_description), R.drawable.icon_selfie_scene));

    }

    /**
     * 初始化情景视图
     */
    private void initSceneView() {
        mSceneView.setHasFixedSize(true);
        mSceneView.setItemAnimator(new DefaultItemAnimator());
        mSceneView.setLayoutManager(new GridLayoutManager(mContext, 3));
//        mSceneView.addItemDecoration(new RecyclerViewDivider(mContext, -1));
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

                default:
                    break;
            }
        }
    };

}
