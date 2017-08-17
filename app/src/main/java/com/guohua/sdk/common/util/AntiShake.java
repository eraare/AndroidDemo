package com.guohua.sdk.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Leo
 * @version 1
 * @since 2017-04-21
 */
public final class AntiShake {
    private static final int DEFAULT_CLICK_TIME = 20;
    /*缓存点击事件*/
    private Map<String, ClickInfo> mClickInfos;

    public AntiShake() {
        mClickInfos = new HashMap<>();
    }

    /**
     * 防抖检测
     *
     * @param object
     * @return
     */
    public boolean check(Object object) {
        return check(object, DEFAULT_CLICK_TIME);
    }

    /**
     * 防抖检测
     *
     * @param object
     * @param time
     * @return
     */
    public boolean check(Object object, long time) {
        String key = object.toString();
        ClickInfo clickInfo = mClickInfos.get(key);
        if (clickInfo == null) {
            clickInfo = new ClickInfo(key, time);
            mClickInfos.put(key, clickInfo);
        }
        return clickInfo.check();
    }

    /*点击事件*/
    private class ClickInfo {
        private String method;
        private long lastTime;
        private long clickTime;

        public ClickInfo(String method, long time) {
            this.lastTime = 0;
            this.method = method;
            this.clickTime = time;
        }

        public String getMethod() {
            return this.method;
        }

        public boolean check() {
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastTime;
            if (timeDifference > clickTime) {
                lastTime = currentTime;
                return true;
            }
            return false;
        }
    }
}
