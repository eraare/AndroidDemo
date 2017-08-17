package com.guohua.sdk.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * @author Leo
 *         #time 2016-09-13
 *         #detail 辅助工具类提供静态方法
 */
public final class ShareUtils {
    private ShareUtils() {
    }

    /**
     * 分享内容
     *
     * @param context
     * @param title
     * @param text
     */
    public static void shareText(Context context, String title, String text, String imagePath) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        // 如果图片不同则同时分享图片
        if (!TextUtils.isEmpty(imagePath)) {
            File file = new File(imagePath);
            if (file != null && file.exists() && file.isFile()) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType("image/*");
            }
        }
        //设置分享列表的标题，并且每次都显示分享列表
        context.startActivity(Intent.createChooser(shareIntent, title));
    }
}
