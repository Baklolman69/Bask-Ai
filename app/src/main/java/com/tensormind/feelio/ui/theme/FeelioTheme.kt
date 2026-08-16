package com.tensormind.feelio.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val CardShape = RoundedCornerShape(20.dp)
val PillShape = RoundedCornerShape(999.dp)

val FeelioColorScheme = lightColorScheme(
    primary = FeelioColors.Cta,
    onPrimary = FeelioColors.CtaText,
    background = FeelioColors.BgCream,
    surface = FeelioColors.Surface,
    onSurface = FeelioColors.TextPrimary,
)

val FeelioShapes = Shapes(
    small = PillShape,
    medium = CardShape,
    large = CardShape,
    extraLarge = CardShape,
)

@Composable
fun FeelioTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FeelioColorScheme,
        typography = Typography,
        shapes = FeelioShapes,
        content = content
    )
}
