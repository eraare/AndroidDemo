package com.guohua.mlight.common.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-06
 * 照相机相关的工具类保存图片和浏览图片
 */
public final class CameraUtils {
    /**
     * 使用系统自带相册查看图片
     *
     * @param context
     * @param path
     */
    public static void viewPictureByPath(Activity context, String path) {
        if (TextUtils.isEmpty(path)) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "image/*");
        context.startActivity(intent);
    }

    /**
     * 保存图片到本地
     *
     * @param source
     */
    public static String saveBitmap2Album(Context context, Bitmap source) {
        ContentResolver contentResolver = context.getContentResolver();
        String content = MediaStore.Images.Media.insertImage(contentResolver, source, "小相机", "Hello World.");
        String path = contentUri2Path(context, content);
        sendUpdateBroadcast(context, path);
        return path;
    }

    /**
     * 将Content Uri解析成File
     *
     * @param content
     * @return
     */
    private static String contentUri2Path(Context context, String content) {
        String path = null; /*返回结果用*/
        Uri uri = Uri.parse(content); /*获取Uri方便解析*/
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, new String[]{"_data"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) { /*取第一个数据*/
            int columnIndex = cursor.getColumnIndexOrThrow("_data");
            path = cursor.getString(columnIndex);
        }
        cursor.close();
        return path;
    }

    /**
     * 发送系统更新广播将图片加载到图库
     *
     * @param path
     */
    private static void sendUpdateBroadcast(Context context, String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(Uri.parse("file://" + path));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        }
        context.sendBroadcast(intent);
    }
}
