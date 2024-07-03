package com.qureka.skool.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.qureka.skool.BuildConfig
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.R
import com.qureka.skool.ServerConfig
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.ipchecker.model.ConsentRequest
import com.qureka.skool.ipchecker.viewmodel.IpCheckerViewModel
import com.qureka.skool.theme.Color_910888
import com.qureka.skool.theme.Color_f58ad9
import com.qureka.skool.theme.Color_ff97e1
import com.qureka.skool.theme.ManageDataTypography
import com.qureka.skool.theme.White
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.utils.AppConstant
import com.qureka.skool.utils.ComposeUtils.gradientBackground
import com.qureka.skool.utils.CountyCheckAndGroupAssign
import com.qureka.skool.utils.IS_CONSENT_ALLOW_DENY
import com.qureka.skool.utils.IS_CONSENT_OPT_OUT
import kotlinx.coroutines.launch

class ManageDataPreferenceActivity : BaseActivity() {
    private val ipCheckerViewModel by viewModels<IpCheckerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                ManageDataPrefUI()
            }
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

    @Composable
    @Preview(name = "ManageDataPrefUI", showBackground = true)
    fun ManageDataPrefUI() {
        val textVisibility = remember { mutableStateOf(false) }
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = with(Modifier) {
                fillMaxSize().paint(
                    painterResource(id = R.drawable.dashboard_bg),
                    contentScale = ContentScale.FillBounds
                )
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .verticalScroll(state = rememberScrollState(), enabled = true)
                        .padding(bottom = 5.dp), verticalArrangement = Arrangement.Top
                ) {
                    Image(
                        modifier = Modifier
                            .clickable(onClick = {
                                finish()
                            })
                            .padding(vertical = 16.dp, horizontal = 17.dp)
                            .width(30.dp)
                            .height(30.dp),
                        painter = painterResource(id = R.drawable.iv_back),
                        contentDescription = "iv_back",
                        contentScale = ContentScale.FillBounds
                    )

                    Image(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 17.dp)
                            .padding(top = 22.dp)
                            .width(129.dp)
                            .height(141.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        painter = painterResource(id = R.drawable.iv_manage_image),
                        contentDescription = "iv_manage_image",
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 25.dp, start = 16.dp),
                        text = stringResource(id = if (textVisibility.value) R.string.you_have_successfully_opted_out else R.string.do_not_sell_my_personal_information),
                        style = ManageDataTypography.bodyLarge,
                    )
                    if (textVisibility.value.not()) {
                        Text(
                            modifier = Modifier
                                .padding(top = 25.dp, start = 16.dp, end = 16.dp)
                                .align(alignment = Alignment.CenterHorizontally),
                            text = stringResource(id = R.string.you_have_the_right_to_opt_out_of_the_sale),
                            style = ManageDataTypography.bodyMedium,
                        )
                    }
                    if (textVisibility.value.not()) {
                        Text(
                            modifier = Modifier
                                .padding(top = 11.dp, start = 16.dp, end = 16.dp)
                                .align(alignment = Alignment.CenterHorizontally),
                            text = stringResource(id = R.string.we_use_device_identifiers_like_cookies),
                            style = ManageDataTypography.bodyMedium,
                        )
                    }
                    if (textVisibility.value) {
                        val textAsAnnotatedString =
                            stringResource(
                                id = R.string.we_re_sorry_to_see_you_go_but,
                                stringResource(id = R.string.app_name),
                                stringResource(id = R.string.app_name),
                                stringResource(id = R.string.app_name)
                            )
                        val spanString = "Privacy Policy"
                        val arrayString = textAsAnnotatedString.split(spanString)
                        val annotedString = buildAnnotatedString {
                            append(arrayString[0])
                            withStyle(
                                style = SpanStyle(
                                    color = Color_f58ad9,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                pushStringAnnotation(tag = spanString, annotation = spanString)
                                append(spanString)
                            }
                            append(arrayString[1])
                        }
                        ClickableText(modifier = Modifier
                            .padding(top = 11.dp, start = 16.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                            text = annotedString,
                            style = ManageDataTypography.bodyMedium,
                            onClick = { offset ->
                                annotedString.getStringAnnotations(offset, offset)
                                    .firstOrNull()?.let { span ->
                                        openPrivacyPolicy(
                                            if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                                                ServerConfig.PRIVACY_POLICY_URL
                                            } else {
                                                ServerConfig.PRIVACY_POLICY_URL_INTL
                                            }
                                        )
                                    }
                            })
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(1f)
                            .padding(top = 22.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!textVisibility.value) {
                            GetStartedButton(textVisibility)
                        }

                        Text(
                            modifier = Modifier
                                .clickable(onClick = {
                                    finish()
                                })
                                .padding(top = 28.dp, start = 10.dp)
                                .align(alignment = Alignment.CenterHorizontally),
                            text = stringResource(id = R.string.close),
                            style = ManageDataTypography.bodyMedium,
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun GetStartedButton(textVisibility: MutableState<Boolean>) {
        Box(
            modifier = with(Modifier) {
                padding(top = 37.dp)
                    .width(167.dp)
                    .height(56.dp)
                    .clickable(onClick = {
                        optOutClick()
                        textVisibility.value = !textVisibility.value
                    })
                    .gradientBackground(
                        listOf(
                            Color_ff97e1,
                            Color_910888,
                        ),
                        angle = 270f
                    )
                    .border(
                        BorderStroke(
                            2.dp,
                            color = White
                        ),
                        RoundedCornerShape(10.dp)
                    )

            }, contentAlignment = Alignment.Center
        )
        {
            Text(
                text = stringResource(id = R.string.opt_out),
                textAlign = TextAlign.Center,
                style = ManageDataTypography.labelMedium,
            )
        }
    }

    fun openPrivacyPolicy(url: String) {
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

    private fun optOutClick() {
        updateGaiDConsentStatus()
        QurekaSkoolApplication.getApplication().isUniqueLaunchForPopUp = true
        if (ServerConfig.groupTwoManagePreferencesApk.not()) QurekaSkoolApplication.getApplication().canShowAd =
            false
        preferences.setBoolean(IS_CONSENT_ALLOW_DENY, false)
        preferences.setBoolean(IS_CONSENT_OPT_OUT, true)
        AppConstant.stopFirebaseAnalytics(this)
        AppConstant.callSingularStopSdk(this)
    }

    override fun onBackPressedCustom() {
        finish()
    }
}