package com.guohua.mlight.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @file BaseFragment.java
 * @author Leo
 * @version 1
 * @detail Fragment的基类，处理公共操作
 * @since 2016/12/14 9:50
 */

/**
 * 文件名：BaseFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/14 9:50
 * 描  述：Fragment的基类，处理公共操作
 */
public abstract class BaseFragment extends Fragment {
    public final String TAG = this.getClass().getSimpleName();
    // 所附属的Activity
    protected BaseActivity mContext;
    private Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 获取所依附的Activity
        mContext = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 布局并初始化
        View view = inflater.inflate(getLayoutId(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(view, savedInstanceState);
        return view;
    }

    /**
     * 进行相应的初始化
     *
     * @param view
     * @param savedInstanceState
     */
    protected void init(View view, Bundle savedInstanceState) {
    }

    /**
     * 获取布局Id
     *
     * @return
     */
    protected abstract int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
        suicide();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 做一些销毁操作
     */
    protected void suicide() {
    }

    public void addFragment(BaseFragment fragment) {
        mContext.addFragment(fragment);
    }

    public void removeFragment() {
        mContext.removeFragment();
    }

    /**
     * 用于展示底部弹出对话框
     *
     * @param fragment
     * @param tag
     */
    public void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment, String tag) {
        if (!fragment.isAdded()) {
            FragmentManager fragmentManager = getFragmentManager();
            fragment.setCancelable(true);
            fragment.show(fragmentManager, tag);
        }
    }
}
