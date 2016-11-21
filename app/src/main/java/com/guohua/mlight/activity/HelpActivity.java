package com.guohua.mlight.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.guohua.mlight.R;
import com.guohua.mlight.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class HelpActivity extends AppCompatActivity {
    /*绑定控件*/
    @BindView(R.id.lv_qa_help)
    ListView qa;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        unbinder = ButterKnife.bind(this);
        init();
    }

    private void init() {
        ArrayList<HashMap<String, String>> mData = new ArrayList<>();
        HashMap<String, String> qa1 = new HashMap<>();
        qa1.put("question", "Q:无法连接魔小灯设备？");
        qa1.put("answer", "A:点“+”扫描设备，扫到设备后点击连接即可。");
        mData.add(qa1);
        HashMap<String, String> qa2 = new HashMap<>();
        qa2.put("question", "Q:扫描不到魔小灯设备？");
        qa2.put("answer", "A:设备是否打开？用系统蓝牙设置进行扫描。扫到设备，直接配对，返回程序点“+“，找到配对的设备，点击连接即可；若扫不到，请重启魔小灯设备。");
        mData.add(qa2);
        HashMap<String, String> qa3 = new HashMap<>();
        qa3.put("question", "Q:连接后无法控制？");
        qa3.put("answer", "A:设备是否离线？退出程序，清理后台。重启魔小灯APP，点击“+”，扫描设备，点击连接即可。");
        mData.add(qa3);
        HashMap<String, String> qa4 = new HashMap<>();
        qa4.put("question", "Q:手机锁屏后，程序无法正常工作，如音乐律动停止？");
        qa4.put("answer", "A:若为华为手机，可打开设置-受保护的后台应用，把魔小灯APP加入白名单即可；其他手机可查看后台程序，长按魔小灯程序即可锁定。");
        mData.add(qa4);
        HashMap<String, String> qa5 = new HashMap<>();
        qa5.put("question", "Q:哪里购买魔小灯？");
        qa5.put("answer", "A:点我即可");
        mData.add(qa5);

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, mData, R.layout.item_qa_help,
                new String[]{"question", "answer"}, new int[]{R.id.tv_question_qa, R.id.tv_answer_qa});

        qa.setAdapter(mSimpleAdapter);
    }

    @OnItemClick(R.id.lv_qa_help)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 4) {
            Uri uri = Uri.parse(Constant.OFFICIAL_WEBSITE);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
