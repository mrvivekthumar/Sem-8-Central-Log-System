import { motion } from 'framer-motion';
import {
  Book, FileText,
  Github,
  GraduationCap,
  Linkedin,
  Link as LinkIcon,
  Loader2,
  Mail,
  Pencil,
  Phone,
  Save,
  Star,
  Target,
  Upload,
  User,
  X
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import {
  SiAndroid,
  SiCplusplus,
  SiDatabricks,
  SiDevpost,
  SiFigma,
  SiGooglecloud,
  SiJavascript,
  SiNodedotjs,
  SiPython,
  SiReact,
  SiTensorflow,
  SiTypescript
} from 'react-icons/si';
import { useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

// Skill icons mapping
const skillIcons = {
  'React': <SiReact className="w-5 h-5 text-blue-500" />,
  'JavaScript': <SiJavascript className="w-5 h-5 text-yellow-500" />,
  'TypeScript': <SiTypescript className="w-5 h-5 text-blue-600" />,
  'Node.js': <SiNodedotjs className="w-5 h-5 text-green-500" />,
  'Python': <SiPython className="w-5 h-5 text-blue-400" />,
  'C++': <SiCplusplus className="w-5 h-5 text-blue-700" />,
  'Machine Learning': <SiTensorflow className="w-5 h-5 text-orange-500" />,
  'Cloud Computing': <SiGooglecloud className="w-5 h-5 text-blue-400" />,
  'DevOps': <SiDevpost className="w-5 h-5 text-purple-500" />,
  'Data Science': <SiDatabricks className="w-5 h-5 text-red-600" />,
  'UI/UX Design': <SiFigma className="w-5 h-5 text-pink-500" />,
  'Mobile Development': <SiAndroid className="w-5 h-5 text-green-600" />
};

// Available skills for selection
const availableSkills = Object.keys(skillIcons).map(name => ({ name, icon: skillIcons[name] }));

function StarRating({ rating }) {
  return (
    <div className="flex items-center gap-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <Star
          key={star}
          className={`w-4 h-4 ${star <= rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
            }`}
        />
      ))}
    </div>
  );
}

