package com.example.firebasechat.network

import com.example.firebasechat.model.NotificationResponse
import com.google.errorprone.annotations.Keep
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiInterface {

    @POST("send")
    suspend fun sendNotification(@Body requestBody: RequestBody): Response<NotificationResponse>
}