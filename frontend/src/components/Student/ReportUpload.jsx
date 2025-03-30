import React, { useState } from 'react';
import { Upload, FileText, Trash2, CheckCircle, AlertCircle } from 'lucide-react';
import axios from 'axios';

export const ReportUpload = ({ 
  onUpload, 
  disabled,
  currentUserId,
  submittedBy,
  report,
  isAllApproved,
  onReportDeleted 
}) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [showFinalSubmission, setShowFinalSubmission] = useState(true);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && file.type === 'application/pdf') {
      setSelectedFile(file);
      setError('');
    } else if (file) {
      setError('Please select a PDF file');
      setTimeout(() => setError(''), 3000);
    }
  };

  const handleUpload = () => {
    if (selectedFile) {
      onUpload(selectedFile);
      setSelectedFile(null);
      setSuccessMessage('Report uploaded successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);
    }
  };

  const handleFinalSubmit = async () => {
    try {
      setIsSubmitting(true);
      setError('');
      
      const response = await axios.put(
        `http://localhost:8765/STUDENT-SERVICE/api/reports/report/${report.reportId}/final-submit`
      );
      
      if (response.data) {
        setSuccessMessage('Final report submitted successfully! Redirecting...');
        setShowFinalSubmission(false);
        
        // Show success message for 3 seconds before refreshing
        setTimeout(() => {
          setSuccessMessage('');
          onReportDeleted(); // Refresh the parent component
        }, 3000);
      }
    } catch (error) {
      setError('Failed to submit final report. Please try again.');
      setTimeout(() => setError(''), 3000);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    try {
      setIsDeleting(true);
      setError('');
      
      const response = await axios.delete(
        `http://localhost:8765/STUDENT-SERVICE/api/reports/report/${report.reportId}`
      );
      
      if (response.status >= 200 && response.status < 300) {
        setSuccessMessage('Report deleted successfully!');
        setTimeout(() => {
          setSuccessMessage('');
          onReportDeleted();
        }, 3000);
      }
    } catch (error) {
      setError('Failed to delete report. Please try again.');
      setTimeout(() => setError(''), 3000);
    } finally {
      setIsDeleting(false);
    }
  };

  // Message component for success and error states
  const MessageBanner = ({ message, type }) => (
    <div className={`fixed top-4 right-4 p-4 rounded-lg shadow-lg transition-all duration-300 ${
      type === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
    }`}>
      <div className="flex items-center gap-2">
        {type === 'success' ? (
          <CheckCircle className="w-5 h-5" />
        ) : (
          <AlertCircle className="w-5 h-5" />
        )}
        <p className="font-medium">{message}</p>
      </div>
    </div>
  );

  // Show submitted state
  if (disabled || (submittedBy && submittedBy !== currentUserId)) {
    return (
      <div className="text-center py-12 relative">
        {successMessage && <MessageBanner message={successMessage} type="success" />}
        {error && <MessageBanner message={error} type="error" />}
        
        <FileText className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">
          Report already submitted
        </h3>
        <p className="mt-2 text-sm text-gray-500">
          A team report has already been uploaded for this project.
        </p>
        
        {/* Final Submit button - only visible when all approved and not yet finally submitted */}
        {isAllApproved && submittedBy === currentUserId && showFinalSubmission && (
          <button
            onClick={handleFinalSubmit}
            disabled={isSubmitting}
            className="mt-6 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center mx-auto gap-2 transition-all duration-300"
          >
            <CheckCircle className="w-5 h-5" />
            {isSubmitting ? 'Submitting...' : 'Submit Final Report'}
          </button>
        )}

        {/* Delete button - only visible to uploader */}
        {submittedBy === currentUserId && (
          <button
            onClick={handleDelete}
            disabled={isDeleting}
            className="mt-4 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center mx-auto gap-2 transition-all duration-300"
          >
            <Trash2 className="w-5 h-5" />
            {isDeleting ? 'Deleting...' : 'Delete Report'}
          </button>
        )}
      </div>
    );
  }

  // Upload state
  return (
    <div className="space-y-6 relative">
      {successMessage && <MessageBanner message={successMessage} type="success" />}
      {error && <MessageBanner message={error} type="error" />}
      
      <div className="border-2 border-dashed border-gray-300 rounded-lg p-12 text-center transition-all duration-300 hover:border-blue-400">
        <Upload className="mx-auto h-12 w-12 text-gray-400" />
        <div className="mt-4">
          <label htmlFor="file-upload" className="cursor-pointer">
            <span className="mt-2 block text-sm font-medium text-gray-900">
              Upload a PDF file
            </span>
            <input
              id="file-upload"
              name="file-upload"
              type="file"
              accept="application/pdf"
              className="sr-only"
              onChange={handleFileChange}
            />
          </label>
        </div>
        
        {selectedFile && (
          <div className="mt-4 animate-fade-in">
            <p className="text-sm text-gray-500">
              Selected: {selectedFile.name}
            </p>
            <button
              onClick={handleUpload}
              className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center justify-center mx-auto gap-2 transition-all duration-300"
            >
              <Upload className="w-5 h-5" />
              Upload Report
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ReportUpload;