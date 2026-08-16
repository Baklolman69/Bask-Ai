package com.tensormind.feelio.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.feelio.ui.theme.FeelioColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    // Animation states
    val textAlpha = remember { Animatable(0f) }
    val textScale = remember { Animatable(0.9f) }
    val copyrightAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Simple fade and scale for "Bask Ai"
        launch {
            textAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        }
        launch {
            textScale.animateTo(1.0f, tween(800, easing = FastOutSlowInEasing))
        }

        // Copyright fade-in
        delay(400.milliseconds)
        launch {
            copyrightAlpha.animateTo(1f, tween(500))
        }

        // Navigate out
        delay(1800.milliseconds)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FeelioColors.BgLavender),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // "Bask Ai" title
            Text(
                text = "Bask Ai",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.5).sp
                ),
                color = FeelioColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                    scaleX = textScale.value
                    scaleY = textScale.value
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Simplified tagline
            Text(
                text = "Your daily calm companion",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.5.sp
                ),
                color = FeelioColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value * 0.8f // Slightly more transparent than main text
                }
            )
        }

        // Bottom copyright: © TensorMind Tech
        Text(
            text = "© TensorMind Tech",
            style = MaterialTheme.typography.labelSmall,
            color = FeelioColors.TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
                .graphicsLayer { alpha = copyrightAlpha.value }
        )
    }
}
