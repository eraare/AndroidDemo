package com.guohua.mlight.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.guohua.ios.dialog.AlertDialog;
import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.permission.PermissionListener;
import com.guohua.mlight.common.permission.PermissionManager;
import com.guohua.mlight.model.bean.SceneBean;
import com.guohua.mlight.model.impl.RxLightService;
import com.guohua.mlight.view.activity.PalletActivity;
import com.guohua.mlight.view.activity.SelfieActivity;
import com.guohua.mlight.view.activity.ShakeActivity;
import com.guohua.mlight.view.activity.TemperatureActivity;
import com.guohua.mlight.view.activity.VisualizerActivity;
import com.guohua.mlight.view.adapter.SceneAdapter;

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
    private boolean isGradientOn = false;

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
        mSceneAdapter.addScene(new SceneBean(0, getString(R.string.scene_color_pallet), getString(R.string.colorpallet_description), R.drawable.icon_music_scene));
        mSceneAdapter.addScene(new SceneBean(1, getString(R.string.scene_colorful_gradient), getString(R.string.warmnight_description), R.drawable.icon_music_scene));
        mSceneAdapter.addScene(new SceneBean(2, getString(R.string.scene_warm_light), getString(R.string.defaultmode_description), R.drawable.icon_music_scene));
        mSceneAdapter.addScene(new SceneBean(3, getString(R.string.scene_set_password), getString(R.string.defaultmode_description), R.drawable.icon_password_scene));
        mSceneAdapter.addScene(new SceneBean(4, getString(R.string.scene_change_name), getString(R.string.defaultmode_description), R.drawable.icon_rename_scene));
        mSceneAdapter.addScene(new SceneBean(5, getString(R.string.scene_preset_color), getString(R.string.defaultmode_description), R.drawable.icon_color_scene));
        mSceneAdapter.addScene(new SceneBean(6, getString(R.string.scene_music_rythm), getString(R.string.defaultmode_description), R.drawable.icon_music_scene));
        mSceneAdapter.addScene(new SceneBean(7, getString(R.string.scene_shake_shake), getString(R.string.defaultmode_description), R.drawable.icon_shake_scene));
//        mSceneAdapter.addScene(new SceneBean(8, getString(R.string.scene_temperature), getString(R.string.defaultmode_description), R.drawable.icon_temperature_scene));
        mSceneAdapter.addScene(new SceneBean(9, getString(R.string.scene_selfie), getString(R.string.defaultmode_description), R.drawable.icon_selfie_scene));

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
                case 0: {
                    startActivity(new Intent(mContext, PalletActivity.class));
                }
                break;
                case 1: {
                    if (isGradientOn) {
                        RxLightService.getInstance().musicOff();
                        mContext.toast("炫彩渐变已关闭");
                        isGradientOn = false;
                    } else {
                        RxLightService.getInstance().musicOn();
                        mContext.toast("炫彩渐变已开启");
                        isGradientOn = true;
                    }
                }
                break;
                case 2: {
                    /*发送魔小灯的颜色*/
                    int color = Color.argb(255, 160, 60, 10);
                    RxLightService.getInstance().adjustColor(color);
                    mContext.toast("小夜灯模式已开启");
                }
                break;
                case 3: {
                    showBottomSheetDialogFragment(PasswordFragment.getInstance(), PasswordFragment.TAG);
                }
                break;
                case 4: {
                    showBottomSheetDialogFragment(RenameFragment.getInstance(), RenameFragment.TAG);
                }
                break;
                case 5: {
                    showPresetColorDialog();
                }
                break;
                case 6: {
                    if (PermissionManager.hasPermission(mContext, Manifest.permission.RECORD_AUDIO)) {
                        startActivity(new Intent(mContext, VisualizerActivity.class));
                    } else {
                        requestPermission(REQUEST_CODE_AUDIO, Manifest.permission.RECORD_AUDIO);
                    }
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
                    /*需要照相机的权限才能正常使用*/
                    if (PermissionManager.hasPermission(mContext, Manifest.permission.CAMERA)) {
                        startActivity(new Intent(mContext, SelfieActivity.class));
                    } else {
                        requestPermission(REQUEST_CODE_CAMERA, Manifest.permission.CAMERA);
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    private void showPresetColorDialog() {
        new AlertDialog(mContext).builder()
                .setCancelable(true)
                .setTitle(getString(R.string.settings_color))
                .setMsg(getString(R.string.settings_color_message))
                .setPositiveButton(getString(R.string.settings_positive), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxLightService.getInstance().presetColor();
                        Toast.makeText(getContext(), R.string.settings_color_tip, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.settings_negative), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    /*Section: Android 6.0权限管理*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                mPermissionManager.onPermissionResult(permissions, grantResults);
                break;
            case REQUEST_CODE_AUDIO:
                mPermissionManager.onPermissionResult(permissions, grantResults);
                break;
            default:
                break;
        }
    }

    /*权限管理器*/
    private PermissionManager mPermissionManager;
    private int permissionRequestCode = -1;
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_AUDIO = 2;

    /**
     * 请求权限
     */
    private void requestPermission(int requestCode, String permission) {
        mPermissionManager = PermissionManager.with(this)
                .addRequestCode(requestCode)
                .permissions(permission)
                .setPermissionsListener(mPermissionListener)
                .request();
    }


    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onGranted() {
            if (permissionRequestCode == REQUEST_CODE_CAMERA) {
                startActivity(new Intent(mContext, SelfieActivity.class));
            } else if (permissionRequestCode == REQUEST_CODE_AUDIO) {
                startActivity(new Intent(mContext, VisualizerActivity.class));
            }
        }

        @Override
        public void onDenied() {
            if (permissionRequestCode == REQUEST_CODE_CAMERA) {
                mContext.toast("必须有相机权限才能使用此功能");
            } else if (permissionRequestCode == REQUEST_CODE_AUDIO) {
                mContext.toast("必须有麦克风权限才能使用此功能");
            }

        }

        @Override
        public void onShowRationale(String[] permissions) {
            String text;
            if (permissionRequestCode == REQUEST_CODE_CAMERA) {
                text = "需要相机权限去拍照";
            } else if (permissionRequestCode == REQUEST_CODE_AUDIO) {
                text = "需要麦克风权限去音乐律动";
            } else {
                text = "需要权限使用此功能";
            }
            Snackbar.make(mSceneView, text, Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPermissionManager.setIsPositive(true);
                            mPermissionManager.request();
                        }
                    }).show();
        }
    };

}
