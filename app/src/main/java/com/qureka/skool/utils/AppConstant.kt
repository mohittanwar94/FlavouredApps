package com.qureka.skool.utils

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.qureka.skool.BuildConfig
import com.qureka.skool.CredentialHelper
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.ServerConfig
import com.qureka.skool.activity.GLOBAL_CONFIG_RESPONSE
import com.qureka.skool.masterdata.BaseResponse
import com.qureka.skool.sharef.AppPreferenceManager
import com.singular.sdk.Singular
import com.singular.sdk.SingularConfig

const val IS_CONSENT_OPT_OUT = "IS_CONSENT_CLICK_ON_OK"
const val IS_CONSENT_ALLOW_DENY = "IS_CONSENT_ALLOW_DENY"

object AppConstant {

    private var singularInitializedObject: Boolean = false

    fun banGoogleAnalyticsCountry(): Boolean {
        return ServerConfig.groupFourBanGAApk
    }

    fun isGroupTwoApk(): Boolean {
        return ServerConfig.groupTwoManagePreferencesApk
    }

    fun callFirebaseAnalytics(context: Context) {
        if (ServerConfig.groupFourBanGAApk || ServerConfig.groupThreeConsonantPopupApk) {
            if (BuildConfig.DEBUG) {
                Utils.showToast(context, "GA Not Available")
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (BuildConfig.DEBUG) {
                    Utils.showToast(context, "GA initialized")
                }
            }, 1500)
            QurekaSkoolApplication.getApplication().firebaseAnalytics =
                FirebaseAnalytics.getInstance(context)
            QurekaSkoolApplication.getApplication().firebaseAnalytics?.setAnalyticsCollectionEnabled(
                true
            )
        }
    }


    fun stopFirebaseAnalytics(context: Context) {
        if (ServerConfig.groupFourBanGAApk || ServerConfig.groupThreeConsonantPopupApk) {
            if (BuildConfig.DEBUG) {
                Utils.showToast(context, "GA Not Available")
            }
        } else {
            QurekaSkoolApplication.getApplication().firebaseAnalytics?.setAnalyticsCollectionEnabled(
                false
            )
            Handler(Looper.getMainLooper()).postDelayed({
                if (BuildConfig.DEBUG) {
                    Utils.showToast(context, "GA initialized Stop")
                }
            }, 1500)
        }
    }


    fun callSingularSdk(context: Context) {
        if (isOptIN(context)) {
            try {
                (AppPreferenceManager.getInstanced(context).getObject(
                    GLOBAL_CONFIG_RESPONSE, BaseResponse::class.java
                ) as? BaseResponse)?.response?.singularSdkKey?.let { singularSecretKey ->
                    val config = if (BuildConfig.DEBUG) {
                        SingularConfig(
                            CredentialHelper.singularAppKey(), singularSecretKey
                        ).withSessionTimeoutInSec(120).withLoggingEnabled().withLogLevel(3)
                    } else {
                        SingularConfig(
                            CredentialHelper.singularAppKey(), singularSecretKey
                        ).withSessionTimeoutInSec(120)
                    }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(context, "Singular INIT call", Toast.LENGTH_SHORT).show()
                    }
                    singularInitializedObject = Singular.init(context, config)
                    Singular.trackingOptIn()
                    Singular.limitDataSharing(false)
                    if (Singular.isAllTrackingStopped()) {
                        Singular.resumeAllTracking()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun callSingularStopSdk(context: Context) {
        try {
            (AppPreferenceManager.getInstanced(context).getObject(
                GLOBAL_CONFIG_RESPONSE, BaseResponse::class.java
            ) as? BaseResponse)?.response?.singularSdkKey?.let { singularSecretKey ->
                if (BuildConfig.DEBUG) {
                    SingularConfig(
                        CredentialHelper.singularAppKey(), singularSecretKey
                    ).withSessionTimeoutInSec(120).withLoggingEnabled().withLogLevel(3)
                } else {
                    SingularConfig(
                        CredentialHelper.singularAppKey(), singularSecretKey
                    ).withSessionTimeoutInSec(120)
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, "Singular Stop All Track", Toast.LENGTH_SHORT).show()
                }
                if (singularInitializedObject) {
                    Singular.limitDataSharing(true)
                    Singular.stopAllTracking()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showConsentDialog(): Boolean {
        return ServerConfig.groupFourBanGAApk || ServerConfig.groupThreeConsonantPopupApk
    }

    fun isOptIN(context: Context): Boolean {
        if (ServerConfig.groupIndiaApk) return true
        else if (ServerConfig.groupTwoManagePreferencesApk) {
            return AppPreferenceManager.getInstanced(context).getBoolean(IS_CONSENT_OPT_OUT).not()
        } else if (ServerConfig.groupThreeConsonantPopupApk || ServerConfig.groupFourBanGAApk) {
            return AppPreferenceManager.getInstanced(context).getBoolean(IS_CONSENT_ALLOW_DENY)
        }
        return false
    }

    fun getAdRequest(activity: Context): AdRequest {
        val isOptOut = AppPreferenceManager.getInstanced(activity).getBoolean(IS_CONSENT_OPT_OUT)
        val isAllow = AppPreferenceManager.getInstanced(activity).getBoolean(IS_CONSENT_ALLOW_DENY)
        return if (ServerConfig.groupIndiaApk) {
            AdRequest.Builder().build()
        } else {
            if (ServerConfig.groupTwoManagePreferencesApk) {
                val bundleExtra = Bundle()
                if (isOptOut) {
                    bundleExtra.putString("rdp", "1")
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(activity, "rdp=1", Toast.LENGTH_SHORT).show()
                    }
                    AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, bundleExtra).build()
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(activity, "pa=1", Toast.LENGTH_SHORT).show()
                    }
                    AdRequest.Builder().build()
                }
            } else {
                if (ServerConfig.groupFourBanGAApk) {
                    if (isAllow) {
                        val bundleExtra = Bundle()
                        bundleExtra.putString("npa", "1")
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(activity, "npa=1", Toast.LENGTH_SHORT).show()
                        }
                        AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter::class.java, bundleExtra).build()
                    } else {
                        AdRequest.Builder().build()
                    }
                } else {
                    AdRequest.Builder().build()
                }
            }
        }
    }

    interface PreferenceKey {
        companion object {
            var isUniqueLaunchForSended = "isUniqueLaunchForSended"
            val GAID = "googleAdId"
        }
    }

    class AppPreference {
        companion object {
            const val IS_CONSENT_CLICK_ON_OK = "IS_CONSENT_CLICK_ON_OK"
            const val IS_CONSENT_ALLOW_DENY = "IS_CONSENT_ALLOW_DENY"
            const val TOKEN_TYPE = "token_type"
        }
    }
}