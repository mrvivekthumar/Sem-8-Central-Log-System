import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { motion } from 'framer-motion';
import { ChevronDown, Star, CheckCircle } from 'lucide-react';
import Button from './Button';

const StudentDetail = () => {
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [students, setStudents] = useState([]);
  const [selectedStudents, setSelectedStudents] = useState([]);
  const [isDropDown, setIsDropDown] = useState(false);

  useEffect(() => {
    const fetchProjectDetails = async () => {
      try {
        const response = await axios.get(`http://localhost:8765/FACULTY-SERVICE/api/project/${projectId}`);
        setProject(response.data);
      } catch (error) {
        console.error("Error fetching project details:", error);
      }
    };

    const fetchStudents = async () => {
      try {
        const response = await axios.get(`http://localhost:8765/FACULTY-SERVICE/api/faculty/studentproject/${projectId}`);
        setStudents(response.data);
      } catch (error) {
        console.error("Error fetching students:", error);
      }
    };

    fetchProjectDetails();
    fetchStudents();
  }, [projectId]);

  const handleSelectStudent = (studentId) => {
    setSelectedStudents((prevSelected) =>
      prevSelected.includes(studentId)
        ? prevSelected.filter((id) => id !== studentId)
        : [...prevSelected, studentId]
    );
  };

  const handleApproveStudents = async () => {
    try {
      await axios.post(
        `http://localhost:8765/FACULTY-SERVICE/api/faculty/${project.facultyId}/studentproject/${projectId}/approved`,
        { studentIds: selectedStudents }
      );
      alert("Students approved successfully!");
      setSelectedStudents([]);
    } catch (error) {
      console.error("Error approving students:", error);
    }
  };

  if (!project) return <p>Loading...</p>;

  return (
    <div className="max-w-4xl mx-auto p-6">
      <motion.h2
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-3xl font-bold text-gray-900 dark:text-white mb-4"
      >
        {project.title}
      </motion.h2>
      <p className="text-gray-600 dark:text-gray-300 mb-4">{project.description}</p>

      {/* Enrolled Students Dropdown */}
      <div className="border border-gray-300 dark:border-gray-700 rounded-lg p-4 bg-gray-100 dark:bg-gray-800">
        <div
          className="flex justify-between items-center cursor-pointer"
          onClick={() => setIsDropDown(!isDropDown)}
        >
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white">Enrolled Students</h3>
          <motion.div
            animate={{ rotate: isDropDown ? 180 : 0 }}
            transition={{ duration: 0.3 }}
          >
            <ChevronDown className="w-5 h-5 text-gray-600 dark:text-white" />
          </motion.div>
        </div>

        <motion.div
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: isDropDown ? 1 : 0, height: isDropDown ? "auto" : 0 }}
          transition={{ duration: 0.3, ease: "easeInOut" }}
          className="overflow-hidden mt-2"
        >
          <div className="max-h-60 overflow-y-auto space-y-2 p-2">
            {students.length > 0 ? (
              students.map((student) => (
                <div
                  key={student.id}
                  className={`p-3 bg-white dark:bg-gray-700 rounded-lg shadow-md flex justify-between items-center`}
                >
                  <div>
                    <p className="text-sm font-medium text-gray-900 dark:text-white">{student.name}</p>
                    <p className="text-xs text-gray-500 dark:text-gray-400">{student.email}</p>
                    <div className="flex items-center space-x-1 mt-1">
                      {[...Array(5)].map((_, index) => (
                        <Star
                          key={index}
                          className={`w-4 h-4 ${index < student.ratings ? 'text-yellow-500' : 'text-gray-400'}`}
                        />
                      ))}
                    </div>
                  </div>

                  {/* ✅ Only CheckCircle is clickable now */}
                  <CheckCircle
                    className={`w-6 h-6 cursor-pointer ${
                      selectedStudents.includes(student.id) ? 'text-green-500' : 'text-gray-400'
                    }`}
                    onClick={(e) => {
                      e.stopPropagation(); // Prevent event bubbling ✅
                      handleSelectStudent(student.id);
                    }}
                  />
                </div>
              ))
            ) : (
              <p className="text-sm text-gray-500">No students enrolled yet.</p>
            )}
          </div>
        </motion.div>
      </div>

      {/* Approve Students Button */}
      {selectedStudents.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="fixed bottom-6 right-6"
        >
          <Button
            onClick={handleApproveStudents}
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full shadow-lg"
          >
            Approve Selected Students
          </Button>
        </motion.div>
      )}
    </div>
  );
};

export default StudentDetail;
