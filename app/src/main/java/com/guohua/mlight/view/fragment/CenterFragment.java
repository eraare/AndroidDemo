package com.guohua.mlight.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.ShareUtils;
import com.guohua.mlight.model.bean.OptionBean;
import com.guohua.mlight.view.activity.AppActivity;
import com.guohua.mlight.view.activity.HelpActivity;
import com.guohua.mlight.view.activity.LoginActivity;
import com.guohua.mlight.view.activity.MainActivity;
import com.guohua.mlight.view.activity.MeActivity;
import com.guohua.mlight.view.activity.UsActivity;
import com.guohua.mlight.view.adapter.OptionAdapter;
import com.guohua.mlight.view.widget.RecyclerViewDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景模式视图
 */
public class CenterFragment extends Fragment {
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

    public static final int CENTER_REQUEST_CODE = 1;
    /*绑定视图控件*/
    private Unbinder mUnbinder;
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

    private MainActivity mContext;
    private OptionAdapter mOptionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_center, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        init();//一些初始化
        return rootView;
    }

    /**
     * 初始化数据
     */
    private void init() {
        mContext = (MainActivity) getActivity();
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
        mOptionAdapter.addItem(new OptionBean(0, getString(R.string.center_phone_func), R.drawable.icon_about_center));
        mOptionAdapter.addItem(new OptionBean(1, getString(R.string.center_problem), R.drawable.icon_about_center));
        mOptionAdapter.addItem(new OptionBean(2, getString(R.string.center_feedback), R.drawable.icon_about_center));
        mOptionAdapter.addItem(new OptionBean(3, getString(R.string.center_about_app), R.drawable.icon_about_center));
        mOptionAdapter.addItem(new OptionBean(4, getString(R.string.center_about_us), R.drawable.icon_about_center));
        mOptionAdapter.addItem(new OptionBean(5, getString(R.string.center_share), R.drawable.icon_about_center));
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
        if (requestCode == CENTER_REQUEST_CODE) {
            initUserInfo();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
