package com.example.yanjiang.stockchart.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.yanjiang.stockchart.api.Constant;
import com.example.yanjiang.stockchart.api.DownLoadApi;
import com.example.yanjiang.stockchart.application.App;
import com.example.yanjiang.stockchart.event.ProgressUpdateEvent;
import com.example.yanjiang.stockchart.inject.component.DaggerServiceComponent;
import com.example.yanjiang.stockchart.inject.component.ServiceComponent;
import com.example.yanjiang.stockchart.inject.modules.ServiceModule;
import com.example.yanjiang.stockchart.rxutils.CommonUtil;
import com.example.yanjiang.stockchart.rxutils.SchedulersCompat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author：ajiang
 * mail：1025065158@qq.com
 * blog：http://blog.csdn.net/qqyanjiang
 */
public class DownLoadService extends IntentService {
    private static final String SERVICE_NAME = DownLoadService.class.getName();
    private ServiceComponent serviceComponent;
    @Inject
    DownLoadApi downLoadApi;
    private Subscription subscription;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownLoadService() {
        super(SERVICE_NAME);
    }

    public DownLoadService(String name) {
        super(name);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getBus().register(this);
        serviceComponent = DaggerServiceComponent.builder()
                .appComponent(((App) getApplication()).getApplicationComponent())
                .serviceModule(new ServiceModule(this))
                .build();
        serviceComponent.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        subscription = downLoadApi.getDownApk()
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<ResponseBody, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(ResponseBody responseBody) {
                        Boolean writtenToDisk = com.example.yanjiang.stockchart.rxutils.FileUtils.writeResponseBodyToDisk(responseBody, getApplicationContext());
                        return Observable.just(writtenToDisk);
                    }
                })
                .compose(SchedulersCompat.<Boolean>applyIoSchedulers())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

//
                      /*  App app = (App) getApplication();
                        app.addPatch();*/
                     /*   try {
                            //String patchPath = CommonUtil.getApatchDownloadPath(getApplicationContext());
                            String patchPath = Constant.EXTERNALPATH + Constant.APATCH_PATH;///storage/emulated/0/out.apatch
                            App.getPatchManager().addPatch(patchPath);
                            //复制且加载补丁成功后，删除下载的补丁
                            File f = new File(patchPath);
                            if (f.exists()) {
                                Log.e("@@@","!!!!有文件");
                                //boolean result = new File(patchPath).delete();
                              *//*  if (!result)
                                    Log.e("@@@", patchPath + " delete fail");*//*
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Boolean responseBody) {
                    }
                });

    }

    /**
     * 停止服务；
     */
    public static void stopTask(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, DownLoadService.class);
            context.stopService(intent);
        }
    }

    /*接收下载文件进度提示*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateProgress(ProgressUpdateEvent progressUpdateEvent) {
        Log.e("yan", progressUpdateEvent.getbytesRead() + "");
    }
}
