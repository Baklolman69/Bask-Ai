package com.tensormind.feelio.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.feelio.ui.theme.FeelioColors

data class Challenge(
    val id: String,
    val title: String,
    val color: Color
)

val challenges = listOf(
    Challenge("worries", "Worries", FeelioColors.ChallengeWorries),
    Challenge("motivation", "Motivation", FeelioColors.ChallengeMotivation),
    Challenge("confidence", "Confidence", FeelioColors.ChallengeConfidence),
    Challenge("sleep", "Sleep", FeelioColors.ChallengeSleep),
    Challenge("low_mood", "Low Mood", FeelioColors.ChallengeLowMood),
    Challenge("work_stress", "Work Stress", FeelioColors.ChallengeWorkStress),
    Challenge("relationships", "Relationships", FeelioColors.ChallengeRelationships),
    Challenge("exam_stress", "Exam Stress", FeelioColors.ChallengeExamStress)
)

@Composable
fun ChallengesScreen(
    onFinished: (List<String>) -> Unit,
    onMaybeLater: () -> Unit
) {
    val selectedIds = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "What would you like help\nwith?",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            color = Color(0xFF6E6A78), // Grayish text as in image
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Choose the challenges you relate to, and\nI'll support you through them",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
            color = Color(0xFF6E6A78),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(challenges) { challenge ->
                val isSelected = selectedIds.contains(challenge.id)
                ChallengeCard(
                    challenge = challenge,
                    isSelected = isSelected,
                    onClick = {
                        if (isSelected) {
                            selectedIds.remove(challenge.id)
                        } else {
                            selectedIds.add(challenge.id)
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selectedIds.isNotEmpty()) {
                Button(
                    onClick = { onFinished(selectedIds.toList()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FeelioColors.Cta,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            } else {
                Surface(
                    onClick = onMaybeLater,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(2.dp, Color(0xFF8B4513)), // Brownish border like in image
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Maybe later",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color(0xFF8B4513)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) FeelioColors.Cta else Color.Transparent,
        animationSpec = tween(200),
        label = "border_color"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        color = challenge.color,
        border = if (isSelected) BorderStroke(2.dp, borderColor) else null,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = Color(0xFF211E27),
                textAlign = TextAlign.Center
            )
        }
    }
}
