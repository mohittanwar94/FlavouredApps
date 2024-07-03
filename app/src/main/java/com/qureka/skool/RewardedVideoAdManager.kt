package com.qureka.skool

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.qureka.skool.utils.Utils
import com.qureka.skool.utils.Utils.getAdRequest
import com.singular.sdk.Singular
import com.singular.sdk.SingularAdData

const val TAG = "RewardedVideoAdManager"

class RewardedVideoAdManager {
    var currentTime = 0L
    var adRequest: AdRequest? = null

    companion object {
        var mRewardedAd: RewardedAd? = null
        var mRewardAdded: Boolean? = false
    }

    interface OnShowAdCompleteListener {
        fun onShowAdDismiss()
        fun onShowAdError()
        fun onAdShowing()
    }

    fun loadRewardedAd(
        context: Activity,
        adUnitId: String,
        onShowAdCompleteListener: OnShowAdCompleteListener?,
    ) {
        if(QurekaSkoolApplication.getApplication().canShowAd.not()){
            mRewardAdded=true
            onShowAdCompleteListener?.onShowAdDismiss()
            return
        }
        currentTime = System.currentTimeMillis()
        mRewardAdded = false
        adRequest = getAdRequest(context)
        RewardedAd.load(context, adUnitId, adRequest!!, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
                mRewardAdded = true
                onShowAdCompleteListener?.onShowAdDismiss()
                Log.d(TAG, "Ad was failed loaded.${adError.toString()}")
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
                addPaidEvent(rewardedAd, context)
                /*if (BuildConfig.DEBUG)
                    Utils.showToast(
                        context,
                        adUnitId
                    )*/
                mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        onShowAdCompleteListener?.onAdShowing()
                        Log.d(TAG, "Ad was onAdClicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        Log.d(TAG, "Ad was onAdDismissedFullScreenContent.")
                        onShowAdCompleteListener?.onShowAdDismiss()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        onShowAdCompleteListener?.onShowAdError()
                        Log.d(TAG, "Ad was onAdFailedToShowFullScreenContent.")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        onShowAdCompleteListener?.onAdShowing()
                        Log.d(TAG, "Ad was onAdImpression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        onShowAdCompleteListener?.onAdShowing()
                        Log.d(TAG, "Ad was onAdShowedFullScreenContent.")
                    }
                }
                mRewardedAd?.show(context) {
                    Log.d(TAG, "mRewardAdded")
                    mRewardAdded = true
                }
            }
        })
    }

    private fun loadAdxRewarded(
        context: Activity,
        adUnitId: String,
        onShowAdCompleteListener: OnShowAdCompleteListener?,
    ) {
        if(QurekaSkoolApplication.getApplication().canShowAd.not()){
            mRewardAdded=true
            onShowAdCompleteListener?.onShowAdDismiss()
            return
        }
        currentTime = System.currentTimeMillis()
        //val adRequest = AdManagerAdRequest.Builder().build()
        adRequest = getAdRequest(context)
        /* when {
             AppConstant.usAndCanadaCountry(AppPreferenceManager.getInstanced(context).getString(AppConstant.PreferenceKey.COUNTRY_CODE)) -> {
                 adRequest = getAdRequest(context)
             }
             else -> {
                 adRequest = AdManagerAdRequest.Builder().build()
             }
         }*/
        RewardedAd.load(context, adUnitId, adRequest!!, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
                mRewardAdded = true
                onShowAdCompleteListener?.onShowAdDismiss()
                Log.d(TAG, "Ad was failed loaded.")
                /*if (BuildConfig.DEBUG)
                    Utils.showToast(
                        context,
                        "$adUnitId====failed ad load"
                    )*/
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
                addPaidEvent(rewardedAd, context)
                /*if (BuildConfig.DEBUG)
                    Utils.showToast(
                        context,
                        adUnitId
                    )*/
                mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        onShowAdCompleteListener?.onAdShowing()
                        Log.d(TAG, "Ad was onAdClicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        Log.d(TAG, "Ad was onAdDismissedFullScreenContent.")
                        onShowAdCompleteListener?.onShowAdDismiss()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        onShowAdCompleteListener?.onShowAdError()
                        Log.d(TAG, "Ad was onAdFailedToShowFullScreenContent.")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        onShowAdCompleteListener?.onAdShowing()
                        Log.d(TAG, "Ad was onAdImpression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        onShowAdCompleteListener?.onAdShowing()
                        Log.d(TAG, "Ad was onAdShowedFullScreenContent.")
                    }
                }
                mRewardedAd?.show(context) {
                    Log.d(TAG, "mRewardAdded")
                    mRewardAdded = true
                }
            }
        })
    }

    private fun addPaidEvent(appOpenAd: RewardedAd, context: Context) {
        appOpenAd.setOnPaidEventListener { adValue ->
            logAppsFlyer(appOpenAd, adValue, context)
            val impressionData: AdValue = adValue
            val data = SingularAdData(
                Utils.getAdPlatFormName(context),
                impressionData.currencyCode,
                impressionData.valueMicros / 1000000.0
            )
            data.withAdUnitId(appOpenAd.adUnitId)
                .withNetworkName(appOpenAd.responseInfo.mediationAdapterClassName)
            Singular.adRevenue(data)
        }
    }

    private fun logAppsFlyer(appOpenAd: RewardedAd, adValue: AdValue, context: Context) {

    }
}