import { AnimatePresence } from 'framer-motion';
import { Navigate, Route, Routes } from 'react-router-dom';
import CurrentProject from './components/CurrentProject';
import AssignProjects from './components/Faculty/AssignProjects';
import ConfirmedProjects from './components/Faculty/ConfirmedProjects';
import FacultyDashboard from './components/Faculty/FacultyDashboard';
import FacultyProjectReview from './components/Faculty/FacultyProjectReview';
import ProjectDetailsPage from './components/Faculty/ProjectDetailsPage';
import StudentDetail from './components/Faculty/StudentDetail';
import StudentFacultyProfile from './components/Faculty/StudentFacultyProfile';
import Layout from './components/Layout';
import AppliedProjects from './components/Student/AppliedProjects';
import CompletedProjects from './components/Student/CompletedProjects';
import { ProjectSubmissionManager } from './components/Student/ProjectSubmissionManager';
import StudentNotifications from './components/StudentNotifications';
import StudentProfile from './components/StudentProfile';
import { useAuth } from './contexts/AuthContext';
import About from './pages/About';
import Dashboard from './pages/Dashboard';
import Feedback from './pages/Feedback';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import ProjectDetails from './pages/ProjectDetails';

// Protected Route wrapper component
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { isAuthenticated, user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  // Check if user role is allowed
  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/dashboard" />;
  }

  return children;
};

function App() {
  return (
    <AnimatePresence mode="wait">
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

        {/* Protected Routes - All Roles */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Layout><Dashboard /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/project/:id"
          element={
            <ProtectedRoute>
              <Layout><ProjectDetails /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/feedback"
          element={
            <ProtectedRoute>
              <Layout><Feedback /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Layout><StudentProfile /></Layout>
            </ProtectedRoute>
          }
        />

        {/* Student Only Routes */}
        <Route
          path="/current-project"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <Layout><CurrentProject /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/notifications"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <Layout><StudentNotifications /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/applied-projects"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <Layout><AppliedProjects /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/completed-projects"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <Layout><CompletedProjects /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/project-submission"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <Layout><ProjectSubmissionManager /></Layout>
            </ProtectedRoute>
          }
        />

        {/* Faculty Only Routes */}
        <Route
          path="/faculty/dashboard"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <Layout><FacultyDashboard /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/faculty/projects"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <Layout><FacultyProjectReview /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/faculty/assign-projects"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <Layout><AssignProjects /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/faculty/confirmed-projects"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <Layout><ConfirmedProjects /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/faculty/project/:id"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <Layout><ProjectDetailsPage /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/faculty/student/:id"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <Layout><StudentDetail /></Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/faculty/student-profile/:id"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <Layout><StudentFacultyProfile /></Layout>
            </ProtectedRoute>
          }
        />

        {/* 404 Route */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </AnimatePresence>
  );
}

export default App;
