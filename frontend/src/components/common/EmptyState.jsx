import { motion } from 'framer-motion';
import { Inbox, Search, FileX, AlertCircle } from 'lucide-react';

const EmptyState = ({
    icon: Icon = Inbox,
    title = 'No data found',
    description = 'There is nothing to display at the moment',
    action,
    variant = 'default' // default, search, error
}) => {
    const getVariantStyles = () => {
        switch (variant) {
            case 'search':
                return {
                    icon: Search,
                    iconBg: 'bg-blue-100 dark:bg-blue-900/20',
                    iconColor: 'text-blue-600 dark:text-blue-400'
                };
            case 'error':
                return {
                    icon: AlertCircle,
                    iconBg: 'bg-red-100 dark:bg-red-900/20',
                    iconColor: 'text-red-600 dark:text-red-400'
                };
            default:
                return {
                    icon: Icon,
                    iconBg: 'bg-gray-100 dark:bg-gray-700',
                    iconColor: 'text-gray-400 dark:text-gray-500'
                };
        }
    };

    const styles = getVariantStyles();
    const DisplayIcon = styles.icon;

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex flex-col items-center justify-center py-12 px-4 text-center"
        >
            {/* Icon */}
            <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ type: "spring", delay: 0.1 }}
                className={`w-20 h-20 ${styles.iconBg} rounded-full flex items-center justify-center mb-6`}
            >
                <DisplayIcon className={`w-10 h-10 ${styles.iconColor}`} />
            </motion.div>

            {/* Title */}
            <motion.h3
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.2 }}
                className="text-xl font-bold text-gray-900 dark:text-white mb-2"
            >
                {title}
            </motion.h3>

            {/* Description */}
            <motion.p
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.3 }}
                className="text-gray-600 dark:text-gray-400 max-w-md mb-6"
            >
                {description}
            </motion.p>

            {/* Action Button */}
            {action && (
                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.4 }}
                >
                    {action}
                </motion.div>
            )}
        </motion.div>
    );
};

export default EmptyState;
