package com.syhx.service;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by wufeifei on 2016/11/18.
 */

public interface DownLoadService {
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
