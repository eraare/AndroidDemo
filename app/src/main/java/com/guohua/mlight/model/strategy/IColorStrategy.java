package com.guohua.mlight.model.strategy;

/**
 * @author Leo
 * @detail 根据音乐的FFT数据获取颜色
 * @time 2015-11-17
 */
public interface IColorStrategy {
    int getColorByFft(byte[] fft, int size, int color);
}
