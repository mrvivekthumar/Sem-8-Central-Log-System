import { useState, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Upload, FileSpreadsheet, Check, AlertCircle } from 'lucide-react';
import * as XLSX from 'xlsx';

const UploadExcelModal = ({ isOpen, onClose, onUpload }) => {
  const [file, setFile] = useState(null);
  const [previewData, setPreviewData] = useState(null);
  const [role, setRole] = useState('student');
  const [error, setError] = useState(null);
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

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a file to upload');
      return;
    }
    onUpload(file, role);
  };

  // This prevents event propagation
  const stopPropagation = (e) => {
    e.stopPropagation();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        // Modal container - exactly matching RegisterModal structure
        <div className="fixed inset-0 z-50 overflow-hidden flex items-center justify-center">
          {/* Semi-transparent backdrop */}
          <div 
            className="absolute inset-0 bg-black bg-opacity-50" 
            onClick={onClose}
          ></div>
          
          {/* Modal content - stop propagation on this element */}
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
                onClick={onClose}
                className="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300 transition-colors"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {/* Form content */}
            <div className="p-6">
              <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
                Upload Excel File
              </h3>

              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    User Type
                  </label>
                  <div className="grid grid-cols-2 gap-4">
                    <label className="flex items-center space-x-2 p-3 rounded-lg border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50 cursor-pointer">
                      <input
                        type="radio"
                        name="role"
                        value="student"
                        checked={role === 'student'}
                        onChange={() => setRole('student')}
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                      />
                      <span className="text-gray-700 dark:text-gray-300">Students</span>
                    </label>
                    <label className="flex items-center space-x-2 p-3 rounded-lg border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50 cursor-pointer">
                      <input
                        type="radio"
                        name="role"
                        value="faculty"
                        checked={role === 'faculty'}
                        onChange={() => setRole('faculty')}
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                      />
                      <span className="text-gray-700 dark:text-gray-300">Faculty</span>
                    </label>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Excel File
                  </label>
                  <div 
                    className="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-8 text-center cursor-pointer hover:border-blue-500 dark:hover:border-blue-400 transition-colors"
                    onClick={() => fileInputRef.current.click()}
                  >
                    <input
                      type="file"
                      ref={fileInputRef}
                      onChange={handleFileChange}
                      accept=".xlsx, .xls"
                      className="hidden"
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
                    <div className="mt-2 flex items-center text-red-600 dark:text-red-400">
                      <AlertCircle className="h-4 w-4 mr-1" />
                      <span className="text-sm">{error}</span>
                    </div>
                  )}
                </div>

                {previewData && (
                  <div>
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
                  </div>
                )}

                <div className="flex justify-end gap-4 pt-4">
                  <button
                    type="button"
                    onClick={onClose}
                    className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
                  >
                    Cancel
                  </button>
                  <motion.button
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                    type="submit"
                    disabled={!file}
                    className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <Check className="h-4 w-4 mr-2" />
                    Upload
                  </motion.button>
                </div>
              </form>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
};

export default UploadExcelModal;