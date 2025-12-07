import { motion } from 'framer-motion';
import {
  Calendar,
  Download, Eye,
  Star,
  Trophy,
  Users
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const CompleteProjectCard = ({ project }) => {
  const navigate = useNavigate();

  return (
    <motion.div
      whileHover={{ y: -8, scale: 1.02 }}
      className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-2xl transition-all group"
    >
      {/* Gradient Header */}
      <div className="relative h-32 bg-gradient-to-br from-purple-500 via-pink-500 to-amber-500 overflow-hidden">
        <div className="absolute inset-0 opacity-20">
          <div className="absolute inset-0" style={{
            backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
          }} />
        </div>

        {/* Trophy Badge */}
        <div className="absolute bottom-0 left-1/2 -translate-x-1/2 translate-y-1/2">
          <div className="w-16 h-16 bg-gradient-to-br from-yellow-400 to-orange-500 rounded-full flex items-center justify-center shadow-xl border-4 border-white dark:border-gray-800">
            <Trophy className="w-8 h-8 text-white" />
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="p-6 pt-10">
        {/* Domain Badge */}
        {project.domain && (
          <span className="inline-block px-3 py-1 text-xs font-semibold bg-purple-100 dark:bg-purple-900/20 text-purple-700 dark:text-purple-300 rounded-lg mb-3">
            {project.domain}
          </span>
        )}

        {/* Title */}
        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2 line-clamp-2">
          {project.title}
        </h3>

        {/* Description */}
        <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-4">
          {project.description}
        </p>

        {/* Rating */}
        <div className="flex items-center justify-center gap-1 mb-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 rounded-xl border border-yellow-200 dark:border-yellow-800">
          <Star className="w-5 h-5 text-yellow-500 fill-yellow-500" />
          <span className="text-2xl font-bold text-gray-900 dark:text-white">
            {project.rating || '4.5'}
          </span>
          <span className="text-sm text-gray-600 dark:text-gray-400">/ 5.0</span>
        </div>

        {/* Info Grid */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          <div className="flex items-center gap-2 text-sm">
            <Users className="w-4 h-4 text-gray-400" />
            <span className="text-gray-600 dark:text-gray-400">
              Team: {project.teamSize || 'N/A'}
            </span>
          </div>
          <div className="flex items-center gap-2 text-sm">
            <Calendar className="w-4 h-4 text-gray-400" />
            <span className="text-gray-600 dark:text-gray-400">
              {project.completedDate ? new Date(project.completedDate).toLocaleDateString('en-US', { month: 'short', year: 'numeric' }) : '2024'}
            </span>
          </div>
        </div>

        {/* Divider */}
        <div className="h-px bg-gray-200 dark:bg-gray-700 mb-4" />

        {/* Actions */}
        <div className="flex gap-3">
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => navigate(`/student/project/${project.projectId}`)}
            className="flex-1 py-2.5 bg-gradient-to-r from-purple-600 to-pink-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-pink-700 transition-all flex items-center justify-center gap-2 text-sm"
          >
            <Eye className="w-4 h-4" />
            View
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="flex-1 py-2.5 bg-white dark:bg-gray-700 border-2 border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-all flex items-center justify-center gap-2 text-sm"
          >
            <Download className="w-4 h-4" />
            Certificate
          </motion.button>
        </div>
      </div>

      {/* Hover Glow */}
      <motion.div
        initial={{ opacity: 0 }}
        whileHover={{ opacity: 1 }}
        className="absolute inset-0 bg-gradient-to-t from-purple-600/10 to-transparent pointer-events-none"
      />
    </motion.div>
  );
};

export default CompleteProjectCard;
