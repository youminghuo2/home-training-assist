package com.example.hometrainng.retrofit;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * retrofit 管理器
 * retrofit 就是对okhttp做了一层封装
 */

public class RetrofitManager {

    private static Retrofit mRetrofit;

    private RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new Gson()))//使用Gson作为数据转换器
                .client(getOkHttpClient())
                .baseUrl(Constants.DEBUG_URL)//地址
                .build();

    }

    public static Retrofit getInstance() {
        synchronized (RetrofitManager.class) {
            if (mRetrofit == null) {
                mRetrofit = new RetrofitManager().getRetrofit();
            }
        }
        return mRetrofit;
    }

    private Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 重新定义okHttpClient
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request oldRequest = chain.request();
                    HttpUrl.Builder builder = oldRequest.url()
                            .newBuilder()
                            .host(oldRequest.url().host())
                            .scheme(oldRequest.url().scheme());
                    //                                .addQueryParameter("key", "value");

                    Request request = oldRequest.newBuilder()
                            .method(oldRequest.method(), oldRequest.body())
                            .url(builder.build())
                            .build();

                    return chain.proceed(request);
                })
                //手动创建一个OkHttpClient并设置超时时间
                .connectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //开启OKHttp的日志拦截
                .addInterceptor(new LogInterceptor())
                //                .addInterceptor(new )
                .build();

        return okHttpClient;

    }

    private static class LogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(chain.request());
            okhttp3.MediaType mediaType = response.body().contentType();
            int code = response.code();
            String content = response.body().string();

            if (response.body() != null) {
                ResponseBody body = ResponseBody.create(mediaType, content);
                return response.newBuilder().body(body).build();
            } else {
                return response;
            }
        }
    }
}
