package com.qureka.skool.activity

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.R
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.theme.Color_910888
import com.qureka.skool.theme.Color_ff97e1
import com.qureka.skool.theme.DetailScreenTypography
import com.qureka.skool.theme.White
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.utils.ComposeUtils.gradientBackground
import com.qureka.skool.utils.Utils
import com.qureka.skool.viewmodel.DetailViewModel


class DetailActivity : BaseActivity() {
    private var bassPadMp: MediaPlayer? = null
    private var snarePadMp: MediaPlayer? = null
    private var tom1PadMp: MediaPlayer? = null
    private var tom2PadMp: MediaPlayer? = null
    private var cymbal1PadMp: MediaPlayer? = null
    private var cymbal2PadMp: MediaPlayer? = null
    private var hiHat1PadMp: MediaPlayer? = null
    private var hiHat2PadMp: MediaPlayer? = null

    private val detailViewModel by viewModels<DetailViewModel>()
    private lateinit var interstitialAdManager: InterstitialAdManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                DetailUI(detailViewModel)
            }
        }
        bassPadMp = MediaPlayer.create(this@DetailActivity, R.raw.bass)
        snarePadMp = MediaPlayer.create(this@DetailActivity, R.raw.snare)
        tom1PadMp = MediaPlayer.create(this@DetailActivity, R.raw.tom1)
        tom2PadMp = MediaPlayer.create(this@DetailActivity, R.raw.tom2)
        cymbal1PadMp = MediaPlayer.create(
            this@DetailActivity,
            R.raw.crashcymbal
        )
        cymbal2PadMp = MediaPlayer
            .create(this@DetailActivity, R.raw.ridecymbal)
        hiHat1PadMp = MediaPlayer.create(this@DetailActivity, R.raw.openhihat)
        hiHat2PadMp = MediaPlayer.create(this@DetailActivity, R.raw.closehihat)
        interstitialAdManager = InterstitialAdManager()
        interstitialAdManager.preLoadInterstitialAd(
            this@DetailActivity, AddUnit.PP_BackButton_Interstitial
        )
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onStop() {
        super.onStop()

    }

    @Preview(showBackground = true)
    @Composable
    fun DetailPreview() {
        WordCheckTheme {
            val previewViewModel = DetailViewModel()
            DetailUI(
                dashboardViewModel = previewViewModel,
            )
        }
    }

    @Composable
    fun DetailUI(dashboardViewModel: DetailViewModel) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = with(Modifier) {
                    fillMaxSize()
                        .paint(
                            painterResource(id = R.drawable.dashboard_bg),
                            contentScale = ContentScale.Crop
                        )

                })
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iv_back),
                        contentDescription = "imageDescription",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(all = 12.7.dp)
                            .height(32.7.dp)
                            .width(32.7.dp)
                            .clickable(onClick = {
                                showAd()
                            })
                    )
                    LazyVerticalGrid()
                }
            }
        }
    }

    @Composable
    fun LazyVerticalGrid() {
        val list = listOf(
            "Crash\n" +
                    "Cymbal", "Tom\n" +
                    "1", "Tom\n" +
                    "2", "Ride\n" +
                    "Cymbal", "Bass", "Snare", "Open\n" +
                    "Hi-Hat", "Closed\n" +
                    "Hi-Hat"
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 12.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(list.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.7.dp, vertical = 24.dp)
                            .gradientBackground(
                                listOf(
                                    Color_ff97e1,
                                    Color_910888,
                                ),
                                angle = 270f
                            )
                            .border(2.dp, color = White, shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth(1f)
                            .height(170.dp)
                            .clickable(onClick = {
                                playSound(index)
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            style = DetailScreenTypography.labelLarge,
                            text = list[index],
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        )
    }

    private fun playSound(index: Int) {
        if (preferences.getBoolean(IS_FIRST_GAME_PLAY, false)?.not() == true) {
            fireFgpEvent()
        }
        try {
            when (index) {
                0 -> {
                    if (cymbal1PadMp != null) {
                        cymbal1PadMp?.stop()
                        cymbal1PadMp?.release()
                    }
                    cymbal1PadMp = MediaPlayer.create(this@DetailActivity, R.raw.crashcymbal)
                    cymbal1PadMp?.setVolume(1f, 1f)
                    cymbal1PadMp?.start()
                }

                1 -> {
                    if (tom1PadMp != null) {
                        tom1PadMp?.stop()
                        tom1PadMp?.release()
                    }
                    tom1PadMp = MediaPlayer.create(this@DetailActivity, R.raw.tom1)
                    tom1PadMp?.setVolume(1f, 1f)
                    tom1PadMp?.start()
                }

                2 -> {
                    if (tom2PadMp != null) {
                        tom2PadMp?.stop()
                        tom2PadMp?.release()
                    }
                    tom2PadMp = MediaPlayer.create(this@DetailActivity, R.raw.tom2)
                    tom2PadMp?.setVolume(1f, 1f)
                    tom2PadMp?.start()
                }

                3 -> {
                    if (cymbal2PadMp != null) {
                        cymbal2PadMp?.stop()
                        cymbal2PadMp?.release()
                    }
                    cymbal2PadMp = MediaPlayer.create(this@DetailActivity, R.raw.ridecymbal)
                    cymbal2PadMp?.setVolume(1f, 1f)
                    cymbal2PadMp?.start()
                }

                4 -> {
                    if (bassPadMp != null) {
                        bassPadMp?.stop()
                        bassPadMp?.release()
                    }
                    bassPadMp = MediaPlayer.create(this@DetailActivity, R.raw.bass)
                    bassPadMp?.setVolume(1f, 1f)
                    bassPadMp?.start()
                }

                5 -> {
                    if (snarePadMp != null) {
                        snarePadMp?.stop()
                        snarePadMp?.release()
                    }
                    snarePadMp = MediaPlayer.create(this@DetailActivity, R.raw.snare)
                    snarePadMp?.setVolume(1f, 1f)
                    snarePadMp?.start()
                }

                6 -> {
                    if (hiHat1PadMp != null) {
                        hiHat1PadMp?.stop()
                        hiHat1PadMp?.release()
                    }
                    hiHat1PadMp = MediaPlayer.create(this@DetailActivity, R.raw.openhihat)
                    hiHat1PadMp?.setVolume(1f, 1f)
                    hiHat1PadMp?.start()
                }

                7 -> {
                    if (hiHat2PadMp != null) {
                        hiHat2PadMp?.stop()
                        hiHat2PadMp?.release()
                    }
                    hiHat2PadMp = MediaPlayer.create(this@DetailActivity, R.raw.closehihat)
                    hiHat2PadMp?.setVolume(1f, 1f)
                    hiHat2PadMp?.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressedCustom() {
        showAd()
    }

    private fun showAd() {
        releaseMediaPlayers()
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

    private fun releaseMediaPlayers() {
        bassPadMp?.stop()
        bassPadMp?.release()
        snarePadMp?.stop()
        snarePadMp?.release()
        tom1PadMp?.stop()
        tom1PadMp?.release()
        tom2PadMp?.stop()
        tom2PadMp?.release()
        cymbal1PadMp?.stop()
        cymbal1PadMp?.release()
        cymbal2PadMp?.stop()
        cymbal2PadMp?.release()
        hiHat1PadMp?.stop()
        hiHat1PadMp?.release()
        hiHat2PadMp?.stop()
        hiHat2PadMp?.release()
    }
}