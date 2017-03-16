package com.guohua.mlight.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class HelpActivity extends BaseActivity {
    /*绑定控件*/
    @BindView(R.id.lv_qa_help)
    ListView mQaView;
    private QaAdapter mQaAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_help;
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
        setToolbarTitle(getString(R.string.center_problem));
        initListView();
    }

    private void initListView() {
        mQaAdapter = new QaAdapter(this);
        mQaView.setAdapter(mQaAdapter);
        loadQaInfo();
    }

    private void loadQaInfo() {
        mQaAdapter.addQa(new QaInfo("1、一直连不上怎么办？", "答：您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。"));
        mQaAdapter.addQa(new QaInfo("2、一直连不上怎么办？", "答：您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。"));
        mQaAdapter.addQa(new QaInfo("3、一直连不上怎么办？", "答：您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。"));
        mQaAdapter.addQa(new QaInfo("4、一直连不上怎么办？", "答：您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。"));
        mQaAdapter.addQa(new QaInfo("5、一直连不上怎么办？", "答：您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。"));
        mQaAdapter.addQa(new QaInfo("6、一直连不上怎么办？", "答：您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。您可以重启APP，重启魔小灯后再次尝试。"));
    }

    @OnItemClick(R.id.lv_qa_help)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 4) {
            Uri uri = Uri.parse(Constants.OFFICIAL_WEBSITE);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    /*QA Bean*/
    private static class QaInfo {
        String question; /*问题*/
        String answer; /*答案*/

        public QaInfo(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    /*QA Adapter*/
    class QaAdapter extends BaseAdapter {
        private List<QaInfo> mDatas;
        private LayoutInflater mInflater;

        public QaAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mDatas = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int i) {
            return mDatas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.item_qa_help, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            QaInfo qaInfo = mDatas.get(i);
            holder.question.setText(qaInfo.question);
            holder.answer.setText(qaInfo.answer);
            return view;
        }

        class ViewHolder {
            @BindView(R.id.tv_question_qa)
            TextView question;
            @BindView(R.id.tv_answer_qa)
            TextView answer;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

        public void addQa(QaInfo qaInfo) {
            mDatas.add(qaInfo);
        }
    }
}
