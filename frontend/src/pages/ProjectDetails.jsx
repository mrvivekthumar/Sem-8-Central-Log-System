import { AnimatePresence, motion } from 'framer-motion';
import {
  AlertCircle,
  ArrowLeft,
  Award,
  BookOpen,
  Calendar,
  CheckCircle,
  Clock,
  Code,
  Loader2,
  Mail,
  Send,
  Sparkles,
  Target,
  User,
  Users
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate, useParams } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';
import { useAuth } from '../contexts/AuthContext';

const ProjectDetails = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [showApplicationModal, setShowApplicationModal] = useState(false);
  const [hasApplied, setHasApplied] = useState(false);

  useEffect(() => {
    fetchProjectDetails();
    checkApplicationStatus();
  }, [projectId]);

  const fetchProjectDetails = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get(`/FACULTY-SERVICE/api/project/${projectId}`);
      setProject(response.data);
    } catch (error) {
      console.error('Error fetching project:', error);
      toast.error('Failed to load project details');
    } finally {
      setLoading(false);
    }
  };

  const checkApplicationStatus = async () => {
    try {
      const response = await axiosInstance.get(
        `/STUDENT-SERVICE/api/studentProject/appliedProjects`
      );
      const applied = response.data.some(p => p.projectId === parseInt(projectId));
      setHasApplied(applied);
    } catch (error) {
      console.error('Error checking application:', error);
    }
  };

  const handleApply = async () => {
    try {
      setApplying(true);
      await axiosInstance.post(
        `/STUDENT-SERVICE/api/studentProject/student/${user.id}/project/${projectId}`
      );
      toast.success('Application submitted successfully!');
      setHasApplied(true);
      setShowApplicationModal(false);
    } catch (error) {
      console.error('Error applying:', error);
      toast.error('Failed to submit application');
    } finally {
      setApplying(false);
    }
  };

  const getStatusConfig = (status) => {
    const configs = {
      'OPEN_FOR_APPLICATIONS': {
        label: 'Open for Applications',
        color: 'green',
        icon: CheckCircle,
        bgColor: 'bg-green-100 dark:bg-green-900/20',
        textColor: 'text-green-700 dark:text-green-300',
        borderColor: 'border-green-200 dark:border-green-800'
      },
      'IN_PROGRESS': {
        label: 'In Progress',
        color: 'blue',
        icon: Clock,
        bgColor: 'bg-blue-100 dark:bg-blue-900/20',
        textColor: 'text-blue-700 dark:text-blue-300',
        borderColor: 'border-blue-200 dark:border-blue-800'
      },
      'COMPLETED': {
        label: 'Completed',
        color: 'purple',
        icon: Award,
        bgColor: 'bg-purple-100 dark:bg-purple-900/20',
        textColor: 'text-purple-700 dark:text-purple-300',
        borderColor: 'border-purple-200 dark:border-purple-800'
      }
    };
    return configs[status] || configs['OPEN_FOR_APPLICATIONS'];
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full"
        />
      </div>
    );
  }

  if (!project) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-4">
        <div className="text-center">
          <AlertCircle className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
            Project Not Found
          </h2>
          <p className="text-gray-600 dark:text-gray-400 mb-6">
            The project you're looking for doesn't exist or has been removed.
          </p>
          <button
            onClick={() => navigate(-1)}
            className="px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-colors"
          >
            Go Back
          </button>
        </div>
      </div>
    );
  }

  const statusConfig = getStatusConfig(project.status);
  const StatusIcon = statusConfig.icon;

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Header */}
      <div className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-40 backdrop-blur-lg bg-white/80 dark:bg-gray-800/80">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center gap-4">
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => navigate(-1)}
              className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-xl transition-colors"
            >
              <ArrowLeft className="w-6 h-6 text-gray-600 dark:text-gray-400" />
            </motion.button>
            <div className="flex-1">
              <motion.h1
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-2xl font-bold text-gray-900 dark:text-white"
              >
                Project Details
              </motion.h1>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Project Header Card */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden"
            >
              {/* Hero Banner */}
              <div className="relative h-48 bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 overflow-hidden">
                <div className="absolute inset-0 opacity-20">
                  <div className="absolute inset-0" style={{
                    backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
                  }} />
                </div>

                {/* Status Badge */}
                <div className="absolute top-6 right-6">
                  <div className={`px-4 py-2 rounded-full ${statusConfig.bgColor} ${statusConfig.borderColor} border-2 backdrop-blur-sm flex items-center gap-2 shadow-lg`}>
                    <StatusIcon className={`w-4 h-4 ${statusConfig.textColor}`} />
                    <span className={`text-sm font-semibold ${statusConfig.textColor}`}>
                      {statusConfig.label}
                    </span>
                  </div>
                </div>
              </div>

              {/* Content */}
              <div className="p-8">
                {/* Domain Tag */}
                {project.domain && (
                  <div className="inline-flex items-center gap-2 px-3 py-1 rounded-lg bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 mb-4">
                    <Code className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                    <span className="text-sm font-semibold text-blue-700 dark:text-blue-300">
                      {project.domain}
                    </span>
                  </div>
                )}

                {/* Title */}
                <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-4">
                  {project.title}
                </h2>

                {/* Description */}
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-6">
                  {project.description}
                </p>

                {/* Meta Info Grid */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div className="text-center p-4 bg-gray-50 dark:bg-gray-700 rounded-xl">
                    <Users className="w-6 h-6 text-blue-600 dark:text-blue-400 mx-auto mb-2" />
                    <div className="text-2xl font-bold text-gray-900 dark:text-white">
                      {project.teamSize || 'N/A'}
                    </div>
                    <div className="text-xs text-gray-600 dark:text-gray-400">Team Size</div>
                  </div>

                  <div className="text-center p-4 bg-gray-50 dark:bg-gray-700 rounded-xl">
                    <Clock className="w-6 h-6 text-purple-600 dark:text-purple-400 mx-auto mb-2" />
                    <div className="text-2xl font-bold text-gray-900 dark:text-white">
                      {project.duration || 'N/A'}
                    </div>
                    <div className="text-xs text-gray-600 dark:text-gray-400">Duration</div>
                  </div>

                  <div className="text-center p-4 bg-gray-50 dark:bg-gray-700 rounded-xl">
                    <Target className="w-6 h-6 text-green-600 dark:text-green-400 mx-auto mb-2" />
                    <div className="text-2xl font-bold text-gray-900 dark:text-white">
                      {project.maxStudents || 'N/A'}
                    </div>
                    <div className="text-xs text-gray-600 dark:text-gray-400">Max Students</div>
                  </div>

                  <div className="text-center p-4 bg-gray-50 dark:bg-gray-700 rounded-xl">
                    <Calendar className="w-6 h-6 text-amber-600 dark:text-amber-400 mx-auto mb-2" />
                    <div className="text-lg font-bold text-gray-900 dark:text-white">
                      {project.deadline ? new Date(project.deadline).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) : 'N/A'}
                    </div>
                    <div className="text-xs text-gray-600 dark:text-gray-400">Deadline</div>
                  </div>
                </div>
              </div>
            </motion.div>

            {/* Technologies Section */}
            {project.technologiesUsed && project.technologiesUsed.length > 0 && (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
                className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6"
              >
                <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                  <Code className="w-6 h-6 text-blue-600" />
                  Technologies & Skills Required
                </h3>
                <div className="flex flex-wrap gap-3">
                  {project.technologiesUsed.map((tech, index) => (
                    <motion.div
                      key={index}
                      initial={{ opacity: 0, scale: 0.8 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: 0.2 + index * 0.05 }}
                      className="px-4 py-2 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 border border-blue-200 dark:border-blue-800 rounded-xl text-sm font-medium text-gray-700 dark:text-gray-300"
                    >
                      {tech}
                    </motion.div>
                  ))}
                </div>
              </motion.div>
            )}

            {/* Additional Details */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6"
            >
              <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                <BookOpen className="w-6 h-6 text-purple-600" />
                Project Details
              </h3>
              <div className="space-y-4 text-gray-600 dark:text-gray-400">
                <div>
                  <span className="font-semibold text-gray-900 dark:text-white">Project ID:</span> {project.projectId}
                </div>
                {project.applicationDeadline && (
                  <div>
                    <span className="font-semibold text-gray-900 dark:text-white">Application Deadline:</span>{' '}
                    {new Date(project.applicationDeadline).toLocaleDateString('en-US', {
                      weekday: 'long',
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}
                  </div>
                )}
                <div>
                  <span className="font-semibold text-gray-900 dark:text-white">Created:</span>{' '}
                  {project.createdAt ? new Date(project.createdAt).toLocaleDateString() : 'N/A'}
                </div>
              </div>
            </motion.div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Faculty Card */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6"
            >
              <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-4">
                Project Mentor
              </h3>
              {project.faculty ? (
                <div className="space-y-4">
                  <div className="flex items-center gap-4">
                    <div className="w-16 h-16 rounded-full bg-gradient-to-br from-purple-500 to-pink-600 flex items-center justify-center text-white text-2xl font-bold">
                      {project.faculty.name?.charAt(0) || 'F'}
                    </div>
                    <div>
                      <div className="font-semibold text-gray-900 dark:text-white text-lg">
                        {project.faculty.name}
                      </div>
                      <div className="text-sm text-gray-600 dark:text-gray-400">Faculty Mentor</div>
                    </div>
                  </div>

                  <div className="space-y-3 pt-4 border-t border-gray-200 dark:border-gray-700">
                    <div className="flex items-center gap-3 text-sm text-gray-600 dark:text-gray-400">
                      <Mail className="w-4 h-4" />
                      <span>{project.faculty.email}</span>
                    </div>
                    {project.faculty.department && (
                      <div className="flex items-center gap-3 text-sm text-gray-600 dark:text-gray-400">
                        <User className="w-4 h-4" />
                        <span>{project.faculty.department}</span>
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                <p className="text-gray-500 dark:text-gray-400">No faculty assigned</p>
              )}
            </motion.div>

            {/* Application Card */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.1 }}
              className="bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl p-6 text-white"
            >
              <Sparkles className="w-8 h-8 mb-4" />
              <h3 className="text-xl font-bold mb-2">Ready to Apply?</h3>
              <p className="text-blue-100 mb-6 text-sm">
                Join this exciting project and collaborate with talented peers under expert guidance.
              </p>

              {hasApplied ? (
                <div className="bg-white/20 backdrop-blur-sm rounded-xl p-4 text-center">
                  <CheckCircle className="w-8 h-8 mx-auto mb-2" />
                  <p className="font-semibold">Already Applied</p>
                  <p className="text-sm text-blue-100 mt-1">Your application is being reviewed</p>
                </div>
              ) : project.status === 'OPEN_FOR_APPLICATIONS' ? (
                <motion.button
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={() => setShowApplicationModal(true)}
                  className="w-full py-3 bg-white text-blue-600 rounded-xl font-semibold hover:bg-blue-50 transition-colors flex items-center justify-center gap-2"
                >
                  <Send className="w-5 h-5" />
                  Apply Now
                </motion.button>
              ) : (
                <div className="bg-white/20 backdrop-blur-sm rounded-xl p-4 text-center">
                  <AlertCircle className="w-8 h-8 mx-auto mb-2" />
                  <p className="font-semibold">Applications Closed</p>
                  <p className="text-sm text-blue-100 mt-1">This project is no longer accepting applications</p>
                </div>
              )}
            </motion.div>
          </div>
        </div>
      </div>

      {/* Application Confirmation Modal */}
      <AnimatePresence>
        {showApplicationModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
            onClick={() => setShowApplicationModal(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
              className="bg-white dark:bg-gray-800 rounded-2xl p-8 max-w-md w-full border border-gray-200 dark:border-gray-700"
            >
              <div className="text-center">
                <div className="w-16 h-16 bg-blue-100 dark:bg-blue-900/20 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Send className="w-8 h-8 text-blue-600 dark:text-blue-400" />
                </div>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
                  Confirm Application
                </h3>
                <p className="text-gray-600 dark:text-gray-400 mb-6">
                  Are you sure you want to apply to "{project.title}"?
                </p>
                <div className="flex gap-3">
                  <button
                    onClick={() => setShowApplicationModal(false)}
                    className="flex-1 px-4 py-3 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleApply}
                    disabled={applying}
                    className="flex-1 px-4 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                  >
                    {applying ? (
                      <>
                        <Loader2 className="w-5 h-5 animate-spin" />
                        Applying...
                      </>
                    ) : (
                      <>
                        <Send className="w-5 h-5" />
                        Apply
                      </>
                    )}
                  </button>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default ProjectDetails;