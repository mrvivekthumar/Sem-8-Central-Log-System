import { motion } from 'framer-motion';
import {
  Award,
  Briefcase,
  Camera,
  Code,
  Edit,
  ExternalLink,
  Github,
  Globe,
  Linkedin,
  Mail,
  MapPin,
  Phone,
  Plus,
  Save,
  Star,
  TrendingUp,
  X,
  Zap
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import {
  SiAndroid,
  SiCplusplus,
  SiDocker,
  SiFigma,
  SiGooglecloud,
  SiJavascript,
  SiMongodb,
  SiNodedotjs, SiPython,
  SiReact,
  SiTensorflow,
  SiTypescript
} from 'react-icons/si';
import axiosInstance from '../api/axiosInstance';
import { useAuth } from '../contexts/AuthContext';

const StudentProfile = () => {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [activeSection, setActiveSection] = useState('overview');
  const [studentData, setStudentData] = useState({
    name: '',
    email: '',
    bio: '',
    skills: [],
    githubProfileLink: '',
    linkedInProfileLink: '',
    portfolioLink: '',
    phone: '',
    location: '',
    ratings: 0,
    projectsCompleted: 0,
    currentProjects: 0
  });

  const [editForm, setEditForm] = useState({});
  const [newSkill, setNewSkill] = useState('');

  const skillIcons = {
    'React': SiReact,
    'JavaScript': SiJavascript,
    'TypeScript': SiTypescript,
    'Node.js': SiNodedotjs,
    'Python': SiPython,
    'C++': SiCplusplus,
    'TensorFlow': SiTensorflow,
    'Google Cloud': SiGooglecloud,
    'Figma': SiFigma,
    'Android': SiAndroid,
    'Docker': SiDocker,
    'MongoDB': SiMongodb
  };

  useEffect(() => {
    fetchStudentProfile();
  }, [user]);

  const fetchStudentProfile = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get(`/STUDENT-SERVICE/students/email/${user.username}`);
      setStudentData(response.data);
      setEditForm(response.data);
    } catch (error) {
      console.error('Error fetching profile:', error);
      toast.error('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    try {
      setLoading(true);
      await axiosInstance.put(`/STUDENT-SERVICE/students/${user.id}`, editForm);
      setStudentData(editForm);
      setIsEditing(false);
      toast.success('Profile updated successfully!');
    } catch (error) {
      console.error('Error updating profile:', error);
      toast.error('Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handleAddSkill = () => {
    if (newSkill && !editForm.skills?.includes(newSkill)) {
      setEditForm({
        ...editForm,
        skills: [...(editForm.skills || []), newSkill]
      });
      setNewSkill('');
    }
  };

  const handleRemoveSkill = (skillToRemove) => {
    setEditForm({
      ...editForm,
      skills: editForm.skills.filter(skill => skill !== skillToRemove)
    });
  };

  const statsCards = [
    {
      label: 'Rating',
      value: studentData.ratings?.toFixed(1) || '0.0',
      icon: Star,
      color: 'yellow',
      suffix: '/ 5.0'
    },
    {
      label: 'Completed',
      value: studentData.projectsCompleted || 0,
      icon: Briefcase,
      color: 'green',
      suffix: 'projects'
    },
    {
      label: 'Active',
      value: studentData.currentProjects || 0,
      icon: Zap,
      color: 'blue',
      suffix: 'ongoing'
    },
    {
      label: 'Skills',
      value: studentData.skills?.length || 0,
      icon: Code,
      color: 'purple',
      suffix: 'mastered'
    }
  ];

  if (loading && !studentData.name) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Header with Cover */}
      <div className="relative h-64 bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 overflow-hidden">
        <div className="absolute inset-0 opacity-20">
          <div className="absolute inset-0" style={{
            backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
          }} />
        </div>

        {/* Edit Button */}
        <div className="absolute top-6 right-6 z-10">
          {!isEditing ? (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setIsEditing(true)}
              className="px-4 py-2 bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 rounded-xl font-semibold flex items-center gap-2 shadow-lg hover:shadow-xl transition-all"
            >
              <Edit className="w-4 h-4" />
              Edit Profile
            </motion.button>
          ) : (
            <div className="flex gap-2">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleSave}
                disabled={loading}
                className="px-4 py-2 bg-green-600 text-white rounded-xl font-semibold flex items-center gap-2 shadow-lg hover:shadow-xl transition-all disabled:opacity-50"
              >
                <Save className="w-4 h-4" />
                Save
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => {
                  setIsEditing(false);
                  setEditForm(studentData);
                }}
                className="px-4 py-2 bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 rounded-xl font-semibold flex items-center gap-2 shadow-lg hover:shadow-xl transition-all"
              >
                <X className="w-4 h-4" />
                Cancel
              </motion.button>
            </div>
          )}
        </div>
      </div>

      {/* Profile Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-32 pb-12">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Left Column - Profile Card */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="lg:col-span-1"
          >
            <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden">
              {/* Avatar Section */}
              <div className="p-8 text-center">
                <div className="relative inline-block mb-4">
                  <div className="w-32 h-32 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white text-4xl font-bold">
                    {studentData.name?.charAt(0) || 'U'}
                  </div>
                  {isEditing && (
                    <button className="absolute bottom-0 right-0 p-2 bg-blue-600 text-white rounded-full hover:bg-blue-700 transition-colors">
                      <Camera className="w-4 h-4" />
                    </button>
                  )}
                </div>

                {isEditing ? (
                  <input
                    type="text"
                    value={editForm.name || ''}
                    onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                    className="w-full text-2xl font-bold text-center text-gray-900 dark:text-white bg-gray-50 dark:bg-gray-700 rounded-lg px-4 py-2 mb-2 border-2 border-blue-500 focus:outline-none"
                  />
                ) : (
                  <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
                    {studentData.name}
                  </h2>
                )}

                <p className="text-gray-600 dark:text-gray-400 mb-4 flex items-center justify-center gap-2">
                  <Mail className="w-4 h-4" />
                  {studentData.email}
                </p>

                {isEditing ? (
                  <textarea
                    value={editForm.bio || ''}
                    onChange={(e) => setEditForm({ ...editForm, bio: e.target.value })}
                    placeholder="Write a short bio about yourself..."
                    className="w-full text-sm text-gray-600 dark:text-gray-400 bg-gray-50 dark:bg-gray-700 rounded-lg px-4 py-3 border-2 border-gray-300 dark:border-gray-600 focus:border-blue-500 focus:outline-none resize-none"
                    rows={4}
                  />
                ) : (
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-6">
                    {studentData.bio || 'No bio added yet'}
                  </p>
                )}
              </div>

              {/* Contact Info */}
              <div className="border-t border-gray-200 dark:border-gray-700 p-6 space-y-4">
                {/* Phone */}
                <div className="flex items-center gap-3">
                  <Phone className="w-5 h-5 text-gray-400" />
                  {isEditing ? (
                    <input
                      type="tel"
                      value={editForm.phone || ''}
                      onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })}
                      placeholder="Phone number"
                      className="flex-1 bg-gray-50 dark:bg-gray-700 rounded-lg px-3 py-2 text-sm border-2 border-gray-300 dark:border-gray-600 focus:border-blue-500 focus:outline-none"
                    />
                  ) : (
                    <span className="text-sm text-gray-600 dark:text-gray-400">
                      {studentData.phone || 'Not provided'}
                    </span>
                  )}
                </div>

                {/* Location */}
                <div className="flex items-center gap-3">
                  <MapPin className="w-5 h-5 text-gray-400" />
                  {isEditing ? (
                    <input
                      type="text"
                      value={editForm.location || ''}
                      onChange={(e) => setEditForm({ ...editForm, location: e.target.value })}
                      placeholder="Your location"
                      className="flex-1 bg-gray-50 dark:bg-gray-700 rounded-lg px-3 py-2 text-sm border-2 border-gray-300 dark:border-gray-600 focus:border-blue-500 focus:outline-none"
                    />
                  ) : (
                    <span className="text-sm text-gray-600 dark:text-gray-400">
                      {studentData.location || 'Not provided'}
                    </span>
                  )}
                </div>

                {/* GitHub */}
                <div className="flex items-center gap-3">
                  <Github className="w-5 h-5 text-gray-400" />
                  {isEditing ? (
                    <input
                      type="url"
                      value={editForm.githubProfileLink || ''}
                      onChange={(e) => setEditForm({ ...editForm, githubProfileLink: e.target.value })}
                      placeholder="GitHub profile URL"
                      className="flex-1 bg-gray-50 dark:bg-gray-700 rounded-lg px-3 py-2 text-sm border-2 border-gray-300 dark:border-gray-600 focus:border-blue-500 focus:outline-none"
                    />
                  ) : studentData.githubProfileLink ? (
                    <a
                      href={studentData.githubProfileLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
                    >
                      View GitHub
                      <ExternalLink className="w-3 h-3" />
                    </a>
                  ) : (
                    <span className="text-sm text-gray-600 dark:text-gray-400">Not provided</span>
                  )}
                </div>

                {/* LinkedIn */}
                <div className="flex items-center gap-3">
                  <Linkedin className="w-5 h-5 text-gray-400" />
                  {isEditing ? (
                    <input
                      type="url"
                      value={editForm.linkedInProfileLink || ''}
                      onChange={(e) => setEditForm({ ...editForm, linkedInProfileLink: e.target.value })}
                      placeholder="LinkedIn profile URL"
                      className="flex-1 bg-gray-50 dark:bg-gray-700 rounded-lg px-3 py-2 text-sm border-2 border-gray-300 dark:border-gray-600 focus:border-blue-500 focus:outline-none"
                    />
                  ) : studentData.linkedInProfileLink ? (
                    <a
                      href={studentData.linkedInProfileLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
                    >
                      View LinkedIn
                      <ExternalLink className="w-3 h-3" />
                    </a>
                  ) : (
                    <span className="text-sm text-gray-600 dark:text-gray-400">Not provided</span>
                  )}
                </div>

                {/* Portfolio */}
                <div className="flex items-center gap-3">
                  <Globe className="w-5 h-5 text-gray-400" />
                  {isEditing ? (
                    <input
                      type="url"
                      value={editForm.portfolioLink || ''}
                      onChange={(e) => setEditForm({ ...editForm, portfolioLink: e.target.value })}
                      placeholder="Portfolio website URL"
                      className="flex-1 bg-gray-50 dark:bg-gray-700 rounded-lg px-3 py-2 text-sm border-2 border-gray-300 dark:border-gray-600 focus:border-blue-500 focus:outline-none"
                    />
                  ) : studentData.portfolioLink ? (
                    <a
                      href={studentData.portfolioLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
                    >
                      View Portfolio
                      <ExternalLink className="w-3 h-3" />
                    </a>
                  ) : (
                    <span className="text-sm text-gray-600 dark:text-gray-400">Not provided</span>
                  )}
                </div>
              </div>
            </div>
          </motion.div>

          {/* Right Column - Stats & Skills */}
          <div className="lg:col-span-2 space-y-8">
            {/* Stats Grid */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
            >
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {statsCards.map((stat, index) => {
                  const Icon = stat.icon;
                  return (
                    <motion.div
                      key={stat.label}
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: 0.2 + index * 0.1 }}
                      whileHover={{ y: -4 }}
                      className="bg-white dark:bg-gray-800 rounded-xl p-6 border border-gray-200 dark:border-gray-700 hover:shadow-lg transition-all"
                    >
                      <div className={`w-12 h-12 rounded-xl bg-${stat.color}-100 dark:bg-${stat.color}-900/20 flex items-center justify-center mb-3`}>
                        <Icon className={`w-6 h-6 text-${stat.color}-600 dark:text-${stat.color}-400`} />
                      </div>
                      <div className="text-3xl font-bold text-gray-900 dark:text-white mb-1">
                        {stat.value}
                      </div>
                      <div className="text-xs text-gray-600 dark:text-gray-400 font-medium">
                        {stat.label}
                      </div>
                      <div className="text-xs text-gray-500 dark:text-gray-500 mt-1">
                        {stat.suffix}
                      </div>
                    </motion.div>
                  );
                })}
              </div>
            </motion.div>

            {/* Skills Section */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6"
            >
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                  <Code className="w-6 h-6 text-blue-600" />
                  Skills & Technologies
                </h3>
              </div>

              {isEditing && (
                <div className="mb-4 flex gap-2">
                  <input
                    type="text"
                    value={newSkill}
                    onChange={(e) => setNewSkill(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleAddSkill()}
                    placeholder="Add a skill..."
                    className="flex-1 px-4 py-2 bg-gray-50 dark:bg-gray-700 border-2 border-gray-300 dark:border-gray-600 rounded-xl focus:border-blue-500 focus:outline-none"
                  />
                  <motion.button
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={handleAddSkill}
                    className="px-4 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors flex items-center gap-2"
                  >
                    <Plus className="w-4 h-4" />
                    Add
                  </motion.button>
                </div>
              )}

              <div className="flex flex-wrap gap-3">
                {(isEditing ? editForm.skills : studentData.skills)?.map((skill, index) => {
                  const SkillIcon = skillIcons[skill];
                  return (
                    <motion.div
                      key={index}
                      initial={{ opacity: 0, scale: 0.8 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: index * 0.05 }}
                      className="group relative px-4 py-2 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 border border-blue-200 dark:border-blue-800 rounded-xl flex items-center gap-2 hover:shadow-md transition-all"
                    >
                      {SkillIcon && <SkillIcon className="w-4 h-4 text-blue-600 dark:text-blue-400" />}
                      <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                        {skill}
                      </span>
                      {isEditing && (
                        <button
                          onClick={() => handleRemoveSkill(skill)}
                          className="ml-1 text-red-500 hover:text-red-700 opacity-0 group-hover:opacity-100 transition-opacity"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      )}
                    </motion.div>
                  );
                })}
                {(!studentData.skills || studentData.skills.length === 0) && !isEditing && (
                  <p className="text-gray-500 dark:text-gray-400">No skills added yet</p>
                )}
              </div>
            </motion.div>

            {/* Achievement Section */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4 }}
              className="bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 rounded-2xl p-8 text-white relative overflow-hidden"
            >
              <div className="absolute inset-0 opacity-20">
                <div className="absolute inset-0" style={{
                  backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
                }} />
              </div>
              <div className="relative z-10 flex items-center justify-between">
                <div>
                  <div className="flex items-center gap-2 mb-2">
                    <Award className="w-6 h-6" />
                    <span className="font-semibold">Keep Growing!</span>
                  </div>
                  <h3 className="text-2xl font-bold mb-2">Complete your profile to unlock opportunities</h3>
                  <p className="text-blue-100 mb-4">Add more skills and project details to get better matches</p>
                </div>
                <TrendingUp className="w-24 h-24 opacity-20" />
              </div>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentProfile;
