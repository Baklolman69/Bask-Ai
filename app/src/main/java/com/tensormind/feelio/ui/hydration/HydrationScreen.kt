package com.tensormind.feelio.ui.hydration

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.tensormind.feelio.R
import com.tensormind.feelio.data.FirebaseRepository
import com.tensormind.feelio.data.GroqRepository
import com.tensormind.feelio.data.UserData
import com.tensormind.feelio.ui.theme.FeelioColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationScreen(
    userData: UserData?,
    firebaseRepository: FirebaseRepository,
    onBack: () -> Unit
) {
    var glassCount by remember { mutableIntStateOf(0) }
    var aiCompliment by remember { mutableStateOf("Ready for your first glass? ✨") }
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var hydrationGoal by remember { mutableStateOf<Int?>(null) }
    var goalExplanation by remember { mutableStateOf("") }
    var isLoadingGoal by remember { mutableStateOf(true) }

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.glasswater))

    // Initial load from Firebase
    LaunchedEffect(userData) {
        userData?.userId?.let { uid ->
            glassCount = firebaseRepository.getWaterIntake(uid)
            val goalData = firebaseRepository.getHydrationGoal(uid)
            if (goalData != null) {
                hydrationGoal = goalData.first
                goalExplanation = goalData.second
            }
            isLoadingGoal = false
        }
    }

    BackHandler { onBack() }

    if (isLoadingGoal) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = FeelioColors.Cta)
        }
    } else if (hydrationGoal == null) {
        HydrationSetupScreen(
            userName = userData?.name,
            onGoalGenerated = { goal, explanation ->
                hydrationGoal = goal
                goalExplanation = explanation
                userData?.userId?.let { uid ->
                    firebaseRepository.saveHydrationGoal(uid, goal, explanation)
                }
            }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Hydration Tracker", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { hydrationGoal = null }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Redo Setup",
                                tint = FeelioColors.TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = FeelioColors.BgCream
                    )
                )
            },
            containerColor = FeelioColors.BgCream
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie Glass
                LottieAnimation(
                    composition = lottieComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(220.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        onClick = {
                            if (glassCount > 0) {
                                glassCount--
                                userData?.userId?.let { firebaseRepository.logWater(it, glassCount) }
                            }
                        },
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FeelioColors.TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val countScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
                            label = "count_scale"
                        )
                        Text(
                            text = glassCount.toString(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 64.sp
                            ),
                            color = FeelioColors.TextPrimary,
                            modifier = Modifier.graphicsLayer {
                                scaleX = countScale
                                scaleY = countScale
                            }
                        )
                        Text(
                            text = "Glasses today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FeelioColors.TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    Surface(
                        onClick = {
                            glassCount++
                            userData?.userId?.let { firebaseRepository.logWater(it, glassCount) }

                            // Generate AI Compliment
                            isGenerating = true
                            scope.launch {
                                val compliment = GroqRepository.getHydrationCompliment(glassCount, userData?.name)
                                aiCompliment = compliment
                                isGenerating = false
                            }
                        },
                        shape = CircleShape,
                        color = FeelioColors.Cta,
                        shadowElevation = 6.dp,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // AI Feedback Area
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = Color(0xFF9E86F0).copy(alpha = 0.2f),
                            spotColor = Color(0xFF9E86F0).copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.background(
                            Brush.verticalGradient(
                                colors = listOf(Color.White, Color(0xFFF9F7FF))
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BASK AI PERSONALIZED PLAN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF9E86F0)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = goalExplanation,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = FeelioColors.TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            AnimatedContent(
                                targetState = isGenerating,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "ai_text"
                            ) { loading ->
                                if (loading) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFF9E86F0))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Thinking...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FeelioColors.TextSecondary
                                        )
                                    }
                                } else {
                                    Surface(
                                        color = Color(0xFFF3EFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = aiCompliment,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF7B5EDC),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Goal Progress
                val progress = (glassCount / (hydrationGoal?.toFloat() ?: 8f)).coerceIn(0f, 1f)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Daily Goal",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = FeelioColors.TextPrimary
                            )
                            Text(
                                text = "$hydrationGoal glasses",
                                style = MaterialTheme.typography.bodySmall,
                                color = FeelioColors.TextSecondary
                            )
                        }
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = Color(0xFF64B5F6)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(CircleShape),
                        color = Color(0xFF64B5F6),
                        trackColor = Color.LightGray.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}
