import { Outlet } from 'react-router-dom';
import { motion } from 'framer-motion';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import { useAuth } from '../contexts/AuthContext';
import StudentSidebar from './StudentSideBar';


const Layout = () => {
  const {user}=useAuth();
  console.log("I am the fucking user bro",user)
  return (
   
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      <div className="flex">
        
      {user?.userRole === 'FACULTY' ? <Sidebar /> : <StudentSidebar />}
        <motion.main 
          className="flex-1 p-8 mt-16"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: 20 }}
          transition={{ duration: 0.3 }}
        >
          <Outlet />
        </motion.main>
      </div>
    </div>
  );
};

export default Layout;