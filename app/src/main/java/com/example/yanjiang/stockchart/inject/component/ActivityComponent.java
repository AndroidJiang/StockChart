package com.example.yanjiang.stockchart.inject.component;

import android.app.Activity;

import com.example.yanjiang.stockchart.BaseActivity;
import com.example.yanjiang.stockchart.inject.modules.ActivityModule;
import com.example.yanjiang.stockchart.inject.others.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivityContext();

    void inject(BaseActivity mBaseActivity);



}
