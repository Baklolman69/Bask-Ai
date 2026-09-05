/**
 * Bask Virtual Pet Application Controller
 * 10x Upgraded Web Audio Buffer Engine, Pre-cached 60fps Lottie Player, and Touch Coordinates Matrix.
 */

document.addEventListener('DOMContentLoaded', () => {
  let pet = window.petModel.pet;
  let activeShopTab = 'foods';
  let audioCtx = null;
  let audioBufferCache = {};
  let lottieJsonCache = {};
  let currentLottieAnim = null;
  let currentAnimStateKey = null;
  let actionRevertTimeout = null;
  let inactivityTimer = null;
  let isSoundMuted = false;
  let touchStreakCount = 0;
  let touchStreakTimer = null;

  // ALL 11 LOTTIE ANIMATIONS MAP
  const LOTTIE_MAP = {
    hello: 'cat_hello.json',
    petting: 'cat_petting.json',
    eating: 'cat_eating.json',
    sleeping: 'cat_sleeping.json',
    dreaming: 'cat_dreaming.json',
    dance: 'cat_dance.json',
    confused: 'cat_confused.json',
    surprised: 'cat_wow.json',
    laughing: 'cat_laughing.json',
    idle: 'cat_idle.json',
    puppy_boxing: 'puppy_boxing.json'
  };

  // REAL CAT AUDIO EFFECTS MAP (7 SOUNDS)
  const CAT_AUDIO_MAP = {
    purr: 'cat_purring.mp3',
    trill: 'cat_trilling.mp3',
    cute: 'cat_cute_meow.mp3',
    hungry: 'cat_hungry.wav',
    begging: 'cat_begging.wav',
    slow: 'cat_meow_slow.mp3',
    meow: 'cat_meow.mp3'
  };

  // SPEECH SYNTHESIS MEOW FALLBACKS
  const MEOW_SPEECH_MAP = {
    purr: 'Purrrrrr... purr purr',
    trill: 'Prr-meow! Mrow!',
    cute: 'Mew mew! Hello!',
    hungry: 'Meow! Feed me fish cookies!',
    begging: 'Mrow! Please give treats!',
    slow: 'Yawn... Meow...',
    meow: 'Meow! Purr!'
  };

  // Minigame Variables
  let gameRunning = false;
  let gameLoopId = null;
  let score = 0;
  let earnedCoins = 0;
  let playerX = 160;
  let items = [];

  // --- INITIALIZATION ---
  initApp();

  function initApp() {
    initBrowserAudioUnlock();
    preloadLottieJsonFiles();
    initUIEvents();
    renderPetStage();
    updateUI();
    resetInactivityTimer();
    showToast('💾 Pet state loaded safely from cache memory!');
  }

  // --- 1. PRE-DECODED WEB AUDIO BUFFER ENGINE (ZERO LATENCY SYNC) ---
  function initBrowserAudioUnlock() {
    const unlocker = () => {
      try {
        if (!audioCtx) {
          audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        }
        if (audioCtx.state === 'suspended') {
          audioCtx.resume();
        }
        preloadAudioBuffers();
      } catch (e) {}

      if ('speechSynthesis' in window) {
        window.speechSynthesis.getVoices();
      }

      document.removeEventListener('click', unlocker);
      document.removeEventListener('touchstart', unlocker);
    };

    document.addEventListener('click', unlocker, { once: true });
    document.addEventListener('touchstart', unlocker, { once: true });

    const soundBtn = document.getElementById('toggle-sound-btn');
    if (soundBtn) {
      soundBtn.onclick = (e) => {
        e.stopPropagation();
        isSoundMuted = !isSoundMuted;
        soundBtn.textContent = isSoundMuted ? '🔇' : '🔊';
        if (isSoundMuted && 'speechSynthesis' in window) window.speechSynthesis.cancel();
        showToast(isSoundMuted ? '🔇 Sound muted' : '🔊 Sound enabled');
      };
    }
  }

  async function preloadAudioBuffers() {
    if (!audioCtx) return;
    for (const [key, fileName] of Object.entries(CAT_AUDIO_MAP)) {
      try {
        const response = await fetch(`./cat-audio/${fileName}`);
        const arrayBuffer = await response.arrayBuffer();
        const decodedBuffer = await audioCtx.decodeAudioData(arrayBuffer);
        audioBufferCache[key] = decodedBuffer;
      } catch (e) {
        console.warn(`Audio preload note for ${fileName}:`, e);
      }
    }
  }

  // Instant Web Audio Buffer Playback (< 1ms Sync)
  function playCatAudio(soundKey) {
    if (isSoundMuted) return;

    if (!audioCtx) {
      audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    }
    if (audioCtx.state === 'suspended') {
      audioCtx.resume();
    }

    const decodedBuffer = audioBufferCache[soundKey];

    if (decodedBuffer) {
      try {
        const source = audioCtx.createBufferSource();
        source.buffer = decodedBuffer;
        const gainNode = audioCtx.createGain();
        gainNode.gain.setValueAtTime(0.95, audioCtx.currentTime);
        source.connect(gainNode);
        gainNode.connect(audioCtx.destination);
        source.start(0);
        return;
      } catch (e) {}
    }

    // Secondary HTML5 Audio fallback
    const fileName = CAT_AUDIO_MAP[soundKey] || 'cat_meow.mp3';
    try {
      const audio = new Audio(`./cat-audio/${fileName}`);
      audio.volume = 0.9;
      audio.play().catch(() => speakCatVoiceFallback(soundKey));
    } catch (e) {
      speakCatVoiceFallback(soundKey);
    }
  }

  function speakCatVoiceFallback(soundKey) {
    if (isSoundMuted || !('speechSynthesis' in window)) {
      playSoftMelodicChime();
      return;
    }
    try {
      window.speechSynthesis.cancel();
      const meowText = MEOW_SPEECH_MAP[soundKey] || 'Meow!';
      const utterance = new SpeechSynthesisUtterance(meowText);
      utterance.pitch = 1.7;
      utterance.rate = 1.1;
      utterance.volume = 0.8;
      window.speechSynthesis.speak(utterance);
    } catch (e) {
      playSoftMelodicChime();
    }
  }

  function playSoftMelodicChime() {
    if (isSoundMuted) return;
    try {
      if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      if (audioCtx.state === 'suspended') audioCtx.resume();

      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();

      osc.type = 'sine';
      osc.frequency.setValueAtTime(587.33, audioCtx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(880, audioCtx.currentTime + 0.15);

      gain.gain.setValueAtTime(0.1, audioCtx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.25);

      osc.connect(gain);
      gain.connect(audioCtx.destination);

      osc.start();
      osc.stop(audioCtx.currentTime + 0.25);
    } catch (e) {}
  }

  // --- 2. PRE-CACHED 60FPS LOTTIE ENGINE (INSTANT SMOOTH SWITCHING) ---
  async function preloadLottieJsonFiles() {
    for (const [key, fileName] of Object.entries(LOTTIE_MAP)) {
      try {
        const res = await fetch(`aniamtions/${fileName}`);
        const json = await res.json();
        lottieJsonCache[key] = json;
      } catch (e) {}
    }
  }

  function playLottieState(stateKey, loop = true, autoRevertMs = 0) {
    if (pet.species === 'shiba' && stateKey === 'boxing') {
      stateKey = 'puppy_boxing';
    }

    const lottieContainer = document.getElementById('lottie-pet-container');
    const avatarContainer = document.getElementById('pet-avatar-render');

    if (actionRevertTimeout) {
      clearTimeout(actionRevertTimeout);
      actionRevertTimeout = null;
    }

    if (currentAnimStateKey === stateKey && currentLottieAnim && loop) {
      return;
    }

    currentAnimStateKey = stateKey;
    const animationData = lottieJsonCache[stateKey];
    const fileName = LOTTIE_MAP[stateKey] || LOTTIE_MAP.hello;

    if (typeof lottie !== 'undefined') {
      lottieContainer.style.display = 'flex';
      avatarContainer.style.display = 'none';

      if (currentLottieAnim) {
        try { currentLottieAnim.destroy(); } catch (e) {}
      }

      const lottieParams = {
        container: lottieContainer,
        renderer: 'svg',
        loop: loop,
        autoplay: true
      };

      if (animationData) {
        lottieParams.animationData = animationData;
      } else {
        lottieParams.path = `aniamtions/${fileName}`;
      }

      currentLottieAnim = lottie.loadAnimation(lottieParams);

      if (!loop || autoRevertMs > 0) {
        const revertDelay = autoRevertMs > 0 ? autoRevertMs : 2400;
        actionRevertTimeout = setTimeout(() => {
          currentAnimStateKey = null;
          playLottieState('hello', true);
        }, revertDelay);
      }
    } else {
      lottieContainer.style.display = 'none';
      avatarContainer.style.display = 'block';
    }
  }

  function resetInactivityTimer() {
    if (inactivityTimer) clearTimeout(inactivityTimer);
    inactivityTimer = setTimeout(() => {
      if (!pet.isSleeping && currentAnimStateKey !== 'sleeping' && currentAnimStateKey !== 'dreaming') {
        playLottieState('idle', true);
        playCatAudio('slow');
        document.getElementById('pet-speech-bubble').textContent = '💤 *Bored yawns...* Tap me to play!';
      }
    }, 12000);
  }

  function renderPetStage(actionType = 'idle') {
    const avatarContainer = document.getElementById('pet-avatar-render');
    const mood = window.petModel.getMoodState();
    const species = pet.species || 'cat';

    if (species === 'cat' && window.catResponseAlgorithm) {
      const evalRes = window.catResponseAlgorithm.evaluateCatBehavior('', pet, actionType, { streak: touchStreakCount });
      
      const isTransientAction = actionType === 'pet' || actionType === 'food' || actionType === 'laser' || actionType === 'bath';
      playLottieState(evalRes.stateKey, !isTransientAction, isTransientAction ? 2400 : 0);
      
      document.getElementById('pet-speech-bubble').textContent = evalRes.fullDialogue;
    } else if (species === 'shiba') {
      if (mood.mood === 'hungry' || mood.mood === 'sad') {
        playLottieState('puppy_boxing', true);
      } else {
        avatarContainer.style.display = 'block';
        document.getElementById('lottie-pet-container').style.display = 'none';
        avatarContainer.innerHTML = renderShibaSVG(mood, pet.equipped);
      }
    } else {
      avatarContainer.style.display = 'block';
      document.getElementById('lottie-pet-container').style.display = 'none';
      if (species === 'bunny') avatarContainer.innerHTML = renderBunnySVG(mood, pet.equipped);
      if (species === 'dragon') avatarContainer.innerHTML = renderDragonSVG(mood, pet.equipped);
    }

    const stage = document.getElementById('pet-stage-viewport');
    if (pet.isSleeping) {
      stage.classList.add('night-mode');
    } else {
      stage.classList.remove('night-mode');
    }
  }

  function renderShibaSVG(moodState, equipped) {
    const isSleeping = moodState.mood === 'sleeping';
    const isHungry = moodState.mood === 'hungry';
    
    const hatOverlay = equipped.hat === 'hat_crown' ? '👑' : equipped.hat === 'hat_party' ? '🥳' : equipped.hat === 'hat_frog' ? '🐸' : '';
    const glassOverlay = equipped.glasses === 'glass_star' ? '⭐' : equipped.glasses === 'glass_smart' ? '👓' : '';
    const accOverlay = equipped.accessory === 'acc_bow' ? '🎀' : equipped.accessory === 'acc_wings' ? '🪽' : '';

    return `
      <svg viewBox="0 0 200 200" width="100%" height="100%">
        <ellipse cx="100" cy="180" rx="60" ry="12" fill="rgba(0,0,0,0.12)" />
        <path d="M 145 125 C 175 110, 165 75, 140 85" fill="none" stroke="#eab308" stroke-width="18" stroke-linecap="round" />
        <ellipse cx="100" cy="135" rx="55" ry="45" fill="#f59e0b" />
        <ellipse cx="100" cy="140" rx="38" ry="30" fill="#fef3c7" />
        ${equipped.accessory === 'acc_wings' ? '<text x="15" y="110" font-size="35">🪽</text><text x="145" y="110" font-size="35" transform="scale(-1,1) translate(-180,0)">🪽</text>' : ''}
        <circle cx="100" cy="85" r="48" fill="#f59e0b" />
        <polygon points="58,55 75,25 90,52" fill="#d97706" />
        <polygon points="142,55 125,25 110,52" fill="#d97706" />
        <polygon points="64,52 75,32 85,50" fill="#fef3c7" />
        <polygon points="136,52 125,32 115,50" fill="#fef3c7" />
        <ellipse cx="100" cy="98" rx="24" ry="18" fill="#fef3c7" />
        <ellipse cx="100" cy="88" rx="7" ry="5" fill="#1e293b" />
        ${isSleeping ? `
          <path d="M 75 80 Q 85 88 90 80" fill="none" stroke="#1e293b" stroke-width="4" stroke-linecap="round" />
          <path d="M 110 80 Q 115 88 125 80" fill="none" stroke="#1e293b" stroke-width="4" stroke-linecap="round" />
        ` : isHungry ? `
          <circle cx="82" cy="78" r="6" fill="#1e293b" />
          <circle cx="118" cy="78" r="6" fill="#1e293b" />
          <path d="M 92 104 Q 100 96 108 104" fill="none" stroke="#1e293b" stroke-width="3" stroke-linecap="round" />
        ` : `
          <circle cx="82" cy="78" r="7" fill="#1e293b" />
          <circle cx="84" cy="76" r="2.5" fill="#fff" />
          <circle cx="118" cy="78" r="7" fill="#1e293b" />
          <circle cx="120" cy="76" r="2.5" fill="#fff" />
          <path d="M 95 102 Q 100 112 105 102" fill="#f43f5e" stroke="#1e293b" stroke-width="2" />
        `}
        <circle cx="68" cy="92" r="7" fill="rgba(244,63,94,0.3)" />
        <circle cx="132" cy="92" r="7" fill="rgba(244,63,94,0.3)" />
        ${hatOverlay ? `<text x="76" y="32" font-size="34">${hatOverlay}</text>` : ''}
        ${glassOverlay ? `<text x="75" y="85" font-size="28">${glassOverlay}</text>` : ''}
        ${accOverlay && equipped.accessory === 'acc_bow' ? `<text x="84" y="125" font-size="28">${accOverlay}</text>` : ''}
      </svg>
    `;
  }

  function renderBunnySVG(moodState, equipped) {
    const isSleeping = moodState.mood === 'sleeping';
    const hatOverlay = equipped.hat === 'hat_crown' ? '👑' : equipped.hat === 'hat_party' ? '🥳' : equipped.hat === 'hat_frog' ? '🐸' : '';
    const glassOverlay = equipped.glasses === 'glass_star' ? '⭐' : equipped.glasses === 'glass_smart' ? '👓' : '';
    const accOverlay = equipped.accessory === 'acc_bow' ? '🎀' : equipped.accessory === 'acc_wings' ? '🪽' : '';

    return `
      <svg viewBox="0 0 200 200" width="100%" height="100%">
        <ellipse cx="100" cy="180" rx="55" ry="10" fill="rgba(0,0,0,0.1)" />
        <ellipse cx="78" cy="40" rx="14" ry="42" fill="#f472b6" />
        <ellipse cx="78" cy="42" rx="8" ry="32" fill="#fbcfe8" />
        <ellipse cx="122" cy="40" rx="14" ry="42" fill="#f472b6" />
        <ellipse cx="122" cy="42" rx="8" ry="32" fill="#fbcfe8" />
        <ellipse cx="100" cy="140" rx="50" ry="40" fill="#f472b6" />
        <ellipse cx="100" cy="142" rx="35" ry="26" fill="#fff" />
        <circle cx="100" cy="95" r="44" fill="#f472b6" />
        ${isSleeping ? `
          <path d="M 78 92 Q 86 98 90 92" fill="none" stroke="#1e293b" stroke-width="4" stroke-linecap="round" />
          <path d="M 110 92 Q 114 98 122 92" fill="none" stroke="#1e293b" stroke-width="4" stroke-linecap="round" />
        ` : `
          <circle cx="82" cy="90" r="6.5" fill="#1e293b" />
          <circle cx="84" cy="88" r="2.5" fill="#fff" />
          <circle cx="118" cy="90" r="6.5" fill="#1e293b" />
          <circle cx="120" cy="88" r="2.5" fill="#fff" />
        `}
        <polygon points="97,100 103,100 100,104" fill="#fb7185" />
        <circle cx="70" cy="100" r="6" fill="rgba(251,113,133,0.4)" />
        <circle cx="130" cy="100" r="6" fill="rgba(251,113,133,0.4)" />
        ${hatOverlay ? `<text x="78" y="28" font-size="32">${hatOverlay}</text>` : ''}
        ${glassOverlay ? `<text x="75" y="95" font-size="28">${glassOverlay}</text>` : ''}
        ${accOverlay && equipped.accessory === 'acc_bow' ? `<text x="84" y="132" font-size="28">${accOverlay}</text>` : ''}
      </svg>
    `;
  }

  function renderDragonSVG(moodState, equipped) {
    const isSleeping = moodState.mood === 'sleeping';
    const hatOverlay = equipped.hat === 'hat_crown' ? '👑' : equipped.hat === 'hat_party' ? '🥳' : equipped.hat === 'hat_frog' ? '🐸' : '';
    const glassOverlay = equipped.glasses === 'glass_star' ? '⭐' : equipped.glasses === 'glass_smart' ? '👓' : '';
    const accOverlay = equipped.accessory === 'acc_bow' ? '🎀' : equipped.accessory === 'acc_wings' ? '🪽' : '';

    return `
      <svg viewBox="0 0 200 200" width="100%" height="100%">
        <ellipse cx="100" cy="180" rx="55" ry="10" fill="rgba(0,0,0,0.12)" />
        <path d="M 45 105 Q 10 70 55 130 Z" fill="#059669" />
        <path d="M 155 105 Q 190 70 145 130 Z" fill="#059669" />
        <path d="M 140 145 Q 170 160 160 180" fill="none" stroke="#10b981" stroke-width="12" stroke-linecap="round" />
        <ellipse cx="100" cy="138" rx="48" ry="40" fill="#10b981" />
        <ellipse cx="100" cy="142" rx="32" ry="26" fill="#a7f3d0" />
        <circle cx="100" cy="90" r="44" fill="#10b981" />
        <polygon points="76,52 68,30 84,48" fill="#fbbf24" />
        <polygon points="124,52 132,30 116,48" fill="#fbbf24" />
        ${isSleeping ? `
          <path d="M 78 88 Q 86 94 90 88" fill="none" stroke="#1e293b" stroke-width="4" stroke-linecap="round" />
          <path d="M 110 88 Q 114 94 122 88" fill="none" stroke="#1e293b" stroke-width="4" stroke-linecap="round" />
        ` : `
          <circle cx="82" cy="86" r="7" fill="#1e293b" />
          <circle cx="84" cy="84" r="2.5" fill="#fff" />
          <circle cx="118" cy="86" r="7" fill="#1e293b" />
          <circle cx="120" cy="84" r="2.5" fill="#fff" />
        `}
        <ellipse cx="100" cy="100" rx="16" ry="10" fill="#a7f3d0" />
        <circle cx="95" cy="98" r="2" fill="#065f46" />
        <circle cx="105" cy="98" r="2" fill="#065f46" />
        ${hatOverlay ? `<text x="78" y="28" font-size="32">${hatOverlay}</text>` : ''}
        ${glassOverlay ? `<text x="75" y="92" font-size="28">${glassOverlay}</text>` : ''}
        ${accOverlay && equipped.accessory === 'acc_bow' ? `<text x="84" y="132" font-size="28">${accOverlay}</text>` : ''}
      </svg>
    `;
  }

  // --- 3. UI UPDATES ---
  function updateUI() {
    const mood = window.petModel.getMoodState();
    
    document.getElementById('display-pet-name').textContent = pet.name;
    document.getElementById('display-level').textContent = `Lv. ${pet.level}`;
    document.getElementById('display-coins').textContent = pet.coins;

    document.getElementById('bar-hunger').style.width = `${pet.stats.hunger}%`;
    document.getElementById('bar-joy').style.width = `${pet.stats.happiness}%`;
    document.getElementById('bar-energy').style.width = `${pet.stats.energy}%`;
    document.getElementById('bar-hygiene').style.width = `${pet.stats.hygiene}%`;
  }

  // --- 4. TOUCH COORDINATE PARTICLES & REACTION MATRIX ---
  function spawnTouchParticles(x, y, emoji) {
    const layer = document.getElementById('particle-layer');
    if (!layer) return;

    for (let i = 0; i < 4; i++) {
      const p = document.createElement('div');
      p.className = 'particle';
      p.textContent = emoji;
      const offsetX = (Math.random() - 0.5) * 60;
      const offsetY = (Math.random() - 0.5) * 30;
      p.style.left = `${x + offsetX}px`;
      p.style.top = `${y + offsetY}px`;
      layer.appendChild(p);
      setTimeout(() => p.remove(), 1600);
    }
  }

  function handlePetTap(e) {
    if (e && e.target) {
      if (e.target.closest('.pet-speech-bubble') || e.target.closest('.modal-overlay') || e.target.closest('.action-btn')) {
        return;
      }
    }

    // Touch streak calculation
    touchStreakCount++;
    if (touchStreakTimer) clearTimeout(touchStreakTimer);
    touchStreakTimer = setTimeout(() => { touchStreakCount = 0; }, 2000);

    // Get tap coordinate
    const rect = document.getElementById('pet-stage-viewport').getBoundingClientRect();
    const clickX = e && e.clientX ? e.clientX - rect.left : 180;
    const clickY = e && e.clientY ? e.clientY - rect.top : 200;

    resetInactivityTimer();
    spawnTouchParticles(clickX, clickY, touchStreakCount > 5 ? '✨' : '❤️');

    pet.stats.happiness = Math.min(100, pet.stats.happiness + 5);
    window.petModel.addCoinsAndXP(1, 4);
    window.petModel.savePet();

    if (pet.species === 'cat') {
      currentAnimStateKey = null;
      const evalRes = window.catResponseAlgorithm.evaluateCatBehavior('', pet, 'pet', { streak: touchStreakCount });
      playCatAudio(evalRes.audioCue);
      playLottieState(evalRes.stateKey, false, 2200);
      document.getElementById('pet-speech-bubble').textContent = evalRes.fullDialogue;
    } else {
      playCatAudio('purr');
      document.getElementById('pet-speech-bubble').textContent = `❤️ *Happy Purrs!* I love you!`;
    }
    updateUI();
  }

  function initUIEvents() {
    const petStage = document.getElementById('pet-stage-viewport');
    const petBox = document.getElementById('pet-character-box');
    const lottieBox = document.getElementById('lottie-pet-container');

    if (petStage) {
      petStage.onclick = handlePetTap;
    }

    if (petBox) {
      petBox.onclick = handlePetTap;
    }

    if (lottieBox) {
      lottieBox.onclick = handlePetTap;
    }

    // Laser Pointer Toy Action 🔴
    const laserBtn = document.getElementById('btn-action-laser');
    if (laserBtn) {
      laserBtn.onclick = (e) => {
        e.stopPropagation();
        resetInactivityTimer();
        triggerLaserToy();
      };
    }

    // Care Buttons
    document.getElementById('btn-action-feed').onclick = (e) => {
      e.stopPropagation();
      resetInactivityTimer();
      playCatAudio('begging');
      openShopModal('foods');
    };

    document.getElementById('btn-action-bath').onclick = (e) => {
      e.stopPropagation();
      resetInactivityTimer();
      spawnParticles('🫧');
      playCatAudio('meow');
      window.petModel.clean();
      if (pet.species === 'cat') {
        currentAnimStateKey = null;
        playLottieState('confused', false, 2200);
      }
      document.getElementById('pet-speech-bubble').textContent = `🧼 *Squeaky Clean!* Thanks master!`;
      updateUI();
    };

    document.getElementById('btn-action-sleep').onclick = (e) => {
      e.stopPropagation();
      resetInactivityTimer();
      const isSleeping = window.petModel.toggleSleep();
      playCatAudio('slow');
      currentAnimStateKey = null;
      if (pet.species === 'cat') {
        playLottieState(isSleeping ? 'sleeping' : 'hello', true);
      }
      document.getElementById('pet-speech-bubble').textContent = isSleeping ? `😴 Zzz... Goodnight master!` : `☀️ Yawn! Good morning!`;
      renderPetStage();
      updateUI();
    };

    document.getElementById('btn-action-arcade').onclick = (e) => {
      e.stopPropagation();
      resetInactivityTimer();
      playCatAudio('trill');
      currentAnimStateKey = null;
      if (pet.species === 'cat') playLottieState('dance', true);
      openArcadeModal();
    };

    document.getElementById('btn-action-shop').onclick = (e) => {
      e.stopPropagation();
      resetInactivityTimer();
      openShopModal('hats');
    };

    document.getElementById('btn-action-chat').onclick = (e) => {
      e.stopPropagation();
      resetInactivityTimer();
      playCatAudio('cute');
      currentAnimStateKey = null;
      if (pet.species === 'cat') playLottieState('surprised', false, 2200);
      openChatModal();
    };

    // Modal Close
    document.querySelectorAll('.close-btn[data-modal]').forEach(btn => {
      btn.onclick = (e) => {
        e.stopPropagation();
        const modalId = btn.getAttribute('data-modal');
        document.getElementById(modalId).classList.remove('active');
        if (modalId === 'modal-arcade') stopArcadeGame();
      };
    });

    // Pet Adoption Modal setup
    document.getElementById('btn-confirm-adopt').onclick = (e) => {
      e.stopPropagation();
      resetInactivityTimer();
      const name = document.getElementById('pet-name-input').value.trim() || 'Luna';
      const activeCard = document.querySelector('.species-card.active');
      const species = activeCard ? activeCard.getAttribute('data-species') : 'cat';
      
      pet = window.petModel.createPet(species, name);
      document.getElementById('modal-select-pet').classList.remove('active');
      currentAnimStateKey = null;
      renderPetStage();
      updateUI();
      playCatAudio('cute');
      showToast(`🐾 Adopted ${name}! Saved to cache memory.`);
    };

    document.querySelectorAll('.species-card').forEach(card => {
      card.onclick = (e) => {
        e.stopPropagation();
        document.querySelectorAll('.species-card').forEach(c => c.classList.remove('active'));
        card.classList.add('active');
      };
    });

    document.getElementById('btn-change-pet-species').onclick = (e) => {
      e.stopPropagation();
      document.getElementById('modal-settings').classList.remove('active');
      document.getElementById('modal-select-pet').classList.add('active');
    };

    // SETTINGS & CACHE MEMORY CONTROLS
    const updateCacheUI = () => {
      const usage = window.petModel.getCacheStorageUsage();
      document.getElementById('cache-coins-val').textContent = `${usage.coins} 🪙`;
      document.getElementById('cache-size-val').textContent = `${usage.kb} KB`;
      document.getElementById('cache-status-badge').textContent = usage.status;
    };

    document.getElementById('open-settings-btn').onclick = (e) => {
      e.stopPropagation();
      document.getElementById('groq-key-input').value = window.groqPetService.getApiKey();
      updateCacheUI();
      document.getElementById('modal-settings').classList.add('active');
    };

    document.getElementById('btn-save-settings').onclick = (e) => {
      e.stopPropagation();
      const key = document.getElementById('groq-key-input').value;
      window.groqPetService.setApiKey(key);
      document.getElementById('modal-settings').classList.remove('active');
      showToast('🔑 Settings saved to cache!');
    };

    document.getElementById('btn-sync-cache').onclick = async (e) => {
      e.stopPropagation();
      await window.petModel.syncToCacheApi();
      updateCacheUI();
      showToast('🔄 Cache memory re-synced!');
    };

    document.getElementById('btn-export-backup').onclick = (e) => {
      e.stopPropagation();
      window.petModel.exportBackup();
      showToast('📥 Backup JSON exported!');
    };

    const fileInput = document.getElementById('import-backup-file-input');
    document.getElementById('btn-import-backup-trigger').onclick = (e) => {
      e.stopPropagation();
      fileInput.click();
    };

    fileInput.onchange = (e) => {
      const file = e.target.files[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = (event) => {
        const success = window.petModel.importBackup(event.target.result);
        if (success) {
          updateCacheUI();
          showToast('📤 Backup restored successfully!');
          pet = window.petModel.pet;
          currentAnimStateKey = null;
          renderPetStage();
          updateUI();
          document.getElementById('modal-settings').classList.remove('active');
        } else {
          alert('Failed to import backup file. Invalid format.');
        }
      };
      reader.readAsText(file);
    };

    document.getElementById('btn-clear-cache').onclick = async (e) => {
      e.stopPropagation();
      if (confirm('Are you sure you want to clear all cached pet data?')) {
        await window.petModel.clearAllCache();
        updateCacheUI();
        showToast('🗑️ Cache cleared.');
        pet = window.petModel.pet;
        currentAnimStateKey = null;
        renderPetStage();
        updateUI();
        document.getElementById('modal-settings').classList.remove('active');
      }
    };
  }

  function triggerLaserToy() {
    const stage = document.getElementById('pet-stage-viewport');
    
    // Spawn red laser dot
    const dot = document.createElement('div');
    dot.className = 'laser-dot';
    dot.style.left = `${20 + Math.random() * 60}%`;
    dot.style.top = `${30 + Math.random() * 40}%`;
    stage.appendChild(dot);

    playCatAudio('trill');
    spawnParticles('🔴');

    if (pet.species === 'cat') {
      currentAnimStateKey = null;
      playLottieState('dance', false, 2400);
      const catRes = window.catResponseAlgorithm.evaluateCatBehavior('', pet, 'laser');
      document.getElementById('pet-speech-bubble').textContent = catRes.fullDialogue;
    } else {
      document.getElementById('pet-speech-bubble').textContent = `🔴 *Pounces wildly!* Got it!`;
    }

    pet.stats.happiness = Math.min(100, pet.stats.happiness + 20);
    window.petModel.addCoinsAndXP(5, 10);
    updateUI();

    setTimeout(() => dot.remove(), 2200);
  }

  // --- 5. SHOP & FEED MODAL ---
  function openShopModal(defaultTab = 'foods') {
    activeShopTab = defaultTab;
    renderShopGrid();

    document.querySelectorAll('#shop-tabs .level-badge').forEach(tab => {
      tab.onclick = (e) => {
        e.stopPropagation();
        document.querySelectorAll('#shop-tabs .level-badge').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        activeShopTab = tab.getAttribute('data-shop-tab');
        renderShopGrid();
      };
    });

    document.getElementById('modal-shop').classList.add('active');
  }

  function renderShopGrid() {
    const grid = document.getElementById('shop-items-grid');
    grid.innerHTML = '';

    const items = window.petModel.SHOP_ITEMS[activeShopTab] || [];

    items.forEach(item => {
      const card = document.createElement('div');
      card.className = 'shop-card';
      const isOwned = pet.inventory.includes(item.id);
      const isEquipped = item.slot && pet.equipped[item.slot] === item.id;

      card.innerHTML = `
        <div class="shop-card-icon">${item.icon}</div>
        <div class="shop-card-title">${item.name}</div>
        <div class="shop-card-cost">${isEquipped ? '✨ Equipped' : isOwned ? 'Owned' : `🪙 ${item.cost}`}</div>
      `;

      card.onclick = (e) => {
        e.stopPropagation();
        resetInactivityTimer();
        if (activeShopTab === 'foods') {
          const res = window.petModel.feed(item.id);
          if (res.error) {
            alert(res.error);
          } else {
            spawnParticles(item.icon);
            playCatAudio('hungry');

            if (pet.species === 'cat') {
              currentAnimStateKey = null;
              playLottieState('eating', false, 2400);
              const catRes = window.catResponseAlgorithm.evaluateCatBehavior('', pet, 'food');
              document.getElementById('pet-speech-bubble').textContent = catRes.fullDialogue;
            } else {
              document.getElementById('pet-speech-bubble').textContent = `🍎 Yummy! Loved the ${item.name}!`;
            }
            updateUI();
          }
        } else {
          if (isOwned) {
            window.petModel.equipItem(item);
          } else {
            const res = window.petModel.buyItem(item);
            if (res.error) alert(res.error);
          }
          currentAnimStateKey = null;
          renderPetStage();
          updateUI();
          renderShopGrid();
        }
      };

      grid.appendChild(card);
    });
  }

  // --- 6. GROQ AI PET CHAT ---
  function openChatModal() {
    document.getElementById('chat-pet-name').textContent = pet.name;
    document.getElementById('modal-chat').classList.add('active');

    document.getElementById('btn-send-pet-chat').onclick = sendPetChat;
    document.getElementById('pet-chat-input').onkeypress = (e) => {
      if (e.key === 'Enter') sendPetChat();
    };
  }

  async function sendPetChat() {
    resetInactivityTimer();
    const input = document.getElementById('pet-chat-input');
    const msg = input.value.trim();
    if (!msg) return;

    const messagesBox = document.getElementById('chat-messages-box');

    const userMsg = document.createElement('div');
    userMsg.className = 'chat-bubble user';
    userMsg.textContent = msg;
    messagesBox.appendChild(userMsg);

    input.value = '';
    messagesBox.scrollTop = messagesBox.scrollHeight;

    const petMsg = document.createElement('div');
    petMsg.className = 'chat-bubble pet';
    petMsg.textContent = '...';
    messagesBox.appendChild(petMsg);

    if (pet.species === 'cat') {
      currentAnimStateKey = null;
      playLottieState('surprised', false, 2400);
      playCatAudio('cute');
    }

    const reply = await window.groqPetService.chatWithPet(msg, pet);
    petMsg.textContent = reply;
    document.getElementById('pet-speech-bubble').textContent = reply;
    messagesBox.scrollTop = messagesBox.scrollHeight;
    spawnParticles('❤️');
  }

  // --- 7. ARCADE MINIGAME (STAR CATCHER) ---
  function openArcadeModal() {
    document.getElementById('modal-arcade').classList.add('active');
    document.getElementById('btn-start-arcade').onclick = startArcadeGame;
  }

  function startArcadeGame() {
    gameRunning = true;
    score = 0;
    earnedCoins = 0;
    items = [];
    playerX = 150;

    const canvas = document.getElementById('minigame-canvas');
    const ctx = canvas.getContext('2d');

    canvas.onmousemove = (e) => {
      const rect = canvas.getBoundingClientRect();
      playerX = e.clientX - rect.left - 25;
    };

    canvas.ontouchmove = (e) => {
      const rect = canvas.getBoundingClientRect();
      if (e.touches[0]) {
        playerX = e.touches[0].clientX - rect.left - 25;
      }
    };

    function gameLoop() {
      if (!gameRunning) return;

      ctx.clearRect(0, 0, canvas.width, canvas.height);

      if (Math.random() < 0.05) {
        items.push({
          x: Math.random() * (canvas.width - 30),
          y: 0,
          speed: 2 + Math.random() * 3,
          isStar: Math.random() > 0.3
        });
      }

      for (let i = items.length - 1; i >= 0; i--) {
        const item = items[i];
        item.y += item.speed;

        ctx.font = '22px sans-serif';
        ctx.fillText(item.isStar ? '⭐' : '🌧️', item.x, item.y);

        if (item.y > canvas.height - 50 && item.y < canvas.height - 10 && item.x > playerX - 15 && item.x < playerX + 55) {
          if (item.isStar) {
            score += 10;
            earnedCoins += 2;
            playCatAudio('trill');
          } else {
            score = Math.max(0, score - 5);
          }
          items.splice(i, 1);
          continue;
        }

        if (item.y > canvas.height) {
          items.splice(i, 1);
        }
      }

      ctx.font = '36px sans-serif';
      const avatarEmoji = pet.species === 'cat' ? '🐱' : pet.species === 'bunny' ? '🐰' : pet.species === 'dragon' ? '🐉' : '🐶';
      ctx.fillText(avatarEmoji, playerX, canvas.height - 15);

      document.getElementById('arcade-score').textContent = `Score: ${score} | Coins: +${earnedCoins}`;

      gameLoopId = requestAnimationFrame(gameLoop);
    }

    gameLoop();

    setTimeout(() => {
      stopArcadeGame();
    }, 25000);
  }

  function stopArcadeGame() {
    gameRunning = false;
    if (gameLoopId) cancelAnimationFrame(gameLoopId);

    if (earnedCoins > 0) {
      if (pet.species === 'cat') {
        currentAnimStateKey = null;
        playLottieState('laughing', false, 3000);
        playCatAudio('trill');
      }
      window.petModel.addCoinsAndXP(earnedCoins, Math.floor(score / 2));
      updateUI();
      alert(`🎉 Game Over! You earned ${earnedCoins} Coins and ${Math.floor(score / 2)} XP!`);
    }
  }

  function spawnParticles(emoji) {
    const layer = document.getElementById('particle-layer');
    if (!layer) return;
    for (let i = 0; i < 5; i++) {
      const p = document.createElement('div');
      p.className = 'particle';
      p.textContent = emoji;
      p.style.left = `${30 + Math.random() * 40}%`;
      p.style.top = `${50 + Math.random() * 20}%`;
      layer.appendChild(p);
      setTimeout(() => p.remove(), 1800);
    }
  }
});
