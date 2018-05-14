package com.syhx.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/10/24.
 */

public interface OnRegisterJpushID {
    @GET("/android/getregistrationid.ado")
    Call<String> saveJpushId(@Query("registrationid") String registrationid,
                             @Query("userid") String userid);
}
