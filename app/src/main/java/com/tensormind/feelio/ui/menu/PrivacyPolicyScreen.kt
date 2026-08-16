package com.tensormind.feelio.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.feelio.ui.theme.FeelioColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PolicySection(
                "Data Privacy",
                "At Bask Ai, your privacy is our top priority. We believe that your mental wellness data is personal and should remain under your control."
            )
            PolicySection(
                "Biometric Data",
                "When you connect a smartwatch, we sync BPM, Sleep, and SpO2 data to provide AI summaries. This data is encrypted and used only to enhance your personalized experience."
            )
            PolicySection(
                "AI Processing",
                "Your thoughts and moods are analyzed by our secure AI models (Bask AI) to provide reflections. We do not sell your personal data to third parties."
            )
            PolicySection(
                "Security",
                "We use industry-standard security measures, including Firebase authentication and Firestore encryption, to protect your account information."
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Last updated: August 2026",
                style = MaterialTheme.typography.labelSmall,
                color = FeelioColors.TextTertiary
            )
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = FeelioColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = FeelioColors.TextSecondary
        )
    }
}
