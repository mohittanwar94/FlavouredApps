package com.qureka.skool.activity

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qureka.skool.R
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.theme.CountryBlockTypography
import com.qureka.skool.theme.WordCheckTheme

class IpCheckActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                CountryBlockUi()
            }
        }
    }

    @Preview(name = "countryBlockUi", widthDp = 480, heightDp = 800, showBackground = true)
    @Composable
    private fun CountryBlockUi() {
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
                        .wrapContentHeight()
                        .fillMaxWidth(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 136.dp)
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .align(Alignment.CenterHorizontally)
                            .paint(
                                painter = painterResource(id = R.drawable.splash_logo_shadow),
                                contentScale = ContentScale.Crop
                            ),
                    ) {
                        Image(
                            modifier = Modifier
                                .width(312.dp)
                                .height(252.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.ip_check),
                            contentDescription = "logo",
                            contentScale = ContentScale.Crop
                        )

                    }
                    Text(
                        modifier = Modifier.padding(top = 37.dp),
                        text = stringResource(id = R.string.app_name),
                        style = CountryBlockTypography.bodyLarge,
                    )

                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp),
                        text = stringResource(id = R.string.is_not_currently_not),
                        style = CountryBlockTypography.titleMedium,
                    )
                }
            }
        }
    }

    override fun onBackPressedCustom() {
        finishAffinity()
    }

    override fun onClick(v: View?) {

    }
}