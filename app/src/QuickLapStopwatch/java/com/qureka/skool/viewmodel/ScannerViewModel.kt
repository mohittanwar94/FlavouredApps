package com.qureka.skool.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerViewModel :ViewModel() {
    val openBottomSheet =  MutableLiveData(false)
    val scanResult =  MutableLiveData("")
    val isFlashLightAvailable =
        MutableLiveData(false)

    val isRelaunchCamera =
        MutableLiveData(false)

    val isFlashLightOn =
        MutableLiveData(false)
}