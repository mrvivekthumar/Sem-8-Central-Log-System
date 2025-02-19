import axios from 'axios';
import { motion } from 'framer-motion';
import { Users, Clock, MessageSquare } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import StudentDetail from './StudentDetail';

const ConfirmedProjects = () => {
  const [projects, setProjects] = useState([]);
  const navigate = useNavigate();
  const { user } = useAuth();
  const [list,setList]=useState([]);
  const [studentsMap, setStudentsMap] = useState({});

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

  // Second useEffect: Fetch Faculty Projects (Runs after list is populated)
  useEffect(() => {
    if (!user?.id || list.length === 0) return; // Only run if user.id exists and list is not empty

    const fetchProjects = async () => {
      try {
        const response = await axios.get('http://localhost:8765/FACULTY-SERVICE/api/project/byFaculty', {
          params: {
            facultyId: user.id,
            projectIds: list.join(','), // Convert array to comma-separated string
          },
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
    <div>
      <motion.h1
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8"
      >
        Applications
      </motion.h1>

      <div className="space-y-6">
        {projects.map((project) => (
          <motion.div
            key={project.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden cursor-pointer"
            onClick={() => navigate(`/application/project/${project.projectId}`)}
          >
            <div className="p-6">
              <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
                {project.title}
              </h3>
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
};

export default ConfirmedProjects;