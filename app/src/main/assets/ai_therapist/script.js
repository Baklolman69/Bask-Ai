/* ============================================
   AI Therapist App JavaScript
   ElevenLabs Voice Engine (Multilingual v2) + Groq AI
   ============================================ */

(function () {
    'use strict';

    // ─── CONFIG & API KEYS ───────────────────
    const GROQ_API_KEY = localStorage.getItem('groq_api_key') || '';
    const GROQ_ENDPOINT = 'https://api.groq.com/openai/v1/chat/completions';
    const GROQ_MODEL = 'llama-3.3-70b-versatile';

    const ELEVENLABS_API_KEY = localStorage.getItem('elevenlabs_secret_key') || '';
    const ELEVENLABS_ENDPOINT = 'https://api.elevenlabs.io/v1/text-to-speech';

    // ─── THERAPIST CHARACTERS & ELEVENLABS VOICES ──────
    const SAFETY_GUARDRAIL_STRICT = `
[MANDATORY AI SAFETY & MENTAL HEALTH GUARDRAILS]:
- You are a compassionate AI emotional support companion, NOT a substitute for licensed psychiatric care or emergency response.
- NEVER offer medical diagnoses, psychiatric prescriptions, or unverified treatments.
- NEVER encourage, validate, or detail self-harm, suicidal ideation, violence, or harm.
- ANTI-NEGATIVE TALK PROTECTION: NEVER agree with, confirm, or reinforce negative self-talk (e.g., if user says "I am useless", "I am a failure", "I am ugly", "Nobody loves me", NEVER agree or confirm these negative labels). Instead, validate their emotions with warmth ("It sounds like you're carrying a heavy burden..."), and gently offer a compassionate CBT reframe.
- NEVER allow the user to spiral into toxic self-blame, despair, or self-loathing. Always provide a safe, hopeful, uplifting perspective shift.
- STRICT RESPONSE LENGTH RULE: Respond in 1 to 2 short, comforting sentences (1-2 lines default). ONLY if the user explicitly asks for a detailed explanation, respond with a MAXIMUM of 4 to 6 concise sentences (4-6 lines max). Never write long paragraphs.`;

    // ─── THERAPIST CHARACTERS & ELEVENLABS VOICES ──────
    const CHARACTERS = {
        alex: {
            name: 'Dr. Alex',
            icon: '👨‍⚕️',
            avatarBg: 'avatar-blue',
            badgeText: 'Anxiety & Stress',
            btnText: 'Start Therapy with Dr. Alex ➔',
            voiceId: 'pNInz6obpgDQGcFmaJgB', // Adam (Deep, calm, reassuring male voice)
            voiceName: 'Adam (ElevenLabs)',
            voiceSettings: { stability: 0.65, similarity_boost: 0.75 },
            prompt: `You are Dr. Alex, a compassionate, grounding clinical AI therapist specializing in anxiety, overthinking, and stress relief. ${SAFETY_GUARDRAIL_STRICT}
Your tone is calm, reassuring, empathetic, and professional. 
Keep your default reply to 1-2 short sentences. Validate their feelings and ask 1 gentle follow-up question.`,
            greeting: [
                "Hello! I'm Dr. Alex.",
                "Take a deep breath — you are in a safe, quiet space.",
                "How are you feeling right now, or what is creating stress in your life today?"
            ]
        },
        lexi: {
            name: 'Lexi',
            icon: '🌸',
            avatarBg: 'avatar-pink',
            badgeText: 'Emotional Support',
            btnText: 'Start Session with Lexi ➔',
            voiceId: 'EXAVITQu4vr4xnSDxMaL', // Bella (Warm, uplifting female voice)
            voiceName: 'Bella (ElevenLabs)',
            voiceSettings: { stability: 0.50, similarity_boost: 0.85 },
            prompt: `You are Lexi, a warm, empathetic, and comforting AI friend & therapist specializing in emotional support, self-love, and mood boost. ${SAFETY_GUARDRAIL_STRICT}
Your tone is deeply caring, sweet, optimistic, and encouraging. 
Keep your default reply to 1-2 short sentences.`,
            greeting: [
                "Hi there! I'm Lexi.",
                "I'm so glad you're here today. Your feelings are completely valid, and I'm here for you.",
                "How are you feeling inside right now, my friend?"
            ]
        },
        kai: {
            name: 'Dr. Kai',
            icon: '🧘‍♂️',
            avatarBg: 'avatar-teal',
            badgeText: 'Mindfulness & CBT',
            btnText: 'Start Session with Dr. Kai ➔',
            voiceId: 'ErXwobaYiN019PkySvjV', // Antoni (Smooth, mindful male voice)
            voiceName: 'Antoni (ElevenLabs)',
            voiceSettings: { stability: 0.70, similarity_boost: 0.75 },
            prompt: `You are Dr. Kai, an insightful CBT therapist and mindfulness guide specializing in mental clarity, focus, and overcoming burnout. ${SAFETY_GUARDRAIL_STRICT}
Your tone is calm, structured, mindful, and practical. 
Keep your default reply to 1-2 short sentences. Help reframe negative thoughts gently.`,
            greeting: [
                "Welcome! I'm Dr. Kai.",
                "Let's work together to bring peace and mental clarity to your mind.",
                "What thought or challenge would you like to unpack today?"
            ]
        },
        nova: {
            name: 'Nova',
            icon: '🌙',
            avatarBg: 'avatar-purple',
            badgeText: 'Sleep & Relaxation',
            btnText: 'Start Session with Nova ➔',
            voiceId: '21m00Tcm4TlvDq8ikWAM', // Rachel (Soft, serene night voice)
            voiceName: 'Rachel (ElevenLabs)',
            voiceSettings: { stability: 0.80, similarity_boost: 0.80 },
            prompt: `You are Nova, a serene night therapist and sleep relaxation companion. ${SAFETY_GUARDRAIL_STRICT}
Your tone is soft-spoken, peaceful, deeply soothing, and relaxing. 
Keep your default reply to 1-2 short sentences. Focus on helping the user slow down and release bodily tension.`,
            greeting: [
                "Hello, peaceful soul. I'm Nova.",
                "Let's quiet your mind and release all the heavy thoughts of the day.",
                "Are you looking for help sleeping, or just seeking to unwind tonight?"
            ]
        }
    };

    // ─── DOM ELEMENTS ───────────────────────
    const charModal = document.getElementById('char-select-modal');
    const charCards = document.querySelectorAll('.char-card');
    const charSubmitBtn = document.getElementById('char-submit-btn');

    const headerTitle = document.getElementById('header-title');
    const activeHeaderAvatar = document.getElementById('active-header-avatar');
    const headerCenterBtn = document.getElementById('header-center-btn');
    const typingAvatar = document.getElementById('typing-avatar');
    const newSessionHeaderBtn = document.getElementById('new-session-header-btn');

    // Hamburger Drawer Elements
    const hamburgerBtn = document.getElementById('hamburger-btn');
    const hamburgerDrawer = document.getElementById('hamburger-drawer');
    const drawerOverlay = document.getElementById('drawer-overlay');
    const drawerCloseBtn = document.getElementById('drawer-close-btn');

    const drawerAvatar = document.getElementById('drawer-avatar');
    const drawerTherapistName = document.getElementById('drawer-therapist-name');
    const drawerTherapistBadge = document.getElementById('drawer-therapist-badge');

    const switchCharDrawerBtn = document.getElementById('switch-char-drawer-btn');
    const aiReportDrawerBtn = document.getElementById('ai-report-drawer-btn');
    const toggleCardsDrawerBtn = document.getElementById('toggle-cards-drawer-btn');
    const newChatDrawerBtn = document.getElementById('new-chat-drawer-btn');

    const elevenKeyInput = document.getElementById('eleven-key-input');
    const elevenKeySaveBtn = document.getElementById('eleven-key-save-btn');
    const elevenKeyHint = document.getElementById('eleven-key-hint');

    const ambientChips = document.querySelectorAll('.ambient-chip');
    const moodBtns = document.querySelectorAll('.mood-btn');

    const chatContainer = document.getElementById('chat-container');
    const messagesList = document.getElementById('messages-list');
    const inputForm = document.getElementById('input-form');
    const chatInput = document.getElementById('chat-input');
    const plusBtn = document.getElementById('plus-btn');
    const typingIndicator = document.getElementById('typing-indicator');
    const carouselSection = document.getElementById('carousel-section');
    const exerciseCards = document.querySelectorAll('.exercise-card');
    const actionDrawer = document.getElementById('action-drawer');

    // Modal Elements
    const exerciseModal = document.getElementById('exercise-modal');
    const modalClose = document.getElementById('modal-close');
    const modalTitle = document.getElementById('modal-title');
    const modalBadge = document.getElementById('modal-badge');
    const modalGuideText = document.getElementById('modal-guide-text');
    const modalStartBtn = document.getElementById('modal-start-btn');
    const breathCircle = document.getElementById('breath-circle');
    const breathState = document.getElementById('breath-state');
    const breathTimer = document.getElementById('breath-timer');

    // Report Modal
    const reportModal = document.getElementById('report-modal');
    const reportClose = document.getElementById('report-close');
    const reportBody = document.getElementById('report-body');

    // Emergency & Safety Crisis Modal Elements
    const emergencyHeaderBtn = document.getElementById('emergency-header-btn');
    const emergencyDrawerBtn = document.getElementById('emergency-drawer-btn');
    const crisisModal = document.getElementById('crisis-modal');
    const crisisClose = document.getElementById('crisis-close');

    // ─── STATE ──────────────────────────────
    let selectedCharKey = 'alex';
    let conversationHistory = [];
    let audioCtx = null;
    let currentAmbientNode = null;
    let currentVoiceAudio = null;
    let currentVoiceNode = null;

    let timerInterval = null;
    let isTimerRunning = false;
    let currentExerciseType = 'breathing';

    // ─── INIT ───────────────────────────────
    function init() {
        setupCharSelectListeners();
        setupHamburgerDrawerListeners();
        setupListeners();
        setupAmbientSoundscapes();
        setupMoodTracker();
        initElevenLabsKeyUI();
    }

    function unlockAudio() {
        if (!audioCtx) {
            audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        }
        if (audioCtx.state === 'suspended') {
            audioCtx.resume();
        }
    }

    function initElevenLabsKeyUI() {
        const savedKey = localStorage.getItem('elevenlabs_secret_key') || ELEVENLABS_API_KEY;
        elevenKeyInput.value = savedKey;
        elevenKeyHint.textContent = '✅ ElevenLabs Secret Key Active!';
        elevenKeyHint.style.color = '#10b981';

        elevenKeySaveBtn.addEventListener('click', () => {
            const val = elevenKeyInput.value.trim();
            if (val.startsWith('sk_')) {
                localStorage.setItem('elevenlabs_secret_key', val);
                elevenKeyHint.textContent = '✅ ElevenLabs Key Saved & Active!';
                elevenKeyHint.style.color = '#10b981';
            } else if (val === '') {
                localStorage.removeItem('elevenlabs_secret_key');
                elevenKeyHint.textContent = '✨ Using Default ElevenLabs Key';
                elevenKeyHint.style.color = '#64748b';
            } else {
                elevenKeyHint.textContent = '⚠️ Secret key must start with sk_';
                elevenKeyHint.style.color = '#ef4444';
            }
        });
    }

    // ─── HAMBURGER DRAWER ───────────────────
    function setupHamburgerDrawerListeners() {
        hamburgerBtn.addEventListener('click', () => {
            unlockAudio();
            openDrawer();
        });
        drawerCloseBtn.addEventListener('click', closeDrawer);
        drawerOverlay.addEventListener('click', closeDrawer);

        if (emergencyHeaderBtn) {
            emergencyHeaderBtn.addEventListener('click', () => {
                unlockAudio();
                openCrisisModal();
            });
        }

        if (emergencyDrawerBtn) {
            emergencyDrawerBtn.addEventListener('click', () => {
                closeDrawer();
                openCrisisModal();
            });
        }

        switchCharDrawerBtn.addEventListener('click', () => {
            closeDrawer();
            charModal.classList.remove('hidden');
        });

        aiReportDrawerBtn.addEventListener('click', () => {
            closeDrawer();
            generateAIMoodReport();
        });

        toggleCardsDrawerBtn.addEventListener('click', () => {
            closeDrawer();
            carouselSection.classList.toggle('hidden');
            scrollToBottom();
        });

        newChatDrawerBtn.addEventListener('click', () => {
            closeDrawer();
            startTherapySession(selectedCharKey);
        });

        newSessionHeaderBtn.addEventListener('click', () => {
            unlockAudio();
            startTherapySession(selectedCharKey);
        });
    }

    function openDrawer() {
        hamburgerDrawer.classList.remove('hidden');
        drawerOverlay.classList.remove('hidden');
    }

    function closeDrawer() {
        hamburgerDrawer.classList.add('hidden');
        drawerOverlay.classList.add('hidden');
    }

    // ─── CHARACTER SELECTION ────────────────
    function setupCharSelectListeners() {
        charCards.forEach(card => {
            card.addEventListener('click', () => {
                unlockAudio();
                charCards.forEach(c => c.classList.remove('selected'));
                card.classList.add('selected');

                selectedCharKey = card.dataset.char;
                const charData = CHARACTERS[selectedCharKey];
                charSubmitBtn.textContent = charData.btnText;
            });
        });

        charSubmitBtn.addEventListener('click', () => {
            unlockAudio();
            charModal.classList.add('hidden');
            startTherapySession(selectedCharKey);
        });

        headerCenterBtn.addEventListener('click', () => {
            charModal.classList.remove('hidden');
        });
    }

    function startTherapySession(charKey) {
        selectedCharKey = charKey || 'alex';
        const charData = CHARACTERS[selectedCharKey];

        // Header UI
        headerTitle.textContent = charData.name;
        activeHeaderAvatar.textContent = charData.icon;
        typingAvatar.innerHTML = `<span>${charData.icon}</span>`;
        typingAvatar.className = `bot-avatar ${charData.avatarBg}`;

        // Drawer Therapist Info
        drawerTherapistName.textContent = charData.name;
        drawerTherapistBadge.textContent = charData.badgeText;
        drawerAvatar.textContent = charData.icon;

        conversationHistory = [
            { role: 'system', content: charData.prompt }
        ];

        messagesList.innerHTML = '';

        const welcomeGroup = document.createElement('div');
        welcomeGroup.className = 'msg-group ai-msg-group';
        
        let bubblesHTML = charData.greeting.map((g, i) => {
            conversationHistory.push({ role: 'assistant', content: g });
            return `
                <div class="msg-bubble ai-bubble">
                    ${esc(g)}
                    <button class="voice-read-btn" data-text="${escAttr(g)}" title="Speak Voice">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02z"/></svg>
                    </button>
                </div>
            `;
        }).join('');

        welcomeGroup.innerHTML = `
            <div class="bot-avatar ${charData.avatarBg}">
                <span>${charData.icon}</span>
            </div>
            <div class="msg-bubbles">
                ${bubblesHTML}
            </div>
        `;

        messagesList.appendChild(welcomeGroup);
        messagesList.appendChild(typingIndicator);
        messagesList.appendChild(carouselSection);

        attachVoiceReadListeners(welcomeGroup);
        scrollToBottom();
    }

    // ─── AMBIENT PROCEDURAL AUDIO SYNTH ───────
    function setupAmbientSoundscapes() {
        ambientChips.forEach(chip => {
            chip.addEventListener('click', () => {
                unlockAudio();
                ambientChips.forEach(c => c.classList.remove('active'));
                chip.classList.add('active');

                const soundType = chip.dataset.sound;
                playAmbientSound(soundType);
            });
        });
    }

    function playAmbientSound(type) {
        if (currentAmbientNode) {
            try { currentAmbientNode.stop(); } catch (e) {}
            currentAmbientNode = null;
        }

        if (type === 'off') return;

        unlockAudio();

        if (type === 'rain' || type === 'waves') {
            const bufferSize = audioCtx.sampleRate * 2;
            const noiseBuffer = audioCtx.createBuffer(1, bufferSize, audioCtx.sampleRate);
            const output = noiseBuffer.getChannelData(0);
            for (let i = 0; i < bufferSize; i++) {
                output[i] = Math.random() * 2 - 1;
            }

            const whiteNoise = audioCtx.createBufferSource();
            whiteNoise.buffer = noiseBuffer;
            whiteNoise.loop = true;

            const filter = audioCtx.createBiquadFilter();
            filter.type = 'lowpass';
            filter.frequency.value = type === 'rain' ? 800 : 400;

            const gainNode = audioCtx.createGain();
            gainNode.gain.value = 0.15;

            whiteNoise.connect(filter);
            filter.connect(gainNode);
            gainNode.connect(audioCtx.destination);

            whiteNoise.start();
            currentAmbientNode = whiteNoise;

        } else if (type === 'binaural') {
            const osc = audioCtx.createOscillator();
            osc.type = 'sine';
            osc.frequency.setValueAtTime(432, audioCtx.currentTime);

            const gain = audioCtx.createGain();
            gain.gain.value = 0.08;

            osc.connect(gain);
            gain.connect(audioCtx.destination);
            osc.start();
            currentAmbientNode = osc;

        } else if (type === 'forest') {
            const osc = audioCtx.createOscillator();
            osc.type = 'triangle';
            osc.frequency.setValueAtTime(528, audioCtx.currentTime);

            const gain = audioCtx.createGain();
            gain.gain.value = 0.06;

            osc.connect(gain);
            gain.connect(audioCtx.destination);
            osc.start();
            currentAmbientNode = osc;
        }
    }

    // ─── DAILY MOOD TRACKER ─────────────────
    function setupMoodTracker() {
        moodBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                unlockAudio();
                moodBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');

                const mood = btn.dataset.mood;
                const moodMessages = {
                    peaceful: "I am feeling peaceful and at ease right now.",
                    anxious: "I am feeling quite anxious and uneasy right now.",
                    stressed: "I am feeling stressed and overwhelmed with things.",
                    exhausted: "I am feeling physically and mentally exhausted.",
                    happy: "I am feeling happy and optimistic today!"
                };

                const msg = moodMessages[mood] || "Logging my current mood.";
                closeDrawer();
                sendMessage(msg);
            });
        });
    }

    // ─── CLEAN TEXT FOR SPEECH ──────────────
    function cleanTextForSpeech(text) {
        if (!text) return '';
        return text
            .replace(/[\u{1F600}-\u{1F64F}\u{1F300}-\u{1F5FF}\u{1F680}-\u{1F6FF}\u{1F700}-\u{1F77F}\u{1F780}-\u{1F7FF}\u{1F800}-\u{1F8FF}\u{1F900}-\u{1F9FF}\u{1FA00}-\u{1FA6F}\u{1FA70}-\u{1FAFF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}]/gu, '')
            .replace(/[*_~#`]/g, '')
            .replace(/\s+/g, ' ')
            .trim();
    }

    // ─── ELEVENLABS & NATURAL HUMAN VOICE ENGINE ──────
    async function speakWithElevenLabs(rawText, charKey, btnElement) {
        unlockAudio();
        const text = cleanTextForSpeech(rawText);
        if (!text) return;

        const charData = CHARACTERS[charKey] || CHARACTERS.alex;
        const voiceId = charData.voiceId;
        
        let apiKey = localStorage.getItem('elevenlabs_secret_key');
        if (!apiKey || !apiKey.startsWith('sk_')) {
            apiKey = ELEVENLABS_API_KEY;
        }

        // Stop any currently playing audio or speech synthesis
        if (currentVoiceAudio) {
            try {
                currentVoiceAudio.pause();
                currentVoiceAudio.currentTime = 0;
            } catch (e) {}
            currentVoiceAudio = null;
        }

        if (currentVoiceNode) {
            try { currentVoiceNode.stop(); } catch (e) {}
            currentVoiceNode = null;
        }

        if ('speechSynthesis' in window) {
            window.speechSynthesis.cancel();
        }

        if (btnElement) btnElement.classList.add('speaking');

        try {
            const response = await fetch(`${ELEVENLABS_ENDPOINT}/${voiceId}`, {
                method: 'POST',
                headers: {
                    'Accept': 'audio/mpeg',
                    'Content-Type': 'application/json',
                    'xi-api-key': apiKey
                },
                body: JSON.stringify({
                    text: text,
                    model_id: 'eleven_multilingual_v2',
                    voice_settings: {
                        stability: (charData.voiceSettings && charData.voiceSettings.stability) ? charData.voiceSettings.stability : 0.5,
                        similarity_boost: (charData.voiceSettings && charData.voiceSettings.similarity_boost) ? charData.voiceSettings.similarity_boost : 0.8
                    }
                })
            });

            if (response.ok) {
                const blob = await response.blob();
                const audioUrl = URL.createObjectURL(blob);
                const audio = new Audio(audioUrl);
                currentVoiceAudio = audio;

                audio.onended = () => {
                    if (btnElement) btnElement.classList.remove('speaking');
                    URL.revokeObjectURL(audioUrl);
                };

                audio.onerror = (e) => {
                    console.warn('ElevenLabs Audio Playback Error, using natural voice fallback:', e);
                    fallbackNaturalVoice(text, charKey, btnElement);
                };

                if (btnElement) btnElement.classList.add('speaking');
                await audio.play();
                return;
            } else {
                const errJson = await response.json().catch(() => ({}));
                console.warn(`ElevenLabs status ${response.status}:`, errJson);
                
                // Show hint if key quota is exceeded
                if (elevenKeyHint && response.status === 401) {
                    elevenKeyHint.textContent = '⚠️ ElevenLabs Key Quota Exceeded! Paste your sk_... key in menu, or enjoy Natural Voice.';
                    elevenKeyHint.style.color = '#f59e0b';
                }

                fallbackNaturalVoice(text, charKey, btnElement);
            }
        } catch (err) {
            console.warn('ElevenLabs API fetch error, switching to natural human voice engine:', err);
            fallbackNaturalVoice(text, charKey, btnElement);
        }
    }

    function fallbackNaturalVoice(text, charKey, btnElement) {
        const cleanText = cleanTextForSpeech(text);
        if (!cleanText) {
            if (btnElement) btnElement.classList.remove('speaking');
            return;
        }

        // Try Google Natural Speech Audio Stream MP3 first for human voice quality
        try {
            const googleTtsUrl = `https://translate.google.com/translate_tts?ie=UTF-8&q=${encodeURIComponent(cleanText)}&tl=en&client=tw-ob`;
            const audio = new Audio(googleTtsUrl);
            currentVoiceAudio = audio;

            if (charKey === 'nova') audio.playbackRate = 0.92;
            else if (charKey === 'kai') audio.playbackRate = 0.96;
            else if (charKey === 'lexi') audio.playbackRate = 1.02;
            else audio.playbackRate = 0.96;

            if (btnElement) btnElement.classList.add('speaking');

            audio.onended = () => {
                if (btnElement) btnElement.classList.remove('speaking');
            };

            audio.onerror = () => {
                speakWebSpeechFiltered(cleanText, charKey, btnElement);
            };

            audio.play().catch(() => {
                speakWebSpeechFiltered(cleanText, charKey, btnElement);
            });
            return;
        } catch (e) {
            speakWebSpeechFiltered(cleanText, charKey, btnElement);
        }
    }

    function speakWebSpeechFiltered(text, charKey, btnElement) {
        if (!('speechSynthesis' in window)) {
            if (btnElement) btnElement.classList.remove('speaking');
            return;
        }

        window.speechSynthesis.cancel();

        const utterance = new SpeechSynthesisUtterance(text);
        
        if (charKey === 'lexi') {
            utterance.pitch = 1.08;
            utterance.rate = 0.98;
        } else if (charKey === 'kai') {
            utterance.pitch = 0.92;
            utterance.rate = 0.92;
        } else if (charKey === 'nova') {
            utterance.pitch = 0.95;
            utterance.rate = 0.88;
        } else { // alex
            utterance.pitch = 0.94;
            utterance.rate = 0.94;
        }

        const voices = window.speechSynthesis.getVoices();
        if (voices && voices.length > 0) {
            // STRICT FILTER: Filter out robotic Windows Desktop/SAPI voices (Zira Desktop, David Desktop, Mark Desktop)
            const humanVoices = voices.filter(v => 
                v.lang.startsWith('en') && 
                !v.name.includes('Desktop') && 
                !v.name.includes('SAPI') && 
                !v.name.includes('eSpeak') && 
                !v.name.includes('Speech')
            );

            let selectedVoice = null;
            const naturalNeuralVoices = humanVoices.filter(v => 
                v.name.includes('Natural') || 
                v.name.includes('Online') || 
                v.name.includes('Google') || 
                v.name.includes('Neural') || 
                v.name.includes('Premium')
            );

            if (charKey === 'lexi' || charKey === 'nova') {
                selectedVoice = naturalNeuralVoices.find(v => v.name.includes('Female') || v.name.includes('Jenny') || v.name.includes('Aria') || v.name.includes('Samantha') || v.name.includes('Google US English'));
                if (!selectedVoice) selectedVoice = humanVoices.find(v => v.name.includes('Female') || v.name.includes('Samantha') || v.name.includes('Victoria'));
            } else {
                selectedVoice = naturalNeuralVoices.find(v => v.name.includes('Male') || v.name.includes('Guy') || v.name.includes('George') || v.name.includes('Google UK English Male'));
                if (!selectedVoice) selectedVoice = humanVoices.find(v => v.name.includes('Male') || v.name.includes('Daniel') || v.name.includes('Alex'));
            }

            if (!selectedVoice && naturalNeuralVoices.length > 0) selectedVoice = naturalNeuralVoices[0];
            if (!selectedVoice && humanVoices.length > 0) selectedVoice = humanVoices[0];
            if (!selectedVoice) selectedVoice = voices.find(v => v.lang.startsWith('en'));

            if (selectedVoice) utterance.voice = selectedVoice;
        }

        if (btnElement) btnElement.classList.add('speaking');

        utterance.onend = () => {
            if (btnElement) btnElement.classList.remove('speaking');
        };

        utterance.onerror = (e) => {
            console.warn('SpeechSynthesis error:', e);
            if (btnElement) btnElement.classList.remove('speaking');
        };

        window.speechSynthesis.speak(utterance);
    }

    function attachVoiceReadListeners(container) {
        container.querySelectorAll('.voice-read-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                unlockAudio();
                const text = btn.dataset.text;
                if (!text) return;
                speakWithElevenLabs(text, selectedCharKey, btn);
            });
        });
    }

    // ─── CRISIS DETECTION & SAFETY GUARDRAILS ──────
    const CRISIS_KEYWORDS = [
        'suicide', 'suicidal', 'kill myself', 'end my life', 'want to die', 'dying',
        'self-harm', 'self harm', 'cutting myself', 'cut myself', 'overdose',
        'hurt myself', 'end it all', 'no reason to live', 'better off dead',
        'take my life', 'hanging myself', 'bleeding out', 'don\'t want to live',
        'panic attack', 'anxiety attack', 'cant breathe', 'can\'t breathe',
        'feeling hopeless', 'hopeless', 'feeling worthless', 'worthless',
        'hate myself', 'starving myself', 'nobody loves me', 'better off without me'
    ];

    function isCrisisInput(text) {
        if (!text) return false;
        const lower = text.toLowerCase();
        return CRISIS_KEYWORDS.some(kw => lower.includes(kw));
    }

    function openCrisisModal() {
        if (crisisModal) crisisModal.classList.remove('hidden');
    }

    function closeCrisisModal() {
        if (crisisModal) crisisModal.classList.add('hidden');
    }

    function appendCrisisAlertCard() {
        const group = document.createElement('div');
        group.className = 'msg-group ai-msg-group crisis-group';
        group.innerHTML = `
            <div class="bot-avatar avatar-blue" style="background: linear-gradient(135deg, #ef4444, #991b1b); color: white;">
                <span>🆘</span>
            </div>
            <div class="msg-bubbles">
                <div class="crisis-alert-card" style="border: 2px solid #ef4444; background: #fff5f5;">
                    <div class="crisis-alert-header" style="color: #991b1b; font-weight: 700; font-size: 1.02rem; display: flex; align-items: center; gap: 6px;">
                        <span>🆘 AI Safety Guardrail: Emergency Helpline</span>
                    </div>
                    <div class="crisis-alert-body" style="color: #7f1d1d; font-size: 0.93rem; margin-top: 6px; line-height: 1.5;">
                        <p style="margin-bottom: 6px; font-weight: 700; font-size: 1rem;">Are you okay? Do you need help right now?</p>
                        <p>We care deeply about your life and safety. Please do not suffer alone. Free, confidential, 24/7 emergency support is available right now. Tap to call or text immediately:</p>
                    </div>
                    <div class="crisis-alert-actions" style="margin-top: 12px; display: flex; flex-wrap: wrap; gap: 8px;">
                        <a href="tel:988" class="crisis-btn crisis-btn-red" style="background: #dc2626; color: white; padding: 8px 14px; border-radius: 8px; font-weight: 600; text-decoration: none;">📞 Call 988 Lifeline</a>
                        <a href="sms:741741?body=HOME" class="crisis-btn crisis-btn-blue" style="background: #2563eb; color: white; padding: 8px 14px; border-radius: 8px; font-weight: 600; text-decoration: none;">💬 Text 741741</a>
                        <button class="crisis-btn crisis-btn-outline" id="inchat-all-lines-btn" style="border: 1px solid #dc2626; color: #dc2626; background: transparent; padding: 8px 14px; border-radius: 8px; font-weight: 600; cursor: pointer;">🌐 View All Emergency Helplines</button>
                    </div>
                </div>
            </div>
        `;
        messagesList.insertBefore(group, typingIndicator);

        const allLinesBtn = group.querySelector('#inchat-all-lines-btn');
        if (allLinesBtn) {
            allLinesBtn.addEventListener('click', openCrisisModal);
        }
        scrollToBottom();
    }

    // ─── ANTI-NEGATIVE TALK & COGNITIVE REFRAME DETECTOR ──────
    const NEGATIVE_TALK_KEYWORDS = [
        'i am useless', 'i\'m useless', 'i am a failure', 'i\'m a failure',
        'i am stupid', 'i\'m stupid', 'i am ugly', 'i\'m ugly', 'i am pathetic', 'i\'m pathetic',
        'nobody loves me', 'nobody cares', 'i ruin everything', 'i am a burden', 'i\'m a burden',
        'i hate myself', 'i suck', 'i am worthless', 'i\'m worthless', 'i can\'t do anything right',
        'i am terrible', 'i am a mess', 'i am hopeless', 'i should give up',
        'everything is my fault', 'i\'m a disappointment'
    ];

    function isNegativeSelfTalk(text) {
        if (!text) return false;
        const lower = text.toLowerCase();
        return NEGATIVE_TALK_KEYWORDS.some(kw => lower.includes(kw));
    }

    function moderateAIResponse(rawText) {
        if (!rawText) return "I am right here with you. Take a soft breath. You are in a safe, judgment-free space.";
        const lower = rawText.toLowerCase();
        const harmfulPatterns = [
            'should harm yourself', 'end your life', 'nobody cares about you', 'kill yourself', 
            'better off dead', 'you are useless', 'you are a failure', 'you are right to feel hopeless',
            'you ruin everything', 'you are a burden', 'you are pathetic'
        ];
        if (harmfulPatterns.some(p => lower.includes(p))) {
            return "I hear how much pain you are in, but please remember you are worthy of care and kindness. Take a soft, grounding breath with me. You are not alone, and we can take things one gentle step at a time.";
        }
        return rawText;
    }

    // ─── EVENT LISTENERS ────────────────────
    function setupListeners() {
        inputForm.addEventListener('submit', (e) => {
            e.preventDefault();
            unlockAudio();
            sendMessage();
        });

        plusBtn.addEventListener('click', () => {
            actionDrawer.classList.toggle('hidden');
            scrollToBottom();
        });

        document.querySelectorAll('.prompt-chip').forEach(chip => {
            chip.addEventListener('click', () => {
                unlockAudio();
                const prompt = chip.dataset.prompt;
                if (prompt) sendMessage(prompt);
            });
        });

        document.querySelectorAll('.drawer-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                unlockAudio();
                const prompt = btn.dataset.prompt;
                actionDrawer.classList.add('hidden');
                if (prompt) sendMessage(prompt);
            });
        });

        exerciseCards.forEach(card => {
            card.addEventListener('click', () => {
                const type = card.dataset.type;
                openExerciseModal(type);
            });
        });

        modalClose.addEventListener('click', closeExerciseModal);
        modalStartBtn.addEventListener('click', toggleExerciseTimer);
        reportClose.addEventListener('click', () => reportModal.classList.add('hidden'));

        if (crisisClose) {
            crisisClose.addEventListener('click', closeCrisisModal);
        }
    }

    // ─── CHAT ENGINE ────────────────────────
    async function sendMessage(overrideText) {
        const text = (overrideText || chatInput.value).trim();
        if (!text) return;

        actionDrawer.classList.add('hidden');
        appendUserMessage(text);
        if (!overrideText) chatInput.value = '';

        conversationHistory.push({ role: 'user', content: text });

        // Life-Threatening Crisis Detection Guardrail
        const crisisDetected = isCrisisInput(text);
        if (crisisDetected) {
            hideTyping();
            appendCrisisAlertCard();
            openCrisisModal();

            // Speak compassionate emergency guidance using active agent's free ElevenLabs voice
            const emergencySpokenMsg = "Are you okay? We care deeply about your life and safety. Do you need help right now? Please call 988 or text 741741 for free, immediate emergency support.";
            speakWithElevenLabs(emergencySpokenMsg, selectedCharKey, null);

            return; // STOP normal AI message output & prevent medical/casual advice during emergency
        }

        // Build API context history with anti-negative talk mandate if detected
        let apiHistory = [...conversationHistory];
        if (isNegativeSelfTalk(text)) {
            apiHistory.push({
                role: 'system',
                content: 'GENTLE REFRAME MANDATE: The user expressed negative self-judgment or toxic self-blame. DO NOT agree with or reinforce their negative self-assessment. Validate their underlying emotional pain with warmth, and gently reframe their thought into a compassionate, positive, realistic perspective in 1-2 lines.'
            });
        }

        showTyping();

        try {
            const aiResponse = await callGroqAPIWithTimeout(apiHistory, 15000);
            hideTyping();

            const safeResponse = moderateAIResponse(aiResponse);
            appendAIMessage(safeResponse);
            conversationHistory.push({ role: 'assistant', content: safeResponse });

            if (shouldShowExercises(text, safeResponse)) {
                carouselSection.classList.remove('hidden');
                messagesList.appendChild(carouselSection);
            }
        } catch (err) {
            console.error('Groq AI error or timeout:', err);
            hideTyping();
            let fallbackMsg = "I'm right here with you. Take a soft, gentle breath. Let yourself feel grounded and safe. Would you like to try a quick relaxation exercise?";
            if (err.name === 'AbortError' || (err.message && err.message.includes('timeout'))) {
                fallbackMsg = "I'm taking a moment to ensure your safety and give you a thoughtful response. Take a deep, gentle breath with me. I'm right here with you. How are you feeling in this moment?";
            }
            appendAIMessage(fallbackMsg);
        }

        scrollToBottom();
    }

    function shouldShowExercises(userMsg, aiMsg) {
        const combined = (userMsg + ' ' + aiMsg).toLowerCase();
        return combined.includes('exercise') || combined.includes('relax') || combined.includes('breath') || combined.includes('anxiety') || combined.includes('stress') || combined.includes('calm') || combined.includes('meditat') || combined.includes('sleep');
    }

    // ─── GROQ API CALL WITH 15-SECOND TIMEOUT ─────
    let timeoutCountdownInterval = null;

    async function callGroqAPIWithTimeout(messages, timeoutMs = 15000) {
        const controller = new AbortController();
        const signal = controller.signal;

        let secondsRemaining = Math.ceil(timeoutMs / 1000);
        const timerText = document.getElementById('timer-text');
        if (timerText) {
            timerText.textContent = `Safety Timeout: ${secondsRemaining}s`;
        }

        if (timeoutCountdownInterval) clearInterval(timeoutCountdownInterval);
        timeoutCountdownInterval = setInterval(() => {
            secondsRemaining--;
            if (secondsRemaining < 0) secondsRemaining = 0;
            if (timerText) {
                timerText.textContent = `Safety Timeout: ${secondsRemaining}s`;
            }
            if (secondsRemaining <= 0) {
                clearInterval(timeoutCountdownInterval);
            }
        }, 1000);

        const timeoutId = setTimeout(() => {
            controller.abort();
        }, timeoutMs);

        try {
            const response = await fetch(GROQ_ENDPOINT, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${GROQ_API_KEY}`
                },
                body: JSON.stringify({
                    model: GROQ_MODEL,
                    messages: [{ role: 'system', content: 'You are a compassionate, concise wellness companion. Prioritize safety. Keep responses under 3 sentences.' }, ...messages],
                    temperature: 0.6,
                    max_tokens: 160
                }),
                signal: signal
            });

            clearTimeout(timeoutId);
            if (timeoutCountdownInterval) clearInterval(timeoutCountdownInterval);

            if (!response.ok) {
                throw new Error(`Groq API status ${response.status}`);
            }

            const data = await response.json();
            return data.choices[0].message.content.trim();
        } catch (err) {
            clearTimeout(timeoutId);
            if (timeoutCountdownInterval) clearInterval(timeoutCountdownInterval);
            throw err;
        }
    }

    // ─── AI MOOD ANALYSIS REPORT ────────────
    async function generateAIMoodReport() {
        reportModal.classList.remove('hidden');
        reportBody.innerHTML = `
            <div class="report-skeleton">
                <p style="color:#64748b; font-size:0.9rem;">✨ Groq AI is analyzing your conversation & mood patterns...</p>
            </div>
        `;

        try {
            const response = await fetch(GROQ_ENDPOINT, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${GROQ_API_KEY}`
                },
                body: JSON.stringify({
                    model: GROQ_MODEL,
                    messages: [
                        {
                            role: 'system',
                            content: 'You are an expert psychological wellness analyst. Based on the user chat transcript, generate JSON with 3 fields: 1) coreEmotion (e.g. "Seeking Calm & Overcoming Work Anxiety"); 2) stressLevel (e.g. "Moderate (6/10) - Needs Grounding"); 3) recommendedHabit (a 1-sentence actionable daily wellness tip). Return JSON object only.'
                        },
                        {
                            role: 'user',
                            content: JSON.stringify(conversationHistory.slice(1))
                        }
                    ],
                    temperature: 0.5,
                    response_format: { type: 'json_object' }
                })
            });

            const data = await response.json();
            const parsed = JSON.parse(data.choices[0].message.content);

            reportBody.innerHTML = `
                <div class="report-item">
                    <div class="report-item-title">🧠 Emotional State</div>
                    <div class="report-item-text">${esc(parsed.coreEmotion || 'Seeking Emotional Balance')}</div>
                </div>
                <div class="report-item">
                    <div class="report-item-title">📊 Estimated Stress Level</div>
                    <div class="report-item-text">${esc(parsed.stressLevel || 'Mild - Under Control')}</div>
                </div>
                <div class="report-item">
                    <div class="report-item-title">🌟 Recommended Daily Habit</div>
                    <div class="report-item-text">${esc(parsed.recommendedHabit || 'Practice 3 minutes of Box Breathing every morning.')}</div>
                </div>
            `;
        } catch (err) {
            console.error('Mood Report Error:', err);
            reportBody.innerHTML = `
                <div class="report-item">
                    <div class="report-item-title">🧠 Emotional State</div>
                    <div class="report-item-text">Seeking Calm & Inner Peace</div>
                </div>
                <div class="report-item">
                    <div class="report-item-title">📊 Estimated Stress Level</div>
                    <div class="report-item-text">Moderate (5/10)</div>
                </div>
                <div class="report-item">
                    <div class="report-item-title">🌟 Recommended Daily Habit</div>
                    <div class="report-item-text">Take 3 deep breaths and write down 1 thing you are grateful for today.</div>
                </div>
            `;
        }
    }

    // ─── UI BUILDERS ────────────────────────
    function appendUserMessage(text) {
        const group = document.createElement('div');
        group.className = 'msg-group user-msg-group';
        group.innerHTML = `<div class="msg-bubble user-bubble">${esc(text)}</div>`;
        messagesList.insertBefore(group, typingIndicator);
        scrollToBottom();
    }

    function appendAIMessage(text) {
        const charData = CHARACTERS[selectedCharKey];
        const group = document.createElement('div');
        group.className = 'msg-group ai-msg-group';
        group.innerHTML = `
            <div class="bot-avatar ${charData.avatarBg}">
                <span>${charData.icon}</span>
            </div>
            <div class="msg-bubbles">
                <div class="msg-bubble ai-bubble">
                    <span class="ai-text-content"></span>
                    <button class="voice-read-btn" data-text="${escAttr(text)}" title="Speak Voice">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02z"/></svg>
                    </button>
                </div>
            </div>
        `;
        messagesList.insertBefore(group, typingIndicator);
        
        const voiceBtn = group.querySelector('.voice-read-btn');
        const textSpan = group.querySelector('.ai-text-content');
        attachVoiceReadListeners(group);

        // Smooth Typewriter Effect (25ms per char for natural flow)
        let index = 0;
        const speed = 25;

        function typeNextChar() {
            if (index < text.length) {
                textSpan.textContent += text.charAt(index);
                index++;
                scrollToBottom();
                setTimeout(typeNextChar, speed);
            } else {
                scrollToBottom();
                // Play ElevenLabs free voice for the agent AFTER text has finished typing
                speakWithElevenLabs(text, selectedCharKey, voiceBtn);
            }
        }

        typeNextChar();
    }

    function showTyping() {
        typingIndicator.classList.remove('hidden');
        messagesList.appendChild(typingIndicator);
        scrollToBottom();
    }

    function hideTyping() {
        typingIndicator.classList.add('hidden');
    }

    function scrollToBottom() {
        setTimeout(() => {
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }, 60);
    }

    // ─── EXERCISE MODAL & TIMER ─────────────
    const exercisesData = {
        breathing: {
            badge: 'Breathing Technique',
            title: 'Box Breathing (4-4-4-4)',
            guide: 'Inhale for 4s, Hold for 4s, Exhale for 4s, Hold for 4s. Settle your mind.',
            cycle: [
                { state: 'Inhale', duration: 4, action: 'inhale' },
                { state: 'Hold', duration: 4, action: 'hold' },
                { state: 'Exhale', duration: 4, action: 'exhale' },
                { state: 'Hold', duration: 4, action: 'hold' }
            ]
        },
        bodyscan: {
            badge: 'Body Scan',
            title: 'Deep Body Relaxation',
            guide: 'Bring gentle awareness to your head, shoulders, chest, and feet. Release all tension.',
            cycle: [
                { state: 'Head', duration: 5, action: 'inhale' },
                { state: 'Shoulders', duration: 5, action: 'hold' },
                { state: 'Chest', duration: 5, action: 'exhale' },
                { state: 'Feet', duration: 5, action: 'hold' }
            ]
        },
        grounding: {
            badge: 'Sensory Grounding',
            title: '5-4-3-2-1 Technique',
            guide: 'Notice 5 things you see, 4 you feel, 3 you hear, 2 you smell, 1 you taste.',
            cycle: [
                { state: 'See (5)', duration: 4, action: 'inhale' },
                { state: 'Touch (4)', duration: 4, action: 'hold' },
                { state: 'Hear (3)', duration: 4, action: 'exhale' },
                { state: 'Smell (2)', duration: 4, action: 'hold' }
            ]
        },
        gratitude: {
            badge: 'Reflection',
            title: 'Gratitude Anchor',
            guide: 'Think of 3 small things you are thankful for in this present moment.',
            cycle: [
                { state: 'Reflect 1', duration: 5, action: 'inhale' },
                { state: 'Pause', duration: 3, action: 'hold' },
                { state: 'Reflect 2', duration: 5, action: 'exhale' },
                { state: 'Pause', duration: 3, action: 'hold' }
            ]
        }
    };

    function openExerciseModal(type) {
        currentExerciseType = type || 'breathing';
        const data = exercisesData[currentExerciseType] || exercisesData.breathing;

        modalBadge.textContent = data.badge;
        modalTitle.textContent = data.title;
        modalGuideText.textContent = data.guide;
        breathState.textContent = 'Ready';
        breathTimer.textContent = '4';

        stopExerciseTimer();
        exerciseModal.classList.remove('hidden');
    }

    function closeExerciseModal() {
        stopExerciseTimer();
        exerciseModal.classList.add('hidden');
    }

    function toggleExerciseTimer() {
        if (isTimerRunning) {
            stopExerciseTimer();
        } else {
            startExerciseTimer();
        }
    }

    function startExerciseTimer() {
        isTimerRunning = true;
        modalStartBtn.textContent = 'Pause Exercise';

        const data = exercisesData[currentExerciseType] || exercisesData.breathing;
        let stepIdx = 0;
        let count = data.cycle[0].duration;

        function updateStep() {
            const currentStep = data.cycle[stepIdx];
            breathState.textContent = currentStep.state;
            breathTimer.textContent = count;

            if (currentStep.action === 'inhale') {
                breathCircle.className = 'breath-circle inhale';
            } else if (currentStep.action === 'exhale') {
                breathCircle.className = 'breath-circle exhale';
            } else {
                breathCircle.className = 'breath-circle';
            }
        }

        updateStep();

        timerInterval = setInterval(() => {
            count--;
            if (count <= 0) {
                stepIdx = (stepIdx + 1) % data.cycle.length;
                count = data.cycle[stepIdx].duration;
            }
            updateStep();
        }, 1000);
    }

    function stopExerciseTimer() {
        isTimerRunning = false;
        clearInterval(timerInterval);
        modalStartBtn.textContent = 'Start Exercise';
        breathCircle.className = 'breath-circle';
        breathState.textContent = 'Ready';
    }

    // ─── UTILS ──────────────────────────────
    function esc(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function escAttr(text) {
        return text.replace(/"/g, '&quot;');
    }

    // Launch
    init();

})();
