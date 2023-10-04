package com.example.firebasechat.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasechat.model.NotificationResponse
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response

class MyViewModel : ViewModel() {
    val repository = Repository()
    val result: MutableLiveData<Response<NotificationResponse>> = MutableLiveData()
    val liveData: LiveData<Response<NotificationResponse>>
        get() = result

    fun sendNotification(requestBody: RequestBody) = viewModelScope.launch {
        result.value = repository.repository(requestBody)
    }
}