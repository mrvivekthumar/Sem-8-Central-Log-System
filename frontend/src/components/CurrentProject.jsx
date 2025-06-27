import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { motion } from 'framer-motion';
import { Clock } from 'lucide-react';
import { Link } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance'

const CurrentProject = () => {
    const [project, setProject] = useState(null); // Start with `null`
    const { user } = useAuth();

    useEffect(() => {
        if (!user?.id) return; // Ensure user ID is available before fetching

        const fetchProject = async () => {
            try {
                const response = await axiosInstance.get(`/STUDENT-SERVICE/api/studentProject/approvedProject/${user.id}`);
                console.log("Approved Project ID:", response.data);
                console.log(`/projects/${project?.projectId}/report-submission`);
                if (!response.data) {
                    console.warn("No approved project found for this student.");
                    setProject(null); // Set project to null to avoid rendering issues
                    return;
                }

                const projectId = response.data;
                const projectData = await axiosInstance.get(`/FACULTY-SERVICE/api/project/${projectId}`);
                setProject(projectData.data);

                console.log("Project Data:", projectData.data);

            } catch (error) {
                console.error("Error fetching project data:", error);
            }
        };

        fetchProject();
    }, [user?.id]); // Depend on `user.id` to ensure correct fetching

    if (!project) {
        return (
            <div className="text-center p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md">
                <p className="text-lg text-gray-500 dark:text-gray-400">No assigned project currently.</p>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <motion.div
                whileHover={{ y: -5 }}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden"
            >
                <div className="p-6">
                    <div className="flex justify-between items-start mb-4">
                        <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                            {project.title}
                        </h3>
                        <span
                            className={`px-3 py-1 rounded-full text-sm font-medium ${project.status === 'OPEN_FOR_APPLICATIONS'
                                    ? 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400'
                                    : project.status === 'APPROVED'
                                        ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400' // In Progress (Yellow)
                                        : 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400'
                                }`}
                        >
                            {project.status === 'APPROVED' ? 'In Progress' : project.status.replaceAll('_', ' ')}
                        </span>
                    </div>

                    <p className="text-gray-600 dark:text-gray-300 mb-4 line-clamp-2">
                        {project.description}
                    </p>

                    <div className="flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
                        <div className="flex items-center">
                            <Clock className="h-4 w-4 mr-1" />
                            <span>
                                Deadline: {project.deadline ? new Date(project.deadline).toLocaleDateString() : "N/A"}
                            </span>
                        </div>
                        <div className="flex items-center">
                            <span>Faculty: {project.faculty?.name || "Unknown"}</span>
                        </div>
                    </div>

                    <Link
                        to={`/projects/${project.projectId}/report-submission`}
                        className="mt-4 block w-full text-center py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-md transition-colors"
                    >
                        View Details
                    </Link>
                </div>
            </motion.div>
        </div>

    );
};

export default CurrentProject;
