import { NavLink } from 'react-router-dom';
import { motion } from 'framer-motion';
import { LayoutDashboard, CheckSquare, MessageSquare ,FolderKanban,CheckCircle ,  } from 'lucide-react';

const sidebarItems = [
  { path: '/student/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { path: '/current-project', icon: FolderKanban , label: 'Current Project' },
  { path: '/applied-projects', icon: CheckSquare, label: 'Applied Projects' },
  { path: '/completed-projects',icon: CheckCircle , label: 'Completed Projects' }
  // { path: '/submit', icon: CheckCircle  , label: 'Feedback' }

  // { path: '/feedback', icon: MessageSquare, label: 'Feedback' },
];

const StudentSidebar = () => {
  return (
    <motion.aside
      initial={{ x: -250 }}
      animate={{ x: 0 }}
      className="w-64 min-h-screen bg-white dark:bg-gray-800 shadow-lg pt-20"
    >
      <nav className="mt-8">
        {sidebarItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center px-6 py-3 text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors ${
                isActive ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400' : ''
              }`
            }
          >
            <item.icon className="h-5 w-5 mr-3" />
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>
    </motion.aside>
  );
};

export default StudentSidebar;