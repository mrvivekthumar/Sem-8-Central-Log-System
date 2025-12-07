import { motion } from 'framer-motion';
import { Loader2, Sparkles } from 'lucide-react';

const LoadingSpinner = ({
    size = 'md',
    fullScreen = false,
    message = 'Loading...',
    variant = 'default' // default, gradient, dots
}) => {
    const sizeClasses = {
        sm: 'w-6 h-6',
        md: 'w-12 h-12',
        lg: 'w-16 h-16',
        xl: 'w-24 h-24'
    };

    const Spinner = () => {
        if (variant === 'gradient') {
            return (
                <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                    className={`${sizeClasses[size]} rounded-full bg-gradient-to-tr from-blue-500 via-purple-500 to-pink-500 p-1`}
                >
                    <div className="w-full h-full rounded-full bg-white dark:bg-gray-900" />
                </motion.div>
            );
        }

        if (variant === 'dots') {
            return (
                <div className="flex gap-2">
                    {[0, 1, 2].map((i) => (
                        <motion.div
                            key={i}
                            animate={{
                                scale: [1, 1.5, 1],
                                opacity: [0.5, 1, 0.5]
                            }}
                            transition={{
                                duration: 1,
                                repeat: Infinity,
                                delay: i * 0.2
                            }}
                            className="w-3 h-3 bg-blue-600 rounded-full"
                        />
                    ))}
                </div>
            );
        }

        return (
            <motion.div
                animate={{ rotate: 360 }}
                transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                className={`${sizeClasses[size]} border-4 border-blue-200 dark:border-gray-700 border-t-blue-600 dark:border-t-blue-500 rounded-full`}
            />
        );
    };

    const content = (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="flex flex-col items-center justify-center gap-4"
        >
            <Spinner />
            {message && (
                <motion.p
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.2 }}
                    className="text-lg font-medium text-gray-700 dark:text-gray-300"
                >
                    {message}
                </motion.p>
            )}
        </motion.div>
    );

    if (fullScreen) {
        return (
            <div className="fixed inset-0 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm z-50 flex items-center justify-center">
                {content}
            </div>
        );
    }

    return content;
};

export default LoadingSpinner;
