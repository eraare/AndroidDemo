package com.guohua.mlight.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.model.bean.ItemInfo;
import com.guohua.mlight.view.adapter.ItemAdapter;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
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
    @BindView(R.id.lv_item_me)
    ListView mItemView;
    /*选项的适配器*/
    private ItemAdapter mItemAdapter;

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        /*1 初始化选项*/
        initItems();
    }

    /**
     * 初始化各选项
     */
    private void initItems() {
        mItemAdapter = new ItemAdapter(mContext);
        /*添加选项*/
        BmobUser currentUser = BmobUser.getCurrentUser();
        mItemAdapter.addItem(new ItemInfo("用户名", currentUser.getUsername()));
        mItemAdapter.addItem(new ItemInfo("手机号", currentUser.getMobilePhoneNumber()));
        mItemAdapter.addItem(new ItemInfo("邮箱", currentUser.getEmail()));
        mItemAdapter.addItem(new ItemInfo("密码", "更改密码"));
        mItemView.setAdapter(mItemAdapter);
    }

    @OnItemClick(R.id.lv_item_me)
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 3: {

            }
            break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_logoff_me)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logoff_me: {
                /*退出账户*/
                BmobUser.logOut();
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
