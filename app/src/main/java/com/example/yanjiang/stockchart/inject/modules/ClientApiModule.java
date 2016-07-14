package com.example.yanjiang.stockchart.inject.modules;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.yanjiang.stockchart.BuildConfig;
import com.example.yanjiang.stockchart.api.ClientApi;
import com.example.yanjiang.stockchart.api.Constant;
import com.example.yanjiang.stockchart.api.DownLoadApi;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yanjiang on 2016/3/25.
 */
@Module
public class ClientApiModule {
    @Provides
    @Singleton
    public DownLoadApi provideDownLoadApi(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.HTTP_BASE)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(DownLoadApi.class);
    }
    @Provides
    @Singleton
    public ClientApi provideClientApi(OkHttpClient client, GsonConverterFactory gsonConverterFactory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.MINUTESURL)
                .client(client)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(ClientApi.class);
    }


    @Provides
    @Singleton
    public GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create();
    }


    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Context context, HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return httpLoggingInterceptor;
    }



    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
