package com.qureka.skool.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.qureka.skool.R
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.masterdata.BaseResponse
import com.qureka.skool.masterdata.GlobalConfigViewModel
import com.qureka.skool.network.StatusCode
import com.qureka.skool.theme.SplashTypography
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.utils.CountyCheckAndGroupAssign
import com.qureka.skool.utils.Utils
import kotlinx.coroutines.launch

const val IS_FIRST_GAME_PLAY = "IS_FIRST_GAME_PLAY"
const val GLOBAL_CONFIG_RESPONSE: String = "GLOBAL_CONFIG_RESPONSE"
const val ON_BOARDING: String = "ON_BOARDING"
const val GLOBAL_CONFIG_CALL_TIME = "GLOBAL_CONFIG_CALL_TIME"
const val HOURS_24 = 86400000L

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    //private lateinit var _binding: ActivitySplashBinding
    private val globalConfigViewModel by viewModels<GlobalConfigViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                SplashUi()
            }
        }
        CountyCheckAndGroupAssign.detectAndAssignCountry(this, "AU")
        if (CountyCheckAndGroupAssign.isBannedCountry()) {
            checkForBlockCountry()
            return
        }
        getGlobalConfig()
        setObserver()
    }

    @Preview(name = "splashUi", widthDp = 480, heightDp = 800, showBackground = true)
    @Composable
    private fun SplashUi() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = with(Modifier) {
                    fillMaxSize()
                        .paint(
                            // Replace with your image id
                            painterResource(id = R.drawable.splash_bg),
                            contentScale = ContentScale.Crop
                        )

                })
            {
                Column(
                    modifier = Modifier.fillMaxSize(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 0.dp, vertical = 20.dp)
                            .width(299.dp)
                            .height(137.dp)
                            .align(Alignment.CenterHorizontally),
                    ) {
                        Image(
                            modifier = Modifier
                                .width(299.dp)
                                .height(137.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.splash_logo),
                            contentDescription = "logo",
                            contentScale = ContentScale.Crop
                        )

                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .fillMaxSize(1f)
                            .padding(bottom = 60.7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = stringResource(id = R.string.welcome_to_flashlightworks),
                            style = SplashTypography.bodyLarge,
                        )
                    }
                }
            }
        }
    }

    private fun checkForBlockCountry() {
        val intent = Intent(this, IpCheckActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun getGlobalConfig() {
        startOnBoarding()
        setStatusAccordingToApk()
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

    override fun onBackPressedCustom() {
        super.onBackPressedCustom()
        finishAffinity()
    }
}