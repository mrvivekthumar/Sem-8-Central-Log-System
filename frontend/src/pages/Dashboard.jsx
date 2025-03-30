import { useState, useEffect } from 'react';
import axios from 'axios';
import { motion } from 'framer-motion';
import ProjectCard from '../components/ProjectCard';
import SearchBar from '../components/SearchBar';
import StudentProjectCard from '../components/StudentProjectCard';

const Dashboard = () => {
  const [projects, setProjects] = useState(null); // Initially null
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await axios.get('http://localhost:8765/STUDENT-SERVICE/students/project/visible');
        
        // Check if response data is empty or null
        if (!response.data || response.data.length === 0) {
          setProjects([]); // Set an empty array to show "No projects available"
        } else {
          setProjects(response.data);
        }
      } catch (error) {
        setError('Failed to fetch projects');
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, []);

  const filteredProjects = projects
    ? projects.filter((project) =>
        project.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        project.faculty.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        project.status.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : [];

  return (
    <div>
      <motion.h1
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8"
      >
        Available Projects
      </motion.h1>

      <SearchBar value={searchQuery} onChange={setSearchQuery} />

      {loading ? (
        <p>Loading...</p>
      ) : error ? (
        <p className="text-red-500">{error}</p>
      ) : filteredProjects.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProjects.map((project) => (
            <StudentProjectCard key={project.id} project={project} />
          ))}
        </div>
      ) : (
        // Display an image when no projects are available
        <div className="flex flex-col items-center col-span-full mt-10">
          {/* <img
            src="/assets/no_projects.jpeg" // Place this image inside 'public/assets'
            alt="No projects available"
            className="w-64 h-64"
          /> */}
          <p className="mt-4 text-lg text-gray-500">No projects available</p>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
