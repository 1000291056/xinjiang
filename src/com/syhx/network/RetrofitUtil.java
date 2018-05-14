package com.syhx.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.syhx.MainActivity;
import com.syhx.MainApplication;
import com.syhx.dao.ProgressListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/10/24.
 */

public class RetrofitUtil {
    //    private static Retrofit retrofit;
    private static ProgressListener mProgressListener;
    private static final String BASEURL = "112.124.14.5/xjsw/";

    public static Retrofit getRetrofit(Context context) {
        return getRetrofit(context, new RequestParams(MainApplication.BASEURL));
    }


    public static Retrofit getRetrofit(Context context, RequestParams params) {
        String baseUrl = !TextUtils.isEmpty(params.getUrl()) ? params.getUrl() : BASEURL;
        if (!TextUtils.isEmpty(baseUrl) && !baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl).client(getOkHttpClient(context, params))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static class ReceivedCookiesInterceptor implements Interceptor {
        private Context context;

        public ReceivedCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            Response originalResponse = chain.proceed(chain.request());
            //这里获取请求返回的cookie
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                final StringBuffer cookieBuffer = new StringBuffer();
                //最近在学习RxJava,这里用了RxJava的相关API大家可以忽略,用自己逻辑实现即可.大家可以用别的方法保存cookie数据
                Observable.from(originalResponse.headers("Set-Cookie"))
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                String[] cookieArray = s.split(";");
                                return cookieArray[0];
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String cookie) {
                                cookieBuffer.append(cookie).append(";");
                            }
                        });
                SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cookie", cookieBuffer.toString());
                editor.commit();
            }

            return originalResponse;
        }
    }

    public static class AddCookiesInterceptor implements Interceptor {
        private Context context;

        public AddCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            final Request.Builder builder = chain.request().newBuilder();
            SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
//最近在学习RxJava,这里用了RxJava的相关API大家可以忽略,用自己逻辑实现即可
            Observable.just("JSESSIONID=" + MainActivity.jsessionid)
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String cookie) {
                            //添加cookie
                            builder.addHeader("Cookie", cookie);
                        }
                    });
            return chain.proceed(builder.build());
        }
    }

    /**
     * 获取okHttp设置
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient(Context context) {
        return getOkHttpClient(context, null);
    }

    /**
     * 获取okHttp设置
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient(Context context, final RequestParams params) {

        try {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.interceptors().add(new ReceivedCookiesInterceptor(context));
            okHttpClientBuilder.interceptors().add(new AddCookiesInterceptor(context));
            if (params != null && params.getDownLoadBean() != null) {//下载
                okHttpClientBuilder.interceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder().body(
                                new SelfResponseBody(originalResponse.body(), params.getDownLoadBean().getProgressListener()))
                                .build();
                    }
                });
            }

//        okHttpClientBuilder.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                long range = 0;
//                if (!TextUtils.isEmpty(url) && url.contains("fileid")) {//表示下载文件 支持断点继续下载
//                    String temp = "fileid=";
//                    String fileName = null;
//                    String parentParent = Environment.getExternalStorageDirectory() + "/sck/";
//                    if (url.contains(temp)) {
//                        fileName = url.substring(url.lastIndexOf(temp) + temp.length());
//                    }
//                    if (!TextUtils.isEmpty(fileName)) {
//                        File file = new File(parentParent + fileName);
//                        if (file.exists()) {
//                            range = file.length();
//                        }
//                    }
//                }
//                Request request = chain.request()
//                        .newBuilder()
////                        .addInterceptordHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
////                        .addHeader("Acceptept-Encoding", "gzip, deflate")
////                        .addHeader("Connection", "keep-alive")
////                        .addHeader("Accept", "*/*")
//
//                        .addHeader("range", String.valueOf(range))
//                        .build();
//                return chain.proceed(request);
//            }
//
//        });
            okHttpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(20, TimeUnit.SECONDS);
            okHttpClientBuilder.writeTimeout(20, TimeUnit.SECONDS);
            okHttpClientBuilder.retryOnConnectionFailure(true);//错误重连
            return okHttpClientBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
