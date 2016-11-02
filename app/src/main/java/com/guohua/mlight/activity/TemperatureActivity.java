package com.guohua.mlight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.guohua.mlight.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
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
        // 加载图标Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container_temperature, LineChartFragment.newInstance()).commit();
        }
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
        private TemperatureActivity mContext;
        private View rootView; //根视图
        private LineChartView mChartView; //图标视图

        private LineChartData mChartData;//数据

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
            init();
            return rootView;
        }

        private void init() {
            mContext = (TemperatureActivity) getActivity();
            findViewsByIds();
            initChartData();
        }

        /**
         * 初始化图标数据
         */
        private void initChartData() {
            Random r = new Random();
            // 初始化点
            List<PointValue> points = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                PointValue pointValue = new PointValue(i, r.nextInt(100));
                points.add(pointValue);
            }

            // 初始化线
            List<Line> lines = new ArrayList<>();
            Line line = new Line(points);
            lines.add(line);

            // 初始化坐标轴
            List<AxisValue> axisValuesX = new ArrayList<>();
            List<AxisValue> axisValuesY = new ArrayList<>();
            AxisValue axisValue = new AxisValue(1.0f);
            axisValue.setLabel("a");
            axisValuesX.add(axisValue);
            axisValuesY.add(axisValue);
            /*坐标轴X*/
            Axis axisX = new Axis();
            axisX.setValues(axisValuesX);
            /*坐标轴Y*/
            Axis axisY = new Axis();
            axisY.setValues(axisValuesY);
            // 一个图
            mChartData = new LineChartData(lines);
            mChartData.setAxisXBottom(axisX);
            mChartData.setAxisYLeft(axisY);
            /*属性设置*/
            mChartView.setLineChartData(mChartData);
            mChartView.setInteractive(true);// 可交互
            mChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL); //可缩放
            mChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);//水平滑动
            mChartView.setVisibility(View.VISIBLE);//可见
        }

        private void findViewsByIds() {
            mChartView = (LineChartView) rootView.findViewById(R.id.lcv_chart_chart);
            mChartView.setOnValueTouchListener(mTouchListener);
        }

        /*Section: 事件监听器*/
        private LineChartOnValueSelectListener mTouchListener = new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                t("onValueSelected");
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
