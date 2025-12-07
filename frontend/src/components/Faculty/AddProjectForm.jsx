import { AnimatePresence, motion } from 'framer-motion';
import {
  AlertCircle, CheckCircle,
  Code,
  FileText,
  Save,
  Sparkles,
  Target,
  X
} from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import { BsRobot } from 'react-icons/bs';
import {
  SiAmazon,
  SiAndroid,
  SiCplusplus,
  SiDocker,
  SiFigma,
  SiGooglecloud,
  SiJavascript,
  SiKubernetes, SiMongodb, SiMysql,
  SiNodedotjs,
  SiPostgresql,
  SiPython, SiReact,
  SiSpring, SiTensorflow,
  SiTypescript
} from 'react-icons/si';
import Select from 'react-select';
import axiosInstance from '../../api/axiosInstance';
import { useAuth } from '../../contexts/AuthContext';

const skillOptions = [
  { value: 'Java', label: 'Java', icon: <Code className="w-5 h-5 text-red-500" /> },
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
  { value: 'Docker', label: 'Docker', icon: <SiDocker className="w-5 h-5 text-blue-500" /> },
  { value: 'Kubernetes', label: 'Kubernetes', icon: <SiKubernetes className="w-5 h-5 text-blue-600" /> },
  { value: 'AWS', label: 'AWS', icon: <SiAmazon className="w-5 h-5 text-orange-500" /> },
  { value: 'MongoDB', label: 'MongoDB', icon: <SiMongodb className="w-5 h-5 text-green-500" /> },
  { value: 'MySQL', label: 'MySQL', icon: <SiMysql className="w-5 h-5 text-blue-500" /> },
  { value: 'PostgreSQL', label: 'PostgreSQL', icon: <SiPostgresql className="w-5 h-5 text-blue-400" /> },
  { value: 'UI/UX Design', label: 'UI/UX Design', icon: <SiFigma className="w-5 h-5 text-pink-500" /> },
  { value: 'Mobile Development', label: 'Mobile Development', icon: <SiAndroid className="w-5 h-5 text-green-600" /> },
];

const domainOptions = [
  'Web Development',
  'Mobile Development',
  'Machine Learning',
  'Artificial Intelligence',
  'Data Science',
  'Cloud Computing',
  'DevOps',
  'Blockchain',
  'IoT',
  'Cybersecurity'
];

