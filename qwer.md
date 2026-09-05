# Bask App (Feelio) — Complete Project Overview & Feature Documentation

## Inspiration

Modern life moves at an overwhelming speed, constantly demanding output, productivity, and connection. In the middle of this daily hustle, we often carry heavy thoughts in silence—suppressing stress, ignoring physical exhaustion, and pushing through on survival mode. We watch our heart rates spike and sleep quality deteriorate, yet we rarely have a quiet, non-judgmental space to pause and evaluate how we truly feel.

**Bask App was born out of a deeply personal question: *What if technology didn't just measure us, but truly listened to us?***

We set out to create more than just another habit tracker or wellness utility. Bask App is designed as an all-in-one sanctuary in your pocket, combining a native Android companion app with an ecosystem of six specialized web applications:

- **A Gentle Ear When You Need to Vent**: Empathetic AI companions and therapist personas that validate raw emotions without judgment or unsolicited advice.
- **Connecting Mind & Body**: Bridging emotional feelings with physical biometric indicators (heart rate, SpO2, sleep) via real-time smartwatch syncing and Wear OS emulation.
- **Clinical & Mindfulness Toolkits**: Empowering users with CBT cognitive restructuring, 432Hz solfeggio audio meditation, clinical self-assessments, NIH research exploration, and a neuroscience-driven virtual pet companion.
- **Micro-Acts of Self-Care**: Transforming routine daily habits—like drinking water or taking a 3-minute breather—into meaningful acts of mindfulness and self-love.

---

## What it does

Bask App (Feelio) seamlessly bridges daily emotional reflections, real-time smartwatch biometrics, dynamic hydration coaching, evidence-based CBT toolkits, audio meditation, and interactive virtual pet companionship into an intuitive digital experience.

### 📱 1. Android Mobile Companion App (`Feelio`)
- **AI Thought Reflections**: Powered by Groq AI (`llama-3.3-70b-versatile`), Bask processes user thoughts in real time, delivering empathetic reflections and recommending targeted internal features (Meditation, CBT, Venting, or Journaling).
- **Interactive Mood Logging**: Bouncy, spring-animated mood tracking built with Jetpack Compose dynamics (`Spring.DampingRatioMediumBouncy`) backed by Firebase Firestore history.
- **Smart Hydration Coaching**: AI-generated hydration targets based on user survey data, paired with periodic Android `WorkManager` background notifications every 3 hours.
- **Smartwatch Telemetry Handshake**: 6-digit pairing code protocol linking mobile devices with Wear OS smartwatches and web emulators.

---

### 🌐 2. Specialized Web Ecosystem Suite (`proj_b`)

#### 🤖 **AI Therapist & Counseling Companion (`/ai_therapist`)**
- **4 Virtual Personas**: Choose between **Dr. Alex** (Anxiety & Stress), **Lexi** (Emotional Support & Self-Love), **Dr. Kai** (Mindfulness & CBT), and **Nova** (Sleep & Unwinding).
- **ElevenLabs Speech Synthesis**: Real-time natural human voice streaming for therapist responses.
- **Web Audio API Ambient Synthesizer**: Generates Rain, Ocean Waves, 432Hz Binaural Frequency, and Forest atmospheres.
- **Mindfulness Exercises Carousel**: Box Breathing (4-4-4-4 timer), Body Scan guide, 5-4-3-2-1 Sensory Grounding, and Gratitude Reflections.
- **24/7 Crisis Lifeline Modal**: Instant click-to-call/text access to 988 Lifeline, Crisis Text Line, UK Samaritans, Vandrevala Foundation, and 130+ international helplines.

#### ⌚ **Bask Wearable Emulator (`/bask-wearable`)**
- **Wear OS 4.0 / Pixel Watch Emulator UI**: Framed inside a realistic Android Studio emulator window with interactive hardware controls (Crown, Back, Power, Volume).
- **Real-Time Firestore Sync Workspace**: Live snapshot listener (`/paired_devices/sync`) enabling instant bi-directional messaging between watch and companion app.
- **1-Tap OLED Mood Logger**: Quick-log circular mood bar saving directly to cloud Firestore.
- **Progressive Web App (PWA)**: Offline caching via Service Workers (`sw.js`) and manifest setup for desktop/mobile installation.

