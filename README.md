

![Android](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?style=for-the-badge&logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=for-the-badge&logo=jetpackcompose)
![Groq AI](https://img.shields.io/badge/AI-Groq%20llama--3.3--70b-orange?style=for-the-badge)
![Firebase](https://img.shields.io/badge/Backend-Firebase%20Firestore-yellow?style=for-the-badge&logo=firebase)

Bask AI is a wellness application for Android and Wear OS. It is built with Jetpack Compose. Uses Groq AI and Firebase Cloud Firestore.

Bask AI has a lot of features that help with wellness. It has AI reflections, mood tracking, smartwatch biometric sync, hydration reminders and interactive wellness tools. All of these features work together to create an ecosystem for mental wellness.

🌟 Key Features

🤖 The Groq AI Companion gives you real-time reflections on your thoughts and suggests activities for you to do.

😊 You can log your mood with emojis and it will sync with Cloud Firestore. You can also make a custom calendar.

⌚ Bask AI can connect to your smartwatch using a 6-digit protocol. This lets it get your heart rate, SpO2 and sleep data in time.

💧 The Hydration Tracker sends you reminders to drink water at the times.

🧘 Bask AI has tools to help you relax like meditation CBT exercises, journaling and AI therapy.

🛠️ Technology Stack

The technology used to build Bask AI includes:

* Kotlin 2.0 Jetpack Compose, Material 3 and Lottie Animations for the user interface

* Groq AI Cloud API for intelligence

* Firebase Cloud Firestore, Firebase Auth and Google Identity for the backend and syncing data

* Android WorkManager and Jetpack DataStore Preferences for background tasks and data storage

---

## 📁 Project Structure

```
BaskAI/
├── app/
│   ├── google-services.json.template # Firebase configuration template
│   ├── build.gradle.kts               # App level dependencies & build script
│   └── src/main/
│       ├── AndroidManifest.xml        # Manifest & permissions
│       └── java/com/tensormind/feelio/
│           ├── MainActivity.kt        # Entry point & navigation
│           ├── data/                  # Repositories (Groq, Firebase, Preferences)
│           ├── ui/                    # Compose screens (Home, Onboarding, Profile, Hydration)
│           ├── util/                  # Notification helpers
│           └── worker/                # WorkManager hydration worker
├── local.properties.example           # Environment properties template
└── build.gradle.kts                   # Top-level build file
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17
- Android Device or Emulator running Android 7.0+ (API 24+)

### Setup & Build
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/bask-ai.git
   cd bask-ai
   ```
2. **Configure properties**:
   - Copy `local.properties.example` to `local.properties` and set your `sdk.dir` and optional `GROQ_API_KEY`.
   - Copy `app/google-services.json.template` to `app/google-services.json` and insert your Firebase credentials.
3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📄 License
Developed for Bask AI Health Ecosystem. All rights reserved.

| **Background & Data** | Android WorkManager, Jetpack DataStore Preferences |

---

## 📁 Project Structure

```
BaskAI/
├── app/
│   ├── google-services.json.template # Firebase configuration template
│   ├── build.gradle.kts               # App level dependencies & build script
│   └── src/main/
│       ├── AndroidManifest.xml        # Manifest & permissions
│       └── java/com/tensormind/feelio/
│           ├── MainActivity.kt        # Entry point & navigation
│           ├── data/                  # Repositories (Groq, Firebase, Preferences)
│           ├── ui/                    # Compose screens (Home, Onboarding, Profile, Hydration)
│           ├── util/                  # Notification helpers
│           └── worker/                # WorkManager hydration worker
├── local.properties.example           # Environment properties template
└── build.gradle.kts                   # Top-level build file
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17
- Android Device or Emulator running Android 7.0+ (API 24+)

### Setup & Build
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/bask-ai.git
   cd bask-ai
   ```
2. **Configure properties**:
   - Copy `local.properties.example` to `local.properties` and set your `sdk.dir` and optional `GROQ_API_KEY`.
   - Copy `app/google-services.json.template` to `app/google-services.json` and insert your Firebase credentials.
3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📄 License
Developed for Bask AI Health Ecosystem. All rights reserved.
