import React, { useEffect, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import axios from 'axios';
import AppliedProjectCard from './AppliedProjectCard';
import axiosInstance from '../../api/axiosInstance';
const AppliedProjectList = () => {
  const [projects, setProjects] = useState([]);
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [draggedProject, setDraggedProject] = useState(null);

  useEffect(() => {
    const fetchProjects = async () => {
        setLoading(true);
        try {
            // Step 1: Get the applied project IDs
            const responseIds = await axiosInstance.get(
                `/STUDENT-SERVICE/api/studentProject/projectIdsByPref/${user.id}`
            );
            console.log("Fetched project Ids:", responseIds.data);  // Expected: [16, 42, 18]

            // Step 2: Get project details from faculty service
            const response = await axiosInstance.post(
                '/FACULTY-SERVICE/api/faculty/projectsbyIds',
                responseIds.data,
                { headers: { 'Content-Type': 'application/json' } }
            );

            console.log("Projects from faculty service:", response.data);

            // Step 3: Reorder projects to match responseIds.data order
            const orderedProjects = responseIds.data.map(id =>
                response.data.find(project => project.projectId === id)
            );

            console.log("Ordered projects:", orderedProjects);

            setProjects(orderedProjects);
        } catch (error) {
            console.error("Error fetching projects:", error);
        } finally {
            setLoading(false);
        }
    };

    if (user?.id) {
        fetchProjects();
    }
}, [user]);


  // Function to handle preference update
  const updatePreference = async (projectId, newPreference) => {
    // Find the project
    const projectIndex = projects.findIndex(p => p.projectId === projectId);
    if (projectIndex === -1) return;

    const currentPreference = projectIndex + 1;
    if (newPreference === currentPreference) return;

    // Create a copy of projects array
    const newProjects = [...projects];
    
    // Remove the project from its current position
    const [movedProject] = newProjects.splice(projectIndex, 1);
    
    // Insert the project at its new position
    newProjects.splice(newPreference - 1, 0, movedProject);
    
    // Update preferences for all affected projects
    const updatedProjects = newProjects.map((proj, idx) => ({
      ...proj,
      preference: idx + 1
    }));

    // Optimistically update UI
    setProjects(updatedProjects);

    try {
      // First, update the moved project's preference
      await axiosInstance.patch(
        `/STUDENT-SERVICE/api/studentProject/updatePreference/${user.id}/project/${projectId}/${newPreference}`
      );
      
      // Next, update all other affected projects' preferences
      const updatePromises = updatedProjects
        .filter(p => p.projectId !== projectId) // Skip the already updated project
        .filter(p => p.preference !== projects.find(op => op.projectId === p.projectId).preference) // Only update changed preferences
        .map(p => 
          axiosInstance.patch(
            `/STUDENT-SERVICE/api/studentProject/updatePreference/${user.id}/project/${p.projectId}/${p.preference}`
          )
        );
      
      if (updatePromises.length > 0) {
        await Promise.all(updatePromises);
      }
      
      console.log("All preferences updated successfully");
    } catch (error) {
      console.error("Error updating preferences:", error);
      // Revert to original state if the API call fails
      setProjects(projects);
    }
  };

  // Handle drag start
  const handleDragStart = (projectId) => {
    setDraggedProject(projectId);
  };

  // Handle drag over
  const handleDragOver = (e) => {
    e.preventDefault();
  };

  // Handle drop on a different project
  const handleDrop = (e, targetProjectId) => {
    e.preventDefault();
    if (!draggedProject || draggedProject === targetProjectId) return;

    const draggedIndex = projects.findIndex(p => p.projectId === draggedProject);
    const targetIndex = projects.findIndex(p => p.projectId === targetProjectId);
    
    updatePreference(draggedProject, targetIndex + 1);
    setDraggedProject(null);
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Your Applied Projects</h2>
      <p className="text-gray-600 dark:text-gray-300 mb-6">
        Use Up and Down Arrows to reorder your preferences. Higher positions indicate higher preference.
      </p>

      {loading ? (
        <div className="flex justify-center items-center h-40">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
      ) : (
        
        <div className="space-y-4">
          {projects.length > 0 ? (
            projects.filter(project => project.status !== "COMPLETED").map((project, index) => (
              <div 
                key={project.projectId}
                draggable={true}
                onDragStart={() => handleDragStart(project.projectId)}
                onDragOver={handleDragOver}
                onDrop={(e) => handleDrop(e, project.projectId)}
                className={`transition-all ${draggedProject === project.projectId ? 'opacity-50' : 'opacity-100'}`}
              >
                <AppliedProjectCard 
                  project={project} 
                  preference={index + 1}
                  isDragging={draggedProject === project.projectId}
                  onMoveUp={index > 0 ? () => updatePreference(project.projectId, index) : null}
                  onMoveDown={index < projects.length - 1 ? () => updatePreference(project.projectId, index + 2) : null}
                />
              </div>
            ))
          ) : (
            <p className="text-gray-500 dark:text-gray-400 text-center py-8">
              No applied projects available. Apply to projects to see them here.
            </p>
          )}
        </div>
      )}
    </div>
  );
};

export default AppliedProjectList;