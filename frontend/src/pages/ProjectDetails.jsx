import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Users, Clock, Send, ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';
import axios from 'axios';
import { useAuth } from '../contexts/AuthContext';

const ProjectDetails = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [project, setProject] = useState(null);
  const [isApplying, setIsApplying] = useState(false);
  const [hasApplied, setHasApplied] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [timeRemaining, setTimeRemaining] = useState('');
  const [studentCount,setStudentCount]=useState(0);
  const {user}=useAuth();
  useEffect(() => {
    if (!project) return;

    const fetchStudentCount = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8765/FACULTY-SERVICE/api/project/${projectId}/student-count`
        );
        setStudentCount(response.data);
      } catch (error) {
        console.error('Error fetching student count:', error);
      }
    };

  
    fetchStudentCount();
  }, [project]);
  useEffect(() => {
    if (!project || !project.applicationDeadline) return;
  
    const appdeadline = new Date(project.applicationDeadline).getTime();
    console.log(project.applicationDeadline)
    const updateTimer = () => {
      const now = new Date().getTime();
      const timeDiff = appdeadline - now;
  
      if (timeDiff <= 0) {
        setTimeRemaining('Application deadline has passed');
        clearInterval(timerInterval);
        return;
      }
  
      const totalHours = Math.floor(timeDiff / (1000 * 60 * 60)); // Get total remaining hours
      const minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60)); // Remaining minutes
      const seconds = Math.floor((timeDiff % (1000 * 60)) / 1000); // Remaining seconds
  
      setTimeRemaining(`${totalHours}h ${minutes}m ${seconds}s remaining`);
    };
  
    updateTimer();
    const timerInterval = setInterval(updateTimer, 1000);
  
    return () => clearInterval(timerInterval);
  }, [project]); 
  useEffect(() => {
    const checkApplicationStatus = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8765/STUDENT-SERVICE/api/studentProject/${user.id}/project/${projectId}/status`
        );
        console.log(response.data);
        setHasApplied(response.data); // Assuming API returns { applied: true/false }
      } catch (error) {
        console.error("Failed to fetch application status:", error);
      }
    };

    if (user?.id && projectId) {
      checkApplicationStatus();
    }
  }, [user?.id, projectId]); // Runs when user or projectId changes
  
  const skillOptions = {
    Java: { label: 'Java', icon: '☕' },
    Python: { label: 'Python', icon: '🐍' },
    "Machine Learning": { label: 'Machine Learning', icon: '🤖' },
    React: { label: 'React', icon: '⚛️' },
    "Node.js": { label: 'Node.js', icon: '🌿' },
    "Spring Boot": { label: 'Spring Boot', icon: '🍃' },
    TensorFlow: { label: 'TensorFlow', icon: '🧠' },
  };

  useEffect(() => {
    const fetchProject = async () => {
      try {
        const response = await axios.get(`http://localhost:8765/FACULTY-SERVICE/api/project/${projectId}`);
        setProject(response.data);
      } catch (err) {
        setError('Failed to load project details');
      } finally {
        setLoading(false);
      }
    };
    fetchProject();
  }, [projectId]);

  if (loading) return <p>Loading...</p>;
  if (error) return <p className="text-red-500">{error}</p>;
  if (!project) return null;
  const handleApplying = () => {
    if (hasApplied) return; // Prevent duplicate applications
  
    setIsApplying(true);
    axios
      .post(`http://localhost:8765/STUDENT-SERVICE/students/apply/${user.id}/project/${projectId}`)
      .then(() => {
        toast.success("Application submitted successfully");
        setHasApplied(true); // Set applied status
      })
      .catch(() => {
        toast.error("Failed to submit application");
      })
      .finally(() => {
        setIsApplying(false);
      });
  };
  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="max-w-4xl mx-auto">
      <motion.button
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
        onClick={() => navigate("/student/dashboard")}
        className="py-2 px-4 bg-gray-600 hover:bg-gray-700 text-white rounded-lg font-medium flex items-center"
      >
        <ArrowLeft className="h-5 w-5 mr-2" /> Back to Dashboard
      </motion.button>
      <br />
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-8">
        <div className="flex justify-between items-start mb-6">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">{project.title}</h1>
          <span className={`px-4 py-2 rounded-full text-sm font-medium ${project.status === 'OPEN_FOR_APPLICATIONS' ? 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400' : 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400'}`}>{project.status}</span>
        </div>
        <div className="flex items-center space-x-6 mb-6 text-gray-600 dark:text-gray-300">
          <div className="flex items-center">
            <Users className="h-5 w-5 mr-2" />
            <span>Faculty: {project.faculty.name}</span>
          </div>
          <div className="flex items-center">
            <Clock className="h-5 w-5 mr-2" />
            <span>Deadline: {new Date(project.deadline).toLocaleDateString()}</span>
          </div>
        </div>
        <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-2">Project Descreption</h3>
        <p className="text-gray-600 dark:text-gray-300 mb-6">{project.description}</p>

        {/* Display Required Skills */}
        <div className="mb-6">
          <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-2">Required Skills</h3>
          <div className="flex flex-wrap gap-2">
            {(project.skills|| []).map((skill, index) => (
              <span
                key={index}
                className="flex items-center bg-blue-100 dark:bg-blue-900/20 text-blue-800 dark:text-blue-400 px-3 py-1 rounded-lg text-sm font-medium"
              >
                {skillOptions[skill]?.icon} {skillOptions[skill]?.label || skill}
              </span>
            ))}
          </div>
        </div>
        <div className="mb-6">
          <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-2"> Time Remaining for apply </h3>
          <div className="flex flex-wrap gap-2">
          <p className="text-red-500 font-semibold">{timeRemaining}</p>
          </div>
        </div>
        <div className="mb-6">
          <div className="flex items-center text-sm text-gray-500 dark:text-gray-400 mb-4">
          <Users className="w-5 h-5 mr-2" />
          <span>{studentCount} students enrolled</span>
        </div>
        </div>
        <motion.button
  whileHover={!hasApplied ? { scale: 1.02 } : {}}
  whileTap={!hasApplied ? { scale: 0.98 } : {}}
  disabled={hasApplied || isApplying}
  onClick={handleApplying}
  className={`w-full py-3 text-white rounded-lg font-medium flex items-center justify-center 
    ${hasApplied ? "bg-gray-500 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"}
    disabled:opacity-50 disabled:cursor-not-allowed`}
>
  {hasApplied ? "Applied ✅" : isApplying ? "Submitting..." : <><Send className="h-5 w-5 mr-2" /> Apply for Project</>}
</motion.button>

      </div>
    </motion.div>
  );
};

export default ProjectDetails;
