import { AnimatePresence, motion } from 'framer-motion';
import {
  BarChart3,
  Briefcase,
  ChevronRight,
  FileText,
  GraduationCap,
  LayoutDashboard,
  Settings,
  Shield,
  Sparkles,
  Users,
  X
} from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

const AdminSidebar = ({ isOpen, onClose }) => {
  const location = useLocation();

  const menuItems = [
    {
      path: '/admin/dashboard',
      icon: LayoutDashboard,
      label: 'Dashboard',
      color: 'from-blue-500 to-blue-600'
    },
    {
      path: '/admin/students',
      icon: Users,
      label: 'Students',
      color: 'from-green-500 to-green-600'
    },
    {
      path: '/admin/faculty',
      icon: GraduationCap,
      label: 'Faculty',
      color: 'from-purple-500 to-purple-600'
    },
    {
      path: '/admin/projects',
      icon: Briefcase,
      label: 'Projects',
      color: 'from-amber-500 to-amber-600'
    },
    {
      path: '/admin/analytics',
      icon: BarChart3,
      label: 'Analytics',
      color: 'from-pink-500 to-pink-600'
    },
    {
      path: '/admin/reports',
      icon: FileText,
      label: 'Reports',
      color: 'from-indigo-500 to-indigo-600'
    },
    {
      path: '/admin/settings',
      icon: Settings,
      label: 'Settings',
      color: 'from-gray-500 to-gray-600'
    }
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Mobile Overlay */}
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
        initial={{ x: -300 }}
        animate={{ x: isOpen ? 0 : -300 }}
        transition={{ type: 'spring', damping: 25, stiffness: 200 }}
        className="fixed left-0 top-0 bottom-0 w-72 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 z-50 lg:translate-x-0 lg:static overflow-y-auto"
      >
        {/* Logo Section */}
        <div className="h-16 px-6 flex items-center justify-between border-b border-gray-200 dark:border-gray-700">
          <Link to="/admin/dashboard" className="flex items-center gap-3">
            <motion.div
              whileHover={{ rotate: 360 }}
              transition={{ duration: 0.5 }}
              className="w-10 h-10 bg-gradient-to-br from-blue-600 to-purple-600 rounded-xl flex items-center justify-center shadow-lg"
            >
              <Shield className="w-6 h-6 text-white" />
            </motion.div>
            <div>
              <div className="text-lg font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                CollabBridge
              </div>
              <div className="text-xs text-gray-500 dark:text-gray-400">
                Admin Panel
              </div>
            </div>
          </Link>

          {/* Mobile Close Button */}
          <motion.button
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            onClick={onClose}
            className="lg:hidden p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          >
            <X className="w-5 h-5 text-gray-600 dark:text-gray-400" />
          </motion.button>
        </div>

        {/* Navigation Menu */}
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
                  className={`relative flex items-center gap-3 px-4 py-3 rounded-xl transition-all group ${active
                    ? 'bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 shadow-sm'
                    : 'hover:bg-gray-50 dark:hover:bg-gray-700'
                    }`}
                >
                  {/* Active Indicator */}
                  {active && (
                    <motion.div
                      layoutId="activeTab"
                      className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-gradient-to-b from-blue-600 to-purple-600 rounded-r-full"
                    />
                  )}

                  {/* Icon with Gradient Background */}
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${active
                    ? `bg-gradient-to-br ${item.color}`
                    : 'bg-gray-100 dark:bg-gray-700'
                    } transition-all group-hover:scale-110`}>
                    <Icon className={`w-5 h-5 ${active ? 'text-white' : 'text-gray-600 dark:text-gray-400'
                      }`} />
                  </div>

                  {/* Label */}
                  <span className={`flex-1 font-semibold ${active
                    ? 'text-gray-900 dark:text-white'
                    : 'text-gray-600 dark:text-gray-400'
                    }`}>
                    {item.label}
                  </span>

                  {/* Arrow Indicator */}
                  {active && (
                    <motion.div
                      initial={{ opacity: 0, x: -10 }}
                      animate={{ opacity: 1, x: 0 }}
                    >
                      <ChevronRight className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                    </motion.div>
                  )}
                </motion.div>
              </Link>
            );
          })}
        </nav>

        {/* Quick Stats Card */}
        <div className="mx-4 mb-4 mt-6">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="relative bg-gradient-to-br from-blue-600 via-purple-600 to-pink-600 rounded-2xl p-6 text-white overflow-hidden"
          >
            {/* Background Pattern */}
            <div className="absolute inset-0 opacity-10">
              <div className="absolute inset-0" style={{
                backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
              }} />
            </div>

            <div className="relative z-10">
              <div className="flex items-center gap-2 mb-3">
                <Sparkles className="w-5 h-5" />
                <span className="text-sm font-semibold">System Status</span>
              </div>
              <div className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="opacity-90">Active Users</span>
                  <span className="font-bold">1,234</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="opacity-90">Total Projects</span>
                  <span className="font-bold">456</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="opacity-90">Pending</span>
                  <span className="font-bold">23</span>
                </div>
              </div>
              <div className="mt-4 pt-4 border-t border-white/20">
                <div className="text-xs opacity-75">Last updated: Just now</div>
              </div>
            </div>
          </motion.div>
        </div>

        {/* Help Section */}
        <div className="mx-4 mb-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6 }}
            className="bg-gray-50 dark:bg-gray-700 rounded-xl p-4 border border-gray-200 dark:border-gray-600"
          >
            <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-2">
              Need Help?
            </h4>
            <p className="text-xs text-gray-600 dark:text-gray-400 mb-3">
              Check our documentation for detailed guides and tutorials.
            </p>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="w-full py-2 bg-blue-600 text-white rounded-lg text-sm font-semibold hover:bg-blue-700 transition-colors"
            >
              View Docs
            </motion.button>
          </motion.div>
        </div>
      </motion.aside>
    </>
  );
};

export default AdminSidebar;
