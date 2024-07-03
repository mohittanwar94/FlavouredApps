package com.qureka.skool.activity

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.qureka.skool.BuildConfig
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.R
import com.qureka.skool.ServerConfig
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.ActivityManageDataPreferenceBinding
import com.qureka.skool.ipchecker.model.ConsentRequest
import com.qureka.skool.ipchecker.viewmodel.IpCheckerViewModel
import com.qureka.skool.utils.AppConstant
import com.qureka.skool.utils.CountyCheckAndGroupAssign
import com.qureka.skool.utils.IS_CONSENT_ALLOW_DENY
import com.qureka.skool.utils.IS_CONSENT_OPT_OUT
import kotlinx.coroutines.launch

class ManageDataPreferenceActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityManageDataPreferenceBinding
    private val ipCheckerViewModel by viewModels<IpCheckerViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageDataPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        setUI()
    }

    private fun setListener() {
        with(binding) {
            ivBtnBack.setOnClickListener(this@ManageDataPreferenceActivity)
            btnOtpOut.setOnClickListener(this@ManageDataPreferenceActivity)
            btnClose.setOnClickListener(this@ManageDataPreferenceActivity)
        }
    }

    private fun setUI() {
        binding.txtWeAreSorryToSee.text =
            String.format(
                getString(R.string.we_re_sorry_to_see_you_go_but),
                getString(R.string.app_name),
                getString(R.string.app_name),
                getString(R.string.app_name)
            )
        setClickableString(
            this.getString(R.string.privacypolicy),
            String.format(
                getString(R.string.we_re_sorry_to_see_you_go_but),
                getString(R.string.app_name),
                getString(R.string.app_name),
                getString(R.string.app_name)
            ),
            binding.txtWeAreSorryToSee
        )
    }

    private fun setClickableString(
        clickableValue: String,
        wholeValue: String,
        yourTextView: TextView,
    ) {
        val spannableString = SpannableString(wholeValue)
        val startIndex = wholeValue.indexOf(clickableValue)
        val endIndex = startIndex + clickableValue.length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color =
                    ContextCompat.getColor(this@ManageDataPreferenceActivity, R.color.color_f8e8c5)
            }

            override fun onClick(p0: View) {
                openPrivacy()
            }

        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        yourTextView.text = spannableString
        yourTextView.movementMethod =
            LinkMovementMethod.getInstance()
    }

    private fun openPrivacy() {
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
            this.startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBtnBack -> {
                this.finish()
            }

            R.id.btnOtpOut -> {
                updateGaiDConsentStatus()
                binding.constraintLayoutOne.isVisible = false
                binding.constraintLayoutTwo.isVisible = true
                binding.btnOtpOut.isVisible = false
                QurekaSkoolApplication.getApplication().isUniqueLaunchForPopUp = true
                if (ServerConfig.groupTwoManagePreferencesApk.not())
                    QurekaSkoolApplication.getApplication().canShowAd = false
                preferences.setBoolean(IS_CONSENT_ALLOW_DENY, false)
                preferences.setBoolean(IS_CONSENT_OPT_OUT, true)
                AppConstant.stopFirebaseAnalytics(this)
                AppConstant.callSingularStopSdk(this)
            }

            R.id.btnClose -> binding.ivBtnBack.performClick()
        }
    }

    private fun updateGaiDConsentStatus() {
        preferences.setConsentStatus(false)
        lifecycleScope.launch {
            val gaId = preferences.getString(AppConstant.PreferenceKey.GAID)
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
}