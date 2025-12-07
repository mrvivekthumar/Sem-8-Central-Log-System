import { motion } from 'framer-motion';
import {
  AlertCircle,
  ArrowDown,
  ArrowUp,
  Calendar,
  CheckCircle,
  Clock,
  ExternalLink,
  Eye,
  GripVertical,
  Star,
  Target,
  TrendingUp,
  Users
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const AppliedProjectCard = ({
  project,
  preference,
  isDragging,
  onMoveUp,
  onMoveDown
}) => {
  const navigate = useNavigate();
 
  const getStatusConfig = (status) => {
    const configs = {
      'OPEN_FOR_APPLICATIONS': {
        label: 'Under Review',
        color: 'blue',
        icon: Clock,
        bgColor: 'bg-blue-100 dark:bg-blue-900/20',
        textColor: 'text-blue-700 dark:text-blue-300',
        borderColor: 'border-blue-200 dark:border-blue-800'
      },
      'IN_PROGRESS': {
        label: 'Accepted',
        color: 'green',
        icon: CheckCircle,
        bgColor: 'bg-green-100 dark:bg-green-900/20',
        textColor: 'text-green-700 dark:text-green-300',
        borderColor: 'border-green-200 dark:border-green-800'
      },
      'COMPLETED': {
        label: 'Completed',
        color: 'purple',
        icon: Star,
        bgColor: 'bg-purple-100 dark:bg-purple-900/20',
        textColor: 'text-purple-700 dark:text-purple-300',
        borderColor: 'border-purple-200 dark:border-purple-800'
      },
      'REJECTED': {
        label: 'Not Selected',
        color: 'red',
        icon: AlertCircle,
        bgColor: 'bg-red-100 dark:bg-red-900/20',
        textColor: 'text-red-700 dark:text-red-300',
        borderColor: 'border-red-200 dark:border-red-800'
      }
    };
    return configs[status] || configs['OPEN_FOR_APPLICATIONS'];
  };

  const statusConfig = getStatusConfig(project.status);
  const StatusIcon = statusConfig.icon;

  const getPriorityLabel = (pref) => {
    if (pref === 1) return { label: 'Top Priority', color: 'from-blue-500 to-blue-600' };
    if (pref === 2) return { label: 'High Priority', color: 'from-indigo-500 to-indigo-600' };
    if (pref === 3) return { label: 'Medium Priority', color: 'from-purple-500 to-purple-600' };
    if (pref <= 5) return { label: 'Lower Priority', color: 'from-pink-500 to-pink-600' };
    return { label: 'Low Priority', color: 'from-gray-500 to-gray-600' };
  };

  const priorityInfo = getPriorityLabel(preference);

  return (
    <motion.div
      layout
      whileHover={{ scale: isDragging ? 1 : 1.02 }}
      className={`group relative bg-white dark:bg-gray-800 rounded-2xl border-2 ${isDragging
        ? 'border-blue-500 shadow-2xl'
        : 'border-gray-200 dark:border-gray-700 hover:border-blue-300 dark:hover:border-blue-700'
        } overflow-hidden transition-all`}
    >
      {/* Drag Handle & Priority Badge */}
      <div className="absolute left-0 top-0 bottom-0 w-12 bg-gradient-to-b from-gray-100 to-gray-50 dark:from-gray-700 dark:to-gray-800 flex flex-col items-center justify-center gap-2 border-r border-gray-200 dark:border-gray-700">
        <GripVertical className="w-5 h-5 text-gray-400 cursor-grab active:cursor-grabbing" />

        {/* Move Up/Down Buttons */}
        <div className="flex flex-col gap-1">
          <motion.button
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            onClick={onMoveUp}
            className="p-1 hover:bg-blue-100 dark:hover:bg-blue-900/20 rounded transition-colors"
            title="Move up"
          >
            <ArrowUp className="w-4 h-4 text-gray-500 hover:text-blue-600" />
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            onClick={onMoveDown}
            className="p-1 hover:bg-blue-100 dark:hover:bg-blue-900/20 rounded transition-colors"
            title="Move down"
          >
            <ArrowDown className="w-4 h-4 text-gray-500 hover:text-blue-600" />
          </motion.button>
        </div>
      </div>

      {/* Main Content */}
      <div className="pl-16 pr-6 py-6">
        <div className="flex items-start justify-between gap-4 mb-4">
          <div className="flex-1">
            {/* Priority Badge */}
            <div className="flex items-center gap-3 mb-3">
              <span className={`inline-flex items-center gap-2 px-3 py-1 rounded-full bg-gradient-to-r ${priorityInfo.color} text-white text-xs font-bold shadow-md`}>
                <TrendingUp className="w-3.5 h-3.5" />
                {priorityInfo.label}
              </span>

              {/* Domain */}
              {project.domain && (
                <span className="px-3 py-1 text-xs font-semibold bg-blue-50 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded-lg border border-blue-200 dark:border-blue-800">
                  {project.domain}
                </span>
              )}
            </div>

            {/* Title */}
            <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2 line-clamp-2 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
              {project.title}
            </h3>

            {/* Description */}
            <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-4">
              {project.description}
            </p>

            {/* Technologies */}
            {project.technologiesUsed && project.technologiesUsed.length > 0 && (
              <div className="flex flex-wrap gap-2 mb-4">
                {project.technologiesUsed.slice(0, 4).map((tech, idx) => (
                  <motion.span
                    key={idx}
                    initial={{ opacity: 0, scale: 0.8 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: idx * 0.05 }}
                    className="px-2.5 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg border border-gray-200 dark:border-gray-600"
                  >
                    {tech}
                  </motion.span>
                ))}
                {project.technologiesUsed.length > 4 && (
                  <span className="px-2.5 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-400 rounded-lg">
                    +{project.technologiesUsed.length - 4}
                  </span>
                )}
              </div>
            )}
          </div>

          {/* Status Badge */}
          <div className={`flex items-center gap-2 px-3 py-1.5 rounded-xl ${statusConfig.bgColor} ${statusConfig.borderColor} border-2`}>
            <StatusIcon className={`w-4 h-4 ${statusConfig.textColor}`} />
            <span className={`text-xs font-semibold ${statusConfig.textColor}`}>
              {statusConfig.label}
            </span>
          </div>
        </div>

        {/* Divider */}
        <div className="h-px bg-gradient-to-r from-transparent via-gray-300 dark:via-gray-600 to-transparent mb-4" />

        {/* Info Grid */}
        <div className="grid grid-cols-4 gap-4 mb-4">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-blue-100 dark:bg-blue-900/20 flex items-center justify-center">
              <Users className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            </div>
            <div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Team</div>
              <div className="text-sm font-semibold text-gray-900 dark:text-white">
                {project.teamSize || 'N/A'}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-purple-100 dark:bg-purple-900/20 flex items-center justify-center">
              <Clock className="w-4 h-4 text-purple-600 dark:text-purple-400" />
            </div>
            <div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Duration</div>
              <div className="text-sm font-semibold text-gray-900 dark:text-white">
                {project.duration || 'N/A'}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-green-100 dark:bg-green-900/20 flex items-center justify-center">
              <Target className="w-4 h-4 text-green-600 dark:text-green-400" />
            </div>
            <div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Max</div>
              <div className="text-sm font-semibold text-gray-900 dark:text-white">
                {project.maxStudents || 'N/A'}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-amber-100 dark:bg-amber-900/20 flex items-center justify-center">
              <Calendar className="w-4 h-4 text-amber-600 dark:text-amber-400" />
            </div>
            <div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Deadline</div>
              <div className="text-sm font-semibold text-gray-900 dark:text-white">
                {project.deadline ? new Date(project.deadline).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) : 'N/A'}
              </div>
            </div>
          </div>
        </div>

        {/* Faculty Info */}
        {project.faculty && (
          <div className="flex items-center gap-3 p-3 bg-gray-50 dark:bg-gray-700 rounded-xl mb-4">
            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-pink-600 flex items-center justify-center text-white font-semibold text-sm">
              {project.faculty.name?.charAt(0) || 'F'}
            </div>
            <div className="flex-1">
              <div className="text-xs text-gray-500 dark:text-gray-400">Faculty Mentor</div>
              <div className="text-sm font-semibold text-gray-900 dark:text-white">
                {project.faculty.name}
              </div>
            </div>
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex gap-3">
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => navigate(`/student/project/${project.projectId}`)}
            className="flex-1 py-2.5 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-md hover:shadow-lg flex items-center justify-center gap-2"
          >
            <Eye className="w-4 h-4" />
            View Details
          </motion.button>

          {project.status === 'IN_PROGRESS' && (
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              onClick={() => navigate(`/student/project/${project.projectId}/submit`)}
              className="px-6 py-2.5 bg-white dark:bg-gray-700 border-2 border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-all flex items-center justify-center gap-2"
            >
              <ExternalLink className="w-4 h-4" />
              Go to Project
            </motion.button>
          )}
        </div>
      </div>

      {/* Preference Indicator Line */}
      <div className={`absolute top-0 left-12 right-0 h-1 bg-gradient-to-r ${priorityInfo.color}`} />
    </motion.div>
  );
};

export default AppliedProjectCard;
