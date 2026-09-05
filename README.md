# Bask AI (Feelio)

> *"In a noisy world that constantly demands our output, Bask AI exists to gently remind us to pause, listen to our body, and feel."*

Bask AI is a holistic wellness application built for Android and Wear OS using Jetpack Compose, Kotlin, Firebase, and Groq AI (`llama-3.3-70b-versatile`). It seamlessly bridges daily emotional reflections, real-time smartwatch biometric telemetry, dynamic hydration coaching, and evidence-based mental wellness exercises into a quiet, compassionate digital sanctuary.

---

## 🌟 Key Features

- **🤖 AI Companion & Reflections**: Uses Groq AI to process user thoughts in real time, offering empathetic 2-sentence responses and recommending targeted tools (Meditation, CBT, Venting, or Journaling).
- **😊 Interactive Mood Logging**: Custom mood tracking driven by Jetpack Compose spring animations (`Spring.DampingRatioMediumBouncy`) and synced with Cloud Firestore.
- **⌚ Smartwatch Biometric Telemetry**: 6-digit handshake protocol pairing the app with Wear OS devices or web emulators to stream real-time heart rate, SpO2, and sleep statistics.
- **💧 Smart Hydration Coaching & Alerts**: Periodic Android `WorkManager` background worker delivering customized hydration prompts every 3 hours alongside interactive glass volume tracking.
- **🧘 Embedded Web Companion Suite**: Native `WebView` integration hosting web companion tools, including CBT assessment screeners, guided audio meditation, AI therapy personas, and virtual pet interactions.

---

## 🛠️ Technology Stack

- **UI & Core Architecture**: Kotlin 2.0, Jetpack Compose, Material 3, Lottie Compose, Jetpack DataStore Preferences
- **AI Integration**: Groq API Cloud (`llama-3.3-70b-versatile`) via custom REST client
- **Backend & Authentication**: Firebase Firestore, Firebase Auth (Google Sign-In + Anonymous fallback)
- **Background Jobs**: Android WorkManager (`2.9.0`)
- **SDK Targets**: Compile SDK 36, Target SDK 36, Min SDK 24 (Android 7.0+)

---

## 🚀 Setup & Local Development

### Prerequisites

- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 configured in Android Studio
- Android device or emulator running API 24 or higher

### Environment Configuration

1. **Clone repository**:
   ```bash
   git clone https://github.com/syntax-savage/Bask-Ai.git
   cd Bask-Ai
   ```

2. **Setup local properties**:
   Copy `local.properties.example` to `local.properties` in the project root:
   ```properties
   sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
   GROQ_API_KEY=gsk_your_actual_groq_api_key_here
   ```

3. **Firebase Configuration**:
   Place your generated `google-services.json` inside the `app/` directory.

### Building & Running

Build the debug APK using Gradle:

```bash
./gradlew assembleDebug
```

To install directly onto a connected USB device or emulator:

```bash
./gradlew installDebug
```

---

## 📁 Project Layout

```
Bask-Ai/
├── app/
│   ├── build.gradle.kts               # App dependencies & GROQ_API_KEY injection
│   ├── google-services.json.template  # Firebase template
│   └── src/main/
│       ├── AndroidManifest.xml        # Manifest & permissions
│       └── java/com/tensormind/feelio/
│           ├── MainActivity.kt        # Entry point & screen router
│           ├── data/                  # Data layer (Groq, Firebase, Preferences)
│           ├── ui/                    # Compose views (Home, Onboarding, Hydration, etc.)
│           ├── util/                  # Notification utilities
│           └── worker/                # WorkManager periodic hydration worker
├── build.gradle.kts                   # Top-level Gradle configuration
├── local.properties.example           # Example local properties
└── settings.gradle.kts                # Plugin repositories & project name
```

---

## 📄 License

Developed for Bask AI Health Ecosystem. All rights reserved.
