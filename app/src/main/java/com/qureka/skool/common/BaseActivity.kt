package com.qureka.skool.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.qureka.skool.AddUnit
import com.qureka.skool.BottomSheetConsentDialog
import com.qureka.skool.BuildConfig
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.NativeAdvancedBannerAdManager
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.R
import com.qureka.skool.RewardedVideoAdManager
import com.qureka.skool.ServerConfig
import com.qureka.skool.activity.GLOBAL_CONFIG_RESPONSE
import com.qureka.skool.activity.IS_FIRST_GAME_PLAY
import com.qureka.skool.activity.ManageDataPreferenceActivity
import com.qureka.skool.dialog.DialogGiftBox
import com.qureka.skool.fgp.FirstGamePlayViewModel
import com.qureka.skool.fgp.model.FirstGamePlayRequest
import com.qureka.skool.ipchecker.model.ConsentRequest
import com.qureka.skool.ipchecker.viewmodel.IpCheckerViewModel
import com.qureka.skool.masterdata.BaseResponse
import com.qureka.skool.network.ResultWrapper
import com.qureka.skool.sharef.AppPreferenceManager
import com.qureka.skool.utils.AppConstant
import com.qureka.skool.utils.AppConstant.PreferenceKey.Companion.GAID
import com.qureka.skool.utils.AppConstant.callFirebaseAnalytics
import com.qureka.skool.utils.AppConstant.callSingularSdk
import com.qureka.skool.utils.AppConstant.callSingularStopSdk
import com.qureka.skool.utils.AppConstant.isOptIN
import com.qureka.skool.utils.AppConstant.stopFirebaseAnalytics
import com.qureka.skool.utils.CountyCheckAndGroupAssign
import com.qureka.skool.utils.Utils
import com.singular.sdk.Singular
import com.singular.sdk.SingularAdData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

