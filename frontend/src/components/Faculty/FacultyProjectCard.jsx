// FacultyProjectCard.js
import { useNavigate } from 'react-router-dom';
import { Users, ChevronRight } from 'lucide-react';
import { motion } from 'framer-motion';

const FacultyProjectCard = ({ project }) => {
  const navigate = useNavigate();

  const viewDetails = () => {
    console.log(project);
    navigate(`/project/${project.projectId}`);
  };

  return (
    <motion.div
      whileHover={{ y: -5 }}
      className="bg-white dark:bg-gray-800 rounded-lg shadow-lg overflow-hidden"
    >
      <div className="p-6">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
            {project.title}
          </h3>
          <span
            className={`px-3 py-1 rounded-full text-sm font-medium ${
              project.status === "OPEN_FOR_APPLICATIONS"
                ? "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
                : "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            }`}
          >
            {project.status.replace(/_/g, " ")}
          </span>
        </div>

        <div className="flex justify-between text-gray-500 dark:text-gray-400 mb-4">
          {/* <div className="flex items-center">
            <Users className="w-5 h-5 mr-2" />
            <span className="font-medium">{project.faculty?.name || "Unknown Faculty"}</span>
          </div> */}
          <div className="flex items-center">
            <span className="text-sm">{new Date(project.deadline).toLocaleDateString()}</span>
          </div>
        </div>

        <div className="flex flex-wrap gap-2 mb-4">
          {project.technologies?.map((tech, index) => (
            <span
              key={index}
              className="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400 rounded"
            >
              {tech}
            </span>
          ))}
        </div>

        <button
          onClick={viewDetails}
          className="w-full flex items-center justify-center px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
        >
          View Details
          <ChevronRight className="w-4 h-4 ml-2" />
        </button>
      </div>
    </motion.div>
  );
};

export default FacultyProjectCard;