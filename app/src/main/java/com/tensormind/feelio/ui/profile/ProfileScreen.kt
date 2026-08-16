package com.tensormind.feelio.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.feelio.data.UserData
import com.tensormind.feelio.ui.theme.FeelioColors

@Composable
fun ProfileScreen(
    userData: UserData?,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onMyProgressClick: () -> Unit = {},
    onSupportClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FeelioColors.BgCream)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 8.dp, end = 24.dp, bottom = 24.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FeelioColors.TextPrimary
                )
            }
            
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = FeelioColors.TextPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        tint = FeelioColors.TextPrimary,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = userData?.name ?: "User",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.TextPrimary
            )
            
            Text(
                text = if (userData?.isGuest == true) "Guest Account" else "Bask Pro User",
                style = MaterialTheme.typography.bodyMedium,
                color = FeelioColors.TextSecondary
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            HorizontalDivider(color = FeelioColors.Border.copy(alpha = 0.5f), thickness = 0.5.dp)
            
            ProfileMenuItem("Account Settings", onClick = onSettingsClick)
            ProfileMenuItem("My Progress", onClick = onMyProgressClick)
            ProfileMenuItem("Privacy", onClick = onPrivacyClick)
            ProfileMenuItem("Support", onClick = onSupportClick)
        }
    }
}

@Composable
fun ProfileMenuItem(title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = FeelioColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )
            HorizontalDivider(color = FeelioColors.Border.copy(alpha = 0.5f), thickness = 0.5.dp)
        }
    }
}
