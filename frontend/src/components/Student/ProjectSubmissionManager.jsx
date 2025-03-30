"use client"

import { useState, useEffect } from "react"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "./Tabs"
import { useParams } from "react-router-dom"
import { Toast } from "./Toast"

import axios from "axios"
import { useAuth } from "../../contexts/AuthContext"
import { Award, CheckCircle, FileText, Users } from "lucide-react"
import { Modal } from "./Model"
import { TeamMembers } from "./TeamMembers"
import { ReportUpload } from "./ReportUpload"
import { TeamReports } from "./TeamReports"

export const ProjectSubmissionManager = () => {
  const { user } = useAuth()
  const { projectId } = useParams()
  const [activeTab, setActiveTab] = useState("team")
  const [toast, setToast] = useState({ show: false, message: "", type: "" })
  const [report, setReport] = useState(null)
  const [loading, setLoading] = useState(true)
  const [isAllApproved, setIsAllApproved] = useState(false)
  const [isProjectCompleted, setIsProjectCompleted] = useState(false)
  const [showSuccessModal, setShowSuccessModal] = useState(false)
  const [modalMessage, setModalMessage] = useState("")

  useEffect(() => {
    fetchReport()
    checkProjectCompletion()
  }, [projectId])

  const checkProjectCompletion = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8765/FACULTY-SERVICE/api/faculty/project/${projectId}/is-complete`,
      )
      setIsProjectCompleted(response.data)
    } catch (error) {
      console.error("Error checking project completion:", error)
      setIsProjectCompleted(false)
    }
  }

  const fetchReport = async () => {
    try {
      setLoading(true)
      const response = await axios.get(`http://localhost:8765/STUDENT-SERVICE/api/reports/project/${projectId}/report`)

      setReport(response.data)

      if (response.data?.reportId) {
        const approvalStatus = await axios.get(
          `http://localhost:8765/STUDENT-SERVICE/api/review/report/${response.data.reportId}/is-approved`,
        )
        setIsAllApproved(approvalStatus.data)
      }
    } catch (error) {
      if (error.response?.status === 404) {
        // Show a warning instead of an error
        showToast("Report not Submitted", "warning")
        setReport(null)
        setIsAllApproved(false)
      } else {
        // Handle other errors
        showToast("Something went wrong. Please try again.", "error")
      }
    } finally {
      setLoading(false)
    }
  }

  const showToast = (message, type) => {
    setToast({ show: true, message, type })
    setTimeout(() => setToast({ show: false, message: "", type: "" }), 3000)
  }

  const handleReportUpload = async (file) => {
    const formData = new FormData()
    formData.append("file", file)

    try {
      const response = await axios.post(
        `http://localhost:8765/STUDENT-SERVICE/api/reports/student/${user.id}/project/${projectId}/submit`,
        formData,
        { headers: { "Content-Type": "multipart/form-data" } },
      )
      setReport(response.data)
      showToast("Report uploaded successfully!", "success")
      fetchReport()
    } catch (error) {
      showToast("Failed to upload report", "error")
    }
  }

  const handleApproval = async (reportId, approve) => {
    try {
      const endpoint = approve ? "approve" : "reject"
      await axios[approve ? "post" : "put"](
        `http://localhost:8765/STUDENT-SERVICE/api/review/${reportId}/${endpoint}/student/${user.id}`,
      )
      showToast(`Report ${approve ? "approved" : "rejected"} successfully!`, "success")
      fetchReport()
    } catch (error) {
      showToast(`Failed to ${approve ? "approve" : "reject"} report`, "error")
    }
  }

  const handleFinalSubmit = async () => {
    try {
      await axios.put(`http://localhost:8765/STUDENT-SERVICE/api/reports/report/${report.reportId}/final-submit`)

      setModalMessage("Your report has been successfully submitted to the faculty!")
      setShowSuccessModal(true)

      // Refresh data
      fetchReport()
      checkProjectCompletion()
    } catch (error) {
      showToast("Failed to submit final report", "error")
    }
  }

  const handleDeleteReport = async () => {
    try {
      await axios.delete(`http://localhost:8765/STUDENT-SERVICE/api/reports/report/${report.reportId}`)
      showToast("Report deleted successfully", "success")
      setReport(null)
      fetchReport()
    } catch (error) {
      showToast("Failed to delete report", "error")
    }
  }

  // Render project completed view
  if (isProjectCompleted) {
    return (
      <div className="max-w-6xl mx-auto p-4 space-y-6">
        <Toast {...toast} />

        <div className="bg-white rounded-xl shadow-sm border p-6">
          <div className="text-center mb-8">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-green-100 rounded-full mb-4">
              <Award className="h-10 w-10 text-green-600" />
            </div>
            <h1 className="text-3xl font-bold text-gray-800 mb-2">Project Completed!</h1>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Congratulations! You and your team have successfully completed the project!
            </p>
            <p className="text-gray-500 mt-2">You can see your updated ratings in the profile section.</p>
          </div>

          <div className="grid md:grid-cols-2 gap-6 mt-8">
            {/* Project Report Card */}
            <div className="bg-white rounded-lg border p-6 hover:shadow-md transition-shadow">
              <div className="flex items-center gap-3 mb-4">
                <FileText className="h-6 w-6 text-blue-600" />
                <h2 className="text-xl font-semibold text-gray-800">Project Report</h2>
              </div>

              {report ? (
                <div className="space-y-4">
                  <p className="text-gray-600">
                    Submitted by: <span className="font-medium">{report.submittedBy.name}</span>
                  </p>
                  <p className="text-gray-600">
                    Date: <span className="font-medium">{new Date(report.submissionDate).toLocaleDateString()}</span>
                  </p>
                  <a
                    href={report.documentUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-block px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                  >
                    View Report
                  </a>
                </div>
              ) : (
                <p className="text-gray-500">No report information available.</p>
              )}
            </div>

            {/* Team Members Card */}
            <div className="bg-white rounded-lg border p-6 hover:shadow-md transition-shadow">
              <div className="flex items-center gap-3 mb-4">
                <Users className="h-6 w-6 text-blue-600" />
                <h2 className="text-xl font-semibold text-gray-800">Team Members</h2>
              </div>

              <TeamMembers
                projectId={projectId}
                currentUserId={user?.id}
                report={report}
                onApprove={(reportId) => handleApproval(reportId, true)}
                onReject={(reportId) => handleApproval(reportId, false)}
                isCompactView={true}
              />
            </div>
          </div>

          <div className="mt-8 text-center">
            <div className="inline-block px-6 py-3 bg-green-50 text-green-700 rounded-lg border border-green-200">
              <p className="font-medium">Thank you for your hard work on this project!</p>
              <p className="text-sm mt-1">Your faculty has marked this project as complete.</p>
            </div>
          </div>
        </div>
      </div>
    )
  }

  // Regular view with tabs
  return (
    <div className="max-w-6xl mx-auto p-4 space-y-6">
      <Toast {...toast} />
      {showSuccessModal && (
        <Modal
          title="Success!"
          message={modalMessage}
          onClose={() => setShowSuccessModal(false)}
          icon={<CheckCircle className="h-10 w-10 text-green-600" />}
        />
      )}

      <div className="bg-white rounded-xl shadow-sm border p-6">
        <h1 className="text-2xl font-semibold text-gray-800 mb-6">Project Submission Manager</h1>

        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList>
            <TabsTrigger value="team">Team Members</TabsTrigger>
            <TabsTrigger value="reports">Reports</TabsTrigger>
            <TabsTrigger value="upload">Upload Report</TabsTrigger>
          </TabsList>

          <TabsContent value="team">
            <TeamMembers
              projectId={projectId}
              currentUserId={user?.id}
              report={report}
              onApprove={(reportId) => handleApproval(reportId, true)}
              onReject={(reportId) => handleApproval(reportId, false)}
            />
          </TabsContent>

          <TabsContent value="reports">
            <TeamReports
              projectId={projectId}
              currentUserId={user?.id}
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
              currentUserId={user?.id}
              submittedBy={report?.submittedBy?.studentId}
              isAllApproved={isAllApproved}
              report={report}
              onFinalSubmit={handleFinalSubmit}
              onDeleteReport={handleDeleteReport}
              isProjectCompleted={isProjectCompleted}
            />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}

