import { User, Mail, Github } from 'lucide-react';

const StudentProfile = ({ student }) => {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-4 shadow-sm">
      <div className="space-y-3">
        <div className="flex items-center space-x-3">
          <User className="w-4 h-4 text-gray-400" />
          <span className="text-gray-900 dark:text-white font-medium">
            {student.name}
          </span>
        </div>
        
        <div className="flex items-center space-x-3">
          <Mail className="w-4 h-4 text-gray-400" />
          <span className="text-gray-600 dark:text-gray-300">
            {student.email}
          </span>
        </div>

        {student.githubUrl && (
          <div className="flex items-center space-x-3">
            <Github className="w-4 h-4 text-gray-400" />
            <a
              href={student.githubUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300"
            >
              GitHub Profile
            </a>
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentProfile;