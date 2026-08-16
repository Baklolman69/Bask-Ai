package com.tensormind.feelio.ui.hydration

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.tensormind.feelio.R
import com.tensormind.feelio.data.GroqRepository
import com.tensormind.feelio.ui.theme.FeelioColors
import kotlinx.coroutines.launch

@Composable
fun HydrationSetupScreen(
    userName: String?,
    onGoalGenerated: (Int, String) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 5
    
    val surveyData = remember { mutableStateMapOf<String, String>() }
    var isCalculating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val mascotComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_mascot))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FeelioColors.BgCream)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isCalculating) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // AI Mascot Small
            LottieAnimation(
                composition = mascotComposition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Let's personalize your plan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.Cta.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(horizontal = 40.dp)
                    .shadow(4.dp, RoundedCornerShape(4.dp)),
                color = FeelioColors.Cta,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "step_content"
            ) { step ->
                when (step) {
                    1 -> SetupStep(
                        title = "Why do you want to track hydration?",
                        options = listOf("Feel healthier", "Better skin", "Improve focus", "Weight management"),
                        onOptionSelected = {
                            surveyData["why"] = it
                            currentStep++
                        }
                    )
                    2 -> SetupStep(
                        title = "What is your main health goal?",
                        options = listOf("General Wellness", "Athletic Performance", "Recovery", "Detox"),
                        onOptionSelected = {
                            surveyData["motivation"] = it
                            currentStep++
                        }
                    )
                    3 -> SetupStep(
                        title = "How much water do you currently drink?",
                        options = listOf("Hardly any", "1-3 glasses", "4-6 glasses", "7+ glasses"),
                        onOptionSelected = {
                            surveyData["current_intake"] = it
                            currentStep++
                        }
                    )
                    4 -> SetupStep(
                        title = "Any health conditions to consider?",
                        options = listOf("None", "Kidney/Heart health", "Pregnant/Nursing", "Highly active lifestyle"),
                        onOptionSelected = {
                            surveyData["health_condition"] = it
                            currentStep++
                        }
                    )
                    5 -> SetupStep(
                        title = "Typical temperature around you?",
                        options = listOf("Cold", "Moderate", "Warm", "Very Hot / Humid"),
                        onOptionSelected = {
                            surveyData["temperature"] = it
                            isCalculating = true
                            scope.launch {
                                val result = GroqRepository.generateHydrationGoal(surveyData.toMap(), userName)
                                onGoalGenerated(result.goal, result.explanation)
                            }
                        }
                    )
                }
            }
        } else {
            CalculatingState()
        }
    }
}

@Composable
fun SetupStep(
    title: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 32.sp
            ),
            color = FeelioColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        options.forEach { option ->
            Surface(
                onClick = { onOptionSelected(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF9E86F0).copy(alpha = 0.2f),
                        spotColor = Color(0xFF9E86F0).copy(alpha = 0.4f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(22.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = FeelioColors.TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CalculatingState() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_mascot))
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        CircularProgressIndicator(color = Color(0xFF9E86F0))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Bask AI is analyzing\nyour profile...",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = FeelioColors.TextPrimary,
            textAlign = TextAlign.Center
        )
    }
}
