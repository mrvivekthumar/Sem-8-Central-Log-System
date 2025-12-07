import { AnimatePresence, motion } from 'framer-motion';
import {
  ArrowLeft,
  Award, CheckCircle,
  Download,
  FileText,
  PartyPopper,
  Send,
  Upload,
  Users
} from 'lucide-react';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import { useAuth } from '../../contexts/AuthContext';
import Modal from './Model';
import { ReportUpload } from './ReportUpload';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/Tabs';
import TeamMembers from './TeamMembers';
import TeamReports from './TeamReports';
import Toast from './Toast';

export const ProjectSubmissionManager = () => {
  const { user } = useAuth();
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('team');
  const [toast, setToast] = useState({ show: false, message: '', type: '' });
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAllApproved, setIsAllApproved] = useState(false);
  const [isProjectCompleted, setIsProjectCompleted] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [project, setProject] = useState(null);

  useEffect(() => {
    fetchProjectData();
    fetchReport();
    checkProjectCompletion();
  }, [projectId]);

  const fetchProjectData = async () => {
    try {
      const response = await axiosInstance.get(`/FACULTY-SERVICE/api/project/${projectId}`);
      setProject(response.data);
    } catch (error) {
      console.error('Error fetching project:', error);
    }
  };

  const checkProjectCompletion = async () => {
    try {
      const response = await axiosInstance.get(
        `/FACULTY-SERVICE/api/faculty/project/${projectId}/is-complete`
      );
      setIsProjectCompleted(response.data);
    } catch (error) {
      console.error('Error checking project completion:', error);
      setIsProjectCompleted(false);
    }
  };

  const fetchReport = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get(
        `/STUDENT-SERVICE/api/reports/project/${projectId}/report`
      );
      setReport(response.data);

      if (response.data?.reportId) {
        const approvalStatus = await axiosInstance.get(
          `/STUDENT-SERVICE/api/review/report/${response.data.reportId}/is-approved`
        );
        setIsAllApproved(approvalStatus.data);
      }
    } catch (error) {
      if (error.response?.status === 404) {
        showToast('Report not submitted yet', 'warning');
        setReport(null);
        setIsAllApproved(false);
      } else {
        showToast('Something went wrong. Please try again.', 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  const showToast = (message, type) => {
    setToast({ show: true, message, type });
    setTimeout(() => setToast({ show: false, message: '', type: '' }), 3000);
  };

  const handleReportUpload = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axiosInstance.post(
        `/STUDENT-SERVICE/api/reports/student/${user.id}/project/${projectId}/submit`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );
      setReport(response.data);
      showToast('Report uploaded successfully!', 'success');
      fetchReport();
    } catch (error) {
      showToast('Failed to upload report', 'error');
    }
  };

  const handleApproval = async (reportId, approve) => {
    try {
      const endpoint = approve ? 'approve' : 'reject';
      await axiosInstance[approve ? 'post' : 'put'](
        `/STUDENT-SERVICE/api/review/${reportId}/${endpoint}/student/${user.id}`
      );
      showToast(`Report ${approve ? 'approved' : 'rejected'} successfully!`, 'success');
      fetchReport();
    } catch (error) {
      showToast(`Failed to ${approve ? 'approve' : 'reject'} report`, 'error');
    }
  };

  const handleFinalSubmit = async () => {
    try {
      await axiosInstance.put(
        `/STUDENT-SERVICE/api/reports/report/${report.reportId}/final-submit`
      );
      setModalMessage('Your report has been successfully submitted to the faculty!');
      setShowSuccessModal(true);
      fetchReport();
      checkProjectCompletion();
    } catch (error) {
      showToast('Failed to submit final report', 'error');
    }
  };

  const handleDeleteReport = async () => {
    try {
      await axiosInstance.delete(`/STUDENT-SERVICE/api/reports/report/${report.reportId}`);
      showToast('Report deleted successfully', 'success');
      setReport(null);
      fetchReport();
    } catch (error) {
      showToast('Failed to delete report', 'error');
    }
  };

  // Render project completed view
  if (isProjectCompleted) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-purple-50 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center p-4">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="max-w-2xl w-full bg-white dark:bg-gray-800 rounded-3xl shadow-2xl p-8 md:p-12 text-center border border-gray-200 dark:border-gray-700"
        >
          {/* Celebration Icon */}
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
            className="w-24 h-24 bg-gradient-to-br from-green-400 to-blue-500 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg"
          >
            <PartyPopper className="w-12 h-12 text-white" />
          </motion.div>

          <motion.h1
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="text-4xl font-bold text-gray-900 dark:text-white mb-4"
          >
            🎉 Project Completed! 🎉
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="text-lg text-gray-600 dark:text-gray-400 mb-8"
          >
            Congratulations! You and your team have successfully completed the project!
          </motion.p>

          {/* Report Info */}
          {report && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 }}
              className="bg-gray-50 dark:bg-gray-700 rounded-xl p-6 mb-6"
            >
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                  <FileText className="w-5 h-5 text-blue-600" />
                  <span className="font-semibold text-gray-900 dark:text-white">Final Report</span>
                </div>
                <a
                  href={report.reportUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 hover:text-blue-700 dark:text-blue-400 flex items-center gap-2"
                >
                  <Download className="w-4 h-4" />
                  Download
                </a>
              </div>
              <div className="text-sm text-gray-600 dark:text-gray-400 text-left">
                <p>Submitted by: <strong>{report.submittedBy.name}</strong></p>
                <p>Date: <strong>{new Date(report.submissionDate).toLocaleDateString()}</strong></p>
              </div>
            </motion.div>
          )}

          {/* Achievement Badge */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6 }}
            className="bg-gradient-to-r from-yellow-50 to-orange-50 dark:from-yellow-900/20 dark:to-orange-900/20 border border-yellow-200 dark:border-yellow-800 rounded-xl p-6 mb-6"
          >
            <Award className="w-8 h-8 text-yellow-600 dark:text-yellow-400 mx-auto mb-3" />
            <p className="text-sm text-gray-700 dark:text-gray-300">
              You can see your updated ratings in the profile section.
            </p>
          </motion.div>

          <motion.button
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => navigate('/student/dashboard')}
            className="px-8 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg"
          >
            Back to Dashboard
          </motion.button>
        </motion.div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
          className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Toast show={toast.show} message={toast.message} type={toast.type} />

      {/* Header */}
      <div className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-40 backdrop-blur-lg bg-white/80 dark:bg-gray-800/80">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center gap-4">
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => navigate(-1)}
              className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-xl transition-colors"
            >
              <ArrowLeft className="w-6 h-6 text-gray-600 dark:text-gray-400" />
            </motion.button>
            <div className="flex-1">
              <motion.h1
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-3xl font-bold text-gray-900 dark:text-white flex items-center gap-2"
              >
                <FileText className="w-8 h-8 text-blue-600" />
                Project Submission
              </motion.h1>
              {project && (
                <motion.p
                  initial={{ opacity: 0, y: -20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.1 }}
                  className="text-gray-600 dark:text-gray-400 mt-1"
                >
                  {project.title}
                </motion.p>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Progress Indicators */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8"
        >
          <div className={`bg-white dark:bg-gray-800 rounded-xl p-6 border-2 ${report ? 'border-green-500' : 'border-gray-200 dark:border-gray-700'
            }`}>
            <div className="flex items-center gap-3 mb-2">
              <div className={`w-10 h-10 rounded-xl ${report ? 'bg-green-100 dark:bg-green-900/20' : 'bg-gray-100 dark:bg-gray-700'
                } flex items-center justify-center`}>
                <Upload className={`w-5 h-5 ${report ? 'text-green-600' : 'text-gray-400'}`} />
              </div>
              <span className="font-semibold text-gray-900 dark:text-white">Upload Report</span>
            </div>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {report ? '✓ Report uploaded' : 'Upload your project report'}
            </p>
          </div>

          <div className={`bg-white dark:bg-gray-800 rounded-xl p-6 border-2 ${isAllApproved ? 'border-green-500' : 'border-gray-200 dark:border-gray-700'
            }`}>
            <div className="flex items-center gap-3 mb-2">
              <div className={`w-10 h-10 rounded-xl ${isAllApproved ? 'bg-green-100 dark:bg-green-900/20' : 'bg-gray-100 dark:bg-gray-700'
                } flex items-center justify-center`}>
                <Users className={`w-5 h-5 ${isAllApproved ? 'text-green-600' : 'text-gray-400'}`} />
              </div>
              <span className="font-semibold text-gray-900 dark:text-white">Team Approval</span>
            </div>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {isAllApproved ? '✓ All approved' : 'Waiting for team approval'}
            </p>
          </div>

          <div className={`bg-white dark:bg-gray-800 rounded-xl p-6 border-2 ${report?.isFinalSubmitted ? 'border-green-500' : 'border-gray-200 dark:border-gray-700'
            }`}>
            <div className="flex items-center gap-3 mb-2">
              <div className={`w-10 h-10 rounded-xl ${report?.isFinalSubmitted ? 'bg-green-100 dark:bg-green-900/20' : 'bg-gray-100 dark:bg-gray-700'
                } flex items-center justify-center`}>
                <Send className={`w-5 h-5 ${report?.isFinalSubmitted ? 'text-green-600' : 'text-gray-400'}`} />
              </div>
              <span className="font-semibold text-gray-900 dark:text-white">Final Submit</span>
            </div>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {report?.isFinalSubmitted ? '✓ Submitted to faculty' : 'Submit to faculty'}
            </p>
          </div>
        </motion.div>

        {/* Tabs */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden"
        >
          <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
            <TabsList className="grid w-full grid-cols-3 border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
              <TabsTrigger value="team" className="flex items-center gap-2 py-4">
                <Users className="w-4 h-4" />
                Team Members
              </TabsTrigger>
              <TabsTrigger value="upload" className="flex items-center gap-2 py-4">
                <Upload className="w-4 h-4" />
                Upload Report
              </TabsTrigger>
              <TabsTrigger value="reports" className="flex items-center gap-2 py-4">
                <FileText className="w-4 h-4" />
                Team Reports
              </TabsTrigger>
            </TabsList>

            <div className="p-6">
              <TabsContent value="team" className="mt-0">
                <TeamMembers
                  projectId={projectId}
                  currentUserId={user.id}
                  report={report}
                  onApprove={(reportId) => handleApproval(reportId, true)}
                  onReject={(reportId) => handleApproval(reportId, false)}
                />
              </TabsContent>

              <TabsContent value="upload" className="mt-0">
                <ReportUpload
                  onUpload={handleReportUpload}
                  disabled={!!report}
                  currentUserId={user.id}
                  submittedBy={report?.submittedBy?.studentId}
                  report={report}
                  isAllApproved={isAllApproved}
                  onReportDeleted={fetchReport}
                />
              </TabsContent>

              <TabsContent value="reports" className="mt-0">
                <TeamReports
                  report={report}
                  isAllApproved={isAllApproved}
                  loading={loading}
                  currentUserId={user.id}
                  onApprove={(reportId) => handleApproval(reportId, true)}
                  onReject={(reportId) => handleApproval(reportId, false)}
                  projectId={projectId}
                />
              </TabsContent>
            </div>
          </Tabs>
        </motion.div>
      </div>

      {/* Success Modal */}
      <AnimatePresence>
        {showSuccessModal && (
          <Modal
            title="Success!"
            message={modalMessage}
            icon={<CheckCircle className="w-12 h-12 text-green-500" />}
            onClose={() => {
              setShowSuccessModal(false);
              navigate('/student/dashboard');
            }}
          />
        )}
      </AnimatePresence>
    </div>
  );
};
