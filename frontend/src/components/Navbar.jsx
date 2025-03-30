import { Sun, Moon, Bell, LogOut } from 'lucide-react';
import { motion } from 'framer-motion';
import { useTheme } from '../contexts/ThemeContext';
import { useAuth } from '../contexts/AuthContext.jsx';
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';

const Navbar = () => {
  const { theme, toggleTheme } = useTheme();
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotificationPopup, setShowNotificationPopup] = useState(false);
  const [latestNotification, setLatestNotification] = useState(null);

  const handleProfileClick = () => {
    if (user && user.id && user.userRole === 'STUDENT') {
      navigate(`/user/profile/${user.id}`);
    }
  };
  
  const handleNotification = () => {
    navigate('/student/notifications');
    // Reset unread count when visiting notifications page
    setUnreadCount(0);
    // Mark notifications as read in the backend
    if (user && user.id) {
      markNotificationsAsRead(user.id);
    }
  };

  // Function to mark notifications as read in the backend
  const markNotificationsAsRead = async (userId) => {
    try {
      // API call to mark notifications as read
      // await fetch(`/api/notifications/${userId}/mark-read`, {
      //   method: 'PUT',
      //   headers: {
      //     'Content-Type': 'application/json',
      //   }
      // });
    } catch (error) {
      console.error("Error marking notifications as read:", error);
    }
  };

  // Fetch initial notifications
  useEffect(() => {
    const fetchNotifications = async () => {
      if (!user || !user.id) return;
      
      try {
        // Replace with your actual API endpoint
        // const response = await fetch(`/api/notifications/${user.id}`);
        // const data = await response.json();
        // setUnreadCount(data.filter(notif => !notif.read).length);
      } catch (error) {
        console.error("Error fetching notifications:", error);
      }
    };

    fetchNotifications();
  }, [user]);

  // Setup WebSocket for real-time notifications
  useEffect(() => {
    if (!user || !user.id) return;

    // Replace with your actual WebSocket implementation
    // const socket = new WebSocket(`ws://your-server/notifications?userId=${user.id}`);
    
    // socket.onmessage = (event) => {
    //   const newNotification = JSON.parse(event.data);
    //   setLatestNotification(newNotification);
    //   setUnreadCount(prev => prev + 1);
    //   setShowNotificationPopup(true);
    //   
    //   // Auto-hide popup after 5 seconds
    //   setTimeout(() => setShowNotificationPopup(false), 5000);
    // };
    
    // Simulate a notification for demonstration (remove this in production)
    // const simulateNotification = setTimeout(() => {
    //   const mockNotification = {
    //     id: 'mock-id',
    //     message: 'This is a sample notification',
    //     timestamp: new Date().toISOString(),
    //     read: false
    //   };
    //   setLatestNotification(mockNotification);
    //   setUnreadCount(prev => prev + 1);
    //   setShowNotificationPopup(true);
      
    //   setTimeout(() => setShowNotificationPopup(false), 5000);
    // }, 3000);

    // return () => {
    //   // socket.close();
    //   clearTimeout(simulateNotification);
    // };
  }, [user]);

  return (
    <nav className="fixed top-0 w-full bg-white dark:bg-gray-800 shadow-md z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <motion.span
              className="text-xl font-bold text-blue-600 dark:text-blue-400"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.5 }}
            >
              CollabBridge
            </motion.span>
          </div>

          <div className="flex items-center space-x-4">
            {/* <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.95 }}
              onClick={toggleTheme}
              className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
            >
              {theme === 'light' ? <Moon className="h-5 w-5" /> : <Sun className="h-5 w-5" />}
            </motion.button> */}

            <div className="relative">
              <motion.button
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.95 }}
                className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
                onClick={handleNotification}
              >
                {/* <Bell className="h-5 w-5" /> */}
                {unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                    {unreadCount > 9 ? '9+' : unreadCount}
                  </span>
                )}
              </motion.button>
              
              {/* Notification popup
              {showNotificationPopup && latestNotification && (
                <div className="absolute right-0 mt-2 w-64 bg-white dark:bg-gray-700 rounded-md shadow-lg overflow-hidden z-50 border border-gray-200 dark:border-gray-600">
                  <div className="p-3">
                    <div className="flex items-center justify-between">
                      <h3 className="text-sm font-medium text-gray-900 dark:text-white">New Notification</h3>
                      <button 
                        onClick={() => setShowNotificationPopup(false)}
                        className="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300"
                      >
                        &times;
                      </button>
                    </div>
                    <p className="mt-1 text-sm text-gray-500 dark:text-gray-300">
                      {latestNotification.message}
                    </p>
                    <motion.button
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                      onClick={handleNotification}
                      className="mt-2 w-full px-3 py-1 bg-blue-500 text-white text-sm font-medium rounded-md hover:bg-blue-600"
                    >
                      View all notifications
                    </motion.button>
                  </div>
                </div>
              )} */}
            </div>

            <div className="flex items-center space-x-3">
              <div
                className={`w-10 h-10 flex items-center justify-center rounded-full bg-blue-500 text-white font-semibold text-lg ${
                  user?.userRole === 'STUDENT' ? 'cursor-pointer' : 'cursor-default'
                }`}
                onClick={user?.userRole === 'STUDENT' ? handleProfileClick : undefined}
              >
                {user?.name?.charAt(0).toUpperCase()}
              </div>

              <motion.button
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.95 }}
                onClick={logout}
                className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
              >
                <LogOut className="h-5 w-5" />
              </motion.button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;