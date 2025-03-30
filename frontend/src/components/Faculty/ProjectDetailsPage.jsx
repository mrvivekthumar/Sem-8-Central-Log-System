import { useParams, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { Users, Clock, ArrowLeft, Edit, Save, Trash2 } from "lucide-react";
import { useEffect, useState } from "react";
import axios from "axios";
import toast from 'react-hot-toast';
import Select from "react-select";

const ProjectDetailsPage = () => {
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editMode, setEditMode] = useState(false);
  const navigate = useNavigate();

  const skillOptions = [
    { value: 'Java', label: 'Java', icon: '☕' },
    { value: 'Python', label: 'Python', icon: '🐍' },
    { value: 'Machine Learning', label: 'Machine Learning', icon: '🤖' },
    { value: 'React', label: 'React', icon: '⚛️' },
    { value: 'Node.js', label: 'Node.js', icon: '🌿' },
    { value: 'Spring Boot', label: 'Spring Boot', icon: '🍃' },
    { value: 'TensorFlow', label: 'TensorFlow', icon: '🧠' },
  ];

  useEffect(() => {
    const fetchProject = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8765/FACULTY-SERVICE/api/project/${projectId}`
        );
        setProject(response.data);
      } catch (error) {
        console.error("Error fetching project:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchProject();
  }, [projectId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProject((prevProject) => ({
      ...prevProject,
      [name]: value,
    }));
  };

  const handleSkillChange = (selectedOptions) => {
    setProject((prevProject) => ({
      ...prevProject,
      skills: selectedOptions.map((option) => option.value),
    }));
  };

  const handleUpdate = async () => {
    try {
      await axios.put(
        `http://localhost:8765/FACULTY-SERVICE/api/project/${projectId}`,
        project
      );
      setEditMode(false);
      toast.success("Project updated successfully!");
    } catch (error) {
      console.error("Error updating project:", error);
      toast.error("Failed to update project.");
    }
  };
  const handleCancelProject = async () => {
    try {
      await axios.delete(`http://localhost:8765/FACULTY-SERVICE/api/faculty/project/${project.projectId}`)
      
      navigate("/faculty/dashboard");
      toast.success("Project Deleted Successfully");
    } catch (error) {
      console.error("Error canceling project:", error);
      toast.error("Failed to cancel project. Please try again.");
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <p className="text-lg font-semibold text-gray-700">Loading...</p>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="flex items-center justify-center h-screen">
        <p className="text-lg font-semibold text-red-600">Project not found.</p>
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="flex items-center justify-center h-screen bg-gray-100 dark:bg-gray-900 px-6"
    >
      <div className="max-w-4xl w-full bg-white dark:bg-gray-800 rounded-lg shadow-lg p-8">
        <div className="flex justify-between items-center mb-4">
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => navigate("/faculty/dashboard")}
            className="py-2 px-4 bg-gray-600 hover:bg-gray-700 text-white rounded-lg font-medium flex items-center"
          >
            <ArrowLeft className="h-5 w-5 mr-2" /> Back to Dashboard
          </motion.button>

          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => setEditMode(!editMode)}
            className="py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium flex items-center"
          >
            <Edit className="h-5 w-5 mr-2" /> {editMode ? "Cancel" : "Edit Project"}
          </motion.button>
        </div>

        <div className="mb-6">
          <label className="block text-gray-700 dark:text-gray-300 font-semibold">
            Project Title
          </label>
          {editMode ? (
            <input
              type="text"
              name="title"
              value={project.title || ""}
              onChange={handleChange}
              className="w-full p-2 bg-gray-100 dark:bg-gray-700 border rounded-lg"
            />
          ) : (
            <h1 className="text-3xl font-bold">{project.title}</h1>
          )}
        </div>
        <div className="mb-6">
          <label className="block text-gray-00 dark:text-gray-300 font-semibold">
            Project description
          </label>
          {editMode ? (
            <input
              type="text"
              name="descreption"
              value={project.description || ""}
              onChange={handleChange}
              className="w-full p-2 bg-gray-100 dark:bg-gray-700 border rounded-lg"
            />
          ) : (
            <h1 className="text-2xl">{project.description}</h1>
          )}
        </div>
        <div className="mb-6">
          <label className="block text-gray-700 dark:text-gray-300 font-semibold">
            Required Skills
          </label>
          {editMode ? (
            <Select
              isMulti
              options={skillOptions}
              value={skillOptions.filter((option) => project.skills?.includes(option.value))}
              onChange={handleSkillChange}
              className="w-full"
            />
          ) : (
            <div className="flex flex-wrap gap-2">
              {project.skills?.map((skill) => {
                const skillObj = skillOptions.find((option) => option.value === skill);
                return (
                  <span
                    key={skill}
                    className="flex items-center px-3 py-1 bg-blue-100 dark:bg-blue-800 text-blue-700 dark:text-blue-300 rounded-full"
                  >
                    <span className="mr-2">{skillObj?.icon}</span>
                    {skill}
                  </span>
                );
              })}
            </div>
          )}
        </div>

        <div className="mb-6">
          <label className="block text-gray-700 dark:text-gray-300 font-semibold">
            Application Deadline
          </label>
          {editMode ? (
            <input
              type="datetime-local"
              name="applicationDeadline"
              value={project.applicationDeadline || ""}
              onChange={handleChange}
              className="w-full p-2 bg-gray-100 dark:bg-gray-700 border rounded-lg"
            />
          ) : (
            <p>{new Date(project.applicationDeadline).toLocaleDateString()}</p>
          )}
        </div>

        <div className="mb-6">
          <label className="block text-gray-700 dark:text-gray-300 font-semibold">
            Deadline
          </label>
          {editMode ? (
            <input
              type="datetime-local"
              name="applicationDeadline"
              value={project.deadline || ""}
              onChange={handleChange}
              className="w-full p-2 bg-gray-100 dark:bg-gray-700 border rounded-lg"
            />
          ) : (
            <p>{new Date(project.deadline).toLocaleDateString()}</p>
          )}
        </div>

        {editMode && (
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={handleUpdate}
            className="mt-4 py-2 px-4 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium flex items-center"
          >
            <Save className="h-5 w-5 mr-2" /> Save Changes
          </motion.button>
        )}
        {!editMode && (
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={handleCancelProject}
            className="py-2 px-4 bg-red-600 hover:bg-red-700 text-white rounded-lg font-medium flex items-center"
          >
            <Trash2 className="h-5 w-5 mr-2" /> Cancel Project
          </motion.button>
        )}
      </div>
    </motion.div>
  );
};

export default ProjectDetailsPage;