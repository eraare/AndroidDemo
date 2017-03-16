package com.guohua.mlight.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;

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
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.btn_set_password)
    public void onClick(View view) {
        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) return; /*密码不为空*/
        if (password.length() != 4) return; /*密码为4位*/
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_PASSWORD, new String[]{sp.getString("ALL", CodeUtils.password), password});
        sp.edit().putString("ALL", password).apply();
        CodeUtils.setPassword(password);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
        Toast.makeText(getContext(), R.string.settings_warning, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
