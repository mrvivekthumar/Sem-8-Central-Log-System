import { motion } from 'framer-motion';
import { Award, Github, Linkedin, Mail, Phone, Star } from 'lucide-react';

const StudentProfile = ({ student, showDetails = false }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6 hover:shadow-lg transition-all"
    >
      <div className="flex items-start gap-4">
        {/* Avatar */}
        <div className="w-16 h-16 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white text-2xl font-bold flex-shrink-0">
          {student.name?.charAt(0) || 'S'}
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
          <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2">
            {student.name}
          </h3>

          <div className="space-y-2">
            {/* Email */}
            <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <Mail className="w-4 h-4 flex-shrink-0" />
              <span className="truncate">{student.email}</span>
            </div>

            {/* Phone */}
            {student.phone && (
              <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                <Phone className="w-4 h-4 flex-shrink-0" />
                <span>{student.phone}</span>
              </div>
            )}

            {/* GitHub */}
            {student.githubUrl && (
              <div className="flex items-center gap-2 text-sm">
                <Github className="w-4 h-4 text-gray-400 flex-shrink-0" />
                <a
                  href={student.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 truncate"
                >
                  GitHub Profile
                </a>
              </div>
            )}

            {/* LinkedIn */}
            {student.linkedInUrl && (
              <div className="flex items-center gap-2 text-sm">
                <Linkedin className="w-4 h-4 text-gray-400 flex-shrink-0" />
                <a
                  href={student.linkedInUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 truncate"
                >
                  LinkedIn Profile
                </a>
              </div>
            )}
          </div>

          {/* Additional Details */}
          {showDetails && (
            <div className="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
              {/* Skills */}
              {student.skills && student.skills.length > 0 && (
                <div className="mb-3">
                  <p className="text-xs font-semibold text-gray-600 dark:text-gray-400 mb-2 flex items-center gap-1">
                    <Award className="w-3 h-3" />
                    Skills
                  </p>
                  <div className="flex flex-wrap gap-2">
                    {student.skills.slice(0, 3).map((skill, idx) => (
                      <span
                        key={idx}
                        className="px-2 py-1 bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded text-xs font-medium"
                      >
                        {skill}
                      </span>
                    ))}
                    {student.skills.length > 3 && (
                      <span className="px-2 py-1 bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400 rounded text-xs">
                        +{student.skills.length - 3}
                      </span>
                    )}
                  </div>
                </div>
              )}

              {/* Rating */}
              {student.rating && (
                <div className="flex items-center gap-2">
                  <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                  <span className="text-sm font-semibold text-gray-900 dark:text-white">
                    {student.rating}
                  </span>
                  <span className="text-sm text-gray-600 dark:text-gray-400">/ 5.0</span>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </motion.div>
  );
};

export default StudentProfile;
