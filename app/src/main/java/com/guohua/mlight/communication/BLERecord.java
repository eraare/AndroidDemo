package com.guohua.mlight.communication;

/**
 * @author Leo
 *         #time 2016-08-17
 *         #detail 解析扫描时的scanRecord数据为BLERecord对象 进行匹配是否为制定设备
 */
public class BLERecord {
    // 过滤最新版本魔小灯
    private static final byte[] standardA = new byte[]{2, 1, 6, 3, 2, -16, -1, 15, 9};
    // 过滤全彩照明和之前版本
    private static final byte[] standardB = new byte[]{2, 1, 6, 3, 2, -16, -1, 12, 9};

    /**
     * 判断是不是我们的设备
     *
     * @param scanRecord
     * @return
     */
    public static boolean isOurDevice(byte[] scanRecord) {
        if (scanRecord == null) {
            return false;
        }
        // 判断是不是A类设备
        boolean isStandardA = tell(standardA, scanRecord);
        //判断是不是B类设备
        boolean isStandardB = tell(standardB, scanRecord);
        //如果是A类或B类设备
        if (isStandardA || isStandardB) {
            return true;
        }
        //否则不是我们的设备
        return false;
    }

    /**
     * 匹配算法
     *
     * @param standard
     * @param bytes
     * @return
     */
    private static boolean tell(byte[] standard, byte[] bytes) {
        int length = standard.length;
        // 从后往前匹配
        for (int i = length - 1; i >= 0; i--) {
            if (standard[i] != bytes[i]) {
                return false;
            }
        }
        return true;
    }
}
