// frontend/src/api/endpoints.js
/**
 * Centralized API Endpoints Configuration
 * All endpoints should match backend controller mappings
 */
const API_ENDPOINTS = {
    // Authentication Service Endpoints
    AUTH: {
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/register',
        LOGOUT: '/api/auth/logout',
        REFRESH_TOKEN: '/api/auth/refresh',
        VERIFY_TOKEN: '/api/auth/verify',
        PROFILE: '/api/auth/profile',
        UPDATE_PROFILE: '/api/auth/profile/update',
        CHANGE_PASSWORD: '/api/auth/password/change',
        RESET_PASSWORD: '/api/auth/password/reset',
        FORGOT_PASSWORD: '/api/auth/password/forgot',
    },

    // Faculty Service Endpoints
    FACULTY: {
        // Base CRUD operations
        BASE: '/api/faculty',
        LIST: '/api/faculty/list',
        GET_BY_ID: (id) => `/api/faculty/${id}`,
        CREATE: '/api/faculty/create',
        UPDATE: (id) => `/api/faculty/${id}/update`,
        DELETE: (id) => `/api/faculty/${id}/delete`,

        // Faculty-specific operations
        DASHBOARD: '/api/faculty/dashboard',
        PROFILE: '/api/faculty/profile',

        // Project management
        PROJECTS: '/api/faculty/projects',
        PROJECT_BY_ID: (id) => `/api/faculty/projects/${id}`,
        CREATE_PROJECT: '/api/faculty/projects/create',
        UPDATE_PROJECT: (id) => `/api/faculty/projects/${id}/update`,
        DELETE_PROJECT: (id) => `/api/faculty/projects/${id}/delete`,
        ASSIGN_PROJECT: '/api/faculty/projects/assign',

        // Student management
        STUDENTS: '/api/faculty/students',
        STUDENT_BY_ID: (id) => `/api/faculty/students/${id}`,
        STUDENTS_BY_PROJECT: (projectId) => `/api/faculty/projects/${projectId}/students`,

        // Document management
        DOCUMENTS: '/api/faculty/documents',
        DOCUMENT_BY_ID: (id) => `/api/faculty/documents/${id}`,
        UPLOAD_DOCUMENT: '/api/faculty/documents/upload',
        DELETE_DOCUMENT: (id) => `/api/faculty/documents/${id}/delete`,

        // Logs and monitoring
        LOGS: '/api/faculty/logs',
        ACTIVITY_LOGS: '/api/faculty/logs/activity',
        SYSTEM_LOGS: '/api/faculty/logs/system',
    },

    // Student Service Endpoints
    STUDENT: {
        // Base CRUD operations
        BASE: '/api/student',
        LIST: '/api/student/list',
        GET_BY_ID: (id) => `/api/student/${id}`,
        CREATE: '/api/student/create',
        UPDATE: (id) => `/api/student/${id}/update`,
        DELETE: (id) => `/api/student/${id}/delete`,

        // Student-specific operations
        DASHBOARD: '/api/student/dashboard',
        PROFILE: '/api/student/profile',
        UPDATE_PROFILE: '/api/student/profile/update',

        // Project operations
        PROJECTS: '/api/student/projects',
        MY_PROJECTS: '/api/student/projects/my',
        PROJECT_BY_ID: (id) => `/api/student/projects/${id}`,
        JOIN_PROJECT: '/api/student/projects/join',
        LEAVE_PROJECT: (id) => `/api/student/projects/${id}/leave`,

        // Document management
        DOCUMENTS: '/api/student/documents',
        DOCUMENT_BY_ID: (id) => `/api/student/documents/${id}`,
        UPLOAD_DOCUMENT: '/api/student/documents/upload',
        DELETE_DOCUMENT: (id) => `/api/student/documents/${id}/delete`,
        DOWNLOAD_DOCUMENT: (id) => `/api/student/documents/${id}/download`,

        // Submission management
        SUBMISSIONS: '/api/student/submissions',
        SUBMISSION_BY_ID: (id) => `/api/student/submissions/${id}`,
        CREATE_SUBMISSION: '/api/student/submissions/create',
        UPDATE_SUBMISSION: (id) => `/api/student/submissions/${id}/update`,

        // Logs
        LOGS: '/api/student/logs',
        ACTIVITY_LOGS: '/api/student/logs/activity',
    },
};

export default API_ENDPOINTS;
