package com.tensormind.feelio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.tensormind.feelio.data.FirebaseRepository
import com.tensormind.feelio.data.UserData
import com.tensormind.feelio.data.UserPreferencesRepository
import com.tensormind.feelio.ui.home.HomeScreen
import com.tensormind.feelio.ui.hydration.HydrationScreen
import com.tensormind.feelio.ui.menu.AboutScreen
import com.tensormind.feelio.ui.menu.MenuScreen
import com.tensormind.feelio.ui.menu.PrivacyPolicyScreen
import com.tensormind.feelio.ui.menu.SettingsScreen
import com.tensormind.feelio.ui.onboarding.ChallengesScreen
import com.tensormind.feelio.ui.onboarding.NameEntryScreen
import com.tensormind.feelio.ui.onboarding.OnboardingPagerScreen
import com.tensormind.feelio.ui.profile.ProfileScreen
import com.tensormind.feelio.ui.splash.SplashScreen
import com.tensormind.feelio.ui.theme.FeelioTheme
import com.tensormind.feelio.ui.webview.WebViewScreen
import com.tensormind.feelio.worker.HydrationWorker
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import androidx.work.*
object WebAppUrls {
    const val COMPANION = "https://your-companion-app.example.com"
    const val MEDITATION = "https://your-meditation-app.example.com"
    const val VENT = "https://your-vent-space.example.com"
    const val CBT = "https://your-cbt-assessment.example.com"
    const val JOURNAL = "https://your-journal-app.example.com"
}

enum class AppScreen {
    Splash,
    Onboarding,
    NameEntry,
    Challenges,
    Home,
    WebView,
    Profile,
    Menu,
    ConnectWatch,
    Hydration,
    Settings,
    PrivacyPolicy,
    About
}

class MainActivity : ComponentActivity() {
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferencesRepository = UserPreferencesRepository(this)
        enableEdgeToEdge()

        setupHydrationReminders()

