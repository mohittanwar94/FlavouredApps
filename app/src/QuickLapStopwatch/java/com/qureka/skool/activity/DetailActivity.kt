package com.qureka.skool.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.stopwatch.ServiceHelper
import com.qureka.skool.stopwatch.StopWatchCommand
import com.qureka.skool.stopwatch.StopWatchManager
import com.qureka.skool.stopwatch.StopWatchScreen
import com.qureka.skool.stopwatch.StopWatchViewModel
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.utils.Utils


class DetailActivity : BaseActivity() {
    private val timerViewModel by viewModels<StopWatchViewModel>()
    private lateinit var interstitialAdManager: InterstitialAdManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                DetailUi()
            }
        }
        interstitialAdManager = InterstitialAdManager()
        interstitialAdManager.preLoadInterstitialAd(
            this@DetailActivity, AddUnit.PP_BackButton_Interstitial
        )
    }

    var context: Context? = null

    @Composable
    private fun DetailUi() {
        context = LocalContext.current
        StopWatchScreen(timerViewModel, object : OnRecyclerViewClick {
            override fun onClick(position: Int, type: Int) {
                if (type == 2)
                    fireFirstGamePlay()
                else {
                    showAd()
                }
            }
        })
    }

    fun fireFirstGamePlay() {
        if (preferences.getBoolean(IS_FIRST_GAME_PLAY, false)?.not() == true)
            fireFgpEvent()
    }

    @Preview(showBackground = true)
    @Composable
    fun DashboardPreview() {
        WordCheckTheme {
            val previewViewModel = StopWatchViewModel(StopWatchManager())
            StopWatchScreen(
                timerViewModel = previewViewModel,
                object : OnRecyclerViewClick {
                    override fun onClick(position: Int, type: Int) {

                    }
                },
            )
        }
    }

    private fun showAd() {
        Utils.showProgBar(this)
        if (interstitialAdManager.mInterstitialAd == null && interstitialAdManager.isAdLoading.value == false) {
            interstitialAdManager.preLoadInterstitialAd(
                this@DetailActivity,
                AddUnit.PP_BackButton_Interstitial,
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

    override fun onBackPressedCustom() {
        context?.let { ServiceHelper.triggerForegroundService(it, StopWatchCommand.CANCEL_SERVICE) }
        showAd()
    }
}