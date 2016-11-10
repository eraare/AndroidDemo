package com.guohua.mlight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.communication.BLEConstant;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * @author Leo
 * @version 1.0
 * @see "温度监测图标显示"
 * @since 2016-11-1
 */
public class TemperatureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        // 加载图表Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container_temperature,
                    LineChartFragment.newInstance()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册监听器监听温度值
        registerTheReceiver();
    }

    /**
     * 注册广播
     */
    private void registerTheReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEConstant.ACTION_RECEIVED_TEMPERATURE);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * 广播接收器
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, BLEConstant.ACTION_RECEIVED_TEMPERATURE)) {
                String deviceAddress = intent.getStringExtra(BLEConstant.EXTRA_DEVICE_ADDRESS);
                String data = intent.getStringExtra(BLEConstant.EXTRA_RECEIVED_DATA);
                if (data != null && !TextUtils.equals("", data)) {
                    String[] temp = data.split(":");
                    if (temp.length >= 2) {
                        float temperature = Float.parseFloat(temp[1]);
                        PointValue pointValue = new PointValue(0f, temperature);
                        LineChartFragment.newInstance().addPoint(pointValue);
                    }
                }

            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        // 取消广播接收器
        unregisterReceiver(mBroadcastReceiver);
    }

    /*Section: 视图Fragment*/

    /**
     * 图标显示的Fragment
     */
    public static class LineChartFragment extends Fragment {
        /*Section: 单例模式*/
        private volatile static LineChartFragment lineChartFragment = null;

        public static LineChartFragment newInstance() {
            if (lineChartFragment == null) {
                synchronized (LineChartFragment.class) {
                    if (lineChartFragment == null) {
                        lineChartFragment = new LineChartFragment();
                    }
                }
            }
            return lineChartFragment;
        }

        /*Section: 主模块*/
        private static final int POINT_NUMBER = 25; //页面显示的点数
        private TemperatureActivity mContext;
        private View rootView; //根视图
        private LineChartView mChartView; //图表视图
        private LineChartData mChartData; //图表数据

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
            init(); //初始化
            return rootView;
        }

        /**
         * 初始化一些内容
         */
        private void init() {
            mContext = (TemperatureActivity) getActivity();
            findViewsByIds(); // 初始化控件
            initLineChartView(); // 初始化折线图
        }

        private void findViewsByIds() {
            mChartView = (LineChartView) rootView.findViewById(R.id.lcv_chart_chart);
            mChartView.setOnValueTouchListener(mTouchListener);
        }

        /**
         * 初始化折线图
         */
        private void initLineChartView() {
            // 获取折线图数据
            mChartData = initLineChartData();
            // 当前视角
            Viewport viewport = initViewport();
            // 设置属性
            mChartView.setLineChartData(mChartData);
            mChartView.setCurrentViewport(viewport);
            mChartView.setInteractive(true);// 可交互
            mChartView.setScrollEnabled(true);//是否可滚动
            mChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);//滚动类型
            mChartView.setZoomEnabled(true); //是否可放大缩小
            mChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL); //缩放类型
            mChartView.setVisibility(View.VISIBLE);//可见
        }

        /**
         * 初始化视角 当前为最后点
         *
         * @return
         */
        private Viewport initViewport() {
            Viewport viewport = new Viewport();
            viewport.top = 200;
            viewport.bottom = 0;
            int right = 25;
            int left = 0;
            if (mChartData != null) {
                right = mChartData.getLines().get(0).getValues().size();
                left = (right - POINT_NUMBER) > 0 ? (right - POINT_NUMBER) : 0;
            }
            viewport.left = left;
            viewport.right = right;

            return viewport;
        }

        /**
         * 初始化图标数据
         */
        private LineChartData initLineChartData() {
            LineChartData lineChartData = new LineChartData();
            // 设置X轴和Y轴
            Axis axisX = initAxisX();
            Axis axisY = initAxisY();
            List<Line> lines = initLines();
            //X在下 Y在左
            lineChartData.setAxisXBottom(axisX);
            lineChartData.setAxisYLeft(axisY);
            lineChartData.setLines(lines);

            return lineChartData;
        }

        /**
         * 初始化一根线
         *
         * @return
         */
        private List<Line> initLines() {
            //Random r = new Random();
            // 初始化点线
            List<PointValue> points = new ArrayList<>();
            points.add(new PointValue(0f, 0f).setLabel("(" + 0.0 + ", " + 0.0 + ")"));
            points.add(new PointValue(1f, 200f).setLabel("(" + 1.0 + ", " + 200.0 + ")"));
            /*for (int i = 0; i < 50; i++) {
                points.add(new PointValue(i, r.nextInt(100)).setLabel("(" + i + ", " + i + ")"));
            }*/
            List<Line> lines = new ArrayList<>();
            Line line = new Line(points);
            line.setCubic(true);
            line.setHasLabels(false);
            line.setShape(ValueShape.DIAMOND);
            lines.add(line);
            return lines;
        }

        /**
         * X轴
         *
         * @return
         */
        private Axis initAxisX() {
            /*坐标轴X*/
            List<AxisValue> axisValuesX = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                axisValuesX.add(new AxisValue(i).setLabel(i + ""));
            }
            Axis axisX = new Axis(axisValuesX).setName("时间/时").setHasLines(true);
            return axisX;
        }

        /**
         * Y轴
         *
         * @return
         */
        private Axis initAxisY() {
             /*坐标轴Y*/
            List<AxisValue> axisValuesY = new ArrayList<>();
            for (int i = 0; i < 200; i += 5) {
                axisValuesY.add(new AxisValue(i).setLabel(i + ""));
            }
            Axis axisY = new Axis(axisValuesY).setName("温度/摄氏度").setHasLines(true);
            return axisY;
        }

        /**
         * 公共接口 添加点
         *
         * @param pointValue
         */
        public void addPoint(PointValue pointValue) {
            List<PointValue> pointValues = mChartData.getLines().get(0).getValues();
            pointValue.set(pointValues.size() + 1, pointValue.getY());
            pointValues.add(pointValue);
            mChartData.getLines().get(0).setValues(pointValues);
            Viewport viewport = initViewport();
            mChartView.setCurrentViewportWithAnimation(viewport);
            mChartView.setLineChartData(mChartData);
        }

        /*Section: 事件监听器*/
        private LineChartOnValueSelectListener mTouchListener = new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                t("(" + value.getX() + ", " + value.getY() + ")");
            }

            @Override
            public void onValueDeselected() {
                t("onValueDeselected");
            }
        };

        private void t(String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }
}