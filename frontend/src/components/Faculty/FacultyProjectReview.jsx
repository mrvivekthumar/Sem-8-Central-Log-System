"use client"

import { useState, useEffect } from "react"
import { useParams } from "react-router-dom"
import axios from "axios"
import { FileText, Users, CheckCircle, AlertTriangle, Award } from "lucide-react"
import { Toast } from "../Student/Toast"
import axiosInstance from '../../api/axiosInstance';
export const FacultyProjectReview = () => {
  const { projectId } = useParams()
  const [report, setReport] = useState(null)
  const [teammates, setTeammates] = useState([])
  const [loading, setLoading] = useState(true)
  const [isProjectCompleted, setIsProjectCompleted] = useState(false)
  const [toast, setToast] = useState({ show: false, message: "", type: "" })
  const [isCheckingCompletion, setIsCheckingCompletion] = useState(false)

  useEffect(() => {
    fetchProjectData()
  }, [projectId])

  const fetchProjectData = async () => {
    try {
      setLoading(true)

      // Check if project is already completed
      await checkProjectCompletion()

      const [reportRes, teamRes] = await Promise.allSettled([
        axiosInstance.get(`/STUDENT-SERVICE/api/reports/project/${projectId}/report`),
        fetchTeammates(),
      ])

      // Check if report API call was successful
      if (reportRes.status === "fulfilled") {
        setReport(reportRes.value.data)
      } else if (reportRes.reason.response && reportRes.reason.response.status === 404) {
        showToast("Report is not submitted yet", "warning")
      } else {
        showToast("Failed to fetch report data", "error")
      }

      // Handle teammates data
      if (teamRes.status === "fulfilled") {
        // No need to initialize ratings anymore
      } else {
        showToast("Failed to fetch teammates data", "error")
      }
    } catch (error) {
      showToast("An unexpected error occurred", "error")
    } finally {
      setLoading(false)
    }
  }

  const checkProjectCompletion = async () => {
    try {
      setIsCheckingCompletion(true)
      const response = await axiosInstance.get(
        `/FACULTY-SERVICE/api/faculty/project/${projectId}/is-complete`,
      )
      setIsProjectCompleted(response.data)
    } catch (error) {
      console.error("Error checking project completion status:", error)
      // Default to false if there's an error
      setIsProjectCompleted(false)
    } finally {
      setIsCheckingCompletion(false)
    }
  }

  const fetchTeammates = async () => {
    const idsResponse = await axiosInstance.get(
      `/STUDENT-SERVICE/api/studentProject/${projectId}/completeIds`,
    )

    const studentIds = idsResponse.data

    const detailsResponse = await axiosInstance.get(
      `/STUDENT-SERVICE/students/by-id?ids=${studentIds.join(",")}`,
    )
    setTeammates(detailsResponse.data)
    return detailsResponse.data
  }

  const handleMarkComplete = async () => {
    try {
      await axiosInstance.put(`/FACULTY-SERVICE/api/project/${projectId}/completed`)
      setIsProjectCompleted(true)
      showToast("Project marked as completed successfully.", "success")
    } catch (error) {
      showToast("Failed to mark project as complete", "error")
    }
  }

  const showToast = (message, type) => {
    setToast({ show: true, message, type })
    setTimeout(() => setToast({ show: false, message: "", type: "" }), 3000)
  }

  if (loading || isCheckingCompletion) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      <Toast {...toast} />

      {/* Project Report Section */}
      <div className="bg-white rounded-xl shadow-sm border p-6">
        <div className="flex items-center gap-3 mb-6">
          <FileText className="h-6 w-6 text-blue-600" />
          <h2 className="text-2xl font-semibold text-gray-800">Project Report</h2>
        </div>

        {report?.finalSubmission ? (
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <div>
                <p className="text-sm text-gray-600">
                  Submitted on {new Date(report.submissionDate).toLocaleDateString()}
                </p>
                <p className="text-sm text-gray-600">By {report.submittedBy.name}</p>
              </div>
              <a
                href={report.documentUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="px-4 py-2 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100 transition-colors"
              >
                View Report
              </a>
            </div>
          </div>
        ) : (
          <div className="text-center py-8 bg-gray-50 rounded-lg border border-dashed">
            <AlertTriangle className="h-12 w-12 text-yellow-500 mx-auto mb-3" />
            <p className="text-gray-600">Final submission pending</p>
          </div>
        )}
      </div>

      {/* Team Members Section */}
      <div className="bg-white rounded-xl shadow-sm border p-6">
        <div className="flex items-center gap-3 mb-6">
          <Users className="h-6 w-6 text-blue-600" />
          <h2 className="text-2xl font-semibold text-gray-800">Team Members</h2>
        </div>

        <div className="space-y-4">
          {teammates.map((member) => (
            <div key={member.studentId} className="bg-gray-50 rounded-lg p-4">
              <div className="flex justify-between items-center">
                <div>
                  <h3 className="font-medium text-gray-900">{member.name}</h3>
                  <p className="text-sm text-gray-500">{member.email}</p>
                </div>
                {/* No individual ratings displayed here anymore */}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Project Completion Section */}
      {isProjectCompleted ? (
        <div className="bg-white rounded-xl shadow-sm border p-6">
          <div className="flex items-center gap-3 mb-4">
            <Award className="h-6 w-6 text-green-600" />
            <h2 className="text-2xl font-semibold text-gray-800">Project Completed</h2>
          </div>

          <div className="bg-green-50 border border-green-100 rounded-lg p-6 text-center">
            <CheckCircle className="h-12 w-12 text-green-500 mx-auto mb-3" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">Successful Student-Faculty Collaboration</h3>
            <p className="text-gray-600">
              This project represents a remarkable journey of learning, innovation, and collaborative effort between students and faculty.
            </p>
            <p className="text-sm text-gray-500 mt-4">
              The completion of this project highlights the power of academic partnership and shared intellectual growth.
            </p>
          </div>
        </div>
      ) : (
        report?.finalSubmission && (
          <div className="bg-white rounded-xl shadow-sm border p-6">
            <div className="flex items-center gap-3 mb-6">
              <CheckCircle className="h-6 w-6 text-green-600" />
              <h2 className="text-2xl font-semibold text-gray-800">Finalize Project Collaboration</h2>
            </div>

            <div className="space-y-4">
              <p className="text-gray-600">
                By marking this project as complete, you'll solidify the collaborative effort between students and faculty, 
                acknowledging the collective hard work and academic achievement.
              </p>

              <button
                onClick={handleMarkComplete}
                className="w-full py-3 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors font-medium flex items-center justify-center gap-2"
              >
                <CheckCircle className="h-5 w-5" />
                Complete Project Collaboration
              </button>
            </div>
          </div>
        )
      )}
    </div>
  )
}

