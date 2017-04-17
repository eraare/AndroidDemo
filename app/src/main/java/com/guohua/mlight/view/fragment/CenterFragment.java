package com.guohua.mlight.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guohua.ios.dialog.ActionSheetDialog;
import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.permission.PermissionListener;
import com.guohua.mlight.common.permission.PermissionManager;
import com.guohua.mlight.common.util.ShareUtils;
import com.guohua.mlight.model.bean.OptionBean;
import com.guohua.mlight.view.activity.AppActivity;
import com.guohua.mlight.view.activity.HelpActivity;
import com.guohua.mlight.view.activity.LoginActivity;
import com.guohua.mlight.view.activity.MeActivity;
import com.guohua.mlight.view.activity.UsActivity;
import com.guohua.mlight.view.adapter.OptionAdapter;
import com.guohua.mlight.view.widget.RecyclerViewDivider;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景模式视图
 */
public class CenterFragment extends BaseFragment {
    public static final String TAG = CenterFragment.class.getSimpleName();
    // 单例模式获取此Fragment
    private static CenterFragment sceneFragment = null;

    public static CenterFragment newInstance() {
        if (sceneFragment == null) {
            synchronized (CenterFragment.class) {
                if (sceneFragment == null) {
                    sceneFragment = new CenterFragment();
                }
            }
        }
        return sceneFragment;
    }

    public static final int CENTER_REQUEST_CODE = 520;
    /*绑定视图控件*/
    @BindView(R.id.ll_me_center)
    RelativeLayout mMeView; /*个人设置中心*/
    @BindView(R.id.iv_head_center)
    ImageView mHeadView; /*头像*/
    @BindView(R.id.tv_name_center)
    TextView mNameView; /*昵称*/
    @BindView(R.id.tv_phone_center)
    TextView mPhoneView; /*手机号*/
    @BindView(R.id.rv_options_center)
    RecyclerView mOptionsView; /*操作项*/