#### 📋 **CBT Clinical Assessment & Medical Explorer (`/cbt_assessment`)**
- **Validated Clinical Screeners**: GAD-7 (Anxiety), PHQ-9 (Depression), PSS-10 (Stress), CBT Cognitive Distortions Test, and a comprehensive 15-question battery.
- **Groq AI Clinical Breakdown**: Dynamic SVG score ring, severity gauge breakdown, AI psychological report, and Web Speech API audio readout.
- **NIH Open API Medical Explorer**: Real-time integrations with **NIH MedlinePlus API** (clinical topic summaries), **ClinicalTrials.gov API v2** (active clinical research trials), and **OpenFDA Drug API** (pharmaceutical guidance).
- **Symptom Reduction Toolkit**: AI Thought Reframer, 4-7-8 Vagus Nerve breathing visualizer, Somatic PMR muscle scan, 5-4-3-2-1 panic grounding, and 7-Day Action Plan generator.

#### 🧘 **Mindfulness & Audio Meditation (`/meditation`)**
- **Soundscapes & Binaural Beats**: Curated tracks spanning Binaural Frequencies, Focus, Relax, Sleep, Singing Bowls, Chakra Healing, and 432Hz Solfeggio tones.
- **Groq AI Mindful Goal Finder Wizard**: 3-step setup modal analyzing user goals and mood to generate personalized soundwave recommendations and practice guides.
- **Interactive Lottie Vector Animations**: Rendered using `@dotlottie/player-component` (Meditating Monk, Breathing Circle, Candle Flame, Wind Chimes).
- **HTML5 Canvas Waveform Spectrum**: Dynamic real-time audio spectrum visualizer reacting live during sound playback.

#### 🐾 **Kawaii Virtual Pet Companion (`/virtual_pet`)**
- **4 Pet Personas**: Choose from **Luna** (Kawaii Cat), **Nova** (Shiba Inu), **Mochi** (Fluffy Bunny), or **Spark** (Cosmic Baby Dragon).
- **Groq AI Pet Dialogue Engine**: Real-time conversational AI (`llama-3.1-8b-instant`) tailored to pet stats (hunger, joy, energy, bonding level).
- **Neuroscience Behavioral Matrix (`CatResponseAlgorithm`)**: Rule-based algorithm calculating touch velocity, interaction streak penalties, time of day phases, and emotional bonding titles.
- **Arcade Minigame & Customization**: "Catch the Star" canvas minigame for earning coins, plus food feeding and wardrobe accessory customization.

#### 📔 **CBT Wellness Journal (`/wellness_journal`)**
- **Rich CBT Journaling**: Mood intensity sliders (1-10), emotion tags, searchable history, and structured entry logging.
- **Groq AI Journal Analysis**: Evaluates entries using `llama-3.3-70b-versatile` to produce structured JSON containing empathetic summaries, cognitive distortion identification, reframes, and actionable advice.
- **Weekly Wellness Digest**: AI-synthesized weekly progress reports highlighting mood trends, personal strengths, and mindfulness tips.

---

## How we built it

Bask App was built with a unified technology stack spanning mobile Android engineering, web applications, Generative AI, cloud sync, and medical research APIs:

| Layer / Subsystem | Technologies & Frameworks |
| :--- | :--- |
| **Native Android App** | Kotlin 2.0, Jetpack Compose, Material 3, Lottie Compose, Jetpack DataStore Preferences, WorkManager 2.9.0 |
| **Web Ecosystem** | HTML5 Semantic Markup, CSS3 Glassmorphism & HSL Design Tokens, Vanilla JavaScript (ES6+ Modules), Service Workers, PWA Manifest |
| **Artificial Intelligence** | Groq Cloud API (`llama-3.3-70b-versatile` for deep clinical analysis; `llama-3.1-8b-instant` for fast pet dialogue and wizards) |
| **Voice & Speech Engine** | ElevenLabs Text-to-Speech API, Web Speech API (`SpeechSynthesis`), HTML5 Web Audio API Synthesizer |
| **Cloud & Real-time Sync** | Firebase Firestore (Real-time snapshot streams), Firebase Authentication (Google OAuth 2.0 & Anonymous Guest Auth) |
| **Medical Open APIs** | US National Library of Medicine (MedlinePlus API), ClinicalTrials.gov API v2, US FDA OpenFDA Drug API |
| **Graphics & Audio** | Lottie Web SDK, `@dotlottie/player-component`, HTML5 2D Canvas API (Audio Waveform Spectrum, Arcade Minigames, Symptom Charts) |

