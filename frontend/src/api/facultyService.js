import axiosInstance from './axiosInstance';
import API_ENDPOINTS from './endpoints';

class FacultyService {
    /**
     * Get faculty dashboard data
     * @returns {Promise}
     */
    async getDashboard() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.DASHBOARD);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Get all faculty projects
     * @returns {Promise}
     */
    async getProjects() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.PROJECTS);
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
                API_ENDPOINTS.FACULTY.PROJECT_DETAIL(projectId)
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Create new project
     * @param {Object} projectData
     * @returns {Promise}
     */
    async createProject(projectData) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.FACULTY.PROJECTS,
                projectData
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Update project
     * @param {string|number} projectId
     * @param {Object} projectData
     * @returns {Promise}
     */
    async updateProject(projectId, projectData) {
        try {
            const response = await axiosInstance.put(
                API_ENDPOINTS.FACULTY.PROJECT_DETAIL(projectId),
                projectData
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Delete project
     * @param {string|number} projectId
     * @returns {Promise}
     */
    async deleteProject(projectId) {
        try {
            const response = await axiosInstance.delete(
                API_ENDPOINTS.FACULTY.PROJECT_DETAIL(projectId)
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Get all students
     * @returns {Promise}
     */
    async getStudents() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.FACULTY.STUDENTS);
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

export default new FacultyService();
