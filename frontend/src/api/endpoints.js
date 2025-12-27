// frontend/src/api/endpoints.js
/**
 * Centralized API Endpoints Configuration
 * All endpoints match backend controller mappings exactly
 * Updated: December 27, 2025
 */

const API_ENDPOINTS = {
    // ========================================
    // Authentication Service Endpoints
    // Backend: AuthController.java @RequestMapping("/api/auth")
    // ========================================
    AUTH: {
        LOGIN: '/api/auth/login',                    // POST - Login user
        REGISTER: '/api/auth/register',              // POST - Register new user
        LOGOUT: '/api/auth/logout',                  // POST - Logout user
        REFRESH_TOKEN: '/api/auth/refresh',          // POST - Refresh access token
        VERIFY_TOKEN: '/api/auth/verify',            // GET - Verify token validity
        PROFILE: '/api/auth/profile',                // GET - Get user profile
        UPDATE_PROFILE: '/api/auth/profile',         // PUT - Update profile (same endpoint, different method)
        CHANGE_PASSWORD: '/api/auth/password/change', // POST - Change password
        RESET_PASSWORD: '/api/auth/password/reset',   // POST - Reset password with token
        FORGOT_PASSWORD: '/api/auth/password/forgot', // POST - Request password reset
    },

    // ========================================
    // Faculty Service Endpoints
    // Backend: FacultyController.java @RequestMapping("/api/faculty")
    // ========================================
    FACULTY: {
        // Base CRUD operations
        BASE: '/api/faculty',                         // GET - Get all faculty
        LIST: '/api/faculty',                         // GET - Same as BASE
        GET_BY_ID: (id) => `/api/faculty/${id}`,     // GET - Get faculty by ID
        GET_BY_EMAIL: (email) => `/api/faculty/email/${email}`, // GET - Get faculty by email
        CREATE: '/api/faculty',                       // POST - Create new faculty
        UPDATE: (id) => `/api/faculty/${id}`,        // PUT - Update faculty (RESTful style)
        DELETE: (id) => `/api/faculty/${id}`,        // DELETE - Delete faculty

        // Utility endpoints
        COUNT: '/api/faculty/count',                  // GET - Get total faculty count
        EMAILS: '/api/faculty/emails',                // GET - Get all faculty emails
        EXISTS: (email) => `/api/faculty/exists/${email}`, // GET - Check if faculty exists
    },

    // ========================================
    // Project Service Endpoints (Faculty Service)
    // Backend: ProjectController.java
    // ========================================
    PROJECTS: {
        BASE: '/api/projects',                        // GET - Get all projects
        LIST: '/api/projects',                        // GET - Same as BASE
        GET_BY_ID: (id) => `/api/projects/${id}`,    // GET - Get project by ID
        CREATE: '/api/projects',                      // POST - Create new project
        UPDATE: (id) => `/api/projects/${id}`,       // PUT - Update project
        DELETE: (id) => `/api/projects/${id}`,       // DELETE - Delete project

        // Faculty-specific project endpoints
        BY_FACULTY: (facultyId) => `/api/projects/faculty/${facultyId}`, // GET - Projects by faculty
        VISIBLE: '/api/projects/visible',             // GET - Get visible projects
        APPROVED: '/api/projects/approved',           // GET - Get approved projects
        PENDING: '/api/projects/pending',             // GET - Get pending projects

        // Project actions
        APPROVE: (id) => `/api/projects/${id}/approve`,       // PUT - Approve project
        REJECT: (id) => `/api/projects/${id}/reject`,         // PUT - Reject project
        ASSIGN_STUDENTS: (id) => `/api/projects/${id}/assign-students`, // POST - Assign students
    },

    // ========================================
    // Student Service Endpoints
    // Backend: StudentController.java @RequestMapping("/api/students")
    // ========================================
    STUDENT: {
        // Base CRUD operations
        BASE: '/api/students',                        // GET - Get all students
        LIST: '/api/students',                        // GET - Same as BASE
        GET_BY_ID: (id) => `/api/students/${id}`,    // GET - Get student by ID
        GET_BY_EMAIL: (email) => `/api/students/email/${email}`, // GET - Get student by email
        CREATE: '/api/students',                      // POST - Create new student
        UPDATE: (id) => `/api/students/${id}`,       // PUT - Update student
        DELETE: (id) => `/api/students/${id}`,       // DELETE - Delete student

        // Student-specific operations
        PROFILE: (id) => `/api/students/${id}/profile`,      // GET - Get student profile
        UPDATE_PROFILE: (id) => `/api/students/${id}/profile`, // PUT - Update profile

        // Availability and status
        AVAILABLE: '/api/students/available',         // GET - Get available students
        MAKE_UNAVAILABLE: (id) => `/api/students/${id}/unavailable`, // PUT - Make student unavailable
        UPDATE_STATUS: (studentId, projectId) =>
            `/api/studentProject/updateStatus/${studentId}/${projectId}`, // PUT - Update status

        // Project-related
        BY_PROJECT: (projectId) => `/api/studentProject/students/${projectId}`, // GET - Students by project
        COUNT_BY_PROJECT: (projectId) => `/api/studentProject/${projectId}/student-count`, // GET - Count
        BY_IDS: '/api/students/byIds',                // POST - Get students by IDs (bulk)

        // Utility
        COUNT: '/api/students/count',                  // GET - Get total student count
        EMAILS: '/api/students/emails',                // GET - Get all student emails
    },

    // ========================================
    // Notification Service Endpoints
    // Backend: NotificationController.java @RequestMapping("/api/notifications")
    // ========================================
    NOTIFICATIONS: {
        SEND: '/api/notifications/send',              // POST - Send single notification
        SEND_MULTIPLE: '/api/notifications/sendToMultiple', // POST - Send to multiple receivers
        GET_BY_RECEIVER: (receiverId) => `/api/notifications/${receiverId}`, // GET - Get notifications
        MARK_READ: (id) => `/api/notifications/${id}/read`, // PUT - Mark as read
        MARK_ALL_READ: (receiverId) => `/api/notifications/${receiverId}/read-all`, // PUT - Mark all read
    },

    // ========================================
    // Document/File Upload Endpoints
    // ========================================
    DOCUMENTS: {
        UPLOAD: '/api/documents/upload',              // POST - Upload document
        DOWNLOAD: (id) => `/api/documents/${id}/download`, // GET - Download document
        DELETE: (id) => `/api/documents/${id}`,       // DELETE - Delete document
        BY_PROJECT: (projectId) => `/api/documents/project/${projectId}`, // GET - Docs by project
        BY_STUDENT: (studentId) => `/api/documents/student/${studentId}`, // GET - Docs by student
    },

    // ========================================
    // Application/StudentProject Endpoints
    // ========================================
    APPLICATIONS: {
        SUBMIT: '/api/applications/submit',           // POST - Submit application
        APPROVE: (id) => `/api/applications/${id}/approve`, // PUT - Approve application
        REJECT: (id) => `/api/applications/${id}/reject`,   // PUT - Reject application
        BY_PROJECT: (projectId) => `/api/applications/project/${projectId}`, // GET - Apps by project
        BY_STUDENT: (studentId) => `/api/applications/student/${studentId}`, // GET - Apps by student
    },

    // ========================================
    // Rating Endpoints
    // ========================================
    RATINGS: {
        UPDATE_PROJECT: (projectId, rating) =>
            `/api/studentProject/${projectId}/rating/${rating}`, // PUT - Update project rating
    },
};

export default API_ENDPOINTS;
