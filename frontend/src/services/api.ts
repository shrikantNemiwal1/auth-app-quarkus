// src/services/api.ts
const BASE_URL = "/api";

async function apiCall(url: string, options: RequestInit = {}) {
  const response = await fetch(url, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok || data.success === false) {
    // ❌ Don't redirect here
    throw new Error(data.message || `Request failed with ${response.status}`);
  }

  return data;
}

const api = {
  login: async (email: string, password: string) => {
    const data = await apiCall(`${BASE_URL}/auth/login`, {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });

    return {
      userId: data.userId,
      email,
      emailVerified: data.emailVerified,
    };
  },

  signup: async (email: string, password: string) => {
    return apiCall(`${BASE_URL}/users`, {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
  },

  logout: async () => {
    return apiCall(`${BASE_URL}/auth/logout`, { method: "POST" });
  },

  getSession: async () => {
    const data = await apiCall(`${BASE_URL}/auth/session`);
    if (!data.authenticated) {
      throw new Error("Not authenticated");
    }
    return data.user;
  },

  verifyEmail: async (token: string) => {
    return apiCall(`${BASE_URL}/users/verify?token=${token}`);
  },

  getDashboard: async () => {
    return apiCall(`${BASE_URL}/dashboard`);
  },
};

export default api;
