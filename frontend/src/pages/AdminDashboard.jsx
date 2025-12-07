import { AnimatePresence, motion } from 'framer-motion';
import {
  Briefcase,
  Download,
  FileSpreadsheet,
  Filter,
  GraduationCap,
  RefreshCw,
  Search,
  Settings,
  TrendingUp,
  UserPlus,
  Users
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import axiosInstance from '../api/axiosInstance';
import AdminHeader from '../components/admin/AdminHeader';
import AdminSidebar from '../components/admin/AdminSidebar';
import RegisterModal from '../components/admin/RegisterModal';
import StatsCard from '../components/admin/StatsCard';
import UpdatePasswordModal from '../components/admin/UpdatePasswordModal';
import UploadExcelModal from '../components/admin/UploadExcelModal';
import UserTable from '../components/admin/UserTable';
import { useAuth } from '../contexts/AuthContext';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [stats, setStats] = useState({
    totalStudents: 0,
    totalFaculty: 0,
    activeProjects: 0,
    completedProjects: 0
  });
  const [loading, setLoading] = useState(true);
  const [showRegisterModal, setShowRegisterModal] = useState(false);
  const [showUploadModal, setShowUploadModal] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      setLoading(true);
      const [studentsRes, facultyRes] = await Promise.all([
        axiosInstance.get('/STUDENT-SERVICE/students/all'),
        axiosInstance.get('/FACULTY-SERVICE/api/faculty/all')
      ]);

      setStats({
        totalStudents: studentsRes.data.length,
        totalFaculty: facultyRes.data.length,
        activeProjects: 145, // Mock data
        completedProjects: 89  // Mock data
      });
    } catch (error) {
      console.error('Error fetching stats:', error);
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    await fetchDashboardStats();
    setTimeout(() => setRefreshing(false), 1000);
    toast.success('Dashboard refreshed!');
  };

  const statsData = [
    {
      title: 'Total Students',
      value: stats.totalStudents,
      icon: GraduationCap,
      color: 'blue',
      change: '+12%',
      trend: 'up'
    },
    {
      title: 'Total Faculty',
      value: stats.totalFaculty,
      icon: Users,
      color: 'purple',
      change: '+5%',
      trend: 'up'
    },
    {
      title: 'Active Projects',
      value: stats.activeProjects,
      icon: Briefcase,
      color: 'green',
      change: '+18%',
      trend: 'up'
    },
    {
      title: 'Completed',
      value: stats.completedProjects,
      icon: TrendingUp,
      color: 'amber',
      change: '+24%',
      trend: 'up'
    }
  ];

  const quickActions = [
    {
      label: 'Add Student',
      icon: UserPlus,
      color: 'blue',
      action: () => setShowRegisterModal(true)
    },
    {
      label: 'Upload Excel',
      icon: FileSpreadsheet,
      color: 'green',
      action: () => setShowUploadModal(true)
    },
    {
      label: 'Export Data',
      icon: Download,
      color: 'purple',
      action: () => toast.success('Exporting data...')
    },
    {
      label: 'Settings',
      icon: Settings,
      color: 'gray',
      action: () => toast.info('Settings coming soon')
    }
  ];

  return (
    <div className="flex h-screen bg-gray-50 dark:bg-gray-900 overflow-hidden">
      {/* Sidebar */}
      <div className="hidden lg:block w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700">
        <AdminSidebar activeTab={activeTab} setActiveTab={setActiveTab} />
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <AdminHeader />

        {/* Content Area */}
        <main className="flex-1 overflow-y-auto">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {/* Welcome Section */}
            <div className="mb-8">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <motion.h1
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="text-3xl font-bold text-gray-900 dark:text-white"
                  >
                    Admin Dashboard
                  </motion.h1>
                  <motion.p
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.1 }}
                    className="text-gray-600 dark:text-gray-400 mt-1"
                  >
                    Manage users, projects, and system settings
                  </motion.p>
                </div>
                <motion.button
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={handleRefresh}
                  disabled={refreshing}
                  className="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center gap-2 disabled:opacity-50"
                >
                  <RefreshCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />
                  Refresh
                </motion.button>
              </div>

              {/* Quick Actions */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.2 }}
                className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8"
              >
                {quickActions.map((action, index) => {
                  const Icon = action.icon;
                  return (
                    <motion.button
                      key={action.label}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.3 + index * 0.1 }}
                      whileHover={{ y: -4, scale: 1.02 }}
                      whileTap={{ scale: 0.98 }}
                      onClick={action.action}
                      className="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 hover:shadow-lg transition-all group"
                    >
                      <div className={`w-10 h-10 rounded-lg bg-${action.color}-100 dark:bg-${action.color}-900/20 flex items-center justify-center mb-3 group-hover:scale-110 transition-transform`}>
                        <Icon className={`w-5 h-5 text-${action.color}-600 dark:text-${action.color}-400`} />
                      </div>
                      <div className="text-sm font-semibold text-gray-900 dark:text-white">
                        {action.label}
                      </div>
                    </motion.button>
                  );
                })}
              </motion.div>
            </div>

            {/* Stats Grid */}
            {loading ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {[1, 2, 3, 4].map((i) => (
                  <div key={i} className="bg-white dark:bg-gray-800 rounded-2xl p-6 animate-pulse">
                    <div className="h-12 w-12 bg-gray-200 dark:bg-gray-700 rounded-xl mb-4" />
                    <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded mb-2" />
                    <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-2/3" />
                  </div>
                ))}
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {statsData.map((stat, index) => (
                  <motion.div
                    key={stat.title}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.4 + index * 0.1 }}
                  >
                    <StatsCard {...stat} />
                  </motion.div>
                ))}
              </div>
            )}

            {/* Tabs */}
            <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden">
              {/* Tab Headers */}
              <div className="border-b border-gray-200 dark:border-gray-700">
                <div className="flex">
                  {['dashboard', 'students', 'faculty'].map((tab) => (
                    <button
                      key={tab}
                      onClick={() => setActiveTab(tab)}
                      className={`flex-1 px-6 py-4 text-sm font-semibold capitalize transition-colors relative ${activeTab === tab
                        ? 'text-blue-600 dark:text-blue-400'
                        : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
                        }`}
                    >
                      {tab}
                      {activeTab === tab && (
                        <motion.div
                          layoutId="activeTab"
                          className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 dark:bg-blue-400"
                        />
                      )}
                    </button>
                  ))}
                </div>
              </div>

              {/* Tab Content */}
              <div className="p-6">
                <AnimatePresence mode="wait">
                  {activeTab === 'dashboard' && (
                    <motion.div
                      key="dashboard"
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -20 }}
                      transition={{ duration: 0.2 }}
                    >
                      <div className="text-center py-12">
                        <div className="w-20 h-20 bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
                          <Briefcase className="w-10 h-10 text-white" />
                        </div>
                        <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
                          Welcome to Admin Dashboard
                        </h3>
                        <p className="text-gray-600 dark:text-gray-400 mb-6 max-w-md mx-auto">
                          Manage students, faculty, and monitor system activities from here.
                        </p>
                        <div className="flex items-center justify-center gap-4">
                          <button
                            onClick={() => setActiveTab('students')}
                            className="px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-colors"
                          >
                            Manage Students
                          </button>
                          <button
                            onClick={() => setActiveTab('faculty')}
                            className="px-6 py-3 bg-white dark:bg-gray-700 border-2 border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors"
                          >
                            Manage Faculty
                          </button>
                        </div>
                      </div>
                    </motion.div>
                  )}

                  {activeTab === 'students' && (
                    <motion.div
                      key="students"
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -20 }}
                      transition={{ duration: 0.2 }}
                    >
                      {/* Search and Filter */}
                      <div className="flex items-center gap-4 mb-6">
                        <div className="flex-1 relative">
                          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                          <input
                            type="text"
                            placeholder="Search students..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="w-full pl-12 pr-4 py-3 bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-900 dark:text-white placeholder-gray-500 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                          />
                        </div>
                        <button className="px-4 py-3 bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors flex items-center gap-2">
                          <Filter className="w-5 h-5" />
                          Filter
                        </button>
                      </div>

                      <UserTable
                        userType="students"
                        searchQuery={searchQuery}
                        onUpdatePassword={(user) => {
                          setSelectedUser(user);
                          setShowPasswordModal(true);
                        }}
                        onDeleteUser={(userId) => {
                          toast.success('User deleted successfully');
                        }}
                      />
                    </motion.div>
                  )}

                  {activeTab === 'faculty' && (
                    <motion.div
                      key="faculty"
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -20 }}
                      transition={{ duration: 0.2 }}
                    >
                      {/* Search and Filter */}
                      <div className="flex items-center gap-4 mb-6">
                        <div className="flex-1 relative">
                          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                          <input
                            type="text"
                            placeholder="Search faculty..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="w-full pl-12 pr-4 py-3 bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-900 dark:text-white placeholder-gray-500 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                          />
                        </div>
                        <button className="px-4 py-3 bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors flex items-center gap-2">
                          <Filter className="w-5 h-5" />
                          Filter
                        </button>
                      </div>

                      <UserTable
                        userType="faculty"
                        searchQuery={searchQuery}
                        onUpdatePassword={(user) => {
                          setSelectedUser(user);
                          setShowPasswordModal(true);
                        }}
                        onDeleteUser={(userId) => {
                          toast.success('User deleted successfully');
                        }}
                      />
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </div>
          </div>
        </main>
      </div>

      {/* Modals */}
      <RegisterModal
        isOpen={showRegisterModal}
        onClose={() => setShowRegisterModal(false)}
        onSubmit={() => {
          setShowRegisterModal(false);
          fetchDashboardStats();
        }}
      />

      <UploadExcelModal
        isOpen={showUploadModal}
        onClose={() => setShowUploadModal(false)}
        onUpload={() => {
          setShowUploadModal(false);
          fetchDashboardStats();
        }}
      />

      {selectedUser && (
        <UpdatePasswordModal
          isOpen={showPasswordModal}
          onClose={() => {
            setShowPasswordModal(false);
            setSelectedUser(null);
          }}
          user={selectedUser}
          onUpdate={() => {
            setShowPasswordModal(false);
            setSelectedUser(null);
          }}
        />
      )}
    </div>
  );
};

export default AdminDashboard;
