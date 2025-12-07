import { motion } from 'framer-motion';
import { Calendar, MessageCircle, Star, ThumbsUp } from 'lucide-react';

const ReviewCard = ({ review, index = 0 }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: index * 0.05 }}
      className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6 hover:shadow-lg transition-all"
    >
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-lg">
            {review.studentName?.charAt(0) || 'S'}
          </div>
          <div>
            <h3 className="font-bold text-gray-900 dark:text-white">
              {review.studentName || 'Anonymous Student'}
            </h3>
            <div className="flex items-center gap-1 text-sm text-gray-600 dark:text-gray-400">
              <Calendar className="w-3.5 h-3.5" />
              {review.date ? new Date(review.date).toLocaleDateString() : 'Recent'}
            </div>
          </div>
        </div>

        {/* Rating */}
        <div className="flex items-center gap-1 bg-yellow-50 dark:bg-yellow-900/20 px-3 py-1.5 rounded-xl">
          <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
          <span className="font-bold text-gray-900 dark:text-white">
            {review.rating || 5.0}
          </span>
        </div>
      </div>

      {/* Review Text */}
      <p className="text-gray-600 dark:text-gray-400 mb-4 leading-relaxed">
        {review.comment || 'Great project experience! Learned a lot and enjoyed working on it.'}
      </p>

      {/* Project Info */}
      {review.projectTitle && (
        <div className="mb-4 p-3 bg-blue-50 dark:bg-blue-900/20 rounded-xl border border-blue-200 dark:border-blue-800">
          <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">Project</p>
          <p className="font-semibold text-blue-600 dark:text-blue-400">
            {review.projectTitle}
          </p>
        </div>
      )}

      {/* Stats */}
      <div className="flex items-center gap-4 pt-4 border-t border-gray-200 dark:border-gray-700">
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 transition-colors"
        >
          <ThumbsUp className="w-4 h-4" />
          <span>{review.likes || 0}</span>
        </motion.button>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 transition-colors"
        >
          <MessageCircle className="w-4 h-4" />
          <span>Reply</span>
        </motion.button>
      </div>
    </motion.div>
  );
};

export default ReviewCard;