---

## Challenges we ran into

1. **Multi-Platform Real-time Cloud Syncing**: Synchronizing smartwatch Wear OS emulator state, native mobile UI, and Firestore database listeners (`/paired_devices/sync`) in real time with minimal latency and zero memory leaks.
2. **Multi-Model AI Schema Engineering**: Managing multiple Groq LLM pipelines—ranging from deep clinical CBT evaluations (`llama-3.3-70b-versatile`) to real-time stat-dependent pet dialogues (`llama-3.1-8b-instant`)—ensuring strict JSON output parsing and instant local fallback responses during API failures.
3. **Web Audio Synthesizer & Natural Speech**: Building low-latency audio wave generators for 432Hz binaural tones while simultaneously streaming ElevenLabs natural speech without triggering browser autoplay restrictions.
4. **Parsing Open Medical Data**: Mapping diverse XML and JSON responses from NIH MedlinePlus, ClinicalTrials.gov, and OpenFDA into clean, user-friendly card components.
5. **Immersive WebViews in Jetpack Compose**: Handling full-screen `WebView` transitions inside Jetpack Compose while dynamically hiding and restoring system status/navigation bars (`WindowInsetsControllerCompat`).

---

## Accomplishments that we're proud of

- **Seamless Ecosystem Integration**: Combining a native Android app with 6 web modules into a unified mental wellness ecosystem.
- **Bi-Directional Smartwatch Telemetry**: Real-time code-pairing handshake between mobile phones and smartwatch Wear OS emulators.
- **Zero-Downtime Resilience**: Designing offline fallback capabilities for both Android (DataStore/Local Fallbacks) and Web PWAs (Service Worker Caching & `localStorage`).
- **AI Clinical & Behavioral Engines**: Developing specialized AI tools including CBT Thought Reframing, Clinical Score Analysis, Weekly Wellness Digests, and the `CatResponseAlgorithm` neuroscience matrix.
- **Silky Smooth Aesthetics**: Rich glassmorphism UI, spring damping animations, live Canvas audio spectrum visualizers, and Lottie vector animations.

---

## What we learned

- **Multi-Model AI Prompt Engineering**: How to choose the right LLM tier (70B vs 8B) for specific user interactions based on required depth versus latency constraints.
- **Connecting Biometrics with AI**: Integrating physical metrics (heart rate, SpO2, sleep) into qualitative AI prompt context to yield actionable health recommendations.
- **Medical API Standards**: Navigating open healthcare APIs (NIH, FDA, ClinicalTrials) and presenting clinical data accessibly to everyday users.
- **Modern Full-Stack Architecture**: Deepening our knowledge of Jetpack Compose state flows, Firebase real-time listeners, PWA service workers, and Web Audio API synthesis.

---

## What's next for Bask App

- **On-Device Local AI Inference**: Integrating MediaPipe LLM Inference / Gemma 2B to run empathetic AI reflections and journal analysis 100% on-device for total privacy.
- **Native Wear OS Companion App**: Replacing the web emulator with a dedicated Wear OS standalone app featuring passive HRV (Heart Rate Variability) stress tracking.
- **Predictive Burnout & Stress Analytics**: Utilizing machine learning to detect rising anxiety and stress trends before burnout occurs by correlating biometric telemetry with logged moods over time.
- **Multi-User Peer Mindfulness Rooms**: Adding real-time synchronized group meditation sessions and anonymous peer support spaces.
