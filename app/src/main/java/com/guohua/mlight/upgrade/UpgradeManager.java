package com.guohua.mlight.upgrade;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.util.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UpgradeManager {
    public static final int WHAT_UPDATE_FALSE = 0;
    public static final int WHAT_UPDATE_TRUE = 1;
    public static final int WHAT_UPDATE_DOWNLOAD = 2;
    public static final int WHAT_UPDATE_FINISH = 3;

    private Handler handler;//传递消息
    private Context context;//检测中用到的上下文
    private String currentVersion;//当前版本
    private int currentVersionCode;//当前版本
    private int serverVersion;//服务器版本的版本号
    private String serverName;//服务器版本的应用名
    private String serverUrl;//服务器版本的URL

    public UpgradeManager(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        currentVersion = getVersionName();
    }

    /**
     * 检查更新
     */
    public void check() {
        try {
            mAsyncTask.execute(Constant.UPGRADE_ADDRESS);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * 对外提供的更新进度条的接口
     */
    public void setProgress() {
        mProgress.setProgress(progress);
    }

    /**
     * 对外提供的安装接口
     */
    public void installApk() {
        installApk(mSavePath, serverName);
    }

    AsyncTask<String, Integer, Void> mAsyncTask = new AsyncTask<String, Integer, Void>() {
        @Override
        protected Void doInBackground(String... params) {
            ParseXmlService parseXmlService = new ParseXmlService();
            HashMap<String, String> result = parseXmlService.parseXmlByUrl(params[0]);
            if (result != null && result.size() > 0) {
                serverVersion = Integer.parseInt(result.get("version"));
                serverName = result.get("name");
                serverUrl = result.get("url");
            }
            System.out.println("upgrademanager serverVersion: " + serverVersion + "; " + "serverName: " + serverName + "; " + "serverUrl: " + serverUrl);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (isUpate()) {
                handler.sendEmptyMessage(WHAT_UPDATE_TRUE);
                System.out.println("upgrademanager serverVersion WHAT_UPDATE_TRUE ");
            } else {
                handler.sendEmptyMessage(WHAT_UPDATE_FALSE);
                System.out.println("upgrademanager serverVersion WHAT_UPDATE_FALSE");
            }
        }
    };

    /**
     * 比较当前版本和服务器版本
     *
     * @return
     */
    private boolean isUpate() {
        if (serverVersion == 0)
            return false;

        if (serverVersion > currentVersionCode) {
            return true;
        }
        return false;
    }

    /**
     * 对处提供当前版本的接口
     *
     * @return
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * 获取系统的当前版本名称
     *
     * @return
     */
    private String getVersionName() {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            currentVersionCode = pi.versionCode;
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 开始更新程序 弹出下载对话框
     */
    public void update() {
        showDownloadDialog();
    }

    private String mSavePath;
    private float apkLength;
    private int apkCount;
    private int progress;
    private boolean cancelUpdate = false;
    private ProgressBar mProgress;
    private TextView mShow;//显示进度数据
    private Dialog mDownloadDialog;

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        Builder builder = new Builder(context);
        builder.setTitle(R.string.soft_update_title);
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        mShow = (TextView) v.findViewById(R.id.update_show);
        builder.setView(v);
        builder.setNegativeButton(R.string.soft_update_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        downloadApk();
    }

    private void downloadApk() {
        new DownloadApkThread().start();
    }


    private class DownloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + Constant.CONTENT_DIR;
                    URL url = new URL(serverUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    apkLength = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, serverName);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    apkCount = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numread = is.read(buf);
                        apkCount += numread;
                        progress = (int) (((float) apkCount / apkLength) * 100);
                        handler.sendEmptyMessage(WHAT_UPDATE_DOWNLOAD);
                        if (numread <= 0) {
                            handler.sendEmptyMessage(WHAT_UPDATE_FINISH);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mDownloadDialog.dismiss();
        }
    }

    /**
     * 根据文件路径安装apk文件
     *
     * @param filePath
     * @param fileName
     */
    private void installApk(String filePath, String fileName) {
        File apkFile = new File(filePath, fileName);
        if (!apkFile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}