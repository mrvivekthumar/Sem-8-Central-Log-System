import React, { useState, useEffect } from 'react';
import { Tabs, TabsList, TabsTrigger, TabsContent } from './Tabs';
import { ReportUpload } from './ReportUpload';
import { TeamReports } from './TeamReports';
import { TeamMembers } from './TeamMembers';
import { useParams } from 'react-router-dom';
import { Toast } from './Toast';
import axios from 'axios';
import { useAuth } from '../../contexts/AuthContext';


export const ProjectSubmissionManager = () => {
  const { user } = useAuth();
  const { projectId } = useParams();
  const [activeTab, setActiveTab] = useState('team');
  const [toast, setToast] = useState({ show: false, message: '', type: '' });
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAllApproved, setIsAllApproved] = useState(false);

  useEffect(() => {
    fetchReport();
  }, [projectId]);

  const fetchReport = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`http://localhost:8765/STUDENT-SERVICE/api/reports/project/${projectId}/report`);
      setReport(response.data);
      
      if (response.data?.reportId) {
        const approvalStatus = await axios.get(
          `http://localhost:8765/STUDENT-SERVICE/api/review/report/${response.data.reportId}/is-approved`
        );
        setIsAllApproved(approvalStatus.data);
      }
    } catch (error) {
      showToast('Failed to fetch report data', 'error');
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
      const response = await axios.post(
        `http://localhost:8765/STUDENT-SERVICE/api/reports/student/${user.id}/project/${projectId}/submit`,
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
      await axios[approve ? 'post' : 'put'](
        `http://localhost:8765/STUDENT-SERVICE/api/review/${reportId}/${endpoint}/student/${user.id}`
      );
      showToast(`Report ${approve ? 'approved' : 'rejected'} successfully!`, 'success');
      fetchReport();
    } catch (error) {
      showToast(`Failed to ${approve ? 'approve' : 'reject'} report`, 'error');
    }
  };

  return (
    <div className="max-w-6xl mx-auto p-4 space-y-6">
      <Toast {...toast} />
      
      <div className="bg-white rounded-xl shadow-sm border p-6">
        <h1 className="text-2xl font-semibold text-gray-800 mb-6">
          Project Submission Manager
        </h1>

        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList>
            <TabsTrigger value="team">Team Members</TabsTrigger>
            <TabsTrigger value="reports">Reports</TabsTrigger>
            <TabsTrigger value="upload">Upload Report</TabsTrigger>
          </TabsList>

          <TabsContent value="team">
            <TeamMembers 
              projectId={projectId} 
              currentUserId={user.id}
              report={report}
              onApprove={(reportId) => handleApproval(reportId, true)}
              onReject={(reportId) => handleApproval(reportId, false)}
            />
          </TabsContent>

          <TabsContent value="reports">
            <TeamReports
              projectId={projectId}
              currentUserId={user.id}
              onApprove={(reportId) => handleApproval(reportId, true)}
              onReject={(reportId) => handleApproval(reportId, false)}
              report={report}
              isAllApproved={isAllApproved}
              loading={loading}
            />
          </TabsContent>

          <TabsContent value="upload">
            <ReportUpload
              onUpload={handleReportUpload}
              disabled={!!report}
              currentUserId={user.id}
              submittedBy={report?.submittedBy?.studentId}
            />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};