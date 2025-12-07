import axiosInstance from './axiosInstance';
import API_ENDPOINTS from './endpoints';

class AuthService {
    /**
     * Login user
     * @param {Object} credentials - { email, password }
     * @returns {Promise} Response with token and user data
     */
    async login(credentials) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.LOGIN,
                credentials
            );

            if (response.data.token) {
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('user', JSON.stringify(response.data.user));
            }

            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Register new user
     * @param {Object} userData - { name, email, password, role }
     * @returns {Promise} Response with user data
     */
    async register(userData) {
        try {
            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.REGISTER,
                userData
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Logout user
     */
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    /**
     * Get current user from localStorage
     * @returns {Object|null} User object or null
     */
    getCurrentUser() {
        try {
            const user = localStorage.getItem('user');
            return user ? JSON.parse(user) : null;
        } catch (error) {
            console.error('Error parsing user data:', error);
            return null;
        }
    }

    /**
     * Check if user is authenticated
     * @returns {boolean}
     */
    isAuthenticated() {
        return !!localStorage.getItem('token');
    }

    /**
     * Get user profile
     * @returns {Promise} User profile data
     */
    async getProfile() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.AUTH.PROFILE);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Validate token
     * @returns {Promise} Token validation response
     */
    async validateToken() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.AUTH.TOKEN);
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

export default new AuthService();
