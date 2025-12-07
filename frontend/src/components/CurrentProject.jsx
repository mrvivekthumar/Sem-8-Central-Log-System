import { motion } from 'framer-motion';
import {
    Activity,
    AlertCircle,
    ArrowRight,
    Briefcase, Calendar,
    Clock, ExternalLink,
    FileText,
    Sparkles,
    Target,
    Users
} from 'lucide-react';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';
import { useAuth } from '../contexts/AuthContext';

const CurrentProject = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [currentProject, setCurrentProject] = useState(null);
    const [loading, setLoading] = useState(true);
    const [progress, setProgress] = useState(0);

    useEffect(() => {
        fetchCurrentProject();
    }, [user]);

    const fetchCurrentProject = async () => {
        try {
            setLoading(true);
            const response = await axiosInstance.get(
                `/STUDENT-SERVICE/api/studentProject/appliedProjects`
            );

            // Find the first IN_PROGRESS project
            const inProgressProjects = response.data.filter(
                p => p.status === 'IN_PROGRESS'
            );

            if (inProgressProjects.length > 0) {
                // Get full project details
                const projectResponse = await axiosInstance.get(
                    `/FACULTY-SERVICE/api/project/${inProgressProjects[0].projectId}`
                );
                setCurrentProject(projectResponse.data);

                // Calculate mock progress based on days elapsed
                const startDate = new Date(projectResponse.data.createdAt || Date.now());
                const endDate = new Date(projectResponse.data.deadline || Date.now());
                const now = new Date();
                const totalDays = (endDate - startDate) / (1000 * 60 * 60 * 24);
                const elapsedDays = (now - startDate) / (1000 * 60 * 60 * 24);
                const calculatedProgress = Math.min(Math.max((elapsedDays / totalDays) * 100, 0), 100);
                setProgress(Math.round(calculatedProgress));
            }
        } catch (error) {
            console.error('Error fetching current project:', error);
        } finally {
            setLoading(false);
        }
    };

    const getDaysRemaining = (deadline) => {
        if (!deadline) return null;
        const now = new Date();
        const end = new Date(deadline);
        const daysLeft = Math.ceil((end - now) / (1000 * 60 * 60 * 24));
        return daysLeft;
    };

    if (loading) {
        return (
            <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-8 animate-pulse">
                <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded w-1/3 mb-4" />
                <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-2/3 mb-2" />
                <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2" />
            </div>
        );
    }

    if (!currentProject) {
        return (
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="bg-gradient-to-br from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 rounded-2xl border-2 border-dashed border-blue-200 dark:border-blue-800 p-8 text-center"
            >
                <div className="w-16 h-16 bg-blue-100 dark:bg-blue-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Briefcase className="w-8 h-8 text-blue-600 dark:text-blue-400" />
                </div>
                <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                    No Active Project
                </h3>
                <p className="text-gray-600 dark:text-gray-400 mb-6">
                    You don't have any ongoing projects at the moment
                </p>
                <motion.button
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => navigate('/student/projects')}
                    className="px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg flex items-center gap-2 mx-auto"
                >
                    Browse Projects
                    <ArrowRight className="w-5 h-5" />
                </motion.button>
            </motion.div>
        );
    }

    const daysRemaining = getDaysRemaining(currentProject.deadline);
    const isUrgent = daysRemaining !== null && daysRemaining <= 7;

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="relative bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-xl transition-all group"
        >
            {/* Gradient Header */}
            <div className="relative h-32 bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 overflow-hidden">
                <div className="absolute inset-0 opacity-20">
                    <div className="absolute inset-0" style={{
                        backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
                    }} />
                </div>

                {/* Status Badge */}
                <div className="absolute top-4 right-4">
                    <div className="px-4 py-2 rounded-full bg-green-500/90 backdrop-blur-sm border border-white/20 flex items-center gap-2 shadow-lg">
                        <Activity className="w-4 h-4 text-white animate-pulse" />
                        <span className="text-sm font-semibold text-white">In Progress</span>
                    </div>
                </div>

                {/* Floating Icon */}
                <div className="absolute bottom-0 left-8 translate-y-1/2">
                    <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl flex items-center justify-center shadow-2xl border-4 border-white dark:border-gray-800 rotate-3 group-hover:rotate-6 transition-transform">
                        <Briefcase className="w-8 h-8 text-white" />
                    </div>
                </div>
            </div>

            {/* Content */}
            <div className="p-8 pt-12">
                {/* Domain Badge */}
                {currentProject.domain && (
                    <span className="inline-block px-3 py-1 text-xs font-semibold bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded-lg mb-3">
                        {currentProject.domain}
                    </span>
                )}

                {/* Title */}
                <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-3 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                    {currentProject.title}
                </h2>

                {/* Description */}
                <p className="text-gray-600 dark:text-gray-400 mb-6 line-clamp-2">
                    {currentProject.description}
                </p>

                {/* Progress Bar */}
                <div className="mb-6">
                    <div className="flex items-center justify-between mb-2">
                        <span className="text-sm font-semibold text-gray-700 dark:text-gray-300">
                            Project Progress
                        </span>
                        <span className="text-sm font-bold text-blue-600 dark:text-blue-400">
                            {progress}%
                        </span>
                    </div>
                    <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                        <motion.div
                            initial={{ width: 0 }}
                            animate={{ width: `${progress}%` }}
                            transition={{ duration: 1, ease: "easeOut" }}
                            className="h-full bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 rounded-full relative overflow-hidden"
                        >
                            <motion.div
                                animate={{ x: ['0%', '100%'] }}
                                transition={{ duration: 1.5, repeat: Infinity, ease: "linear" }}
                                className="absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent"
                            />
                        </motion.div>
                    </div>
                </div>

                {/* Stats Grid */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                    <div className="text-center p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
                        <Users className="w-5 h-5 text-blue-600 dark:text-blue-400 mx-auto mb-1" />
                        <div className="text-lg font-bold text-gray-900 dark:text-white">
                            {currentProject.teamSize || 'N/A'}
                        </div>
                        <div className="text-xs text-gray-600 dark:text-gray-400">Team Size</div>
                    </div>

                    <div className="text-center p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
                        <Clock className="w-5 h-5 text-purple-600 dark:text-purple-400 mx-auto mb-1" />
                        <div className="text-lg font-bold text-gray-900 dark:text-white">
                            {currentProject.duration || 'N/A'}
                        </div>
                        <div className="text-xs text-gray-600 dark:text-gray-400">Duration</div>
                    </div>

                    <div className="text-center p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
                        <Target className="w-5 h-5 text-green-600 dark:text-green-400 mx-auto mb-1" />
                        <div className="text-lg font-bold text-gray-900 dark:text-white">
                            {progress}%
                        </div>
                        <div className="text-xs text-gray-600 dark:text-gray-400">Complete</div>
                    </div>

                    <div className="text-center p-3 bg-gray-50 dark:bg-gray-700 rounded-xl">
                        <Calendar className="w-5 h-5 text-amber-600 dark:text-amber-400 mx-auto mb-1" />
                        <div className={`text-lg font-bold ${isUrgent ? 'text-red-600 dark:text-red-400' : 'text-gray-900 dark:text-white'}`}>
                            {daysRemaining !== null ? `${daysRemaining}d` : 'N/A'}
                        </div>
                        <div className="text-xs text-gray-600 dark:text-gray-400">Remaining</div>
                    </div>
                </div>

                {/* Deadline Warning */}
                {isUrgent && daysRemaining !== null && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95 }}
                        animate={{ opacity: 1, scale: 1 }}
                        className="flex items-center gap-3 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-xl mb-6"
                    >
                        <AlertCircle className="w-5 h-5 text-red-600 dark:text-red-400 flex-shrink-0" />
                        <div>
                            <div className="text-sm font-semibold text-red-900 dark:text-red-200">
                                Deadline Approaching!
                            </div>
                            <div className="text-xs text-red-700 dark:text-red-300">
                                Only {daysRemaining} {daysRemaining === 1 ? 'day' : 'days'} left to complete this project
                            </div>
                        </div>
                    </motion.div>
                )}

                {/* Faculty Info */}
                {currentProject.faculty && (
                    <div className="flex items-center gap-3 p-4 bg-gradient-to-r from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20 rounded-xl border border-purple-200 dark:border-purple-800 mb-6">
                        <div className="w-12 h-12 rounded-full bg-gradient-to-br from-purple-500 to-pink-600 flex items-center justify-center text-white font-bold text-lg">
                            {currentProject.faculty.name?.charAt(0) || 'F'}
                        </div>
                        <div>
                            <div className="text-xs text-gray-500 dark:text-gray-400">Faculty Mentor</div>
                            <div className="text-sm font-semibold text-gray-900 dark:text-white">
                                {currentProject.faculty.name}
                            </div>
                        </div>
                    </div>
                )}

                {/* Action Buttons */}
                <div className="flex gap-3">
                    <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        onClick={() => navigate(`/student/project/${currentProject.projectId}`)}
                        className="flex-1 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
                    >
                        <ExternalLink className="w-5 h-5" />
                        View Project
                    </motion.button>

                    <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        onClick={() => navigate(`/student/project/${currentProject.projectId}/submit`)}
                        className="flex-1 py-3 bg-white dark:bg-gray-700 border-2 border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-all flex items-center justify-center gap-2"
                    >
                        <FileText className="w-5 h-5" />
                        Submit Work
                    </motion.button>
                </div>
            </div>

            {/* Sparkle Effect */}
            <motion.div
                animate={{
                    scale: [1, 1.2, 1],
                    opacity: [0.5, 1, 0.5]
                }}
                transition={{
                    duration: 2,
                    repeat: Infinity,
                    ease: "easeInOut"
                }}
                className="absolute top-20 right-8 text-yellow-400"
            >
                <Sparkles className="w-6 h-6" />
            </motion.div>
        </motion.div>
    );
};

export default CurrentProject;
