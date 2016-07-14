package com.example.yanjiang.stockchart.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;


public class ProgressInterceptor implements Interceptor
{
   // private ProgressListener progressListener;


  /*  public ProgressInterceptor(ProgressListener progressListener)
    {
        this.progressListener = progressListener;
    }*/

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body())).build();
    }
}
