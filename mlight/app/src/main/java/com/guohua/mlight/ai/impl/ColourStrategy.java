package com.guohua.mlight.ai.impl;

import android.graphics.Color;

import com.guohua.mlight.ai.IColorStrategy;

/**
 * Created by Leo on 2015/11/17.
 */
public class ColourStrategy implements IColorStrategy {
    @Override
    public int getColorByFft(byte[] fft, int size, int color) {
        int[] model = fft2Model(fft);
        double brightStandard = size * Math.sqrt(2) * 128;//亮度的标准面积
        double sb = 0;//亮度的实际面积
        int i;
        for (i = 1; i < size + 1; i++) {
            sb += model[i];
        }
        double brightness = (sb / brightStandard) * 255;
        //int alpha = (int) (Color.alpha(color) * brightness / 255);
        ///////////////////////////////关闭了白光////////////////////////////////////
        int alpha = 0;//关闭白灯
        int red = (int) (Color.red(color) * brightness / 255);
        int green = (int) (Color.green(color) * brightness / 255);
        int blue = (int) (Color.blue(color) * brightness / 255);
        color = Color.argb(alpha, red, green, blue);
        if (red == 0 && green == 0 && blue == 0) {
            color = Color.argb(alpha, 5, 5, 5);
        }
        return color;
    }

    /**
     * fft转成振幅
     *
     * @param fft
     * @return
     */
    private int[] fft2Model(byte[] fft) {
        byte[] model = new byte[fft.length / 2 + 1];
        int[] unsignedModel = new int[model.length];
        model[0] = (byte) Math.abs(fft[0]);
        for (int i = 2, j = 1; i < fft.length - 1; i += 2, j++) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            unsignedModel[j] = getUnsignedByte(model[j]);
        }
        return unsignedModel;
    }

    /**
     * 转换成无符号
     *
     * @param b
     * @return
     */
    private int getUnsignedByte(byte b) {
        int result = b & 0xff;
        return result;
    }
}
