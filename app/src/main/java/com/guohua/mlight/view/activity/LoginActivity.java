package com.guohua.mlight.view.activity;

import android.os.Bundle;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.view.fragment.LoginFragment;

public class LoginActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected BaseFragment getFirstFragment() {
        return LoginFragment.getInstance();
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_container_login;
    }
}
