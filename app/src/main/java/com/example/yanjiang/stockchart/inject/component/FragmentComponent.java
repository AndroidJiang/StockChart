package com.example.yanjiang.stockchart.inject.component;

import android.app.Activity;

import com.example.yanjiang.stockchart.inject.modules.FragmentModule;
import com.example.yanjiang.stockchart.inject.others.PerFragment;

import dagger.Component;

@PerFragment
@Component(modules = FragmentModule.class, dependencies = AppComponent.class)
public interface FragmentComponent {

    Activity getActivity();

  //  void inject(BaseFragment mBaseFragment);


}
