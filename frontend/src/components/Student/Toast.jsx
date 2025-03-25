import React from 'react';
import { CheckCircle, XCircle, AlertTriangle } from 'lucide-react';

export const Toast = ({ show, message, type }) => {
  if (!show) return null;

  const icons = {
    success: <CheckCircle className="h-5 w-5 text-green-400" />,
    error: <XCircle className="h-5 w-5 text-red-400" />,
    warning: <AlertTriangle className="h-5 w-5 text-yellow-400" />
  };

  const styles = {
    success: 'bg-green-50 text-green-800 border-green-200',
    error: 'bg-red-50 text-red-800 border-red-200',
    warning: 'bg-yellow-50 text-yellow-800 border-yellow-200'
  };

  return (
    <div className="fixed top-4 right-4 z-50">
      <div className={`flex items-center space-x-2 px-4 py-3 rounded-lg border ${styles[type]}`}>
        {icons[type]}
        <span className="text-sm font-medium">{message}</span>
      </div>
    </div>
  );
};