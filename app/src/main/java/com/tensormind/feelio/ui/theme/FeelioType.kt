package com.tensormind.feelio.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.tensormind.feelio.R

// Google Fonts Provider for downloadable web fonts
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val FredokaFont = GoogleFont("Fredoka")
val PlusJakartaFont = GoogleFont("Plus Jakarta Sans")
val InterFont = GoogleFont("Inter")

// Display / Headline font family with Google Fonts + System Rounded fallbacks
val DisplayFontFamily = FontFamily(
    Font(googleFont = PlusJakartaFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = FredokaFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = FredokaFont, fontProvider = provider, weight = FontWeight.SemiBold)
)

// Body font family with Google Fonts + System Sans-Serif fallbacks
val BodyFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.SemiBold)
)

// 10x Startup-Grade Typography system with tight letter spacing & premium line heights
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 31.sp,
        lineHeight = 39.sp,
        letterSpacing = (-0.8).sp,
        color = FeelioColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 23.sp,
        lineHeight = 31.sp,
        letterSpacing = (-0.5).sp,
        color = FeelioColors.TextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.25).sp,
        color = FeelioColors.TextSecondary
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.15).sp,
        color = FeelioColors.TextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
        color = FeelioColors.TextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
        color = FeelioColors.CtaText
    )
)