        // Hide system bars and status bar for immersive full-screen mode on all pages
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            FeelioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FeelioApp(userPreferencesRepository, firebaseRepository)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Re-enforce hiding system bars when window gains focus
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupHydrationReminders() {
        val workRequest = PeriodicWorkRequestBuilder<HydrationWorker>(3, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "hydration_reminders",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun FeelioApp(userPreferencesRepository: UserPreferencesRepository, firebaseRepository: FirebaseRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userData by userPreferencesRepository.userData.collectAsState(initial = null)
    
    var currentScreen by remember { mutableStateOf(AppScreen.Splash) }
    var webViewUrl by remember { mutableStateOf("") }

    LaunchedEffect(userData) {
        if (userData?.isLoggedIn == true && currentScreen == AppScreen.Splash) {
            // If already logged in, we can skip to home after splash or immediately
            // For now, let splash finish normally then we'll decide
        }
    }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 400),
        label = "screen_crossfade",
    ) { screen ->
        when (screen) {
            AppScreen.Splash -> {
                SplashScreen {
                    if (userData?.isLoggedIn == true) {
                        currentScreen = AppScreen.Home
                    } else {
                        currentScreen = AppScreen.Onboarding
                    }
                }
            }
            AppScreen.Onboarding -> {
                OnboardingPagerScreen {
                    currentScreen = AppScreen.NameEntry
                }
            }
            AppScreen.NameEntry -> {
                NameEntryScreen(
                    onGuestContinue = { name ->
                        scope.launch {
                            val firebaseUid = firebaseRepository.ensureAuthenticated()
                            val cleanName = name.trim().ifBlank { "Guest" }
                            val userId = cleanName
                            val user = UserData(userId, cleanName, isLoggedIn = true, isGuest = true)
                            userPreferencesRepository.saveUser(userId, cleanName, isGuest = true)
                            firebaseRepository.createUser(user)
                            currentScreen = AppScreen.Challenges
                        }
                    },
                    onGoogleLogin = {
                        scope.launch {
                            val firebaseUid = firebaseRepository.ensureAuthenticated()
                            val userId = if (firebaseUid.isNotBlank()) firebaseUid else UUID.randomUUID().toString()
                            val user = UserData(userId, "Google User", isLoggedIn = true, isGuest = false)
                            userPreferencesRepository.saveUser(userId, "Google User", isGuest = false)
                            firebaseRepository.createUser(user)
                            currentScreen = AppScreen.Challenges
                        }
                    }
                )
            }
            AppScreen.Challenges -> {
                ChallengesScreen(
                    onFinished = { selectedChallenges ->
                        userData?.userId?.let { uid ->
                            firebaseRepository.saveChallenges(uid, selectedChallenges)
                        }
                        currentScreen = AppScreen.Home
                    },
                    onMaybeLater = {
                        userData?.userId?.let { uid ->
                            firebaseRepository.saveChallenges(uid, emptyList())
                        }
                        currentScreen = AppScreen.Home
                    }
                )
            }
            AppScreen.Home -> {
                HomeScreen(
                    userData = userData,
                    firebaseRepository = firebaseRepository,
                    onProfileClick = { currentScreen = AppScreen.Profile },
                    onMenuClick = { currentScreen = AppScreen.Menu },
                    onCatClick = {
                        webViewUrl = WebAppUrls.COMPANION
                        currentScreen = AppScreen.WebView
                    },
                    onActivityClick = { activity ->
                        userData?.userId?.let { uid ->
                            firebaseRepository.trackUsage(uid, activity.title)
                        }
                        when (activity.title) {
                            "Meditation for focus" -> {
                                webViewUrl = WebAppUrls.MEDITATION
                                currentScreen = AppScreen.WebView
                            }
                            "Just need to talk" -> {
                                webViewUrl = WebAppUrls.VENT
                                currentScreen = AppScreen.WebView
                            }
                            "CBT test" -> {
                                webViewUrl = WebAppUrls.CBT
                                currentScreen = AppScreen.WebView
                            }
                            "Hydration tracker" -> {
                                currentScreen = AppScreen.Hydration
                            }
                            "Journal" -> {
                                webViewUrl = WebAppUrls.JOURNAL
                                currentScreen = AppScreen.WebView
                            }
                        }
                    }
                )
            }
            AppScreen.WebView -> {
                WebViewScreen(
                    url = webViewUrl,
                    onBack = {
                        currentScreen = AppScreen.Home
                    }
                )
            }
            AppScreen.Profile -> {
                ProfileScreen(
                    userData = userData,
                    onBack = { currentScreen = AppScreen.Home },
                    onSettingsClick = { currentScreen = AppScreen.Settings },
                    onPrivacyClick = { currentScreen = AppScreen.PrivacyPolicy },
                    onMyProgressClick = { /* Placeholder for future feature */ },
                    onSupportClick = { /* Placeholder for future feature */ }
                )
            }
            AppScreen.Menu -> {
                MenuScreen(
                    onBack = { currentScreen = AppScreen.Home },
                    onConnectWatchClick = { currentScreen = AppScreen.ConnectWatch },
                    onSettingsClick = { currentScreen = AppScreen.Settings },
                    onPrivacyPolicyClick = { currentScreen = AppScreen.PrivacyPolicy },
                    onAboutClick = { currentScreen = AppScreen.About },
                    onLogout = {
                        scope.launch {
                            userPreferencesRepository.clear()
                            currentScreen = AppScreen.Splash
                        }
                    }
                )
            }
            AppScreen.ConnectWatch -> {
                com.tensormind.feelio.ui.menu.ConnectWatchScreen(
                    userData = userData,
                    firebaseRepository = firebaseRepository,
                    onBack = { currentScreen = AppScreen.Menu }
                )
            }
            AppScreen.Hydration -> {
                HydrationScreen(
                    userData = userData,
                    firebaseRepository = firebaseRepository,
                    onBack = { currentScreen = AppScreen.Home }
                )
            }
            AppScreen.Settings -> {
                SettingsScreen(onBack = { currentScreen = AppScreen.Menu })
            }
            AppScreen.PrivacyPolicy -> {
                PrivacyPolicyScreen(onBack = { currentScreen = AppScreen.Menu })
            }
            AppScreen.About -> {
                AboutScreen(onBack = { currentScreen = AppScreen.Menu })
            }
        }
    }
}
