import { AnimatePresence, motion } from 'framer-motion';
import { AlertTriangle, CheckCircle, Info, X } from 'lucide-react';

const ConfirmationDialog = ({
    isOpen,
    onClose,
    onConfirm,
    title = 'Confirm Action',
    message = 'Are you sure you want to proceed?',
    confirmText = 'Confirm',
    cancelText = 'Cancel',
    variant = 'warning', // warning, danger, info, success
    loading = false
}) => {
    if (!isOpen) return null;

    const getVariantConfig = () => {
        const configs = {
            warning: {
                icon: AlertTriangle,
                iconBg: 'bg-yellow-100 dark:bg-yellow-900/20',
                iconColor: 'text-yellow-600 dark:text-yellow-400',
                confirmBg: 'bg-yellow-600 hover:bg-yellow-700',
                gradient: 'from-yellow-500 to-orange-600'
            },
            danger: {
                icon: AlertTriangle,
                iconBg: 'bg-red-100 dark:bg-red-900/20',
                iconColor: 'text-red-600 dark:text-red-400',
                confirmBg: 'bg-red-600 hover:bg-red-700',
                gradient: 'from-red-500 to-pink-600'
            },
            info: {
                icon: Info,
                iconBg: 'bg-blue-100 dark:bg-blue-900/20',
                iconColor: 'text-blue-600 dark:text-blue-400',
                confirmBg: 'bg-blue-600 hover:bg-blue-700',
                gradient: 'from-blue-500 to-purple-600'
            },
            success: {
                icon: CheckCircle,
                iconBg: 'bg-green-100 dark:bg-green-900/20',
                iconColor: 'text-green-600 dark:text-green-400',
                confirmBg: 'bg-green-600 hover:bg-green-700',
                gradient: 'from-green-500 to-emerald-600'
            }
        };
        return configs[variant] || configs.warning;
    };

    const config = getVariantConfig();
    const Icon = config.icon;

    return (
        <AnimatePresence>
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
                onClick={onClose}
            >
                <motion.div
                    initial={{ scale: 0.9, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    exit={{ scale: 0.9, opacity: 0 }}
                    onClick={(e) => e.stopPropagation()}
                    className="bg-white dark:bg-gray-800 rounded-2xl w-full max-w-md overflow-hidden shadow-2xl border border-gray-200 dark:border-gray-700"
                >
                    {/* Header Gradient */}
                    <div className={`h-2 bg-gradient-to-r ${config.gradient}`} />

                    <div className="p-6">
                        {/* Icon and Close */}
                        <div className="flex items-start justify-between mb-4">
                            <div className={`w-12 h-12 ${config.iconBg} rounded-xl flex items-center justify-center`}>
                                <Icon className={`w-6 h-6 ${config.iconColor}`} />
                            </div>
                            <motion.button
                                whileHover={{ scale: 1.1, rotate: 90 }}
                                whileTap={{ scale: 0.9 }}
                                onClick={onClose}
                                className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
                            >
                                <X className="w-5 h-5 text-gray-600 dark:text-gray-400" />
                            </motion.button>
                        </div>

                        {/* Title */}
                        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                            {title}
                        </h3>

                        {/* Message */}
                        <p className="text-gray-600 dark:text-gray-400 mb-6 leading-relaxed">
                            {message}
                        </p>

                        {/* Actions */}
                        <div className="flex gap-3">
                            <motion.button
                                whileHover={{ scale: 1.02 }}
                                whileTap={{ scale: 0.98 }}
                                onClick={onClose}
                                disabled={loading}
                                className="flex-1 px-4 py-3 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50"
                            >
                                {cancelText}
                            </motion.button>
                            <motion.button
                                whileHover={{ scale: loading ? 1 : 1.02 }}
                                whileTap={{ scale: loading ? 1 : 0.98 }}
                                onClick={onConfirm}
                                disabled={loading}
                                className={`flex-1 px-4 py-3 ${config.confirmBg} text-white rounded-xl font-semibold transition-all shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2`}
                            >
                                {loading ? (
                                    <>
                                        <motion.div
                                            animate={{ rotate: 360 }}
                                            transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                                            className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                                        />
                                        Processing...
                                    </>
                                ) : (
                                    confirmText
                                )}
                            </motion.button>
                        </div>
                    </div>
                </motion.div>
            </motion.div>
        </AnimatePresence>
    );
};

export default ConfirmationDialog;
