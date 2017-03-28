package com.guohua.mlight.common.config;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-22
 * 系统使用到的常量值
 */
public final class Constants {
    /*系统使用到的KEY-VALUE的KEY*/
    public static final String KEY_GLOBAL_PASSWORD = "key_global_password";
    public static final String KEY_SHAKE_MODE = "key_shake_mode";
    public static final String KEY_THRESHOLD = "key_threshold";
    public static final String KEY_PERSONAL_FEEL = "key_personal_feel";

    /*系统使用到的默认值DEFAULT*/
    public static final String DEFAULT_GLOBAL_PASSWORD = "0000";

    /*验证码请求时长和间隔*/
    public static final long CAPTCHA_TIMER_DELAY = 60 * 1000;
    public static final long CAPTCHA_TIMER_INTERVAL = 1000;

    /*广点通的广告用到的广告位ID*/
    //public static final String APP_ID = "1105667675";
    //public static final String SPLASH_POS_ID = "1030028134195577";
    public static final String APP_ID = "1101152570";
    public static final String SPLASH_POS_ID = "8863364436303842593";


    /**************************************
     * 旧的
     *****************************/
    /*键值对的键*/
    public static final String KEY_DEVICE_NAME = "device_name";
    public static final String KEY_COLOR = "key_color";
    public static final String KEY_STATUS_MESSAGE = "key_status_message";
    public static final String EXIST_TIMER_OPEN = "exist_timer_open";
    public static final String KEY_TIMER_OPEN = "key_timer_open";
    public static final String KEY_BLUETOOTH_INIT_STATE = "key_bluetooth_init_state";
    public static final String CALL_REMINDER_SHINEMODE = "call_reminder_shinemode";
    public static final String CALL_REMINDER_SHINEMODE_VALUE = "call_reminder_shinemode_value";
    /*网址*/
    public static final String OFFICIAL_WEBSITE = "http://www.guohua-net.cn";
    public static final String FEEDBACK_WEBSITE = "http://www.guohua-net.cn/message.html";
    public static final String UPGRADE_ADDRESS = "http://www.guohua-net.com/magiclamp/mlight-upgrade.xml";
    /**
     * 密码目录改变颜色
     */
    public static final String DEFAULT_PASSWORD = "0000";
    public static final String CONTENT_DIR = "guohua";
    public static final int WHAT_CHANGE_COLOR = 1;

    /**
     * 用到的action
     */
    public static final String ACTION_EXIT = "com.guohua.glight.ACTION_EXIT";
    public static final String ACTION_FIRMWARE_VERSION = "action_FIRMWARE_VERSION";
    public static final String ACTION_OPENLIGHT_TIMER = "action_OPENLIGHT_TIMER";
}
