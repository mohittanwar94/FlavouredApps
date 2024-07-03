package com.qureka.skool.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.qureka.skool.*
import com.bumptech.glide.Glide
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.ActivitySplashBinding
import com.qureka.skool.masterdata.BaseResponse
import com.qureka.skool.masterdata.GlobalConfigViewModel
import com.qureka.skool.network.StatusCode
import com.qureka.skool.utils.*
import com.singular.sdk.Singular
import com.singular.sdk.SingularConfig
import kotlinx.coroutines.launch

const val IS_FIRST_GAME_PLAY = "IS_FIRST_GAME_PLAY"
const val ANIMATION_POSITION = "ANIMATION_POSITION"
const val GLOBAL_CONFIG_RESPONSE: String = "GLOBAL_CONFIG_RESPONSE"
const val ON_BOARDING: String = "ON_BOARDING"
const val GLOBAL_CONFIG_CALL_TIME = "GLOBAL_CONFIG_CALL_TIME"
const val HOURS_24 = 86400000L

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var _binding: ActivitySplashBinding
    private val globalConfigViewModel by viewModels<GlobalConfigViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        CountyCheckAndGroupAssign.detectAndAssignCountry(this, "AU")
        if (CountyCheckAndGroupAssign.isBannedCountry()) {
            checkForBlockCountry()
            return
        }
        initUi()
        loadImage()
        getGlobalConfig()
        setObserver()
    }

    private fun checkForBlockCountry() {
        val intent = Intent(this, IpCheckActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun initUi() {
        when (QurekaSkoolApplication.getApplication().packageName) {
            getString(R.string.app_02) -> {
                _binding.splashMainConstraintLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        this@SplashActivity,
                        R.color.white
                    )
                )
                _binding.tvSubtitle.isVisible = false
               /* Utils.createGettingStartedSpan(
                    getString(R.string.enjoy_spreading_laughter_and_creating_unforgettable_moments),
                    _binding.tvTitle,
                    this@SplashActivity
                )*/
            }
        }

    }

    private fun getGlobalConfig() {
        (preferences.getObject(
            GLOBAL_CONFIG_RESPONSE, BaseResponse::class.java
        ) as? BaseResponse)?.let {
            if (preferences.getlong(
                    GLOBAL_CONFIG_CALL_TIME, 0L
                ) + HOURS_24 > System.currentTimeMillis()
            ) {
                startOnBoarding()
                setStatusAccordingToApk()
            } else {
                lifecycleScope.launch {
                    globalConfigViewModel.getGlobalConfig()
                }
            }
        } ?: kotlin.run {
            lifecycleScope.launch {
                globalConfigViewModel.getGlobalConfig()
            }
        }
    }

    private fun callSingularSdk() {
        (preferences.getObject(
            GLOBAL_CONFIG_RESPONSE,
            BaseResponse::class.java
        ) as? BaseResponse)?.response?.singularSdkKey?.let { singularSecretKey ->
            val config = if (ServerConfig.appScreenShotDisable) {
                SingularConfig(
                    CredentialHelper.singularAppKey(), singularSecretKey
                ).withSessionTimeoutInSec(120).withLoggingEnabled()
            } else {
                SingularConfig(
                    CredentialHelper.singularAppKey(), singularSecretKey
                ).withSessionTimeoutInSec(120).withLoggingEnabled().withLogLevel(3)
            }
            Singular.init(this, config)
        }
    }

    private fun setObserver() {
        globalConfigViewModel.globalConfigResponse.observe(this) {
            when (it.status) {
                StatusCode.SUCCESS.toString() -> {
                    preferences.putObject(
                        GLOBAL_CONFIG_RESPONSE, it
                    )
                    preferences.setlong(GLOBAL_CONFIG_CALL_TIME, System.currentTimeMillis())
                    setStatusAccordingToApk()
                    startOnBoarding()
                }

                else -> {
                    startOnBoarding()
                    Utils.showToast(this, it.message ?: "Something went wrong")
                }
            }
        }
        globalConfigViewModel.errorResponse.observe(this) {
            it?.let {
                it.status_code?.let { code ->
                    Utils.showToast(this, it.message ?: "Something went wrong")
                }
            }
        }
        globalConfigViewModel.errorMessage.observe(this) {
            it?.let {
                Utils.showToast(this, it)
            }
        }
    }

    private fun startOnBoarding() {
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                startActivity(
                    Intent(
                        this,
                        if (preferences.getBoolean(
                                ON_BOARDING,
                                false
                            ) == true
                        ) DashBoardActivity::class.java
                        else GettingStartActivity::class.java
                    )
                )
                finish()
            }, 1000)
        }
    }

    private fun loadImage() {
        Glide.with(this).load(R.drawable.splash_logo).into(_binding.splashImage)
    }
    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()
    }
}