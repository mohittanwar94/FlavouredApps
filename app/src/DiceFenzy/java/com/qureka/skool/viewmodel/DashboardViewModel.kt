package com.qureka.skool.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    private val _isReloadAd = MutableLiveData(true)
    val isReloadAd: LiveData<Boolean> = _isReloadAd
    private val _isManageDataPreferenceButtonVisible = MutableLiveData(false)
    val isManageDataPreferenceButtonVisible: LiveData<Boolean> =
        _isManageDataPreferenceButtonVisible
    private val _canShowAds =
        MutableLiveData(true)
    val canShowAds: LiveData<Boolean> = _canShowAds

    fun updateCanShowAds(canShowAd: Boolean) {
        _canShowAds.value = canShowAd
    }

    fun isReloadAd(isReload: Boolean) {
        _isReloadAd.value = isReload
    }

    fun isManageDataPreferenceButtonVisible(manageDataPrefVisible: Boolean) {
        _isManageDataPreferenceButtonVisible.value = manageDataPrefVisible

    }
}