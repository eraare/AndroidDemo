package com.guohua.mlight.model.impl;

import android.content.Context;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.lwble.BLECenter;
import com.guohua.mlight.model.bean.LightInfo;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-21
 * 处理多个设备的操作
 */
public class RxLightService {
    private volatile static RxLightService mService = null;

    public static RxLightService getInstance() {
        if (mService == null) {
            synchronized (LightService.class) {
                if (mService == null) {
                    mService = new RxLightService();
                }
            }
        }
        return mService;
    }

    private LightService mLightService; /*实际操作的服务类*/
    private BLECenter mBleCenter;

    private RxLightService() {
        mLightService = LightService.getInstance();
        mBleCenter = BLECenter.getInstance();
    }

    /**
     * 开启所有的灯
     */
    public void turnOn() {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.turnOn(lightInfo.address);
                    }
                });
    }

    /**
     * 关闭所有的灯
     */
    public void turnOff() {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.turnOff(lightInfo.address);
                    }
                });
    }

    /**
     * 调整所有灯的颜色
     *
     * @param color
     */
    public void adjustColor(final int color) {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.adjustColor(lightInfo.address, color);
                    }
                });
    }

    /**
     * 调节所有灯的亮度
     *
     * @param color
     */
    public void adjustBrightness(final int color) {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.adjustBrightness(lightInfo.address, color);
                    }
                });
    }

    /**
     * 对所有灯进行预置灯色操作
     */
    public void presetColor() {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.presetColor(lightInfo.address);
                    }
                });
    }

    /**
     * 延迟关闭所有灯
     *
     * @param time
     */
    public void delayOff(final int time) {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.delayOff(lightInfo.address, time);
                    }
                });
    }

    /**
     * 更改所有灯的密码
     *
     * @param oldPwd
     * @param newPwd
     */
    public void password(final String oldPwd, final String newPwd) {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        Observable.from(AppContext.getInstance().lights)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(new Action1<LightInfo>() {
                                    @Override
                                    public void call(LightInfo lightInfo) {
                                        mLightService.password(lightInfo.address, oldPwd, newPwd);
                                    }
                                });
                    }
                });
    }

    /**
     * 对所有灯进行重命名
     *
     * @param name
     */
    public void name(final String name) {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        lightInfo.name = name;
                        mLightService.name(lightInfo.address, name);
                    }
                });
    }

    /**
     * 连接所有设备
     *
     * @param context
     * @param isAutoConnect
     */
    public void connect(final Context context, final boolean isAutoConnect) {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.connect(context, lightInfo.address, isAutoConnect);
                    }
                });
    }

    /**
     * 对所有灯进行炫彩渐变开启
     */
    public void musicOn() {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.musicOn(lightInfo.address);
                    }
                });
    }

    /**
     * 对所有灯进行炫彩渐变关闭
     */
    public void musicOff() {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mLightService.musicOff(lightInfo.address);
                    }
                });
    }

    /**
     * 发送数据
     *
     * @param data
     */
    public void send(final byte[] data) {
        Observable.from(AppContext.getInstance().lights)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<LightInfo, Boolean>() {
                    @Override
                    public Boolean call(LightInfo lightInfo) {
                        return lightInfo.select;
                    }
                })
                .subscribe(new Action1<LightInfo>() {
                    @Override
                    public void call(LightInfo lightInfo) {
                        mBleCenter.send(lightInfo.address, data);
                    }
                });
    }
}
