package com.qureka.skool.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qureka.skool.network.ErrorResponse
import com.qureka.skool.network.ResultWrapper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class UserViewModel : ViewModel() {
    val scopeSuperVisor =
        CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() })
    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    fun showGenericError(response: ResultWrapper.GenericError) {
        response.error?.let {
            _errorResponse.postValue(it)
            it.message?.let { msg ->
                _errorMessage.postValue(msg)
            }
        }
    }

    fun showNetworkError(networkError: ResultWrapper.NetworkError) {
        networkError.message?.let {
            _errorMessage.postValue(it)
        }
    }
}