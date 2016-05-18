package com.example.yanjiang.stockchart.inject.modules;

import android.content.Context;

import com.example.yanjiang.stockchart.application.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

}
