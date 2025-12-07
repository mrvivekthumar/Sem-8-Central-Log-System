import { AnimatePresence, motion } from 'framer-motion';
import {
  Bell,
  Briefcase,
  ChevronRight,
  FileCheck,
  LayoutDashboard,
  Sparkles,
  Trophy,
  User,
  X
} from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

const StudentSideBar = ({ isOpen, onClose }) => {
  const location = useLocation();

  const menuItems = [
    { path: '/student/dashboard', icon: LayoutDashboard, label: 'Dashboard', gradient: 'from-blue-500 to-blue-600' },
    { path: '/student/projects', icon: Briefcase, label: 'Projects', gradient: 'from-purple-500 to-purple-600' },
    { path: '/student/applied-projects', icon: FileCheck, label: 'Applied', gradient: 'from-green-500 to-green-600' },
    { path: '/student/completed-projects', icon: Trophy, label: 'Completed', gradient: 'from-amber-500 to-amber-600' },
    { path: '/student/notifications', icon: Bell, label: 'Notifications', gradient: 'from-pink-500 to-pink-600' },
    { path: '/student/profile', icon: User, label: 'Profile', gradient: 'from-indigo-500 to-indigo-600' }
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Overlay */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm z-40 lg:hidden"
          />
        )}
      </AnimatePresence>

      {/* Sidebar */}
      <motion.aside
        initial={{ x: -280 }}
        animate={{ x: isOpen ? 0 : -280 }}
        transition={{ type: 'spring', damping: 25, stiffness: 200 }}
        className="fixed left-0 top-16 bottom-0 w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 z-50 lg:translate-x-0 overflow-y-auto"
      >
        <nav className="p-4 space-y-1">
          {menuItems.map((item, index) => {
            const Icon = item.icon;
            const active = isActive(item.path);

            return (
              <Link key={item.path} to={item.path} onClick={onClose}>
                <motion.div
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                  whileHover={{ x: 4 }}
                  className={`relative flex items-center gap-3 px-4 py-3 rounded-xl transition-all group ${active
                    ? 'bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20'
                    : 'hover:bg-gray-50 dark:hover:bg-gray-700'
                    }`}
                >
                  {/* Active Bar */}
                  {active && (
                    <motion.div
                      layoutId="activeBar"
                      className="absolute left-0 w-1 h-8 bg-gradient-to-b from-blue-600 to-purple-600 rounded-r-full"
                    />
                  )}

                  {/* Icon */}
                  <div className={`w-9 h-9 rounded-lg flex items-center justify-center ${active
                    ? `bg-gradient-to-br ${item.gradient}`
                    : 'bg-gray-100 dark:bg-gray-700'
                    } group-hover:scale-110 transition-transform`}>
                    <Icon className={`w-5 h-5 ${active ? 'text-white' : 'text-gray-600 dark:text-gray-400'
                      }`} />
                  </div>

                  {/* Label */}
                  <span className={`flex-1 font-medium text-sm ${active
                    ? 'text-gray-900 dark:text-white'
                    : 'text-gray-600 dark:text-gray-400'
                    }`}>
                    {item.label}
                  </span>

                  {/* Arrow */}
                  {active && (
                    <motion.div
                      initial={{ opacity: 0, x: -10 }}
                      animate={{ opacity: 1, x: 0 }}
                    >
                      <ChevronRight className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                    </motion.div>
                  )}
                </motion.div>
              </Link>
            );
          })}
        </nav>

        {/* Stats Card */}
        <div className="mx-4 my-6">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="bg-gradient-to-br from-blue-600 via-purple-600 to-pink-600 rounded-2xl p-4 text-white relative overflow-hidden"
          >
            <div className="absolute top-2 right-2">
              <Sparkles className="w-5 h-5 opacity-50" />
            </div>
            <h3 className="text-sm font-semibold mb-3">Quick Stats</h3>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="opacity-90">Active</span>
                <span className="font-bold">2</span>
              </div>
              <div className="flex justify-between">
                <span className="opacity-90">Applied</span>
                <span className="font-bold">3</span>
              </div>
              <div className="flex justify-between">
                <span className="opacity-90">Completed</span>
                <span className="font-bold">5</span>
              </div>
            </div>
          </motion.div>
        </div>

        {/* Close Button (Mobile) */}
        <button
          onClick={onClose}
          className="lg:hidden absolute top-4 right-4 p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
        >
          <X className="w-5 h-5 text-gray-600 dark:text-gray-400" />
        </button>
      </motion.aside>
    </>
  );
};

export default StudentSideBar;
