import { motion } from 'framer-motion';
import {
    Award, Crown,
    Mail,
    MessageCircle,
    MoreVertical,
    Phone, Star,
    UserMinus,
    Users
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import axiosInstance from '../../api/axiosInstance';

const TeamMembers = ({ projectId }) => {
    const [members, setMembers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showMenu, setShowMenu] = useState(null);

    useEffect(() => {
        fetchTeamMembers();
    }, [projectId]);

    const fetchTeamMembers = async () => {
        try {
            setLoading(true);
            const response = await axiosInstance.get(
                `/STUDENT-SERVICE/api/project/${projectId}/team-members`
            );
            setMembers(response.data || []);
        } catch (error) {
            console.error('Error fetching team members:', error);
            toast.error('Failed to load team members');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center py-8">
                <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                    className="w-8 h-8 border-3 border-blue-500 border-t-transparent rounded-full"
                />
            </div>
        );
    }

    if (members.length === 0) {
        return (
            <div className="text-center py-8">
                <div className="w-16 h-16 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Users className="w-8 h-8 text-gray-400" />
                </div>
                <p className="text-gray-600 dark:text-gray-400">No team members yet</p>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            {members.map((member, index) => (
                <motion.div
                    key={member.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.05 }}
                    className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6 hover:shadow-lg transition-all group"
                >
                    <div className="flex items-start gap-4">
                        {/* Avatar */}
                        <div className="relative">
                            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white text-2xl font-bold">
                                {member.name?.charAt(0) || 'M'}
                            </div>
                            {member.isLeader && (
                                <div className="absolute -top-1 -right-1 w-6 h-6 bg-yellow-400 rounded-full flex items-center justify-center border-2 border-white dark:border-gray-800">
                                    <Crown className="w-3 h-3 text-yellow-800" />
                                </div>
                            )}
                        </div>

                        {/* Info */}
                        <div className="flex-1 min-w-0">
                            <div className="flex items-start justify-between mb-2">
                                <div>
                                    <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                                        {member.name}
                                        {member.isLeader && (
                                            <span className="px-2 py-0.5 bg-yellow-100 dark:bg-yellow-900/20 text-yellow-700 dark:text-yellow-300 rounded text-xs font-semibold">
                                                Team Leader
                                            </span>
                                        )}
                                    </h3>
                                    <p className="text-sm text-gray-600 dark:text-gray-400">
                                        {member.department || 'Student'}
                                    </p>
                                </div>

                                {/* Menu */}
                                <div className="relative">
                                    <motion.button
                                        whileHover={{ scale: 1.1 }}
                                        whileTap={{ scale: 0.9 }}
                                        onClick={() => setShowMenu(showMenu === member.id ? null : member.id)}
                                        className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
                                    >
                                        <MoreVertical className="w-5 h-5 text-gray-600 dark:text-gray-400" />
                                    </motion.button>

                                    {showMenu === member.id && (
                                        <motion.div
                                            initial={{ opacity: 0, scale: 0.95, y: -10 }}
                                            animate={{ opacity: 1, scale: 1, y: 0 }}
                                            className="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-xl shadow-xl border border-gray-200 dark:border-gray-700 py-2 z-10"
                                        >
                                            <button className="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">
                                                <MessageCircle className="w-4 h-4" />
                                                Send Message
                                            </button>
                                            <button className="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">
                                                <Award className="w-4 h-4" />
                                                View Profile
                                            </button>
                                            {!member.isLeader && (
                                                <button className="w-full flex items-center gap-3 px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors">
                                                    <UserMinus className="w-4 h-4" />
                                                    Remove Member
                                                </button>
                                            )}
                                        </motion.div>
                                    )}
                                </div>
                            </div>

                            {/* Contact Info */}
                            <div className="space-y-2 mb-3">
                                <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                                    <Mail className="w-4 h-4" />
                                    {member.email}
                                </div>
                                {member.phone && (
                                    <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                                        <Phone className="w-4 h-4" />
                                        {member.phone}
                                    </div>
                                )}
                            </div>

                            {/* Skills */}
                            {member.skills && member.skills.length > 0 && (
                                <div className="flex flex-wrap gap-2 mb-3">
                                    {member.skills.slice(0, 3).map((skill, idx) => (
                                        <span
                                            key={idx}
                                            className="px-2 py-1 bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded text-xs font-medium"
                                        >
                                            {skill}
                                        </span>
                                    ))}
                                    {member.skills.length > 3 && (
                                        <span className="px-2 py-1 bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400 rounded text-xs font-medium">
                                            +{member.skills.length - 3} more
                                        </span>
                                    )}
                                </div>
                            )}

                            {/* Rating */}
                            {member.rating && (
                                <div className="flex items-center gap-1">
                                    <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                                    <span className="text-sm font-semibold text-gray-900 dark:text-white">
                                        {member.rating}
                                    </span>
                                    <span className="text-sm text-gray-600 dark:text-gray-400">/ 5.0</span>
                                </div>
                            )}
                        </div>
                    </div>
                </motion.div>
            ))}
        </div>
    );
};

export default TeamMembers;
