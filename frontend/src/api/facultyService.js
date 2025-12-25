// frontend/src/api/facultyService.js
import axiosInstance from './axiosInstance';
import API_ENDPOINTS from './endpoints';

/**
 * Faculty Service
 * Handles all faculty-related API operations
 */
class FacultyService {
    /**
     * Get faculty dashboard data
     * @returns {Promise<Object>} Dashboard statistics and data
     */
    async getDashboard() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.DASHBOARD);
            console.log('✅ Faculty dashboard loaded');
            return response.data;
        } catch (error) {
            console.error('❌ Failed to load dashboard:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Get faculty profile
     * @returns {Promise<Object>} Faculty profile data
     */
    async getProfile() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.PROFILE);
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch profile:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Get list of all faculty members
     * @param {Object} params - Query parameters
     * @param {number} params.page - Page number
     * @param {number} params.size - Page size
     * @param {string} params.search - Search query
     * @returns {Promise<Object>} Paginated faculty list
     */
    async getFacultyList(params = {}) {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.LIST, { params });
            console.log(`✅ Loaded ${response.data.content?.length || 0} faculty members`);
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch faculty list:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Get faculty member by ID
     * @param {string|number} id - Faculty ID
     * @returns {Promise<Object>} Faculty details
     */
    async getFacultyById(id) {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.GET_BY_ID(id));
            return response.data;
        } catch (error) {
            console.error(`❌ Failed to fetch faculty ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Create new faculty member
     * @param {Object} facultyData - Faculty data
     * @returns {Promise<Object>} Created faculty
     */
    async createFaculty(facultyData) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.FACULTY.CREATE,
                facultyData
            );
            console.log('✅ Faculty member created successfully');
            return response.data;
        } catch (error) {
            console.error('❌ Failed to create faculty:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Update faculty member
     * @param {string|number} id - Faculty ID
     * @param {Object} facultyData - Updated faculty data
     * @returns {Promise<Object>} Updated faculty
     */
    async updateFaculty(id, facultyData) {
        try {
            const response = await axiosInstance.put(
                API_ENDPOINTS.FACULTY.UPDATE(id),
                facultyData
            );
            console.log(`✅ Faculty ${id} updated successfully`);
            return response.data;
        } catch (error) {
            console.error(`❌ Failed to update faculty ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Delete faculty member
     * @param {string|number} id - Faculty ID
     * @returns {Promise<void>}
     */
    async deleteFaculty(id) {
        try {
            await axiosInstance.delete(API_ENDPOINTS.FACULTY.DELETE(id));
            console.log(`✅ Faculty ${id} deleted successfully`);
        } catch (error) {
            console.error(`❌ Failed to delete faculty ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    // Project Management Methods

    /**
     * Get all projects
     * @param {Object} params - Query parameters
     * @returns {Promise<Object>} Projects list
     */
    async getProjects(params = {}) {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.PROJECTS, { params });
            console.log(`✅ Loaded ${response.data.content?.length || 0} projects`);
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch projects:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Get project by ID
     * @param {string|number} id - Project ID
     * @returns {Promise<Object>} Project details
     */
    async getProjectById(id) {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.PROJECT_BY_ID(id));
            return response.data;
        } catch (error) {
            console.error(`❌ Failed to fetch project ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Create new project
     * @param {Object} projectData - Project data
     * @returns {Promise<Object>} Created project
     */
    async createProject(projectData) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.FACULTY.CREATE_PROJECT,
                projectData
            );
            console.log('✅ Project created successfully:', response.data.name);
            return response.data;
        } catch (error) {
            console.error('❌ Failed to create project:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Update project
     * @param {string|number} id - Project ID
     * @param {Object} projectData - Updated project data
     * @returns {Promise<Object>} Updated project
     */
    async updateProject(id, projectData) {
        try {
            const response = await axiosInstance.put(
                API_ENDPOINTS.FACULTY.UPDATE_PROJECT(id),
                projectData
            );
            console.log(`✅ Project ${id} updated successfully`);
            return response.data;
        } catch (error) {
            console.error(`❌ Failed to update project ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Delete project
     * @param {string|number} id - Project ID
     * @returns {Promise<void>}
     */
    async deleteProject(id) {
        try {
            await axiosInstance.delete(API_ENDPOINTS.FACULTY.DELETE_PROJECT(id));
            console.log(`✅ Project ${id} deleted successfully`);
        } catch (error) {
            console.error(`❌ Failed to delete project ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Assign project to students
     * @param {Object} assignmentData - Assignment data
     * @param {string} assignmentData.projectId - Project ID
     * @param {Array<string>} assignmentData.studentIds - Array of student IDs
     * @returns {Promise<Object>} Assignment result
     */
    async assignProject(assignmentData) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.FACULTY.ASSIGN_PROJECT,
                assignmentData
            );
            console.log('✅ Project assigned successfully');
            return response.data;
        } catch (error) {
            console.error('❌ Failed to assign project:', error.message);
            throw this.handleError(error);
        }
    }

    // Student Management Methods

    /**
     * Get all students
     * @param {Object} params - Query parameters
     * @returns {Promise<Object>} Students list
     */
    async getStudents(params = {}) {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.STUDENTS, { params });
            console.log(`✅ Loaded ${response.data.content?.length || 0} students`);
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch students:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Get student by ID
     * @param {string|number} id - Student ID
     * @returns {Promise<Object>} Student details
     */
    async getStudentById(id) {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.STUDENT_BY_ID(id));
            return response.data;
        } catch (error) {
            console.error(`❌ Failed to fetch student ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Get students by project
     * @param {string|number} projectId - Project ID
     * @returns {Promise<Array>} List of students
     */
    async getStudentsByProject(projectId) {
        try {
            const response = await axiosInstance.get(
                API_ENDPOINTS.FACULTY.STUDENTS_BY_PROJECT(projectId)
            );
            return response.data;
        } catch (error) {
            console.error(`❌ Failed to fetch students for project ${projectId}:`, error.message);
            throw this.handleError(error);
        }
    }

    // Document Management Methods

    /**
     * Get all documents
     * @param {Object} params - Query parameters
     * @returns {Promise<Object>} Documents list
     */
    async getDocuments(params = {}) {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.DOCUMENTS, { params });
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch documents:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Upload document
     * @param {FormData} formData - Form data with file
     * @returns {Promise<Object>} Upload result
     */
    async uploadDocument(formData) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.FACULTY.UPLOAD_DOCUMENT,
                formData,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                }
            );
            console.log('✅ Document uploaded successfully');
            return response.data;
        } catch (error) {
            console.error('❌ Failed to upload document:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Delete document
     * @param {string|number} id - Document ID
     * @returns {Promise<void>}
     */
    async deleteDocument(id) {
        try {
            await axiosInstance.delete(API_ENDPOINTS.FACULTY.DELETE_DOCUMENT(id));
            console.log(`✅ Document ${id} deleted successfully`);
        } catch (error) {
            console.error(`❌ Failed to delete document ${id}:`, error.message);
            throw this.handleError(error);
        }
    }

    // Logs and Monitoring Methods

    /**
     * Get activity logs
     * @param {Object} params - Query parameters
     * @returns {Promise<Object>} Activity logs
     */
    async getActivityLogs(params = {}) {
        try {
            const response = await axiosInstance.get(
                API_ENDPOINTS.FACULTY.ACTIVITY_LOGS,
                { params }
            );
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch activity logs:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Get system logs
     * @param {Object} params - Query parameters
     * @returns {Promise<Object>} System logs
     */
    async getSystemLogs(params = {}) {
        try {
            const response = await axiosInstance.get(
                API_ENDPOINTS.FACULTY.SYSTEM_LOGS,
                { params }
            );
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch system logs:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Handle and format API errors
     * @param {Object} error - Axios error object
     * @returns {Error} Formatted error
     */
    handleError(error) {
        if (error.response) {
            const message = error.response.data?.message || 'An error occurred';
            const statusCode = error.response.status;
            return new Error(`${message} (Status: ${statusCode})`);
        } else if (error.request) {
            return new Error('No response from server. Please check your connection.');
        } else {
            return new Error(error.message || 'An unexpected error occurred');
        }
    }
}

export default new FacultyService();
