package com.qureka.skool.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.qureka.skool.AddUnit
import com.qureka.skool.R
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.ActivityDashBoardBinding
import com.qureka.skool.utils.Utils


const val POSITION = "position"

class DashBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashBoardBinding
    private val TIME_DELAY = 2000
    private var back_pressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUi()
        updateSessionCount()
        onShowAd()
        updateDataPreferenceView()
    }

    override fun onShowAd() {
        super.onShowAd()
        loadNativeBannerAd(
            this@DashBoardActivity,
            AddUnit.PP_Dashboard_NativeAdvanced,
            binding.adView
        )
    }

    override fun onResume() {
        super.onResume()
        binding.scrollView.post { binding.scrollView.scrollTo(0, 0) }
        checkForConsentConditions()
        showManageDataPref(binding.layoutIncludeBottom.tvManageDataPref)
    }

    override fun updateDataPreferenceView() {
        showManageDataPref(binding.layoutIncludeBottom.tvManageDataPref)
    }

    override fun onRvAdDismiss(position: Int) {
        if (preferences.getBoolean(IS_FIRST_GAME_PLAY, false)?.not() == true) {
            fireFgpEvent()
        }
        startActivity(
            Intent(activity, DetailsActivity::class.java)
        )
    }

    private fun setUi() {
        binding.layoutIncludeTop.tvTitle.text = HtmlCompat.fromHtml(
            getString(R.string.prankpulse_with_column),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        binding.layoutIncludeBottom.tvFirstPointBullet.text = HtmlCompat.fromHtml(
            getString(R.string.onlyBullet),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        binding.layoutIncludeBottom.tvSecondPointBullet.text = HtmlCompat.fromHtml(
            getString(R.string.onlyBullet),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        binding.layoutIncludeBottom.tvThirdPointBullet.text = HtmlCompat.fromHtml(
            getString(R.string.onlyBullet),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        binding.layoutIncludeBottom.tvFourthPointBullet.text = HtmlCompat.fromHtml(
            getString(R.string.onlyBullet),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        createPrivacyPolicyAndCookiesPolicySpan(
            getString(R.string.tnc_and_cookies_span),
            binding.tvPleaseAgree,
            this@DashBoardActivity
        )

        binding.clTranslateText.setOnClickListener {
            showRvDialog(0, this@DashBoardActivity)
        }

       /* binding.clTranslateCamera.setOnClickListener {
            if (checkCameraReadWritePermissions().not()) {
                checkCameraReadWritePermissions()
            } else
                showRvDialog(1, this@DashBoardActivity)
        }*/
    }

    override fun onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Utils.showToast(this@DashBoardActivity, "Press once again to exit!")
        }
        back_pressed = System.currentTimeMillis()
    }
}