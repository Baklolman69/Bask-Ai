package com.tensormind.feelio.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tensormind.feelio.R
import com.tensormind.feelio.ui.theme.FeelioColors

@Composable
fun OnboardingScreenTwo(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.personwriting_pg2))

    // Entrance animation for text content
    val headlineAlpha = remember { Animatable(0f) }
    val headlineTranslateY = remember { Animatable(30f) }
    val subtextAlpha = remember { Animatable(0f) }
    val subtextTranslateY = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        headlineAlpha.animateTo(1f, tween(450, delayMillis = 200, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        headlineTranslateY.animateTo(0f, tween(450, delayMillis = 200, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        subtextAlpha.animateTo(1f, tween(400, delayMillis = 380, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        subtextTranslateY.animateTo(0f, tween(400, delayMillis = 380, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Lottie illustration area (~52%)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.52f),
            contentAlignment = Alignment.Center
        ) {
            // Ambient aura
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                FeelioColors.CardBlue.copy(alpha = 0.40f),
                                Color.Transparent
                            )
                        )
                    )
            )

            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxHeight(0.90f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Headline
        Text(
            text = "Understand your\npatterns",
            style = MaterialTheme.typography.headlineLarge,
            color = FeelioColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = headlineAlpha.value
                    translationY = headlineTranslateY.value
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtext
        Text(
            text = "Track how you feel, spot trends,\nand see your progress over time.",
            style = MaterialTheme.typography.bodyLarge,
            color = FeelioColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = subtextAlpha.value
                    translationY = subtextTranslateY.value
                }
        )
    }
}
