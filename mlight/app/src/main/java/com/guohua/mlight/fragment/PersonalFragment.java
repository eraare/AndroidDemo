package com.guohua.mlight.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.guohua.mlight.ConnectActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.view.activity.AboutActivity;
import com.guohua.mlight.view.adapter.OptionsAdapter;
import com.guohua.mlight.model.bean.Option;
import com.guohua.mlight.common.util.Constant;
import com.guohua.mlight.common.util.ToastUtill;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {
    public static final int ID = 1;

    public static final String TAG = PersonalFragment.class.getSimpleName();
    /**
     * 单例模式
     */
    private volatile static PersonalFragment personalFragment = null;

    public static PersonalFragment getInstance() {
        if (personalFragment == null) {
            synchronized (Scene1Fragment.class) {
                if (personalFragment == null) {
                    personalFragment = new PersonalFragment();
                }
            }
        }
        return personalFragment;
    }

    private ConnectActivity mContext;//用于和ConnectActivity通信

    public PersonalFragment() {
        // Required empty public constructor
    }

    private View rootView;
    private ListView mOptions;
    private OptionsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        init();
        return rootView;
    }

    private void init() {
        mContext = (ConnectActivity) getContext();
        findViewsByIds();
    }

    private void findViewsByIds() {
        mOptions = (ListView) rootView.findViewById(R.id.lv_options_personal);
        mAdapter = new OptionsAdapter(mContext);
        mAdapter.addOption(new Option(R.drawable.icon_personal_feedback, getString(R.string.personal_feedback)));
        mAdapter.addOption(new Option(R.drawable.icon_about_app, getString(R.string.personal_about_app)));
        mAdapter.addOption(new Option(R.drawable.icon_about_us, getString(R.string.personal_about_us)));
        mOptions.setAdapter(mAdapter);
        mOptions.setOnItemClickListener(mOnItemClickListener);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0: {
                    /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.OFFICIAL_WEBSITE));
                    startActivity(intent);*/
                    ToastUtill.showToast(mContext, getString(R.string.default_tobecontinued), Constant.TOASTLENGTH).show();
                }
                break;
                case 1: {
                    Intent intent = new Intent(mContext, AboutActivity.class);
                    startActivity(intent);
                }
                break;
                case 2: {
                    /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.OFFICIAL_WEBSITE));
                    startActivity(intent);*/
                    ToastUtill.showToast(mContext, getString(R.string.default_tobecontinued), Constant.TOASTLENGTH).show();
                }
                break;
            }
        }
    };

}
