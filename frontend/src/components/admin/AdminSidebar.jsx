import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { 
  LayoutDashboard, 
  GraduationCap, 
  Users, 
  FileSpreadsheet, 
  Settings, 
  LogOut,
  BookOpen,
  Star
} from 'lucide-react';

const sidebarItems = [
  { path: '/admin/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { path: '/admin/students', icon: GraduationCap, label: 'Students' },
  { path: '/admin/faculty', icon: Users, label: 'Faculty' },
  // { path: '/admin/projects', icon: FileSpreadsheet, label: 'Projects' },
  // { path: '/admin/settings', icon: Settings, label: 'Settings' },
];

const AdminSidebar = ({ activeTab, setActiveTab }) => {
  return (
    <motion.aside
      initial={{ x: -250 }}
      animate={{ x: 0 }}
      className="w-64 bg-white dark:bg-gray-800 shadow-lg"
    >
      <div className="p-6">
        {/* <div className="flex items-center mb-8">
          <div className="flex items-center">
            <BookOpen className="h-8 w-8 text-blue-600 dark:text-blue-400 mr-2" />
            <Star className="h-5 w-5 text-yellow-500 animate-pulse" />
          </div>
          <h1 className="ml-2 text-xl font-bold text-gray-900 dark:text-white">
            CollabBridge
          </h1>
        </div> */}

        <nav className="space-y-1">
          {sidebarItems.map((item) => {
            const isActive = item.label.toLowerCase() === activeTab;
            return (
              <motion.button
                key={item.path}
                whileHover={{ x: 5 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => setActiveTab(item.label.toLowerCase())}
                className={`flex items-center w-full px-4 py-3 rounded-lg transition-colors ${
                  isActive
                    ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400'
                    : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700'
                }`}
              >
                <item.icon className="h-5 w-5 mr-3" />
                <span>{item.label}</span>
              </motion.button>
            );
          })}
        </nav>
      </div>

      <div className="absolute bottom-0 w-full p-6">
        <motion.button
          whileHover={{ x: 5 }}
          whileTap={{ scale: 0.95 }}
          className="flex items-center w-full px-4 py-3 text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
        >
          
        </motion.button>
      </div>
    </motion.aside>
  );
};

export default AdminSidebar;