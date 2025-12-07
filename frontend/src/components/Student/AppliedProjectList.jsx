import { AnimatePresence, motion, Reorder } from 'framer-motion';
import {
  ArrowUp,
  GripVertical, Info,
  RefreshCw,
  Sparkles, TrendingUp
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import axiosInstance from '../../api/axiosInstance';
import { useAuth } from '../../contexts/AuthContext';
import AppliedProjectCard from './AppliedProjectCard';

const AppliedProjectList = () => {
  const [projects, setProjects] = useState([]);
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [draggedProject, setDraggedProject] = useState(null);
  const [isReordering, setIsReordering] = useState(false);

  useEffect(() => {
    if (user?.id) {
      fetchProjects();
    }
  }, [user]);

  const fetchProjects = async () => {
    setLoading(true);
    try {
      // Step 1: Get the applied project IDs with preferences
      const responseIds = await axiosInstance.get(
        `/STUDENT-SERVICE/api/studentProject/projectIdsByPref/${user.id}`
      );

      if (!responseIds.data || responseIds.data.length === 0) {
        setProjects([]);
        setLoading(false);
        return;
      }

      // Step 2: Get project details from faculty service
      const response = await axiosInstance.post(
        '/FACULTY-SERVICE/api/faculty/projectsbyIds',
        responseIds.data,
        { headers: { 'Content-Type': 'application/json' } }
      );

      // Step 3: Reorder projects to match responseIds.data order
      const orderedProjects = responseIds.data
        .map(id => response.data.find(project => project.projectId === id))
        .filter(project => project !== undefined);

      setProjects(orderedProjects);
    } catch (error) {
      console.error('Error fetching projects:', error);
      toast.error('Failed to load applied projects');
    } finally {
      setLoading(false);
    }
  };

  const updatePreference = async (projectId, newPreference) => {
    const projectIndex = projects.findIndex(p => p.projectId === projectId);
    if (projectIndex === -1) return;

    const currentPreference = projectIndex + 1;
    if (newPreference === currentPreference) return;

    // Create a copy of projects array
    const newProjects = [...projects];
    const [movedProject] = newProjects.splice(projectIndex, 1);
    newProjects.splice(newPreference - 1, 0, movedProject);

    // Optimistically update UI
    setProjects(newProjects);
    setIsReordering(true);

    try {
      // Update the moved project's preference
      await axiosInstance.patch(
        `/STUDENT-SERVICE/api/studentProject/updatePreference/${user.id}/project/${projectId}/${newPreference}`
      );

      // Update all other affected projects' preferences
      const updatePromises = newProjects
        .filter(p => p.projectId !== projectId)
        .map((p, idx) =>
          axiosInstance.patch(
            `/STUDENT-SERVICE/api/studentProject/updatePreference/${user.id}/project/${p.projectId}/${idx + 1}`
          )
        );

      await Promise.all(updatePromises);
      toast.success('Preference updated successfully!');
    } catch (error) {
      console.error('Error updating preferences:', error);
      toast.error('Failed to update preference');
      // Revert to original state
      fetchProjects();
    } finally {
      setIsReordering(false);
    }
  };

  const handleMoveUp = (projectId) => {
    const currentIndex = projects.findIndex(p => p.projectId === projectId);
    if (currentIndex > 0) {
      updatePreference(projectId, currentIndex);
    }
  };

  const handleMoveDown = (projectId) => {
    const currentIndex = projects.findIndex(p => p.projectId === projectId);
    if (currentIndex < projects.length - 1) {
      updatePreference(projectId, currentIndex + 2);
    }
  };

  const handleReorder = (newOrder) => {
    setProjects(newOrder);
  };

  const handleReorderComplete = async () => {
    setIsReordering(true);
    try {
      const updatePromises = projects.map((project, index) =>
        axiosInstance.patch(
          `/STUDENT-SERVICE/api/studentProject/updatePreference/${user.id}/project/${project.projectId}/${index + 1}`
        )
      );
      await Promise.all(updatePromises);
      toast.success('Preferences reordered successfully!');
    } catch (error) {
      console.error('Error reordering:', error);
      toast.error('Failed to save new order');
      fetchProjects();
    } finally {
      setIsReordering(false);
    }
  };

  const getPreferenceColor = (preference) => {
    if (preference === 1) return 'from-blue-500 to-blue-600';
    if (preference === 2) return 'from-indigo-500 to-indigo-600';
    if (preference === 3) return 'from-purple-500 to-purple-600';
    if (preference <= 5) return 'from-pink-500 to-pink-600';
    return 'from-gray-500 to-gray-600';
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Header */}
      <div className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-40 backdrop-blur-lg bg-white/80 dark:bg-gray-800/80">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div>
              <motion.h1
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-3xl font-bold text-gray-900 dark:text-white flex items-center gap-2"
              >
                <Sparkles className="w-8 h-8 text-blue-600" />
                My Applications
              </motion.h1>
              <motion.p
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
                className="text-gray-600 dark:text-gray-400 mt-1"
              >
                Manage your project preferences by reordering them
              </motion.p>
            </div>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={fetchProjects}
              disabled={loading || isReordering}
              className="px-4 py-2 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors flex items-center gap-2 disabled:opacity-50"
            >
              <RefreshCw className={`w-4 h-4 ${(loading || isReordering) ? 'animate-spin' : ''}`} />
              Refresh
            </motion.button>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Info Banner */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 border border-blue-200 dark:border-blue-800 rounded-2xl p-6 mb-8"
        >
          <div className="flex items-start gap-4">
            <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center flex-shrink-0">
              <Info className="w-5 h-5 text-white" />
            </div>
            <div className="flex-1">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                How Preferences Work
              </h3>
              <ul className="space-y-2 text-sm text-gray-700 dark:text-gray-300">
                <li className="flex items-center gap-2">
                  <GripVertical className="w-4 h-4 text-blue-600" />
                  <span><strong>Drag and drop</strong> projects to reorder your preferences</span>
                </li>
                <li className="flex items-center gap-2">
                  <ArrowUp className="w-4 h-4 text-blue-600" />
                  <span>Use <strong>arrow buttons</strong> for quick adjustments</span>
                </li>
                <li className="flex items-center gap-2">
                  <TrendingUp className="w-4 h-4 text-blue-600" />
                  <span><strong>Higher positions</strong> indicate stronger interest</span>
                </li>
              </ul>
            </div>
          </div>
        </motion.div>

        {/* Stats Row */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8"
        >
          {[
            { label: 'Total Applications', value: projects.length, color: 'blue' },
            { label: 'Top 3 Preferences', value: Math.min(3, projects.length), color: 'purple' },
            { label: 'Pending Review', value: projects.filter(p => p.status === 'OPEN_FOR_APPLICATIONS').length, color: 'amber' }
          ].map((stat, index) => (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.3 + index * 0.1 }}
              className="bg-white dark:bg-gray-800 rounded-xl p-6 border border-gray-200 dark:border-gray-700"
            >
              <div className="text-3xl font-bold text-gray-900 dark:text-white mb-1">
                {stat.value}
              </div>
              <div className="text-sm text-gray-600 dark:text-gray-400 font-medium">
                {stat.label}
              </div>
            </motion.div>
          ))}
        </motion.div>

        {/* Projects List */}
        {projects.length === 0 ? (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white dark:bg-gray-800 rounded-2xl border-2 border-dashed border-gray-300 dark:border-gray-700 p-12 text-center"
          >
            <div className="w-20 h-20 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
              <Sparkles className="w-10 h-10 text-gray-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
              No Applications Yet
            </h3>
            <p className="text-gray-600 dark:text-gray-400 mb-6">
              Start applying to projects to see them here
            </p>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => window.location.href = '/student/projects'}
              className="px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-colors"
            >
              Browse Projects
            </motion.button>
          </motion.div>
        ) : (
          <div>
            <Reorder.Group
              axis="y"
              values={projects}
              onReorder={handleReorder}
              className="space-y-4"
            >
              <AnimatePresence>
                {projects.map((project, index) => (
                  <Reorder.Item
                    key={project.projectId}
                    value={project}
                    onDragStart={() => setDraggedProject(project.projectId)}
                    onDragEnd={handleReorderComplete}
                    className="cursor-grab active:cursor-grabbing"
                  >
                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -20 }}
                      transition={{ delay: index * 0.05 }}
                      whileHover={{ scale: 1.01 }}
                      className="relative"
                    >
                      {/* Preference Badge */}
                      <div className="absolute -left-4 top-1/2 -translate-y-1/2 z-10">
                        <motion.div
                          whileHover={{ scale: 1.1 }}
                          className={`w-12 h-12 rounded-xl bg-gradient-to-br ${getPreferenceColor(index + 1)} shadow-lg flex items-center justify-center`}
                        >
                          <span className="text-white font-bold text-lg">{index + 1}</span>
                        </motion.div>
                      </div>

                      {/* Project Card */}
                      <div className="ml-6">
                        <AppliedProjectCard
                          project={project}
                          preference={index + 1}
                          isDragging={draggedProject === project.projectId}
                          onMoveUp={() => handleMoveUp(project.projectId)}
                          onMoveDown={() => handleMoveDown(project.projectId)}
                        />
                      </div>
                    </motion.div>
                  </Reorder.Item>
                ))}
              </AnimatePresence>
            </Reorder.Group>

            {/* Legend */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 }}
              className="mt-8 bg-white dark:bg-gray-800 rounded-xl p-6 border border-gray-200 dark:border-gray-700"
            >
              <h4 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-4">
                Preference Legend
              </h4>
              <div className="flex flex-wrap gap-4">
                {[
                  { range: '1st', color: 'from-blue-500 to-blue-600', label: 'Top Priority' },
                  { range: '2nd', color: 'from-indigo-500 to-indigo-600', label: 'High Priority' },
                  { range: '3rd', color: 'from-purple-500 to-purple-600', label: 'Medium Priority' },
                  { range: '4-5', color: 'from-pink-500 to-pink-600', label: 'Lower Priority' }
                ].map((item) => (
                  <div key={item.range} className="flex items-center gap-2">
                    <div className={`w-8 h-8 rounded-lg bg-gradient-to-br ${item.color} flex items-center justify-center text-white text-xs font-bold`}>
                      {item.range}
                    </div>
                    <span className="text-sm text-gray-600 dark:text-gray-400">{item.label}</span>
                  </div>
                ))}
              </div>
            </motion.div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AppliedProjectList;