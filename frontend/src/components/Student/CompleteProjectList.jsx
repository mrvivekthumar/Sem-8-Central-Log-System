import { motion } from 'framer-motion';

import { useAuth } from '../../contexts/AuthContext';
import { useState, useEffect } from 'react';
import axios from 'axios';
import CompletedProjectCard from './CompleteProjectCard';
import axiosInstance from '../../api/axiosInstance';
const CompletedProjectList = () => {
  const [projects, setProjects] = useState([]);
  const { user } = useAuth();

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        // Step 1: Fetch Project IDs
        const idsResponse = await axiosInstance.get(
          `/STUDENT-SERVICE/students/${user.id}/completed-projects`
        );
        const projectIds = idsResponse.data;
        if (!projectIds.length) return;

        // Step 2: Fetch Projects by IDs
        const projectsResponse = await axiosInstance.post(
          "/FACULTY-SERVICE/api/faculty/projectsbyIds",
          projectIds,
          { headers: { "Content-Type": "application/json" } }
        );

        // Step 3: Set Projects State
        setProjects(projectsResponse.data);
      } catch (error) {
        console.error("Error fetching completed projects:", error);
      }
    };

    if (user?.id) {
      fetchProjects();
    }
  }, [user]);

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {projects && projects.length > 0 ? (
        projects.map((project, index) => (
          <motion.div
            key={project.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <CompletedProjectCard project={project} />
          </motion.div>
        ))
      ) : (
        <div className="flex flex-col items-center col-span-full mt-10">
          <p className="mt-4 text-lg text-gray-500">No completed projects available</p>
        </div>
      )}
    </div>
  );
};

export default CompletedProjectList;
