package com.guohua.sdk.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guohua.sdk.R;
import com.guohua.sdk.common.base.BaseFragment;
import com.guohua.sdk.common.config.Constants;
import com.guohua.sdk.common.util.ShareUtils;
import com.guohua.sdk.bean.Option;
import com.guohua.sdk.view.activity.AppActivity;
import com.guohua.sdk.view.activity.HelpActivity;
import com.guohua.sdk.view.activity.UsActivity;
import com.guohua.sdk.view.adapter.OptionAdapter;
import com.guohua.sdk.view.widget.RecyclerViewDivider;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Leo
 * @version 1
 * @since 2016-08-25
 * 情景模式视图
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
        mNameView.setText(R.string.fragment_login_login);
        mPhoneView.setText(R.string.fragment_login_login);
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
        mOptionAdapter.addItem(new Option(0, getString(R.string.center_problem), R.drawable.icon_help_center));
        mOptionAdapter.addItem(new Option(1, getString(R.string.center_feedback), R.drawable.icon_feedback_center));
        mOptionAdapter.addItem(new Option(2, getString(R.string.center_about_app), R.drawable.icon_app_center));
        mOptionAdapter.addItem(new Option(3, getString(R.string.center_about_us), R.drawable.icon_us_center));
        mOptionAdapter.addItem(new Option(4, getString(R.string.center_share), R.drawable.icon_share_center));
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
                    startActivity(new Intent(mContext, HelpActivity.class));
                }
                break;
                case 1: {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FEEDBACK_WEBSITE)));
                }
                break;
                case 2: {
                    startActivity(new Intent(mContext, AppActivity.class));
                }
                break;
                case 3: {
                    startActivity(new Intent(mContext, UsActivity.class));
                }
                break;
                case 4: {
                    ShareUtils.shareText(mContext, getString(R.string.center_share_title),
                            getString(R.string.center_share_text), null);
                }
                break;
                default:
                    break;
            }
        }
    };

    @OnClick(R.id.ll_me_center)
    public void onClick(View view) {
        mContext.toast("Hello world.");
    }

}
