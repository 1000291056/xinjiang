/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.syhx;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.syhx.network.DownLoadBean;
import com.syhx.network.NetWork;
import com.syhx.network.RetrofitUtil;
import com.syhx.service.OnRegisterJpushID;

import org.apache.cordova.CordovaActivity;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends CordovaActivity {
    private static String userid = "";
    private String cookie = "";
    public static String jsessionid = "";
    public static boolean isForeground = false;
    private IntentFilter mIntentFilter;
//    private BroadCast mBroadCast;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!TextUtils.isEmpty(userid) && !TextUtils.isEmpty(jsessionid)&&!TextUtils.isEmpty(MainApplication.registrationid)) {
                saveJpushInfo();
            } else {
                sendEmptyMessageDelayed(0, 500);
            }

        }
    };

//    public class BroadCast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            mHandler.sendEmptyMessage(0);
//
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set by <content src="index.html" /> in config.xml
        System.out.print("launchUrl:" + launchUrl);
        loadUrl(launchUrl);

//        startActivity(new Intent(this,PdfActivity.class));
//        CrashReport.testJavaCrash();
    }

//    private void checkPerssion() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    public Object onMessage(String id, Object data) {
        if (id.equals("saveInfo")) {
            if (data != null) {
                Class c = data.getClass();
                JSONObject jsonObject = (JSONObject) data;
                try {
                    if (jsonObject.has("userid")) {
                        userid = jsonObject.getString("userid");
                    }
                    if (jsonObject.has("baseUrl")) {
                        MainApplication.BASEURL = jsonObject.getString("baseUrl");
                    }
                    if (jsonObject.has("jsessionid")) {
                        jsessionid = jsonObject.getString("jsessionid");
                    }
                    mHandler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if ("onPageFinished".equals(id)) {
            mHandler.sendEmptyMessage(0);
        } else if ("onPageStarted".equals(id)) {
            Log.i(TAG, "onPageStarted");
            if (!TextUtils.isEmpty(appView.getUrl()) && appView.getUrl().contains("fileid")) {
                appView.stopLoading();
                new NetWork().downLoad(new DownLoadBean(appView.getUrl()), this);
            }

        }

        return super.onMessage(id, data);
    }

    private void saveJpushInfo() {
        Call call = RetrofitUtil.getRetrofit(this).create(OnRegisterJpushID.class).saveJpushId(MainApplication.registrationid, userid);
        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("aa", response.message());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("aa", t.getMessage());
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userid = "";
        MainApplication.registrationid = "";
    }

    private String cookie() {
        return "UserCodeToken=" + appView.getCookieManager().getCookie("UserCodeToken") + ";" + "JSESSIONID=" + appView.getCookieManager().getCookie("JSESSIONID");
    }

    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
}
