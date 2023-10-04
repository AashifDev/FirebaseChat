package com.example.firebasechat.network

import com.example.firebasechat.model.NotificationResponse
import okhttp3.RequestBody
import retrofit2.Response

class Repository {
    suspend fun repository(requestBody: RequestBody): Response<NotificationResponse> {
        return Retrofit().loginClient!!.sendNotification(requestBody)
    }
}