// IWatcherKing.aidl
package com.guohua.mlight.guard;

// Declare any non-default types here with import statements

interface IWatcherKing {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    //启动服务
    void startService();
    //停止服务
    void stopService();
}
