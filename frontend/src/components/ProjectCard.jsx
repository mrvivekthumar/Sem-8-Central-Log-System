import { AnimatePresence, motion } from 'framer-motion';
import {
  AlertCircle,
  Award,
  Calendar,
  CheckCircle,
  ChevronRight,
  Clock,
  Code,
  Sparkles,
  Users,
  Zap
} from 'lucide-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const ProjectCard = ({ project, showApplyButton = true, onApply }) => {
  const navigate = useNavigate();
  const [isHovered, setIsHovered] = useState(false);

  const getStatusConfig = (status) => {
    const configs = {
      'OPEN_FOR_APPLICATIONS': {
        label: 'Open for Applications',
        color: 'green',
        icon: CheckCircle,
        gradient: 'from-green-500 to-emerald-600'
      },
      'IN_PROGRESS': {
        label: 'In Progress',
        color: 'blue',
        icon: Clock,
        gradient: 'from-blue-500 to-indigo-600'
      },
      'COMPLETED': {
        label: 'Completed',
        color: 'purple',
        icon: Award,
        gradient: 'from-purple-500 to-pink-600'
      },
      'CLOSED': {
        label: 'Closed',
        color: 'gray',
        icon: AlertCircle,
        gradient: 'from-gray-500 to-gray-600'
      }
    };
    return configs[status] || configs['CLOSED'];
  };

  const statusConfig = getStatusConfig(project.status);
  const StatusIcon = statusConfig.icon;

  const handleCardClick = () => {
    navigate(`/student/project/${project.projectId}`);
  };

  const getDomainColor = (domain) => {
    const colors = {
      'Web Development': 'blue',
      'Mobile Development': 'green',
      'Machine Learning': 'purple',
      'AI': 'pink',
      'Data Science': 'indigo',
      'Cloud Computing': 'cyan',
      'DevOps': 'orange',
      'Blockchain': 'yellow',
      'IoT': 'red',
      'Cybersecurity': 'rose'
    };
    return colors[domain] || 'gray';
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      whileHover={{ y: -8, scale: 1.02 }}
      onHoverStart={() => setIsHovered(true)}
      onHoverEnd={() => setIsHovered(false)}
      className="group relative bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-2xl transition-all duration-300 cursor-pointer"
      onClick={handleCardClick}
    >
      {/* Gradient Overlay on Hover */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: isHovered ? 0.05 : 0 }}
        className={`absolute inset-0 bg-gradient-to-br ${statusConfig.gradient} pointer-events-none`}
      />

      {/* Status Badge */}
      <div className="absolute top-4 right-4 z-10">
        <motion.div
          whileHover={{ scale: 1.1 }}
          className={`px-3 py-1.5 rounded-full bg-gradient-to-r ${statusConfig.gradient} shadow-lg flex items-center gap-2`}
        >
          <StatusIcon className="w-3.5 h-3.5 text-white" />
          <span className="text-xs font-semibold text-white">{statusConfig.label}</span>
        </motion.div>
      </div>

      {/* Card Content */}
      <div className="p-6 relative z-10">
        {/* Header */}
        <div className="mb-4">
          {/* Domain Tag */}
          {project.domain && (
            <motion.div
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 mb-3"
            >
              <Code className="w-3.5 h-3.5 text-blue-600 dark:text-blue-400" />
              <span className="text-xs font-semibold text-blue-700 dark:text-blue-300">
                {project.domain}
              </span>
            </motion.div>
          )}

          {/* Title */}
          <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2 line-clamp-2 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
            {project.title}
          </h3>

          {/* Description */}
          <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-3 leading-relaxed">
            {project.description}
          </p>
        </div>

        {/* Technologies */}
        {project.technologiesUsed && project.technologiesUsed.length > 0 && (
          <div className="mb-4">
            <div className="flex flex-wrap gap-2">
              {project.technologiesUsed.slice(0, 4).map((tech, index) => (
                <motion.span
                  key={index}
                  initial={{ opacity: 0, scale: 0.8 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="px-2.5 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg border border-gray-200 dark:border-gray-600"
                >
                  {tech}
                </motion.span>
              ))}
              {project.technologiesUsed.length > 4 && (
                <span className="px-2.5 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-400 rounded-lg">
                  +{project.technologiesUsed.length - 4} more
                </span>
              )}
            </div>
          </div>
        )}

        {/* Divider */}
        <div className="h-px bg-gradient-to-r from-transparent via-gray-300 dark:via-gray-600 to-transparent mb-4" />

        {/* Meta Information */}
        <div className="space-y-3 mb-4">
          {/* Faculty */}
          {project.faculty && (
            <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <div className="w-8 h-8 rounded-full bg-gradient-to-br from-purple-500 to-pink-600 flex items-center justify-center text-white font-semibold text-xs">
                {project.faculty.name?.charAt(0) || 'F'}
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-500">Mentor</div>
                <div className="font-medium text-gray-900 dark:text-white">
                  {project.faculty.name}
                </div>
              </div>
            </div>
          )}

          {/* Team Size & Duration */}
          <div className="grid grid-cols-2 gap-3">
            <div className="flex items-center gap-2 text-sm">
              <div className="w-8 h-8 rounded-lg bg-blue-100 dark:bg-blue-900/20 flex items-center justify-center">
                <Users className="w-4 h-4 text-blue-600 dark:text-blue-400" />
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-500">Team Size</div>
                <div className="font-semibold text-gray-900 dark:text-white">
                  {project.teamSize || 'N/A'}
                </div>
              </div>
            </div>

            <div className="flex items-center gap-2 text-sm">
              <div className="w-8 h-8 rounded-lg bg-purple-100 dark:bg-purple-900/20 flex items-center justify-center">
                <Calendar className="w-4 h-4 text-purple-600 dark:text-purple-400" />
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-500">Duration</div>
                <div className="font-semibold text-gray-900 dark:text-white">
                  {project.duration || 'N/A'}
                </div>
              </div>
            </div>
          </div>

          {/* Deadline */}
          {project.deadline && (
            <div className="flex items-center gap-2 px-3 py-2 bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800 rounded-lg">
              <Clock className="w-4 h-4 text-amber-600 dark:text-amber-400" />
              <span className="text-xs font-medium text-amber-700 dark:text-amber-300">
                Deadline: {new Date(project.deadline).toLocaleDateString('en-US', {
                  month: 'short',
                  day: 'numeric',
                  year: 'numeric'
                })}
              </span>
            </div>
          )}
        </div>

        {/* Action Buttons */}
        <div className="flex gap-3 mt-4">
          {showApplyButton && project.status === 'OPEN_FOR_APPLICATIONS' && (
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              onClick={(e) => {
                e.stopPropagation();
                onApply && onApply(project);
              }}
              className="flex-1 py-2.5 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-md hover:shadow-lg flex items-center justify-center gap-2"
            >
              <Sparkles className="w-4 h-4" />
              Apply Now
            </motion.button>
          )}

          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={(e) => {
              e.stopPropagation();
              handleCardClick();
            }}
            className="flex-1 py-2.5 bg-white dark:bg-gray-700 border-2 border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-all flex items-center justify-center gap-2 group"
          >
            View Details
            <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </motion.button>
        </div>
      </div>

      {/* Floating Sparkle Effect on Hover */}
      <AnimatePresence>
        {isHovered && (
          <>
            <motion.div
              initial={{ opacity: 0, scale: 0 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0 }}
              className="absolute top-8 left-8 text-yellow-400"
            >
              <Sparkles className="w-4 h-4" />
            </motion.div>
            <motion.div
              initial={{ opacity: 0, scale: 0 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0 }}
              transition={{ delay: 0.1 }}
              className="absolute bottom-8 right-8 text-blue-400"
            >
              <Zap className="w-4 h-4" />
            </motion.div>
          </>
        )}
      </AnimatePresence>

      {/* Hover Border Glow */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: isHovered ? 1 : 0 }}
        className={`absolute inset-0 rounded-2xl border-2 border-gradient-to-r ${statusConfig.gradient} pointer-events-none`}
      />
    </motion.div>
  );
};

export default ProjectCard;
