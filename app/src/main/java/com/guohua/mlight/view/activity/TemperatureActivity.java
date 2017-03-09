package com.guohua.mlight.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.communication.BLEConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
                        float x = Float.parseFloat(temp[1]);
                        double T = 1.40120080422346e-15 * Math.pow(x, 6) - 5.56853978625422e-12 * Math.pow(x, 5)
                                + 8.77512419103114e-09 * Math.pow(x, 4) - 7.01484439592888e-06 * Math.pow(x, 3)
                                + 0.00304713220829926 * Math.pow(x, 2) - 0.763588201330716 * x + 129.340455275951;
                        T = T > 100 ? 100 : T;
                        PointValue pointValue = new PointValue(0f, (float) T);
                        LineChartFragment.newInstance().addPoint(deviceAddress, pointValue);
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
        private static final int POINT_NUMBER = 20; //页面显示的点数
        private TemperatureActivity mContext;
        private View rootView; //根视图
        private TextView mShow; //显示标题
        private LineChartView mChartView; //图表视图
        private LineChartData mChartData; //图表数据
        private HashMap<String, Line> mLines; //根据设备地址保存所有的线

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
            showTitle();
        }

        private void showTitle() {
            if (mLines == null) {
                mShow.setText("No Devices");
                return;
            }
            int size = mLines.size();
            if (size <= 0) {
                mShow.setText("No Devices");
                return;
            }
            StringBuilder sb = new StringBuilder();

            Set<String> keys = mLines.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String address = iterator.next();
                String name = getNameByAddress(address);
                Line line = mLines.get(address);
                int color = line.getColor();
                String r = int2HexString(Color.red(color));
                String g = int2HexString(Color.green(color));
                String b = int2HexString(Color.blue(color));

                String s = "<font color=\"#" + r + g + b + "\"><b>" + name + "</b></font>&nbsp&nbsp";
                sb.append(s);
            }
            System.out.println(sb.toString());
            Spanned spanned = Html.fromHtml(sb.toString());
            mShow.setText(spanned);
        }

        /**
         * 根据地址获取名字
         *
         * @param address
         * @return
         */
        private String getNameByAddress(String address) {
            List<Device> devices = AppContext.getInstance().devices;
            for (Device device : devices) {
                if (TextUtils.equals(device.getDeviceAddress(), address)) {
                    return device.getDeviceName();
                }
            }
            return "Unknown Name";
        }

        private String int2HexString(int dec) {
            String hex = Integer.toHexString(dec);
            if (dec < 16) {
                hex = "0" + hex;
            }
            return hex;
        }

        private void findViewsByIds() {
            mShow = (TextView) rootView.findViewById(R.id.tv_show_chart);
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
            //Viewport viewport = initViewport(mChartData.getLines().get(0));
            // 设置属性
            mChartView.setLineChartData(mChartData);
            //mChartView.setCurrentViewport(viewport);
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
        private Viewport initViewport(Line line) {
            Viewport viewport = new Viewport();
            viewport.top = 110;
            viewport.bottom = 0;
            int right = 25;
            int left = 0;
            if (mChartData != null) {
                right = line.getValues().size();
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
            List<Line> lines = new ArrayList<>();
            // 根据已有设备建立曲线
            ArrayList<Device> devices = AppContext.getInstance().devices;
            // 判空
            if (devices == null) {
                return lines;
            }
            int size = devices.size();
            if (size <= 0) {
                return lines;
            }
            mLines = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Device device = devices.get(i);
                String deviceAddress = device.getDeviceAddress();
                Line line = initLine(randomColor());
                mLines.put(deviceAddress, line);
                lines.add(line);
            }
            return lines;
        }

        private int randomColor() {
            Random r = new Random();
            int alpha = 255;
            int red = r.nextInt(256);
            int green = r.nextInt(256);
            int blue = r.nextInt(256);
            return Color.argb(alpha, red, green, blue);
        }

        private Line initLine(int color) {
            List<PointValue> points = new ArrayList<>();
            points.add(new PointValue(0f, 0f)/*.setLabel("(0, 0)")*/);
            points.add(new PointValue(1f, 100f)/*.setLabel("(100, 100)")*/);
            Line line = new Line(points);
            //line.setCubic(true);
            line.setHasLabels(true);
            line.setShape(ValueShape.CIRCLE);
            line.setColor(color);
            return line;
        }

        /**
         * X轴
         *
         * @return
         */
        private Axis initAxisX() {
            /*坐标轴X*/
            List<AxisValue> axisValuesX = new ArrayList<>();
            for (int i = 0; i < 500; i += 5) {
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
            for (int i = 0; i < 110; i += 10) {
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
        public void addPoint(String deviceAddress, PointValue pointValue) {
            Line line = mLines.get(deviceAddress);
            if (line == null) {
                return;
            }
            List<PointValue> pointValues = line.getValues();
            pointValue.set(pointValues.size() + 1, pointValue.getY());
            pointValues.add(pointValue);
            mChartData.getLines().get(0).setValues(pointValues);
            Viewport viewport = initViewport(line);
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