package com.guohua.mlight.communication;

/**
 * Created by Leo on 16/6/27.
 */
public final class BLEConstant {
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";
    public static final String EXTRA_DEVICE_LIST = "extra_device_list";
    public static final int REQUEST_DEVICE_SCAN = 0;

    public static final String EXTRA_RECEIVED_DATA = "extra_received_data";

    public static final String ACTION_RECEIVED_VERSION = "action.RECEIVED_VERSION";
    public static final String ACTION_RECEIVED_TEMPERATURE = "action.RECEIVED_TEMPERATURE";
    public static final String ACTION_RECEIVED_VOLTAGE = "action.RECEIVED_VOLTAGE";
    public static final String ACTION_RECEIVED_SELFIE = "action.RECEIVED_SELFIE";

    public static final String ACTION_BLE_CONNECTED = "action.BLE_CONNECTED";
    public static final String ACTION_BLE_DISCONNECTED = "action_BLE_DISCONNECTED";
    public static final String ACTION_BLE_CONNECTING = "action_BLE_CONNECTING";
}
