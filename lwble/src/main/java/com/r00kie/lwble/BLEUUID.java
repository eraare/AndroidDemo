package com.r00kie.lwble;

import java.util.UUID;

/*此文档需要进行外部配置才能适配所有的蓝牙设备*/
/**
 * @file BLEUUID.java
 * @author Leo
 * @version 1
 * @detail BLE连接需要的UUID，不同设备不一样，需要配置
 * @since 2016/12/29 8:54
 */

/**
 * 文件名：BLEUUID.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/29 8:54
 * 描  述：BLE连接需要的UUID，不同设备不一样，需要配置
 */
public final class BLEUUID {
    //服务所用的UUID
    public static final UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    //特征所用的UUID
    public static final UUID UUID_CHARACTERISTIC = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    //描述所用的UUID
    public static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
