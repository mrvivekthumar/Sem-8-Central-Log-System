// frontend/src/api/axiosInstance.js
import axios from 'axios';

// Environment configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const REQUEST_TIMEOUT = import.meta.env.VITE_API_TIMEOUT || 30000;

/**
 * Axios instance with interceptors for logging and error handling
 */
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: REQUEST_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request logger utility
 * @param {Object} config - Axios request config
 */
const logRequest = (config) => {
  const timestamp = new Date().toISOString();
  console.group(`🚀 API Request [${timestamp}]`);
  console.log('Method:', config.method?.toUpperCase());
  console.log('URL:', config.baseURL + config.url);
  console.log('Headers:', config.headers);
  if (config.data) {
    console.log('Payload:', config.data);
  }
  if (config.params) {
    console.log('Query Params:', config.params);
  }
  console.groupEnd();
};

/**
 * Response logger utility
 * @param {Object} response - Axios response object
 */
const logResponse = (response) => {
  const timestamp = new Date().toISOString();
  console.group(`✅ API Response [${timestamp}]`);
  console.log('Status:', response.status);
  console.log('URL:', response.config.url);
  console.log('Data:', response.data);
  console.log('Duration:', `${Date.now() - response.config.metadata.startTime}ms`);
  console.groupEnd();
};

/**
 * Error logger utility
 * @param {Object} error - Axios error object
 */
const logError = (error) => {
  const timestamp = new Date().toISOString();
  console.group(`❌ API Error [${timestamp}]`);
  console.log('Message:', error.message);

  if (error.response) {
    console.log('Status:', error.response.status);
    console.log('URL:', error.config?.url);
    console.log('Error Data:', error.response.data);
  } else if (error.request) {
    console.log('No Response Received');
    console.log('Request:', error.request);
  }

  if (error.config?.metadata) {
    console.log('Duration:', `${Date.now() - error.config.metadata.startTime}ms`);
  }
  console.groupEnd();
};

// Request Interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    // Add metadata for tracking
    config.metadata = { startTime: Date.now() };

    // Add auth token if available
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Log request in development
    if (import.meta.env.DEV) {
      logRequest(config);
    }

    return config;
  },
  (error) => {
    logError(error);
    return Promise.reject(error);
  }
);

// Response Interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    // Log response in development
    if (import.meta.env.DEV) {
      logResponse(response);
    }

    return response;
  },
  async (error) => {
    logError(error);

    const originalRequest = error.config;

    // Handle 401 Unauthorized - Token expired
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');

        if (refreshToken) {
          const response = await axios.post(
            `${API_BASE_URL}/api/auth/refresh`,
            { refreshToken }
          );

          const { accessToken } = response.data;
          localStorage.setItem('accessToken', accessToken);

          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return axiosInstance(originalRequest);
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    // Handle 403 Forbidden
    if (error.response?.status === 403) {
      console.error('Access denied. Insufficient permissions.');
    }

    // Handle 500 Internal Server Error
    if (error.response?.status >= 500) {
      console.error('Server error occurred. Please try again later.');
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
