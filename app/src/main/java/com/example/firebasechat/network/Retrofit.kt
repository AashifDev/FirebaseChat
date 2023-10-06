package com.example.firebasechat.network

import com.example.firebasechat.utils.Constant
import com.example.firebasechat.utils.Constant.SERVER_KEY
import com.google.logging.type.HttpRequest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class Retrofit {

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


    inner class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest: Request = chain.request()

            // Add your custom header here
            val newRequest: Request = originalRequest.newBuilder()
                .header("Cache-Control", "no-cache")
                .header("Postman-Token", "calculated when request is sent")
                .header("Content-Type", "application/json")
                .header("Content-Length", "calculated when request is sent")
                .header("Host", "calculated when request is sent")
                .header("User-Agent", "PostmanRuntime/7.33.0")
                .header("User-Agent", "PostmanRuntime/7.33.0")
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Authorization", "key=$SERVER_KEY")
                .build()

            return chain.proceed(newRequest)
        }
    }

    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .build()
    }


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