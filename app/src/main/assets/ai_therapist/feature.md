# AI Therapist — Feature Documentation

The **AI Therapist** application (`/ai_therapist`) is an empathetic AI counseling platform, virtual therapist companion, and mindfulness suite designed to deliver personalized mental wellness support.

---

## 🌟 Key Features

### 🎭 1. Virtual AI Therapist Personalities
Users can select from four distinct AI therapist companions, each with specialized backgrounds, conversational tone, and custom CSS color identities:
- 👨‍⚕️ **Dr. Alex**: Clinical specialist focusing on anxiety, stress management, and grounding.
- 🌸 **Lexi**: Warm, empathetic companion for emotional support, self-love, and mood elevation.
- 🧘‍♂️ **Dr. Kai**: Mindful, structured CBT guide focused on thought clarity and cognitive restructuring.
- 🌙 **Nova**: Soothing night companion for sleep, unwinding, and late-night relaxation.

### 💬 2. Real-Time Conversational AI Engine
- Powered by **Groq AI LLM API** with customized persona prompt injection.
- Dynamic message rendering with Markdown formatting.
- **Safety Timeout Indicator**: Built-in 15-second visual countdown timer and typing dot animation for system safety.
- Quick prompt chips and expandable **Plus (+) Action Drawer** (Deep Breathing, Panic Reset, Affirmations, Process Emotion).

### 🫁 3. Interactive Mindfulness Exercises Carousel
In-chat interactive exercises complete with animated modal guides:
- **Box Breathing (4-4-4-4)**: Visual expanding/contracting breath circle timer.
- **Body Scan Meditation**: Guided focus on physical sensation release.
- **5-4-3-2-1 Sensory Grounding**: Step-by-step panic de-escalation framework.
- **Gratitude Reflections**: Mindful journaling prompts.

### 🎵 4. Ambient Soundscapes Synthesizer
Synthesized ambient audio generator accessible via the navigation drawer:
- 🌧️ Rain Soundscape
- 🌊 Ocean Waves
- 🧘 432Hz Binaural Frequency
- 🌲 Forest Atmosphere

### 🎙️ 5. ElevenLabs Natural Speech Synthesis Integration
- Optional input for ElevenLabs API keys (`sk_...`).
- Streams natural human voice speech for AI therapist responses.

### 📊 6. Groq AI Comprehensive Mood Report
- Analyzes session chat history and mood check-in patterns.
- Generates a clinical-style wellness assessment report outlining emotional themes, coping mechanisms, and recommended action steps.

### 🆘 7. 24/7 Emergency Crisis Lifeline Modal
Provides immediate click-to-call/text access to worldwide crisis hotlines:
- **988 Lifeline**: US & Canada (Call/Text 988)
- **Crisis Text Line**: Text HOME to 741741
- **UK Samaritans & NHS**: Call 116 123
- **India Vandrevala Helpline**: Call +91 9999 666 555
- **Find A Helpline**: Link to 130+ international resources

### 🔥 8. Daily Mood Check-In Widget
- Drawer widget allowing 1-tap logging of daily mood (Peaceful, Anxious, Stressed, Tired, Happy).
- Tracks continuous streak days with visual badge indicators.

---

## 🛠️ File Structure

| File Path | Description |
| :--- | :--- |
| [index.html](file:///c:/Projects/proj_b/ai_therapist/index.html) | Application shell, modal overlays, drawer navigation, and input bar |
| [style.css](file:///c:/Projects/proj_b/ai_therapist/style.css) | Custom styling, glassmorphism overlays, animations, and dark/light themes |
| [script.js](file:///c:/Projects/proj_b/ai_therapist/script.js) | Chat logic, AI API integration, sound synthesizer, and modal controls |
