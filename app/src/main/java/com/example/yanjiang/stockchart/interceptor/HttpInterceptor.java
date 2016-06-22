package com.example.yanjiang.stockchart.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.IOException;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by yanjiang on 2016/3/21.
 */
public class HttpInterceptor implements Interceptor {
    private static String mBoundry;
    private static final int BOUNDARY_LENGTH = 32;
    private Context context;
    private static final String BOUNDARY_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

    public HttpInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(request);

    }

    private Response addCookie(Chain chain) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", "");
        Request request = chain.request();
        Response response;
        if (!"".equals(cookie)) {
            Request compressedRequest = request.newBuilder()
                    .header("Content-type","application/x-www-form-urlencoded; charset=UTF-8")
                    .header("cookie", cookie.substring(0,cookie.length()-1))
                    .build();

             response = chain.proceed(compressedRequest);
        }else{
             response = chain.proceed(request);
        }
        return response;
    }

    private static String setBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < BOUNDARY_LENGTH; ++i)
            sb.append(BOUNDARY_ALPHABET.charAt(random.nextInt(BOUNDARY_ALPHABET.length())));
        return sb.toString();
    }

    public String getBoundary() {
        return mBoundry;
    }

    @Nullable
    private Response getBase64Response(Response response) throws IOException {
        if (response.body() != null) {

            String bodyString = response.body().string();
            /*解码*/
            String pp = new String(
                    Base64.decode(bodyString, Base64.DEFAULT));
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), pp))
                    .build();
        }
        return null;
    }
//应该可以转码，不过得限制requestBody不能是多参类型，否则会附带其他的一些参数，编码不出想要的结果
  /*  private RequestBody encode(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                Log.e("yan","type"+body.contentType());
                return MediaType.parse("text/plain");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                byte[] encoded = Base64.encode(buffer.readByteArray(), Base64.DEFAULT);
                sink.write(encoded);
                buffer.close();
                sink.close();
            }
        };
    }
    public static String bodyToString(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }*/
}
