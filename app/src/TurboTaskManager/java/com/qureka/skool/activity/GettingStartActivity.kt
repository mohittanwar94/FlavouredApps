package com.qureka.skool.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qureka.skool.AdInfo
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.R
import com.qureka.skool.ServerConfig
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.common.PLAYED_STATE
import com.qureka.skool.theme.Color_70AFEF00
import com.qureka.skool.theme.Color_9f66e7
import com.qureka.skool.theme.Color_afef00
import com.qureka.skool.theme.GetStartedTypography
import com.qureka.skool.theme.PoppinsBold
import com.qureka.skool.theme.White
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.utils.CountyCheckAndGroupAssign
import com.qureka.skool.utils.Utils


class GettingStartActivity : BaseActivity() {
    private val timeDelay = 2000
    private var backPressed: Long = 0
    private lateinit var interstitialAdManager: InterstitialAdManager
    private val tncSpan = SpanStyle(
        color = Color.White,
        fontSize = 9.5.sp,
        fontFamily = PoppinsBold,
        textDecoration = TextDecoration.Underline
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                GettingStartActivityUI()
            }
        }
        interstitialAdManager = InterstitialAdManager()
        interstitialAdManager.preLoadInterstitialAd(
            this@GettingStartActivity, AddUnit.GET_STARTED_SCREEN
        )
    }

    @Preview(
        name = "getStarted",
        showBackground = true,
        showSystemUi = true,
        widthDp = 400,
        heightDp = 800
    )
    @Composable
    private fun GettingStartActivityUI() {
        val checkedState = remember { mutableStateOf(true) }
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = with(Modifier) {
                    fillMaxSize()
                        .paint(
                            // Replace with your image id
                            painterResource(id = R.drawable.dashboard_bg),
                            contentScale = ContentScale.Crop
                        )

                })
            {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .verticalScroll(state = rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Image(
                        modifier = Modifier
                            .padding(top = 45.dp)
                            .width(210.dp)
                            .height(137.dp),
                        painter = painterResource(id = R.drawable.splash_logo),
                        contentDescription = "logo",
                        contentScale = ContentScale.FillBounds
                    )
                    Image(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .width(155.dp)
                            .height(183.dp),
                        painter = painterResource(id = R.drawable.todo_logo),
                        contentDescription = "todo logo",
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        modifier = Modifier.padding(
                            top = 15.dp
                        ),
                        text = stringResource(id = R.string.welcome_to_drumJam),
                        style = GetStartedTypography.titleLarge,
                    )
                    DisplayBottomUI(checkedState)
                }
            }
        }
    }

    @Composable
    private fun DisplayBottomUI(checkedState: MutableState<Boolean>) {
        Text(
            modifier = Modifier
                .padding(
                    start = 45.dp, end = 45.dp, top = 0.dp
                ),
            text = stringResource(id = R.string.hello_adventurer),
            textAlign = TextAlign.Center,
            style = GetStartedTypography.titleMedium,
        )
        GetStartedButton(checkedState)
        CheckboxComponent(checkedState)
    }


    @Composable
    private fun CheckboxComponent(checkedState: MutableState<Boolean>) {
        Row(
            modifier = Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedCornerCheckbox(
                isChecked = checkedState.value,
                onValueChange = { checkedState.value = it },
                modifier = Modifier.padding(12.dp)
            )
            val tnc = "T&C"
            val privacyPolicy = "Privacy Policy"
            val cookiePolicy = "Cookie Policy"
            val annotatedString = buildAnnotatedString {
                append("By using the app you agree to our ")
                withStyle(
                    style = tncSpan
                ) {
                    pushStringAnnotation(tag = tnc, annotation = tnc)
                    append(tnc)
                }
                append(", ")
                withStyle(
                    style = tncSpan
                ) {
                    pushStringAnnotation(tag = privacyPolicy, annotation = privacyPolicy)
                    append(privacyPolicy)
                }
                append(" and ")

                withStyle(
                    style = tncSpan
                ) {
                    pushStringAnnotation(tag = cookiePolicy, annotation = cookiePolicy)
                    append(cookiePolicy)
                }
            }
            ClickableText(modifier = Modifier.padding(
                start = 0.dp
            ),
                style = TextStyle(
                    color = Color.White, fontSize = 9.5.sp,
                    fontFamily = PoppinsBold,
                ),
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(offset, offset)
                        .firstOrNull()?.let { span ->
                            cookiePolicy.clickTncAndCookies(span.item, tnc, privacyPolicy)
                        }
                })
        }
    }

    private fun String.clickTncAndCookies(
        spanName: String,
        tnc: String,
        privacyPolicy: String,
    ) {
        val url: String = when (spanName) {
            tnc -> {
                if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                    ServerConfig.TERM_CONDITION_URL
                } else {
                    ServerConfig.TERM_CONDITION_URL_INTL
                }
            }

            privacyPolicy -> {
                if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                    ServerConfig.PRIVACY_POLICY_URL
                } else {
                    ServerConfig.PRIVACY_POLICY_URL_INTL
                }
            }

            this -> {
                if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                    ServerConfig.COOKIES_URL
                } else {
                    ServerConfig.COOKIES_URL_INTL
                }
            }

            else -> ServerConfig.COOKIES_URL
        }
        try {
            val browserIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Composable
    private fun GetStartedButton(checkedState: MutableState<Boolean>) {
        val buttonBg =
            if (checkedState.value) R.drawable.start_btn_bg else R.drawable.btn_bg_disabled
        Box(
            modifier = with(Modifier) {
                padding(top = 28.dp)
                    .width(195.dp)
                    .height(54.dp)
                    .clickable(onClick = {
                        clickOnGetStarted(checkedState.value)
                    })
                    .paint(painterResource(id = buttonBg), contentScale = ContentScale.FillBounds)
            }, contentAlignment = Alignment.Center
        )
        {
            Text(
                modifier = Modifier
                    .padding(horizontal = 30.dp),
                text = stringResource(id = R.string.get_started),
                textAlign = TextAlign.Center,
                style = GetStartedTypography.labelLarge,
            )
        }
    }

    private fun clickOnGetStarted(isChecked: Boolean) {
        if (isChecked.not()) {
            Utils.showToast(this, getString(R.string.tnc_validate_msg))
            return
        }
        showAd()
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

    override fun onResume() {
        super.onResume()
        if (ServerConfig.groupThreeConsonantPopupApk || ServerConfig.groupFourBanGAApk)
            checkForConsentConditions()
    }


    override fun onBackPressedCustom() {
        if (backPressed + timeDelay > System.currentTimeMillis()) {
            finish()
        } else {
            Utils.showToast(this@GettingStartActivity, "Press once again to exit!")
        }
        backPressed = System.currentTimeMillis()
    }

    @Composable
    fun RoundedCornerCheckbox(
        isChecked: Boolean,
        modifier: Modifier = Modifier,
        size: Float = 20f,
        checkedColor: Color = Color_afef00,
        uncheckedColor: Color = Color.White,
        onValueChange: (Boolean) -> Unit,
    ) {
        val checkboxColor: Color by animateColorAsState(
            if (isChecked) checkedColor else uncheckedColor,
            label = "checkbox"
        )
        val density = LocalDensity.current
        val duration = 200

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .heightIn(18.dp) // height of 48dp to comply with minimum touch target size
                .toggleable(
                    value = isChecked,
                    role = Role.Checkbox,
                    onValueChange = onValueChange
                )
        ) {
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .background(color = checkboxColor, shape = RoundedCornerShape(4.dp))
                    .border(width = 1.5.dp, color = checkedColor, shape = RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = isChecked,
                    enter = slideInHorizontally(animationSpec = tween(duration)) {
                        with(density) { (size * -0.5).dp.roundToPx() }
                    } + expandHorizontally(
                        expandFrom = Alignment.Start,
                        animationSpec = tween(duration)
                    ),
                    exit = fadeOut()
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = uncheckedColor
                    )
                }
            }
        }
    }
}
