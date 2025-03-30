import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AnimatePresence } from 'framer-motion';
import { ThemeProvider } from './contexts/ThemeContext';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import ProjectDetails from './pages/ProjectDetails';
import ConfirmedProjects from './components/Faculty/ConfirmedProjects';
import Feedback from './pages/Feedback';
import Layout from './components/Layout';
import FacultyDashboard from './components/Faculty/FacultyDashboard';
import ProjectDetailsPage from './components/Faculty/ProjectDetailsPage';
import Home from './pages/Home';
import StudentProjectCard from './components/StudentProjectCard';
import StudentDetail from './components/Faculty/StudentDetail';
import { useEffect } from 'react';
import AssignProjects from './components/Faculty/AssignProjects';
import CurrentProject from './components/CurrentProject';
import StudentProfile from './components/StudentProfile';
import StudentFacultyProfile from './components/Faculty/StudentFacultyProfile';
import AppliedProjects from './components/Student/AppliedProjects';
import StudentNotifications from './components/StudentNotifications';
import AdminDashboard from './pages/AdminDashboard';
import { ProjectSubmissionManager } from './components/Student/ProjectSubmissionManager';
import { FacultyProjectReview } from './components/Faculty/FacultyProjectReview';
import CompletedProjects from './components/Student/CompletedProjects';
// Protected Route wrapper component
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" />;
};


function App() {
  return (
    <Router>
      <ThemeProvider>
        <AuthProvider>
          <Toaster position="top-right" />
          <AnimatePresence mode="wait">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/" element={<Home />} />
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <Layout />
                  </ProtectedRoute>
                }
              >
                <Route path='student/dashboard' element={<Dashboard />} />
                <Route path="/project/:projectId" element={<ProjectDetailsPage />} />
                <Route path="/application/project/:projectId" element={<StudentDetail />} />
                <Route path="/studentproject/:projectId" element={<ProjectDetails />} />
                <Route path="faculty/dashboard" element={<FacultyDashboard />} />
                <Route path="project/:id" element={<ProjectDetails />} />
                <Route path="confirmed-projects" element={<ConfirmedProjects />} />
                <Route path="feedback" element={<Feedback />} />
                <Route path="/assigned-projects" element={<AssignProjects />} />
                <Route path="/current-project" element={<CurrentProject />} />

                <Route path="/user/profile/:studentId" element={<StudentProfile />} />
                <Route path='/student/profile/:studentId' element={<StudentFacultyProfile />} />
                <Route path="/applied-projects" element={<AppliedProjects />} />
                <Route path='/student/notifications' element={<StudentNotifications />} />
                <Route path='/admin/dashboard' element={<AdminDashboard />} />
                <Route path='/projects/:projectId/report-submission' element={<ProjectSubmissionManager />} />
                <Route path='/project/:projectId/report' element={<FacultyProjectReview/>}/>
                <Route path='/completed-projects' element={<CompletedProjects/>}/>
              </Route>
            </Routes>
          </AnimatePresence>
        </AuthProvider>
      </ThemeProvider>
    </Router>
  );
}

export default App;