const AddProjectForm = ({ onClose }) => {
  const { user } = useAuth();
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    domain: '',
    deadline: '',
    applicationDeadline: '',
    technologiesUsed: [],
    maxStudents: 5,
    teamSize: 3,
    duration: ''
  });

  const [errors, setErrors] = useState({});

  const validateStep = (step) => {
    const newErrors = {};

    if (step === 1) {
      if (!formData.title.trim()) newErrors.title = 'Project title is required';
      if (!formData.description.trim()) newErrors.description = 'Description is required';
      if (!formData.domain) newErrors.domain = 'Domain is required';
    }

    if (step === 2) {
      if (formData.technologiesUsed.length === 0) {
        newErrors.technologiesUsed = 'At least one technology is required';
      }
      if (!formData.duration) newErrors.duration = 'Duration is required';
    }

    if (step === 3) {
      if (!formData.deadline) newErrors.deadline = 'Project deadline is required';
      if (!formData.applicationDeadline) newErrors.applicationDeadline = 'Application deadline is required';
      if (!formData.maxStudents || formData.maxStudents < 1) {
        newErrors.maxStudents = 'Max students must be at least 1';
      }
      if (!formData.teamSize || formData.teamSize < 1) {
        newErrors.teamSize = 'Team size must be at least 1';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = () => {
    if (validateStep(currentStep)) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handleBack = () => {
    setCurrentStep(currentStep - 1);
    setErrors({});
  };

  const handleSkillChange = (selectedOptions) => {
    setFormData(prev => ({
      ...prev,
      technologiesUsed: selectedOptions ? selectedOptions.map(option => option.value) : []
    }));
  };

  const handleRemoveSkill = (skill) => {
    setFormData(prev => ({
      ...prev,
      technologiesUsed: prev.technologiesUsed.filter(s => s !== skill)
    }));
  };

  const handleSubmit = async () => {
    if (!validateStep(3)) return;

    try {
      setLoading(true);
      await axiosInstance.post(`/FACULTY-SERVICE/api/project/${user.id}`, formData);
      toast.success('Project created successfully!');
      onClose();
    } catch (error) {
      console.error('Error creating project:', error);
      toast.error('Failed to create project');
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    { number: 1, title: 'Basic Info', icon: FileText },
    { number: 2, title: 'Technologies', icon: Code },
    { number: 3, title: 'Details', icon: Target }
  ];

  return (
    <div className="relative">
      {/* Progress Bar */}
      <div className="mb-8">
        <div className="flex items-center justify-between">
          {steps.map((step, index) => {
            const StepIcon = step.icon;
            const isActive = currentStep === step.number;
            const isCompleted = currentStep > step.number;

            return (
              <div key={step.number} className="flex-1 flex items-center">
                <div className="flex flex-col items-center flex-1">
                  <motion.div
                    whileHover={{ scale: 1.1 }}
                    className={`w-12 h-12 rounded-full flex items-center justify-center border-2 transition-all ${isActive
                      ? 'bg-blue-600 border-blue-600 text-white'
                      : isCompleted
                        ? 'bg-green-600 border-green-600 text-white'
                        : 'bg-gray-100 dark:bg-gray-700 border-gray-300 dark:border-gray-600 text-gray-400'
                      }`}
                  >
                    {isCompleted ? (
                      <CheckCircle className="w-6 h-6" />
                    ) : (
                      <StepIcon className="w-6 h-6" />
                    )}
                  </motion.div>
                  <span className={`text-xs font-medium mt-2 ${isActive ? 'text-blue-600 dark:text-blue-400' : 'text-gray-500 dark:text-gray-400'
                    }`}>
                    {step.title}
                  </span>
                </div>
                {index < steps.length - 1 && (
                  <div className={`flex-1 h-0.5 mx-2 transition-colors ${currentStep > step.number ? 'bg-green-600' : 'bg-gray-300 dark:bg-gray-600'
                    }`} />
                )}
              </div>
            );
          })}
        </div>
      </div>

      {/* Form Content */}
      <AnimatePresence mode="wait">
        <motion.div
          key={currentStep}
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: -20 }}
          transition={{ duration: 0.3 }}
        >
          {/* Step 1: Basic Information */}
          {currentStep === 1 && (
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                  Project Title *
                </label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="Enter an engaging project title"
                  className={`w-full px-4 py-3 rounded-xl border-2 ${errors.title
                    ? 'border-red-500 focus:border-red-500'
                    : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                    } bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none transition-colors`}
                />
                {errors.title && (
                  <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                    <AlertCircle className="w-4 h-4" />
                    {errors.title}
                  </p>
                )}
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                  Domain *
                </label>
                <select
                  value={formData.domain}
                  onChange={(e) => setFormData({ ...formData, domain: e.target.value })}
                  className={`w-full px-4 py-3 rounded-xl border-2 ${errors.domain
                    ? 'border-red-500 focus:border-red-500'
                    : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                    } bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none transition-colors`}
                >
                  <option value="">Select a domain</option>
                  {domainOptions.map(domain => (
                    <option key={domain} value={domain}>{domain}</option>
                  ))}
                </select>
                {errors.domain && (
                  <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                    <AlertCircle className="w-4 h-4" />
                    {errors.domain}
                  </p>
                )}
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                  Description *
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Describe your project in detail..."
                  rows={6}
                  className={`w-full px-4 py-3 rounded-xl border-2 ${errors.description
                    ? 'border-red-500 focus:border-red-500'
                    : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                    } bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none transition-colors resize-none`}
                />
                {errors.description && (
                  <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                    <AlertCircle className="w-4 h-4" />
                    {errors.description}
                  </p>
                )}
              </div>
            </div>
          )}

          {/* Step 2: Technologies */}
          {currentStep === 2 && (
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                  Required Technologies & Skills *
                </label>
                <Select
                  options={skillOptions}
                  isMulti
                  value={skillOptions.filter(option => formData.technologiesUsed.includes(option.value))}
                  onChange={handleSkillChange}
                  className="react-select-container"
                  classNamePrefix="react-select"
                  placeholder="Select technologies..."
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
                {errors.technologiesUsed && (
                  <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                    <AlertCircle className="w-4 h-4" />
                    {errors.technologiesUsed}
                  </p>
                )}
              </div>

              {/* Selected Skills Display */}
              {formData.technologiesUsed.length > 0 && (
                <div>
                  <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">
                    Selected Technologies ({formData.technologiesUsed.length})
                  </label>
                  <div className="flex flex-wrap gap-3">
                    {formData.technologiesUsed.map((skill) => {
                      const skillData = skillOptions.find(s => s.value === skill);
                      return (
                        <motion.div
                          key={skill}
                          initial={{ opacity: 0, scale: 0.8 }}
                          animate={{ opacity: 1, scale: 1 }}
                          exit={{ opacity: 0, scale: 0.8 }}
                          className="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 border border-blue-200 dark:border-blue-800 rounded-xl group"
                        >
                          {skillData?.icon}
                          <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                            {skill}
                          </span>
                          <button
                            type="button"
                            onClick={() => handleRemoveSkill(skill)}
                            className="ml-1 text-red-500 hover:text-red-700 opacity-0 group-hover:opacity-100 transition-opacity"
                          >
                            <X className="w-4 h-4" />
                          </button>
                        </motion.div>
                      );
                    })}
                  </div>
                </div>
              )}

              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                  Project Duration *
                </label>
                <input
                  type="text"
                  value={formData.duration}
                  onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                  placeholder="e.g., 3 months, 6 weeks"
                  className={`w-full px-4 py-3 rounded-xl border-2 ${errors.duration
                    ? 'border-red-500 focus:border-red-500'
                    : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                    } bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none transition-colors`}
                />
                {errors.duration && (
                  <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                    <AlertCircle className="w-4 h-4" />
                    {errors.duration}
                  </p>
                )}
              </div>
            </div>
          )}

          {/* Step 3: Details */}
          {currentStep === 3 && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                    Team Size *
                  </label>
                  <input
                    type="number"
                    min="1"
                    value={formData.teamSize}
                    onChange={(e) => setFormData({ ...formData, teamSize: parseInt(e.target.value) })}
                    className={`w-full px-4 py-3 rounded-xl border-2 ${errors.teamSize
                      ? 'border-red-500 focus:border-red-500'
                      : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                      } bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none transition-colors`}
                  />
                  {errors.teamSize && (
                    <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                      <AlertCircle className="w-4 h-4" />
                      {errors.teamSize}
                    </p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                    Max Students *
                  </label>
                  <input
                    type="number"
                    min="1"
                    value={formData.maxStudents}
                    onChange={(e) => setFormData({ ...formData, maxStudents: parseInt(e.target.value) })}
                    className={`w-full px-4 py-3 rounded-xl border-2 ${errors.maxStudents
                      ? 'border-red-500 focus:border-red-500'
                      : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                      } bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none transition-colors`}
                  />
                  {errors.maxStudents && (
                    <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                      <AlertCircle className="w-4 h-4" />
                      {errors.maxStudents}
                    </p>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                  Application Deadline *
                </label>
                <input
                  type="datetime-local"
                  value={formData.applicationDeadline}
                  min={new Date().toISOString().slice(0, 16)}
                  onChange={(e) => setFormData({ ...formData, applicationDeadline: e.target.value })}
                  className={`w-full px-4 py-3 rounded-xl border-2 ${errors.applicationDeadline
                    ? 'border-red-500 focus:border-red-500'
                    : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                    } bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none transition-colors`}
                />
                {errors.applicationDeadline && (
                  <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                    <AlertCircle className="w-4 h-4" />
                    {errors.applicationDeadline}
                  </p>
                )}
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
                  Project Deadline *
                </label>
                <input
                  type="datetime-local"
                  value={formData.deadline}
                  min={new Date().toISOString().slice(0, 16)}
                  onChange={(e) => setFormData({ ...formData, deadline: e.target.value })}
                  className={`w-full px-4 py-3 rounded-xl border-2 ${errors.deadline
                    ? 'border-red-500 focus:border-red-500'
                    : 'border-gray-300 dark:border-gray-600 focus:border-blue-500'
                    } bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none transition-colors`}
                />
                {errors.deadline && (
                  <p className="mt-1 text-sm text-red-500 flex items-center gap-1">
                    <AlertCircle className="w-4 h-4" />
                    {errors.deadline}
                  </p>
                )}
              </div>
            </div>
          )}
        </motion.div>
      </AnimatePresence>

      {/* Navigation Buttons */}
      <div className="flex items-center justify-between mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
        <div>
          {currentStep > 1 && (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={handleBack}
              className="px-6 py-3 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
            >
              Back
            </motion.button>
          )}
        </div>

        <div className="flex gap-3">
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={onClose}
            className="px-6 py-3 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors flex items-center gap-2"
          >
            <X className="w-5 h-5" />
            Cancel
          </motion.button>

          {currentStep < 3 ? (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={handleNext}
              className="px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-colors flex items-center gap-2"
            >
              Next
              <Sparkles className="w-5 h-5" />
            </motion.button>
          ) : (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={handleSubmit}
              disabled={loading}
              className="px-6 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl font-semibold hover:from-green-700 hover:to-emerald-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 shadow-lg"
            >
              {loading ? (
                <>
                  <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                    className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                  />
                  Creating...
                </>
              ) : (
                <>
                  <Save className="w-5 h-5" />
                  Create Project
                </>
              )}
            </motion.button>
          )}
        </div>
      </div>
    </div>
  );
};

export default AddProjectForm;
