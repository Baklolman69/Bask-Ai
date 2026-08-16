# Bask AI — Mental Wellness & Wearable Ecosystem

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

📁 Project Structure

The Bask AI project is organized like this:

BaskAI/

├── app/

│   ├──

│   ├── build.gradle.kts

│   └── src/main/

│       ├── AndroidManifest.xml

│       └── java/com/tensormind/feelio/

│           ├── MainActivity.kt

│           ├── data/

│           ├── ui/

│           ├── util/

│           └── worker/

├── local.properties.example

└── build.gradle.kts

🚀 Getting Started

To start using Bask AI you need to have:

* Android Studio Ladybug or newer

* JDK 17

* An Android device or emulator with Android 7.0 or newer

To set up Bask AI:

* Clone the repository, from github

* Copy the local.properties.example file to local.properties. Add your sdk.dir and GROQ_API_KEY

* Copy the file to google-services.json and add your Firebase credentials

* Build the Debug APK using the gradlew assembleDebug command

📄 License

Bask AI is developed for the Bask AI Health Ecosystem. All rights are reserved by Bask AI.
---

## 📄 License
Developed for Bask AI Health Ecosystem. All rights reserved.
