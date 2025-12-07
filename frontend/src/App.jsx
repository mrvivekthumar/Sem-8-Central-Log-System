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
import AdminDashboard from './pages/AdminDashboard';
import Dashboard from './pages/Dashboard';
import Feedback from './pages/Feedback';
import Home from './pages/Home';
import Login from './pages/Login';
import ProjectDetails from './pages/ProjectDetails';

// Protected Route wrapper component
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <AnimatePresence mode="wait">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />

        {/* Protected Routes */}
        <Route path="/dashboard" element={<ProtectedRoute><Layout><Dashboard /></Layout></ProtectedRoute>} />
        <Route path="/project/:id" element={<ProtectedRoute><Layout><ProjectDetails /></Layout></ProtectedRoute>} />
        <Route path="/feedback" element={<ProtectedRoute><Layout><Feedback /></Layout></ProtectedRoute>} />
        <Route path="/current-project" element={<ProtectedRoute><Layout><CurrentProject /></Layout></ProtectedRoute>} />
        <Route path="/notifications" element={<ProtectedRoute><Layout><StudentNotifications /></Layout></ProtectedRoute>} />
        <Route path="/profile" element={<ProtectedRoute><Layout><StudentProfile /></Layout></ProtectedRoute>} />
        <Route path="/applied-projects" element={<ProtectedRoute><Layout><AppliedProjects /></Layout></ProtectedRoute>} />
        <Route path="/completed-projects" element={<ProtectedRoute><Layout><CompletedProjects /></Layout></ProtectedRoute>} />
        <Route path="/project-submission" element={<ProtectedRoute><Layout><ProjectSubmissionManager /></Layout></ProtectedRoute>} />

        {/* Faculty Routes */}
        <Route path="/faculty/dashboard" element={<ProtectedRoute><Layout><FacultyDashboard /></Layout></ProtectedRoute>} />
        <Route path="/faculty/projects" element={<ProtectedRoute><Layout><FacultyProjectReview /></Layout></ProtectedRoute>} />
        <Route path="/faculty/assign-projects" element={<ProtectedRoute><Layout><AssignProjects /></Layout></ProtectedRoute>} />
        <Route path="/faculty/confirmed-projects" element={<ProtectedRoute><Layout><ConfirmedProjects /></Layout></ProtectedRoute>} />
        <Route path="/faculty/project/:id" element={<ProtectedRoute><Layout><ProjectDetailsPage /></Layout></ProtectedRoute>} />
        <Route path="/faculty/student/:id" element={<ProtectedRoute><Layout><StudentDetail /></Layout></ProtectedRoute>} />
        <Route path="/faculty/student-profile/:id" element={<ProtectedRoute><Layout><StudentFacultyProfile /></Layout></ProtectedRoute>} />

        {/* Admin Routes */}
        <Route path="/admin/dashboard" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>} />
      </Routes>
    </AnimatePresence>
  );
}

export default App;
