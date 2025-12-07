import { motion } from 'framer-motion';
import {
  Bell,
  Briefcase, FileText,
  Home,
  Settings,
  Trophy,
  User,
  X
} from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Sidebar = ({ isOpen, onClose }) => {
  const location = useLocation();
  const { user } = useAuth();

  const menuItems = [
    { path: '/student/dashboard', icon: Home, label: 'Dashboard', color: 'from-blue-500 to-blue-600' },
    { path: '/student/projects', icon: Briefcase, label: 'Browse Projects', color: 'from-purple-500 to-purple-600' },
    { path: '/student/applied-projects', icon: FileText, label: 'My Applications', color: 'from-green-500 to-green-600' },
    { path: '/student/completed-projects', icon: Trophy, label: 'Completed', color: 'from-amber-500 to-amber-600' },
    { path: '/student/notifications', icon: Bell, label: 'Notifications', color: 'from-pink-500 to-pink-600' },
    { path: '/student/profile', icon: User, label: 'Profile', color: 'from-indigo-500 to-indigo-600' },
    { path: '/student/settings', icon: Settings, label: 'Settings', color: 'from-gray-500 to-gray-600' }
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Mobile Overlay */}
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={onClose}
          className="fixed inset-0 bg-black/50 backdrop-blur-sm z-40 lg:hidden"
        />
      )}

      {/* Sidebar */}
      <motion.aside
        initial={{ x: -300 }}
        animate={{ x: isOpen ? 0 : -300 }}
        transition={{ type: 'spring', damping: 25, stiffness: 200 }}
        className="fixed left-0 top-16 bottom-0 w-72 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 z-40 lg:translate-x-0 overflow-y-auto"
      >
        {/* User Info Card */}
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-lg">
              {user?.name?.charAt(0) || 'S'}
            </div>
            <div className="flex-1 min-w-0">
              <div className="text-sm font-semibold text-gray-900 dark:text-white truncate">
                {user?.name || 'Student'}
              </div>
              <div className="text-xs text-gray-500 dark:text-gray-400">
                {user?.username || 'student@example.com'}
              </div>
            </div>
            <button
              onClick={onClose}
              className="lg:hidden p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Navigation */}
        <nav className="p-4 space-y-2">
          {menuItems.map((item, index) => {
            const Icon = item.icon;
            const active = isActive(item.path);

            return (
              <Link key={item.path} to={item.path}>
                <motion.div
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                  whileHover={{ x: 4 }}
                  className={`relative flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${active
                    ? 'bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 shadow-sm'
                    : 'hover:bg-gray-50 dark:hover:bg-gray-700'
                    }`}
                >
                  {active && (
                    <motion.div
                      layoutId="activeSidebar"
                      className="absolute left-0 w-1 h-8 bg-gradient-to-b from-blue-600 to-purple-600 rounded-r-full"
                    />
                  )}

                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${active ? `bg-gradient-to-br ${item.color}` : 'bg-gray-100 dark:bg-gray-700'
                    }`}>
                    <Icon className={`w-5 h-5 ${active ? 'text-white' : 'text-gray-600 dark:text-gray-400'}`} />
                  </div>

                  <span className={`flex-1 font-medium ${active ? 'text-gray-900 dark:text-white' : 'text-gray-600 dark:text-gray-400'
                    }`}>
                    {item.label}
                  </span>
                </motion.div>
              </Link>
            );
          })}
        </nav>

        {/* Quick Stats */}
        <div className="mx-4 mb-4 mt-6">
          <div className="bg-gradient-to-br from-blue-600 to-purple-600 rounded-2xl p-4 text-white">
            <h3 className="text-sm font-semibold mb-3">Your Progress</h3>
            <div className="space-y-2">
              <div className="flex items-center justify-between text-xs">
                <span className="opacity-90">Active Projects</span>
                <span className="font-bold">2</span>
              </div>
              <div className="flex items-center justify-between text-xs">
                <span className="opacity-90">Completed</span>
                <span className="font-bold">5</span>
              </div>
              <div className="flex items-center justify-between text-xs">
                <span className="opacity-90">Applied</span>
                <span className="font-bold">3</span>
              </div>
            </div>
          </div>
        </div>
      </motion.aside>
    </>
  );
};

export default Sidebar;