    private OptionAdapter mOptionAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_center;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
        /*1 初始化选项*/
        initOptionsView();
        /*2 初始化个人信息*/
        initUserInfo();
    }

    /**
     * 初始化个人信息
     */
    private void initUserInfo() {
        /*获取当前登录用户*/
        BmobUser currentUser = BmobUser.getCurrentUser();
        if (currentUser != null) {
            /*获取用户信息 显示信息到控件*/
            String username = currentUser.getUsername();
            String phoneNumber = currentUser.getMobilePhoneNumber();
            mNameView.setText(username);
            mPhoneView.setText(phoneNumber);
        } else {
            mNameView.setText(R.string.fragment_login_login);
            mPhoneView.setText(R.string.fragment_login_login);
        }
    }

    /**
     * 初始化View
     */
    private void initOptionsView() {
        mOptionsView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mOptionsView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mOptionsView.setItemAnimator(itemAnimator);
        mOptionsView.addItemDecoration(new RecyclerViewDivider(mContext, OrientationHelper.VERTICAL));
        mOptionAdapter = new OptionAdapter(mContext);
        mOptionsView.setAdapter(mOptionAdapter);
        mOptionAdapter.setOnItemClickListener(mOnItemClickListener);
        initOptions();
    }

    /**
     * 设置选项
     */
    private void initOptions() {
        mOptionAdapter.addItem(new OptionBean(0, getString(R.string.center_phone_func), R.drawable.icon_telephony_center));
        mOptionAdapter.addItem(new OptionBean(1, getString(R.string.center_problem), R.drawable.icon_help_center));
        mOptionAdapter.addItem(new OptionBean(2, getString(R.string.center_feedback), R.drawable.icon_feedback_center));
        mOptionAdapter.addItem(new OptionBean(3, getString(R.string.center_about_app), R.drawable.icon_app_center));
        mOptionAdapter.addItem(new OptionBean(4, getString(R.string.center_about_us), R.drawable.icon_us_center));
        mOptionAdapter.addItem(new OptionBean(5, getString(R.string.center_share), R.drawable.icon_share_center));
//        mOptionAdapter.addItem(new OptionBean(6, "应用推荐", R.drawable.icon_share_center));
    }

    /**
     * 选项单击接口
     */
    private OptionAdapter.OnItemClickListener mOnItemClickListener = new OptionAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, Object tag) {
            int id = (int) tag;
            switch (id) {
                case 0: {
                    requestPermission();
                }
                break;
                case 1: {
                    startActivity(new Intent(mContext, HelpActivity.class));
                }
                break;
                case 2: {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FEEDBACK_WEBSITE)));
                }
                break;
                case 3: {
                    startActivity(new Intent(mContext, AppActivity.class));
                }
                break;
                case 4: {
                    startActivity(new Intent(mContext, UsActivity.class));
                }
                break;
                case 5: {
                    ShareUtils.shareText(mContext, getString(R.string.center_share_title),
                            getString(R.string.center_share_text), null);
                }
                break;
                case 6: {
                }
                break;
                default:
                    break;
            }
        }
    };

    @OnClick(R.id.ll_me_center)
    public void onClick(View view) {
        /*当前用户是否已登录*/
        BmobUser currentUser = BmobUser.getCurrentUser();
        if (currentUser != null) {
            startActivityForResult(new Intent(mContext, MeActivity.class), CENTER_REQUEST_CODE);
        } else {
            startActivityForResult(new Intent(mContext, LoginActivity.class), CENTER_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CENTER_REQUEST_CODE) {
            initUserInfo();
        }
    }

    /*显示来电提醒对话框*/
    private void showTelephonyDialog() {
        new ActionSheetDialog(mContext)
                .builder()
                .setTitle(getString(R.string.choose_call_reminder))
                .addSheetItem(getString(R.string.choose_call_reminder_red), ActionSheetDialog.SheetItemColor.Blue, mOnSheetItemClickListener)
                .addSheetItem(getString(R.string.choose_call_reminder_green), ActionSheetDialog.SheetItemColor.Blue, mOnSheetItemClickListener)
                .addSheetItem(getString(R.string.choose_call_reminder_blue), ActionSheetDialog.SheetItemColor.Blue, mOnSheetItemClickListener)
                /*.addSheetItem(getString(R.string.choose_call_reminder_diy), ActionSheetDialog.SheetItemColor.Red, mOnSheetItemClickListener)*/
                .show();
    }

    private ActionSheetDialog.OnSheetItemClickListener mOnSheetItemClickListener = new ActionSheetDialog.OnSheetItemClickListener() {
        @Override
        public void onClick(int which) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            switch (which) {
                case 1:
                    sp.edit().putInt(Constants.CALL_REMINDER_SHINEMODE, 0).apply();
                    mContext.toast(R.string.choose_call_reminder_red);
                    break;
                case 2:
                    sp.edit().putInt(Constants.CALL_REMINDER_SHINEMODE, 1).apply();
                    mContext.toast(R.string.choose_call_reminder_green);
                    break;
                case 3:
                    sp.edit().putInt(Constants.CALL_REMINDER_SHINEMODE, 2).apply();
                    mContext.toast(R.string.choose_call_reminder_blue);
                    break;
                case 4:
                    //自定义模式
                    break;
                default:
                    break;
            }
        }
    };

    /*Section: 权限管理*/
    private PermissionManager mHelper; /*权限管理类*/
    public static final int PERMISSION_REQUEST_CODE = 103;

    private void requestPermission() {
        /*是否有定位权限使用蓝牙操作进行扫描*/
        if (PermissionManager.hasPermission(mContext, Manifest.permission.READ_PHONE_STATE)) {
            showTelephonyDialog();
        } else {
            if (mHelper == null) {
                mHelper = PermissionManager.with(this)
                        .permissions(Manifest.permission.READ_PHONE_STATE)
                        .setPermissionsListener(mPermissionListener)
                        .addRequestCode(PERMISSION_REQUEST_CODE);
            }
            mHelper.request();
        }
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onGranted() {
            showTelephonyDialog();
        }

        @Override
        public void onDenied() {
            mContext.toast("来电提醒功能需要电话状态权限");
        }

        @Override
        public void onShowRationale(String[] permissions) {
            Snackbar.make(mHeadView, "需要电话状态权限使用来电提醒功能", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHelper.setIsPositive(true);
                            mHelper.request();
                        }
                    }).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            mHelper.onPermissionResult(permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
