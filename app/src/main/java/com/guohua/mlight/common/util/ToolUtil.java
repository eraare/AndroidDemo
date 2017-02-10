package com.guohua.mlight.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 公共的未归类的工具方法集
 */
public final class ToolUtil {
    /**
     * 使用MD5加密
     *
     * @param origin
     * @return
     */
    public static String encryptByMD5(String origin) {
        /*MD5加密工具*/
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return origin;
        }
        md5.update(origin.getBytes());
        byte[] bytes = md5.digest();
        /*加密得到的字节数据转成16进制字符串*/
        String result = byteArray2HexString(bytes);
        /*统一成16位大写的MD5密文*/
        return result.substring(8, 24).toLowerCase();
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes
     * @return
     */
    public static String byteArray2HexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp;
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            /*byte转int后转16进制字符串*/
            temp = Integer.toHexString(bytes[i] & 0xFF);
            /*如果为1位则补0*/
            if (temp.length() == 1) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }
}
