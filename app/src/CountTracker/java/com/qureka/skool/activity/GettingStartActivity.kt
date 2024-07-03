package com.qureka.skool.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
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
    private val REQUEST_PERMISSIONS_CODE = 101
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

    private fun initUi() {
        with(binding) {
            tvTitle.text = HtmlCompat.fromHtml(
                String.format(
                    getString(R.string.prankpulse),
                    ""
                ), HtmlCompat.FROM_HTML_MODE_COMPACT
            )
//            tvDescription.text = HtmlCompat.fromHtml(
//                String.format(
//                    getString(R.string.get_ready_to_add_a_dash_of_humor_to_your_day_with_our_collection_of_hilarious_prank_sounds),
//                    ""
//                ), HtmlCompat.FROM_HTML_MODE_COMPACT
//            )
            tvHeading.text = HtmlCompat.fromHtml(
                String.format(
                    getString(R.string.splash_app_name),
                    ""
                ), HtmlCompat.FROM_HTML_MODE_COMPACT
            )
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvGetStarted -> binding.constraintBtn.performClick()
            R.id.constraintBtn -> {
                if (binding.checkBox.isChecked.not()) {
                    Utils.showToast(this, getString(R.string.tnc_validate_msg))
                    return
                }
                binding.tvGetStarted.applyClickShrink(shrinkValue = 0.80f, animationDuration = 150L)
                //QurekaSkoolApplication.getApplication().logFirebaseEvent(Event.GET_STARTED)
               showAd()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ServerConfig.groupThreeConsonantPopupApk || ServerConfig.groupFourBanGAApk)
            checkForConsentConditions()
    }

    private fun showAd() {
        Utils.showProgBar(this)
        if (interstitialAdManager.mInterstitialAd == null && interstitialAdManager.isAdLoading.value == false) {
            interstitialAdManager.preLoadInterstitialAd(
                this@GettingStartActivity,
                AddUnit.GET_STARTED_SCREEN,
            )
            interstitialAdManager.isAdLoading.value = true
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

    override fun onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Utils.showToast(this@GettingStartActivity, "Press once again to exit!")
        }
        back_pressed = System.currentTimeMillis()
    }
}
