package com.example.firebasechat.model

data class NotificationRequestBody(
    val notification: Notification,
    val priority: String,
    val to: String
)