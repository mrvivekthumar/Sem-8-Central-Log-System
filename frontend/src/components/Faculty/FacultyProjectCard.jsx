import { motion } from 'framer-motion';
import {
  AlertCircle,
  Calendar,
  CheckCircle,
  Clock,
  Edit,
  Eye,
  MoreVertical,
  Trash2,
  UserCheck
} from 'lucide-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const FacultyProjectCard = ({ project, onEdit, onDelete }) => {
  const navigate = useNavigate();
  const [showMenu, setShowMenu] = useState(false);

  const getStatusConfig = (status) => {
    const configs = {
      'OPEN_FOR_APPLICATIONS': {
        label: 'Open',
        icon: Clock,
        gradient: 'from-green-500 to-emerald-600',
        bg: 'bg-green-50 dark:bg-green-900/20',
        border: 'border-green-200 dark:border-green-800',
        text: 'text-green-700 dark:text-green-300'
      },
      'IN_PROGRESS': {
        label: 'In Progress',
        icon: UserCheck,
        gradient: 'from-blue-500 to-indigo-600',
        bg: 'bg-blue-50 dark:bg-blue-900/20',
        border: 'border-blue-200 dark:border-blue-800',
        text: 'text-blue-700 dark:text-blue-300'
      },
      'COMPLETED': {
        label: 'Completed',
        icon: CheckCircle,
        gradient: 'from-purple-500 to-pink-600',
        bg: 'bg-purple-50 dark:bg-purple-900/20',
        border: 'border-purple-200 dark:border-purple-800',
        text: 'text-purple-700 dark:text-purple-300'
      },
      'CLOSED': {
        label: 'Closed',
        icon: AlertCircle,
        gradient: 'from-gray-500 to-gray-600',
        bg: 'bg-gray-50 dark:bg-gray-900/20',
        border: 'border-gray-200 dark:border-gray-800',
        text: 'text-gray-700 dark:text-gray-300'
      }
    };
    return configs[status] || configs['OPEN_FOR_APPLICATIONS'];
  };

  const statusConfig = getStatusConfig(project.status);
  const StatusIcon = statusConfig.icon;

  return (
    <motion.div
      whileHover={{ y: -6 }}
      className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-2xl transition-all group"
    >
      {/* Gradient Header */}
      <div className={`h-2 bg-gradient-to-r ${statusConfig.gradient}`} />

      <div className="p-6">
        {/* Header Section */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            {/* Domain Badge */}
            {project.domain && (
              <span className="inline-block px-3 py-1 text-xs font-semibold bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded-lg mb-2">
                {project.domain}
              </span>
            )}

            {/* Title */}
            <h3 className="text-xl font-bold text-gray-900 dark:text-white line-clamp-2 mb-2 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
              {project.title}
            </h3>
          </div>

          {/* Menu */}
          <div className="relative">
            <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              onClick={() => setShowMenu(!showMenu)}
              className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
            >
              <MoreVertical className="w-5 h-5 text-gray-600 dark:text-gray-400" />
            </motion.button>

            {/* Dropdown Menu */}
            {showMenu && (
              <motion.div
                initial={{ opacity: 0, scale: 0.95, y: -10 }}
                animate={{ opacity: 1, scale: 1, y: 0 }}
                className="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-xl shadow-xl border border-gray-200 dark:border-gray-700 py-2 z-10"
              >
                <button
                  onClick={() => {
                    navigate(`/faculty/project/${project.projectId}`);
                    setShowMenu(false);
                  }}
                  className="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                >
                  <Eye className="w-4 h-4" />
                  View Details
                </button>
                <button
                  onClick={() => {
                    onEdit?.(project);
                    setShowMenu(false);
                  }}
                  className="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                >
                  <Edit className="w-4 h-4" />
                  Edit Project
                </button>
                <div className="h-px bg-gray-200 dark:bg-gray-700 my-2" />
                <button
                  onClick={() => {
                    onDelete?.(project);
                    setShowMenu(false);
                  }}
                  className="w-full flex items-center gap-3 px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
                >
                  <Trash2 className="w-4 h-4" />
                  Delete
                </button>
              </motion.div>
            )}
          </div>
        </div>

        {/* Description */}
        <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-4">
          {project.description}
        </p>

        {/* Status Badge */}
        <div className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-xl ${statusConfig.bg} border ${statusConfig.border} mb-4`}>
          <StatusIcon className={`w-4 h-4 ${statusConfig.text}`} />
          <span className={`text-xs font-semibold ${statusConfig.text}`}>
            {statusConfig.label}
          </span>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-3 gap-3 mb-4">
          <div className="text-center p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
            <div className="text-2xl font-bold text-gray-900 dark:text-white mb-1">
              {project.appliedStudents || 0}
            </div>
            <div className="text-xs text-gray-600 dark:text-gray-400">Applicants</div>
          </div>
          <div className="text-center p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
            <div className="text-2xl font-bold text-gray-900 dark:text-white mb-1">
              {project.acceptedStudents || 0}
            </div>
            <div className="text-xs text-gray-600 dark:text-gray-400">Accepted</div>
          </div>
          <div className="text-center p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
            <div className="text-2xl font-bold text-gray-900 dark:text-white mb-1">
              {project.maxStudents || 0}
            </div>
            <div className="text-xs text-gray-600 dark:text-gray-400">Max</div>
          </div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between pt-4 border-t border-gray-200 dark:border-gray-700">
          <div className="flex items-center gap-2 text-xs text-gray-500 dark:text-gray-400">
            <Calendar className="w-3.5 h-3.5" />
            {project.applicationDeadline
              ? new Date(project.applicationDeadline).toLocaleDateString()
              : 'No deadline'
            }
          </div>

          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => navigate(`/faculty/project/${project.projectId}/review`)}
            className="px-4 py-2 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-lg text-sm font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg hover:shadow-xl"
          >
            Review Apps
          </motion.button>
        </div>
      </div>
    </motion.div>
  );
};

export default FacultyProjectCard;
