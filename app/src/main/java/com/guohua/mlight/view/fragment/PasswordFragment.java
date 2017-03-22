package com.guohua.mlight.view.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.model.impl.RxLightService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @file PasswordFragment.java
 * @author Leo
 * @version 1
 * @detail 设置密码功能
 * @since 2017/1/13 16:22
 */

/**
 * 文件名：PasswordFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2017/1/13 16:22
 * 描  述：设置密码功能
 */
public class PasswordFragment extends BottomSheetDialogFragment {
    /*标签*/
    public static final String TAG = PasswordFragment.class.getSimpleName();
    private volatile static PasswordFragment singleton = null;

    public static PasswordFragment getInstance() {
        if (singleton == null) {
            synchronized (PasswordFragment.class) {
                if (singleton == null) {
                    singleton = new PasswordFragment();
                }
            }
        }
        return singleton;
    }

    @BindView(R.id.et_password_password)
    EditText mPasswordView;/*密码视图*/
    @BindView(R.id.tv_tip_password)
    TextView mTipView; /*提示语*/
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.tv_set_password)
    public void onClick(View view) {
        String password = mPasswordView.getText().toString();
        if (checkPassword(password)) {
            /*读取旧密码*/
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String oldPassword = preferences.getString(Constants.KEY_GLOBAL_PASSWORD, Constants.DEFAULT_GLOBAL_PASSWORD);
            /*设置新密码*/
            preferences.edit().putString(Constants.KEY_GLOBAL_PASSWORD, password).apply();
            /*发送给灯设置新密码*/
            RxLightService.getInstance().password(oldPassword, password);
            Toast.makeText(getContext(), R.string.settings_warning, Toast.LENGTH_SHORT).show();
        }
    }

    /*密码有效性的校验*/
    private boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            mTipView.setText("密码不能为空");
            mTipView.setTextColor(Color.RED);
            return false;
        }
        if (password.length() != 4) {
            mTipView.setText("密码必须为四位数字");
            mTipView.setTextColor(Color.RED);
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
