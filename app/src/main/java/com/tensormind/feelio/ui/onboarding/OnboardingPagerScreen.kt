package com.tensormind.feelio.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tensormind.feelio.ui.theme.FeelioColors
import com.tensormind.feelio.ui.theme.PillShape
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun OnboardingPagerScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit,
) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()

    // Page background colors for liquid color blending
    val pageColors = listOf(
        FeelioColors.BgPeach,
        FeelioColors.BgPowderBlue,
        FeelioColors.BgMint,
    )

    // Smooth interpolated background color during drag / page transition
    val currentBgColor = remember(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
        val position = pagerState.currentPage
        val offset = pagerState.currentPageOffsetFraction
        val startColor = pageColors.getOrElse(position) { pageColors.last() }

        if (offset > 0f && position < (pageColors.size - 1)) {
            lerp(startColor, pageColors[position + 1], offset)
        } else if (offset < 0f && position > 0) {
            lerp(startColor, pageColors[position - 1], abs(offset))
        } else {
            startColor
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(currentBgColor),
    ) {
        // HorizontalPager with 3D depth transforms
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (pagerState.currentPage - page + pagerState.currentPageOffsetFraction)
            val absOffset = abs(pageOffset)

            // Depth scale, alpha fade, subtle tilt, and spatial parallax
            val pageAlpha = (1f - absOffset * 0.5f).coerceIn(0.15f, 1f)
            val pageScale = (1f - absOffset * 0.10f).coerceIn(0.82f, 1f)
            val pageTilt = pageOffset * -3.5f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = pageAlpha
                        scaleX = pageScale
                        scaleY = pageScale
                        rotationZ = pageTilt
                        translationX = pageOffset * size.width * 0.18f
                    }
            ) {
                when (page) {
                    0 -> OnboardingScreenOne()
                    1 -> OnboardingScreenTwo()
                    2 -> OnboardingScreenThree()
                }
            }
        }

        // Top-Right "Skip" text button (pages 0–2)
        AnimatedVisibility(
            visible = pagerState.currentPage < 2,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
        ) {
            val skipInteractionSource = remember { MutableInteractionSource() }
            val isSkipPressed by skipInteractionSource.collectIsPressedAsState()
            val skipScale by animateFloatAsState(
                targetValue = if (isSkipPressed) 0.92f else 1.0f,
                animationSpec = tween(100),
                label = "skip_press_scale"
            )

            TextButton(
                onClick = onFinished,
                interactionSource = skipInteractionSource,
                modifier = Modifier
                    .padding(top = 16.dp, end = 18.dp)
                    .graphicsLayer {
                        scaleX = skipScale
                        scaleY = skipScale
                    }
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FeelioColors.TextSecondary
                )
            }
        }

        // Bottom Bar: Dot indicators + Next / Get Started controls
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            // Interactive dot indicators
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 28.dp else 8.dp,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "dot_width"
                    )
                    val dotColor by animateColorAsState(
                        targetValue = if (isSelected) FeelioColors.Cta else FeelioColors.Border,
                        animationSpec = tween(durationMillis = 300),
                        label = "dot_color"
                    )

                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(dotColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        page = index,
                                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                                    )
                                }
                            }
                    )
                }
            }

            // Next pill button with label + arrow (pages 0–1)
            AnimatedVisibility(
                visible = pagerState.currentPage < 2,
                enter = fadeIn(tween(250)) + scaleIn(tween(200)),
                exit = fadeOut(tween(180)) + scaleOut(tween(150)),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                val nextInteractionSource = remember { MutableInteractionSource() }
                val isNextPressed by nextInteractionSource.collectIsPressedAsState()
                val nextScale by animateFloatAsState(
                    targetValue = if (isNextPressed) 0.93f else 1.0f,
                    animationSpec = tween(100),
                    label = "next_press_scale"
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage + 1,
                                animationSpec = tween(350, easing = FastOutSlowInEasing)
                            )
                        }
                    },
                    interactionSource = nextInteractionSource,
                    modifier = Modifier
                        .height(52.dp)
                        .graphicsLayer {
                            scaleX = nextScale
                            scaleY = nextScale
                        },
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FeelioColors.Cta,
                        contentColor = FeelioColors.CtaText
                    )
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = FeelioColors.CtaText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Page",
                        tint = FeelioColors.CtaText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // "Get Started" CTA button (page 2)
            AnimatedVisibility(
                visible = pagerState.currentPage == 2,
                enter = fadeIn(tween(300)) + slideInVertically { it / 2 },
                exit = fadeOut(tween(180)) + slideOutVertically { it / 2 },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                val ctaInteractionSource = remember { MutableInteractionSource() }
                val isCtaPressed by ctaInteractionSource.collectIsPressedAsState()
                val ctaScale by animateFloatAsState(
                    targetValue = if (isCtaPressed) 0.95f else 1.0f,
                    animationSpec = tween(100),
                    label = "cta_press_scale"
                )

                Button(
                    onClick = onFinished,
                    interactionSource = ctaInteractionSource,
                    modifier = Modifier
                        .height(52.dp)
                        .graphicsLayer {
                            scaleX = ctaScale
                            scaleY = ctaScale
                        },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FeelioColors.Cta,
                        contentColor = FeelioColors.CtaText
                    )
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = FeelioColors.CtaText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Get Started",
                        tint = FeelioColors.CtaText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
