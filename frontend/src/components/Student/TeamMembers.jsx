import React, { useState, useEffect } from 'react';
import { Check, X, Clock, Github } from 'lucide-react';
import axios from 'axios';
import axiosInstance from '../../api/axiosInstance';
export const TeamMembers = ({
    projectId,
    currentUserId,
    report,
    onApprove,
    onReject
}) => {
    const [teammates, setTeammates] = useState([]);
    const [teammateStatuses, setTeammateStatuses] = useState({});
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchTeammatesAndStatuses();
    }, [projectId, report]);

    const fetchTeammatesAndStatuses = async () => {
        try {
            // Fetch team members
            const idsResponse = await axiosInstance.get(
                `/STUDENT-SERVICE/api/studentProject/${projectId}/completeIds`
            );
            const studentIds = idsResponse.data;

            // Fetch team member details
            const detailsResponse = await axiosInstance.get(
                `/STUDENT-SERVICE/students/by-id?ids=${studentIds.join(',')}`
            );
            const teammates = detailsResponse.data;

            // If no report, set all as pending
            if (!report?.reportId) {
                const defaultStatusMap = teammates.reduce((acc, teammate) => {
                    acc[teammate.studentId] = 'PENDING';
                    return acc;
                }, {});
                setTeammates(teammates);
                setTeammateStatuses(defaultStatusMap);
                setLoading(false);
                return;
            }

            // Fetch approval status for each team member
            const statusPromises = teammates.map(async (teammate) => {
                try {
                    const statusResponse = await axiosInstance.get(
                        `/STUDENT-SERVICE/api/review/report/${report.reportId}/student/${teammate.studentId}/is-approved`
                    );
                    return { 
                        studentId: teammate.studentId, 
                        status: statusResponse.data ? 'APPROVED' : 'REJECTED'
                    };
                } catch (error) {
                    return { 
                        studentId: teammate.studentId, 
                        status: 'PENDING' 
                    };
                }
            });

            const statuses = await Promise.all(statusPromises);
            const statusMap = statuses.reduce((acc, status) => {
                acc[status.studentId] = status.status;
                return acc;
            }, {});

            setTeammates(teammates);
            setTeammateStatuses(statusMap);
        } catch (error) {
            console.error('Failed to fetch teammates:', error);
        } finally {
            setLoading(false);
        }
    };

    // Modify approval handler to fetch and update status
    const handleApprove = async (reportId) => {
        try {
            await onApprove(reportId);
            // Refetch statuses after approval
            fetchTeammatesAndStatuses();
        } catch (error) {
            console.error('Approval failed:', error);
        }
    };

    const handleReject = async (reportId) => {
        try {
            await onReject(reportId);
            // Refetch statuses after rejection
            fetchTeammatesAndStatuses();
        } catch (error) {
            console.error('Rejection failed:', error);
        }
    };

    // Status icon and color mapping
    const getStatusInfo = (status) => {
        switch (status) {
            case 'APPROVED':
                return {
                    icon: <Check size={20} className="text-green-600" />,
                    text: 'Approved',
                    bgColor: 'bg-green-50',
                    borderColor: 'border-green-200'
                };
            case 'REJECTED':
                return {
                    icon: <X size={20} className="text-red-600" />,
                    text: 'Rejected',
                    bgColor: 'bg-red-50',
                    borderColor: 'border-red-200'
                };
            default:
                return {
                    icon: <Clock size={20} className="text-gray-500" />,
                    text: 'Pending',
                    bgColor: 'bg-gray-50',
                    borderColor: 'border-gray-200'
                };
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center p-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
            </div>
        );
    }

    // Check if current user is the report submitter
    const isReportSubmitter = report?.submittedBy?.studentId === currentUserId;

    return (
        <div className="space-y-4">
            <h2 className="text-xl font-semibold text-gray-800 mb-4">Team Members Approval Status</h2>
            {teammates.filter(teammate=>teammate.studentId!=currentUserId).map((teammate) => {
                const status = teammateStatuses[teammate.studentId] || 'PENDING';
                const { icon, text, bgColor, borderColor } = getStatusInfo(status);

                return (
                    <div
                        key={teammate.studentId}
                        className={`flex items-center justify-between p-4 rounded-lg border ${
                            teammate.studentId === currentUserId ? 'bg-blue-50 border-blue-200' : bgColor
                        } ${borderColor}`}
                    >
                        <div className="flex items-center space-x-4">
                            <div className="w-12 h-12 rounded-full bg-gray-200 flex items-center justify-center">
                                <span className="text-lg font-medium text-gray-600">
                                    {teammate.name.charAt(0)}
                                </span>
                            </div>
                            <div>
                                <h3 className="font-medium text-gray-900">
                                    {teammate.name}
                                    {teammate.studentId === currentUserId && (
                                        <span className="ml-2 text-sm text-blue-600">(You)</span>
                                    )}
                                </h3>
                                <p className="text-sm text-gray-500">{teammate.email}</p>
                            </div>
                        </div>

                        <div className="flex items-center space-x-3">
                            {teammate.githubProfileLink && (
                                <a
                                    href={teammate.githubProfileLink}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-gray-500 hover:text-gray-700"
                                >
                                    <Github size={20} />
                                </a>
                            )}

                            <div className="flex items-center space-x-2">
                                {icon}
                                <span className="text-sm font-medium">{text}</span>
                            </div>

                           
                        </div>
                    </div>
                );
            })}
        </div>
    );
};