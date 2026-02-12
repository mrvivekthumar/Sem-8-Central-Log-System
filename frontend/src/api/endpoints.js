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
    // Backend: FacultyController.java @RequestMapping("") with context-path /faculty
    // Gateway routes: /api/faculty/** -> /faculty/**
    // ========================================
    FACULTY: {
        // Dashboard and profile
        DASHBOARD: '/api/faculty/dashboard',          // GET - Get faculty dashboard data
        PROFILE: '/api/faculty/profile',              // GET - Get faculty profile
        UPDATE_PROFILE: '/api/faculty/profile',       // PUT - Update faculty profile

        // Base CRUD operations
        BASE: '/api/faculty',                         // GET - Get all faculty
        LIST: '/api/faculty',                         // GET - Same as BASE
        ALL: '/api/faculty/all',                      // GET - Get all faculty (admin)
        GET_BY_ID: (id) => `/api/faculty/${id}`,     // GET - Get faculty by ID
        GET_BY_EMAIL: (email) => `/api/faculty/email/${email}`, // GET - Get faculty by email
        CREATE: '/api/faculty',                       // POST - Create new faculty
        UPDATE: (id) => `/api/faculty/${id}`,        // PUT - Update faculty (RESTful style)
        DELETE: (id) => `/api/faculty/${id}`,        // DELETE - Delete faculty

        // Project management (for faculty use)
        PROJECTS: '/api/faculty/projects',            // GET - Get all projects
        PROJECT_BY_ID: (id) => `/api/faculty/projects/${id}`, // GET - Get project by ID
        PROJECTS_BY_IDS: '/api/faculty/projectsbyIds', // POST - Get projects by IDs (bulk)
        CREATE_PROJECT: '/api/faculty/projects',      // POST - Create new project
        UPDATE_PROJECT: (id) => `/api/faculty/projects/${id}`, // PUT - Update project
        DELETE_PROJECT: (id) => `/api/faculty/projects/${id}`, // DELETE - Delete project
        ASSIGN_PROJECT: '/api/faculty/projects/assign', // POST - Assign project to students
        IS_COMPLETE: (projectId) => `/api/faculty/project/${projectId}/is-complete`, // GET

        // Student management (for faculty use)
        STUDENTS: '/api/faculty/students',            // GET - Get all students
        STUDENT_BY_ID: (id) => `/api/faculty/students/${id}`, // GET - Get student by ID
        STUDENTS_BY_PROJECT: (projectId) => `/api/faculty/projects/${projectId}/students`, // GET

        // Document management
        DOCUMENTS: '/api/faculty/documents',          // GET - Get all documents
        UPLOAD_DOCUMENT: '/api/faculty/documents/upload', // POST - Upload document
        DELETE_DOCUMENT: (id) => `/api/faculty/documents/${id}`, // DELETE - Delete document

        // Activity and logs
        ACTIVITY_LOGS: '/api/faculty/activity-logs',  // GET - Get activity logs
        SYSTEM_LOGS: '/api/faculty/system-logs',      // GET - Get system logs

        // Utility endpoints
        COUNT: '/api/faculty/count',                  // GET - Get total faculty count
        EMAILS: '/api/faculty/emails',                // GET - Get all faculty emails
        EXISTS: (email) => `/api/faculty/exists/${email}`, // GET - Check if faculty exists

        // Student project approval
        STUDENT_PROJECT: (projectId) => `/api/faculty/studentproject/${projectId}`, // GET
        APPROVE_STUDENT: (facultyId, projectId) => `/api/faculty/${facultyId}/studentproject/${projectId}/approved`, // POST

        // Confirmed and approved projects
        CONFIRMED_PROJECTS: (facultyId) => `/api/faculty/${facultyId}/confirmed-projects`, // GET
        APPROVED_PROJECTS: (facultyId) => `/api/faculty/${facultyId}/approved-projects`, // GET

        // Project creation by faculty
        CREATE_PROJECT_BY_FACULTY: (facultyId) => `/api/projects/${facultyId}`, // POST - Create project for faculty
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

        // Applications
        APPLICATIONS: (projectId) => `/api/projects/${projectId}/applications`, // GET - Get applications
        ACCEPT_STUDENT: (projectId, studentId) => `/api/projects/${projectId}/accept/${studentId}`, // POST
        REJECT_STUDENT: (projectId, studentId) => `/api/projects/${projectId}/reject/${studentId}`, // POST

        // Student assignment
        ACCEPTED_STUDENTS: (projectId) => `/api/projects/${projectId}/accepted-students`, // GET
        ASSIGN: (projectId) => `/api/projects/${projectId}/assign`, // POST - Assign students to project
    },

    // ========================================
    // Student Service Endpoints
    // Backend: StudentController.java @RequestMapping("/students")
    // ========================================
    STUDENT: {
        // Dashboard and core endpoints
        DASHBOARD: '/api/students/dashboard',         // GET - Get dashboard data
        PROJECTS: '/api/students/projects',           // GET - Get student projects
        PROJECT_DETAIL: (id) => `/api/students/projects/${id}`, // GET - Get project by ID
        PROFILE: '/api/students/profile',             // GET - Get student profile
        UPDATE_PROFILE: '/api/students/profile',      // PUT - Update profile

        // Base CRUD operations (Admin use)
        BASE: '/api/students',                        // GET - Get all students
        LIST: '/api/students',                        // GET - Same as BASE
        GET_BY_ID: (id) => `/api/students/${id}`,    // GET - Get student by ID
        GET_BY_EMAIL: (email) => `/api/students/email/${email}`, // GET - Get student by email
        CREATE: '/api/students',                      // POST - Create new student
        UPDATE: (id) => `/api/students/${id}`,       // PUT - Update student
        DELETE: (id) => `/api/students/${id}`,       // DELETE - Delete student

        // Admin profile access by ID
        PROFILE_BY_ID: (id) => `/api/students/${id}/profile`,      // GET - Get student profile by ID (admin)
        UPDATE_PROFILE_BY_ID: (id) => `/api/students/${id}/profile`, // PUT - Update profile by ID (admin)

        // Availability and status
        AVAILABLE: '/api/students/available',         // GET - Get available students
        MAKE_UNAVAILABLE: (id) => `/api/students/${id}/unavailable`, // PUT - Make student unavailable
        UPDATE_STATUS: (studentId, projectId) =>
            `/api/studentProject/updateStatus/${studentId}/${projectId}`, // PUT - Update status

        // Project-related
        BY_PROJECT: (projectId) => `/api/studentProject/students/${projectId}`, // GET - Students by project
        COUNT_BY_PROJECT: (projectId) => `/api/studentProject/${projectId}/student-count`, // GET - Count
        BY_IDS: '/api/students/byIds',                // POST - Get students by IDs (bulk)
        ALL: '/api/students/all',                     // GET - Get all students (admin)
        COMPLETED_PROJECTS: (id) => `/api/students/${id}/completed-projects`, // GET - Completed projects

        // Utility
        COUNT: '/api/students/count',                  // GET - Get total student count
        EMAILS: '/api/students/emails',                // GET - Get all student emails

        // Image upload
        UPLOAD_IMAGE: (studentId) => `/api/students/student/${studentId}/upload-image`, // POST

        // Personal projects
        PERSONAL_PROJECTS: (studentId) => `/api/personalProject/${studentId}`, // GET
        CREATE_PERSONAL_PROJECT: (studentId) => `/api/personalProject/${studentId}`, // POST
        UPDATE_PERSONAL_PROJECT: (projectId) => `/api/personalProject/${projectId}`, // PUT
        DELETE_PERSONAL_PROJECT: (projectId) => `/api/personalProject/${projectId}`, // DELETE
    },

    // ========================================
    // StudentProject Endpoints (Student-Project Assignment)
    // Backend: StudentProjectController.java
    // ========================================
    STUDENT_PROJECT: {
        // Applied projects
        APPLIED_PROJECTS: '/api/studentProject/appliedProjects', // GET - Get applied projects
        PROJECT_IDS_BY_PREF: (studentId) => `/api/studentProject/projectIdsByPref/${studentId}`, // GET

        // Status and assignment
        STATUS: (studentId, projectId) => `/api/studentProject/student/${studentId}/project/${projectId}`, // GET
        UPDATE_STATUS: (studentId, projectId) => `/api/studentProject/updateStatus/${studentId}/${projectId}`, // PUT

        // Get students for a project
        BY_PROJECT: (projectId) => `/api/studentProject/${projectId}/student`, // GET

        // Get faculties for a student's project
        GET_FACULTIES: (studentId, projectId) => `/api/studentProject/${studentId}/getProjectFaculties/project/${projectId}`, // GET
        // Preferences
        UPDATE_PREFERENCE: (studentId, projectId, preference) =>
            `/api/studentProject/updatePreference/${studentId}/project/${projectId}/${preference}`, // PUT

        // Team
        STUDENTS_BY_PROJECT: (projectId) => `/api/studentProject/students/${projectId}`, // GET
        STUDENT_COUNT: (projectId) => `/api/studentProject/${projectId}/student-count`, // GET
    },

    // ========================================
    // Reports Endpoints
    // Backend: ReportsController.java
    // ========================================
    REPORTS: {
        BY_PROJECT: (projectId) => `/api/reports/project/${projectId}`, // GET
        PROJECT_REPORTS: (projectId) => `/api/project/${projectId}/reports`, // GET - Reports for project
        SUBMIT: (studentId, projectId) => `/api/reports/student/${studentId}/project/${projectId}/submit`, // POST
        FINAL_SUBMIT: (reportId) => `/api/reports/report/${reportId}/final-submit`, // PUT
        DELETE: (reportId) => `/api/reports/report/${reportId}`, // DELETE
        GET_REPORT: (projectId) => `/api/reports/project/${projectId}/report`, // GET
    },

    // ========================================
    // Review Endpoints
    // Backend: ReviewController.java
    // ========================================
    REVIEWS: {
        IS_APPROVED: (reportId) => `/api/review/report/${reportId}/is-approved`, // GET
        APPROVE: (reportId, studentId) => `/api/review/${reportId}/approve/student/${studentId}`, // PUT
        REJECT: (reportId, studentId) => `/api/review/${reportId}/reject/student/${studentId}`, // PUT
    },

    // ========================================
    // Notification Service Endpoints
    // Backend: NotificationController.java @RequestMapping("/notifications")
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

// Support both named and default exports
export { API_ENDPOINTS };
export default API_ENDPOINTS;
