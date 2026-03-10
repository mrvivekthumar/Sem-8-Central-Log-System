// frontend/src/api/authService.js
import axiosInstance from './axiosInstance';
import API_ENDPOINTS from './endpoints';

/**
 * Authentication Service
 * Handles all authentication-related API calls
 * Updated: December 27, 2025
 */
class AuthService {
    /**
     * User login
     * @param {Object} credentials - User login credentials
     * @param {string} credentials.email - User email
     * @param {string} credentials.password - User password
     * @returns {Promise<Object>} Authentication response with tokens
     */
    async login(credentials) {
        try {
            console.log('🔐 Attempting login for:', credentials.email);

            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.LOGIN,
                credentials
            );

            const { accessToken, refreshToken, user } = response.data;

            // Store tokens and user info
            this.setTokens(accessToken, refreshToken);
            this.setUser(user);

            console.log('✅ Login successful:', user.email, '| Role:', user.role);
            return response.data;
        } catch (error) {
            console.error('❌ Login failed:', error.response?.data?.message || error.message);
            throw this.handleError(error);
        }
    }

    /**
     * User registration
     * @param {Object} userData - New user data
     * @param {string} userData.email - User email
     * @param {string} userData.password - User password
     * @param {string} userData.name - User name
     * @param {string} userData.role - User role (STUDENT/FACULTY)
     * @returns {Promise<Object>} Registration response
     */
    async register(userData) {
        try {
            console.log('📝 Attempting registration for:', userData.email);

            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.REGISTER,
                userData
            );

            console.log('✅ Registration successful:', userData.email);
            return response.data;
        } catch (error) {
            console.error('❌ Registration failed:', error.response?.data?.message || error.message);
            throw this.handleError(error);
        }
    }

    /**
     * User logout
     * @returns {Promise<void>}
     */
    async logout() {
        try {
            const refreshToken = this.getRefreshToken();

            if (refreshToken) {
                console.log('🚪 Logging out...');
                await axiosInstance.post(API_ENDPOINTS.AUTH.LOGOUT, { refreshToken });
            }

            this.clearAuth();
            console.log('✅ Logout successful');
        } catch (error) {
            console.error('❌ Logout error:', error.message);
            // Clear auth even if logout request fails
            this.clearAuth();
        }
    }

    /**
     * Refresh access token
     * @returns {Promise<string>} New access token
     */
    async refreshToken() {
        try {
            const refreshToken = this.getRefreshToken();

            if (!refreshToken) {
                throw new Error('No refresh token available');
            }

            console.log('🔄 Refreshing access token...');

            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.REFRESH_TOKEN,
                { refreshToken }
            );

            const { accessToken } = response.data;
            this.setAccessToken(accessToken);

            console.log('✅ Token refreshed successfully');
            return accessToken;
        } catch (error) {
            console.error('❌ Token refresh failed:', error.message);
            this.clearAuth();
            throw error;
        }
    }

    /**
     * Verify current token validity
     * @returns {Promise<boolean>} Token validity status
     */
    async verifyToken() {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.AUTH.VERIFY_TOKEN);
            console.log('✅ Token is valid');
            return response.data.valid;
        } catch (error) {
            console.error('❌ Token verification failed:', error.message);
            return false;
        }
    }

    /**
     * Get user profile
     * @returns {Promise<Object>} User profile data
     */
    async getProfile() {
        try {
            console.log('👤 Fetching user profile...');

            const response = await axiosInstance.get(API_ENDPOINTS.AUTH.PROFILE);

            console.log('✅ Profile fetched successfully');
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch profile:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Update user profile
     * ✅ FIXED: Uses PUT method to /api/auth/profile (not /update)
     * @param {Object} profileData - Updated profile data
     * @returns {Promise<Object>} Updated profile
     */
    async updateProfile(profileData) {
        try {
            console.log('📝 Updating profile...');

            const response = await axiosInstance.put(
                API_ENDPOINTS.AUTH.UPDATE_PROFILE,  // Uses /api/auth/profile
                profileData
            );

            // Update stored user data
            const updatedUser = response.data;
            this.setUser(updatedUser);

            console.log('✅ Profile updated successfully');
            return updatedUser;
        } catch (error) {
            console.error('❌ Profile update failed:', error.response?.data || error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Change password
     * @param {Object} passwordData - Password change data
     * @param {string} passwordData.currentPassword - Current password
     * @param {string} passwordData.newPassword - New password
     * @returns {Promise<Object>} Success response
     */
    async changePassword(passwordData) {
        try {
            console.log('🔒 Changing password...');

            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.CHANGE_PASSWORD,
                passwordData
            );

            console.log('✅ Password changed successfully');
            return response.data;
        } catch (error) {
            console.error('❌ Password change failed:', error.response?.data || error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Forgot password - Request reset
     * @param {string} email - User email
     * @returns {Promise<Object>} Reset email sent confirmation
     */
    async forgotPassword(email) {
        try {
            console.log('📧 Requesting password reset for:', email);

            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.FORGOT_PASSWORD,
                { email }
            );

            console.log('✅ Password reset email sent');
            return response.data;
        } catch (error) {
            console.error('❌ Forgot password failed:', error.message);
            throw this.handleError(error);
        }
    }

    /**
     * Reset password with token
     * @param {Object} resetData - Reset password data
     * @param {string} resetData.token - Reset token from email
     * @param {string} resetData.newPassword - New password
     * @returns {Promise<Object>} Success response
     */
    async resetPassword(resetData) {
        try {
            console.log('🔑 Resetting password with token...');

            const response = await axiosInstance.post(
                API_ENDPOINTS.AUTH.RESET_PASSWORD,
                resetData
            );

            console.log('✅ Password reset successful');
            return response.data;
        } catch (error) {
            console.error('❌ Password reset failed:', error.message);
            throw this.handleError(error);
        }
    }

    // ========================================
    // Token and Storage Management Methods
    // ========================================

    /**
     * Set access and refresh tokens
     * @param {string} accessToken - JWT access token
     * @param {string} refreshToken - JWT refresh token
     */
    setTokens(accessToken, refreshToken) {
        localStorage.setItem('token', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
    }

    /**
     * Set access token only
     * @param {string} accessToken - JWT access token
     */
    setAccessToken(accessToken) {
        localStorage.setItem('token', accessToken);
    }

    /**
     * Get access token
     * @returns {string|null} Access token
     */
    getAccessToken() {
        return localStorage.getItem('token');
    }

    /**
     * Get refresh token
     * @returns {string|null} Refresh token
     */
    getRefreshToken() {
        return localStorage.getItem('refreshToken');
    }

    /**
     * Set user data in storage
     * @param {Object} user - User object
     */
    setUser(user) {
        localStorage.setItem('user', JSON.stringify(user));
    }

    /**
     * Get user data from storage
     * @returns {Object|null} User object
     */
    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }

    /**
     * Check if user is authenticated
     * @returns {boolean} Authentication status
     */
    isAuthenticated() {
        const token = this.getAccessToken();
        const user = this.getUser();
        return !!(token && user);
    }

    /**
     * Get user role
     * @returns {string|null} User role (STUDENT, FACULTY, ADMIN)
     */
    getUserRole() {
        const user = this.getUser();
        return user?.role || null;
    }

    /**
     * Get user email
     * @returns {string|null} User email
     */
    getUserEmail() {
        const user = this.getUser();
        return user?.email || null;
    }

    /**
     * Get user ID
     * @returns {number|null} User ID
     */
    getUserId() {
        const user = this.getUser();
        return user?.id || null;
    }

    /**
     * Clear all authentication data
     */
    clearAuth() {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        console.log('🔒 Authentication cleared');
    }

    /**
     * Handle and format API errors
     * @param {Object} error - Axios error object
     * @returns {Error} Formatted error
     */
    handleError(error) {
        if (error.response) {
            // Server responded with error status
            const message = error.response.data?.message ||
                error.response.data?.error ||
                'An error occurred';
            const statusCode = error.response.status;

            // Auto-logout on 401 Unauthorized
            if (statusCode === 401) {
                console.warn('🔒 Unauthorized - Clearing auth and redirecting to login');
                this.clearAuth();
                // Redirect to login if not already there
                if (window.location.pathname !== '/login') {
                    window.location.href = '/login';
                }
            }

            return new Error(`${message} (Status: ${statusCode})`);
        } else if (error.request) {
            // Request made but no response
            return new Error('No response from server. Please check your connection.');
        } else {
            // Something else happened
            return new Error(error.message || 'An unexpected error occurred');
        }
    }
}

export default new AuthService();
