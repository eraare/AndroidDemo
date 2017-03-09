package com.guohua.mlight.common.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.guohua.mlight.model.bean.Word;
import com.guohua.mlight.view.fragment.GroupFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leo on 2016/5/19.
 */
public final class EverydayWords extends AsyncTask<Void, Integer, String> {
    private Context context;

    public EverydayWords(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = request(httpUrl, httpArg);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
        Word word = parseWord(s);
        if(s == null || TextUtils.equals(s, "")){
            return;
        }
        String string = word.getTaici() + "-" + word.getSource();
        sendWordBroadcast(string);
    }

    private static final String httpUrl = "http://apis.baidu.com/acman/zhaiyanapi/tcrand";
    private static final String httpArg = "fangfa=json";

    /**
     * @param httpUrl :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    private String request(String httpUrl, String httpArg) {
        BufferedReader reader;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "ff0ec139e614f04fc398706509c8aa97");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Word parseWord(String json) {
        Word word = new Word();

        if(json == null || json.equals("")){
            return new Word("001", "Hello World!", "Hello World!", "Hello World!", "Hello World!", "Hello World!");
        }

        JSONObject object = null;
        try {
            object = new JSONObject(json);
            word.setTaici(object.getString("taici"));
            word.setSource(object.getString("source"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return word;
    }

    private void sendWordBroadcast(String string) {
        if (string == null || TextUtils.equals("", string)) {
            return;
        }
        if (context == null) {
            return;
        }

        Intent intent = new Intent(GroupFragment.ACTION_RECEIVED_WORDS);
        intent.putExtra(GroupFragment.KEY_EXTRA_WORDS, string);
        context.sendBroadcast(intent);
    }
}
