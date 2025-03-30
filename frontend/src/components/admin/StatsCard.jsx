import { motion } from 'framer-motion';

const StatsCard = ({ title, value, icon: Icon, color }) => {
  const colorClasses = {
    blue: 'from-blue-500 to-blue-600 shadow-blue-500/30',
    purple: 'from-purple-500 to-purple-600 shadow-purple-500/30',
    green: 'from-green-500 to-green-600 shadow-green-500/30',
    amber: 'from-amber-500 to-amber-600 shadow-amber-500/30',
    red: 'from-red-500 to-red-600 shadow-red-500/30',
  };

  return (
    <motion.div
      whileHover={{ y: -5 }}
      className="bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden"
    >
      <div className="p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-medium text-gray-700 dark:text-gray-300">
            {title}
          </h3>
          <div className={`h-10 w-10 rounded-lg bg-gradient-to-br ${colorClasses[color]} flex items-center justify-center shadow-lg`}>
            <Icon className="h-5 w-5 text-white" />
          </div>
        </div>
        <div className="flex items-end">
          <span className="text-3xl font-bold text-gray-900 dark:text-white">
            {value.toLocaleString()}
          </span>
        </div>
      </div>
    </motion.div>
  );
};

export default StatsCard;