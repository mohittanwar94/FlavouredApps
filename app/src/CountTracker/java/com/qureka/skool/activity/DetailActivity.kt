package com.qureka.skool.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.R
import com.qureka.skool.SoundInfo
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.ActivityDetailBinding
import com.qureka.skool.utils.Utils
import com.qureka.skool.utils.Utils.saveUnlock

class DetailActivity : BaseActivity() {
    private var oldPosition: Int = 0
    private lateinit var binding: ActivityDetailBinding
    private val positionSelected by lazy { intent.getIntExtra(POSITION, 0) }
    private var currentSound: SoundInfo? = null
    private var currentPosition: Int = 0
    private var clickCount = 0
    private val REQUEST_PERMISSIONS_CODE = 101
    private var score = 0;
    private var clickscore = 0
    private var scorePageName = ""

    private lateinit var interstitialAdManagerBackButton: InterstitialAdManager
    private lateinit var interstitialAdManagerNewSession: InterstitialAdManager
    val TAG = "MainActivity"


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        interstitialAdManagerBackButton = InterstitialAdManager()
        interstitialAdManagerBackButton.preLoadInterstitialAd(
            this@DetailActivity, AddUnit.PP_BackButton_Interstitial
        )

        interstitialAdManagerNewSession = InterstitialAdManager()
        interstitialAdManagerNewSession.preLoadInterstitialAd(
            this@DetailActivity, AddUnit.PP_Sound_NewSession_Interstital
        )

        currentPosition = positionSelected
        currentSound = Utils.getSoundData(this@DetailActivity)[currentPosition]
        println("=====sond name===="+currentSound?.nameSound)
        currentSound?.nameSound?.let { preferences.getString(it, "0") }?.let {
            score = it.toInt()
            binding.tvHrs.text = when (currentPosition) {
                3, 4, 6, 8 -> it + "h"
                else -> it
            }
        }
        if (score == 0) {
            binding.tvMinus.isEnabled = false
            binding.ivMinus.isEnabled = false
        }
        if (currentSound?.isUnlock == 1) clickCount = 1
        setUi()
        logFirebaseEvent(currentPosition)
    }

    private fun setUi() {
        with(binding) {
            ivBack.setOnClickListener {
                displayBackPressAd()
            }
            btnNextSound.setOnClickListener {
                playNextSound(false)
            }

            // preferences.setInt(ANIMATION_POSITION, currentPosition)
            // preferences.setInt(ANIMATION_POSITION, -1)
            updateSoundName()
            setImage()
            clMinus.setOnClickListener {
                minusCounter(5)
            }
            clPlus.setOnClickListener {
                plusCounter(5)
            }

            ivMinus.setOnClickListener {
                minusCounter(1)
            }
            ivPlus.setOnClickListener {
                plusCounter(1)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun plusCounter(counter: Int) {
        with(binding) {
            tvMinus.isEnabled = true
            ivMinus.isEnabled = true
            score += counter
            preferences.setString(scorePageName, score.toString())
            tvHrs.text = when (currentPosition) {
                3, 4, 6, 8 -> score.toString() + "h"
                else -> score.toString()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun minusCounter(counter: Int) {
        with(binding) {
            clickscore = (score - counter)
            if (clickscore >= 0) {
                score -= counter
                preferences.setString(scorePageName, score.toString())
                if (score == 0) {
                    tvMinus.isEnabled = false
                    ivMinus.isEnabled = false
                }
                tvHrs.text = when (currentPosition) {
                    3, 4, 6, 8 -> score.toString() + "h"
                    else -> score.toString()
                }
            }
        }
    }

    private fun updateSoundName() {
        scorePageName = currentSound?.nameSound.toString()
        binding.tvTitle.text = currentSound?.nameSound
    }

    private fun playNextSound(isLeftPress: Boolean) {
        oldPosition = currentPosition
        if (isLeftPress) {
            if (currentPosition == 0) currentPosition = 8
            else currentPosition -= 1
        } else {
            if (currentPosition == 8) currentPosition = 0
            else currentPosition++
        }
        currentSound = Utils.getSoundData(this@DetailActivity)[currentPosition]
        if (currentSound?.isUnlock == 1) {
            println("========dialog========$clickCount")
            currentSound?.nameSound?.let { preferences.getString(it, "0") }?.let {
                score = it.toInt()
                binding.tvHrs.text = when (currentPosition) {
                    3, 4, 6, 8 -> it + "h"
                    else -> it
                }
            }
          /*  if (getLaunchCount() > 1 && (clickCount % 3 == 0)) {
                clickCount++
            } else {*/
                clickCount++
                updateSoundName()
                setImage()
          //  }
        } else {
            println("=======dialog else====")
            showRvDialog(currentPosition, this)
        }
    }

    private fun updateSavedValues(currentSound: SoundInfo?) {
        currentSound?.nameSound?.let { preferences.getString(it, "0") }?.let {
            score = it.toInt()
            binding.tvHrs.text = when (currentPosition) {
                3, 4, 6, 8 -> it + "h"
                else -> it
            }
        }
        if (score == 0) {
            binding.tvMinus.isEnabled = false
            binding.ivMinus.isEnabled = false
        }
    }

    override fun dismissRVDialog(position: Int) {
        currentPosition = oldPosition
        currentSound = Utils.getSoundData(this@DetailActivity)[currentPosition]
        updateSavedValues(currentSound)
    }

    private fun logFirebaseEvent(currentPosition: Int) {
        QurekaSkoolApplication.getApplication().logFirebaseEvent(
            when (currentPosition) {
                0 -> "WHISTLE_ACTIVATED"
                1 -> "CLAP_ACTIVATED"
                2 -> "BURPING_ACTIVATED"
                3 -> "SINGING_ACTIVATED"
                4 -> "SHOUTING_ACTIVATED"
                5 -> "SNORING_ACTIVATED"
                6 -> "SNEEZING_ACTIVATED"
                7 -> "LAUGHING_ACTIVATED"
                8 -> "NAME_ACTIVATED"
                else -> "Whistle_Activated"
            }
        )
    }

    override fun onRvAdDismiss(position: Int) {
        saveUnlock(position, this)
        updateSoundName()
        updateSavedValues(currentSound)
        setImage()
    }

    private fun setImage() {
        with(binding) {
            val resID = resources.getIdentifier(
                currentSound?.soundImageName, "drawable", packageName
            )
            if (currentSound?.nameSound == getString(R.string.police_siren)) {
                ivSoundImage.setBackgroundResource(R.drawable.siren)
            } else {
                ivSoundImage.setBackgroundResource(resID)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        displayBackPressAd()
    }

    private fun displayBackPressAd() {
        showAdBackButton()
    }

    private fun showAdBackButton() {
        Utils.showProgBar(this)
        if (interstitialAdManagerBackButton.mInterstitialAd == null && interstitialAdManagerBackButton.isAdLoading.value == false) {
            interstitialAdManagerBackButton.preLoadInterstitialAd(
                this@DetailActivity,
                AddUnit.PP_BackButton_Interstitial,
            )
            interstitialAdManagerBackButton.isAdLoading.value = true
        }
        showAdMobAd(
            interstitialAdManagerBackButton,
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
}