function StudentFacultyProfile() {
  const { studentId } = useParams();
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [studentData, setStudentData] = useState(null);
  const [formData, setFormData] = useState({
    githubProfileLink: '',
    bio: '',
    skills: [],
    linkedInUrl: '',
    cgpa: '',
    semesterNo: '',
    phoneNo: ''
  });
  const [isAddingProject, setIsAddingProject] = useState(false);
  const [editingProjectId, setEditingProjectId] = useState(null);
  const [projectFormData, setProjectFormData] = useState({
    name: '',
    descreption: '', // Note: This matches your API field name
    projectLink: ''
  });

  // Fetch student data
  useEffect(() => {
    const fetchStudent = async () => {
      try {
        setIsLoading(true);
        const response = await axiosInstance.get(`/STUDENT-SERVICE/students/${studentId}`);
        const data = response.data;
        setStudentData(data);
        setFormData({
          githubProfileLink: data.githubProfileLink || '',
          bio: data.bio || '',
          skills: data.skills || [],
          linkedInUrl: data.linkedInUrl || '',
          cgpa: data.cgpa || '',
          semesterNo: data.semesterNo || '',
          phoneNo: data.phoneNo || ''
        });
      } catch (error) {
        console.error('Error fetching student data:', error);
        toast.error('Failed to load student profile');
      } finally {
        setIsLoading(false);
      }
    };
    fetchStudent();
  }, [studentId]);

  // Fetch projects
  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await axiosInstance.get(`/STUDENT-SERVICE/api/personalProject/${studentId}`);
        setStudentData(prev => ({ ...prev, personalProjects: response.data }));
      } catch (error) {
        console.error('Error fetching projects:', error);
      }
    };
    if (studentData) {
      fetchProjects();
    }
  }, [studentData?.studentId]);

  const handleAvatarUpload = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('image', file);

    try {
      const response = await axiosInstance.post(
        `/STUDENT-SERVICE/students/student/${studentData.studentId}/upload-image`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );
      setStudentData(prev => ({ ...prev, imageUrl: response.data.imageUrl }));
      toast.success('Avatar updated successfully!');
    } catch (error) {
      console.error('Error uploading avatar:', error);
      toast.error('Failed to upload avatar');
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSkillChange = (skill) => {
    setFormData(prev => {
      if (prev.skills.includes(skill)) {
        return { ...prev, skills: prev.skills.filter(s => s !== skill) };
      } else {
        return { ...prev, skills: [...prev.skills, skill] };
      }
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    try {
      const response = await axiosInstance.put(
        `/STUDENT-SERVICE/students/${studentData.studentId}`,
        {
          ...formData,
          name: studentData.name,
          email: studentData.email
        }
      );
      setStudentData(prev => ({ ...prev, ...response.data }));
      setIsEditing(false);
      toast.success('Profile updated successfully!');
    } catch (error) {
      console.error('Error updating profile:', error);
      toast.error('Failed to update profile');
    } finally {
      setIsSaving(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    setFormData({
      githubProfileLink: studentData.githubProfileLink || '',
      bio: studentData.bio || '',
      skills: studentData.skills || [],
      linkedInUrl: studentData.linkedInUrl || '',
      cgpa: studentData.cgpa || '',
      semesterNo: studentData.semesterNo || '',
      phoneNo: studentData.phoneNo || ''
    });
  };

  // Project handlers
  const handleAddProject = async (projectData) => {
    try {
      const response = await axiosInstance.post(
        `/STUDENT-SERVICE/api/personalProject/${studentId}`,
        projectData
      );
      setStudentData(prev => ({
        ...prev,
        personalProjects: [...(prev.personalProjects || []), response.data]
      }));
      toast.success('Project added successfully!');
      return response.data;
    } catch (error) {
      console.error('Error adding project:', error);
      toast.error('Failed to add project');
      throw error;
    }
  };

  const handleUpdateProject = async (projectId, projectData) => {
    try {
      const response = await axiosInstance.put(
        `/STUDENT-SERVICE/api/personalProject/${projectId}`,
        projectData
      );
      setStudentData(prev => ({
        ...prev,
        personalProjects: prev.personalProjects.map(p =>
          p.personalProjectId === projectId ? response.data : p
        )
      }));
      toast.success('Project updated successfully!');
      return response.data;
    } catch (error) {
      console.error('Error updating project:', error);
      toast.error('Failed to update project');
      throw error;
    }
  };

  const handleDeleteProject = async (projectId) => {
    if (!window.confirm('Are you sure you want to delete this project?')) return;

    try {
      await axiosInstance.delete(`/STUDENT-SERVICE/api/personalProject/${projectId}`);
      setStudentData(prev => ({
        ...prev,
        personalProjects: prev.personalProjects.filter(p => p.personalProjectId !== projectId)
      }));
      toast.success('Project deleted successfully!');
      return true;
    } catch (error) {
      console.error('Error deleting project:', error);
      toast.error('Failed to delete project');
      throw error;
    }
  };

  const handleProjectInputChange = (e) => {
    const { name, value } = e.target;
    setProjectFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleProjectFormSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingProjectId) {
        await handleUpdateProject(editingProjectId, projectFormData);
      } else {
        await handleAddProject(projectFormData);
      }
      setProjectFormData({ name: '', descreption: '', projectLink: '' });
      setIsAddingProject(false);
      setEditingProjectId(null);
    } catch (error) {
      console.error('Failed to save project:', error);
    }
  };

  const startEditingProject = (project) => {
    setProjectFormData({
      name: project.name,
      descreption: project.descreption,
      projectLink: project.projectLink
    });
    setEditingProjectId(project.personalProjectId);
    setIsAddingProject(true);
  };

  const cancelProjectForm = () => {
    setProjectFormData({ name: '', descreption: '', projectLink: '' });
    setIsAddingProject(false);
    setEditingProjectId(null);
  };

  if (isLoading || !studentData) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="flex flex-col items-center space-y-4"
        >
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          >
            <Loader2 className="w-12 h-12 text-blue-500" />
          </motion.div>
          <p className="text-xl font-medium text-gray-700 dark:text-gray-300">
            Loading profile...
          </p>
        </motion.div>
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className={`min-h-screen ${isDarkMode ? 'bg-gray-900 text-white' : 'bg-gray-100 text-gray-900'}`}
    >
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto rounded-2xl shadow-2xl overflow-hidden border border-gray-200 dark:border-gray-700">
          {/* Header Section */}
          <div className="relative h-40 bg-gradient-to-r from-blue-500 via-purple-600 to-pink-600 overflow-hidden">
            <div className="absolute inset-0 opacity-20">
              <div className="absolute inset-0" style={{
                backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
              }} />
            </div>

            {/* Header Buttons */}
            <div className="absolute top-4 right-4 flex gap-2">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => setIsDarkMode(!isDarkMode)}
                className="px-4 py-2 rounded-xl bg-white/20 backdrop-blur-sm text-white hover:bg-white/30 transition-colors"
              >
                {isDarkMode ? '☀️ Light' : '🌙 Dark'}
              </motion.button>
              {!isEditing && (
                <motion.button
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={() => setIsEditing(true)}
                  className="px-4 py-2 rounded-xl bg-white/20 backdrop-blur-sm text-white hover:bg-white/30 transition-colors flex items-center gap-2"
                >
                  <Pencil className="w-4 h-4" />
                  <span>Edit</span>
                </motion.button>
              )}
            </div>
          </div>

          {/* Avatar Section */}
          <div className="relative -mt-20 px-6">
            <div className="relative w-32 h-32 mx-auto">
              {studentData.imageUrl ? (
                <img
                  src={studentData.imageUrl}
                  alt={studentData.name}
                  className="rounded-full border-4 border-white dark:border-gray-800 shadow-2xl object-cover w-full h-full"
                />
              ) : (
                <div className="rounded-full border-4 border-white dark:border-gray-800 shadow-2xl w-full h-full bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center text-white text-3xl font-bold">
                  {studentData.name?.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2)}
                </div>
              )}
              <label className="absolute bottom-0 right-0 bg-blue-600 p-2.5 rounded-full cursor-pointer hover:bg-blue-700 transition-colors shadow-lg border-2 border-white dark:border-gray-800">
                <Upload className="w-4 h-4 text-white" />
                <input
                  type="file"
                  className="hidden"
                  accept="image/*"
                  onChange={handleAvatarUpload}
                />
              </label>
            </div>
          </div>

          {/* Content Section */}
          {isEditing ? (
            /* Edit Mode */
            <div className={`p-6 ${isDarkMode ? 'bg-gray-800' : 'bg-white'}`}>
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Personal Details */}
                <div className="text-center mb-6">
                  <h1 className="text-3xl font-bold flex items-center justify-center gap-3 mb-2">
                    <User className="w-7 h-7" />
                    {studentData.name}
                  </h1>
                  <div className="flex items-center justify-center gap-2 mt-2">
                    <Mail className="w-4 h-4 text-gray-500" />
                    <span className="text-gray-600 dark:text-gray-400">{studentData.email}</span>
                  </div>
                  <div className="flex items-center justify-center gap-2 mt-2">
                    <Phone className="w-4 h-4 text-gray-500" />
                    <span className="text-gray-600 dark:text-gray-400">{studentData.phoneNo}</span>
                  </div>
                </div>

                {/* Editable Fields */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {/* GitHub */}
                  <div>
                    <label className="block text-sm font-semibold mb-2">GitHub Profile URL</label>
                    <div className="relative">
                      <input
                        name="githubProfileLink"
                        value={formData.githubProfileLink}
                        onChange={handleInputChange}
                        placeholder="https://github.com/yourusername"
                        className={`w-full pl-10 pr-4 py-3 rounded-xl border-2 focus:ring-2 focus:ring-blue-500 outline-none transition-all ${isDarkMode ? 'bg-gray-700 border-gray-600 text-white' : 'bg-white border-gray-200 text-gray-900'
                          }`}
                      />
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Github className="h-5 w-5 text-gray-400" />
                      </div>
                    </div>
                  </div>

                  {/* LinkedIn */}
                  <div>
                    <label className="block text-sm font-semibold mb-2">LinkedIn URL</label>
                    <div className="relative">
                      <input
                        name="linkedInUrl"
                        value={formData.linkedInUrl}
                        onChange={handleInputChange}
                        placeholder="https://linkedin.com/in/yourusername"
                        className={`w-full pl-10 pr-4 py-3 rounded-xl border-2 focus:ring-2 focus:ring-blue-500 outline-none transition-all ${isDarkMode ? 'bg-gray-700 border-gray-600 text-white' : 'bg-white border-gray-200 text-gray-900'
                          }`}
                      />
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Linkedin className="h-5 w-5 text-gray-400" />
                      </div>
                    </div>
                  </div>

                  {/* CGPA */}
                  <div>
                    <label className="block text-sm font-semibold mb-2">CGPA</label>
                    <input
                      name="cgpa"
                      type="number"
                      step="0.01"
                      min="0"
                      max="10.0"
                      value={formData.cgpa}
                      onChange={handleInputChange}
                      placeholder="Enter your CGPA"
                      className={`w-full px-4 py-3 rounded-xl border-2 focus:ring-2 focus:ring-blue-500 outline-none transition-all ${isDarkMode ? 'bg-gray-700 border-gray-600 text-white' : 'bg-white border-gray-200 text-gray-900'
                        }`}
                    />
                  </div>

                  {/* Semester */}
                  <div>
                    <label className="block text-sm font-semibold mb-2">Semester Number</label>
                    <input
                      name="semesterNo"
                      type="number"
                      min="1"
                      max="12"
                      value={formData.semesterNo}
                      onChange={handleInputChange}
                      placeholder="Current semester"
                      className={`w-full px-4 py-3 rounded-xl border-2 focus:ring-2 focus:ring-blue-500 outline-none transition-all ${isDarkMode ? 'bg-gray-700 border-gray-600 text-white' : 'bg-white border-gray-200 text-gray-900'
                        }`}
                    />
                  </div>

                  {/* Bio */}
                  <div className="md:col-span-2">
                    <label className="block text-sm font-semibold mb-2">Bio</label>
                    <textarea
                      name="bio"
                      value={formData.bio}
                      onChange={handleInputChange}
                      rows={4}
                      placeholder="Tell us about yourself, your interests, and your career goals..."
                      className={`w-full px-4 py-3 rounded-xl border-2 focus:ring-2 focus:ring-blue-500 outline-none resize-none transition-all ${isDarkMode ? 'bg-gray-700 border-gray-600 text-white' : 'bg-white border-gray-200 text-gray-900'
                        }`}
                    />
                  </div>
                </div>

                {/* Skills */}
                <div>
                  <label className="block text-sm font-semibold mb-4 flex items-center gap-2">
                    <Target className="w-5 h-5" />
                    Skills & Expertise
                  </label>
                  <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                    {availableSkills.map(({ name, icon }) => (
                      <label
                        key={name}
                        className={`flex items-center space-x-3 p-3 rounded-xl border-2 transition-all cursor-pointer ${formData.skills.includes(name)
                          ? isDarkMode
                            ? 'border-blue-700 bg-blue-900/30'
                            : 'border-blue-300 bg-blue-50'
                          : isDarkMode
                            ? 'border-gray-700 hover:border-gray-600'
                            : 'border-gray-200 hover:border-gray-300'
                          }`}
                      >
                        <input
                          type="checkbox"
                          checked={formData.skills.includes(name)}
                          onChange={() => handleSkillChange(name)}
                          className="sr-only"
                        />
                        <div className="flex items-center space-x-3">
                          <div className={formData.skills.includes(name) ? 'text-blue-500' : 'text-gray-400'}>
                            {icon}
                          </div>
                          <span className={`text-sm ${formData.skills.includes(name)
                            ? isDarkMode ? 'text-blue-300' : 'text-blue-700'
                            : isDarkMode ? 'text-gray-300' : 'text-gray-700'
                            }`}>
                            {name}
                          </span>
                        </div>
                        <div className={`ml-auto ${formData.skills.includes(name) ? 'opacity-100' : 'opacity-0'}`}>
                          <div className="w-4 h-4 rounded-full bg-blue-500 flex items-center justify-center">
                            <svg width="8" height="8" viewBox="0 0 8 8" fill="none">
                              <path d="M1 4L3 6L7 2" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                            </svg>
                          </div>
                        </div>
                      </label>
                    ))}
                  </div>
                </div>

                {/* Action Buttons */}
                <div className="flex justify-end gap-4 pt-4 border-t border-gray-200 dark:border-gray-700">
                  <motion.button
                    type="button"
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                    onClick={handleCancel}
                    className={`px-6 py-3 rounded-xl transition-colors flex items-center space-x-2 ${isDarkMode ? 'text-gray-300 hover:bg-gray-800' : 'text-gray-700 hover:bg-gray-100'
                      }`}
                  >
                    <X className="w-4 h-4" />
                    <span>Cancel</span>
                  </motion.button>
                  <motion.button
                    type="submit"
                    disabled={isSaving}
                    whileHover={{ scale: isSaving ? 1 : 1.02 }}
                    whileTap={{ scale: isSaving ? 1 : 0.98 }}
                    className="px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl hover:from-blue-700 hover:to-purple-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-2 shadow-lg"
                  >
                    {isSaving ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        <span>Saving...</span>
                      </>
                    ) : (
                      <>
                        <Save className="w-4 h-4" />
                        <span>Save Changes</span>
                      </>
                    )}
                  </motion.button>
                </div>
              </form>
            </div>
          ) : (
            /* View Mode */
            <div className={`p-6 ${isDarkMode ? 'bg-gray-800' : 'bg-white'}`}>
              {/* Personal Details */}
              <div className="text-center mb-6">
                <h1 className="text-3xl font-bold flex items-center justify-center gap-3 mb-2">
                  <User className="w-7 h-7" />
                  {studentData.name}
                </h1>
                <div className="flex items-center justify-center gap-2 mt-2">
                  <Mail className="w-4 h-4 text-gray-500" />
                  <span className="text-gray-600 dark:text-gray-400">{studentData.email}</span>
                </div>
                <div className="flex items-center justify-center gap-2 mt-2">
                  <Phone className="w-4 h-4 text-gray-500" />
                  <span className="text-gray-600 dark:text-gray-400">{studentData.phoneNo}</span>
                </div>
                {studentData.ratings > 0 && (
                  <div className="mt-4 flex items-center justify-center gap-4">
                    <div className="flex items-center gap-1">
                      <StarRating rating={studentData.ratings} />
                      <span className="text-sm ml-2 font-semibold">{studentData.ratings}</span>
                    </div>
                  </div>
                )}
              </div>

              {/* Academic Info */}
              <div className={`grid grid-cols-1 md:grid-cols-2 gap-4 p-4 rounded-xl mb-6 ${isDarkMode ? 'bg-gray-700' : 'bg-gray-50'
                }`}>
                {studentData.cgpa && (
                  <div className="flex items-center gap-2">
                    <GraduationCap className="w-5 h-5 text-blue-500" />
                    <span>CGPA: <strong>{studentData.cgpa}</strong></span>
                  </div>
                )}
                {studentData.semesterNo && (
                  <div className="flex items-center gap-2">
                    <Book className="w-5 h-5 text-blue-500" />
                    <span>Semester: <strong>{studentData.semesterNo}</strong></span>
                  </div>
                )}
              </div>

              {/* Bio */}
              {studentData.bio && (
                <div className="mb-6">
                  <div className="flex items-center gap-2 mb-3">
                    <FileText className="w-5 h-5 text-blue-500" />
                    <h2 className="text-xl font-semibold">Bio</h2>
                  </div>
                  <p className={`p-4 rounded-xl leading-relaxed ${isDarkMode ? 'bg-gray-700' : 'bg-gray-50'
                    }`}>
                    {studentData.bio}
                  </p>
                </div>
              )}

              {/* Social Links */}
              <div className="flex gap-4 mb-6">
                {studentData.githubProfileLink && (
                  <motion.a
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    href={studentData.githubProfileLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gray-800 text-white hover:bg-gray-700 transition-colors"
                  >
                    <Github className="w-5 h-5" />
                    GitHub
                  </motion.a>
                )}
                {studentData.linkedInUrl && (
                  <motion.a
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    href={studentData.linkedInUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-2 px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700 transition-colors"
                  >
                    <Linkedin className="w-5 h-5" />
                    LinkedIn
                  </motion.a>
                )}
              </div>

              {/* Skills */}
              <div className="mb-6">
                <div className="flex items-center gap-2 mb-4">
                  <Target className="w-5 h-5 text-blue-500" />
                  <h2 className="text-xl font-semibold">Skills</h2>
                </div>
                {studentData.skills && studentData.skills.length > 0 ? (
                  <div className="flex flex-wrap gap-3">
                    {studentData.skills.map((skill) => (
                      <motion.div
                        key={skill}
                        whileHover={{ scale: 1.05 }}
                        className={`flex items-center gap-2 px-4 py-2 rounded-xl ${isDarkMode ? 'bg-gray-700 text-white' : 'bg-blue-100 text-blue-800'
                          }`}
                      >
                        {skillIcons[skill] || null}
                        <span className="font-medium">{skill}</span>
                      </motion.div>
                    ))}
                  </div>
                ) : (
                  <div className={`p-4 text-center rounded-xl border border-dashed ${isDarkMode ? 'border-gray-700 text-gray-400' : 'border-gray-200 text-gray-500'
                    }`}>
                    No skills added yet.
                  </div>
                )}
              </div>

              {/* Personal Projects */}
              {studentData.personalProjects && studentData.personalProjects.length > 0 && (
                <div>
                  <div className="flex items-center gap-2 mb-4">
                    <LinkIcon className="w-5 h-5 text-purple-500" />
                    <h2 className="text-xl font-semibold">Personal Projects</h2>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {studentData.personalProjects.map((project) => (
                      <motion.div
                        key={project.personalProjectId}
                        whileHover={{ y: -4 }}
                        className={`p-5 rounded-xl border transition-all ${isDarkMode ? 'bg-gray-700 border-gray-600' : 'bg-blue-50 border-blue-200'
                          }`}
                      >
                        <h3 className="font-semibold text-lg mb-2">{project.name}</h3>
                        <p className={`text-sm mb-3 ${isDarkMode ? 'text-gray-300' : 'text-gray-600'
                          }`}>
                          {project.descreption}
                        </p>
                        {project.projectLink && (
                          <a
                            href={project.projectLink}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-500 hover:text-blue-600 text-sm flex items-center gap-1"
                          >
                            <LinkIcon className="w-4 h-4" />
                            View Project
                          </a>
                        )}
                      </motion.div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </motion.div>
  );
}

export default StudentFacultyProfile;