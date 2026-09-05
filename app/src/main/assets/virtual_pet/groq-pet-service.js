/**
 * Groq AI Pet Service for Bask Virtual Pet Companion
 * Integrates Cat Persona Algorithm and dynamic pet dialogue.
 */

class GroqPetService {
  constructor() {
    this.apiUrl = 'https://api.groq.com/openai/v1/chat/completions';
    this.defaultModel = 'llama-3.3-70b-versatile';
    this.fastModel = 'llama-3.1-8b-instant';
  }

  getApiKey() {
    return localStorage.getItem('groq_api_key') || '';
  }

  setApiKey(key) {
    localStorage.setItem('groq_api_key', key.trim());
  }

  hasValidKey() {
    return this.getApiKey().length > 10;
  }

  async fetchGroqCompletion(messages, temperature = 0.8) {
    const apiKey = this.getApiKey();
    if (!apiKey) throw new Error('GROQ_KEY_MISSING');

    const response = await fetch(this.apiUrl, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${apiKey}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        model: this.fastModel,
        messages: messages,
        temperature: temperature,
        max_tokens: 150
      })
    });

    if (!response.ok) {
      const err = await response.json().catch(() => ({}));
      throw new Error(err.error?.message || `Groq API Error ${response.status}`);
    }

    const data = await response.json();
    return data.choices[0]?.message?.content || '';
  }

  async chatWithPet(userMessage, petState) {
    // Run Cat Response Algorithm if species is cat
    if (petState.species === 'cat' && window.catResponseAlgorithm) {
      const catEval = window.catResponseAlgorithm.evaluateCatResponse(userMessage, petState);
      if (!this.hasValidKey()) {
        return catEval.fullDialogue;
      }
    }

    if (!this.hasValidKey()) {
      return this.getMockPetResponse(userMessage, petState);
    }

    const speciesPersonas = {
      cat: "You are Luna, a sassy, deeply affectionate, purring Kawaii Cat virtual pet. You speak with endearing meows ('Prrrr...', 'Mew!', '*wiggles tail*', '*slow blinks*'). You treat your owner as your beloved human servant.",
      shiba: "You are Nova, an adorable, enthusiastic, loyal Shiba Inu virtual pet. You speak in cute short sentences, occasionally saying 'Woof!', 'Tail wag!', or 'Arf!'. You deeply love your owner.",
      bunny: "You are Mochi, a sweet, gentle, fluffy Kawaii Bunny virtual pet. You speak softly, wiggling your nose, saying 'Hop hop!', '*twitches ears*', and asking for cuddles.",
      dragon: "You are Spark, a tiny, proud, playful baby Cosmic Dragon virtual pet. You speak with cheerful excitement, puffing tiny harmless embers, saying 'Rawr!', '*puffs cute flame*', and boasting about getting stronger."
    };

    const persona = speciesPersonas[petState.species] || speciesPersonas.cat;

    const systemPrompt = `${persona}
Your current stats: Hunger: ${petState.stats.hunger}%, Joy: ${petState.stats.happiness}%, Energy: ${petState.stats.energy}%.
Respond to your owner in 1 to 2 short, adorable, endearing sentences. Never break character.`;

    try {
      const reply = await this.fetchGroqCompletion([
        { role: 'system', content: systemPrompt },
        { role: 'user', content: userMessage }
      ]);
      return reply.trim();
    } catch (e) {
      return this.getMockPetResponse(userMessage, petState);
    }
  }

  getMockPetResponse(userMsg, petState) {
    const species = petState.species || 'cat';
    
    if (species === 'cat' && window.catResponseAlgorithm) {
      return window.catResponseAlgorithm.evaluateCatResponse(userMsg, petState).fullDialogue;
    }

    const msg = userMsg.toLowerCase();

    if (species === 'bunny') {
      if (msg.includes('love') || msg.includes('cute')) return "*twitches nose happily* Hop hop! I love you so much master! 💕";
      if (msg.includes('food') || msg.includes('hungry')) return "*wiggles ears* Mmm! Can I have a yummy cupcake or starberry please? 🧁";
      return "Hop hop! *snuggles close* I'm so happy to be your virtual bunny companion! ✨";
    } else if (species === 'dragon') {
      if (msg.includes('love') || msg.includes('proud')) return "*puffs a tiny cute heart ember* Rawr! You are the best dragon trainer ever! 🔥❤️";
      if (msg.includes('food') || msg.includes('play')) return "Rawr! Let's play Catch the Star in the Arcade and get more coins! 🌟";
      return "*puffs tiny smoke ring* Rawr! Spark is getting stronger every single level up!";
    } else {
      if (msg.includes('love') || msg.includes('good boy')) return "*happy tail wag* Woof woof! I love you more than anything in the world! 🐾❤️";
      if (msg.includes('play') || msg.includes('walk')) return "Arf arf! *grabs favorite tennis ball* Let's go play minigames!";
      return "Woof! *pant pant* Thanks for taking such great care of me today master!";
    }
  }
}

window.groqPetService = new GroqPetService();
