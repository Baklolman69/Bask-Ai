# Bask AI — Mental Wellness & Wearable Ecosystem

![Android](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?style=for-the-badge&logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=for-the-badge&logo=jetpackcompose)
![Groq AI](https://img.shields.io/badge/AI-Groq%20llama--3.3--70b-orange?style=for-the-badge)
![Firebase](https://img.shields.io/badge/Backend-Firebase%20Firestore-yellow?style=for-the-badge&logo=firebase)

# Bask AI — Mental Wellness & Wearable Ecosystem

![Android](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?style=for-the-badge&logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=for-the-badge&logo=jetpackcompose)
![Groq AI](https://img.shields.io/badge/AI-Groq%20llama--3.3--70b-orange?style=for-the-badge)
![Firebase](https://img.shields.io/badge/Backend-Firebase%20Firestore-yellow?style=for-the-badge&logo=firebase)

> **Bask AI** is a mental wellness Android & Wear OS application built with **Jetpack Compose**, powered by **Groq AI** (`llama-3.3-70b-versatile`) and **Firebase Cloud Firestore**. It combines proactive AI reflections, mood tracking, smartwatch biometric sync, hydration reminders, and interactive wellness tools into a unified ecosystem.

---

## 🌟 Key Features

- 🤖 **Groq AI Companion**: Real-time empathetic reflections on user thoughts and intelligent activity recommendations.
- 😊 **Interactive Mood Log & Custom Calendar**: Dynamic bouncy emoji mood logging synced with Cloud Firestore.
- ⌚ **Smartwatch Pairing**: 6-digit handshake protocol syncing heart rate, SpO2, and sleep data in real time.
- 💧 **Hydration Tracker**: WorkManager periodic background worker delivering custom hydration reminders.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **UI & Framework** | Kotlin 2.0, Jetpack Compose, Material 3, Lottie Animations |
| **Artificial Intelligence** | Groq AI Cloud API (`llama-3.3-70b-versatile`) |
| **Backend & Sync** | Firebase Cloud Firestore, Firebase Auth, Google Identity |
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
