/**
 * Virtual Pet State Manager & Dual Cache Memory Controller
 * Persistent Storage via LocalStorage + CacheStorage API + JSON Backup Tools.
 */

class PetModel {
  constructor() {
    this.STORAGE_KEY = 'bask_virtual_pet_state';
    this.CACHE_NAME = 'bask_virtual_pet_cache_v1';
    this.pet = this.loadPet();

    // Shop Items Catalog
    this.SHOP_ITEMS = {
      foods: [
        { id: 'fish_cookie', name: 'Fish Cookie', icon: '🐟', cost: 10, hunger: 30, joy: 20 },
        { id: 'milk_saucer', name: 'Milk Saucer', icon: '🥛', cost: 15, hunger: 40, joy: 15 },
        { id: 'catnip_mouse', name: 'Catnip Mouse', icon: '🐭', cost: 20, hunger: 25, joy: 45 },
        { id: 'cupcake', name: 'Rainbow Cupcake', icon: '🧁', cost: 12, hunger: 35, joy: 15 },
        { id: 'starberry', name: 'Cosmic Starberry', icon: '🍓', cost: 25, hunger: 55, joy: 40 }
      ],
      hats: [
        { id: 'hat_party', name: 'Party Hat', icon: '🥳', cost: 30, slot: 'hat' },
        { id: 'hat_crown', name: 'Golden Crown', icon: '👑', cost: 80, slot: 'hat' },
        { id: 'hat_frog', name: 'Frog Beanie', icon: '🐸', cost: 50, slot: 'hat' },
        { id: 'hat_cat', name: 'Cat Ears', icon: '🐱', cost: 35, slot: 'hat' }
      ],
      glasses: [
        { id: 'glass_star', name: 'Star Sunglasses', icon: '⭐', cost: 40, slot: 'glasses' },
        { id: 'glass_smart', name: 'Smart Specs', icon: '👓', cost: 45, slot: 'glasses' }
      ],
      accessories: [
        { id: 'acc_bow', name: 'Red Bowtie', icon: '🎀', cost: 25, slot: 'accessory' },
        { id: 'acc_laser', name: 'Laser Toy', icon: '🔴', cost: 60, slot: 'accessory' },
        { id: 'acc_wings', name: 'Angel Wings', icon: '🪽', cost: 90, slot: 'accessory' }
      ]
    };

    this.initDecayTimer();
    this.syncToCacheApi();
  }

  getDefaultPet(species = 'cat', name = 'Luna') {
    return {
      name: name,
      species: species, // 'cat', 'shiba', 'bunny', 'dragon'
      level: 1,
      xp: 0,
      coins: 80,
      stats: {
        hunger: 80,
        happiness: 85,
        energy: 90,
        hygiene: 95
      },
      equipped: {
        hat: null,
        glasses: null,
        accessory: null
      },
      inventory: ['fish_cookie', 'milk_saucer', 'cupcake'],
      isSleeping: false,
      lastUpdate: Date.now()
    };
  }

