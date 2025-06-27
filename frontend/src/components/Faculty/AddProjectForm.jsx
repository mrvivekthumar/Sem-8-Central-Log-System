import { useState } from 'react';
import { ArrowLeft, X } from 'lucide-react';
import { motion } from 'framer-motion';
import Select from 'react-select';
import axios from 'axios';
import { useAuth } from '../../contexts/AuthContext';
import toast from 'react-hot-toast';
import {
  SiPython,
  SiReact,
  SiNodedotjs,
  SiSpring,
  SiTensorflow,
  SiJavascript,
  SiTypescript,
  SiCplusplus,
  SiGooglecloud,
  SiDevpost,
  SiDatabricks,
  SiFigma,
  SiAndroid,
  SiDocker,
  SiKubernetes,
  SiMongodb,
  SiMysql,
  SiPostgresql
} from 'react-icons/si';
import { BsRobot } from 'react-icons/bs';
import axiosInstance from '../../api/axiosInstance';

// Enhanced skill options with React Icons
const skillOptions = [
  { value: 'Java', label: 'Java', icon:'☕' },
  { value: 'Python', label: 'Python', icon: <SiPython className="w-5 h-5 text-blue-400" /> },
  { value: 'Machine Learning', label: 'Machine Learning', icon: <BsRobot className="w-5 h-5 text-green-500" /> },
  { value: 'React', label: 'React', icon: <SiReact className="w-5 h-5 text-blue-500" /> },
  { value: 'Node.js', label: 'Node.js', icon: <SiNodedotjs className="w-5 h-5 text-green-500" /> },
  { value: 'Spring Boot', label: 'Spring Boot', icon: <SiSpring className="w-5 h-5 text-green-600" /> },
  { value: 'TensorFlow', label: 'TensorFlow', icon: <SiTensorflow className="w-5 h-5 text-orange-500" /> },
  { value: 'JavaScript', label: 'JavaScript', icon: <SiJavascript className="w-5 h-5 text-yellow-500" /> },
  { value: 'TypeScript', label: 'TypeScript', icon: <SiTypescript className="w-5 h-5 text-blue-600" /> },
  { value: 'C++', label: 'C++', icon: <SiCplusplus className="w-5 h-5 text-blue-700" /> },
  { value: 'Cloud Computing', label: 'Cloud Computing', icon: <SiGooglecloud className="w-5 h-5 text-blue-400" /> },
  { value: 'DevOps', label: 'DevOps', icon: <SiDevpost className="w-5 h-5 text-purple-500" /> },
  { value: 'Data Science', label: 'Data Science', icon: <SiDatabricks className="w-5 h-5 text-red-600" /> },
  { value: 'UI/UX Design', label: 'UI/UX Design', icon: <SiFigma className="w-5 h-5 text-pink-500" /> },
  { value: 'Mobile Development', label: 'Mobile Development', icon: <SiAndroid className="w-5 h-5 text-green-600" /> },
  { value: 'Docker', label: 'Docker', icon: <SiDocker className="w-5 h-5 text-blue-500" /> },
  { value: 'Kubernetes', label: 'Kubernetes', icon: <SiKubernetes className="w-5 h-5 text-blue-600" /> },
  // { value: 'AWS', label: 'AWS', icon: <SiAmazonaws className="w-5 h-5 text-orange-500" /> },
  { value: 'MongoDB', label: 'MongoDB', icon: <SiMongodb className="w-5 h-5 text-green-500" /> },
  { value: 'MySQL', label: 'MySQL', icon: <SiMysql className="w-5 h-5 text-blue-500" /> },
  { value: 'PostgreSQL', label: 'PostgreSQL', icon: <SiPostgresql className="w-5 h-5 text-blue-400" /> },
];

const initialState = {
  title: '',
  description: '',
  deadline: '',
  applicationDeadline: '',
  skills: [],
  maxStudents: 1
};

const AddProjectForm = ({ onBack }) => {
  const { user } = useAuth();
  const [project, setProject] = useState(initialState);

  const handleSkillChange = (selectedOptions) => {
    setProject(prev => ({
      ...prev,
      skills: selectedOptions ? selectedOptions.map(option => option.value) : []
    }));
  };

  const handleRemoveSkill = (skill) => {
    setProject(prev => ({
      ...prev,
      skills: prev.skills.filter(s => s !== skill)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axiosInstance.post(`/FACULTY-SERVICE/api/project/${user.id}`, project);
      toast.success('Project created successfully!');
      setProject(initialState);
      onBack();
      
      
    } catch (error) {
      toast.error('Failed to create project');
      console.error('Error creating project:', error);
    }
  };

  return (
    <div className="p-6">
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={onBack}
        className="flex items-center px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors mb-4"
      >
        <ArrowLeft className="w-5 h-5 mr-2" />
        Back to Dashboard
      </motion.button>

      <motion.h2
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-2xl font-bold mb-4 text-gray-900 dark:text-white"
      >
        Add New Project
      </motion.h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-gray-700 dark:text-white">Project Name</label>
          <input
            type="text"
            value={project.title}
            onChange={(e) => setProject(prev => ({ ...prev, title: e.target.value }))}
            className="w-full p-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 dark:text-white">Description</label>
          <textarea
            value={project.description}
            onChange={(e) => setProject(prev => ({ ...prev, description: e.target.value }))}
            className="w-full p-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            rows={4}
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 dark:text-white">Project Deadline</label>
          <input
            type="datetime-local"
            value={project.deadline}
            min={new Date().toISOString().slice(0, 16)}
            onChange={(e) => setProject(prev => ({ ...prev, deadline: e.target.value }))}
            className="w-full p-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 dark:text-white">Application Deadline</label>
          <input
            type="datetime-local"
            value={project.applicationDeadline}
            min={new Date().toISOString().slice(0, 16)}
            onChange={(e) => setProject(prev => ({ ...prev, applicationDeadline: e.target.value }))}
            className="w-full p-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 dark:text-white">Required Skills</label>
          <Select
            options={skillOptions}
            isMulti
            value={skillOptions.filter(option => project.skills.includes(option.value))}
            onChange={handleSkillChange}
            className="react-select-container"
            classNamePrefix="react-select"
            placeholder="Select skills..."
            getOptionLabel={(option) => (
              <div className="flex items-center gap-2">
                {option.icon}
                <span>{option.label}</span>
              </div>
            )}
            components={{
              SingleValue: ({ data }) => (
                <div className="flex items-center gap-2">
                  {data.icon}
                  <span>{data.label}</span>
                </div>
              ),
            }}
          />
        </div>

        <div className="flex flex-wrap gap-2 mt-2">
          {project.skills.map(skill => {
            const skillData = skillOptions.find(s => s.value === skill);
            return (
              <div key={skill} className="flex items-center bg-blue-100 text-blue-700 px-3 py-1 rounded-lg">
                {skillData?.icon}
                <span className="ml-2">{skill}</span>
                <button
                  type="button"
                  onClick={() => handleRemoveSkill(skill)}
                  className="ml-2 hover:text-red-500"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            );
          })}
        </div>

        <div>
          <label className="block text-gray-700 dark:text-white">Maximum Students</label>
          <input
            type="number"
            min={1}
            value={project.maxStudents}
            onChange={(e) => setProject(prev => ({ ...prev, maxStudents: parseInt(e.target.value) }))}
            className="w-full p-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            required
          />
        </div>

        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          type="submit"
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Submit Project
        </motion.button>
      </form>
    </div>
  );
};

export default AddProjectForm;