import React, { useState, useEffect } from 'react';
import { AlertCircle, Check, X, Upload, Download, Users, FileText } from 'lucide-react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import axios from 'axios';

const ProjectSubmissionManager = ({ currentStudentId = 1 }) => {
  // State variables
  const {user}=useAuth();
  const {projectId}=useParams();
  const [teammates, setTeammates] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadedReport, setUploadedReport] = useState(null);
  const [approvals, setApprovals] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [toast, setToast] = useState({ show: false, message: '', type: '' });
  const [teamReports, setTeamReports] = useState([]);
  const [loadingReports, setLoadingReports] = useState(false);

  // Fetch teammates data
  useEffect(() => {
    const fetchTeammates = async () => {
      try {
        setLoading(true);
        // Fetch student IDs assigned to the project
        const idsResponse = await fetch(`http://localhost:8765/STUDENT-SERVICE/api/studentProject/${projectId}/aprovedStudents`);
        const studentIds = await idsResponse.json();
        
        // Fetch student details by IDs
        const detailsResponse = await fetch(`http://localhost:8765/STUDENT-SERVICE/students/by-id?ids=${studentIds.join(',')}`);
        const studentDetails = await detailsResponse.json();
        
        setTeammates(studentDetails);
        
        // Initialize approvals object
        const initialApprovals = {};
        studentIds.forEach(id => {
          initialApprovals[id] = false;
        });
        setApprovals(initialApprovals);
        
        setLoading(false);
      } catch (err) {
        setError('Failed to load teammates. Please try again later.');
        setLoading(false);
      }
    };
    
    fetchTeammates();
  }, [projectId]);

  // Fetch team reports
  useEffect(() => {
    const fetchTeamReports = async () => {
      try {
        setLoadingReports(true);
        const response = await axios.get(`http://localhost:8765/STUDENT-SERVICE/api/reports/project/${projectId}/report`);
        setTeamReports(response.data);
        setLoadingReports(false);
      } catch (err) {
        console.error('Failed to fetch team reports:', err);
        showToast('Failed to load team reports', 'error');
        setLoadingReports(false);
      }
    };
    
    fetchTeamReports();
  }, [projectId]);

  // Handle file selection
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.type === 'application/pdf') {
        setSelectedFile(file);
        showToast('File selected successfully!', 'success');
      } else {
        e.target.value = '';
        showToast('Please select a PDF file only', 'error');
      }
    }
  };

  // Handle file upload
  const handleUpload = async () => {
    if (!selectedFile) {
      showToast('Please select a file to upload', 'error');
      return;
    }
  
    const formData = new FormData();
    formData.append('file', selectedFile);
  
    try {
      const response = await axios.post(
        `http://localhost:8765/STUDENT-SERVICE/api/reports/student/${user.id}/project/${projectId}/submit`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );
  
      setUploadedReport({
        name: response.data.name,
        url: response.data.url,
        uploadedBy: currentStudentId,
        uploadedAt: new Date().toISOString(),
      });
      console.log(uploadedReport);
  
      showToast('Report uploaded successfully!', 'success');
      setSelectedFile(null);
      
      // Refresh team reports after upload
      fetchTeamReports();
    } catch (error) {
      showToast(error.response?.data?.message || 'Failed to upload report', 'error');
    }
  };

  // Fetch team reports function
  const fetchTeamReports = async () => {
    try {
      const response = await axios.get(`http://localhost:8765/STUDENT-SERVICE/api/reports/project/${projectId}/report`);
      setTeamReports(response.data);
    } catch (err) {
      console.error('Failed to fetch team reports:', err);
    }
  };

  // Handle approval/rejection
  const handleApprovalChange = (studentId, isApproved) => {
    setApprovals(prev => ({
      ...prev,
      [studentId]: isApproved
    }));
    
    showToast(isApproved ? 'Report approved!' : 'Report rejected', isApproved ? 'success' : 'warning');
  };

  // Check if all teammates have approved
  const allApproved = () => {
    return Object.values(approvals).every(status => status === true);
  };

  // Handle final submission
  const handleSubmitFinal = () => {
    // Simulate submission to backend
    // In a real implementation, you would send the report to an API endpoint
    showToast('Final report submitted successfully!', 'success');
  };

  // Show toast message
  const showToast = (message, type) => {
    setToast({ show: true, message, type });
    setTimeout(() => {
      setToast({ show: false, message: '', type: '' });
    }, 3000);
  };

  // Format date string
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // Custom GitHub icon component to replace the lucide-react GitHub icon
  const GitHubIcon = () => (
    <svg 
      xmlns="http://www.w3.org/2000/svg" 
      width="16" 
      height="16" 
      viewBox="0 0 24 24" 
      fill="none" 
      stroke="currentColor" 
      strokeWidth="2" 
      strokeLinecap="round" 
      strokeLinejoin="round"
    >
      <path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22"></path>
    </svg>
  );

  if (loading) {
    return <div className="flex justify-center items-center p-8">Loading teammates data...</div>;
  }

  if (error) {
    return <div className="bg-red-50 p-4 rounded-lg text-red-600">{error}</div>;
  }

  return (
    <div className="bg-white shadow-md rounded-lg p-6 max-w-4xl mx-auto">
      {/* Toast Notification */}
      {toast.show && (
        <div className={`fixed top-4 right-4 px-4 py-2 rounded-md shadow-lg z-50 ${
          toast.type === 'success' ? 'bg-green-100 text-green-800' : 
          toast.type === 'error' ? 'bg-red-100 text-red-800' : 
          'bg-yellow-100 text-yellow-800'
        }`}>
          {toast.message}
        </div>
      )}

      <h2 className="text-2xl font-semibold text-gray-800 mb-6 border-b pb-2">Project Submission Manager</h2>
      
      {/* Teammates Section */}
      <div className="mb-8">
        <div className="flex items-center gap-2 mb-4">
          <Users size={20} className="text-blue-600" />
          <h3 className="text-lg font-medium text-gray-700">Your Teammates</h3>
        </div>
        
        <div className="bg-gray-50 rounded-lg p-4">
          {teammates.map(student => (
            <div key={student.studentId} className={`flex items-center justify-between p-3 mb-2 rounded-md ${
              student.studentId === user?.id ? 'bg-blue-50 border border-blue-100' : 'bg-white border'
            }`}>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-semibold">
                  {student.name.charAt(0)}
                </div>
                <div>
                  <p className="font-medium text-gray-800">
                    {student.name}
                    {student.studentId === user.id && <span className="ml-2 text-sm text-blue-600">(You)</span>}
                  </p>
                  <p className="text-sm text-gray-600">{student.email}</p>
                </div>
              </div>
              
              <div className="flex items-center gap-3">
                <a 
                  href={student.githubProfileLink} 
                  target="_blank" 
                  rel="noopener noreferrer"
                  className="flex items-center gap-1 text-sm text-gray-600 hover:text-gray-900"
                >
                  <GitHubIcon />
                  <span className="hidden sm:inline">GitHub</span>
                </a>
                
                {uploadedReport && student.studentId !== user.id && (
                  <div className="flex gap-1">
                    <button
                      onClick={() => handleApprovalChange(student.studentId, true)}
                      className={`p-1 rounded-md ${
                        approvals[student.studentId] ? 'bg-green-100 text-green-600' : 'bg-gray-100 hover:bg-green-50'
                      }`}
                    >
                      <Check size={16} />
                    </button>
                    <button
                      onClick={() => handleApprovalChange(student.studentId, false)}
                      className={`p-1 rounded-md ${
                        approvals[student.studentId] === false ? 'bg-red-100 text-red-600' : 'bg-gray-100 hover:bg-red-50'
                      }`}
                    >
                      <X size={16} />
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
      
      {/* Team Reports Section */}
      <div className="mb-8">
        <div className="flex items-center gap-2 mb-4">
          <FileText size={20} className="text-blue-600" />
          <h3 className="text-lg font-medium text-gray-700">Team Reports</h3>
        </div>
        
        {loadingReports ? (
          <div className="bg-gray-50 p-4 rounded-lg text-center">
            <p className="text-gray-600">Loading team reports...</p>
          </div>
        ) : teamReports.length > 0 ? (
          <div className="bg-gray-50 rounded-lg p-4">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Report Name</th>
                    <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Uploaded By</th>
                    <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Submitted</th>
                    <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {teamReports.map((report, index) => (
                    <tr key={index} className="hover:bg-gray-50">
                      <td className="px-4 py-3 whitespace-nowrap">
                        <div className="flex items-center">
                          <FileText size={16} className="text-blue-500 mr-2" />
                          <span className="text-sm font-medium text-gray-900">{report.name || "Project Report"}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap">
                        <span className="text-sm text-gray-600">
                          {teammates.find(student => student.studentId === report.studentProject.student.studentId)?.name || `Student ${report.studentId}`}
                        </span>
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap">
                        <span className="text-sm text-gray-600">{formatDate(report.submissionDate)}</span>
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap">
                        <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          report.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                          report.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                          'bg-yellow-100 text-yellow-800'
                        }`}>
                          {report.status || "Pending"}
                        </span>
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-sm font-medium">
                        <a
                          href={report.documentUrl}
                          className="text-blue-600 hover:text-blue-900 inline-flex items-center gap-1"
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <Download size={16} />
                          <span>Download</span>
                        </a>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        ) : (
          <div className="bg-gray-50 p-6 rounded-lg border border-dashed border-gray-300 text-center">
            <FileText size={32} className="mx-auto text-gray-400 mb-2" />
            <p className="text-gray-600">No team reports found for this project.</p>
          </div>
        )}
      </div>
      
      {/* Report Upload Section */}
      <div className="mb-8">
        <h3 className="text-lg font-medium text-gray-700 mb-4">Report Management</h3>
        
        {/* Upload controls - only for current student */}
        {user.id && !uploadedReport && (
          <div className="bg-gray-50 p-6 rounded-lg border border-dashed border-gray-300 text-center">
            <Upload size={32} className="mx-auto text-gray-400 mb-2" />
            <p className="mb-4 text-gray-600">Upload your project report (PDF only)</p>
            <div className="flex flex-col sm:flex-row justify-center gap-4">
              <input
                type="file"
                id="report-file"
                accept="application/pdf"
                onChange={handleFileChange}
                className="hidden"
              />
              <label
                htmlFor="report-file"
                className="px-4 py-2 bg-blue-50 text-blue-600 rounded-md cursor-pointer hover:bg-blue-100 inline-block"
              >
                Select File
              </label>
              <button
                onClick={handleUpload}
                disabled={!selectedFile}
                className={`px-4 py-2 rounded-md ${
                  selectedFile 
                  ? 'bg-blue-600 text-white hover:bg-blue-700' 
                  : 'bg-gray-200 text-gray-500 cursor-not-allowed'
                }`}
              >
                Upload Report
              </button>
            </div>
            {selectedFile && (
              <p className="mt-2 text-sm text-gray-600">
                Selected file: {selectedFile.name}
              </p>
            )}
          </div>
        )}
        
        {/* Uploaded report display */}
        {uploadedReport && (
          <div className="bg-gray-50 p-4 rounded-lg">
            <div className="flex items-center justify-between bg-white p-3 rounded-md border mb-4">
              <div>
                <h4 className="font-medium">Current Report</h4>
                <p className="text-sm text-gray-600">{uploadedReport.name}</p>
              </div>
              <div className="flex gap-2">
                <a
                  href={uploadedReport.documentUrl}
                  download={uploadedReport.documentUrl}
                  className="flex items-center gap-1 px-3 py-1 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100"
                >
                  <Download size={16} />
                  <span>Download</span>
                </a>
              </div>
            </div>
            
            {/* Approval status */}
            <div className="mb-4">
              <h4 className="font-medium mb-2">Approval Status</h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                {teammates.filter((student)=>student.studentId!=user.id).map(student => (
                  <div key={student.studentId} className="flex items-center gap-2 bg-white p-2 rounded border">
                    <div className={`w-2 h-2 rounded-full ${
                      approvals[student.studentId] ? 'bg-green-500' : 
                      approvals[student.studentId] === false ? 'bg-red-500' : 'bg-gray-300'
                    }`} />
                    <span>{student.name}: </span>
                    <span className={`text-sm ${
                      approvals[student.studentId] ? 'text-green-600' : 
                      approvals[student.studentId] === false ? 'text-red-600' : 'text-gray-500'
                    }`}>
                      {approvals[student.studentId] ? 'Approved' : 
                       approvals[student.studentId] === false ? 'Rejected' : 'Pending'}
                    </span>
                  </div>
                ))}
              </div>
            </div>
            
            {/* Final submission */}
            <div className="mt-6">
              {!allApproved() ? (
                <div className="flex items-center gap-2 bg-yellow-50 text-yellow-700 p-4 rounded-md">
                  <AlertCircle size={20} />
                  <p>All teammates must approve the report before final submission.</p>
                </div>
              ) : (
                <button
                  onClick={handleSubmitFinal}
                  className="w-full py-2 bg-green-600 hover:bg-green-700 text-white rounded-md flex items-center justify-center gap-2"
                >
                  <Check size={18} />
                  Submit Final Report
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectSubmissionManager;