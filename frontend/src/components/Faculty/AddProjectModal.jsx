import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Plus, Minus } from 'lucide-react';
import toast from 'react-hot-toast';
import axiosInstance from '../../api/axiosInstance';

const AddProjectModal = ({ isOpen, onClose }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: 'OPEN_FOR_APPLICATIONS',
    date: new Date().toISOString().split("T")[0],
    deadline: '',
    applicationDeadline: '',
    faculty: '',
    maxStudents: 4,
    technologies: [''],
    projectType: 'Internal',
    budget: '',
    projectUrl: '',
  });

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      return () => {
        document.body.style.overflow = 'unset';
      };
    }
  }, [isOpen]);

  const handleSubmit = (e) => {
    e.preventDefault();
    toast.success('Project created successfully!');
    onClose();
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const addTechnology = () => {
    setFormData((prev) => ({
      ...prev,
      technologies: [...prev.technologies, ''],
    }));
  };

  const removeTechnology = (index) => {
    setFormData((prev) => ({
      ...prev,
      technologies: prev.technologies.filter((_, i) => i !== index),
    }));
  };

  const updateTechnology = (index, value) => {
    setFormData((prev) => ({
      ...prev,
      technologies: prev.technologies.map((tech, i) =>
        i === index ? value : tech
      ),
    }));
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <div
          className="fixed inset-0 z-50 overflow-y-auto flex items-center justify-center bg-black bg-opacity-50"
          onClick={onClose}
        >
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg max-w-2xl w-full relative"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={onClose}
              className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
            >
              <X className="w-6 h-6" />
            </button>

            <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
              Add New Project
            </h3>

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Title */}
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Title
                </label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border rounded-lg"
                  required
                />
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Description
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border rounded-lg"
                  required
                />
              </div>

              {/* Status Dropdown */}
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Status
                </label>
                <select
                  name="status"
                  value={formData.status}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border rounded-lg"
                >
                  <option value="OPEN_FOR_APPLICATIONS">Open for Applications</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                </select>
              </div>

              {/* Dates */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium">Start Date</label>
                  <input
                    type="date"
                    name="date"
                    value={formData.date}
                    onChange={handleChange}
                    className="w-full px-4 py-2 border rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium">Deadline</label>
                  <input
                    type="date"
                    name="deadline"
                    value={formData.deadline}
                    onChange={handleChange}
                    className="w-full px-4 py-2 border rounded-lg"
                  />
                </div>
              </div>

              {/* Faculty */}
              <div>
                <label className="block text-sm font-medium">Faculty</label>
                <input
                  type="text"
                  name="faculty"
                  value={formData.faculty}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border rounded-lg"
                />
              </div>

              {/* Max Students */}
              <div>
                <label className="block text-sm font-medium">Max Students</label>
                <input
                  type="number"
                  name="maxStudents"
                  value={formData.maxStudents}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border rounded-lg"
                />
              </div>

              {/* Technologies */}
              <div>
                <label className="block text-sm font-medium">Technologies</label>
                {formData.technologies.map((tech, index) => (
                  <div key={index} className="flex items-center space-x-2 mb-2">
                    <input
                      type="text"
                      value={tech}
                      onChange={(e) => updateTechnology(index, e.target.value)}
                      className="flex-grow px-4 py-2 border rounded-lg"
                    />
                    {index > 0 && (
                      <button type="button" onClick={() => removeTechnology(index)}>
                        <Minus className="w-5 h-5 text-red-500" />
                      </button>
                    )}
                  </div>
                ))}
                <button type="button" onClick={addTechnology} className="text-blue-600">
                  <Plus className="w-4 h-4 mr-1" /> Add Technology
                </button>
              </div>

              {/* Budget & Project URL */}
              <div>
                <label className="block text-sm font-medium">Budget</label>
                <input
                  type="number"
                  name="budget"
                  value={formData.budget}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium">Project URL</label>
                <input
                  type="text"
                  name="projectUrl"
                  value={formData.projectUrl}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border rounded-lg"
                />
              </div>

              {/* Submit Button */}
              <div className="flex justify-end">
                <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-lg">
                  Create Project
                </button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
};

export default AddProjectModal;
