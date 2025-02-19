import { useState } from 'react';
import { ArrowLeft, X } from 'lucide-react';
import { motion } from 'framer-motion';
import Select from 'react-select';
import axios from 'axios';
import { useAuth } from '../../contexts/AuthContext';
import { table } from 'framer-motion/client';
import toast from 'react-hot-toast';

// Skill options with icons
const skillOptions = [
  { value: 'Java', label: 'Java', icon: '☕' },
  { value: 'Python', label: 'Python', icon: '🐍' },
  { value: 'Machine Learning', label: 'Machine Learning', icon: '🤖' },
  { value: 'React', label: 'React', icon: '⚛️' },
  { value: 'Node.js', label: 'Node.js', icon: '🌿' },
  { value: 'Spring Boot', label: 'Spring Boot', icon: '🍃' },
  { value: 'TensorFlow', label: 'TensorFlow', icon: '🧠' },
];

const initialState = {
  title: '',
  description: '',
  deadline: '',
  applicationDeadline: '',
  skills: [],
};


const AddProjectForm = ({ onBack }) => {
  const {user}=useAuth();
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

  const handleSubmit = async(e) => {
    e.preventDefault();
    try {
      const response = await  axios.post(`http://localhost:8765/FACULTY-SERVICE/api/project/${user. id}`,project);
      console.log('Project Added:', project);
      setProject(initialState);
      onBack();
    } catch (error) {
      console.log(error);
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
            className="w-full p-2 border rounded-lg"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 dark:text-white">Description</label>
          <textarea 
            value={project.description} 
            onChange={(e) => setProject(prev => ({ ...prev, description: e.target.value }))} 
            className="w-full p-2 border rounded-lg"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 dark:text-white">Deadline</label>
          <input 
            type="datetime-local" 
            value={project.deadline} 
            min={new Date().toISOString().slice(0, 16)}
            onChange={(e) => setProject(prev => ({ ...prev, deadline: e.target.value }))}  
            className="w-full p-2 border rounded-lg"
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
            className="w-full p-2 border rounded-lg"
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
            className="w-full"
            placeholder="Select skills..."
            getOptionLabel={(e) => (
              <div className="flex items-center">
                <span className="mr-2">{e.icon}</span> {e.label}
              </div>
            )}
            components={{ SingleValue: () => null }}
          />
        </div>

        <div className="flex flex-wrap gap-2 mt-2">
          {project.skills.map(skill => {
            const skillData = skillOptions.find(s => s.value === skill);
            return (
              <div key={skill} className="flex items-center bg-blue-100 text-blue-700 px-3 py-1 rounded-lg">
                <span className="mr-2">{skillData?.icon}</span> {skill}
                <button type="button" onClick={() => handleRemoveSkill(skill)} className="ml-2">
                  <X className="w-4 h-4 text-red-500" />
                </button>
              </div>
            );
          })}
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
