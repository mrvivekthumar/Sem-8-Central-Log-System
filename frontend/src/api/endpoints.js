// API Endpoints Configuration
const API_ENDPOINTS = {
    // Auth endpoints
    AUTH: {
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/register',
        TOKEN: '/api/auth/token',
        LOGOUT: '/api/auth/logout',
        REFRESH: '/api/auth/refresh',
        PROFILE: '/api/auth/profile',
    },

    // Faculty endpoints
    FACULTY: {
        BASE: '/api/faculty',
        LIST: '/api/faculty/list',
        DETAIL: (id) => `/api/faculty/${id}`,
        CREATE: '/api/faculty/create',
        UPDATE: (id) => `/api/faculty/${id}`,
        DELETE: (id) => `/api/faculty/${id}`,
        PROJECTS: '/api/faculty/projects',
        PROJECT_DETAIL: (id) => `/api/faculty/projects/${id}`,
        STUDENTS: '/api/faculty/students',
        DASHBOARD: '/api/faculty/dashboard',
    },

    // Student endpoints
    STUDENT: {
        BASE: '/api/student',
        LIST: '/api/student/list',
        DETAIL: (id) => `/api/student/${id}`,
        CREATE: '/api/student/create',
        UPDATE: (id) => `/api/student/${id}`,
        DELETE: (id) => `/api/student/${id}`,
        PROJECTS: '/api/student/projects',
        PROJECT_DETAIL: (id) => `/api/student/projects/${id}`,
        UPLOAD_DOCUMENT: '/api/student/documents/upload',
        DOCUMENTS: '/api/student/documents',
        DASHBOARD: '/api/student/dashboard',
    },
};

export default API_ENDPOINTS;
