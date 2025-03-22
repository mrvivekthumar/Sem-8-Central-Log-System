import { useState, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Upload, FileSpreadsheet, Check, AlertCircle, Loader2 } from 'lucide-react';
import * as XLSX from 'xlsx';
import axios from 'axios';

const UploadExcelModal = ({ isOpen, onClose, onUpload }) => {
  const [file, setFile] = useState(null);
  const [previewData, setPreviewData] = useState(null);
  const [role, setRole] = useState('student');
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState(null);
  const fileInputRef = useRef(null);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) return;

    if (!selectedFile.name.endsWith('.xlsx') && !selectedFile.name.endsWith('.xls')) {
      setError('Please upload an Excel file (.xlsx or .xls)');
      setFile(null);
      setPreviewData(null);
      return;
    }

    setError(null);
    setFile(selectedFile);
    setUploadStatus(null);

    // Read and preview Excel file
    const reader = new FileReader();
    reader.onload = (evt) => {
      try {
        const data = new Uint8Array(evt.target.result);
        const workbook = XLSX.read(data, { type: 'array' });
        const firstSheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[firstSheetName];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        
        // Take only first 5 rows for preview
        const previewRows = jsonData.slice(0, 5);
        setPreviewData(previewRows);
      } catch (error) {
        setError('Failed to parse Excel file. Please check the file format.');
        setPreviewData(null);
      }
    };
    reader.readAsArrayBuffer(selectedFile);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a file to upload');
      return;
    }

    setIsLoading(true);
    setError(null);
    setUploadStatus(null);

    try {
      // Create form data
      const formData = new FormData();
      formData.append('file', file);

      // Select the appropriate API URL based on role
      const apiUrl = role === 'faculty' 
        ? 'http://localhost:8765/ADMIN-SERVICE/api/admin/faculty/registerFile'
        : 'http://localhost:8765/ADMIN-SERVICE/api/admin/student/registerFile';

      // Make API call
      const response = await axios.post(apiUrl, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      // Handle successful upload
      setUploadStatus({
        success: true,
        message: `Successfully registered ${role === 'faculty' ? 'faculty' : 'student'} data!`
      });
      
      // Call onUpload if provided by parent component
      if (onUpload) {
        onUpload(file, role, response.data);
      }
    } catch (error) {
      console.error('Upload error:', error);
      setError(error.response?.data || `Failed to upload ${role} data. Please try again.`);
      setUploadStatus({
        success: false,
        message: `Something went wrong while uploading ${role} data.`
      });
    } finally {
      setIsLoading(false);
    }
  };

  // This prevents event propagation
  const stopPropagation = (e) => {
    e.stopPropagation();
  };

  // Reset state when modal closes
  const handleClose = () => {
    if (!isLoading) {
      onClose();
      // Clear form state after animation completes
      setTimeout(() => {
        if (uploadStatus?.success) {
          setFile(null);
          setPreviewData(null);
          setError(null);
          setUploadStatus(null);
        }
      }, 300);
    }
  };

  return (
    <AnimatePresence>
      {isOpen && (
        // Modal container
        <div className="fixed inset-0 z-50 overflow-hidden flex items-center justify-center">
          {/* Semi-transparent backdrop */}
          <div 
            className="absolute inset-0 bg-black bg-opacity-50" 
            onClick={isLoading ? undefined : handleClose}
          ></div>
          
          {/* Modal content */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className="relative bg-white dark:bg-gray-800 w-full max-w-2xl mx-4 rounded-lg shadow-xl z-10"
            onClick={stopPropagation}
          >
            {/* Close button */}
            <div className="absolute right-0 top-0 pr-4 pt-4">
              <button
                onClick={handleClose}
                disabled={isLoading}
                className="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300 transition-colors disabled:opacity-50"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {/* Form content */}
            <div className="p-6">
              <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
                Upload Excel File
              </h3>

              {uploadStatus?.success ? (
                // Success message
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-6 mb-6"
                >
                  <div className="flex flex-col items-center text-center">
                    <div className="rounded-full bg-green-100 dark:bg-green-800 p-3 mb-4">
                      <Check className="h-8 w-8 text-green-600 dark:text-green-400" />
                    </div>
                    <h4 className="text-xl font-semibold text-green-800 dark:text-green-400 mb-2">
                      Upload Successful!
                    </h4>
                    <p className="text-green-700 dark:text-green-300">
                      {uploadStatus.message}
                    </p>
                    <button
                      onClick={handleClose}
                      className="mt-6 px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                    >
                      Close
                    </button>
                  </div>
                </motion.div>
              ) : (
                <form onSubmit={handleSubmit} className="space-y-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      User Type
                    </label>
                    <div className="grid grid-cols-2 gap-4">
                      <label className={`flex items-center space-x-2 p-3 rounded-lg border ${role === 'student' ? 'border-blue-500 dark:border-blue-400 bg-blue-50 dark:bg-blue-900/20' : 'border-gray-200 dark:border-gray-700'} hover:bg-gray-50 dark:hover:bg-gray-700/50 cursor-pointer transition-colors`}>
                        <input
                          type="radio"
                          name="role"
                          value="student"
                          checked={role === 'student'}
                          onChange={() => setRole('student')}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                        />
                        <span className={`${role === 'student' ? 'text-blue-700 dark:text-blue-400 font-medium' : 'text-gray-700 dark:text-gray-300'}`}>Students</span>
                      </label>
                      <label className={`flex items-center space-x-2 p-3 rounded-lg border ${role === 'faculty' ? 'border-blue-500 dark:border-blue-400 bg-blue-50 dark:bg-blue-900/20' : 'border-gray-200 dark:border-gray-700'} hover:bg-gray-50 dark:hover:bg-gray-700/50 cursor-pointer transition-colors`}>
                        <input
                          type="radio"
                          name="role"
                          value="faculty"
                          checked={role === 'faculty'}
                          onChange={() => setRole('faculty')}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                        />
                        <span className={`${role === 'faculty' ? 'text-blue-700 dark:text-blue-400 font-medium' : 'text-gray-700 dark:text-gray-300'}`}>Faculty</span>
                      </label>
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Excel File
                    </label>
                    <div 
                      className={`border-2 border-dashed ${file ? 'border-green-300 dark:border-green-600' : 'border-gray-300 dark:border-gray-600'} rounded-lg p-8 text-center cursor-pointer hover:border-blue-500 dark:hover:border-blue-400 transition-colors`}
                      onClick={() => fileInputRef.current.click()}
                    >
                      <input
                        type="file"
                        ref={fileInputRef}
                        onChange={handleFileChange}
                        accept=".xlsx, .xls"
                        className="hidden"
                        disabled={isLoading}
                      />
                      {file ? (
                        <div className="flex flex-col items-center">
                          <FileSpreadsheet className="h-12 w-12 text-green-500 mb-2" />
                          <p className="text-sm font-medium text-gray-900 dark:text-white">{file.name}</p>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                            {(file.size / 1024).toFixed(2)} KB
                          </p>
                          <button
                            type="button"
                            className="mt-4 text-sm text-blue-600 dark:text-blue-400 hover:underline"
                            onClick={(e) => {
                              e.stopPropagation();
                              fileInputRef.current.click();
                            }}
                            disabled={isLoading}
                          >
                            Change File
                          </button>
                        </div>
                      ) : (
                        <div className="flex flex-col items-center">
                          <Upload className="h-12 w-12 text-gray-400 mb-2" />
                          <p className="text-sm font-medium text-gray-900 dark:text-white">
                            Click to upload or drag and drop
                          </p>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                            Excel files only (.xlsx, .xls)
                          </p>
                        </div>
                      )}
                    </div>
                    {error && (
                      <motion.div 
                        initial={{ opacity: 0, y: -10 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="mt-2 flex items-center text-red-600 dark:text-red-400"
                      >
                        <AlertCircle className="h-4 w-4 mr-1" />
                        <span className="text-sm">{error}</span>
                      </motion.div>
                    )}
                  </div>

                  {previewData && (
                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                    >
                      <h4 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                        Preview
                      </h4>
                      <div className="overflow-x-auto border border-gray-200 dark:border-gray-700 rounded-lg">
                        <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                          <thead className="bg-gray-50 dark:bg-gray-700">
                            <tr>
                              {previewData[0]?.map((header, index) => (
                                <th 
                                  key={index}
                                  className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider"
                                >
                                  {header}
                                </th>
                              ))}
                            </tr>
                          </thead>
                          <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                            {previewData.slice(1).map((row, rowIndex) => (
                              <tr key={rowIndex}>
                                {row.map((cell, cellIndex) => (
                                  <td 
                                    key={cellIndex}
                                    className="px-4 py-2 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300"
                                  >
                                    {cell}
                                  </td>
                                ))}
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                      <p className="mt-2 text-xs text-gray-500 dark:text-gray-400">
                        Showing first {previewData.length - 1} rows of data
                      </p>
                    </motion.div>
                  )}

                  <div className="flex justify-end gap-4 pt-4">
                    <button
                      type="button"
                      onClick={handleClose}
                      disabled={isLoading}
                      className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Cancel
                    </button>
                    <motion.button
                      whileHover={{ scale: isLoading ? 1 : 1.02 }}
                      whileTap={{ scale: isLoading ? 1 : 0.98 }}
                      type="submit"
                      disabled={!file || isLoading}
                      className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {isLoading ? (
                        <>
                          <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                          Uploading...
                        </>
                      ) : (
                        <>
                          <Check className="h-4 w-4 mr-2" />
                          Upload
                        </>
                      )}
                    </motion.button>
                  </div>
                </form>
              )}
            </div>

            {/* Loading overlay */}
            {isLoading && (
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="absolute inset-0 bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm flex flex-col items-center justify-center rounded-lg z-20"
              >
                <div className="text-center p-6 rounded-lg">
                  <div className="relative mb-4">
                    <div className="w-16 h-16 relative">
                      <motion.div
                        className="absolute inset-0 rounded-full border-4 border-t-blue-600 border-r-transparent border-b-transparent border-l-transparent"
                        animate={{ rotate: 360 }}
                        transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                      />
                      <motion.div
                        className="absolute inset-2 rounded-full border-4 border-t-blue-400 border-r-transparent border-b-transparent border-l-transparent"
                        animate={{ rotate: -360 }}
                        transition={{ duration: 1.5, repeat: Infinity, ease: "linear" }}
                      />
                    </div>
                  </div>
                  <h4 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                    Processing your {role} data
                  </h4>
                  <p className="text-gray-600 dark:text-gray-300">
                    Please wait while we register your {role === 'faculty' ? 'faculty members' : 'students'} in the system.
                  </p>
                </div>
              </motion.div>
            )}
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
};

export default UploadExcelModal;