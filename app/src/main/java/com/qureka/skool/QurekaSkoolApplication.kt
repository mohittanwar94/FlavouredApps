package com.qureka.skool

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.getkeepsafe.relinker.ReLinker
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.qureka.skool.ServerConfig.fireBaseEventPreFix
import com.qureka.skool.event.EventLogger
import com.qureka.skool.utils.AppConstant
import com.qureka.skool.utils.CountyCheckAndGroupAssign
import com.qureka.skool.utils.DefaultLocaleHelper
import dagger.hilt.android.HiltAndroidApp
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@HiltAndroidApp
class QurekaSkoolApplication : Application(), EventLogger,
    Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
    var isUniqueLaunchForPopUp = false
    var isCredentialLibLoaded = false
    var isUniqueLaunchForIntro = false
    var firebaseAnalytics: FirebaseAnalytics? = null
    private var currentActivity: Activity? = null
    var isAdMobInitialized: Boolean = false
    var canShowAd: Boolean = false

    init {
       loadNativeLib()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        mapplication = this
        initAd()
        setupActivityListener()
        // for set language
        if (BuildConfig.APPLICATION_ID == getString(R.string.app_8)) {
            CountyCheckAndGroupAssign.detectAndAssignCountry(this, "AU")
            if (ServerConfig.commonCountryCode == "ES" || ServerConfig.commonCountryCode == "MX") {
                DefaultLocaleHelper.getInstance(this).setCurrentLocale("es")
            }
        }
    }

    private fun loadNativeLib() {
        ReLinker.loadLibrary(this, "native-lib", object : ReLinker.LoadListener {
            override fun success() {
                isCredentialLibLoaded = true
            }

            override fun failure(t: Throwable?) {
                t?.printStackTrace()
                isCredentialLibLoaded = false
            }
        })
    }

    @SuppressLint("HardwareIds")
    private fun initAd() {
        if (ServerConfig.groupIndiaApk || AppConstant.isOptIN(this)) {
            initializeMobileAdsSdk()
        }
    }

    @SuppressLint("HardwareIds")
    fun initializeMobileAdsSdk() {
        if (BuildConfig.DEBUG) {
            val androidId: String =
                Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
            val deviceId: String = md5(androidId).uppercase()
            println("==========$deviceId")
            val testDeviceIds: List<String> =
                listOf(deviceId)
            val configuration =
                RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
        }
        isAdMobInitialized = true
        MobileAds.initialize(this) {
        }
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(DefaultLocaleHelper.getInstance(base!!).onAttach())
    }
    fun md5(mdString: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(mdString.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun logFirebaseEvent(event: String) {
        val eventName = (fireBaseEventPreFix + event).uppercase()
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        //println("==**log instance ======${firebaseAnalytics}")
         println("==**logFirebaseEvent======$eventName")
        firebaseAnalytics?.logEvent(eventName, bundle)
    }

    override fun logGAEvents(eventName: String) {
    }

    override fun logApsalarEvent(eventName: String) {
    }

    override fun subscribeToChannel(channelName: String) {
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mapplication: QurekaSkoolApplication? = null
        fun getApplication(): QurekaSkoolApplication {
            return mapplication!!
        }
    }

    private fun setupActivityListener() {
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(
        activity: Activity,
        bundle: Bundle,
    ) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
    }

    /** LifecycleObserver methods  */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
    }
}