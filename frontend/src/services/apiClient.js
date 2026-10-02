import axios from "axios";

const apiClient = axios.create({
  baseURL:
    import.meta.env.VITE_API_BASE_URL ||
    (import.meta.env.DEV
      ? "http://localhost:8080"
      : "https://devhire-production-1553.up.railway.app"),
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("devhire_token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

apiClient.interceptors.response.use(
  (response) => response,

  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("devhire_token");

      window.location.href = "/login";
    }

    return Promise.reject(error);
  },
);

export default apiClient;
