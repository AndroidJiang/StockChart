package com.example.yanjiang.stockchart.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by yanjiang on 2016/4/20.
 * 恩恩
 */
public interface DownLoadApi {
    /*http://7xrnuc.com1.z0.glb.clouddn.com/out.apatch*/
    @Streaming
    @GET("out.apatch")
    Observable<ResponseBody> getDownApk();

}
