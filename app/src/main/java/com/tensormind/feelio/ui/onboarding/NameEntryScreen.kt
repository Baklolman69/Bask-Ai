package com.tensormind.feelio.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tensormind.feelio.R
import com.tensormind.feelio.ui.theme.FeelioColors
import com.tensormind.feelio.ui.theme.PillShape

@Composable
fun NameEntryScreen(
    onGuestContinue: (String) -> Unit,
    onGoogleLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.personwriting_pg2)
    )

    val contentAlpha = remember { Animatable(0f) }
    val contentTranslateY = remember { Animatable(28f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
        contentTranslateY.animateTo(0f, tween(450, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FeelioColors.BgCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Illustration & Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcoming Lottie Illustration with Soft Radial Aura
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        FeelioColors.CardMint.copy(alpha = 0.50f),
                                        FeelioColors.CardPink.copy(alpha = 0.30f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    LottieAnimation(
                        composition = lottieComposition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Subtitle
                Text(
                    text = "What should I call you?",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        lineHeight = 36.sp
                    ),
                    color = FeelioColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentTranslateY.value
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your name helps me personalize your calm journey.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FeelioColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentTranslateY.value
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Styled Name Input Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = contentAlpha.value
                        },
                    placeholder = {
                        Text(
                            text = "Enter your name",
                            color = FeelioColors.TextTertiary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Name Icon",
                            tint = FeelioColors.TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    trailingIcon = {
                        if (name.isNotEmpty()) {
                            IconButton(onClick = { name = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear name",
                                    tint = FeelioColors.TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (name.isNotBlank()) {
                                onGuestContinue(name)
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FeelioColors.Cta,
                        unfocusedBorderColor = FeelioColors.Border,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = FeelioColors.TextPrimary,
                        unfocusedTextColor = FeelioColors.TextPrimary,
                        cursorColor = FeelioColors.Cta
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = contentAlpha.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Continue with Google Button
                val googleInteractionSource = remember { MutableInteractionSource() }
                val isGooglePressed by googleInteractionSource.collectIsPressedAsState()
                val googleScale by animateFloatAsState(
                    targetValue = if (isGooglePressed) 0.96f else 1.0f,
                    animationSpec = tween(100),
                    label = "google_press_scale"
                )

                OutlinedButton(
                    onClick = onGoogleLogin,
                    interactionSource = googleInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .graphicsLayer {
                            scaleX = googleScale
                            scaleY = googleScale
                        },
                    shape = PillShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = FeelioColors.TextPrimary
                    ),
                    border = BorderStroke(1.5.dp, FeelioColors.Border)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        GoogleLogoIcon()
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = FeelioColors.TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Divider ("or")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = FeelioColors.Border,
                        thickness = 1.dp
                    )
                    Text(
                        text = "or",
                        modifier = Modifier.padding(horizontal = 14.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = FeelioColors.TextTertiary
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = FeelioColors.Border,
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Continue CTA Button
                val primaryInteractionSource = remember { MutableInteractionSource() }
                val isPrimaryPressed by primaryInteractionSource.collectIsPressedAsState()
                val primaryScale by animateFloatAsState(
                    targetValue = if (isPrimaryPressed) 0.96f else 1.0f,
                    animationSpec = tween(100),
                    label = "primary_press_scale"
                )

                val isNameFilled = name.isNotBlank()

                Button(
                    onClick = {
                        val finalName = if (isNameFilled) name.trim() else "Friend"
                        onGuestContinue(finalName)
                    },
                    interactionSource = primaryInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .graphicsLayer {
                            scaleX = primaryScale
                            scaleY = primaryScale
                        },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNameFilled) FeelioColors.Cta else Color(0xFFE8E3DA),
                        contentColor = if (isNameFilled) FeelioColors.CtaText else FeelioColors.TextSecondary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isNameFilled) "Continue" else "Continue as Guest",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = if (isNameFilled) FeelioColors.CtaText else FeelioColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continue",
                            tint = if (isNameFilled) FeelioColors.CtaText else FeelioColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Authentic styled Google logo badge drawn with Compose Canvas.
 */
@Composable
private fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(16.dp)) {
            val radius = size.minDimension / 2
            val center = this.center

            // Red arc (top left)
            drawArc(
                color = Color(0xFFEA4335),
                startAngle = 210f,
                sweepAngle = 90f,
                useCenter = true
            )
            // Yellow arc (bottom left)
            drawArc(
                color = Color(0xFFFBBC05),
                startAngle = 120f,
                sweepAngle = 90f,
                useCenter = true
            )
            // Green arc (bottom right)
            drawArc(
                color = Color(0xFF34A853),
                startAngle = 30f,
                sweepAngle = 90f,
                useCenter = true
            )
            // Blue arc (top right)
            drawArc(
                color = Color(0xFF4285F4),
                startAngle = -60f,
                sweepAngle = 90f,
                useCenter = true
            )
            // Inner white circle mask
            drawCircle(
                color = Color.White,
                radius = radius * 0.58f,
                center = center
            )
        }
    }
}
