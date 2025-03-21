import axios from 'axios';
import { motion } from 'framer-motion';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const ConfirmedProjects = () => {
  const [projects, setProjects] = useState([]);
  const navigate = useNavigate();
  const { user } = useAuth();
  const [list, setList] = useState([]);

  useEffect(() => {
    const fetchAppliedProjects = async () => {
      try {
        const response = await axios.get(`http://localhost:8765/STUDENT-SERVICE/api/studentProject/appliedProjects`);
        setList(response.data);
        console.log("Applied Projects List:", response.data);
      } catch (error) {
        console.error("Error fetching applied projects:", error);
      }
    };
    fetchAppliedProjects();
  }, []);

  useEffect(() => {
    if (!user?.id || list.length === 0) return;

    const fetchProjects = async () => {
      try {
        const response = await axios.get('http://localhost:8765/FACULTY-SERVICE/api/project/byFaculty', {
          params: { facultyId: user.id, projectIds: list.join(',') },
        });

        setProjects(response.data);
        console.log("Faculty Projects:", response.data);
      } catch (error) {
        console.error("Error fetching faculty projects:", error);
      }
    };

    fetchProjects();
  }, [user, list]);

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 p-4">
      {projects.length > 0 ? (
        projects
          .filter((project) => project.status !== "APPROVED")
          .map((project) => (
            <motion.div
              key={project.projectId}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              whileHover={{ scale: 1.05, boxShadow: "0px 8px 20px rgba(0, 122, 255, 0.2)" }}
              transition={{ duration: 0.3 }}
              className="relative bg-white border border-gray-200 shadow-md rounded-xl 
                      hover:shadow-lg transform transition-all cursor-pointer"
              onClick={() => navigate(`/application/project/${project.projectId}`)}
            >
              <div className="p-6">
                <h3 className="text-lg font-semibold text-blue-600">{project.title}</h3>
                <p className="text-sm text-gray-500">Click to view details</p>
              </div>
              <div className="absolute bottom-0 left-0 w-full h-1 bg-blue-500"></div>
            </motion.div>
          ))
      ) : (
        <motion.div 
          initial={{ opacity: 0, y: 20 }} 
          animate={{ opacity: 1, y: 0 }} 
          className="flex flex-col items-center justify-center p-6 bg-white border border-gray-200 rounded-lg shadow-md"
        >
          <p className="text-lg text-gray-500">No applications available.</p>
        </motion.div>
      )}
    </div>
  );
};

export default ConfirmedProjects;
