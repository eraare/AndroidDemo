package com.guohua.mlight.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
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
import com.guohua.mlight.model.impl.RxLightService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @file RenameFragment.java
 * @author Leo
 * @version 1
 * @detail 设备重命名功能页面
 * @since 2017/1/13 16:31
 */

/**
 * 文件名：RenameFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2017/1/13 16:31
 * 描  述：设备重命名功能页面
 */
public class RenameFragment extends BottomSheetDialogFragment {
    /*标签*/
    public static final String TAG = RenameFragment.class.getSimpleName();
    private volatile static RenameFragment singleton = null;

    public static RenameFragment getInstance() {
        if (singleton == null) {
            synchronized (RenameFragment.class) {
                if (singleton == null) {
                    singleton = new RenameFragment();
                }
            }
        }
        return singleton;
    }

    @BindView(R.id.et_name_rename)
    EditText mNameView; /*新的设备名*/
    @BindView(R.id.tv_tip_rename)
    TextView mTipView; /*内容提示框*/
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rename, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.tv_change_rename)
    public void onClick(View view) {
        String name = mNameView.getText().toString();
        if (checkName(name)) {
            RxLightService.getInstance().name(name);
            Toast.makeText(getContext(), R.string.settings_warning, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 校验名字信息
     *
     * @param name
     * @return
     */
    private boolean checkName(String name) {
        if (TextUtils.isEmpty(name)) {
            mTipView.setText("设备名不能为空");
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
