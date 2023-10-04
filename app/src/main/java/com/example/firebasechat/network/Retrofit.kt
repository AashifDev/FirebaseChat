package com.example.firebasechat.network

import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit {
    val SERVER_KEY = Constant.SERVER_KEY

    var mOkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder().also {
            it.addHeader("Cache-Control", "no-cache")
            it.addHeader("Postman-Token", "calculated when request is sent")
            it.addHeader("Content-Type", "application/json")
            it.addHeader("Content-Length", "calculated when request is sent")
            it.addHeader("Host", "calculated when request is sent")
            it.addHeader("User-Agent", "PostmanRuntime/7.33.0")
            it.addHeader("Accept", "*/*")
            it.addHeader("Accept-Encoding", "gzip, deflate, br")
            it.addHeader("Connection", "keep-alive")
            it.addHeader("Authorization", "key=$SERVER_KEY")
        }.build())
    }.also { client ->
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(logging).build()
    }.build()

    var mRetrofit: ApiInterface? = null

    val loginClient: ApiInterface?
        get() {
            if(mRetrofit == null){
                mRetrofit = Retrofit.Builder()
                    .baseUrl(Constant.FCM_PUSH_URL)
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiInterface::class.java)
            }
            return mRetrofit
        }
}