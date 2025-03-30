import { useState } from 'react';
import { motion } from 'framer-motion';
import { Bell, Sun, Moon, User } from 'lucide-react';
import { useTheme } from '../../contexts/ThemeContext';

const AdminHeader = () => {
  const { theme, toggleTheme } = useTheme();
  const [notificationCount] = useState(3);

  return (
    <header className="bg-white dark:bg-gray-800 shadow-sm z-10">
      <div className="flex items-center justify-end h-16 px-6">
        <div className="flex items-center space-x-4">
          <motion.div
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            className="relative"
          >
            <button className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700">
              <Bell className="h-5 w-5 text-gray-600 dark:text-gray-300" />
              {notificationCount > 0 && (
                <span className="absolute top-0 right-0 h-4 w-4 bg-red-500 rounded-full text-xs text-white flex items-center justify-center">
                  {notificationCount}
                </span>
              )}
            </button>
          </motion.div>

          <motion.button
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            onClick={toggleTheme}
            className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
          >
            {theme === 'light' ? (
              <Moon className="h-5 w-5 text-gray-600" />
            ) : (
              <Sun className="h-5 w-5 text-gray-300" />
            )}
          </motion.button>

          <div className="flex items-center space-x-3">
            <div className="flex flex-col items-end">
              <span className="text-sm font-medium text-gray-900 dark:text-white">
                Admin User
              </span>
              <span className="text-xs text-gray-500 dark:text-gray-400">
                admin@collabbridge.com
              </span>
            </div>
            <div className="h-8 w-8 rounded-full bg-blue-600 flex items-center justify-center text-white">
              <User className="h-4 w-4" />
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default AdminHeader;