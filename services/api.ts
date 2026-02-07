// ВНИМАНИЕ: Замени эту ссылку на свою из Render (без слэша в конце!)
const API_BASE_URL = 'https://aaaaaaaaaaaaaaaaaaa-rzrb.onrender.com;

// Мы оставляем MOCK_ROOMS только как аварийный запас
const MOCK_ROOMS = [
  {
    id: 1,
    title: "Luxury Beachfront Apartment",
    description: "Stunning view of the ocean with all modern amenities. Perfect for a relaxing getaway.",
    city: "Nice",
    pricePerNight: 120,
    maxGuests: 4,
    imageUrl: "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80",
    ownerName: "John Host"
  }
];

export const api = {
  async get(endpoint: string) {
    try {
      const token = localStorage.getItem('token');
      // endpoint обычно начинается со слэша, например /api/rooms
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        headers: {
          'Authorization': token ? `Bearer ${token}` : '',
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) throw new Error('Fetch error');
      return await response.json();
    } catch (err) {
      console.warn(`Backend unreachable at ${endpoint}, using mock data.`, err);
      return this.handleMockGet(endpoint);
    }
  },

  async post(endpoint: string, data: any) {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: {
          'Authorization': token ? `Bearer ${token}` : '',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Post error');
      }
      return await response.json();
    } catch (err: any) {
      // Если это реальная ошибка сервера (например, Email taken), пробрасываем её в UI
      if (err.message && (err.message.includes('Email') || err.message.includes('credentials'))) {
        throw err;
      }
      
      console.warn(`Backend unreachable at ${endpoint}, simulating action.`);
      return this.handleMockPost(endpoint, data);
    }
  },

  // Эти методы остаются для того, чтобы сайт не "падал", если интернет пропадет
  handleMockGet(endpoint: string) {
    if (endpoint.includes('/rooms')) {
      return MOCK_ROOMS;
    }
    return [];
  },

  handleMockPost(endpoint: string, data: any) {
    if (endpoint.includes('/auth/login')) {
      return { token: 'mock-token', user: { name: 'Guest', email: data.email } };
    }
    return { success: true };
  }
};
