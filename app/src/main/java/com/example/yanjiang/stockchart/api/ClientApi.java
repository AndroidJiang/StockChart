package com.example.yanjiang.stockchart.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by yanjiang on 2016/3/15.
 * 示例1
 *
 * @GET("Advertisement") Observable<List<TransMainDao>> getWeatherData(@Query("instID") String id);
 * <p/>
 * 示例2
 * @GET("FundPaperTrade/AppUserLogin") Observable<TransDao> getTransData(@QueryMap Map<String,String> map);
 * <p/>
 * 示例3
 * @FormUrlEncoded
 * @POST("/newfind/index_ask") Observable<Response> getDaJia(@Field("page") int page,
 * @Field("pageSize") int size,
 * @Field("tokenMark") long tokenMark,
 * @Field("token") String token
 * );
 * <p/>
 * 示例4
 * @FormUrlEncoded
 * @POST("FundPaperTrade/AppUserLogin") Observable<Response> getTransData(@FieldMap Map<String,String> map);
 */

public interface ClientApi {


    /*分时图url*/
    @GET(Constant.DETAILURL)
    Observable<ResponseBody> getMinutes(@Query("code") String code);

}
