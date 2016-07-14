package com.example.yanjiang.stockchart.inject.component;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yanjiang.stockchart.api.ClientApi;
import com.example.yanjiang.stockchart.api.DownLoadApi;
import com.example.yanjiang.stockchart.application.App;
import com.example.yanjiang.stockchart.inject.modules.AppModule;
import com.example.yanjiang.stockchart.inject.modules.ClientApiModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ClientApiModule.class})
public interface AppComponent {
    Context context();

    ClientApi clientApi();

    DownLoadApi downLoadApi();
    SharedPreferences sharedPreferences();

    void inject(App application);
}
