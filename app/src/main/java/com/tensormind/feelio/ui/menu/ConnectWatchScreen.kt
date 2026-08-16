package com.tensormind.feelio.ui.menu

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ListenerRegistration
import com.tensormind.feelio.data.FirebaseRepository
import com.tensormind.feelio.data.GroqRepository
import com.tensormind.feelio.data.PairResult
import com.tensormind.feelio.data.UserData
import com.tensormind.feelio.ui.theme.FeelioColors
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ConnectWatchScreen(
    userData: UserData? = null,
    firebaseRepository: FirebaseRepository? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pairingCodeInput by remember { mutableStateOf("") }
    var activePairingCode by remember { mutableStateOf("") }
    var isPaired by remember { mutableStateOf(false) }
    var isPairingLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Live Sync Messages Stream
    var liveSyncMessages by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var outgoingMessage by remember { mutableStateOf("") }

    // Biometrics State
    var bpm by remember { mutableIntStateOf(74) }
    var spO2 by remember { mutableIntStateOf(98) }
    var sleepHours by remember { mutableStateOf(7.5f) }

    // Groq AI State
    var isAnalyzing by remember { mutableStateOf(false) }
    var aiBiometricAnalysis by remember { mutableStateOf<String?>(null) }

    // Subscribe to Firestore /paired_devices/{code}/sync_data when paired
    DisposableEffect(isPaired, activePairingCode) {
        var listener: ListenerRegistration? = null
        if (isPaired && activePairingCode.isNotBlank() && firebaseRepository != null) {
            listener = firebaseRepository.subscribeToWatchSyncData(activePairingCode) { updatedList ->
                liveSyncMessages = updatedList
            }
        }
        onDispose {
            listener?.remove()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FeelioColors.BgCream)
            .verticalScroll(scrollState)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
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
                text = if (isPaired) "Bask Watch Workspace" else "Pair with Bask Wearable",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.TextPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = if (isPaired) 
                    "Real-time bi-directional sync active with your Bask Wearable smartwatch."
                else 
                    "Enter the 6-digit code shown on your Pixel Watch / Bask Wearable screen",
                style = MaterialTheme.typography.bodyLarge,
                color = FeelioColors.TextSecondary
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            // Pairing Input Section
            if (!isPaired) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "6-Digit Watch Pairing Code",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = FeelioColors.TextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = pairingCodeInput,
                            onValueChange = { input ->
                                // Auto format as XXX-XXX
                                val digits = input.filter { it.isDigit() }.take(6)
                                pairingCodeInput = if (digits.length > 3) "${digits.substring(0, 3)}-${digits.substring(3)}" else digits
                            },
                            placeholder = { Text("e.g. 481-920", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                letterSpacing = 3.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FeelioColors.Cta,
                                unfocusedBorderColor = FeelioColors.Border,
                                focusedContainerColor = Color(0xFFFAF4EE),
                                unfocusedContainerColor = Color(0xFFFAF4EE)
                            )
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage!!,
                                color = Color(0xFFD32F2F),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (pairingCodeInput.isBlank()) {
                                    errorMessage = "Please enter the 6-digit code from your watch."
                                    return@Button
                                }
                                isPairingLoading = true
                                errorMessage = null

                                scope.launch {
                                    val result = firebaseRepository?.pairWithWatch(pairingCodeInput)
                                        ?: PairResult.Success(pairingCodeInput)

                                    isPairingLoading = false
                                    when (result) {
                                        is PairResult.Success -> {
                                            activePairingCode = result.pairingCode
                                            isPaired = true
                                            Toast.makeText(context, "Watch paired successfully!", Toast.LENGTH_SHORT).show()
                                            userData?.userId?.let { uid ->
                                                firebaseRepository?.saveWatchPairing(uid, activePairingCode, true)
                                                firebaseRepository?.saveBiometricSnapshot(uid, bpm, spO2, sleepHours)
                                            }
                                        }
                                        is PairResult.Error -> {
                                            errorMessage = result.message
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FeelioColors.Cta,
                                contentColor = Color.White
                            )
                        ) {
                            if (isPairingLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Verifying Handshake...")
                            } else {
                                Text("Pair Watch", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            } else {
                // Connected Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8F5E9),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF81C784))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Connected",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "✓ Synced with Bask Wearable",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "Pairing ID: $activePairingCode • /paired_devices/$activePairingCode/sync_data",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Live Bi-Directional Message Stream Workspace
            if (isPaired) {
                Text(
                    text = "Live Sync Workspace",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = FeelioColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Real-time Bi-directional Messages",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = FeelioColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Message List Container
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color(0xFFFAF7F2), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (liveSyncMessages.isEmpty()) {
                                Text(
                                    text = "No messages yet. Send a message to watch or trigger a watch tile log...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FeelioColors.TextTertiary
                                )
                            } else {
                                liveSyncMessages.forEach { msg ->
                                    val text = msg["text"] as? String ?: ""
                                    val sender = msg["sender"] as? String ?: "Unknown"
                                    val isMe = sender == "Mobile App"
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isMe) FeelioColors.Cta else Color(0xFFE0E0E0)
                                        ) {
                                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                                Text(
                                                    text = sender,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isMe) Color.White.copy(alpha = 0.8f) else Color.DarkGray
                                                )
                                                Text(
                                                    text = text,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = if (isMe) Color.White else Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Send Input Box
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = outgoingMessage,
                                onValueChange = { outgoingMessage = it },
                                placeholder = { Text("Send message to watch...") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FeelioColors.Cta,
                                    unfocusedBorderColor = FeelioColors.Border
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (outgoingMessage.isNotBlank()) {
                                        firebaseRepository?.sendSyncMessageToWatch(activePairingCode, outgoingMessage)
                                        outgoingMessage = ""
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = FeelioColors.Cta
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Biometrics Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BiometricStatCard(
                        title = "Heart Rate",
                        value = "$bpm",
                        unit = "BPM",
                        iconEmoji = "❤️",
                        modifier = Modifier.weight(1f)
                    )
                    BiometricStatCard(
                        title = "SpO2 Level",
                        value = "$spO2%",
                        unit = "Normal",
                        iconEmoji = "🫁",
                        modifier = Modifier.weight(1f)
                    )
                    BiometricStatCard(
                        title = "Sleep",
                        value = "${sleepHours}h",
                        unit = "Restful",
                        iconEmoji = "🌙",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Heart Rate Graph Card
                HeartRateGraphCard(bpm = bpm)

                Spacer(modifier = Modifier.height(20.dp))

                // Groq AI Biometric Analysis Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFFAF4EE),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE8DCCF))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Groq AI Biometric Insights",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = FeelioColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (aiBiometricAnalysis != null) {
                            Text(
                                text = aiBiometricAnalysis!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = FeelioColors.TextPrimary,
                                lineHeight = 22.sp
                            )
                        } else {
                            Text(
                                text = "Analyze how your sleep duration ($sleepHours hrs) and heart rate ($bpm BPM) correlate with your daily emotional resilience.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FeelioColors.TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isAnalyzing = true
                                scope.launch {
                                    val analysis = GroqRepository.getBiometricAnalysis(
                                        bpm = bpm,
                                        spO2 = spO2,
                                        sleepHours = sleepHours,
                                        userName = userData?.name
                                    )
                                    aiBiometricAnalysis = analysis
                                    isAnalyzing = false
                                    userData?.userId?.let { uid ->
                                        firebaseRepository?.saveBiometricSnapshot(uid, bpm, spO2, sleepHours, analysis)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FeelioColors.Cta,
                                contentColor = Color.White
                            )
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Analyzing Wearable Data...")
                            } else {
                                Text("Analyze Biometrics with Groq AI ✨", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Step by Step Setup Instructions
            Text(
                text = "Setup Instructions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            WatchStep(
                number = "1",
                title = "Install Bask Ai on Wear OS",
                description = "Search for 'Bask Ai' on your watch's Play Store and install the app."
            )
            
            WatchStep(
                number = "2",
                title = "Link with Phone",
                description = "Open Bask Ai on your watch and enter the 6-digit pairing code shown on screen."
            )
            
            WatchStep(
                number = "3",
                title = "Grant Permissions",
                description = "Grant Health Connect permissions to sync BPM, Sleep, and SpO2 in real-time."
            )
            
            WatchStep(
                number = "4",
                title = "Use Watch Tiles",
                description = "Add the Bask Ai Tile to your watch face for quick 1-tap mood logging throughout the day."
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun BiometricStatCard(
    title: String,
    value: String,
    unit: String,
    iconEmoji: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = iconEmoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.TextPrimary
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = FeelioColors.TextSecondary
            )
        }
    }
}

@Composable
fun HeartRateGraphCard(bpm: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "24h Heart Rate Trend",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = FeelioColors.TextPrimary
                )
                Text(
                    text = "Avg $bpm BPM",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = FeelioColors.Cta
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                val width = size.width
                val height = size.height
                val points = listOf(0.6f, 0.45f, 0.7f, 0.35f, 0.5f, 0.4f, 0.65f, 0.3f, 0.55f, 0.45f)
                val path = Path()

                points.forEachIndexed { i, factor ->
                    val x = i * (width / (points.size - 1))
                    val y = height * factor
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = FeelioColors.Cta,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun WatchStep(number: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = FeelioColors.Cta
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = FeelioColors.TextSecondary
            )
        }
    }
}
