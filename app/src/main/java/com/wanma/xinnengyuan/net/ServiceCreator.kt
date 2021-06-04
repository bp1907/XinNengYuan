package com.wanma.xinnengyuan.net

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * author: wanma
 * Date: 2021/1/26
 * Description
 */
object ServiceCreator {

    //域名
    private const val SAVE_BASE_URL = "http://isrmadmin.wanmagroup.com/"
//    private const val ADD_BASE_URL = "http://172.17.4.250:8081/" // 测试版
    private const val ADD_BASE_URL = "http://oa.wanmaco.com/" // 正式版

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor{
            val original = it.request()
            val request = original.newBuilder()
                .addHeader("Content-Type", "multipart/form-data")
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "SK:SRM12345678")
                .build()
            it.proceed(request)
        }
            .addInterceptor(HttpHeaderInterceptor())
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SAVE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

    //获取动态代理对象
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)


    class HttpHeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            //配置请求头
            val request = chain.request()
            //从request中获取原有的httpUrl实例
            val oldHttpUrl = request.url()
            //获取request的创建者builder
            val builder = request.newBuilder()
            //从request中获取headers，通过给定的url_name
            val headerValues = request.headers("url_name")
            if(headerValues.size > 0) {
               //如果有指定header,先将配置的header删除，因此header仅用作app和okHttp之间使用
                builder.removeHeader("url_name")
                //匹配获得新的BaseUrl
                val headerValue = headerValues[0]
                var newBaseUrl: HttpUrl?
                newBaseUrl = when(headerValue) {
                    "saveFile" -> HttpUrl.parse(SAVE_BASE_URL)
                    "add" -> HttpUrl.parse(ADD_BASE_URL)
                    else -> oldHttpUrl
                }
                //重新构建新的HttpUrl，修改需要修改的url部分
                val newFullUrl = newBaseUrl?.let {
                    oldHttpUrl.newBuilder()
                            .host(it.host())
                            .port(it.port())
                            .build()
                }
                //重新构建request
                Log.d("url", newFullUrl.toString())
                return chain.proceed(builder.url(newFullUrl).build())
            }
            return chain.proceed(request)
        }

    }
}