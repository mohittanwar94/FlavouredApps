package com.qureka.skool.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.R
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.theme.Color_630000
import com.qureka.skool.theme.Color_8a0a05
import com.qureka.skool.theme.Color_Black
import com.qureka.skool.theme.Color_e879ce
import com.qureka.skool.theme.Color_ffa44e
import com.qureka.skool.theme.DetailScreenTypography
import com.qureka.skool.theme.White
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.utils.Utils
import java.text.DecimalFormat


class DetailActivity : BaseActivity() {
    private lateinit var interstitialAdManager: InterstitialAdManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                DetailUI()
            }
        }
        try {
            interstitialAdManager = InterstitialAdManager()
            interstitialAdManager.preLoadInterstitialAd(
                this@DetailActivity, AddUnit.PP_BackButton_Interstitial
            )
        } catch (ignored: Throwable) {
        }
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
            DetailUI()
        }
    }

    @Composable
    fun DetailUI() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = with(Modifier) {
                fillMaxSize().paint(
                    painterResource(id = R.drawable.dashboard_bg), contentScale = ContentScale.Crop
                )

            }) {
                Column(
                    modifier = Modifier.fillMaxSize(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Image(
                        modifier = Modifier
                            .padding(top = 12.dp , start = 12.dp)
                            .width(35.dp)
                            .height(35.dp)
                            .clickable(onClick = {showAd()}),
                        painter = painterResource(id = R.drawable.iv_back),
                        contentDescription = "iv_back",
                        contentScale = ContentScale.FillBounds
                    )
                    DetailScreenUI()
                }
            }
        }
    }

    @Composable
    fun DetailScreenUI() {
        var result: Int by remember { mutableStateOf(1) }
        var count: Int by remember { mutableStateOf(0) }
        var total: Int by remember { mutableStateOf(0) }
        var average: Double by remember { mutableStateOf(0.0) }

        val imageResource = when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        Box(modifier = with(Modifier) {
            fillMaxWidth(1f)
            fillMaxHeight(1f).padding(start = 15.dp, end = 15.dp)
        }, contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(
                        color = Color_ffa44e, shape = RoundedCornerShape(8.dp)
                    )
                    .border(2.7.dp, color = Color_8a0a05, RoundedCornerShape(8.dp))
                    .padding(13.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(27.3.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .wrapContentHeight()
                        .weight(weight = 3f, fill = false)
                ) {
                    val counterAnnotatedString = buildAnnotatedString {
                        append("Counter: ")
                        withStyle(style = SpanStyle(color = Color_Black)) {
                            append("" + count)
                        }
                    }

                    val totalAnnotatedString = buildAnnotatedString {
                        append("Total: ")
                        withStyle(style = SpanStyle(color = Color_Black)) {
                            append("" + total)
                        }
                    }

                    val averageAnnotatedString = buildAnnotatedString {
                        append("Average: ")
                        withStyle(style = SpanStyle(color = Color_Black)) {
                            append("" + average)
                        }
                    }

                    Text(
                        text = counterAnnotatedString,
                        modifier = Modifier.weight(1f, fill = true),
                        style = DetailScreenTypography.labelMedium,
                    )
                    Text(
                        text = totalAnnotatedString,
                        modifier = Modifier.weight(1f, fill = true),
                        style = DetailScreenTypography.labelMedium,
                    )
                    Text(
                        text = averageAnnotatedString,
                        modifier = Modifier.weight(1f, fill = true),
                        style = DetailScreenTypography.labelMedium,
                    )
                }

                Spacer(modifier = Modifier.height(23.3.dp))

                val buttonBg =
                    R.drawable.start_btn_bg
                val shouldShowDialog = remember { mutableStateOf(false) }
                Box(
                    modifier = with(Modifier) {
                        padding(top = 20.dp)
                            .width(115.dp)
                            .height(30.dp)
                            .clickable(onClick = {
                                shouldShowDialog.value = true
                                count = 0
                                total = 0
                                average = 0.0
                            })
                            .paint(
                                painterResource(id = buttonBg),
                                contentScale = ContentScale.FillBounds
                            )
                    }, contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = stringResource(id = R.string.reset),
                        textAlign = TextAlign.Center,
                        style = DetailScreenTypography.displayLarge,
                    )
                }

                Spacer(modifier = Modifier.height(49.3.dp))
                Image(
                    modifier = Modifier
                        .width(128.7.dp)
                        .height(130.dp),
                    painter = painterResource(imageResource),
                    contentDescription = "logo",
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(47.3.dp))
                Text(
                    text = "Click Button to Roll Dice",
                    style = DetailScreenTypography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(15.3.dp))
                Box(
                    modifier = with(Modifier) {
                            width(115.dp)
                            .height(30.dp)
                            .clickable(onClick = {
                                if (preferences
                                        .getBoolean(IS_FIRST_GAME_PLAY, false)
                                        ?.not() == true
                                ) {
                                    fireFgpEvent()
                                }
                                shouldShowDialog.value = true
                                result = (1..6).random()
                                count++
                                total += result
                                val df = DecimalFormat("#.##")
                                average = df
                                    .format((total.toDouble() / count.toDouble()))
                                    .toDouble()
                            })
                            .paint(
                                painterResource(id = buttonBg),
                                contentScale = ContentScale.FillBounds
                            )
                    }, contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = stringResource(id = R.string.roll),
                        textAlign = TextAlign.Center,
                        style = DetailScreenTypography.displayLarge,
                    )
                }

                Spacer(modifier = Modifier.height(57.3.dp))
            }
        }
    }

    override fun onBackPressedCustom() {
        showAd()
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
        showAdMobAd(interstitialAdManager,
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