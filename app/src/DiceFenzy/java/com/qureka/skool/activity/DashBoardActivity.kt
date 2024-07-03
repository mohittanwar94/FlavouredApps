package com.qureka.skool.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.window.Dialog
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
import com.google.android.ump.UserMessagingPlatform
import com.qureka.skool.AddUnit
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.R
import com.qureka.skool.ServerConfig
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.NativeAdvancedMediumAdLayoutBinding
import com.qureka.skool.theme.Color_8a0a05
import com.qureka.skool.theme.Color_ffa44e
import com.qureka.skool.theme.DashboardTypography
import com.qureka.skool.theme.DialogTypography
import com.qureka.skool.theme.PoppinsBold
import com.qureka.skool.theme.PoppinsMedium
import com.qureka.skool.theme.White
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.utils.CountyCheckAndGroupAssign
import com.qureka.skool.utils.Utils
import com.qureka.skool.viewmodel.DashboardViewModel
import com.singular.sdk.Singular
import com.singular.sdk.SingularAdData


class DashBoardActivity : BaseActivity() {
    private val DELAY = 2000
    private var back_pressed: Long = 0
    private val titleSpan = SpanStyle(
        color = White,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 13.3.sp
    )
    private val tncSpan = SpanStyle(
        color = Color.White,
        fontSize = 9.sp,
        fontFamily = PoppinsBold,
        textDecoration = TextDecoration.Underline
    )
    private val application by lazy { QurekaSkoolApplication.getApplication() }
    private val dashboardViewModel by viewModels<DashboardViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                DashboardUi(dashboardViewModel, application)
            }
        }
        updateSessionCount()
        onShowAd()
        updateDataPreferenceView()
    }

    @Preview(showBackground = true)
    @Composable
    fun DashboardPreview() {
        WordCheckTheme {
            val previewViewModel = DashboardViewModel()
            DashboardUi(
                dashboardViewModel = previewViewModel,
                QurekaSkoolApplication.getApplication()
            )
        }
    }

    @Composable
    fun DashboardUi(dashboardViewModel: DashboardViewModel, application: QurekaSkoolApplication) {
        val isReloadAd by dashboardViewModel.isReloadAd.observeAsState()
        val isManageDataPreferenceButtonVisible by dashboardViewModel.isManageDataPreferenceButtonVisible.observeAsState()
        val canShowAds by dashboardViewModel.canShowAds.observeAsState()
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
                        .fillMaxSize(1f)
                        .padding(all = 5.dp)
                        .verticalScroll(state = rememberScrollState(), enabled = true),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    if (isReloadAd == true)
                        NativeMediumAd(canShowAds)
                    Spacer(modifier = Modifier.padding(0.dp, 20.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 5.dp, 0.dp),
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.your_unlimited_prank_sound_app))
                            append(" ")
                            withStyle(style = titleSpan) {
                                append(stringResource(id = R.string.each_roll_is_fair_and_random))
                            }
                            // stringResource(id = R.string.your_unlimited_prank_sound_app)
                        },
                        textAlign = TextAlign.Center,
                        style = DashboardTypography.titleLarge,
                    )
                    Spacer(modifier = Modifier.padding(0.dp, 10.7.dp))
                    Image(
                        painter = painterResource(id = R.drawable.drum),
                        contentDescription = "imageDescription",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(top = 19.dp)
                            .height(128.dp)
                            .width(128.dp)
                    )

                    GetStartedButton()
                    Spacer(modifier = Modifier.padding(vertical = 25.3.dp))
                    InstructionsLayout()
                    if (isManageDataPreferenceButtonVisible == true) {
                        Spacer(modifier = Modifier.padding(vertical = 20.3.dp, horizontal = 16.dp))
                        ManageDataPreference()
                    }
                    Spacer(modifier = Modifier.padding(vertical = 23.dp, horizontal = 16.dp))
                    TncComponent()
                    Spacer(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp))
                }
            }
        }
    }


    @Composable
    private fun TncComponent() {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tnc = "T&C"
            val privacyPolicy = "Privacy Policy"
            val cookiePolicy = "Cookie Policy"
            val annotatedString = buildAnnotatedString {
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
                    color = Color.White, fontSize = 9.sp,
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
    private fun InstructionsLayout() {
        Box(
            modifier = with(Modifier) {
                fillMaxWidth(1f)
                wrapContentHeight()
                    .padding(horizontal = 0.dp, vertical = 5.dp)
            }, contentAlignment = Alignment.TopStart
        )
        {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(5.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = stringResource(id = R.string.how_to_use),
                    style = DashboardTypography.displayLarge
                )
                Row(
                    modifier = Modifier.padding(top = 8.3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BulletPoint()
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = stringResource(id = R.string.open_the_prankpluse_app_on_your_device),
                        textAlign = TextAlign.Start,
                        style = DashboardTypography.displayMedium,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 8.3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BulletPoint()
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = stringResource(id = R.string.browse_the_sounds),
                        textAlign = TextAlign.Start,
                        style = DashboardTypography.displayMedium,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 15.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BulletPoint()
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .offset(0.dp, (-5).dp),
                        text = stringResource(id = R.string.the_dashboard),
                        textAlign = TextAlign.Start,
                        style = DashboardTypography.displayMedium,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 15.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BulletPoint()
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .offset(0.dp, (-5).dp),
                        text = stringResource(id = R.string.select_your_sound),
                        textAlign = TextAlign.Start,
                        style = DashboardTypography.displayMedium,
                    )
                }
            }
        }
    }

    @Composable
    private fun BulletPoint() {
        Box(
            modifier = with(
                Modifier
                    .width(9.dp)
                    .height(9.dp),
            ) {
                fillMaxSize()
                    .paint(
                        // Replace with your image id
                        painterResource(id = R.drawable.ic_dice),
                        contentScale = ContentScale.FillBounds
                    )
            }
        )
    }

    @Composable
    private fun GetStartedButton() {
        val shouldShowDialog = remember { mutableStateOf(false) }
        Box(
            modifier = with(Modifier) {
                padding(top = 21.dp)
                    .width(230.dp)
                    .height(60.7.dp)
                    .clickable(onClick = {
                        shouldShowDialog.value = true
                    })
                    .paint(
                        painterResource(id = R.drawable.start_btn_bg),
                        contentScale = ContentScale.FillBounds
                    )
            }, contentAlignment = Alignment.Center
        )
        {
            Text(
                modifier = Modifier.padding(all = 15.dp),
                text = stringResource(id = R.string.start_scanning),
                textAlign = TextAlign.Center,
                style = DashboardTypography.labelLarge,
            )
        }
        if (shouldShowDialog.value)
            DialogWithImage(shouldShowDialog = shouldShowDialog)
    }

    @Composable
    private fun ManageDataPreference() {
        Box(
            modifier = with(Modifier) {
                wrapContentWidth()
                    .width(260.dp)
                    .height(58.dp)
                    .clickable(onClick = {
                        clickOnManageDataPreferences()
                    })
                    .paint(
                        painterResource(id = R.drawable.start_btn_bg),
                        contentScale = ContentScale.FillBounds
                    )

            }, contentAlignment = Alignment.Center
        )
        {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp),
                text = stringResource(id = R.string.manage_data_preferences),
                textAlign = TextAlign.Center,
                style = DashboardTypography.labelMedium,
            )
        }
    }

    private fun clickOnManageDataPreferences() {
        if (ServerConfig.groupThreeConsonantPopupApk) {
            UserMessagingPlatform.showPrivacyOptionsForm(
                this
            )
            {
                it?.let { Utils.showToast(this, it.message ?: "") } ?: kotlin.run {
                    initializeAdMobAds(consentInformation, true)
                    updateDataPreferenceView()
                }
            }
        } else {
            val openActivity = Intent(this, ManageDataPreferenceActivity::class.java)
            startActivity(openActivity)
        }
    }

    override fun onShowAd() {
        dashboardViewModel.updateCanShowAds(application.canShowAd)
        println("====can show ad====${application.canShowAd}")
        dashboardViewModel.isReloadAd(true)
        dashboardViewModel.isManageDataPreferenceButtonVisible(isManageDataPrefVisible())
        super.onShowAd()
    }

    override fun onResume() {
        super.onResume()
        checkForConsentConditions()
    }

    override fun updateDataPreferenceView() {
        isManageDataPrefVisible()
    }

    override fun onRvAdDismiss(position: Int) {
        startActivity(Intent(this, DetailActivity::class.java))
    }

    override fun onBackPressedCustom() {
        super.onBackPressedCustom()
        if (back_pressed + DELAY > System.currentTimeMillis()) {
            finish()
        } else {
            Utils.showToast(this@DashBoardActivity, "Press once again to exit!")
        }
        back_pressed = System.currentTimeMillis()
    }

    @Composable
    fun NativeMediumAd(canShowAds: Boolean?) {
        val context = LocalContext.current
        val adUnit: String = AddUnit.PP_Dashboard_NativeAdvanced
        var isAdRequested = false

        AndroidViewBinding(
            factory = NativeAdvancedMediumAdLayoutBinding::inflate,
            modifier = Modifier
                .navigationBarsPadding()
                .wrapContentHeight(unbounded = true)
        ) {
            if (canShowAds?.not() == true) {
                this.root.isVisible = false
                isAdRequested = false
                return@AndroidViewBinding
            }
            this.root.isVisible = true
            if (isAdRequested)
                return@AndroidViewBinding
            val adView = nativeAdView.also { adView ->
                adView.bodyView = tvBody
                adView.callToActionView = btnCta
                adView.headlineView = tvHeadline
                adView.iconView = ivAppIcon
                adView.mediaView = mvContent
            }
            kotlin.runCatching {
                AdLoader.Builder(adView.context, adUnit)
                    .forNativeAd { nativeAd ->
                        nativeAd.body?.let { body ->
                            tvBody.text = body
                        }

                        nativeAd.callToAction?.let { cta ->
                            btnCta.text = cta
                        }

                        nativeAd.headline?.let { headline ->
                            tvHeadline.text = headline
                        }

                        nativeAd.icon?.let { icon ->
                            ivAppIcon.setImageDrawable(icon.drawable)
                        }

                        adView.setNativeAd(nativeAd)
                        sendSingularRevenue(nativeAd, adUnit, context)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()

                            shimmerFrameLayout.visibility = View.GONE
                            adView.visibility = View.VISIBLE
                        }

                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder().setAdChoicesPlacement(ADCHOICES_TOP_RIGHT).build()
                    )
                    .build()
            }.onSuccess {
                it.loadAd(Utils.getAdRequest(context))
                isAdRequested = true
            }
        }
    }


    private fun sendSingularRevenue(appOpenAd: NativeAd, adUnitID: String, context: Context) {
        appOpenAd.setOnPaidEventListener { adValue ->
            val impressionData: AdValue = adValue
            val data = SingularAdData(
                Utils.getAdPlatFormName(context), impressionData.currencyCode,
                impressionData.valueMicros / 1000000.0
            )
            data.withAdUnitId(adUnitID)
                .withNetworkName(appOpenAd.responseInfo?.mediationAdapterClassName)
            Singular.adRevenue(data)
        }
    }

    @Composable
    fun DialogWithImage(
        shouldShowDialog: MutableState<Boolean>,
    ) {
        Dialog(onDismissRequest = { shouldShowDialog.value = false }) {
            Box(contentAlignment = Alignment.TopCenter) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .wrapContentHeight(),
                    border = BorderStroke(
                        color = Color_8a0a05,
                        width = 2.dp
                    ),
                    colors = CardColors(
                        containerColor = Color_ffa44e,
                        disabledContainerColor = Color_ffa44e,
                        contentColor = Color_ffa44e,
                        disabledContentColor = Color_ffa44e
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.End),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cancel_button),
                            contentDescription = "imageDescription",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clickable {
                                    shouldShowDialog.value = false
                                }
                                .height(30.dp)
                                .width(30.dp),
                        )
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(127.dp),
                                painter = painterResource(id = R.drawable.splash_logo),
                                contentDescription = "dialog logo",
                                contentScale = ContentScale.FillBounds,
                                alignment = Alignment.Center
                            )
                            Text(
                                text = stringResource(id = R.string.ready_to_illuminate_your_world),
                                modifier = Modifier.padding(horizontal = 27.dp, vertical = 5.dp),
                                style = DialogTypography.titleLarge
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = stringResource(id = R.string.ad),
                                    style = DialogTypography.bodySmall
                                )
                            }
                            DialogWatchNowButton(checkedState = shouldShowDialog)
                            Spacer(modifier = Modifier.padding(bottom = 15.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DialogWatchNowButton(checkedState: MutableState<Boolean>) {
        Box(
            modifier = with(Modifier) {
                padding(top = 0.dp, bottom = 10.dp)
                    .width(230.dp)
                    .height(60.dp)
                    .clickable(onClick = {
                        checkedState.value = false
                        loadRvAds(0, activity)
                    })
                    .paint(
                        painterResource(id = R.drawable.start_btn_bg),
                        contentScale = ContentScale.FillBounds
                    )

            }, contentAlignment = Alignment.Center
        )
        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ad_icon),
                    contentDescription = "imageDescription",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(25.dp)
                        .width(25.dp)
                        .padding(start = 5.dp)
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .padding(top = 2.dp),
                    text = stringResource(id = R.string.watch_now),
                    textAlign = TextAlign.Center,
                    style = DialogTypography.bodyLarge
                )
            }
        }
    }
}