import { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import axios from 'axios';

// Create axios instance with default config
const api = axios.create({
  baseURL: 'http://localhost:8765',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  }
});

const AuthContext = createContext(undefined);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const navigate = useNavigate();

  // Set token in axios headers whenever it changes
  useEffect(() => {
    if (token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete api.defaults.headers.common['Authorization'];
    }
  }, [token]);

  const fetchUserData = async (storedToken) => {
    try {
      const response = await axios.get(`http://localhost:8765/auth/user?token=${storedToken}`);
      const userData = response.data;

      if (userData.userRole === 'STUDENT') {
        const studentResponse = await api.get(
          `/STUDENT-SERVICE/students/email/${userData.username}`
        );
        Object.assign(userData, studentResponse.data);
        userData.id = userData.studentId;
      } else if (userData.userRole === 'FACULTY') {
        const facultyResponse = await api.get(
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
      axios.get(`http://localhost:8765/auth/validate?token=${storedToken}`)
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
      const response = await axios.post('http://localhost:8765/auth/token', { 
        username, 
        password 
      });
      
      const newToken = response.data.token;
      localStorage.setItem('token', newToken);
      setToken(newToken);

      await fetchUserData(newToken);
      toast.success('Login successful!');
    } catch (error) {
      console.error('Login error:', error);
      toast.error('Login failed. Please check your credentials.');
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setToken(null);
    delete api.defaults.headers.common['Authorization'];
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

// import { createContext, useContext, useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
// import axios from 'axios';
// import { toast } from 'react-toastify';

// const AuthContext = createContext();

// export const AuthProvider = ({ children }) => {
//   const [user, setUser] = useState(null);
//   const [token, setToken] = useState(localStorage.getItem('token') || null);
//   const navigate = useNavigate();

//   // Login function
//   const login = async (username, password, role) => {
//     try {
      
//       const response = await axios.post('http://localhost:8765/auth/token', {
//         username,
//         password,
//       });

//       const newToken = response.data.token;
//       console.log("get the token bro");
//       // Store token and role in localStorage
//       localStorage.setItem('token', newToken);
//       localStorage.setItem('role', role);

//       setToken(newToken);
//       setUser({ username, role });
//       console.log(user);
//       toast.success('Login successful!');
//        console.log(role);
//       // Navigate based on stored role
//       if (role === 'faculty') {
//         console.log("Go to dashboard",role);
//         navigate('/faculty/dashboard');
//         console.log("out from faculty dashboard");
//       } else if (role === 'student') {
//         navigate('/student/dashboard');
//       }
//     } catch (error) {
//       toast.error('Login failed. Please check your credentials.');
//       console.error('Login error:', error);
//     }
//   };

//   // Validate stored token
//   const validateToken = async () => {
//     const storedToken = localStorage.getItem('token');
//     console.log('Token stored:', storedToken);
//     const storedRole = localStorage.getItem('role'); // Get role from localStorage
//     console.log('Stored role:', storedRole);
//     if (!storedToken) return false;

//     try {
//       await axios.get(`http://localhost:8765/auth/validate?token=${storedToken}`);

//       // Set user state
//       setUser({ username: 'storedUser', role: storedRole });
      
//       // Navigate based on stored role
//       if (storedRole === 'faculty') {
//         console.log("In role role",storedRole);
//         navigate('/faculty/dashboard', { replace: true });
//         console.log("out from faculty dashboard");
//       } else if (storedRole === 'student') {
//         navigate('/student/dashboard');
//       }
//       return true;
//     } catch (error) {
//       localStorage.removeItem('token');
//       localStorage.removeItem('role');
//       setToken(null);
//       setUser(null);
//       return false;
//     }
//   };

//   // Check token validity on component mount
//   // useEffect(() => {
//   //   (async () => {
//   //     console.log("DO not validate");
//   //     const isValid = await validateToken();
      
//   //   })();
//   // }, []);

//   // Logout function
//   const logout = () => {
//     localStorage.removeItem('token');
//     localStorage.removeItem('role');
//     setToken(null);
//     setUser(null);
//     navigate('/login');
//     toast.info('Logged out successfully');
//   };

//   return (
//     <AuthContext.Provider value={{ user, login, logout, token }}>
//       {children}
//     </AuthContext.Provider>
//   );
// };

// // Custom hook for using AuthContext
// export const useAuth = () => useContext(AuthContext);
