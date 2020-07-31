package com.example.hometrainng.retrofit;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpHelper {
    private static Retrofit mRetrofit;

    private Context mContext;
    private String domain;

    //配置请求超时
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(600, TimeUnit.SECONDS)//连接超时时间
            .writeTimeout(600, TimeUnit.SECONDS)//写入数据超时
            .readTimeout(600, TimeUnit.SECONDS)//读取数据超时
            .build();

    //    OkHttpClient client = new OkHttpClient.Builder()
    //            .addInterceptor(new Interceptor() {
    //                @Override
    //                public Response intercept(Chain chain) throws IOException {
    //
    //                    Request request = chain.request()
    //                            .newBuilder()
    //                            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
    //                            .addHeader("Accept-Encoding", "gzip, deflate")
    //                            .addHeader("Connection", "keep-alive")
    //                            .addHeader("Accept", "*/*")
    //                            .addHeader("Cookie", "add cookies here")
    //                            .build();
    //                    return chain.proceed(request);
    //
    //                }
    //
    //
    //            })
    //            .build();

    private HttpHelper() {
//    Log.i("Constants", Constants.DEBUG_URL);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.DEBUG_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())//往retrofit中装一个Gson插件
                .build();
    }

    public static Retrofit getInstance() {
        synchronized (RetrofitManager.class) {
            if (mRetrofit == null) {
                mRetrofit = new HttpHelper().getRetrofit();
            }
        }
        return mRetrofit;
    }


    private Retrofit getRetrofit() {
        return mRetrofit;
    }


    private OkHttpClient getOkHttpClient() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                Log.d("zcb", "OkHttp====Message:" + message)
        );
        loggingInterceptor.setLevel(level);
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        return httpClientBuilder.build();
    }

}
