package com.guohua.mlight.view.activity;

import android.os.Bundle;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.view.fragment.MeFragment;
import com.guohua.mlight.view.widget.TitleView;

import butterknife.BindView;

public class MeActivity extends BaseActivity {
    @BindView(R.id.tnb_title_me)
    TitleView mTitleView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_me;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mTitleView.setOnLeftClickListener(mOnLeftClickListener);
    }

    /*左键单击事件*/
    private TitleView.OnLeftClickListener mOnLeftClickListener = new TitleView.OnLeftClickListener() {
        @Override
        public void onLeftClick(View v) {
            removeFragment();
        }
    };

    @Override
    protected BaseFragment getFirstFragment() {
        return MeFragment.getInstance();
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_container_me;
    }
}
