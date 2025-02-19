import { useState } from 'react';
import { Plus } from 'lucide-react';
import FacultyProjectList from './FacultyProjectList';
import AddProjectForm from './AddProjectForm'; // Import the new component
import { motion } from 'framer-motion';

const FacultyDashboard = () => {
  const [isAddingProject, setIsAddingProject] = useState(false);

  return (
    <div className="p-6">
      {isAddingProject ? (
        // Show Add Project Form when isAddingProject is true
        <AddProjectForm onBack={() => setIsAddingProject(false)} />
      ) : (
        // Show Dashboard when isAddingProject is false
        <>
          <div className="flex justify-between items-center mb-8">
            <motion.h1 
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              className="text-3xl font-bold text-gray-900 dark:text-white"
            >
              Faculty Dashboard
            </motion.h1>
            <motion.button 
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setIsAddingProject(true)} // Switch to Add Project Form
              className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Plus className="w-5 h-5 mr-2" />
              Add New Project
            </motion.button>
          </div>

          <FacultyProjectList />
        </>
      )}
    </div>
  );
};

export default FacultyDashboard;
