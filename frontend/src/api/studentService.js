import axiosInstance from './axiosInstance';
import API_ENDPOINTS from './endpoints';

class StudentService {
    /**
     * Get student dashboard data
     * @returns {Promise}
     */
    async getDashboard() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.STUDENT.DASHBOARD);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Get all student projects
     * @returns {Promise}
     */
    async getProjects() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.STUDENT.PROJECTS);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Get project by ID
     * @param {string|number} projectId
     * @returns {Promise}
     */
    async getProjectById(projectId) {
        try {
            const response = await axiosInstance.get(
                API_ENDPOINTS.STUDENT.PROJECT_DETAIL(projectId)
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Upload document
     * @param {FormData} formData - File upload data
     * @returns {Promise}
     */
    async uploadDocument(formData) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.STUDENT.UPLOAD_DOCUMENT,
                formData,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                }
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Get all documents
     * @returns {Promise}
     */
    async getDocuments() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.STUDENT.DOCUMENTS);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Update student profile
     * @param {string|number} studentId
     * @param {Object} profileData
     * @returns {Promise}
     */
    async updateProfile(studentId, profileData) {
        try {
            const response = await axiosInstance.put(
                API_ENDPOINTS.STUDENT.UPDATE(studentId),
                profileData
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Handle API errors
     * @param {Error} error
     * @returns {Error}
     */
    handleError(error) {
        const message = error.response?.data?.message ||
            error.response?.data?.error ||
            error.message ||
            'An error occurred';

        return new Error(message);
    }
}

export default new StudentService();
