// IBLEService.aidl
package com.guohua.mlight.communication;

// Declare any non-default types here with import statements

interface IBLEService {
    /**
     * 根据地址连接到某个设备
     *
     * @param deviceAddress
     * @return
     */
    boolean connect(String deviceAddress);

    /**
     * 向某个地址发送数据
     *
     * @param deviceAddress
     * @param message
     * @return
     */
    boolean send(String deviceAddress, String message);
    boolean sendByte(String deviceAddress, inout byte[] message);

    /**
     * 连接到某个设备
     *
     * @param deviceAddress
     */
    void disconnect(String deviceAddress);

    /**
     * 判断设备是否已连接
     *
     * @param deviceAddress
     * @return
     */
    boolean isConnected(String deviceAddress);

    /**
     * 关闭所有已连接的设备
     */
    void disconnectAll();

    /**
     * 向所有已连接的设备发送数据
     */
    boolean sendAll(String message);
    boolean sendAllByte(inout byte[] message);
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

}
