package com.qureka.skool.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailViewModel : ViewModel() {
    private val _isStopped = MutableLiveData(false)
    val isStopped: LiveData<Boolean> = _isStopped
    fun isStopped(isReload: Boolean) {
        _isStopped.value = isReload
    }
}