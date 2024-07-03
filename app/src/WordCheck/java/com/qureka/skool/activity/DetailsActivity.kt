package com.qureka.skool.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.R
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.ActivityDetailsBinding
import com.qureka.skool.utils.Utils


class DetailsActivity : BaseActivity(), OnClickListener {

    private lateinit var binding: ActivityDetailsBinding
    private var wordCount: Int = 0
    private var charCount: Int = 0
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        interstitialAdManager = InterstitialAdManager()
        interstitialAdManager.preLoadInterstitialAd(
            this@DetailsActivity, AddUnit.GET_STARTED_SCREEN
        )

        binding.etWordCount.setOnTouchListener(OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        })
        setUI()
        createLengthSpannable(
            getString(R.string.no_of_words) + " ",
            binding.tvNoOfWordHeading,
            this, getString(R.string.no_of_words)
        )

        binding.etWordCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                wordCount(p0.toString())
                charCount = p0.toString().length
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }


    private fun setUI() {
        with(binding) {
            clWordCount.setOnClickListener(this@DetailsActivity)
            clCharCount.setOnClickListener(this@DetailsActivity)
            ivBack.setOnClickListener(this@DetailsActivity)
            clClearEdittext.setOnClickListener(this@DetailsActivity)
        }
    }

    fun wordCount(str: String) {
        val trimmedStr = str.trim()
        wordCount = if (trimmedStr.isEmpty()) 0 else trimmedStr.split("\\s+".toRegex()).size
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.clCharCount -> {
                binding.tvNoOfWordHeading.text = getString(R.string.no_of_char_count)
                setCharCount()
            }

            R.id.clWordCount -> {
                binding.tvNoOfWordHeading.text = getString(R.string.no_of_words)
                setWordCount()
            }

            R.id.clClearEdittext -> {
                clearEditText()
                wordCount = 0
                charCount = 0
                setWordCount()
                setCharCount()
            }

            R.id.ivBack -> {
                showAd()
            }
        }
    }

    private fun clearEditText() {
        binding.etWordCount.setText("")
    }

    private fun setWordCount() {
        createLengthSpannable(
            getString(R.string.no_of_words) + " " + if (wordCount > 0) wordCount.toString() else "0",
            binding.tvNoOfWordHeading,
            this, getString(R.string.no_of_words)
        )
    }

    private fun setCharCount() {
        createLengthSpannable(
            getString(R.string.no_of_char_count) + " " + if (charCount > 0) charCount.toString() else "0",
            binding.tvNoOfWordHeading,
            this, getString(R.string.no_of_char_count)
        )
    }

    private fun createLengthSpannable(
        str: String,
        textView: TextView,
        context: Context,
        displayText: String
    ) {
        try {
            val ss = SpannableString(str)
            val color: Int = ContextCompat.getColor(context, R.color.color_f8e8c5)
            val index = str.indexOf(displayText)
            ss.setSpan(
                ForegroundColorSpan(color),
                index,
                index + displayText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showAd() {
        Utils.showProgBar(this)
        if (interstitialAdManager.mInterstitialAd == null && interstitialAdManager.isAdLoading.value == false) {
            interstitialAdManager.preLoadInterstitialAd(
                this@DetailsActivity,
                AddUnit.GET_STARTED_SCREEN,
            )
            interstitialAdManager.isAdLoading.value = true
        }
        showAdMobAd(
            interstitialAdManager,
            this,
            object : InterstitialAdManager.interstitialAdCompleteListener {
                override fun onNavigateToNext() {
                    Utils.dismissProgress()
                    finish()
                }

                override fun dismissProgressBar() {
                    Utils.dismissProgress()
                }
            })
    }

    override fun onBackPressed() {
        showAd()
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }
}