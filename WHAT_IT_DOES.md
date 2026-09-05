# Feelio (Bask AI) — What It Does

> *"In a noisy world that constantly demands our output, Feelio exists to gently remind us to pause, listen to our body, and feel."*

**Feelio** is an intelligent, compassionate Android mental wellness companion designed to harmonize emotional self-reflection, real-time biometric health telemetry, personalized habit micro-nudges, and evidence-based therapeutic tools into a seamless, sanctuary-like digital experience.

---

## 🌟 Executive Summary

Feelio bridges the gap between **emotional awareness** and **physical telemetry**. Rather than acting as a static mood tracker or a simple health monitor, Feelio actively correlates how you feel mentally with what your body is experiencing physically (heart rate, blood oxygen levels, sleep quality). Powered by **Groq AI (`llama-3.3-70b-versatile`)**, **Firebase**, and **Jetpack Compose**, Feelio offers real-time empathetic listening, automated therapeutic routing, smartwatch synchronization, and adaptive background hydration coaching.

---

## 🚀 Core Features & Capabilities

### 1. 🤖 AI Thought Reflection & Smart Feature Routing
- **Empathetic Listening**: Users type or dictate their raw, unfiltered daily thoughts. Groq AI evaluates the emotional tone and returns a warm, non-judgmental 2-sentence validation.
- **Contextual Tool Recommendation**: AI automatically analyzes the user's state of mind and directs them to the most effective therapeutic exercise:
  - 🧘 **Meditation for Focus**: Guided breathing and calming audio routines.
  - 💬 **Just Need to Talk**: Dedicated non-judgmental venting space.
  - 🧠 **CBT Assessment**: Structured cognitive reframing & distortion identifier.
  - 📖 **Daily Journaling**: Guided prompt journaling for quiet reflection.

### 2. 😊 Interactive Mood Logging
- **Dynamic Micro-Interactions**: Built using Jetpack Compose spring physics (`Spring.DampingRatioMediumBouncy`) to provide tactile, responsive emoji feedback.
- **Firestore Synchronization**: Tracks daily emotional fluctuations over time with timestamped mood entries stored in Cloud Firestore.

### 3. ⌚ Smartwatch Biometric Telemetry & 6-Digit Handshake
- **Real-Time Telemetry Stream**: Connects with Wear OS devices or web emulators to stream vital health stats:
  - ❤️ **Heart Rate (BPM)**
  - 🩸 **Blood Oxygen Saturation (SpO2 %)**
  - 😴 **Sleep Duration & Quality (Hours)**
- **6-Digit Secure Pairing Protocol**: Establishes a temporary 6-digit handshake code in `pairing_codes` collection, upgrading to a real-time Firestore listener stream (`paired_devices/{code}/sync_data`) upon code verification.
- **AI Health Intelligence**: Correlates biometric anomalies (e.g., elevated heart rate paired with low sleep) with user-reported moods to offer gentle health insights and recovery suggestions.

### 4. 💧 Dynamic Hydration Coaching & Micro-Nudges
- **AI-Customized Hydration Goals**: Survey algorithm (evaluating user activity, climate, and wellness goals) calculates tailored daily water intake targets (in 250ml glasses).
- **Background Worker Reminders**: Uses Android `WorkManager` (`HydrationWorker`) to deliver contextual notifications every 3 hours without draining system battery.
- **Hydration Tracker Interface**: Visual glass logging, progress percentages, and AI compliments upon reaching daily hydration targets.

### 5. 🌐 Native Embedded Web Companion Modules
- **WebView Integration**: Seamlessly hosts web companion applications and interactive tools (`WebViewScreen.kt`) with full-screen edge-to-edge layout, hiding system bars for immersive focus.
- **Included Tools**:
  - CBT Screener & Cognitive Restructuring Tool (`/cbt_assessment`)
  - Audio Meditation & Solfeggio Soundscape Player (`/meditation`)
  - AI Therapist & Specialized Counseling Companion (`/ai_therapist`)
  - Kawaii Virtual Pet Companion & Behavioral Engine (`/virtual_pet`)
  - CBT Mental Wellness Journal (`/wellness_journal`)

### 6. 👤 Personalized Onboarding & Progress Tracking
- **Multi-Stage Onboarding**: Engaging visual pagers detailing the application's philosophy.
- **Challenge Customization**: Users select personal wellness goals (e.g., managing anxiety, improving sleep, boosting daily focus, building stress resilience).
- **Flexible Authentication**: Supports seamless Google Sign-In as well as anonymous Guest authentication with zero barrier to entry.
- **Firestore Usage Analytics**: Tracks user activity counts, logging frequency, and goal progress.

---

## 🛠️ System Architecture & Data Flow

```
                                  +-----------------------+
                                  |   User Input / View   |
                                  |  (Jetpack Compose UI) |
                                  +-----------+-----------+
                                              |
                     +------------------------+------------------------+
                     |                                                 |
         +-----------v-----------+                         +-----------v-----------+
         | UserPreferencesRepo  |                         |  Groq AI Repository   |
         | (Jetpack DataStore)   |                         | (Llama-3.3-70b-Cloud) |
         +-----------+-----------+                         +-----------+-----------+
                     |                                                 |
                     | Local Auth / Preferences                        | Empathetic Validation
                     v                                                 v & Feature Routing
         +-----------+-----------+                         +-----------+-----------+
         |  FirebaseRepository   |                         |  Embedded Web Tools   |
         | (Auth & Cloud Store)  |<----------------------->|    (WebView Screen)   |
         +-----------+-----------+                         +-----------------------+
                     ^
                     | Real-Time 6-Digit Telemetry Sync
         +-----------+-----------+
         | Wear OS / Smartwatch  |
         | Telemetry Engine      |
         +-----------------------+
```

---

## 📱 Navigation & Screen Matrix

| Screen | Description | Primary Capabilities |
| :--- | :--- | :--- |
| **Splash Screen** | Launch experience | Determines auth state & routes user |
| **Onboarding Pager** | Welcome presentation | Introduces features & mind-body philosophy |
| **Name Entry** | Identity selection | Guest login / Google Sign-In setup |
| **Challenges Screen** | Goal selection | Customizes app focus (anxiety, sleep, focus) |
| **Home Screen** | Main Dashboard | Mood logging, AI thought reflection, tool shortcuts |
| **Hydration Screen** | Water tracking | Log glasses, set survey goals, progress tracking |
| **Connect Watch Screen**| Telemetry sync | 6-digit code generator & live biometric display |
| **Profile Screen** | User hub | View progress, usage stats, and security settings |
| **Menu / Settings** | Utility drawer | App settings, privacy policy, about page, logout |
| **WebView Screen** | Therapeutic tools | Native container for Meditation, CBT, Venting, Pet |

---

## 🔒 Security, Privacy & Offline Resilience

- **Privacy-First Mindset**: Guest mode enables full functionality without forcing users to disclose personal identity or email credentials.
- **Deterministic Offline Fallbacks**: If network connectivity drops or the Groq API key is unconfigured, `GroqRepository` automatically switches to local reflection algorithms to ensure continuous, uninterrupted access to mental health tools.
- **Immersive Fullscreen Mode**: Automatically suppresses Android system navigation and status bars (`BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`) to reduce digital distractions.

---

## 📄 License & Attribution

Developed for **Feelio / Bask AI**. Designed to provide gentle, tech-enabled digital sanctuary for emotional well-being.
