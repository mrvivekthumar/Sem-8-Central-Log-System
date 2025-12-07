import { motion } from 'framer-motion';
import {
  ArrowRight,
  Award,
  Calendar,
  CheckCircle, Users
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import { useAuth } from '../../contexts/AuthContext';

const ApprovedProjectList = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchApprovedProjects();
  }, []);

  const fetchApprovedProjects = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get(
        `/FACULTY-SERVICE/api/faculty/${user.id}/approved-projects`
      );
      setProjects(response.data || []);
    } catch (error) {
      console.error('Error fetching approved projects:', error);
      toast.error('Failed to load approved projects');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full"
        />
      </div>
    );
  }

  if (projects.length === 0) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center py-12"
      >
        <div className="w-20 h-20 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
          <CheckCircle className="w-10 h-10 text-gray-400" />
        </div>
        <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
          No Approved Projects
        </h3>
        <p className="text-gray-600 dark:text-gray-400">
          Projects with approved students will appear here.
        </p>
      </motion.div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {projects.map((project, index) => (
        <motion.div
          key={project.projectId}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: index * 0.05 }}
          whileHover={{ y: -8 }}
          className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-2xl transition-all group cursor-pointer"
          onClick={() => navigate(`/faculty/project/${project.projectId}`)}
        >
          {/* Gradient Header */}
          <div className="h-2 bg-gradient-to-r from-green-500 to-emerald-600" />

          <div className="p-6">
            {/* Status Badge */}
            <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 mb-4">
              <CheckCircle className="w-4 h-4 text-green-600 dark:text-green-400" />
              <span className="text-xs font-semibold text-green-700 dark:text-green-300">
                Approved
              </span>
            </div>

            {/* Title */}
            <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2 line-clamp-2 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
              {project.title}
            </h3>

            {/* Description */}
            <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-4">
              {project.description}
            </p>

            {/* Stats Grid */}
            <div className="grid grid-cols-2 gap-3 mb-4">
              <div className="text-center p-3 bg-blue-50 dark:bg-blue-900/20 rounded-xl border border-blue-200 dark:border-blue-800">
                <Users className="w-4 h-4 text-blue-600 dark:text-blue-400 mx-auto mb-1" />
                <div className="text-lg font-bold text-gray-900 dark:text-white">
                  {project.approvedCount || 0}
                </div>
                <div className="text-xs text-gray-600 dark:text-gray-400">Approved</div>
              </div>
              <div className="text-center p-3 bg-purple-50 dark:bg-purple-900/20 rounded-xl border border-purple-200 dark:border-purple-800">
                <Award className="w-4 h-4 text-purple-600 dark:text-purple-400 mx-auto mb-1" />
                <div className="text-lg font-bold text-gray-900 dark:text-white">
                  {project.maxStudents || 0}
                </div>
                <div className="text-xs text-gray-600 dark:text-gray-400">Max</div>
              </div>
            </div>

            {/* Footer */}
            <div className="flex items-center justify-between pt-4 border-t border-gray-200 dark:border-gray-700">
              <div className="flex items-center gap-2 text-xs text-gray-500 dark:text-gray-400">
                <Calendar className="w-3.5 h-3.5" />
                {project.deadline ? new Date(project.deadline).toLocaleDateString() : 'No deadline'}
              </div>
              <motion.div
                className="flex items-center gap-1 text-blue-600 dark:text-blue-400 font-semibold text-sm"
                whileHover={{ x: 4 }}
              >
                View
                <ArrowRight className="w-4 h-4" />
              </motion.div>
            </div>
          </div>

          {/* Hover Effect */}
          <motion.div
            initial={{ opacity: 0 }}
            whileHover={{ opacity: 1 }}
            className="absolute inset-0 bg-gradient-to-t from-green-600/10 to-transparent pointer-events-none"
          />
        </motion.div>
      ))}
    </div>
  );
};

export default ApprovedProjectList;
