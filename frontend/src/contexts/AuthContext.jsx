import { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import axios from 'axios';
import axiosInstance from '../api/axiosInstance'

const AuthContext = createContext(undefined);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const navigate = useNavigate();

  // Set token in axios headers whenever it changes
  useEffect(() => {
    if (token) {
      axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axiosInstance.defaults.headers.common['Authorization'];
    }
  }, [token]);

  const fetchUserData = async (storedToken) => {
    try {
      const response = await axiosInstance.get(`/auth/user?token=${storedToken}`);
      const userData = response.data;

      if (userData.userRole === 'STUDENT') {
        const studentResponse = await axiosInstance.get(
          `/STUDENT-SERVICE/students/email/${userData.username}`
        );
        Object.assign(userData, studentResponse.data);
        userData.id = userData.studentId;
      } else if (userData.userRole === 'FACULTY') {
        const facultyResponse = await axiosInstance.get(
          `/FACULTY-SERVICE/api/faculty/email/${userData.username}`
        );
        Object.assign(userData, facultyResponse.data);
        userData.id = userData.f_id;
      }

      setUser(userData);
      navigate(
        userData.userRole === 'STUDENT'
          ? '/student/dashboard'
          : userData.userRole === 'FACULTY'
            ? '/faculty/dashboard'
            : '/admin/dashboard',
        { replace: true }
      );

    } catch (error) {
      console.error('Failed to fetch user data:', error);
      logout();
    }
  };

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      axiosInstance.get(`/auth/validate?token=${storedToken}`)
        .then(() => {
          setToken(storedToken);
          fetchUserData(storedToken);
        })
        .catch(() => {
          logout();
        });
    }
  }, []);

  const login = async (username, password) => {
    try {
      const response = await axiosInstance.post('/auth/token',
        { username, password },
        {
          headers: {
            'Content-Type': 'application/json',
          }
        }
      );

      const newToken = response.data.token;
      localStorage.setItem('token', newToken);
      setToken(newToken);

      await fetchUserData(newToken);
      toast.success('Login successful!');
    } catch (error) {
      console.error('Login error:', error);
      if (error.response?.status === 403) {
        toast.error('Access forbidden. Please check your credentials.');
      } else if (error.response?.status === 401) {
        toast.error('Invalid credentials. Please try again.');
      } else {
        toast.error('Login failed. Please try again later.');
      }
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setToken(null);
    delete axiosInstance.defaults.headers.common['Authorization'];
    navigate('/login');
    toast.success('Logged out successfully');
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      isAuthenticated: !!token,
      login,
      logout
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