const val REQUEST_CODE_STORAGE_PERMISSION = 1012
const val LAUNCH_COUNT = "LAUNCH_COUNT"
const val PLAYED_STATE = "PLAYED_STATE"
const val REDIRECTION_WAIT_TIME = 3000L
const val GAME_PLAY_COUNT = "GAME_PLAY_COUNT"

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    private val handlerRedirection = Handler(Looper.getMainLooper())
    private var callBackListener: InterstitialAdManager.interstitialAdCompleteListener? = null
    lateinit var activity: Activity
    public lateinit var consentInformation: ConsentInformation
    val preferences: AppPreferenceManager by lazy {
        AppPreferenceManager.getInstanced(this)
    }
    private val firstGamePlayViewModel by viewModels<FirstGamePlayViewModel>()
    private val ipCheckerViewModel by viewModels<IpCheckerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Thread.setDefaultUncaughtExceptionHandler(TopExceptionHandler(this))
        activity = this
        registerOnBackPressedCallBack()
        if (ServerConfig.groupThreeConsonantPopupApk)
            consentInformation =
                UserMessagingPlatform.getConsentInformation(this)
    }

    fun setStatusAccordingToApk() {
        if (ServerConfig.groupIndiaApk) {
            callSingularSdk(this)
            callFirebaseAnalytics(this)
            QurekaSkoolApplication.getApplication().canShowAd = true
            QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
        } else if (ServerConfig.groupTwoManagePreferencesApk) {
            when (isOptIN(this)) {
                true -> {
                    lifecycleScope.launch {
                        Utils.getGaID(this@BaseActivity)?.let { gaId ->
                            preferences.setString(GAID, gaId)
                        }
                    }
                    QurekaSkoolApplication.getApplication().canShowAd = true
                    QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
                    callSingularSdk(this)
                    callFirebaseAnalytics(this)
                }

                false -> {
                    QurekaSkoolApplication.getApplication().canShowAd = true
                    callSingularStopSdk(this)
                    stopFirebaseAnalytics(this)
                }
            }
        }
    }

    /*override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(DefaultLocaleHelper.getInstance(newBase!!).onAttach())
    }*/
    override fun onDestroy() {
        try {
            handlerRedirection.removeCallbacks(runnable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }


    fun showAdMobAd(
        interstitialAdManager: InterstitialAdManager?,
        viewLifecycleOwner: LifecycleOwner,
        listener: InterstitialAdManager.interstitialAdCompleteListener,
    ) {
        if (interstitialAdManager?.mInterstitialAd != null) {
            showInterstitial(listener, interstitialAdManager)
        } else {
            handlerRedirection.postDelayed(runnable, REDIRECTION_WAIT_TIME)
            interstitialAdManager?.isAdLoading?.observe(
                viewLifecycleOwner
            ) {
                if (it.not()) {
                    handlerRedirection.removeCallbacks(runnable)
                    listener.dismissProgressBar()
                    println("==============interstitialAdManager.mInterstitialAd ==${interstitialAdManager.mInterstitialAd}")
                    if (interstitialAdManager.mInterstitialAd != null) {
                        showInterstitial(listener, interstitialAdManager)
                    } else {
                        listener.onNavigateToNext()
                    }
                    interstitialAdManager.isAdLoading.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }

    private fun showInterstitial(
        listener: InterstitialAdManager.interstitialAdCompleteListener,
        interstitialAdManager: InterstitialAdManager,
    ) {
        interstitialAdManager.mInterstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    listener.dismissProgressBar()
                    // Called when a click is recorded for an ad.
                    InterstitialAdManager.isAdClicked = true
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    interstitialAdManager.mInterstitialAd = null
                    listener.dismissProgressBar()
                    listener.onNavigateToNext()
                    InterstitialAdManager.isAdClicked = false
                }

                override fun onAdFailedToShowFullScreenContent(
                    adError: AdError,
                ) {
                    // Called when ad fails to show.
                    interstitialAdManager.mInterstitialAd = null
                    listener.dismissProgressBar()
                    listener.onNavigateToNext()
                }

                override fun onAdImpression() {
                    interstitialAdManager.mInterstitialAd?.let { addPaidEvent(it, activity) }
                }
            }
        interstitialAdManager.mInterstitialAd?.show(
            activity
        )
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

    @SuppressLint("HardwareIds")
    fun fireFgpEvent() {
        if (isOptIN(this)) {
            (preferences.getObject(
                GLOBAL_CONFIG_RESPONSE, BaseResponse::class.java
            ) as? BaseResponse)?.response?.authenticationKey?.let { authenticationKey ->
                lifecycleScope.launch {
                    preferences.setBoolean(IS_FIRST_GAME_PLAY, true)
                    val gaID = Utils.getGaID(this@BaseActivity)
                    firstGamePlayViewModel.sendFirstGpEvent(
                        preferences,
                        FirstGamePlayRequest().apply {
                            ga_id = gaID
                            package_id = activity.packageName
                            device_id =
                                Settings.Secure.getString(
                                    contentResolver,
                                    Settings.Secure.ANDROID_ID
                                )
                            signature = authenticationKey.sha256()
                        })
                }
            }
        }
    }

    private fun showMessage(networkError: ResultWrapper.NetworkError) {
        networkError.message?.let { message ->
            Utils.showToast(this, message)
        }
    }

    fun getLaunchCount(): Int = preferences.getInt(LAUNCH_COUNT, 1)
    fun updateSessionCount() {
        val lunchCount = preferences.getInt(LAUNCH_COUNT, 0) + 1
        preferences.setInt(LAUNCH_COUNT, lunchCount)
    }

    fun getGameCount(): Int = preferences.getInt(GAME_PLAY_COUNT, 1)
    fun setGameCount() {
        val lunchCount = preferences.getInt(GAME_PLAY_COUNT, 0) + 1
        preferences.setInt(GAME_PLAY_COUNT, lunchCount)
    }


    open fun checkAudioPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                }
            }
        }
        return true
    }


    open fun checkCameraPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                }
            }
        }
        return true
    }

    open fun checkCameraReadWritePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                if (BuildConfig.APPLICATION_ID == getString(R.string.app_44)) {
                    val permissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                    )
                    for (permission in permissions) {
                        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                            permission
                        )
                    }
                } else {
                    val permissions = arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA
                    )
                    for (permission in permissions) {
                        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                            permission
                        )
                    }
                }

                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                if (BuildConfig.APPLICATION_ID == getString(R.string.app_44)) {
                    val permissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                    )
                    for (permission in permissions) {
                        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                            permission
                        )
                    }
                } else {
                    val permissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                    for (permission in permissions) {
                        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                            permission
                        )
                    }
                }

                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                } else {
                    return true
                }
            }
        }
        return true
    }

    open fun checkReadWritePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (notGranted.isNotEmpty()) {
                    requestPermissions(
                        notGranted.toTypedArray(),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                    return false
                }
            }
        }
        return true
    }

    private val runnable = Runnable { callBackListener?.onNavigateToNext() }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    fun loadNativeBannerAd(activity: Activity, adUnit: String, linearLayout: LinearLayout) {
        NativeAdvancedBannerAdManager.loadNativeAd(
            activity,
            adUnit,
            linearLayout
        )
    }

    fun showRvDialog(position: Int, activity: Activity) {
        val dialogGiftBox = DialogGiftBox.newInstance()
        supportFragmentManager.beginTransaction()
            .add(dialogGiftBox, DialogGiftBox::class.simpleName).commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
        dialogGiftBox.setListener(object : DialogGiftBox.OnClickType {
            override fun onClick(type: Int) {
                when (type) {
                    1 -> loadRvAds(position, activity)
                    2 -> dismissRVDialog(position)
                    else -> Unit
                }
            }
        })
    }

    open fun dismissRVDialog(position: Int) {

    }

    open fun onRvAdDismiss(position: Int) {}
    public fun loadRvAds(position: Int, activity: Activity) {
        Utils.showProgBar(this)
        RewardedVideoAdManager().loadRewardedAd(activity,
            adUnitId = AddUnit.UnlockingGame_RV,
            object : RewardedVideoAdManager.OnShowAdCompleteListener {
                override fun onAdShowing() {
                    Utils.dismissProgress()
                }

                override fun onShowAdDismiss() {
                    Utils.dismissProgress()
                    if (RewardedVideoAdManager.mRewardAdded == true) {
                        onRvAdDismiss(position)
                    }
                }

                override fun onShowAdError() {
                    Utils.dismissProgress()
                }
            })
    }

    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val encodedHash = md.digest(toByteArray(StandardCharsets.UTF_8))
        return bytesToHex(encodedHash)
    }

    open fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuilder(2 * hash.size)
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

    private fun checkForFirebaseAndAppsFlyerInitialization() {
        when (AppConstant.showConsentDialog()) {
            true -> if (QurekaSkoolApplication.getApplication().isUniqueLaunchForPopUp.not()) {
                getConsentStatus()
                QurekaSkoolApplication.getApplication().isUniqueLaunchForPopUp = true
            }

            false -> Unit
        }
    }

    fun checkForConsentConditions() {
        if (ServerConfig.groupIndiaApk.not()) {
            callSingularSdk(this)
            if (ServerConfig.groupThreeConsonantPopupApk) {
                checkForFirebaseAndAppsFlyerInitialization()
            } else if (ServerConfig.groupFourBanGAApk) {
                checkForGroupFour()
                callSingularSdk(this)
            }
            if (QurekaSkoolApplication.getApplication().canShowAd) {
                QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
            }
            onShowAd()
        } else {
            QurekaSkoolApplication.getApplication().canShowAd = true
            QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
            callSingularSdk(this)
            callFirebaseAnalytics(this)
            onShowAd()
        }
    }

    private fun checkForGroupFour() {
        if (AppConstant.showConsentDialog()) {
            when (isOptIN(this)) {
                true -> {
                    QurekaSkoolApplication.getApplication().canShowAd = true
                }

                else -> {
                    if (QurekaSkoolApplication.getApplication().isUniqueLaunchForPopUp.not()) {
                        QurekaSkoolApplication.getApplication().isUniqueLaunchForPopUp = true
                        val openActivity = Intent(this, BottomSheetConsentDialog::class.java)
                        openActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(openActivity)
                    }
                }
            }
        }
    }

    open fun onShowAd() {
    }

    fun isManageDataPrefVisible(): Boolean {
        if (ServerConfig.groupIndiaApk.not()) {
            return if (ServerConfig.groupThreeConsonantPopupApk) {
                isPrivacyOptionsRequired()
            } else isOptIN(this)

        }
        return false
    }

    fun showManageDataPref(button: View) {
        if (ServerConfig.groupIndiaApk.not()) {
            button.isVisible = if (ServerConfig.groupThreeConsonantPopupApk) {
                isPrivacyOptionsRequired()
            } else isOptIN(this)
            button.setOnClickListener {
                if (ServerConfig.groupThreeConsonantPopupApk) {
                    UserMessagingPlatform.showPrivacyOptionsForm(
                        this
                    )
                    {
                        it?.let { Utils.showToast(this, it.message ?: "") } ?: kotlin.run {
                            initializeAdMobAds(consentInformation, true)
                            updateDataPreferenceView()
                        }
                    }
                } else {
                    val openActivity = Intent(this, ManageDataPreferenceActivity::class.java)
                    startActivity(openActivity)
                }
            }
        }
    }

    private fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    @SuppressLint("HardwareIds")
    private fun getConsentStatus() {
        Utils.showProgBar(this@BaseActivity)
        val androidId: String =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(Utils.md5(androidId).uppercase())
            .build()
        val parametersDebug = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .setTagForUnderAgeOfConsent(false)
            .build()
        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()
        consentInformation.requestConsentInfoUpdate(
            this,
            if (BuildConfig.DEBUG) parametersDebug else params,
            {
                val consentStatus = consentInformation.consentStatus
                when (consentStatus) {
                    ConsentInformation.ConsentStatus.REQUIRED, ConsentInformation.ConsentStatus.UNKNOWN -> {
                        loadFormConsent(consentInformation)
                    }

                    ConsentInformation.ConsentStatus.OBTAINED -> {
                        Utils.dismissProgress()
                        updateDataPreferenceView()
                        initializeAdMobAds(consentInformation, false)
                    }

                    ConsentInformation.ConsentStatus.NOT_REQUIRED -> {
                        Utils.dismissProgress()
                        consentNotRequired()
                    }
                }
            },
            { requestConsentError ->
            })
        if (consentInformation.canRequestAds() && isOptIN(this)) {
            QurekaSkoolApplication.getApplication().canShowAd = true
            if (BuildConfig.DEBUG)
                Utils.showToast(this, "Sdk Can Show Ads")
            QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
        } else {
            QurekaSkoolApplication.getApplication().canShowAd = false
        }
    }

    private fun loadFormConsent(consentInformation: ConsentInformation) {
        if (consentInformation.isConsentFormAvailable) {
            preferences
                .setBoolean(AppConstant.AppPreference.IS_CONSENT_ALLOW_DENY, false)
            preferences
                .setBoolean(
                    AppConstant.PreferenceKey.isUniqueLaunchForSended,
                    false
                )
            loadForm(consentInformation)
        } else {
            Utils.dismissProgress()
        }
    }

    private fun consentNotRequired() {
        preferences
            .setBoolean(AppConstant.AppPreference.IS_CONSENT_ALLOW_DENY, true)
        callSingularSdk(this)
        QurekaSkoolApplication.getApplication().canShowAd = true
        preferences
            .setBoolean(
                AppConstant.PreferenceKey.isUniqueLaunchForSended,
                false
            )
        QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
        onShowAd()
        println("Consent======NOT_REQUIRED===")
    }

    private fun loadForm(consentInformation: ConsentInformation) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(
            this
        ) { formError ->
            Utils.dismissProgress()
            formError?.let {
                Utils.showToast(this, formError.message)
            } ?: kotlin.run {
                //consent gathered
                updateDataPreferenceView()
                initializeAdMobAds(consentInformation, true)
            }
        }
    }

    open fun updateDataPreferenceView() {

    }

    fun initializeAdMobAds(
        consentInformation: ConsentInformation,
        isUpdateOnServer: Boolean,
    ) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val purposesConsents = sharedPref.getString("IABTCF_PurposeConsents", "")
        if (purposesConsents.equals("1111001011")) {
            if (isUpdateOnServer) {
                saveConsentStatusWithGaIdOnLocalAndServer()
            }
            preferences
                .setBoolean(AppConstant.PreferenceKey.isUniqueLaunchForSended, false)
            preferences
                .setBoolean(AppConstant.AppPreference.IS_CONSENT_ALLOW_DENY, true)
        } else if (purposesConsents.equals("1011")) {
            if (isUpdateOnServer) {
                saveConsentStatusWithGaIdOnLocalAndServer()
            }
            preferences
                .setBoolean(AppConstant.PreferenceKey.isUniqueLaunchForSended, false)
            preferences
                .setBoolean(AppConstant.AppPreference.IS_CONSENT_ALLOW_DENY, true)
        } else {
            if (preferences.getConsentStatus())
                updateGaiDConsentStatus()
            preferences
                .setBoolean(AppConstant.AppPreference.IS_CONSENT_ALLOW_DENY, false)
        }
        checkSingular(purposesConsents.equals("1111001011") || purposesConsents.equals("1011"))
        if (consentInformation.canRequestAds() && isOptIN(this)) {
            QurekaSkoolApplication.getApplication().canShowAd = true
            if (BuildConfig.DEBUG)
                Utils.showToast(this, "Sdk Can Show Ads")
            QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
            onShowAd()
        } else {
            QurekaSkoolApplication.getApplication().canShowAd = false
            onShowAd()
        }
    }

    @SuppressLint("HardwareIds")
    private fun checkSingular(isAllowed: Boolean) {
        if (isAllowed) {
            callSingularSdk(this)
        } else {
            callSingularStopSdk(this)
        }
    }

    private fun updateGaiDConsentStatus() {
        preferences.setConsentStatus(false)
        lifecycleScope.launch {
            (AppPreferenceManager.getInstanced(this@BaseActivity).getObject(
                GLOBAL_CONFIG_RESPONSE,
                BaseResponse::class.java
            ) as? BaseResponse)?.response?.singularSdkKey
            val gaId = preferences.getString(GAID)
            ipCheckerViewModel.updateConsentStatus(
                ConsentRequest(
                    gaId,
                    BuildConfig.VERSION_NAME,
                    "OPT_OUT",
                    ServerConfig.commonCountryCode,
                    app_id = packageName
                )
            )
        }
    }

    private fun saveConsentStatusWithGaIdOnLocalAndServer() {
        preferences.setConsentStatus(true)
        lifecycleScope.launch {
            val gaId = Utils.getGaID(this@BaseActivity)
            gaId?.let {
                preferences.setString(GAID, gaId)
                ipCheckerViewModel.updateConsentStatus(
                    ConsentRequest(
                        gaId,
                        BuildConfig.VERSION_NAME,
                        "OPT_IN",
                        ServerConfig.commonCountryCode,
                        app_id = packageName
                    )
                )
            }
        }
    }

    fun createPrivacyPolicyAndCookiesPolicySpan(
        str: String,
        textView: TextView,
        context: Context,
    ) {
        try {
            val ss = SpannableString(str)
            val mediumFont: Typeface = when (BuildConfig.APPLICATION_ID) {

                context.getString(R.string.app_29),
                context.getString(R.string.app_30),
                context.getString(R.string.app_31),
                context.getString(R.string.app_38),
                context.getString(R.string.app_33),
                -> {
                    Typeface.createFromAsset(context.assets, "fonts/poppins_bold.ttf")
                }

                context.getString(R.string.app_28) -> {
                    Typeface.createFromAsset(context.assets, "fonts/montserrat_regular.ttf")
                }

                context.getString(R.string.app_26) -> {
                    Typeface.createFromAsset(context.assets, "fonts/canava_grotesk_regular.ttf")
                }

                context.getString(R.string.app_22),
                context.getString(R.string.app_20),
                -> {
                    Typeface.createFromAsset(context.assets, "fonts/montserrat_medium.ttf")
                }

                context.getString(R.string.app_21) -> {
                    Typeface.createFromAsset(context.assets, "fonts/calibri.ttf")
                }

                context.getString(R.string.app_19) -> {
                    Typeface.createFromAsset(
                        context.assets,
                        "fonts/franklin_gothic_urw_boo.ttf"
                    )
                }

                context.getString(R.string.app_16) -> {
                    Typeface.createFromAsset(
                        context.assets,
                        "fonts/archivo_expanded_regular.ttf"
                    )
                }

                context.getString(R.string.app_14) -> {
                    Typeface.createFromAsset(context.assets, "fonts/open_sans_regular.ttf")
                }

                context.getString(R.string.app_6),
                context.getString(R.string.app_8),
                -> {
                    Typeface.createFromAsset(context.assets, "fonts/poppins_regular.ttf")
                }

                else -> {
                    Typeface.createFromAsset(context.assets, "fonts/poppins_medium.ttf")
                }
            }

            val color: Int = when (BuildConfig.APPLICATION_ID) {
                context.getString(R.string.app_5),
                context.getString(R.string.app_10),
                context.getString(R.string.app_11),
                context.getString(R.string.app_12),
                context.getString(R.string.app_13),
                context.getString(R.string.app_15),
                context.getString(R.string.app_16),
                context.getString(R.string.app_21),
                context.getString(R.string.app_23),
                context.getString(R.string.app_25),
                context.getString(R.string.app_26),
                context.getString(R.string.app_28),
                context.getString(R.string.app_29),
                context.getString(R.string.app_30),
                context.getString(R.string.app_31),
                context.getString(R.string.app_33),
                context.getString(R.string.app_42),
                -> {
                    ContextCompat.getColor(context, R.color.white)
                }

                context.getString(R.string.app_1) -> {
                    ContextCompat.getColor(context, R.color.greyish_brown)
                }

                context.getString(R.string.app_2),
                context.getString(R.string.app_17),
                context.getString(R.string.app_38),
                context.getString(R.string.app_39),
                -> {
                    ContextCompat.getColor(context, R.color.black)
                }

                else -> {
                    ContextCompat.getColor(context, R.color.color_tncs)
                }
            }
            val typefaceSpanMediumFontTnc: TypefaceSpan = CustomTypefaceSpan(mediumFont)
            val typefaceSpanMediumFontPrivacy: TypefaceSpan = CustomTypefaceSpan(mediumFont)
            val typefaceSpanMediumFontCookies: TypefaceSpan = CustomTypefaceSpan(mediumFont)
            val privacyClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                                        ServerConfig.PRIVACY_POLICY_URL
                                    } else {
                                        ServerConfig.PRIVACY_POLICY_URL_INTL
                                    }
                                )
                            )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val tncClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                                        ServerConfig.TERM_CONDITION_URL
                                    } else {
                                        ServerConfig.TERM_CONDITION_URL_INTL
                                    }
                                )
                            )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val cookiesClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                                        ServerConfig.COOKIES_URL
                                    } else {
                                        ServerConfig.COOKIES_URL_INTL
                                    }
                                )
                            )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val cookiesPolicy = context.getString(R.string.cookies)
            val indexCookies = str.indexOf(cookiesPolicy)
            ss.setSpan(
                cookiesClick,
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(color),
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                UnderlineSpan(),
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanMediumFontCookies,
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            val privacyPolicy = context.getString(R.string.privacypolicy)
            val index = str.indexOf(privacyPolicy)
            ss.setSpan(
                privacyClick,
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(color),
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                UnderlineSpan(),
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanMediumFontPrivacy,
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            val termsCondition = context.getString(R.string.termsCondition)
            val indexTerms = str.indexOf(termsCondition)
            ss.setSpan(
                tncClick,
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(color),
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                UnderlineSpan(),
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanMediumFontTnc,
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun checkBoxListenerWithCheckChange(view: View, cbTermsPolicy: CheckBox) {
        cbTermsPolicy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                view.background =
                    ContextCompat.getDrawable(
                        this@BaseActivity,
                        R.drawable.get_started_btn
                    )
            } else {
                view.background =
                    ContextCompat.getDrawable(
                        this@BaseActivity,
                        R.drawable.btn_bg_disabled
                    )
            }
        }
    }

    fun View.isClickAble(boolean: Boolean) {
        this.isClickable = boolean
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //showing dialog and then closing the application..
            onBackPressedCustom()
        }
    }

    open fun onBackPressedCustom() {

    }

    private fun registerOnBackPressedCallBack() {
        when (packageName) {
            getString(R.string.app_47),getString(R.string.app_49),getString(R.string.app_50),getString(R.string.app_51),getString(R.string.app_52),getString(R.string.app_53),getString(R.string.app_54),getString(R.string.app_55) ->{
                onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
            }
            else -> Unit
        }
    }
}