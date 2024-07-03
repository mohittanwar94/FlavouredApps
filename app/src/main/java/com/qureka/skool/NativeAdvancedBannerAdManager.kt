package com.qureka.skool

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.qureka.skool.utils.Utils
import com.qureka.skool.utils.Utils.getAdRequest
import com.singular.sdk.Singular
import com.singular.sdk.SingularAdData

object NativeAdvancedBannerAdManager {
    private const val LOG_TAG = "NativeAdvancedBannerAdManager"

    fun loadNativeAd(
        context: Context,
        adUnitID: String,
        rootView: LinearLayout,
    ) {
        rootView.isVisible = true
        if(QurekaSkoolApplication.getApplication().canShowAd.not()){
            rootView.isVisible = false
            return
        }
        if (BuildConfig.DEBUG)
            println("adUnit Id ==== $adUnitID")
        val layoutInflater = LayoutInflater.from(context)
        val adLoader = AdLoader.Builder(context, adUnitID)
            .forNativeAd { nativeAd ->
                try {
                    val adViewLayout = layoutInflater.inflate(
                        R.layout.native_advanced_medium_ad_layout_intro_quiz,
                        null
                    ) as NativeAdView
                    rootView.gravity = Gravity.CENTER
                    populateUnifiedNativeAdView(nativeAd, adViewLayout)
                    rootView.removeAllViews()
                    rootView.addView(adViewLayout)
                    addPaidEvent(nativeAd, adUnitID, context)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (ee: Exception) {
                    ee.printStackTrace()
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rootView.removeAllViews()
                    rootView.isVisible = false
                    println("$LOG_TAG ==onAdFailedToLoad   ${adError.message}")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    println("$LOG_TAG ====onAdImpression")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    println("$LOG_TAG ====onAdClicked")
                }
            }).withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setRequestCustomMuteThisAd(true)
                    .build()
            )
            .build()
        adLoader.loadAd(getAdRequest(context))
    }


    private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adViewLayout: NativeAdView) {
        // Set the media view.
        adViewLayout.mediaView = adViewLayout.findViewById(R.id.ad_media)

        // Set other ad assets.
        adViewLayout.headlineView = adViewLayout.findViewById(R.id.ad_headline)
        adViewLayout.bodyView = adViewLayout.findViewById(R.id.ad_body)
        adViewLayout.callToActionView = adViewLayout.findViewById(R.id.ad_call_to_action)
        adViewLayout.iconView = adViewLayout.findViewById(R.id.ad_app_icon)
        adViewLayout.priceView = adViewLayout.findViewById(R.id.ad_price)
        adViewLayout.starRatingView = adViewLayout.findViewById(R.id.ad_stars)
        adViewLayout.storeView = adViewLayout.findViewById(R.id.ad_store)
        adViewLayout.advertiserView = adViewLayout.findViewById(R.id.ad_advertiser)
        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adViewLayout.headlineView as TextView).text = nativeAd.headline
        adViewLayout.mediaView?.mediaContent = nativeAd.mediaContent
        if (nativeAd.body == null) {
            adViewLayout.bodyView?.visibility = View.INVISIBLE
        } else {
            adViewLayout.bodyView?.visibility = View.VISIBLE
            (adViewLayout.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adViewLayout.callToActionView?.visibility = View.INVISIBLE
        } else {
            adViewLayout.callToActionView?.visibility = View.VISIBLE
            (adViewLayout.callToActionView as Button).text = nativeAd.callToAction
            /*  (adViewLayout.callToActionView as Button).background = ContextCompat.getDrawable(
                  (adViewLayout.callToActionView as Button).context,
                  R.drawable.button_blue
              )*/
        }
        if (nativeAd.icon == null) {
            adViewLayout.iconView?.visibility = View.GONE
        } else {
            (adViewLayout.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adViewLayout.iconView?.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adViewLayout.priceView?.visibility = View.INVISIBLE
        } else {
            adViewLayout.priceView?.visibility = View.VISIBLE
            (adViewLayout.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adViewLayout.storeView?.visibility = View.INVISIBLE
        } else {
            adViewLayout.storeView?.visibility = View.VISIBLE
            (adViewLayout.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adViewLayout.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adViewLayout.starRatingView as RatingBar).rating =
                (nativeAd.starRating ?: 0).toFloat()
            adViewLayout.starRatingView?.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adViewLayout.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adViewLayout.advertiserView as TextView).text = nativeAd.advertiser
            adViewLayout.advertiserView?.visibility = View.VISIBLE
        }
        adViewLayout.setNativeAd(nativeAd)
    }

    private fun addPaidEvent(appOpenAd: NativeAd, adUnitID: String, context: Context) {
        appOpenAd.setOnPaidEventListener { adValue ->
            val impressionData: AdValue = adValue
            logAppsFlyer(appOpenAd, adValue, context, adUnitID)
            val data = SingularAdData(
                Utils.getAdPlatFormName(context), impressionData.currencyCode,
                impressionData.valueMicros / 1000000.0
            )
            data.withAdUnitId(adUnitID)
                .withNetworkName(appOpenAd.responseInfo?.mediationAdapterClassName)
            Singular.adRevenue(data)
        }
    }

    private fun logAppsFlyer(
        appOpenAd: NativeAd,
        adValue: AdValue,
        context: Context,
        adUnitID: String,
    ) {
        /*val customParams: MutableMap<String, String> = HashMap()
        customParams[Scheme.AD_UNIT] = adUnitID
        AppsFlyerAdRevenue.logAdRevenue(
            appOpenAd.responseInfo?.mediationAdapterClassName ?: Utils.getAdPlatFormName(context),
            MediationNetwork.googleadmob,
            Currency.getInstance(adValue.currencyCode),
            adValue.valueMicros / 1000000.0,
            customParams
        )*/
    }
}