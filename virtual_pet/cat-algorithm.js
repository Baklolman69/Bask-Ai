/**
 * 10x Advanced Cat Neuroscience & Behavioral Personality Engine
 * Calculates Multi-Vector Mood Dynamics, Touch Velocity, Time Phases, and Pet Bonding Levels.
 */

class CatResponseAlgorithm {
  constructor() {
    this.BOND_LEVELS = [
      { minXp: 0, title: 'Acquaintance 🐾' },
      { minXp: 50, title: 'Trusted Friend 😸' },
      { minXp: 150, title: 'Best Companion 💖' },
      { minXp: 300, title: 'Soulmate Cat 👑' }
    ];

    this.PERSONALITY_DIALOGUES = {
      EUPHORIC: [
        '✨ *Paws dancing with pure delight!* You are the absolute BEST human ever!',
        '🌟 Mrow! *Does a cute backflip* More pets please master!',
        '💖 *Loud happy purrs vibrating through the room!* I love you so much!'
      ],
      PLAYFUL: [
        '🚀 *ZOOMIES ENGAGED!* Bouncing off walls! Catch me if you can!',
        '🔴 *Eyes dilate into big shiny marbles* The red dot stands no chance!',
        '⚡ *Playful pounce!* Let\'s play in the arcade!'
      ],
      AFFECTIONATE: [
        '😻 *Slow blinks with deep affection* Prrrrr... *headbutts your palm*',
        '❤️ *Kneading dough on soft blanket* So warm and cozy with you...',
        '🐾 *Soft chin rubs* Never stop petting me human~'
      ],
      GRUMPY: [
        '😾 *Tail flicks with sassy attitude* Hmpf! Too many pets at once!',
        '🧼 Mrow! I need a warm bubble bath ASAP!',
        '🍎 *Stares at empty bowl judgingly* Where are my gourmet tuna treats?'
      ],
      SLEEPY: [
        '😴 *Yawnnnn...* Curled into a warm furry donut... Zzz...',
        '🌌 *Dreaming of swimming in a lake of catnip tea* Mmrph...',
        '🌙 Goodnight human servant... wake me at breakfast...'
      ]
    };
  }

  /**
   * 10X BEHAVIORAL CALCULATOR MATRIX
   * Evaluates Emotional Vector, Time Phases, Touch Streaks, and Bond Titles.
   */
  evaluateCatBehavior(userMessage = '', pet = {}, actionType = 'idle', touchData = null) {
    const stats = pet.stats || { hunger: 70, happiness: 70, energy: 80, hygiene: 80 };
    const xp = pet.xp || 0;
    const isSleeping = pet.isSleeping || false;

    // 1. Calculate Bond Title
    let bondTitle = 'Acquaintance 🐾';
    for (const lvl of this.BOND_LEVELS) {
      if (xp >= lvl.minXp) bondTitle = lvl.title;
    }

    // 2. Time-of-Day Detection
    const hour = new Date().getHours();
    let timePhase = 'AFTERNOON';
    if (hour >= 6 && hour < 12) timePhase = 'MORNING';
    else if (hour >= 12 && hour < 18) timePhase = 'AFTERNOON';
    else if (hour >= 18 && hour < 23) timePhase = 'EVENING';
    else timePhase = 'NIGHT';

    // 3. Multi-Vector Emotion Math
    const emotionalScore = (0.35 * stats.happiness) + (0.25 * stats.hunger) + (0.20 * stats.energy) + (0.20 * stats.hygiene);

    // 4. State Determination
    let stateKey = 'hello';
    let moodCategory = 'AFFECTIONATE';
    let audioCue = 'purr';

    if (actionType === 'pet') {
      if (touchData && touchData.streak > 8) {
        stateKey = 'confused';
        moodCategory = 'GRUMPY';
        audioCue = 'meow';
      } else {
        stateKey = 'petting';
        moodCategory = emotionalScore > 80 ? 'EUPHORIC' : 'AFFECTIONATE';
        audioCue = 'purr';
      }
    } else if (actionType === 'food') {
      stateKey = 'eating';
      moodCategory = 'EUPHORIC';
      audioCue = 'hungry';
    } else if (actionType === 'laser') {
      stateKey = 'dance';
      moodCategory = 'PLAYFUL';
      audioCue = 'trill';
    } else if (actionType === 'bath') {
      stateKey = 'confused';
      moodCategory = 'GRUMPY';
      audioCue = 'meow';
    } else if (isSleeping || (timePhase === 'NIGHT' && stats.energy < 40)) {
      stateKey = stats.energy < 20 ? 'dreaming' : 'sleeping';
      moodCategory = 'SLEEPY';
      audioCue = 'slow';
    } else if (stats.hunger < 35) {
      stateKey = 'eating';
      moodCategory = 'GRUMPY';
      audioCue = 'hungry';
    } else if (stats.hygiene < 35) {
      stateKey = 'confused';
      moodCategory = 'GRUMPY';
      audioCue = 'meow';
    } else if (emotionalScore > 85) {
      stateKey = 'laughing';
      moodCategory = 'EUPHORIC';
      audioCue = 'trill';
    } else if (timePhase === 'EVENING' && stats.happiness > 60) {
      stateKey = 'dance';
      moodCategory = 'PLAYFUL';
      audioCue = 'trill';
    } else if (timePhase === 'MORNING') {
      stateKey = 'hello';
      moodCategory = 'AFFECTIONATE';
      audioCue = 'cute';
    }

    // 5. Select Dialogue
    const options = this.PERSONALITY_DIALOGUES[moodCategory] || this.PERSONALITY_DIALOGUES.AFFECTIONATE;
    let text = options[Math.floor(Math.random() * options.length)];

    if (userMessage) {
      const msg = userMessage.toLowerCase();
      if (msg.includes('love') || msg.includes('cute')) {
        text = "💖 *Slow blinks lovingly* You are my favorite human! " + bondTitle;
        stateKey = 'petting';
        audioCue = 'purr';
      }
    }

    return {
      fullDialogue: text,
      stateKey,
      moodCategory,
      audioCue,
      emotionalScore: Math.round(emotionalScore),
      bondTitle,
      timePhase
    };
  }
}

window.catResponseAlgorithm = new CatResponseAlgorithm();
