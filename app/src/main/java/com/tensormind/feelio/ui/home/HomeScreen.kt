package com.tensormind.feelio.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tensormind.feelio.R
import com.tensormind.feelio.data.FirebaseRepository
import com.tensormind.feelio.data.GroqRepository
import com.tensormind.feelio.data.UserData
import com.tensormind.feelio.ui.theme.FeelioColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val sampleActivities = listOf(
    ActivityItem("Meditation for focus", FeelioColors.BgPeach, lottieRes = R.raw.mindfulness),
    ActivityItem("Hydration tracker", FeelioColors.BgMint, lottieRes = R.raw.glasswater),
    ActivityItem("Journal", FeelioColors.BgLavender, lottieRes = R.raw.card3), // Placeholder
    ActivityItem("CBT test", FeelioColors.CardMustard, lottieRes = R.raw.card4) // Placeholder
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData? = null,
    firebaseRepository: FirebaseRepository? = null,
    onProfileClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onCatClick: () -> Unit = {},
    onActivityClick: (ActivityItem) -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var historicalMoods by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var moodMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val scope = rememberCoroutineScope()
    var aiReflection by remember { mutableStateOf<GroqRepository.AiReflection?>(null) }
    var isGeneratingAi by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isAtBottom by remember {
        derivedStateOf {
            scrollState.value > 0 && scrollState.value >= scrollState.maxValue - 50
        }
    }

    LaunchedEffect(selectedDate, userData) {
        val uid = userData?.userId
        if (uid != null && firebaseRepository != null) {
            historicalMoods = firebaseRepository.getMoodLogsForDate(uid, selectedDate)
            moodMap = firebaseRepository.getAllMoodLogsMap(uid)
        }
    }

    if (showDatePicker) {
        EmojiCalendarDialog(
            initialDate = selectedDate,
            moodMap = moodMap,
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FeelioColors.BgCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
    ) {
        HomeHeader(
            selectedDate = selectedDate,
            onProfileClick = onProfileClick,
            onMenuClick = onMenuClick,
            onDateClick = { showDatePicker = true }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        GreetingSection(userName = userData?.name)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        ThoughtSection(
            onThoughtSubmitted = { thought ->
                isGeneratingAi = true
                scope.launch {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val todayKey = sdf.format(selectedDate)
                    val reflection = GroqRepository.getReflectionAndRecommendation(
                        userThought = thought,
                        currentMoodEmoji = moodMap[todayKey],
                        userName = userData?.name
                    )
                    aiReflection = reflection
                    isGeneratingAi = false
                    
                    userData?.userId?.let { uid ->
                        firebaseRepository?.saveThought(uid, thought, reflection.message, reflection.recommendedFeature)
                    }
                }
            }
        )

        if (isGeneratingAi) {
            Spacer(modifier = Modifier.height(16.dp))
            AiLoadingCard()
        } else if (aiReflection != null) {
            Spacer(modifier = Modifier.height(16.dp))
            AiRecommendationCard(
                reflection = aiReflection!!,
                onFeatureClick = { featureName ->
                    val matched = sampleActivities.find { it.title.equals(featureName, ignoreCase = true) }
                    if (matched != null) {
                        onActivityClick(matched)
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(28.dp))
        
        if (isSameDay(selectedDate, Date())) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayKey = sdf.format(selectedDate)
            val currentMoodEmoji = moodMap[todayKey]

            MoodLogSection(
                initialEmoji = currentMoodEmoji,
                onMoodSelected = { moodIndex, emoji ->
                    userData?.userId?.let { uid ->
                        firebaseRepository?.logMood(uid, moodIndex, emoji, date = selectedDate)
                        moodMap = moodMap + (todayKey to emoji)
                    }
                }
            )
        } else {
            HistoricalMoodSection(selectedDate, historicalMoods)
        }

        Spacer(modifier = Modifier.height(24.dp))

        FeaturedActivityCard(
            title = "AI Companion",
            subtitle = "Talk about what's on your mind.",
            onClick = {
                onActivityClick(ActivityItem("Just need to talk", FeelioColors.BgLavender, imageRes = R.drawable.talk_icon))
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ActivityGrid(onActivityClick = onActivityClick)

        Spacer(modifier = Modifier.height(60.dp)) // Extra space at bottom
        
        PeepingCatSection(isVisible = isAtBottom, onClick = onCatClick)
    }
}

@Composable
fun PeepingCatSection(isVisible: Boolean, onClick: () -> Unit) {
    val mascotComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.orangecatpeeping))
    val phrases = listOf("look for hooman", "pet me", "I'm watching you!", "Pspspsps!", "Give me treats!")
    var currentPhrase by remember { mutableStateOf(phrases[0]) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            while(true) {
                kotlinx.coroutines.delay(3000)
                currentPhrase = phrases.random()
            }
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 15.dp, bottom = 0.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
                contentAlignment = Alignment.BottomCenter
            ) {
                LottieAnimation(
                    composition = mascotComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(140.dp)
                )

                // Speech Bubble - Positioned on top of the cat
                Surface(
                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border),
                    modifier = Modifier
                        .padding(bottom = 90.dp) // Slightly adjusted for better overlap
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = currentPhrase,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = FeelioColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun AiLoadingCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF7F2EC),
        border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = FeelioColors.Cta,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Feelio AI is reflecting on your thoughts...",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = FeelioColors.TextSecondary
            )
        }
    }
}

@Composable
fun AiRecommendationCard(
    reflection: GroqRepository.AiReflection,
    onFeatureClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFAF4EE),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE8DCCF))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✨", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Feelio AI Companion",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = FeelioColors.TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = reflection.message,
                style = MaterialTheme.typography.bodyMedium,
                color = FeelioColors.TextPrimary,
                lineHeight = 22.sp
            )

            if (!reflection.recommendedFeature.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    onClick = { onFeatureClick(reflection.recommendedFeature) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = FeelioColors.Cta
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Recommended for you",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = reflection.recommendedFeature,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Try feature",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmojiCalendarDialog(
    initialDate: Date,
    moodMap: Map<String, String>,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    var calendar by remember {
        mutableStateOf(Calendar.getInstance().apply { time = initialDate })
    }
    
    val currentMonthCal = remember(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    val monthTitle = remember(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonthCal.time)
    }

    val daysInMonth = currentMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = currentMonthCal.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sun

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Month, Year & Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = FeelioColors.TextPrimary
                    )
                    Row {
                        IconButton(onClick = {
                            val prev = Calendar.getInstance().apply {
                                time = currentMonthCal.time
                                add(Calendar.MONTH, -1)
                            }
                            calendar = prev
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = FeelioColors.TextPrimary
                            )
                        }
                        IconButton(onClick = {
                            val next = Calendar.getInstance().apply {
                                time = currentMonthCal.time
                                add(Calendar.MONTH, 1)
                            }
                            calendar = next
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = FeelioColors.TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Days of Week Header
                val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    daysOfWeek.forEach { dayName ->
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = FeelioColors.TextSecondary,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calendar Days Grid
                val totalCells = firstDayOfWeek + daysInMonth
                val rows = (totalCells + 6) / 7

                Column {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - firstDayOfWeek + 1
                                
                                if (cellIndex < firstDayOfWeek || dayNum > daysInMonth) {
                                    Spacer(modifier = Modifier.weight(1f).height(44.dp))
                                } else {
                                    val dayCal = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, currentMonthCal.get(Calendar.YEAR))
                                        set(Calendar.MONTH, currentMonthCal.get(Calendar.MONTH))
                                        set(Calendar.DAY_OF_MONTH, dayNum)
                                    }
                                    val dateKey = sdf.format(dayCal.time)
                                    val emojiForDate = moodMap[dateKey]
                                    val isSelectedDay = isSameDay(dayCal.time, initialDate)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelectedDay) FeelioColors.Cta.copy(alpha = 0.15f)
                                                else Color.Transparent
                                            )
                                            .then(
                                                if (isSelectedDay) Modifier.border(2.dp, FeelioColors.Cta, CircleShape)
                                                else Modifier
                                            )
                                            .clickable {
                                                onDateSelected(dayCal.time)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (emojiForDate != null) {
                                            Text(
                                                text = emojiForDate,
                                                fontSize = 22.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            Text(
                                                text = dayNum.toString(),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelectedDay) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = FeelioColors.TextPrimary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(onClick = onDismiss) {
                        Text("OK", color = FeelioColors.Cta, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
           cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
}

@Composable
fun HistoricalMoodSection(date: Date, moods: List<Map<String, Any>>) {
    val dateStr = SimpleDateFormat("MMMM d", Locale.getDefault()).format(date)
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Mood for $dateStr",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = FeelioColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
        ) {
            if (moods.isEmpty()) {
                Text(
                    text = "No mood logged for this day.",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = FeelioColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            } else {
                Column(modifier = Modifier.padding(20.dp)) {
                    moods.forEach { mood ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = mood["emoji"] as? String ?: "", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "You felt this way on $dateStr",
                                style = MaterialTheme.typography.bodyLarge,
                                color = FeelioColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    selectedDate: Date,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
    onDateClick: () -> Unit
) {
    val dateStr = remember(selectedDate) {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(selectedDate)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable { onProfileClick() },
            shape = CircleShape,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = FeelioColors.TextPrimary
                )
            }
        }
        
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border),
            modifier = Modifier.clickable { onDateClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = FeelioColors.TextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Date",
                    tint = FeelioColors.TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable { onMenuClick() },
            shape = CircleShape,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = FeelioColors.TextPrimary
                )
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String? = null) {
    val hourOfDay = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    
    val timeGreeting = remember(hourOfDay) {
        when (hourOfDay) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    val subtitleGreeting = remember(hourOfDay) {
        when (hourOfDay) {
            in 22..23, in 0..4 -> "Ready to unwind for\nthe night?"
            in 17..21 -> "How was your day\ntoday?"
            else -> "How are you feeling\ntoday?"
        }
    }

    val cleanName = userName?.trim()?.takeIf { it.isNotBlank() }
    val fullGreeting = if (cleanName != null) "$timeGreeting, $cleanName" else "$timeGreeting,"

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = fullGreeting,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            color = FeelioColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitleGreeting,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            color = FeelioColors.TextPrimary
        )
    }
}

@Composable
fun ThoughtSection(onThoughtSubmitted: (String) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var currentThought by remember { mutableStateOf("") }
    var lastSavedThought by remember { mutableStateOf("") }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Your Thought",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = FeelioColors.TextPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Tell Feelio AI what's on your mind. You'll receive a warm reflection and personalized app recommendation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FeelioColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = currentThought,
                        onValueChange = { currentThought = it },
                        placeholder = { Text("e.g. Feeling stressed about work, or had a peaceful morning walk...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FeelioColors.Cta,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        if (currentThought.isNotBlank()) {
                            onThoughtSubmitted(currentThought)
                            lastSavedThought = currentThought
                            currentThought = ""
                        }
                        showDialog = false
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = FeelioColors.Cta,
                        contentColor = Color.White
                    )
                ) {
                    Text("Reflect with AI ✨", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = FeelioColors.TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color(0xFFF3E5E0).copy(alpha = 0.5f),
                spotColor = Color(0xFFF3E5E0)
            )
            .clickable { showDialog = true },
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFFF3E5E0).copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, FeelioColors.Border.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (lastSavedThought.isNotBlank()) "\"$lastSavedThought\"" else "Your thought...",
                style = MaterialTheme.typography.bodyLarge,
                color = if (lastSavedThought.isNotBlank()) FeelioColors.TextPrimary else FeelioColors.TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Add thought",
                tint = FeelioColors.TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class ParticleItem(
    val id: Long,
    val emoji: String,
    val animY: Animatable<Float, AnimationVector1D>,
    val animAlpha: Animatable<Float, AnimationVector1D>
)

@Composable
fun MoodLogSection(
    initialEmoji: String? = null,
    onMoodSelected: (Int, String) -> Unit = { _, _ -> }
) {
    val moods = remember {
        listOf(
            Triple(FeelioColors.MoodHappy, "😊", "Feeling Happy! ✨"),
            Triple(FeelioColors.MoodSad, "😞", "Feeling Sad 💙"),
            Triple(FeelioColors.MoodNeutral, "😐", "Feeling Neutral 🍃"),
            Triple(FeelioColors.MoodAngry, "😠", "Feeling Angry ⚡"),
            Triple(FeelioColors.MoodTired, "😴", "Feeling Tired 🌙")
        )
    }

    var selectedMood by remember(initialEmoji) {
        mutableStateOf(moods.indexOfFirst { it.second == initialEmoji })
    }

    var particles by remember { mutableStateOf<List<ParticleItem>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Daily mood log",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                moods.forEachIndexed { index, (color, emoji, _) ->
                    val isSelected = selectedMood == index

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.25f else if (selectedMood != -1) 0.90f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "emoji_scale_$index"
                    )

                    val alpha by animateFloatAsState(
                        targetValue = if (selectedMood == -1 || isSelected) 1.0f else 0.45f,
                        animationSpec = tween(300),
                        label = "emoji_alpha_$index"
                    )

                    val containerColor by animateColorAsState(
                        targetValue = if (isSelected) color else color.copy(alpha = 0.35f),
                        animationSpec = tween(300),
                        label = "emoji_bg_$index"
                    )

                    val shadowElevation by animateDpAsState(
                        targetValue = if (isSelected) 10.dp else 0.dp,
                        animationSpec = tween(200),
                        label = "emoji_shadow_$index"
                    )

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                            .size(56.dp)
                            .shadow(shadowElevation, CircleShape)
                            .clip(CircleShape)
                            .background(containerColor)
                            .clickable {
                                selectedMood = index
                                onMoodSelected(index, emoji)

                                // Trigger particle burst animation
                                val newParticles = (0..2).map { pIndex ->
                                    ParticleItem(
                                        id = System.currentTimeMillis() + pIndex,
                                        emoji = emoji,
                                        animY = Animatable(0f),
                                        animAlpha = Animatable(1f)
                                    )
                                }
                                particles = newParticles
                                newParticles.forEach { particle ->
                                    scope.launch {
                                        launch { particle.animY.animateTo(-60f, tween(500)) }
                                        launch { particle.animAlpha.animateTo(0f, tween(500)) }
                                    }
                                }
                            }
                            .then(
                                if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 28.sp)
                    }
                }
            }

            // Floating particles burst animation
            particles.forEach { particle ->
                Text(
                    text = particle.emoji,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = particle.animY.value
                            alpha = particle.animAlpha.value
                        }
                )
            }
        }

        // Animated Mood Chip Label
        Spacer(modifier = Modifier.height(14.dp))

        AnimatedVisibility(
            visible = selectedMood in moods.indices,
            enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { 20 }),
            exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { 20 })
        ) {
            if (selectedMood in moods.indices) {
                val currentMood = moods[selectedMood]
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = currentMood.first.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, currentMood.first.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentMood.third,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = FeelioColors.TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedActivityCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val mascotComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_mascot))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color(0xFF9E86F0).copy(alpha = 0.5f),
                spotColor = Color(0xFF9E86F0)
            ),
        shape = RoundedCornerShape(32.dp),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF3EFFF) // Very faint lavender at bottom
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = FeelioColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FeelioColors.TextSecondary,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Surface(
                        onClick = onClick,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF9E86F0), // AI Theme Purple
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "Chat Now",
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    LottieAnimation(
                        composition = mascotComposition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(140.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityGrid(onActivityClick: (ActivityItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        items(sampleActivities) { activity ->
            ActivityCard(activity, onActivityClick)
        }
    }
}

data class ActivityItem(
    val title: String, 
    val color: Color, 
    val lottieRes: Int? = null,
    val imageRes: Int? = null
)

@Composable
fun ActivityCard(activity: ActivityItem, onClick: (ActivityItem) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = activity.color.copy(alpha = 0.5f),
                spotColor = activity.color
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default ripple to use our custom feedback
                onClick = { onClick(activity) }
            ),
        shape = RoundedCornerShape(24.dp),
        color = activity.color
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            if (activity.lottieRes != null) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(activity.lottieRes))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.Center)
                )
            } else if (activity.imageRes != null) {
                Image(
                    painter = painterResource(id = activity.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                )
            }

            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = FeelioColors.TextPrimary,
                modifier = Modifier.align(Alignment.TopStart)
            )
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPressed) Color.Black.copy(alpha = 0.15f)
                        else Color.White.copy(alpha = 0.3f)
                    )
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Start",
                    tint = FeelioColors.TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
