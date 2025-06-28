import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronDown, Star, User, Mail, Bookmark, Award, Clock, Check, AlertTriangle, X, Info, ExternalLink } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import toast from 'react-hot-toast';
import axiosInstance from '../../api/axiosInstance';
const StudentDetail = () => {
  const { user } = useAuth();
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [students, setStudents] = useState([]);
  const [studentProject, setStudentProject] = useState([]);
  const [isDropDown, setIsDropDown] = useState(true);
  const [selectedIds, setSelectedIds] = useState(new Set());
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);
  const [showWarningDialog, setShowWarningDialog] = useState(false);
  const [pendingStudentId, setPendingStudentId] = useState(null);
  const [expandedStudentIds, setExpandedStudentIds] = useState(new Set());
  const [otherProjectsMap, setOtherProjectsMap] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        const [projectResponse, studentsResponse] = await Promise.all([
          axiosInstance.get(`/FACULTY-SERVICE/api/project/${projectId}`),
          axiosInstance.get(`/FACULTY-SERVICE/api/faculty/studentproject/${projectId}`)
        ]);

        setProject(projectResponse.data);
        const studentList = studentsResponse.data;

        console.log("Hy STudents", studentsResponse.data);

        // Corrected studentIds extraction
        const studentIds = studentList.map(student => student.studentId);
        console.log("Hy StudentIds", studentIds);

        // Fetch preferences in a single request
        const preferencesResponse = await axiosInstance.post(
          `/STUDENT-SERVICE/api/studentProject/${projectId}/student`, // Ensure correct API path
          studentIds,
          {
            headers: {
              'Content-Type': 'application/json'
            }
          }
        );
        console.log("Give a fuck bro", preferencesResponse.data)

        // Merge students with their preferences
        const studentsWithPreferences = studentList.map(student => {
          const matchedPreference = preferencesResponse.data.find(p =>
            p.student && p.student.studentId === student.studentId
          );

          console.log(`Student ID: ${student.studentId}, Matched Preference:`, matchedPreference);

          return {
            ...student,
            preference: matchedPreference ? matchedPreference.preference : 'N/A'
          };
        });

        console.log("✅ Final Merged Data:", studentsWithPreferences);
        setStudents(studentsWithPreferences);

        // Fetch other faculty project data for each student
        await fetchOtherFacultyProjects(studentsWithPreferences);

      } catch (error) {
        console.error("Error fetching data:", error);
        toast.error("Failed to load project details");
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();

  }, [projectId]);

  const fetchOtherFacultyProjects = async (studentsList) => {
    const projectsDataMap = {};

    for (const student of studentsList) {
      try {
        // Get other project IDs for this student
        const otherProjectsResponse = await axiosInstance.get(
          `/STUDENT-SERVICE/api/studentProject/${student.studentId}/getProjectFaculties/project/${projectId}`
        );

        const otherProjectIds = otherProjectsResponse.data;

        // If student has applied to other projects
        if (otherProjectIds && otherProjectIds.length > 0) {
          // Fetch details for those projects
          const otherProjectsDetailsResponse = await axiosInstance.post(
            `/FACULTY-SERVICE/api/faculty/projectsbyIds`,
            otherProjectIds,
            {
              headers: {
                'Content-Type': 'application/json'
              }
            }
          );

          // Filter out projects where the faculty ID matches the current user ID
          const filteredProjects = otherProjectsDetailsResponse.data.filter(project =>
            project.faculty?.f_id !== user.id
          );

          projectsDataMap[student.studentId] = filteredProjects;
        } else {
          projectsDataMap[student.studentId] = [];
        }
      } catch (error) {
        console.error(`Error fetching other faculty projects for student ${student.studentId}:`, error);
        projectsDataMap[student.studentId] = [];
      }
    }

    setOtherProjectsMap(projectsDataMap);
  };

  const toggleSelection = (studentId, event, preference) => {
    event.stopPropagation();

    // Check if student is already selected
    if (selectedIds.has(studentId)) {
      setSelectedIds(prevSelected => {
        const newSelection = new Set(prevSelected);
        newSelection.delete(studentId);
        return newSelection;
      });
      return;
    }

    // Check if max students limit reached
    if (selectedIds.size >= project.maxStudents) {
      toast.error(`You can only select up to ${project.maxStudents} students.`);
      return;
    }

    // If preference is greater than 3, show warning dialog
    if (preference > 3) {
      setPendingStudentId(studentId);
      setShowWarningDialog(true);
    } else {
      // Otherwise, add student directly
      setSelectedIds(prevSelected => {
        const newSelection = new Set(prevSelected);
        newSelection.add(studentId);
        return newSelection;
      });
    }
  };

  const toggleOtherProjects = (studentId, event) => {
    event.stopPropagation();
    setExpandedStudentIds(prev => {
      const newSet = new Set(prev);
      if (newSet.has(studentId)) {
        newSet.delete(studentId);
      } else {
        newSet.add(studentId);
      }
      return newSet;
    });
  };


  const confirmStudentSelection = () => {
    if (pendingStudentId) {
      setSelectedIds(prevSelected => {
        const newSelection = new Set(prevSelected);
        newSelection.add(pendingStudentId);
        return newSelection;
      });
      setPendingStudentId(null);
      setShowWarningDialog(false);
    }
  };

  const cancelStudentSelection = () => {
    setPendingStudentId(null);
    setShowWarningDialog(false);
  };

  const handleApproveStudents = async () => {
    const selectedStudentIds = Array.from(selectedIds);

    if (selectedStudentIds.length === 0) {
      toast.error("Please select at least one student");
      return;
    }

    try {
      await axiosInstance.post(
        `/FACULTY-SERVICE/api/faculty/${user.id}/studentproject/${projectId}/approved`,
        selectedStudentIds,
        { headers: { 'Content-Type': 'application/json' } }
      );

      toast.success("Students approved successfully!");
      setSelectedIds(new Set());
      navigate("/faculty/dashboard");
    } catch (error) {
      console.error("Error approving students:", error);
      toast.error("Failed to approve students. Please try again.");
    }
  };

  // Helper function to get preference color
  const getPreferenceColor = (preference) => {
    if (preference === 1) return 'bg-blue-600 border-blue-500';
    if (preference === 2) return 'bg-indigo-600 border-indigo-500';
    if (preference === 3) return 'bg-purple-600 border-purple-500';
    if (preference <= 5) return 'bg-pink-600 border-pink-500';
    return 'bg-gray-600 border-gray-500';
  };

  // Helper function to get preference text
  const getPreferenceText = (preference) => {
    if (preference === 1) return 'First Choice';
    if (preference === 2) return 'Second Choice';
    if (preference === 3) return 'Third Choice';
    return `Preference ${preference}`;
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (!project) return (
    <div className="max-w-4xl mx-auto p-6 text-center">
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="bg-red-50 p-4 rounded-lg"
      >
        <p className="text-red-600">Project not found or error loading data.</p>
      </motion.div>
    </div>
  );

  const availableStudents = students.filter((student) => student.studentAvaibility === "AVAILABLE");

  // Find the pending student's details
  const pendingStudent = pendingStudentId ?
    students.find(student => student.studentId === pendingStudentId) : null;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="max-w-4xl mx-auto p-6"
    >
      {/* Project Header */}
      <motion.div
        initial={{ y: -20 }}
        animate={{ y: 0 }}
        transition={{ duration: 0.5 }}
        className="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 mb-6"
      >
        <div className="flex items-center mb-4">
          <div className="w-2 h-12 bg-blue-500 rounded-full mr-4"></div>
          <h2 className="text-3xl font-bold text-gray-900 dark:text-white">{project.title}</h2>
        </div>

        <div className="ml-6">
          <p className="text-gray-600 dark:text-gray-300 mb-4">{project.description}</p>

          <div className="flex flex-wrap gap-4 text-sm">
            <div className="flex items-center bg-blue-50 dark:bg-blue-900/20 px-3 py-1.5 rounded-full">
              <Clock className="w-4 h-4 text-blue-600 dark:text-blue-400 mr-2" />
              <span className="text-blue-600 dark:text-blue-400">Deadline: {new Date(project.deadline || Date.now()).toLocaleDateString()}</span>
            </div>

            <div className="flex items-center bg-purple-50 dark:bg-purple-900/20 px-3 py-1.5 rounded-full">
              <Award className="w-4 h-4 text-purple-600 dark:text-purple-400 mr-2" />
              <span className="text-purple-600 dark:text-purple-400">Max Students: {project.maxStudents}</span>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Warning Dialog */}
      <AnimatePresence>
        {showWarningDialog && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4"
          >
            <motion.div
              initial={{ scale: 0.9 }}
              animate={{ scale: 1 }}
              exit={{ scale: 0.9 }}
              className="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-md w-full overflow-hidden"
            >
              <div className="bg-yellow-50 dark:bg-yellow-900/20 p-4 flex items-start">
                <AlertTriangle className="w-6 h-6 text-yellow-500 mr-3 flex-shrink-0" />
                <div>
                  <h3 className="font-bold text-yellow-800 dark:text-yellow-400">Low Student Interest Warning</h3>
                  <p className="text-yellow-700 dark:text-yellow-300 text-sm">
                    This student has a low preference for this project.
                  </p>
                </div>
                <button
                  onClick={cancelStudentSelection}
                  className="ml-auto text-gray-400 hover:text-gray-500 dark:text-gray-500 dark:hover:text-gray-400"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              <div className="p-5">
                <p className="text-gray-700 dark:text-gray-300 mb-4">
                  {pendingStudent?.name} has this project as their {getPreferenceText(pendingStudent?.preference || 0)}
                  (preference ranking: {pendingStudent?.preference}).
                  Students with preferences above 3 typically show less interest in the project.
                </p>
                <p className="text-gray-700 dark:text-gray-300 mb-6">
                  Are you sure you want to select this student?
                </p>

                <div className="flex justify-end space-x-3">
                  <button
                    onClick={cancelStudentSelection}
                    className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={confirmStudentSelection}
                    className="px-4 py-2 bg-yellow-500 hover:bg-yellow-600 text-white rounded-md transition-colors"
                  >
                    Yes, Select Anyway
                  </button>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Students List */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg overflow-hidden">
        <div
          className="flex justify-between items-center p-5 cursor-pointer border-b border-gray-200 dark:border-gray-700"
          onClick={() => setIsDropDown(!isDropDown)}
        >
          <div className="flex items-center">
            <motion.div
              animate={{ rotate: isDropDown ? 180 : 0 }}
              transition={{ duration: 0.3 }}
            >
              <ChevronDown className="w-5 h-5 text-blue-500 mr-3" />
            </motion.div>
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">Enrolled Students</h3>
            {availableStudents.length > 0 && (
              <span className="ml-3 bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 px-2.5 py-0.5 rounded-full text-sm">
                {availableStudents.length}
              </span>
            )}
          </div>

          {selectedIds.size > 0 && (
            <motion.div
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              className="flex items-center"
            >
              <span className="text-sm text-gray-500 dark:text-gray-400 mr-3">
                {selectedIds.size} student{selectedIds.size > 1 ? 's' : ''} selected
              </span>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleApproveStudents();
                }}
                className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-md transition-colors flex items-center"
              >
                <Check className="w-4 h-4 mr-2" />
                Approve Selected
              </button>
            </motion.div>
          )}
        </div>

        <AnimatePresence>
          {isDropDown && (
            <motion.div
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              transition={{ duration: 0.3 }}
              className="overflow-hidden"
            >
              <div className="p-5 grid gap-4">
                {availableStudents.length > 0 ? (
                  availableStudents.map((student, index) => (
                    <motion.div
                      key={student.studentId}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: index * 0.05 }}
                      className={`relative rounded-lg overflow-hidden shadow-md transition-all duration-300 
                        ${expandedStudentIds.has(student.studentId) ? 'ring-2 ring-blue-400' : ''}`}
                    >
                      <div className={`absolute top-0 left-0 h-full w-1 ${getPreferenceColor(student.preference || 0).split(' ')[0]}`}></div>
                      <div className="p-5 bg-white dark:bg-gray-800">
                        <div className="flex items-start justify-between">
                          <div className="flex flex-1" onClick={() => navigate(`/student/profile/${student.studentId}`)}>
                            <div className="mr-4 flex-shrink-0">
                              <div className={`w-10 h-10 rounded-full flex items-center justify-center text-white font-bold ${getPreferenceColor(student.preference || 0).split(' ')[0]}`}>
                                {student.preference || '?'}
                              </div>
                            </div>
                            <div>
                              <h4 className="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center">
                                {student.name}
                                {student.preference && student.preference <= 3 && (
                                  <span className={`ml-2 text-xs px-2 py-0.5 rounded-full ${student.preference === 1 ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300' :
                                    student.preference === 2 ? 'bg-indigo-100 text-indigo-800 dark:bg-indigo-900/30 dark:text-indigo-300' :
                                      'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-300'
                                    }`}>
                                    {getPreferenceText(student.preference)}
                                  </span>
                                )}
                                {student.preference && student.preference > 3 && (
                                  <span className="ml-2 text-xs px-2 py-0.5 rounded-full bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-300">
                                    Low Interest
                                  </span>
                                )}
                              </h4>
                              <div className="flex items-center text-sm text-gray-500 dark:text-gray-400 mt-1">
                                <Mail className="w-4 h-4 mr-1" />
                                {student.email}
                              </div>
                              <div className="flex mt-2">
                                {[...Array(5)].map((_, starIndex) => (
                                  <Star
                                    key={starIndex}
                                    className={`w-5 h-5 ${starIndex < (student.ratings || 0)
                                      ? 'text-yellow-400 fill-yellow-400'
                                      : 'text-gray-300 dark:text-gray-600'
                                      }`}
                                  />
                                ))}
                                <span className="ml-2 text-sm text-gray-500 dark:text-gray-400">
                                  {student.ratings || 0}/5
                                </span>
                              </div>
                            </div>
                          </div>

                          <div className="flex items-center space-x-2">
                            {/* Display badge if student has applied to other projects */}
                            {otherProjectsMap[student.studentId]?.length > 0 && (
                              <button
                                onClick={(e) => toggleOtherProjects(student.studentId, e)}
                                className={`flex items-center px-3 py-1 rounded-md text-sm
                                  ${expandedStudentIds.has(student.studentId)
                                    ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300'
                                    : 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300'
                                  }`}
                              >
                                <Info className="w-4 h-4 mr-1" />
                                {otherProjectsMap[student.studentId].length} other {otherProjectsMap[student.studentId].length === 1 ? 'project' : 'projects'}
                              </button>
                            )}

                            <button
                              onClick={(e) => toggleSelection(student.studentId, e, student.preference)}
                              className={`relative overflow-hidden px-4 py-2 rounded-md transition-all duration-300 ${selectedIds.has(student.studentId)
                                ? 'bg-blue-500 text-white hover:bg-blue-600'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200 dark:bg-gray-700 dark:text-gray-300 dark:hover:bg-gray-600'
                                }`}
                            >
                              <motion.span
                                animate={{
                                  y: selectedIds.has(student.studentId) ? 0 : 30,
                                  opacity: selectedIds.has(student.studentId) ? 1 : 0,
                                }}
                                transition={{ duration: 0.2 }}
                                className="absolute inset-0 flex items-center justify-center"
                              >
                                Selected
                              </motion.span>
                              <motion.span
                                animate={{
                                  y: selectedIds.has(student.studentId) ? -30 : 0,
                                  opacity: selectedIds.has(student.studentId) ? 0 : 1,
                                }}
                                transition={{ duration: 0.2 }}
                              >
                                Select
                              </motion.span>
                            </button>
                          </div>
                        </div>

                        {/* Other Projects Section */}
                        <AnimatePresence>
                          {expandedStudentIds.has(student.studentId) && otherProjectsMap[student.studentId]?.length > 0 && (
                            <motion.div
                              initial={{ height: 0, opacity: 0, y: -10 }}
                              animate={{ height: 'auto', opacity: 1, y: 0 }}
                              exit={{ height: 0, opacity: 0, y: -10 }}
                              transition={{ duration: 0.3 }}
                              className="mt-4 overflow-hidden"
                            >
                              <div className="p-3 bg-gray-50 dark:bg-gray-700/20 rounded-lg border border-gray-200 dark:border-gray-700">
                                <h5 className="font-medium text-gray-700 dark:text-gray-300 mb-2">
                                  Applied to other faculty projects:
                                </h5>
                                <div className="space-y-3">
                                  {otherProjectsMap[student.studentId].map((otherProject) => (
                                    <div
                                      key={otherProject.projectId}
                                      className="flex items-start p-2 bg-white dark:bg-gray-800 rounded-md shadow-sm border border-gray-100 dark:border-gray-700"
                                    >
                                      <div className="flex-1">
                                        <div className="font-medium text-blue-600 dark:text-blue-400">
                                          {otherProject.title}
                                        </div>
                                        <div className="flex items-center text-sm text-gray-500 dark:text-gray-400 mt-1">
                                          <User className="w-3.5 h-3.5 mr-1" />
                                          Faculty: {otherProject.faculty?.name || 'Unknown'}
                                        </div>

                                      </div>
                                      <div>
                                        <button
                                          onClick={() => navigate(`/faculty/project/${otherProject.projectId}`)}
                                          className="p-1 rounded-md text-gray-400 hover:text-gray-600 dark:text-gray-500 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                                        >
                                          <ExternalLink className="w-4 h-4" />
                                        </button>
                                      </div>
                                    </div>
                                  ))}
                                </div>
                              </div>
                            </motion.div>
                          )}
                        </AnimatePresence>
                      </div>
                    </motion.div>
                  ))
                ) : (
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="p-8 text-center text-gray-500 dark:text-gray-400 bg-gray-50 dark:bg-gray-800/50 rounded-lg"
                  >
                    <User className="w-12 h-12 mx-auto mb-4 text-gray-400 dark:text-gray-500" />
                    <p className="text-lg">No students enrolled yet.</p>
                  </motion.div>
                )}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Floating button for approving students - only visible on mobile when scrolling */}
      {selectedIds.size > 0 && (
        <motion.div
          initial={{ y: 100, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          className="md:hidden fixed bottom-6 right-6 z-10"
        >
          <button
            onClick={handleApproveStudents}
            className="bg-green-500 text-white px-6 py-3 rounded-full shadow-lg hover:bg-green-600 transition-colors flex items-center"
          >
            <Check className="w-5 h-5 mr-2" />
            Approve ({selectedIds.size})
          </button>
        </motion.div>
      )}
    </motion.div>
  );
};

export default StudentDetail;