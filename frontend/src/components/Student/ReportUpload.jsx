import React, { useState } from 'react';
import { Upload, FileText } from 'lucide-react';

export const ReportUpload = ({ 
  onUpload, 
  disabled,
  currentUserId,
  submittedBy 
}) => {
  const [selectedFile, setSelectedFile] = useState(null);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && file.type === 'application/pdf') {
      setSelectedFile(file);
    }
  };

  const handleUpload = () => {
    if (selectedFile) {
      onUpload(selectedFile);
      setSelectedFile(null);
    }
  };

  if (disabled || (submittedBy && submittedBy !== currentUserId)) {
    return (
      <div className="text-center py-12">
        <FileText className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">
          Report already submitted
        </h3>
        <p className="mt-2 text-sm text-gray-500">
          A team report has already been uploaded for this project.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="border-2 border-dashed border-gray-300 rounded-lg p-12 text-center">
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
          <div className="mt-4">
            <p className="text-sm text-gray-500">
              Selected: {selectedFile.name}
            </p>
            <button
              onClick={handleUpload}
              className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            >
              Upload Report
            </button>
          </div>
        )}
      </div>
    </div>
  );
};