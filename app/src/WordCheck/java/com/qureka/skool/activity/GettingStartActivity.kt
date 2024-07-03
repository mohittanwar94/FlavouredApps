package com.qureka.skool.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.muratozturk.click_shrink_effect.applyClickShrink
import com.qureka.skool.AdInfo
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.R
import com.qureka.skool.ServerConfig
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.common.PLAYED_STATE
import com.qureka.skool.databinding.ActivityGettingStartBinding
import com.qureka.skool.utils.Event
import com.qureka.skool.utils.Utils


class GettingStartActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGettingStartBinding
    private val TIME_DELAY = 2000
    private var back_pressed: Long = 0
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGettingStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        interstitialAdManager = InterstitialAdManager()
        interstitialAdManager.preLoadInterstitialAd(
            this@GettingStartActivity, AddUnit.GET_STARTED_SCREEN
        )
        binding.tvGetStarted.applyClickShrink(shrinkValue = 0.80f, animationDuration = 150L)
        initUi()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initUi() {
        with(binding) {
            constraintBtn.setOnClickListener(this@GettingStartActivity)
            binding.tvGetStarted.setOnClickListener(this@GettingStartActivity)
            checkBoxListenerWithCheckChange(constraintBtn,checkBox)
            Utils.createPrivacyPolicySpan(
                getString(R.string.tnc_span),
                binding.tvPleaseAgree,
                this@GettingStartActivity
            )
        }
    }

    override fun checkBoxListenerWithCheckChange(view: View, cbTermsPolicy: CheckBox) {
        cbTermsPolicy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                view.background =
                    ContextCompat.getDrawable(
                        this@GettingStartActivity,
                        R.drawable.start_btn_bg
                    )
            } else {
                view.background =
                    ContextCompat.getDrawable(
                        this@GettingStartActivity,
                        R.drawable.btn_bg_disabled
                    )
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvGetStarted -> binding.constraintBtn.performClick()
            R.id.constraintBtn -> {
                if (binding.checkBox.isChecked.not()) {
                    Utils.showToast(this, getString(R.string.tnc_validate_msg))
                    return
                }
                binding.tvGetStarted.applyClickShrink(shrinkValue = 0.80f, animationDuration = 150L)
               // QurekaSkoolApplication.getApplication().logFirebaseEvent(Event.GET_STARTED)
                showAd()
            }
        }
    }

    private fun showAd() {
        Utils.showProgBar(this)
        if (interstitialAdManager.mInterstitialAd == null && interstitialAdManager.isAdLoading.value == false) {
            interstitialAdManager.preLoadInterstitialAd(
                this@GettingStartActivity,
                AddUnit.GET_STARTED_SCREEN,
            )
            interstitialAdManager.isAdLoading.value=true
        }
        showAdMobAd(
            interstitialAdManager,
            this,
            object : InterstitialAdManager.interstitialAdCompleteListener {
                override fun onNavigateToNext() {
                    preferences.setBoolean(ON_BOARDING, true)
                    Utils.dismissProgress()
                    preferences.putObject(PLAYED_STATE, AdInfo())
                    startActivity(
                        Intent(
                            this@GettingStartActivity,
                            DashBoardActivity::class.java
                        )
                    )
                    finish()
                }

                override fun dismissProgressBar() {
                    Utils.dismissProgress()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        if (ServerConfig.groupThreeConsonantPopupApk || ServerConfig.groupFourBanGAApk)
            checkForConsentConditions()
    }

    /*private fun showAd() {
        Utils.showProgBar(this)
        InterstitialAdManager().loadInterstitialAd(
            this@GettingStartActivity,
            AddUnit.GET_STARTED_SCREEN,
            this
        )
    }*/

    /*override fun onShowAdComplete() {
        preferences.setBoolean(ON_BOARDING, true)
        Utils.dismissProgress()
        preferences.putObject(PLAYED_STATE, AdInfo())
        startActivity(Intent(this, DashBoardActivity::class.java))
        finish()
    }

    override fun onAdNotAvailable() {
        Utils.dismissProgress()
    }

    override fun onAdClick() {

    }*/

    override fun onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Utils.showToast(this@GettingStartActivity, "Press once again to exit!")
        }
        back_pressed = System.currentTimeMillis()
    }
}
