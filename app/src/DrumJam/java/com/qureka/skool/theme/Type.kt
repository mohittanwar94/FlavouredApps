package com.qureka.skool.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.qureka.skool.R

// Set of Material typography styles to start with
val PoppinsBold = FontFamily(
    Font(R.font.poppins_bold, FontWeight.Bold)
)
val Azonix = FontFamily(
    Font(R.font.azonix, FontWeight.Bold)
)
val MontserratExtraBold = FontFamily(
    Font(R.font.montserrat_extrabold, FontWeight.Bold)
)
val MontserratRegular = FontFamily(
    Font(R.font.monterrat_regural, FontWeight.Normal)
)
val MontserratBold = FontFamily(
    Font(R.font.montserrat_bold, FontWeight.Bold)
)
val MontserratMedium = FontFamily(
    Font(R.font.montserrat_medium, FontWeight.Medium)
)
val BasementGrotesqueBlack = FontFamily(
    Font(R.font.basement_grotesque_black, FontWeight.Bold)
)
val PoppinsMedium = FontFamily(
    Font(R.font.poppins_medium_font, FontWeight.Medium)
)
val PoppinsRegular = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal)
)
val SplashTypography = Typography(
    bodyLarge = TextStyle(
        color = White,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 13.3.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val CountryBlockTypography = Typography(
    bodyLarge = TextStyle(
        color = White,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    titleMedium = TextStyle(
        color = White,
        fontFamily = MontserratMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val GetStartedTypography = Typography(
    titleLarge = TextStyle(
        color = White,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    titleMedium = TextStyle(
        color = White,
        fontFamily = MontserratMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    labelLarge = TextStyle(
        color = White,
        fontFamily = Azonix,
        fontWeight = FontWeight.Bold,
        fontSize = 22.7.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val DialogTypography = Typography(
    titleLarge = TextStyle(
        color = Color_Black,
        fontFamily = MontserratMedium,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        fontSize = 15.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    bodyLarge = TextStyle(
        color = White,
        fontFamily = Azonix,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    bodySmall = TextStyle(
        color = Color_Black,
        fontFamily = MontserratMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 7.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
    ),
    headlineMedium = TextStyle(
        color = White,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    ),
    labelMedium = TextStyle(
        color = Color_Black,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
)

val DashboardTypography = Typography(
    titleLarge = TextStyle(
        color = White,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    labelLarge = TextStyle(
        color = White,
        fontFamily = Azonix,
        fontWeight = FontWeight.Normal,
        fontSize = 22.7.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    displayLarge = TextStyle(
        color = White,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.7.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    displayMedium = TextStyle(
        color = White,
        fontFamily = MontserratMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 12.7.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val DetailScreenTypography = Typography(
    labelLarge = TextStyle(
        color = White,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.7.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val ManageDataTypography = Typography(
    bodyLarge = TextStyle(
        color = Color_e879ce,
        fontFamily = MontserratBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.3.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        color = White,
        fontFamily = MontserratMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
    ),
    labelMedium = TextStyle(
        color = White,
        fontFamily = Azonix,
        fontWeight = FontWeight.Bold,
        fontSize = 23.sp,
        letterSpacing = 0.5.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)