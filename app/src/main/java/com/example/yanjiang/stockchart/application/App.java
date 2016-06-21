package com.example.yanjiang.stockchart.application;

import android.app.Application;

import com.example.yanjiang.stockchart.BuildConfig;
import com.example.yanjiang.stockchart.inject.component.AppComponent;
import com.example.yanjiang.stockchart.inject.component.DaggerAppComponent;
import com.example.yanjiang.stockchart.inject.modules.AppModule;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;


public class App extends Application {
    private static final int SHOW_TIME_MIN = 1000;
    private static App mApp;
    private static EventBus sBus;
    private AppComponent applicationComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }
        initComponent();
        mApp=this;
        sBus = EventBus.getDefault();
    }
    public static App getApp() {
        return mApp;
    }
    private void initComponent() {
        applicationComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        applicationComponent.inject(this);
    }

    public AppComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static EventBus getBus() {
        return sBus;
    }

}
