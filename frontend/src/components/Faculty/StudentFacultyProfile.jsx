import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  User,
  Mail,
  Star,
  GraduationCap,
  Book,
  FileText,
  Github,
  Linkedin,
  Target,
  Link as LinkIcon,
  Upload,
  CircleDot,
  Pencil,
  X,
  Save,
  Loader2,
  Phone
} from 'lucide-react';

import {
  SiReact, SiJavascript, SiTypescript, SiNodedotjs, SiPython, SiCplusplus,
  SiTensorflow, SiGooglecloud, SiFigma, SiAndroid, SiDevpost, SiDatabricks
} from "react-icons/si";
import { useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

// Skill icons mapping
const skillIcons = {
  "React": <SiReact className="w-5 h-5 text-blue-500" />,
  "JavaScript": <SiJavascript className="w-5 h-5 text-yellow-500" />,
  "TypeScript": <SiTypescript className="w-5 h-5 text-blue-600" />,
  "Node.js": <SiNodedotjs className="w-5 h-5 text-green-500" />,
  "Python": <SiPython className="w-5 h-5 text-blue-400" />,
  "C++": <SiCplusplus className="w-5 h-5 text-blue-700" />,
  "Machine Learning": <SiTensorflow className="w-5 h-5 text-orange-500" />,
  "Cloud Computing": <SiGooglecloud className="w-5 h-5 text-blue-400" />,
  "DevOps": <SiDevpost className="w-5 h-5 text-purple-500" />,
  "Data Science": <SiDatabricks className="w-5 h-5 text-red-600" />,
  "UI/UX Design": <SiFigma className="w-5 h-5 text-pink-500" />,
  "Mobile Development": <SiAndroid className="w-5 h-5 text-green-600" />,
};

// Available skills for selection
const availableSkills = Object.keys(skillIcons).map(name => ({
  name,
  icon: skillIcons[name]
}));

function StarRating({ rating }) {
  return (
    <div className="flex items-center">
      {[1, 2, 3, 4, 5].map((star) => (
        <Star
          key={star}
          className={`w-4 h-4 ${star <= rating
              ? "fill-yellow-400 text-yellow-400"
              : "text-gray-300"
            }`}
        />
      ))}
    </div>
  );
}

function StudentFacultyProfile() {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [studentData, setStudentData] = useState(null);
  const [formData, setFormData] = useState({
    githubProfileLink: "",
    bio: "",
    skills: [],
    linkedInUrl: "",
    cgpa: "",
    semesterNo: "",
    phoneNo: "",
  });
  const [projectFormData, setProjectFormData] = useState({
    name: "",
    descreption: "", // Note: This matches your API field name with the typo
    projectLink: ""
  });
  const { studentId } = useParams();
  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await axiosInstance.get(
          `/STUDENT-SERVICE/api/personalProject/${studentId}`
        );
        setStudentData(prev => ({
          ...prev,
          personalProjects: response.data
        }));
      } catch (error) {
        console.error("Error fetching projects:", error);
      }
    };

    if (studentData) {
      fetchProjects();
    }
  }, [studentData?.studentId]);
  // Fetch student data
  useEffect(() => {
    const fetchStudent = async () => {
      try {
        setIsLoading(true);
        // Assuming you have the student ID
        const response = await axiosInstance.get(
          `/STUDENT-SERVICE/students/${studentId}`
        );
        const data = response.data;
        setStudentData(data);
        setFormData({
          githubProfileLink: data.githubProfileLink || "",
          bio: data.bio || "",
          skills: data.skills || [],
          linkedInUrl: data.linkedInUrl || "",
          cgpa: data.cgpa || "",
          semesterNo: data.semesterNo || "",
          phoneNo: data.phoneNo || "",
        });
      } catch (error) {
        console.error("Error fetching student data:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchStudent();
  }, []);

  const handleAvatarUpload = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('image', file);

    try {
      const studentId = studentData.studentId;
      const response = await axiosInstance.post(
        `/STUDENT-SERVICE/students/student/${studentId}/upload-image`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      );
      setStudentData(prev => ({
        ...prev,
        imageUrl: response.data.imageUrl
      }));
    } catch (error) {
      console.error('Error uploading avatar:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSkillChange = (skill) => {
    setFormData(prev => {
      if (prev.skills.includes(skill)) {
        return {
          ...prev,
          skills: prev.skills.filter(s => s !== skill)
        };
      } else {
        return {
          ...prev,
          skills: [...prev.skills, skill]
        };
      }
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    try {
      const studentId = studentData.studentId;
      const response = await axiosInstance.put(
        `/STUDENT-SERVICE/students/student/${studentId}`,
        {
          ...formData,
          name: studentData.name,
          email: studentData.email,
        }
      );

      setStudentData(prev => ({
        ...prev,
        ...response.data
      }));
      setIsEditing(false);
    } catch (error) {
      console.error("Error updating profile:", error);
    } finally {
      setIsSaving(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    setFormData({
      githubProfileLink: studentData.githubProfileLink || "",
      bio: studentData.bio || "",
      skills: studentData.skills || [],
      linkedInUrl: studentData.linkedInUrl || "",
      cgpa: studentData.cgpa || "",
      semesterNo: studentData.semesterNo || "",
      phoneNo: studentData.phoneNo || "",
    });
  };

  if (isLoading || !studentData) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="w-12 h-12 text-blue-500 animate-spin" />
          <p className="text-xl font-medium text-gray-700 dark:text-gray-300">Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className={`min-h-screen ${isDarkMode ? 'bg-gray-900 text-white' : 'bg-gray-100 text-gray-900'}`}>
      <div className="container mx-auto px-4 py-8">
        <div className={`max-w-4xl mx-auto rounded-xl shadow-lg overflow-hidden ${isDarkMode ? 'bg-gray-800' : 'bg-white'
          }`}>
          {/* Header Section */}
          <div className="relative h-32 bg-gradient-to-r from-blue-500 to-purple-600">
            {/* <button
              onClick={() => setIsDarkMode(!isDarkMode)}
              className="absolute top-4 right-4 px-4 py-2 rounded-lg bg-white/20 text-white hover:bg-white/30 transition"
            >
              {isDarkMode ? '☀️ Light' : '🌙 Dark'}
            </button> */}

          </div>

          {/* Avatar Section */}
          <div className="relative -mt-16 px-6">
            <div className="relative w-32 h-32 mx-auto">
              {studentData.imageUrl ? (
                <img
                  src={studentData.imageUrl}
                  alt={studentData.name}
                  className="rounded-full border-4 border-white shadow-lg object-cover w-full h-full"
                />
              ) : (
                <div className="rounded-full border-4 border-white shadow-lg w-full h-full bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center text-white text-2xl font-bold">
                  {studentData.name.split(" ").map(n => n[0]).join("").toUpperCase().substring(0, 2)}
                </div>
              )}

            </div>
          </div>

          {/* Content Section */}
          {isEditing ? (
            // Edit Mode
            <div className="p-6">
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Personal Details */}
                <div className="text-center mb-6">
                  <h1 className="text-2xl font-bold flex items-center justify-center gap-2">
                    <User className="w-5 h-5" />
                    {studentData.name}
                  </h1>
                  <div className="flex items-center justify-center gap-2 mt-2">
                    <Mail className="w-4 h-4" />
                    <span>{studentData.email}</span>
                  </div>
                </div>
                <div className="flex items-center justify-center gap-2 mt-2">
                  <Phone className="w-4 h-4" />
                  <span>{studentData.phoneNo}</span>
                </div>

                {/* Editable Fields */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium mb-2">
                      GitHub Profile URL
                    </label>
                    <div className="relative">
                      <input
                        name="githubProfileLink"
                        value={formData.githubProfileLink}
                        onChange={handleInputChange}
                        placeholder="https://github.com/yourusername"
                        className={`w-full pl-10 pr-4 py-3 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none ${isDarkMode
                            ? 'bg-gray-700 border-gray-600 text-white'
                            : 'bg-white border-gray-200 text-gray-900'
                          }`}
                      />
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Github className="h-5 w-5 text-gray-400" />
                      </div>
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-2">
                      LinkedIn URL
                    </label>
                    <div className="relative">
                      <input
                        name="linkedInUrl"
                        value={formData.linkedInUrl}
                        onChange={handleInputChange}
                        placeholder="https://linkedin.com/in/yourusername"
                        className={`w-full pl-10 pr-4 py-3 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none ${isDarkMode
                            ? 'bg-gray-700 border-gray-600 text-white'
                            : 'bg-white border-gray-200 text-gray-900'
                          }`}
                      />
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Linkedin className="h-5 w-5 text-gray-400" />
                      </div>
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-2">
                      CGPA
                    </label>
                    <input
                      name="cgpa"
                      type="number"
                      step="0.01"
                      min="0"
                      max="10.0"
                      value={formData.cgpa}
                      onChange={handleInputChange}
                      placeholder="Enter your CGPA"
                      className={`w-full px-4 py-3 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none ${isDarkMode
                          ? 'bg-gray-700 border-gray-600 text-white'
                          : 'bg-white border-gray-200 text-gray-900'
                        }`}
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-2">
                      Semester Number
                    </label>
                    <input
                      name="semesterNo"
                      type="number"
                      min="1"
                      max="12"
                      value={formData.semesterNo}
                      onChange={handleInputChange}
                      placeholder="Current semester"
                      className={`w-full px-4 py-3 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none ${isDarkMode
                          ? 'bg-gray-700 border-gray-600 text-white'
                          : 'bg-white border-gray-200 text-gray-900'
                        }`}
                    />
                  </div>



                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium mb-2">
                      Bio
                    </label>
                    <textarea
                      name="bio"
                      value={formData.bio}
                      onChange={handleInputChange}
                      rows={4}
                      placeholder="Tell us about yourself, your interests, and your career goals..."
                      className={`w-full px-4 py-3 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none resize-none ${isDarkMode
                          ? 'bg-gray-700 border-gray-600 text-white'
                          : 'bg-white border-gray-200 text-gray-900'
                        }`}
                    />
                  </div>
                </div>

                {/* Skills */}
                <div>
                  <label className="block text-sm font-medium mb-4">
                    Skills & Expertise
                  </label>
                  <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                    {availableSkills.map(({ name, icon }) => (
                      <label
                        key={name}
                        className={`flex items-center space-x-3 p-3 rounded-lg border transition-all cursor-pointer ${formData.skills.includes(name)
                            ? isDarkMode
                              ? "border-blue-700 bg-blue-900/30"
                              : "border-blue-300 bg-blue-50"
                            : isDarkMode
                              ? "border-gray-700 hover:border-gray-600"
                              : "border-gray-200 hover:border-gray-300"
                          }`}
                      >
                        <input
                          type="checkbox"
                          checked={formData.skills.includes(name)}
                          onChange={() => handleSkillChange(name)}
                          className="sr-only"
                        />
                        <div className="flex items-center space-x-3">
                          <div className={`${formData.skills.includes(name) ? "text-blue-500" : "text-gray-400"}`}>
                            {icon}
                          </div>
                          <span className={`text-sm ${formData.skills.includes(name)
                              ? isDarkMode ? "text-blue-300" : "text-blue-700"
                              : isDarkMode ? "text-gray-300" : "text-gray-700"
                            }`}>{name}</span>
                        </div>
                        <div className={`ml-auto ${formData.skills.includes(name) ? "opacity-100" : "opacity-0"}`}>
                          <div className="w-4 h-4 rounded-full bg-blue-500 flex items-center justify-center">
                            <svg width="8" height="8" viewBox="0 0 8 8" fill="none" xmlns="http://www.w3.org/2000/svg">
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
                  <button
                    type="button"
                    onClick={handleCancel}
                    className={`px-6 py-3 rounded-lg transition-colors flex items-center space-x-2 ${isDarkMode
                        ? "text-gray-300 hover:bg-gray-800"
                        : "text-gray-700 hover:bg-gray-100"
                      }`}
                  >
                    <X className="w-4 h-4" />
                    <span>Cancel</span>
                  </button>
                  <button
                    type="submit"
                    disabled={isSaving}
                    className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-2"
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
                  </button>
                </div>
              </form>
            </div>
          ) : (
            // View Mode
            <div className="p-6">
              {/* Personal Details */}
              <div className="text-center mb-6">
                <h1 className="text-2xl font-bold flex items-center justify-center gap-2">
                  <User className="w-5 h-5" />
                  {studentData.name}
                </h1>
                <div className="flex items-center justify-center gap-2 mt-2">
                  <Mail className="w-4 h-4" />
                  <span>{studentData.email}</span>
                </div>
                <div className="flex items-center justify-center gap-2 mt-2">
                  <Phone className="w-4 h-4" />
                  <span>{studentData.phoneNo}</span>
                </div>
                <div className="mt-2 flex items-center justify-center gap-4">

                  {studentData.ratings > 0 && (
                    <div className="flex items-center gap-1">
                      <StarRating rating={studentData.ratings} />
                      <span className="text-sm">({studentData.ratings})</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Academic Info */}
              <div className={`grid grid-cols-1 md:grid-cols-2 gap-4 p-4 rounded-lg mb-6 ${isDarkMode ? 'bg-gray-700' : 'bg-gray-50'
                }`}>
                {studentData.cgpa && (
                  <div className="flex items-center gap-2">
                    <GraduationCap className="w-5 h-5 text-blue-500" />
                    <span>CGPA: {studentData.cgpa}</span>
                  </div>
                )}
                {studentData.semesterNo && (
                  <div className="flex items-center gap-2">
                    <Book className="w-5 h-5 text-blue-500" />
                    <span>Semester {studentData.semesterNo}</span>
                  </div>
                )}
              </div>

              {/* Bio */}
              {studentData.bio && (
                <div className="mb-6">
                  <div className="flex items-center gap-2 mb-2">
                    <FileText className="w-5 h-5 text-blue-500" />
                    <h2 className="text-xl font-semibold">Bio</h2>
                  </div>
                  <p className={`p-4 rounded-lg ${isDarkMode ? 'bg-gray-700' : 'bg-gray-50'}`}>
                    {studentData.bio}
                  </p>
                </div>
              )}

              {/* Social Links */}
              <div className="flex gap-4 mb-6">
                {studentData.githubProfileLink && (
                  <a
                    href={studentData.githubProfileLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-2 px-4 py-2 rounded-lg bg-gray-800 text-white hover:bg-gray-700 transition"
                  >
                    <Github className="w-5 h-5" />
                    GitHub
                  </a>
                )}
                {studentData.linkedInUrl && (
                  <a
                    href={studentData.linkedInUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
                  >
                    <Linkedin className="w-5 h-5" />
                    LinkedIn
                  </a>
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
                      <div
                        key={skill}
                        className={`flex items-center gap-2 px-3 py-2 rounded-lg ${isDarkMode
                            ? 'bg-gray-700 text-white'
                            : 'bg-blue-100 text-blue-800'
                          }`}
                      >
                        {skillIcons[skill] || null}
                        <span>{skill}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className={`p-4 text-center rounded-lg border border-dashed ${isDarkMode ? 'border-gray-700 text-gray-400' : 'border-gray-200 text-gray-500'
                    }`}>
                    No skills added yet.
                  </div>
                )}
              </div>

              

              {/* Personal Projects */}
              {studentData.personalProjects && studentData.personalProjects.length > 0 && (
                <div>
                  <div className="flex items-center gap-2 mb-4">
                    <FileText className="w-5 h-5 text-purple-500" />
                    <h2 className="text-xl font-semibold">Personal Projects</h2>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {studentData.personalProjects.map((project) => (
                      <div
                        key={project.personalProjectId}
                        className={`p-4 rounded-lg ${isDarkMode ? 'bg-gray-700' : 'bg-blue-50'
                          }`}
                      >
                        <h3 className="font-semibold mb-2">{project.name}</h3>
                        <p className="text-sm mb-3">{project.descreption}</p>
                        {project.projectLink && (

                          <a href={project.projectLink}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-500 hover:text-blue-600 text-sm flex items-center gap-1"
                          >
                            <LinkIcon className="w-4 h-4" />
                            View Project
                          </a>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              )}

            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default StudentFacultyProfile;