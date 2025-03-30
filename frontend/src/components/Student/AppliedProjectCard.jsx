import React from 'react';
import { motion } from 'framer-motion';
import { Clock, ChevronUp, ChevronDown, GripVertical } from 'lucide-react';
import { Link } from 'react-router-dom';

const AppliedProjectCard = ({ project, preference, isDragging, onMoveUp, onMoveDown }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className={`bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden border-l-4 ${
        isDragging ? 'shadow-2xl border-blue-500' : getBorderColorByPreference(preference)
      }`}
    >
      <div className="p-6">
        <div className="flex justify-between items-start mb-4">
          <div className="flex items-center space-x-3">
            <div className="flex items-center">
              <GripVertical className="h-5 w-5 text-gray-400 mr-2" />
              <div 
                className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-white ${getPreferenceColor(preference)}`}
              >
                {preference}
              </div>
            </div>
            <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
              {project.title}
            </h3>
          </div>
          <span
            className={`px-3 py-1 rounded-full text-sm font-medium ${
              project.status === 'OPEN_FOR_APPLICATIONS'
                ? 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400'
                : 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400'
            }`}
          >
            {project.status.replaceAll('_', ' ')}
          </span>
        </div>

        <p className="text-gray-600 dark:text-gray-300 mb-4 line-clamp-2">
          {project.description}
        </p>

        <div className="flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
          <div className="flex items-center">
            <Clock className="h-4 w-4 mr-1" />
            <span>Deadline: {new Date(project.deadline).toLocaleDateString()}</span>
          </div>
          <div className="flex items-center">
            <span>Faculty: {project.faculty?.name || 'Unknown'}</span>
          </div>
        </div>

        <div className="mt-4 flex items-center">
          <div className="flex-grow">
            <Link
              to={`/studentProject/${project.projectId}`}
              className="block w-full text-center py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-md transition-colors"
            >
              View Details
            </Link>
          </div>
          <div className="ml-3 flex flex-col">
            <button 
              onClick={onMoveUp} 
              disabled={!onMoveUp}
              className={`p-1 rounded ${!onMoveUp ? 'opacity-30 cursor-not-allowed' : 'hover:bg-gray-100 dark:hover:bg-gray-700'}`}
              title="Move Up"
            >
              <ChevronUp className="h-5 w-5 text-gray-600 dark:text-gray-300" />
            </button>
            <button 
              onClick={onMoveDown} 
              disabled={!onMoveDown}
              className={`p-1 rounded ${!onMoveDown ? 'opacity-30 cursor-not-allowed' : 'hover:bg-gray-100 dark:hover:bg-gray-700'}`}
              title="Move Down"
            >
              <ChevronDown className="h-5 w-5 text-gray-600 dark:text-gray-300" />
            </button>
          </div>
        </div>
      </div>
    </motion.div>
  );
};

// Helper functions for preference styling
const getPreferenceColor = (preference) => {
  if (preference === 1) return 'bg-blue-600';
  if (preference === 2) return 'bg-indigo-600';
  if (preference === 3) return 'bg-purple-600';
  if (preference <= 5) return 'bg-pink-600';
  return 'bg-gray-600';
};

const getBorderColorByPreference = (preference) => {
  if (preference === 1) return 'border-blue-500';
  if (preference === 2) return 'border-indigo-500';
  if (preference === 3) return 'border-purple-500';
  if (preference <= 5) return 'border-pink-500';
  return 'border-gray-500';
};

export default AppliedProjectCard;