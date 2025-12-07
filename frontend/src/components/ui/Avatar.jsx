import { motion } from 'framer-motion';
import { User } from 'lucide-react';

const Avatar = ({
  src,
  alt = 'Avatar',
  size = 'md', // sm, md, lg, xl
  fallback,
  online = false,
  className = ''
}) => {
  const sizes = {
    sm: 'w-8 h-8 text-sm',
    md: 'w-12 h-12 text-base',
    lg: 'w-16 h-16 text-xl',
    xl: 'w-24 h-24 text-3xl'
  };

  const onlineSizes = {
    sm: 'w-2 h-2',
    md: 'w-3 h-3',
    lg: 'w-4 h-4',
    xl: 'w-5 h-5'
  };

  return (
    <div className={`relative ${className}`}>
      {src ? (
        <motion.img
          whileHover={{ scale: 1.05 }}
          src={src}
          alt={alt}
          className={`${sizes[size]} rounded-full object-cover border-2 border-white dark:border-gray-800 shadow-lg`}
        />
      ) : (
        <motion.div
          whileHover={{ scale: 1.05 }}
          className={`${sizes[size]} rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold shadow-lg`}
        >
          {fallback || <User className="w-1/2 h-1/2" />}
        </motion.div>
      )}

      {online && (
        <span className={`absolute bottom-0 right-0 ${onlineSizes[size]} bg-green-500 border-2 border-white dark:border-gray-800 rounded-full`} />
      )}
    </div>
  );
};

export default Avatar;
