package com.example.yanjiang.stockchart.inject.modules;

import android.app.Activity;

import com.example.yanjiang.stockchart.inject.others.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private Activity mActivity;


    public ActivityModule(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return mActivity;
    }
}
