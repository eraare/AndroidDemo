package com.guohua.mlight.library;

import android.app.Activity;

/**
 * @author Leo
 * @detail 显示已配对的蓝牙设备和新搜索的蓝牙设备
 * @time 2015-12-14
 */
public class ScanBluetoothDevice extends Activity {
    /*private BluetoothAdapter mBtAdapter;//蓝牙适配器用于扫描
    private DeviceAdapter mDeviceAdapter;//蓝牙设备显示适配器
    private Button scanButton;//扫描按钮
    private SwipeMenuListView deviceListView;//设备显示列表

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        init();
    }

    *//**
     * 初始化数据
     *//*
    private void init() {
        findViewsByIds();
        //显示已有的设备
        mDeviceAdapter = new DeviceAdapter(this);
        deviceListView.setAdapter(mDeviceAdapter);
        initSwipeMenu();
        //初始化蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        initBondedDevices();
        //注册监听器添加扫描到的内容
    }

    private void initSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem connectItem = new SwipeMenuItem(getApplicationContext());
                connectItem.setBackground(new ColorDrawable(getResources().getColor(R.color.main_pink)));
                connectItem.setWidth(ToolUtils.dp2px(getApplicationContext(), 80));
                connectItem.setTitle(getString(R.string.bluetooth_connect));
                connectItem.setTitleSize(16);
                connectItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(connectItem);

                SwipeMenuItem cancelItem = new SwipeMenuItem(getApplicationContext());
                cancelItem.setBackground(new ColorDrawable(getResources().getColor(R.color.main)));
                cancelItem.setWidth(ToolUtils.dp2px(getApplicationContext(), 80));
                cancelItem.setTitle(getString(R.string.bluetooth_unpair));
                cancelItem.setTitleSize(16);
                cancelItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(cancelItem);
            }
        };
        // set creator
        deviceListView.setMenuCreator(creator);

        // step 2. listener item click event
        deviceListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        setResultToConnect(position);
                        break;
                    case 1: {
                        if (mDeviceAdapter == null) {
                            return;
                        }
                        BluetoothDevice device = mDeviceAdapter.getDevice(position);
                        if (device == null) {
                            return;
                        }
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                            return;
                        }
                        BluetoothUtil.unpairDevice(device);
                        mDeviceAdapter.removeDevice(position);
                        mDeviceAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerTheReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
            //mBtAdapter = null;
        }
        unregisterReceiver(mReceiver);
    }

    *//**
     * 初始化已绑定配对设备
     *//*
    private void initBondedDevices() {
        Set<BluetoothDevice> btDevices = mBtAdapter.getBondedDevices();
        if (btDevices == null) {
            return;
        }
        for (BluetoothDevice device : btDevices) {
            mDeviceAdapter.addDevice(device);
        }
        mDeviceAdapter.notifyDataSetChanged();
    }

    *//**
     * 注册监听器
     *//*
    private void registerTheReceiver() {
        //注册监听器监听设备扫描以及扫描完成
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    }

    *//**
     * 获取控件
     *//*
    private void findViewsByIds() {
        scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
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
        deviceListView = (SwipeMenuListView) findViewById(R.id.list_devices);
        deviceListView.setOnItemClickListener(mDeviceClickListener);
    }

    *//**
     * 查找设备
     *//*
    private void doDiscovery() {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
            scanButton.setText(getString(R.string.bluetooth_scan));
        } else {
            mDeviceAdapter.clear();
            mDeviceAdapter.notifyDataSetChanged();
            mBtAdapter.startDiscovery();
            scanButton.setText(getString(R.string.bluetooth_scanning));
        }
    }

    *//**
     * 设备单机事件事件 点击连接设备
     *//*
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setResultToConnect(position);
        }
    };

    private void setResultToConnect(int position) {
        if (mBtAdapter.isDiscovering())
            mBtAdapter.cancelDiscovery();

        BluetoothDevice device = mDeviceAdapter.getDevice(position);
        String name = device.getName().trim();
        String address = device.getAddress().trim();

        Intent intent = new Intent();
        intent.putExtra(BluetoothConstant.EXTRA_DEVICE_ADDRESS, address);
        intent.putExtra(BluetoothConstant.EXTRA_DEVICE_NAME, name);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    *//**
     * 监听器
     *//*
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (TextUtils.equals(BluetoothDevice.ACTION_FOUND, action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceAdapter.addDevice(device);
                mDeviceAdapter.notifyDataSetChanged();
            } else if (TextUtils.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED, action)) {
                scanButton.setText(getString(R.string.bluetooth_scan));
            }
        }
    };

    *//**
     * 蓝牙设备的展示列表适配器类
     *//*
    private class DeviceAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//用于布局
        private ArrayList<BluetoothDevice> devices;//数据源
        private int BONDED_COLOR;

        public DeviceAdapter(Context context) {
            mInflater = LayoutInflater.from(context);//取得布局器
            devices = new ArrayList<>();//初始化
            BONDED_COLOR = Color.argb(255, 37, 155, 36);
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.device_name, null);

                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BluetoothDevice device = devices.get(position);
            viewHolder.deviceName.setText(device.getName());
            viewHolder.deviceAddress.setText(device.getAddress());
            int state = device.getBondState();
            if (BluetoothDevice.BOND_BONDED == state) {
                viewHolder.deviceName.setTextColor(BONDED_COLOR);
                viewHolder.deviceAddress.setTextColor(BONDED_COLOR);
            }
            return convertView;
        }

        *//**
         * 控件类
         *//*
        private class ViewHolder {
            public TextView deviceName;
            public TextView deviceAddress;
        }

        public void addDevice(BluetoothDevice device) {
            if (!devices.contains(device)) {
                this.devices.add(device);
            }
        }

        public void removeDevice(int position) {
            devices.remove(position);
        }

        public BluetoothDevice getDevice(int position) {
            return devices.get(position);
        }

        public void clear() {
            devices.clear();
        }
    }

    public void back(View view) {
        this.finish();
    }*/
}
