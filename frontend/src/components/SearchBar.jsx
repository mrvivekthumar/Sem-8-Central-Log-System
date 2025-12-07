import { motion } from 'framer-motion';
import { Search, X } from 'lucide-react';
import { useState } from 'react';

const SearchBar = ({ value, onChange, placeholder = "Search projects..." }) => {
  const [isFocused, setIsFocused] = useState(false);

  const handleClear = () => {
    onChange('');
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      className="relative"
    >
      <div className={`relative transition-all ${isFocused ? 'ring-2 ring-blue-500' : ''
        }`}>
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />

        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder={placeholder}
          className="w-full pl-12 pr-12 py-4 bg-white dark:bg-gray-800 border-2 border-gray-200 dark:border-gray-700 rounded-2xl text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 transition-all text-lg"
        />

        {value && (
          <motion.button
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            exit={{ scale: 0 }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            onClick={handleClear}
            className="absolute right-4 top-1/2 -translate-y-1/2 p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors"
          >
            <X className="w-4 h-4 text-gray-500" />
          </motion.button>
        )}
      </div>

      {/* Search Hint */}
      {isFocused && !value && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="absolute top-full mt-2 w-full bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl p-4 shadow-xl z-10"
        >
          <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
            Try searching for:
          </p>
          <div className="flex flex-wrap gap-2">
            {['Machine Learning', 'Web Development', 'AI', 'Mobile Apps'].map((hint) => (
              <button
                key={hint}
                onClick={() => onChange(hint)}
                className="px-3 py-1 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg text-xs hover:bg-blue-100 dark:hover:bg-blue-900/20 transition-colors"
              >
                {hint}
              </button>
            ))}
          </div>
        </motion.div>
      )}
    </motion.div>
  );
};

export default SearchBar;
