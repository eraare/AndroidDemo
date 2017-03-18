package com.guohua.mlight.view.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.guohua.ios.dialog.AlertDialog;
import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.view.adapter.FragmentAdapter;
import com.guohua.mlight.view.fragment.CenterFragment;
import com.guohua.mlight.view.fragment.HomeFragment;
import com.guohua.mlight.view.fragment.SceneFragment;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * @author Leo
 * @detail 项目框架主界面 具有滑动切换功能 各类功能在各自Fragment里
 * @time 2015-10-29
 */
@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {
    /*绑定控件*/
    @BindView(R.id.bnb_bar_main)
    BottomNavigationBar mBarView;
    @BindView(R.id.vp_pager_main)
    ViewPager mPagerView;
    /*绑定数组*/
    @BindArray(R.array.activity_titles_main)
    String[] mTitles; // 显示在底部标签栏的标题
    @BindArray(R.array.fragment_alias_main)
    String[] mAlias; // 显示在顶部导航栏的别名
    /*底部标签导航的ICON图像*/
    private int[] mNormalIconIds = {
            R.drawable.icon_home_normal,
            R.drawable.icon_scene_normal,
            R.drawable.icon_center_normal,
    };
    /*底部标签导航被选中的ICON图像*/
    private int[] mSelectedIconIds = {
            R.drawable.icon_home_checked,
            R.drawable.icon_scene_checked,
            R.drawable.icon_center_checked,
    };

    /*默认页面的位置*/
    private int lastSelectedPosition = 0;
    /*ViewPager加载Fragment所需要的Adapter*/
    private FragmentAdapter mFragmentAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        setShowBack(false);
        initial();
    }

    /**
     * 初始化操作
     */
    private void initial() {
        /*1 初始化底部导航条*/
        initBottomNavigationBar();
        /*2 初始化ViewPager*/
        initViewPager();
        /*3 初始化顶部导航栏标题*/
        setToolbarTitle(mAlias[lastSelectedPosition]);
        showOrHideForward(lastSelectedPosition);
        /* 添加右边前进键单机事件*/
        setOnForwardClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, DeviceActivity.class));
        }
    };

    /**
     * 初始化ViewPager显示Fragments
     */
    private void initViewPager() {
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mFragmentAdapter.addFragment(HomeFragment.newInstance());
        mFragmentAdapter.addFragment(SceneFragment.newInstance());
        mFragmentAdapter.addFragment(CenterFragment.newInstance());
        mPagerView.setAdapter(mFragmentAdapter);
        mPagerView.setOffscreenPageLimit(2);
        mPagerView.addOnPageChangeListener(mOnPageChangeListener);
        mPagerView.setCurrentItem(lastSelectedPosition, true);
    }

    /**
     * ViewPager滑动事件
     */
    private ViewPager.SimpleOnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            /*切换底部导航栏*/
            mBarView.selectTab(position, false);
            /*设置顶部导航栏标题*/
            setToolbarTitle(mAlias[position]);
            /*显示隐藏Forward按钮*/
            showOrHideForward(position);
        }
    };

    private void showOrHideForward(int position) {
        if (position == 0) {
            setForwardVisibility(View.VISIBLE);
        } else {
            setForwardVisibility(View.GONE);
        }
    }

    /**
     * 初始化底部导航栏
     */
    private void initBottomNavigationBar() {
        /*添加标签页*/
        int length = mTitles.length;
        for (int i = 0; i < length; i++) {
            BottomNavigationItem item = new BottomNavigationItem(mSelectedIconIds[i], mTitles[i]);
            item.setInactiveIconResource(mNormalIconIds[i]);
            mBarView.addItem(item);
        }
        mBarView.setFirstSelectedPosition(lastSelectedPosition > length ? length : lastSelectedPosition);
        mBarView.initialise();
        mBarView.setTabSelectedListener(mOnTabSelectedListener);
    }

    /**
     * 标签选择事件
     */
    private BottomNavigationBar.SimpleOnTabSelectedListener mOnTabSelectedListener = new BottomNavigationBar.SimpleOnTabSelectedListener() {
        @Override
        public void onTabSelected(int position) {
            /*切换ViewPager的显示页面*/
            mPagerView.setCurrentItem(position, true);
            /*切换顶部导航栏的标题*/
            setToolbarTitle(mAlias[position]);
            /*显示隐藏Forward按钮*/
            showOrHideForward(position);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog(this).builder()
                    .setCancelable(true)
                    .setTitle(getString(R.string.dialog_title))
                    .setMsg(getString(R.string.dialog_message))
                    .setPositiveButton(getString(R.string.dialog_positive), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            background();
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_negative), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exit();
                        }
                    }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出程序关闭所有
     */
    private void exit() {

    }

    /**
     * 后台运行，关闭MainActivity 但不关闭后台服务 不关闭蓝牙
     */
    private void background() {
        //moveTaskToBack(false);//此句代码可代替下面所有代码
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startActivitySafely(startIntent);
        Toast.makeText(getApplicationContext(), R.string.exit_background, Toast.LENGTH_SHORT).show();
    }

    /**
     * 安全启动Activity
     *
     * @param intent
     */
    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d("Leo", "MainActivity startActivitySafely() error");
        } catch (SecurityException e) {
            Log.d("Leo", "MainActivity startActivitySafely() error");
        }
    }

    @Override
    protected void suicide() {
        super.suicide();
        AppContext.getInstance().exitApplication();
    }
}
