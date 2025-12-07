import { motion } from 'framer-motion';
import { Minus, TrendingDown, TrendingUp } from 'lucide-react';

const StatsCard = ({
  title,
  value,
  icon: Icon,
  change,
  changeType = 'neutral', // increase, decrease, neutral
  gradient = 'from-blue-500 to-purple-600',
  delay = 0
}) => {
  const getTrendIcon = () => {
    switch (changeType) {
      case 'increase':
        return <TrendingUp className="w-4 h-4" />;
      case 'decrease':
        return <TrendingDown className="w-4 h-4" />;
      default:
        return <Minus className="w-4 h-4" />;
    }
  };

  const getTrendColor = () => {
    switch (changeType) {
      case 'increase':
        return 'text-green-600 dark:text-green-400 bg-green-50 dark:bg-green-900/20';
      case 'decrease':
        return 'text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20';
      default:
        return 'text-gray-600 dark:text-gray-400 bg-gray-50 dark:bg-gray-700';
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay }}
      whileHover={{ y: -8, scale: 1.02 }}
      className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6 hover:shadow-2xl transition-all group relative overflow-hidden"
    >
      {/* Background Gradient Effect */}
      <div className={`absolute top-0 right-0 w-32 h-32 bg-gradient-to-br ${gradient} opacity-5 rounded-full blur-2xl group-hover:opacity-10 transition-opacity`} />

      <div className="relative z-10">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className={`w-12 h-12 bg-gradient-to-br ${gradient} rounded-xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform`}>
            {Icon && <Icon className="w-6 h-6 text-white" />}
          </div>
          {change && (
            <div className={`flex items-center gap-1 px-2 py-1 rounded-lg text-xs font-semibold ${getTrendColor()}`}>
              {getTrendIcon()}
              <span>{change}</span>
            </div>
          )}
        </div>

        {/* Value */}
        <motion.div
          initial={{ scale: 0.5, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ delay: delay + 0.1, type: "spring" }}
          className="text-4xl font-bold text-gray-900 dark:text-white mb-2"
        >
          {value}
        </motion.div>

        {/* Title */}
        <p className="text-sm text-gray-600 dark:text-gray-400 font-medium">
          {title}
        </p>
      </div>

      {/* Hover Border Effect */}
      <motion.div
        initial={{ scale: 0, opacity: 0 }}
        whileHover={{ scale: 1, opacity: 1 }}
        className={`absolute inset-0 border-2 border-transparent bg-gradient-to-br ${gradient} rounded-2xl opacity-0 group-hover:opacity-20 transition-opacity pointer-events-none`}
      />
    </motion.div>
  );
};

export default StatsCard;
