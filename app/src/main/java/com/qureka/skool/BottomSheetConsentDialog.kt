package com.qureka.skool

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.BottomSheetConsentDialogNewBinding
import com.qureka.skool.ipchecker.model.ConsentRequest
import com.qureka.skool.ipchecker.viewmodel.IpCheckerViewModel
import com.qureka.skool.sharef.AppPreferenceManager
import com.qureka.skool.utils.AppConstant
import com.qureka.skool.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

const val CLICK_Allow = 1
const val CLICK_DENY = 2
const val CLICK_LINK = 3

class BottomSheetConsentDialog : BaseActivity(), View.OnClickListener {
    private lateinit var _binding: BottomSheetConsentDialogNewBinding
    private val ipCheckerViewModel by viewModels<IpCheckerViewModel>()
    private val appPreference: AppPreferenceManager by lazy {
        AppPreferenceManager.getInstanced(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        _binding = BottomSheetConsentDialogNewBinding.inflate(layoutInflater)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(_binding.root)
        this.setFinishOnTouchOutside(true)
        initUi()

        _binding.rlmore.setOnClickListener {
            if (_binding.rllearnDeatils.isVisible) {
                _binding.rllearnDeatils.visibility = View.GONE
            } else {
                _binding.rllearnDeatils.visibility = View.VISIBLE
                loadUrl(ServerConfig.COOKIES_URL)
            }
        }

        _binding.tvlearnmore.setOnClickListener {
            if (_binding.rllearnDeatils.isVisible) {
                _binding.rllearnDeatils.visibility = View.GONE
            } else {
                _binding.rllearnDeatils.visibility = View.VISIBLE
                loadUrl(ServerConfig.COOKIES_URL_INTL)
            }
        }
    }

    private fun initUi() {
        _binding.allow.setOnClickListener(this)
        _binding.deny.setOnClickListener(this)
        _binding.tvlearnmore.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.allow -> {
                saveConsentStatusWithGaIdOnLocalAndServer()
                QurekaSkoolApplication.getApplication().canShowAd = true
                QurekaSkoolApplication.getApplication().initializeMobileAdsSdk()
                AppPreferenceManager.getInstanced(this)
                    .setBoolean(AppConstant.PreferenceKey.isUniqueLaunchForSended, false)
                AppPreferenceManager.getInstanced(this)
                    .setBoolean(AppConstant.AppPreference.IS_CONSENT_ALLOW_DENY, true)
            }

            R.id.deny -> {
                updateGaiDConsentStatus()
                QurekaSkoolApplication.getApplication().canShowAd = false
                AppPreferenceManager.getInstanced(this)
                    .setBoolean(AppConstant.AppPreference.IS_CONSENT_ALLOW_DENY, false)
                finish()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveConsentStatusWithGaIdOnLocalAndServer() {
        appPreference.setConsentStatus(true)
        lifecycleScope.launch {
            val gaId = Utils.getGaID(this@BottomSheetConsentDialog)
            println("saveConsentStatusWithGaIdOnLocalAndServer==============$gaId")
            gaId?.let {
                appPreference.setString(AppConstant.PreferenceKey.GAID, gaId)
                ipCheckerViewModel.updateConsentStatus(
                    ConsentRequest(
                        gaId,
                        BuildConfig.VERSION_NAME,
                        "OPT_IN",
                        country_code = ServerConfig.commonCountryCode,
                        app_id = packageName
                    )
                )
            }
            finish()
        }
    }

    private fun updateGaiDConsentStatus() {
        if (appPreference.getConsentStatus()) {
            appPreference.setConsentStatus(false)
            val gaId = appPreference.getString(AppConstant.PreferenceKey.GAID)
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

    private fun loadUrl(url: String) {
        _binding.webView.webViewClient = MyWebViewClient()
        _binding.webView.settings.javaScriptEnabled = true
        _binding.webView.loadUrl(url)
    }

    class MyWebViewClient internal constructor() : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            val url: String = request?.url.toString()
            view?.loadUrl(url)
            return true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {

        }
    }

    private var doubleBackToExitPressedOnce = false

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 3000)
    }
}