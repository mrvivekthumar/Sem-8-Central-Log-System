import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import {
  Users,
  GraduationCap,
  Upload,
  Search,
  UserPlus,
  FileSpreadsheet,
  Clock
} from 'lucide-react';
import toast from 'react-hot-toast';
import AdminSidebar from '../components/admin/AdminSidebar';
import AdminHeader from '../components/admin/AdminHeader';
import StatsCard from '../components/admin/StatsCard';
import UserTable from '../components/admin/UserTable';
import RegisterModal from '../components/admin/RegisterModal';
import UploadExcelModal from '../components/admin/UploadExcelModal';
import UpdatePasswordModal from '../components/admin/UpdatePasswordModal';
import axios from 'axios';

const mockStats = {
  totalStudents: 1250,
  totalFaculty: 85,
  activeProjects: 42,
  pendingApprovals: 18
};

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('students');
  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
  const [isUploadModalOpen, setIsUploadModalOpen] = useState(false);
  const [studentCount, setStudentCount] = useState(0);
  const [facultyCount, setFacultyCount] = useState(0);
  const [projectsCount,setProjectCount]=useState(0);
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const [responseStudent, responseFaculty,responseProject] = await Promise.all([
          axios.get("http://localhost:8765/STUDENT-SERVICE/students/count"),
          axios.get("http://localhost:8765/FACULTY-SERVICE/api/faculty/count"),
          axios.get("http://localhost:8765/FACULTY-SERVICE/api/faculty/approvedProject")
        ]);

        setStudentCount(responseStudent.data);
        setFacultyCount(responseFaculty.data);
        setProjectCount(responseProject.data);
      } catch (error) {
        console.error("Error fetching counts:", error);
      }
    };

    fetchCounts();
  }, []);

  const handleOpenPasswordModal = (user) => {
    setSelectedUser(user);
    setIsPasswordModalOpen(true);
  };

  const handleRegisterUser = (data) => {
    // Add API call here
    toast.success(`${data.role} registered successfully!`);
    setIsRegisterModalOpen(false);
  };

  const handleUploadExcel = (file, role) => {
    // Add API call here
    toast.success(`${role} data uploaded successfully!`);
    setIsUploadModalOpen(false);
  };

  const handleUpdatePassword = (userId, newPassword) => {
    // Add API call here
    toast.success('Password updated successfully!');
    setIsPasswordModalOpen(false);
  };

  const handleDeleteUser = (userId) => {
    // Add API call here
    toast.success('User deleted successfully!');
  };

  return (
    // Root container: full viewport, horizontal overflow hidden.
    <div className="flex h-screen w-screen overflow-x-hidden bg-gray-50 dark:bg-gray-900">
      <AdminSidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      <div className="flex-1 flex flex-col overflow-hidden">
      

        {/* Main area: allow vertical scrolling but hide the scrollbar */}
        <main className="flex-1 p-6 overflow-y-auto overflow-x-hidden no-scrollbar">
          <div className="max-w-full">
            <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              className="mb-8"
            >
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                Admin Dashboard
              </h1>
              <p className="text-gray-600 dark:text-gray-300 mt-1">
                Manage students and faculty members
              </p>
            </motion.div>

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <StatsCard 
                title="Total Students" 
                value={studentCount} 
                icon={GraduationCap} 
                color="blue" 
              />
              <StatsCard 
                title="Total Faculty" 
                value={facultyCount} 
                icon={Users} 
                color="purple" 
              />
              <StatsCard 
                title="Active Projects" 
                value={projectsCount} 
                icon={FileSpreadsheet} 
                color="green" 
              />
              {/* <StatsCard 
                title="Pending Approvals" 
                value={mockStats.pendingApprovals} 
                icon={Clock} 
                color="amber" 
              /> */}
            </div>

            {/* Action Buttons */}
            <div className="flex flex-wrap gap-4 mb-8">
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={() => setIsRegisterModalOpen(true)}
                className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <UserPlus className="w-5 h-5 mr-2" />
                Register New User
              </motion.button>
              
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={() => setIsUploadModalOpen(true)}
                className="flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
              >
                <Upload className="w-5 h-5 mr-2" />
                Upload Excel
              </motion.button>
            </div>

            {/* Search and Filter */}
            <div className="mb-6">
              <div className="relative max-w-md">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder={`Search ${activeTab}...`}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            {/* Tabs */}
            <div className="mb-6 border-b border-gray-200 dark:border-gray-700">
              <nav className="-mb-px flex space-x-8">
                {['students', 'faculty'].map((tab) => (
                  <button
                    key={tab}
                    onClick={() => setActiveTab(tab)}
                    className={`${
                      activeTab === tab
                        ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                        : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 hover:border-gray-300'
                    } whitespace-nowrap py-4 px-1 border-b-2 font-medium capitalize transition-colors`}
                  >
                    {tab}
                  </button>
                ))}
              </nav>
            </div>

            {/* User Table */}
            <div className="w-full overflow-x-hidden">
              <UserTable 
                userType={activeTab}
                searchQuery={searchQuery}
                onUpdatePassword={handleOpenPasswordModal}
                onDeleteUser={handleDeleteUser}
              />
            </div>
          </div>
        </main>
      </div>

      {/* Modals */}
      {isRegisterModalOpen && (
        <RegisterModal 
          isOpen={isRegisterModalOpen} 
          onClose={() => setIsRegisterModalOpen(false)}
          onSubmit={handleRegisterUser}
        />
      )}

      {isUploadModalOpen && (
        <UploadExcelModal 
          isOpen={isUploadModalOpen} 
          onClose={() => setIsUploadModalOpen(false)}
          onUpload={handleUploadExcel}
        />
      )}

      {isPasswordModalOpen && selectedUser && (
        <UpdatePasswordModal 
          isOpen={isPasswordModalOpen} 
          onClose={() => setIsPasswordModalOpen(false)}
          user={selectedUser}
          onUpdate={handleUpdatePassword}
        />
      )}
    </div>
  );
};

export default AdminDashboard;

