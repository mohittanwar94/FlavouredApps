package com.qureka.skool.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
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
val PoppinsExtraBold = FontFamily(
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold)
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
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Bold,
        fontSize = 13.3.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    bodyMedium = TextStyle(
        color = Color_afef00,
        fontFamily = PoppinsMedium,
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
        color = Color_afef00,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    titleMedium = TextStyle(
        color = White,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.27.sp,
        lineHeight = 18.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    labelLarge = TextStyle(
        color = Color_Black,
        fontFamily = BasementGrotesqueBlack,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val DialogTypography = Typography(
    titleLarge = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    bodyLarge = TextStyle(
        color = Color_Black,
        fontFamily = BasementGrotesqueBlack,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        letterSpacing = 0.27.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    bodySmall = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsExtraBold,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 10.sp,
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
    headlineMedium = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 13.3.sp,
        letterSpacing = -0.61.sp,
    ),
    titleLarge = TextStyle(
        color = White,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 11.7.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    labelLarge = TextStyle(
        color = Color_Black,
        fontFamily = BasementGrotesqueBlack,
        fontWeight = FontWeight.Normal,
        fontSize = 13.3.sp,
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
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    labelMedium = TextStyle(
        color = Color_afef00,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 11.7.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    headlineLarge = TextStyle(
        color = Color_afef00,
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    bodyLarge = TextStyle(
        color = Color_afef00,
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val DetailScreenTypography = Typography(
    titleLarge = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.7.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),

    titleMedium = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 8.7.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),

    bodyLarge = TextStyle(
        color = Color_Black,
        fontFamily = BasementGrotesqueBlack,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    titleSmall = TextStyle(
        color = Color_afef00,
        fontFamily = BasementGrotesqueBlack,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
//hint color searchbar
    labelSmall = TextStyle(
        color = Color.Gray,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 13.3.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
//text color searchbar
    labelMedium = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 13.3.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val AddEditNotesTypography = Typography(
    titleLarge = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 18.7.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
//hint color title
    labelSmall = TextStyle(
        color = Color_46000000,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
//text color title
    labelMedium = TextStyle(
        color = Color_Black,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)

val ManageDataTypography = Typography(
    bodyLarge = TextStyle(
        color = Color_afef00,
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 13.3.sp,
        letterSpacing = (-0.61).sp,
    ),
    bodyMedium = TextStyle(
        color = White,
        fontFamily = PoppinsMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = -0.48.sp,
    ),
    labelMedium = TextStyle(
        color = Color_Black,
        fontFamily = BasementGrotesqueBlack,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)