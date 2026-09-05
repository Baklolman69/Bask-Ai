# Feelio — Developer & Architecture Notes

This document provides internal developer reference notes for Feelio (Bask AI), covering screen routing, state management, API integration specs, database schema, background worker tasks, and local debugging tips.

---

## Architecture & Navigation Flow

Feelio uses a single-activity layout (`MainActivity.kt`) built entirely with Jetpack Compose. Navigation is managed through an `AppScreen` enum state inside `FeelioApp()`, using Compose `Crossfade` with a 400ms tween animation between screens.

```
Splash -> Onboarding -> NameEntry -> Challenges -> Home -> [Feature Screens / WebView]
                                                     ├── Profile
                                                     ├── Menu -> ConnectWatch / Settings / About
                                                     ├── Hydration
                                                     └── WebView (Meditation, CBT, Journal, etc.)
```

### Data Layer Overview

The app delegates data responsibility across three primary repository classes:

1. **`UserPreferencesRepository.kt`**:
   - Built on Jetpack DataStore Preferences (`user_preferences.preferencespb`).
   - Persists session state: `user_id`, `user_name`, `is_logged_in`, `is_guest`.
   - Exposes a reactive `userData: Flow<UserData>` stream to drive root navigation decision making.

2. **`FirebaseRepository.kt`**:
   - Manages Cloud Firestore queries and Firebase Authentication (Google ID and anonymous sign-in fallback).
   - Handles data operations for mood entries, thought logs, water intake, user onboarding challenge selections, and 6-digit smartwatch pairing handshakes.

3. **`GroqRepository.kt`**:
   - Direct HTTP client (`HttpURLConnection`) interacting with the Groq AI API (`llama-3.3-70b-versatile`).
   - Handles prompt composition, response parsing, and offline fallback matching when network connectivity drops or API keys are unconfigured.

---

## Groq AI Integration Specifications

- **API Endpoint**: `https://api.groq.com/openai/v1/chat/completions`
- **Default Model**: `llama-3.3-70b-versatile`
- **Key Injection**: Read from `BuildConfig.GROQ_API_KEY` defined at compile time from `local.properties` or environment variables.

### Core Methods

- `getReflectionAndRecommendation(userThought, currentMoodEmoji, userName)`:
  Constructs a system prompt asking Groq to generate a 2-sentence empathetic response and recommend one of 4 internal feature routes (`"Meditation for focus"`, `"Just need to talk"`, `"CBT test"`, `"Journal your day"`). Parses the choice from the model output.

- `getBiometricAnalysis(bpm, spO2, sleepHours, moodEmoji, userName)`:
  Summarizes smartwatch health metrics into a concise 3-sentence analysis correlating rest and heart rate with daily mood.

- `generateHydrationGoal(surveyData, userName)`:
  Processes user survey inputs (motivation, temperature, activity level) and returns a targeted daily water intake goal (in 250ml glasses) alongside an explanation.

- `getHydrationReminder(userName)` / `getHydrationCompliment(glasses, userName)`:
  Generates short, non-repetitive micro-prompts for background notification popups.

*Fallback Behavior*: If `GROQ_API_KEY` is missing or HTTP request fails (non-200 response), `GroqRepository` logs the exception and returns deterministic local fallback content without throwing runtime exceptions.

---

## Firestore Database Schema

```
users/
└── {userId}/
    ├── name: string
    ├── isGuest: boolean
    ├── authProvider: "guest" | "google"
    ├── createdAt: timestamp
    ├── lastLogin: timestamp
    ├── usageStats: map<string, number>
    │
    ├── mood_logs/
    │   └── {yyyy-MM-dd}
    │       ├── moodIndex: int (0-4)
    │       ├── emoji: string
    │       ├── label: string
    │       ├── dateStr: string
    │       └── timestamp: timestamp
    │
    ├── thoughts/
    │   └── {thoughtId}
    │       ├── text: string
    │       ├── aiResponse: string
    │       ├── recommendedFeature: string
    │       └── timestamp: timestamp
    │
    ├── hydration_logs/
    │   └── {yyyy-MM-dd}
    │       ├── glasses: int
    │       └── timestamp: timestamp
    │
    └── watch_pairing/
        └── status
            ├── pairingCode: string
            ├── isPaired: boolean
            └── pairedAt: timestamp

pairing_codes/
└── {code}/                       # 6-digit pairing handshake code
    ├── status: "pending" | "paired"
    ├── code: string
    └── pairedAt: timestamp

paired_devices/
└── {code}/
    └── sync_data/               # Real-time bi-directional messaging stream
        └── {messageId}
            ├── text: string
            ├── sender: string
            └── timestamp: timestamp
```

### Wearable Handshake Protocol

1. Smartwatch app or web emulator generates a 6-digit code registered in `pairing_codes/{code}` with `status = "pending"`.
2. Mobile app submits code via `FirebaseRepository.pairWithWatch(code)`.
3. If valid, code status updates to `"paired"`.
4. Both devices register Firestore listeners via `addSnapshotListener()` on `paired_devices/{code}/sync_data` to exchange real-time telemetry updates.

---

## Background Worker Execution

Hydration reminders are driven by Android `WorkManager` (`HydrationWorker.kt`):

- **Schedule**: Enqueued in `MainActivity.setupHydrationReminders()` as a periodic request every 3 hours with `ExistingPeriodicWorkPolicy.KEEP`.
- **Constraint**: `NetworkType.CONNECTED` constraint to enable calling `GroqRepository.getHydrationReminder()`.
- **Notification**: Triggers `NotificationHelper.showNotification()` with channel ID `hydration_reminders`.

---

## WebView & Fullscreen Insets

Embedded web tools (Meditation, CBT assessment, Journal, Virtual Pet) load remote/web URLs via `WebViewScreen.kt`.

To achieve immersive full-screen display without system distraction:
- System bars and status bars are hidden in `MainActivity.onCreate()` using `WindowInsetsControllerCompat`.
- `onWindowFocusChanged()` re-enforces `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE` when returning to the application window.

---

## Local Testing & Tips

### Simulating Groq Fallbacks
To test offline resilience, remove or set `GROQ_API_KEY=` in `local.properties`. Rebuild the project. All AI prompts will fall back to local responses instantly without waiting for connection timeouts.

### Manual WorkManager Trigger
To run `HydrationWorker` immediately without waiting 3 hours:
```bash
adb shell cmd jobscheduler run -f com.tensormind.feelio <JOB_ID>
```
Or check active WorkManager jobs in logcat with tag `HydrationWorker`.

### Authentication Testing
If Google Sign-In is not configured with SHA-1 credentials in your Firebase Console project, use the Guest login option on the `NameEntry` screen. Guest users complete anonymous auth (`auth.signInAnonymously()`) and have full access to Firestore operations.
