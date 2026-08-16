# Bask AI — Developer Notes & Project Story

## Inspiration

Mental wellness tools today are often fragmented—users must juggle separate apps for mood tracking, journaling, CBT exercises, meditation soundscapes, smartwatch health monitoring, and AI therapy. 

We created **Bask AI** to solve this friction by building a unified, holistic mental wellness ecosystem. Our goal was to blend native Android UI excellence (Jetpack Compose) with ultra-fast LLM inference (Groq AI `llama-3.3-70b-versatile`), wearable biometrics (Wear OS pairing), open medical APIs (NIH, OpenFDA, ClinicalTrials.gov), and interactive web modules. Bask AI delivers proactive, empathetic mental healthcare right at the user's fingertips—and on their wrist.

---

## What it does

Bask AI is a comprehensive mental health application and Wear OS ecosystem comprising:

1. **Groq AI Companion (`llama-3.3-70b-versatile`)**:
   - Analyzes real-time user thought inputs and health metrics to output instant 2-sentence empathetic reflections.
   - Intelligently routes users to core activities (Meditation, Venting, CBT Diagnostics, Journaling) using dynamic interactive action cards.
2. **Interactive Mood Log & Custom Emoji Calendar**:
   - Fluid spring scaling animations (`Spring.DampingRatioMediumBouncy`) with floating emoji particle pop effects.
   - Replaces standard numeric calendar dates with logged mood emojis (😊, 😞, 😐, 😠, 😴) synced to Firebase Cloud Firestore.
3. **Smartwatch Pairing & Real-Time Biometrics Workspace**:
   - 6-digit pairing protocol (`/pairing_codes/{code}`) linking mobile devices with Wear OS smartwatches (`bask-wearable`).
   - Live Firestore streams tracking heart rate trend lines, SpO2 levels, and restful sleep duration.
4. **Native Hydration Tracker & Reminders**:
   - WorkManager periodic background worker delivering gentle, non-intrusive hydration alerts every 3 hours.
5. **6 Embedded Web App Modules**:
   - 🤖 **AI Therapist (`/ai_therapist`)**: Virtual personas (Dr. Alex, Lexi, Dr. Kai, Nova), ElevenLabs speech synthesis, Web Audio ambient soundscapes (Rain, Ocean, 432Hz binaural tones), and 24/7 emergency lifeline access.
   - ⌚ **Bask Wearable (`/bask-wearable`)**: Pixel Watch API 33 emulator UI with 6-digit handshake and live database sync.
   - 📋 **CBT Assessment (`/cbt_assessment`)**: Validated screeners (GAD-7, PHQ-9, PSS-10), NIH MedlinePlus, ClinicalTrials.gov, OpenFDA APIs, AI thought reframer, and 4-7-8 vagus nerve breathing visualizer.
   - 🧘 **Meditation (`/meditation`)**: Soundscapes & binaural beats library, Lottie vector visualizers, HTML5 Canvas waveform spectrum, and Groq AI Mindful Goal Finder.
   - 🐾 **Virtual Pet Companion (`/virtual_pet`)**: Multi-species companions (Luna, Nova, Mochi, Spark), `CatResponseAlgorithm` behavioral matrix, wardrobe customization, and arcade minigames.
   - 📔 **Wellness Journal (`/wellness_journal`)**: CBT journaling with Groq AI structured JSON evaluation (`llama-3.3-70b`), cognitive distortion reframer, prompt generator, and weekly progress digest.

---

## How we built it

- **Android Companion App**: Engineered using **Kotlin 2.0**, **Jetpack Compose**, Material 3, Jetpack DataStore Preferences, Android WorkManager (`2.9.0`), and Google Identity Credential Manager.
- **Artificial Intelligence**: Powered by **Groq AI Cloud API** (`llama-3.3-70b-versatile` for deep clinical analysis, counseling, and weekly digests; `llama-3.1-8b-instant` for rapid pet dialogue and goal curation).
- **Backend & Real-Time Data Sync**: Built on **Firebase Cloud Firestore** and **Firebase Auth (BOM 33.9.0)** with `onSnapshot()` listeners for real-time bi-directional streaming between watch and phone.
- **Voice, Speech & Audio Synthesis**: **ElevenLabs Text-to-Speech API** for natural therapist voice streaming, browser native **Web Speech API** for assessment readouts, and **HTML5 Web Audio API** for synthesized ambient soundscapes and binaural beats.
- **Open Government Medical APIs**: Directly querying **NIH MedlinePlus API** (health topic summaries), **ClinicalTrials.gov API v2** (active clinical research), and **OpenFDA Drug API** (psychiatric drug labeling).
- **Animations & Visuals**: **Lottie Web SDK** / `@dotlottie/player-component` for mindfulness animations, and **HTML5 Canvas 2D API** for live audio spectrum visualizers and minigames.

---

## Challenges we ran into

1. **Bi-Directional Wearable Data Sync**: Coordinating real-time biometrics and 1-tap smartwatch mood logs over Firestore while maintaining sub-second UI updating on both mobile and watch emulators without race conditions.
2. **Clinical Safety & Rapid AI Responses**: Ensuring Groq AI LLM inference was therapeutic, safe, and fast (<1 second) while strictly adhering to system prompts and structured JSON schemas for CBT journal analysis.
3. **Hybrid Native & Web Performance**: Seamlessly hosting complex HTML5 Canvas spectrum visualizers, Lottie vector animations, and Web Audio synthesizers inside native Android `WebViewScreen` containers without dropping frames.
4. **Neuroscience Behavioral Math**: Designing `CatResponseAlgorithm` for the Virtual Pet Companion to calculate touch velocity, time-of-day phases, and streak penalties into organic pet interactions.

---

## Accomplishments that we're proud of

- **Unified 6-Module Ecosystem**: Successfully connecting native Jetpack Compose with six distinct interactive web modules and smartwatch pairing.
- **Sub-Second Groq AI Reflections**: Delivering real-time empathetic reflections and intelligent feature recommendations based on user thoughts.
- **Verified Clinical API Integrations**: Bridging self-care tools with accredited government research from NIH MedlinePlus, ClinicalTrials.gov, and OpenFDA.
- **Polished UI/UX**: Crafting vibrant glassmorphism visuals, dark modes, spring physics animations, and custom emoji calendar visualizations.

---

## What we learned

- Structuring prompts for Groq LLMs (`llama-3.3-70b-versatile`) to output valid structured JSON under strict psychological frameworks (CBT cognitive distortions).
- Generating low-latency Web Audio API synthesized frequencies (432Hz Solfeggio / binaural tones) directly in browser contexts.
- Optimizing native Android `WebView` lifecycle management and JavaScript interfaces for hybrid web apps.

---

## What's next for Bask Ai

- ⌚ **Native Wear OS App**: Building a dedicated Wear OS standalone app using Compose for Wear OS.
- 🫀 **Biometric Stress Detection**: Utilizing continuous Wear OS HRV (Heart Rate Variability) metrics to trigger proactive Groq AI mindfulness prompts before stress peaks.
- 📱 **On-Device Offline AI**: Integrating on-device local LLM execution via MediaPipe / Gemma for offline therapy and journaling.
- 🌐 **Multilingual Expansion**: Expanding Groq AI counseling personas and ElevenLabs speech synthesis to support Spanish, Hindi, French, and Japanese.
