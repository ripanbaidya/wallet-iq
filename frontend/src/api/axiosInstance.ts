import axios, { type AxiosError } from "axios";

const ACCESS_TOKEN_KEY = "walletiq_access_token";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

// REQUEST interceptor — attach token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// RESPONSE interceptor — handle 401 globally
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Clear session and redirect to login
      localStorage.removeItem(ACCESS_TOKEN_KEY);
      localStorage.removeItem("walletiq_refresh_token");
      localStorage.removeItem("walletiq_user");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;