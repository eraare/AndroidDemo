package com.guohua.mlight.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.view.fragment.MeFragment;

public class MeActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_me;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return MeFragment.getInstance();
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_container_me;
    }

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        setToolbarTitle(R.string.fragment_me_setting);
    }
}
