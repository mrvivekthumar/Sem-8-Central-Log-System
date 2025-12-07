import { motion } from 'framer-motion';
import {
  ArrowRight,
  Calendar,
  Clock,
  Target,
  Users
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const StudentProjectCard = ({ project }) => {
  const navigate = useNavigate();

  const getStatusColor = (status) => {
    const colors = {
      'OPEN_FOR_APPLICATIONS': 'from-green-500 to-emerald-600',
      'IN_PROGRESS': 'from-blue-500 to-indigo-600',
      'COMPLETED': 'from-purple-500 to-pink-600'
    };
    return colors[status] || 'from-gray-500 to-gray-600';
  };

  return (
    <motion.div
      whileHover={{ y: -8, scale: 1.02 }}
      className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-2xl transition-all cursor-pointer group"
      onClick={() => navigate(`/student/project/${project.projectId}`)}
    >
      {/* Gradient Header */}
      <div className={`h-2 bg-gradient-to-r ${getStatusColor(project.status)}`} />

      <div className="p-6">
        {/* Domain Badge */}
        {project.domain && (
          <span className="inline-block px-3 py-1 text-xs font-semibold bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded-lg mb-3">
            {project.domain}
          </span>
        )}

        {/* Title */}
        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2 line-clamp-2 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
          {project.title}
        </h3>

        {/* Description */}
        <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-4">
          {project.description}
        </p>

        {/* Faculty */}
        {project.faculty && (
          <div className="flex items-center gap-2 mb-4 p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
            <div className="w-8 h-8 rounded-full bg-gradient-to-br from-purple-500 to-pink-600 flex items-center justify-center text-white text-sm font-semibold">
              {project.faculty.name?.charAt(0) || 'F'}
            </div>
            <div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Faculty</div>
              <div className="text-sm font-semibold text-gray-900 dark:text-white">
                {project.faculty.name}
              </div>
            </div>
          </div>
        )}

        {/* Stats Grid */}
        <div className="grid grid-cols-3 gap-3 mb-4">
          <div className="text-center p-2 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <Users className="w-4 h-4 text-blue-600 dark:text-blue-400 mx-auto mb-1" />
            <div className="text-xs font-semibold text-gray-900 dark:text-white">
              {project.teamSize || 'N/A'}
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-400">Team</div>
          </div>
          <div className="text-center p-2 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <Target className="w-4 h-4 text-green-600 dark:text-green-400 mx-auto mb-1" />
            <div className="text-xs font-semibold text-gray-900 dark:text-white">
              {project.maxStudents || 'N/A'}
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-400">Max</div>
          </div>
          <div className="text-center p-2 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <Clock className="w-4 h-4 text-purple-600 dark:text-purple-400 mx-auto mb-1" />
            <div className="text-xs font-semibold text-gray-900 dark:text-white">
              {project.duration || 'N/A'}
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-400">Duration</div>
          </div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between pt-4 border-t border-gray-200 dark:border-gray-700">
          <div className="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1">
            <Calendar className="w-3.5 h-3.5" />
            {project.deadline ? new Date(project.deadline).toLocaleDateString() : 'No deadline'}
          </div>
          <motion.div
            className="flex items-center gap-1 text-blue-600 dark:text-blue-400 font-semibold text-sm"
            whileHover={{ x: 4 }}
          >
            View Details
            <ArrowRight className="w-4 h-4" />
          </motion.div>
        </div>
      </div>

      {/* Hover Effect */}
      <motion.div
        initial={{ opacity: 0 }}
        whileHover={{ opacity: 1 }}
        className="absolute inset-0 bg-gradient-to-t from-blue-600/10 to-transparent pointer-events-none"
      />
    </motion.div>
  );
};

export default StudentProjectCard;
