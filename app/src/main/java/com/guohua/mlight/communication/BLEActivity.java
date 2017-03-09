package com.guohua.mlight.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class BLEActivity extends Activity {
	private static Context mContext;// 上下文

	private BluetoothAdapter mBluetoothAdapter = null;// 本地蓝牙设备

	private Handler mHandler = null;// 用于postDelay
	private boolean mScanning = false;// 循环标志位

	private static final long SCAN_PERIOD = 10000;// 扫描10s
	private static final int REQUEST_ENABLE_BT = 1;// 请求码

	private ListView mListView = null;
	private BLEAdapter mAdapter = null;

	private Button scanButton;
	private Button button_connect;
	private TextView tv_selectall;

	private ArrayList<String> selectedScanDeviceList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ble);
		if (!checkBluetooth()) {
			finish();
		}
		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		mAdapter = new BLEAdapter(mContext);
		mListView.setAdapter(mAdapter);
//		if (mBluetoothAdapter.isEnabled()) {
//			scanLeDevice(true);
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		//mAdapter.clear();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		suiside();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 判断是否支持蓝牙和BLE
	 * 
	 * @return
	 */
	private boolean checkBluetooth() {
		// 判断是否支持BLE
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			toast("ble not supported");
			return false;
		}

		// 初始化BluetoothAdapter
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙不支 持就退出程序
		if (mBluetoothAdapter == null) {
			toast("bluetooth not supported");
			return false;
		}

		return true;
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		mContext = this;
		mHandler = new Handler();
		findViewsByIds();
	}

	private void findViewsByIds() {
		scanButton = (Button) findViewById(R.id.button_scan);
		button_connect = (Button) findViewById(R.id.button_connect);
		tv_selectall = (TextView) findViewById(R.id.tv_selectall);

		scanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
			}
		});
		scanButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivityForResult(intent, 0);
				return true;
			}
		});

		mListView = (ListView) findViewById(R.id.lv_device_ble);
		mListView.setOnItemClickListener(mItemClickListener);

		button_connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

                clearSelectedScanDeviceList();
                addSelectedScanDeviceList();
                Intent intent = new Intent();
				intent.putExtra(BLEConstant.EXTRA_DEVICE_LIST, selectedScanDeviceList);

				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});

		tv_selectall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tv_selectall.getText().toString().contains(getString(R.string.dialog_select_all))){
					tv_selectall.setText(getString(R.string.dialog_deselect_all));
                    mAdapter.setAllDeviceCheckState(true);
                    clearSelectedScanDeviceList();
                    addSelectedScanDeviceList();
                    mAdapter.notifyDataSetChanged();
				}else {
                    tv_selectall.setText(getString(R.string.dialog_select_all));
                    mAdapter.setAllDeviceCheckState(false);
                    clearSelectedScanDeviceList();
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}

    private void addSelectedScanDeviceList(){
        BluetoothDevice device;
        String name;
        String address;
        System.out.println("selectedScanDeviceList.get(i):  mAdapter.getCount()  " + mAdapter.getCount());
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if(mAdapter.getMLeDevicesIsselected().get(i)){
                device = mAdapter.getDevice(i);
                name = device.getName().trim();
                address = device.getAddress().trim();
                selectedScanDeviceList.add(address + ";" + name);
                System.out.println("selectedScanDeviceList.get(i): " + address + ";" + name);
            }
        }
        System.out.println("selectedScanDeviceList.get(i):  selectedScanDeviceList.size()  " + selectedScanDeviceList.size() );
    }

    private void clearSelectedScanDeviceList(){
        selectedScanDeviceList.clear();
    }

	/**
	 * 列表单击事件
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			/**
			 * 跳转到BLEService 传递一个参数 一个地址 并把名字和地址保存起来
			 */
			System.out.println("跳转到BLEService 传递一个参数 一个地址 并把名字和地址保存起来======== position: " + position);
			setResultToConnect(position);
		}
	};

	private void setResultToConnect(int position) {//被 cb_select_scandev 抢走了焦点
		scanLeDevice(false);

		BluetoothDevice device = mAdapter.getDevice(position);
		String name = device.getName().trim();
		String address = device.getAddress().trim();

        selectedScanDeviceList.add(address + ";" + name);

		System.out.println("selectedScanDeviceList.get(i) ========selectedScanDeviceList.======== " + selectedScanDeviceList.size());
		for (int i = 0; i < selectedScanDeviceList.size(); i++) {
			System.out.println(selectedScanDeviceList.get(i));
		}
		System.out.println("selectedScanDeviceList.get(i) ================");
		mAdapter.setDeviceCheckState(position, !mAdapter.getDeviceCheckState(position));
		mAdapter.notifyDataSetChanged();

	}

	/**
	 * 吐丝
	 * 
	 * @param text
	 */
	private void toast(String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 扫描BLE设备
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					scanButton.setText(getString(R.string.bluetooth_scan));
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	/**
	 * 查找设备
	 */
	private void doDiscovery() {
		if (mScanning) {
			scanLeDevice(false);
			scanButton.setText(getString(R.string.bluetooth_scan));
		} else {
			mAdapter.clear();
			mAdapter.notifyDataSetChanged();
			scanLeDevice(true);
			scanButton.setText(getString(R.string.bluetooth_scanning));
		}
	}

	/**
	 * BLE扫描回调函数，设备保存在remoteDevice里面
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAdapter.addDevice(device);
					mAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	/**
	 * 自杀
	 */
	private void suiside() {
		scanLeDevice(false);
	}

	public void back(View view) {
		this.finish();
	}
}
