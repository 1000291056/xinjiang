package com.syhx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/9/14.
 */
public class JPushReceiver extends MyReceiver {
    private static final String REGISTRATION = "cn.jpush.android.intent.REGISTRATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (REGISTRATION.equals(action)) {
            if (bundle.containsKey(JPushInterface.EXTRA_REGISTRATION_ID)){
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                if (TextUtils.isEmpty(MainApplication.registrationid)){
                    MainApplication.registrationid=regId;
                }
                Log.i(JPushReceiver.class.getSimpleName(),"registrationid="+regId);
            }

            Log.i("aa", action);
        }
//        try {
//            Bundle bundle = intent.getExtras();
//            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
//            Set<String> set = bundle.keySet();
//            Iterator<String> iterator = set.iterator();
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                Log.i("aaa", bundle.getString(key));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
