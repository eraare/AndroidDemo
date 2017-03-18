package com.guohua.mlight.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;


/**
 * @file ColorFragment.java
 * @author Leo
 * @version 1
 * @detail 色彩调节底部弹出对话框
 * @since 2017/1/4 11:25
 */

/**
 * 文件名：ColorFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2017/1/4 11:25
 * 描  述：色彩调节底部弹出对话框
 */
public class MeFragment extends BaseFragment {
    public static final String TAG = MeFragment.class.getSimpleName();

    private volatile static MeFragment singleton = null;

    public static MeFragment getInstance() {
        if (singleton == null) {
            synchronized (MeFragment.class) {
                if (singleton == null) {
                    singleton = new MeFragment();
                }
            }
        }
        return singleton;
    }

    /*绑定视图*/
    @BindView(R.id.tv_nickname_me)
    TextView mNickNameView;
    @BindView(R.id.tv_phone_me)
    TextView mPhoneNumberView;

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        /*1 初始化选项*/
        initItems();
    }

    /**
     * 初始化各选项
     */
    private void initItems() {
        /*添加选项*/
        BmobUser currentUser = BmobUser.getCurrentUser();
        mNickNameView.setText(currentUser.getUsername());
        mPhoneNumberView.setText(currentUser.getMobilePhoneNumber());
    }

    @OnClick({R.id.btn_logoff_me, R.id.fl_change_password_me})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_change_password_me: {
//                addFragment(DeviceFragment.getInstance());
            }
            break;
            case R.id.btn_logoff_me: {
                BmobUser.logOut();
                mContext.setResult(Activity.RESULT_OK);
                removeFragment();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }
}
