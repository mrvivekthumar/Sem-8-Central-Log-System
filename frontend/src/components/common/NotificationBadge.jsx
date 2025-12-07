import { AnimatePresence, motion } from 'framer-motion';

const NotificationBadge = ({ count, showZero = false }) => {
    if (!count && !showZero) return null;

    const displayCount = count > 99 ? '99+' : count;

    return (
        <AnimatePresence>
            <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                exit={{ scale: 0 }}
                className="absolute -top-1 -right-1 min-w-[20px] h-5 bg-gradient-to-r from-red-500 to-pink-600 text-white text-xs font-bold rounded-full flex items-center justify-center px-1.5 shadow-lg border-2 border-white dark:border-gray-800"
            >
                <motion.span
                    key={displayCount}
                    initial={{ scale: 1.5, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    transition={{ type: "spring", stiffness: 500, damping: 30 }}
                >
                    {displayCount}
                </motion.span>
            </motion.div>
        </AnimatePresence>
    );
};

export default NotificationBadge;
