package com.example.firebasechat.model

data class NotificationResponse(
    val canonical_ids: Int,
    val failure: Int,
    val multicast_id: Long,
    val results: Result,
    val success: Int
)
{
    data class Result(
        val message_id: String
    )
}