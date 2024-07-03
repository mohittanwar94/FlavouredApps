package com.qureka.skool

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.qureka.skool.utils.Utils
import com.qureka.skool.utils.Utils.getAdRequest
import com.singular.sdk.Singular
import com.singular.sdk.SingularAdData

class InterstitialAdManager {
    interface interstitialAdCompleteListener {
        fun onNavigateToNext()
        fun dismissProgressBar()
    }

    private val LOG_TAG = "InterstitialAdManager";
    val isAdLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    var mInterstitialAd: InterstitialAd? = null
    var rewardedInterstitialAd: RewardedInterstitialAd? = null

    companion object {
        var isAdClicked = false
        var mRewardAdded: Boolean? = false
    }

    interface OnShowRewardedAdCompleteListener {
        fun onShowAdDismiss()
        fun onShowAdError()
        fun onAdShowing()
    }

    fun preLoadInterstitialAd(
        context: Activity,
        adUnitId: String,
    ) {
        if (QurekaSkoolApplication.getApplication().canShowAd.not()) {
            Utils.dismissProgress()
            mInterstitialAd = null
            isAdLoading.postValue(false)
            return
        }
        isAdLoading.postValue(true)
        InterstitialAd.load(
            context,
            adUnitId,
            getAdRequest(context),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Utils.dismissProgress()
                    Log.d(LOG_TAG, adError.toString())
                    mInterstitialAd = null
                    isAdLoading.postValue(false)
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    Utils.dismissProgress()
                    Log.d(LOG_TAG, "Ad was loaded.")
                    mInterstitialAd = p0
                    isAdLoading.postValue(false)
                }
            })
    }

    fun loadInterstitialAd(
        context: Activity,
        adUnitId: String,
        onShowAdCompleteListener: AppOpenAdManager.OnShowAdCompleteListener?,
    ) {
        if (QurekaSkoolApplication.getApplication().canShowAd.not()) {
            Utils.dismissProgress()
            onShowAdCompleteListener?.onShowAdComplete()
            mInterstitialAd = null
            isAdLoading.postValue(false)
            return
        }
        InterstitialAd.load(
            context,
            adUnitId,
            getAdRequest(context),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Utils.dismissProgress()
                    onShowAdCompleteListener?.onShowAdComplete()
                    Log.d(LOG_TAG, adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    Utils.dismissProgress()
                    addPaidEvent(p0, context)
                    Log.d(LOG_TAG, "Ad was loaded.")
                    mInterstitialAd = p0
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                                isAdClicked = true
                                onShowAdCompleteListener?.onAdClick()
                                Log.d(LOG_TAG, "Ad was clicked.====$isAdClicked")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                Log.d(LOG_TAG, "Ad dismissed fullscreen content.====$isAdClicked")
                                mInterstitialAd = null
                                if (isAdClicked.not()) {
                                    onShowAdCompleteListener?.onShowAdComplete()
                                }
                                isAdClicked = false
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when ad fails to show.
                                Log.e(LOG_TAG, "Ad failed to show fullscreen content.")
                                mInterstitialAd = null
                                onShowAdCompleteListener?.onShowAdComplete()
                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(LOG_TAG, "Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(LOG_TAG, "Ad showed fullscreen content.")
                            }
                        }
                    mInterstitialAd?.show(context)
                }
            })
    }

    private fun addPaidEvent(appOpenAd: InterstitialAd, context: Context) {
        appOpenAd.setOnPaidEventListener { adValue ->
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

    private fun addPaidEventRewared(appOpenAd: RewardedInterstitialAd, context: Context) {
        appOpenAd.setOnPaidEventListener { adValue ->
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


    fun loadRewardedInterstitialAd(
        context: Activity,
        adUnitId: String,
        onShowAdCompleteListener: AppOpenAdManager.OnShowAdCompleteListener?,
    ) {
        if (QurekaSkoolApplication.getApplication().canShowAd.not()) {
            Utils.dismissProgress()
            onShowAdCompleteListener?.onShowAdComplete()
            mInterstitialAd = null
            isAdLoading.postValue(false)
            return
        }
        isAdLoading.postValue(true)
        println("==================loadRewardedInterstitialAd$adUnitId")
        RewardedInterstitialAd.load(context,
            adUnitId,
            getAdRequest(context),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("RewardedAd", adError.toString())
                    rewardedInterstitialAd = null
                    mRewardAdded = true
                    isAdLoading.postValue(false)
                    if (BuildConfig.DEBUG)
                        Utils.showToast(
                            context,
                            "$adUnitId====failed ad load"
                        )
                    onShowAdCompleteListener?.onShowAdComplete()
                }

                override fun onAdLoaded(p0: RewardedInterstitialAd) {
                    Utils.dismissProgress()
                    addPaidEventRewared(p0, context)
                    Log.d(LOG_TAG, "Ad was loaded.")
                    rewardedInterstitialAd = p0
                    rewardedInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                isAdClicked = true
                                onShowAdCompleteListener?.onAdClick()
                                Log.d(LOG_TAG, "Ad was clicked.====$isAdClicked")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                Log.d(LOG_TAG, "Ad dismissed fullscreen content.====$isAdClicked")
                                rewardedInterstitialAd = null
                                if (isAdClicked.not()) {
                                    onShowAdCompleteListener?.onShowAdComplete()
                                }
                                isAdClicked = false
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Log.e(LOG_TAG, "Ad failed to show fullscreen content.")
                                rewardedInterstitialAd = null
                                onShowAdCompleteListener?.onShowAdComplete()
                            }

                            override fun onAdImpression() {
                                Log.d(LOG_TAG, "Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                Log.d(LOG_TAG, "Ad showed fullscreen content.")
                            }
                        }
                    rewardedInterstitialAd?.show(context) {
                        mRewardAdded = true
                    }
                }
            })

    }
}