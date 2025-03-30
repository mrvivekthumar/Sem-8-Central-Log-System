"use client"
import { Download, FileText, Check, X, AlertCircle } from "lucide-react"

export const TeamReports = ({ report, isAllApproved, loading, currentUserId, onApprove, onReject, projectId }) => {
  // Check if current user is the report submitter
  const isReportSubmitter = report && report.submittedBy.studentId === currentUserId

  // Render loading state
  if (loading) {
    return (
      <div className="flex justify-center items-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
      </div>
    )
  }

  // Render empty state
  if (!report) {
    return (
      <div className="text-center py-12">
        <FileText className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">No reports yet</h3>
        <p className="mt-2 text-sm text-gray-500">No team reports have been uploaded for this project.</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg border p-6">
        {/* Report Header */}
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-lg font-medium text-gray-900">Current Report</h3>
            <p className="text-sm text-gray-500">
              Submitted by {report.submittedBy.name} on {new Date(report.submissionDate).toLocaleDateString()}
            </p>
          </div>
          <div className="flex items-center space-x-2">
            {/* Report Status Badge */}
            <span
              className={`px-3 py-1 rounded-full text-sm font-medium ${
                report.status === "APPROVED"
                  ? "bg-green-100 text-green-800"
                  : report.status === "REJECTED"
                    ? "bg-red-100 text-red-800"
                    : "bg-yellow-100 text-yellow-800"
              }`}
            >
              {report.status}
            </span>

            {/* Download Button */}
            <a
              href={report.documentUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center space-x-2 px-4 py-2 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100"
            >
              <Download size={16} />
              <span>Download</span>
            </a>
          </div>
        </div>

        {/* Feedback Section */}
        {report.feedback && (
          <div className="mt-4 p-4 bg-gray-50 rounded-md">
            <h4 className="font-medium text-gray-900 mb-2">Feedback</h4>
            <p className="text-gray-700">{report.feedback}</p>
          </div>
        )}

        {/* Approval Section */}
        <div className="mt-6">
          {isAllApproved ? (
            <div className="bg-green-50 p-4 rounded-md flex items-center space-x-3">
              <Check size={24} className="text-green-600" />
              <div>
                <p className="text-green-800 font-medium">All team members have approved the report</p>
                <p className="text-green-600 text-sm">The report is ready for final submission</p>
              </div>
            </div>
          ) : (
            <div className="bg-yellow-50 p-4 rounded-md flex items-center space-x-3">
              <AlertCircle size={24} className="text-yellow-600" />
              <div>
                <p className="text-yellow-800 font-medium">Report pending team approval</p>
                <p className="text-yellow-600 text-sm">Waiting for all team members to review and approve</p>
              </div>
            </div>
          )}
        </div>

        {/* Conditional Approval Buttons */}
        {!isReportSubmitter && (
          <div className="mt-4 flex justify-center space-x-4">
            <button
              onClick={() => onApprove(report.reportId)}
              className="flex items-center space-x-2 px-4 py-2 bg-green-50 text-green-600 rounded-md hover:bg-green-100"
            >
              <Check size={20} />
              <span>Approve Report</span>
            </button>
            <button
              onClick={() => onReject(report.reportId)}
              className="flex items-center space-x-2 px-4 py-2 bg-red-50 text-red-600 rounded-md hover:bg-red-100"
            >
              <X size={20} />
              <span>Reject Report</span>
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

