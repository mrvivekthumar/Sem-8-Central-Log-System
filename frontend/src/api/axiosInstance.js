// src/api/axiosInstance.js
import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'https://api-gateway-1w0w.onrender.com',
  withCredentials: true, // This must match setAllowCredentials(true) on backend
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000, // 15 second timeout for slow responses
});

// Add request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    // Add debug logging
    console.log('Request URL:', config.baseURL + config.url);
    console.log('Request method:', config.method);
    console.log('Request headers:', config.headers);
    
    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for better error handling
axiosInstance.interceptors.response.use(
  (response) => {
    console.log('Response received:', response.status);
    return response;
  },
  (error) => {
    console.error('Response error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      headers: error.response?.headers,
      url: error.config?.url
    });
    
    // Handle specific errors
    if (error.response?.status === 401) {
      console.log('Unauthorized - removing token');
      localStorage.removeItem('token');
      // Don't redirect here as it might interfere with React Router
    }
    
    if (error.response?.status === 403) {
      console.error('Forbidden - CORS or authentication issue');
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;