package com.syhx;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wufeifei on 2016/10/24.
 */

public class MainApplication extends Application {
    public static String registrationid = "";
    public static String BASEURL = "";
    public static String cookie = "";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getINSTANCE();
        crashHandler.init(getApplicationContext());
        CrashReport.initCrashReport(getApplicationContext(), "35fa0bcaba", false);
        initJpush();

    }

    private void initJpush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        if (TextUtils.isEmpty(registrationid)) {
            registrationid = JPushInterface.getRegistrationID(this);
            Log.i(MainApplication.class.getSimpleName(), "registrationid=" + registrationid);
        }
    }
}