  loadPet() {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      if (raw) {
        const data = JSON.parse(raw);
        this.applyOfflineDecay(data);
        return data;
      }
    } catch (e) {
      console.error('Error loading pet data:', e);
    }
    return this.getDefaultPet();
  }

  savePet() {
    this.pet.lastUpdate = Date.now();
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.pet));
    this.syncToCacheApi();
  }

  createPet(species, name) {
    this.pet = this.getDefaultPet(species, name);
    this.savePet();
    return this.pet;
  }

  applyOfflineDecay(petData) {
    const now = Date.now();
    const elapsedMinutes = Math.floor((now - (petData.lastUpdate || now)) / 60000);
    if (elapsedMinutes > 0) {
      const decayFactor = Math.min(elapsedMinutes, 120);
      petData.stats.hunger = Math.max(10, petData.stats.hunger - decayFactor * 0.4);
      petData.stats.happiness = Math.max(10, petData.stats.happiness - decayFactor * 0.3);
      petData.stats.hygiene = Math.max(10, petData.stats.hygiene - decayFactor * 0.25);
      if (!petData.isSleeping) {
        petData.stats.energy = Math.max(10, petData.stats.energy - decayFactor * 0.35);
      } else {
        petData.stats.energy = Math.min(100, petData.stats.energy + decayFactor * 0.8);
      }
      petData.lastUpdate = now;
    }
  }

  initDecayTimer() {
    setInterval(() => {
      if (this.pet.isSleeping) {
        this.pet.stats.energy = Math.min(100, this.pet.stats.energy + 2);
        this.pet.stats.hunger = Math.max(5, this.pet.stats.hunger - 0.2);
      } else {
        this.pet.stats.hunger = Math.max(5, this.pet.stats.hunger - 0.5);
        this.pet.stats.happiness = Math.max(5, this.pet.stats.happiness - 0.4);
        this.pet.stats.energy = Math.max(5, this.pet.stats.energy - 0.3);
        this.pet.stats.hygiene = Math.max(5, this.pet.stats.hygiene - 0.2);
      }
      this.savePet();
    }, 15000);
  }

  // --- DUAL CACHE STORAGE API ---
  async syncToCacheApi() {
    if (!('caches' in window)) return;
    try {
      const cache = await caches.open(this.CACHE_NAME);
      const jsonBlob = new Blob([JSON.stringify(this.pet)], { type: 'application/json' });
      const response = new Response(jsonBlob, {
        headers: { 'Content-Type': 'application/json', 'X-Bask-Pet': 'v1' }
      });
      await cache.put('/api/pet-state.json', response);
    } catch (e) {
      console.warn('Cache API sync note:', e);
    }
  }

  getCacheStorageUsage() {
    const raw = JSON.stringify(this.pet);
    const bytes = new Blob([raw]).size;
    const kb = (bytes / 1024).toFixed(2);
    return {
      status: 'Active 🟢',
      kb,
      level: this.pet.level,
      coins: this.pet.coins
    };
  }

  exportBackup() {
    const jsonStr = JSON.stringify(this.pet, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `bask_virtual_pet_${this.pet.name}_backup.json`;
    a.click();
    URL.revokeObjectURL(url);
  }

  importBackup(jsonText) {
    try {
      const data = JSON.parse(jsonText);
      if (data && data.name && data.stats) {
        this.pet = data;
        this.savePet();
        return true;
      }
    } catch (e) {
      console.error('Invalid backup json:', e);
    }
    return false;
  }

  async clearAllCache() {
    localStorage.removeItem(this.STORAGE_KEY);
    if ('caches' in window) {
      await caches.delete(this.CACHE_NAME);
    }
    this.pet = this.getDefaultPet();
    this.savePet();
  }

  // --- ACTIONS ---
  feed(foodId) {
    const food = this.SHOP_ITEMS.foods.find(f => f.id === foodId);
    if (!food) return false;

    const invIndex = this.pet.inventory.indexOf(foodId);
    if (invIndex !== -1) {
      this.pet.inventory.splice(invIndex, 1);
    } else {
      if (this.pet.coins < food.cost) return { error: 'Not enough coins!' };
      this.pet.coins -= food.cost;
    }

    this.pet.stats.hunger = Math.min(100, this.pet.stats.hunger + food.hunger);
    this.pet.stats.happiness = Math.min(100, this.pet.stats.happiness + food.joy);
    this.addXP(15);
    this.savePet();
    return { success: true, food };
  }

  clean() {
    this.pet.stats.hygiene = 100;
    this.pet.stats.happiness = Math.min(100, this.pet.stats.happiness + 15);
    this.addXP(10);
    this.savePet();
    return true;
  }

  toggleSleep() {
    this.pet.isSleeping = !this.pet.isSleeping;
    this.savePet();
    return this.pet.isSleeping;
  }

  addCoinsAndXP(coins, xp) {
    this.pet.coins += coins;
    this.addXP(xp);
    this.savePet();
  }

  addXP(amount) {
    this.pet.xp += amount;
    if (this.pet.xp >= 100) {
      this.pet.level += 1;
      this.pet.xp = this.pet.xp % 100;
      this.pet.coins += 50;
      return true;
    }
    return false;
  }

  equipItem(item) {
    if (this.pet.equipped[item.slot] === item.id) {
      this.pet.equipped[item.slot] = null;
    } else {
      this.pet.equipped[item.slot] = item.id;
    }
    this.savePet();
  }

  buyItem(item) {
    if (this.pet.coins < item.cost) return { error: 'Not enough coins!' };
    this.pet.coins -= item.cost;
    if (!this.pet.inventory.includes(item.id)) {
      this.pet.inventory.push(item.id);
    }
    this.equipItem(item);
    this.savePet();
    return { success: true };
  }

  getMoodState() {
    const { hunger, happiness, energy, hygiene } = this.pet.stats;
    if (this.pet.isSleeping) return { mood: 'sleeping', label: 'Sleeping 😴', emoji: '😴' };
    if (hunger < 30) return { mood: 'hungry', label: 'Hungry 🍎', emoji: '🥺' };
    if (hygiene < 30) return { mood: 'dirty', label: 'Needs Bath 🧼', emoji: '🧼' };
    if (energy < 30) return { mood: 'sleepy', label: 'Tired 🥱', emoji: '🥱' };
    if (happiness > 80) return { mood: 'ecstatic', label: 'Purring Ecstatic! ✨', emoji: '😻' };
    if (happiness > 50) return { mood: 'happy', label: 'Happy 😊', emoji: '😸' };
    return { mood: 'sad', label: 'Gloomy 😔', emoji: '😾' };
  }
}

window.petModel = new PetModel();
