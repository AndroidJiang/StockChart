package com.example.yanjiang.stockchart.inject.modules;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.example.yanjiang.stockchart.inject.others.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {
    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @Provides
    @PerFragment
    public Activity provideActivity() {
        return mFragment.getActivity();
    }
}
