package com.qureka.skool


class AppOpenAdManager {
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
        fun onAdNotAvailable()
        fun onAdClick()
    }

}