import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Edit, Trash, ChevronDown, ChevronUp, Star } from 'lucide-react';
import axios from 'axios';
import axiosInstance from '../../api/axiosInstance';
const UserTable = ({ userType, searchQuery, onUpdatePassword, onDeleteUser }) => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [sortField, setSortField] = useState('name');
  const [sortDirection, setSortDirection] = useState('asc');

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);
        let response;
        
        if (userType === 'students') {
          response = await axiosInstance.get('/STUDENT-SERVICE/students/all');
        } else {
          response = await axiosInstance.get('/FACULTY-SERVICE/api/faculty/all');
        }
        
        setUsers(response.data);
        setError(null);
      } catch (err) {
        setError('Failed to fetch user data. Please try again later.');
        console.error('Error fetching users:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [userType]);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  const getSortIcon = (field) => {
    if (sortField !== field) return <ChevronDown className="h-4 w-4 text-gray-400" />;
    return sortDirection === 'asc' ? 
      <ChevronUp className="h-4 w-4 text-blue-500" /> : 
      <ChevronDown className="h-4 w-4 text-blue-500" />;
  };
  
  // Filter users based on search query
  const filteredUsers = users.filter(user => 
    (user.name?.toLowerCase().includes(searchQuery.toLowerCase()) || false) ||
    (user.email?.toLowerCase().includes(searchQuery.toLowerCase()) || false)
  );

  // Sort users
  const sortedUsers = [...filteredUsers].sort((a, b) => {
    const valA = a[sortField] || '';
    const valB = b[sortField] || '';
    
    if (valA < valB) return sortDirection === 'asc' ? -1 : 1;
    if (valA > valB) return sortDirection === 'asc' ? 1 : -1;
    return 0;
  });

  // Map skills array to string for display
  const formatSkills = (skills) => {
    if (!skills || skills.length === 0) return 'No skills listed';
    return skills.join(', ');
  };

  // Render star rating component
  const StarRating = ({ rating }) => {
    if (!rating) return <span>Not rated</span>;
    
    const ratingValue = parseFloat(rating);
    const fullStars = Math.floor(ratingValue);
    const hasHalfStar = ratingValue - fullStars >= 0.5;
    const maxStars = 5;
    
    return (
      <div className="flex items-center">
        {[...Array(maxStars)].map((_, index) => {
          // Render filled star
          if (index < fullStars) {
            return <Star key={index} className="h-4 w-4 fill-yellow-400 text-yellow-400" />;
          } 
          // Render half star (we'll use a filled star with reduced opacity for simplicity)
          else if (index === fullStars && hasHalfStar) {
            return <Star key={index} className="h-4 w-4 fill-yellow-400 text-yellow-400 opacity-50" />;
          }
          // Render empty star
          return <Star key={index} className="h-4 w-4 text-gray-300 dark:text-gray-600" />;
        })}
        <span className="ml-2 text-sm text-gray-500 dark:text-gray-400">
          {ratingValue.toFixed(1)}
        </span>
      </div>
    );
  };

  if (loading) {
    return <div className="text-center py-8">Loading {userType}...</div>;
  }

  if (error) {
    return <div className="text-center py-8 text-red-500">{error}</div>;
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead className="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th 
                scope="col" 
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider cursor-pointer"
                onClick={() => handleSort('name')}
              >
                <div className="flex items-center space-x-1">
                  <span>Name</span>
                  {getSortIcon('name')}
                </div>
              </th>
              <th 
                scope="col" 
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider cursor-pointer"
                onClick={() => handleSort('email')}
              >
                <div className="flex items-center space-x-1">
                  <span>Email</span>
                  {getSortIcon('email')}
                </div>
              </th>
              {userType === 'students' && (
                <>
                  <th 
                    scope="col" 
                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider cursor-pointer"
                    onClick={() => handleSort('githubProfileLink')}
                  >
                    <div className="flex items-center space-x-1">
                      <span>GitHub Profile</span>
                      {getSortIcon('githubProfileLink')}
                    </div>
                  </th>
                  <th 
                    scope="col" 
                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider cursor-pointer"
                    onClick={() => handleSort('ratings')}
                  >
                    <div className="flex items-center space-x-1">
                      <span>Ratings</span>
                      {getSortIcon('ratings')}
                    </div>
                  </th>
                  <th 
                    scope="col" 
                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider cursor-pointer"
                    onClick={() => handleSort('studentAvaibility')}
                  >
                    <div className="flex items-center space-x-1">
                      <span>Availability</span>
                      {getSortIcon('studentAvaibility')}
                    </div>
                  </th>
                </>
              )}
              <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            {sortedUsers.map((user, index) => (
              <motion.tr 
                key={index}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                whileHover={{ backgroundColor: 'rgba(0, 0, 0, 0.02)' }}
                className="hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors"
              >
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm font-medium text-gray-900 dark:text-white">
                    {user.name || 'No name provided'}
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm text-gray-500 dark:text-gray-300">
                    {user.email || 'No email provided'}
                  </div>
                </td>
                {userType === 'students' && (
                  <>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500 dark:text-gray-300">
                        {user.githubProfileLink ? (
                          <a 
                            href={user.githubProfileLink} 
                            target="_blank" 
                            rel="noopener noreferrer"
                            className="text-blue-500 hover:underline"
                          >
                            GitHub Profile
                          </a>
                        ) : 'No GitHub profile'}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm">
                        <StarRating rating={user.ratings} />
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        user.studentAvaibility === 'AVAILABLE' 
                          ? 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400' 
                          : 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400'
                      }`}>
                        {user.studentAvaibility || 'Unknown'}
                      </span>
                    </td>
                  </>
                )}
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div className="flex items-center justify-end space-x-3">
                    <motion.button
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => onUpdatePassword(user)}
                      className="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                    >
                      <Edit className="h-4 w-4" />
                    </motion.button>
                    <motion.button
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => onDeleteUser(index)}
                      className="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                    >
                      <Trash className="h-4 w-4" />
                    </motion.button>
                  </div>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>
      </div>
      {sortedUsers.length === 0 && (
        <div className="text-center py-8 text-gray-500 dark:text-gray-400">
          No {userType} found matching your search criteria.
        </div>
      )}
    </div>
  );
};

export default UserTable;