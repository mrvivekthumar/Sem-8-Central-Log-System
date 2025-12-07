import { AnimatePresence, motion } from 'framer-motion';
import {
  AlertCircle,
  CheckCircle,
  Frown,
  Meh,
  MessageSquare,
  Send,
  Smile,
  Sparkles,
  Star,
  ThumbsUp
} from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import axiosInstance from '../api/axiosInstance';
import { useAuth } from '../contexts/AuthContext';

const Feedback = () => {
  const { user } = useAuth();
  const [rating, setRating] = useState(0);
  const [hoveredRating, setHoveredRating] = useState(0);
  const [category, setCategory] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const categories = [
    { value: 'feature', label: 'Feature Request', icon: Sparkles, color: 'blue' },
    { value: 'bug', label: 'Bug Report', icon: AlertCircle, color: 'red' },
    { value: 'improvement', label: 'Improvement', icon: ThumbsUp, color: 'green' },
    { value: 'general', label: 'General Feedback', icon: MessageSquare, color: 'purple' }
  ];

  const ratingLabels = [
    { value: 1, label: 'Poor', icon: Frown, color: 'text-red-500' },
    { value: 2, label: 'Fair', icon: Meh, color: 'text-orange-500' },
    { value: 3, label: 'Good', icon: Smile, color: 'text-yellow-500' },
    { value: 4, label: 'Very Good', icon: Smile, color: 'text-lime-500' },
    { value: 5, label: 'Excellent', icon: Smile, color: 'text-green-500' }
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!rating) {
      toast.error('Please select a rating');
      return;
    }

    if (!category) {
      toast.error('Please select a category');
      return;
    }

    if (!message.trim()) {
      toast.error('Please enter your feedback');
      return;
    }

    setLoading(true);

    try {
      await axiosInstance.post('/feedback', {
        userId: user?.id,
        rating,
        category,
        message,
        timestamp: new Date().toISOString()
      });

      toast.success('Thank you for your feedback!');
      setSubmitted(true);

      // Reset form after 3 seconds
      setTimeout(() => {
        setRating(0);
        setCategory('');
        setMessage('');
        setSubmitted(false);
      }, 3000);
    } catch (error) {
      console.error('Error submitting feedback:', error);
      toast.error('Failed to submit feedback. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-12"
        >
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl mb-4 shadow-xl">
            <MessageSquare className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-3">
            We Value Your Feedback
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
            Help us improve CollabBridge by sharing your thoughts, suggestions, and experiences.
          </p>
        </motion.div>

        <AnimatePresence mode="wait">
          {submitted ? (
            // Success State
            <motion.div
              key="success"
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.8 }}
              className="bg-white dark:bg-gray-800 rounded-3xl p-12 border border-gray-200 dark:border-gray-700 text-center shadow-2xl"
            >
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ delay: 0.2, type: "spring", stiffness: 200 }}
                className="w-20 h-20 bg-green-100 dark:bg-green-900/20 rounded-full flex items-center justify-center mx-auto mb-6"
              >
                <CheckCircle className="w-10 h-10 text-green-600 dark:text-green-400" />
              </motion.div>
              <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-3">
                Thank You!
              </h2>
              <p className="text-gray-600 dark:text-gray-400 text-lg">
                Your feedback has been submitted successfully. We appreciate your input!
              </p>
            </motion.div>
          ) : (
            // Feedback Form
            <motion.div
              key="form"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="bg-white dark:bg-gray-800 rounded-3xl p-8 md:p-12 border border-gray-200 dark:border-gray-700 shadow-2xl"
            >
              <form onSubmit={handleSubmit} className="space-y-8">
                {/* Rating Section */}
                <div>
                  <label className="block text-lg font-semibold text-gray-900 dark:text-white mb-4">
                    How would you rate your experience?
                  </label>
                  <div className="flex items-center justify-center gap-3 mb-3">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <motion.button
                        key={star}
                        type="button"
                        whileHover={{ scale: 1.2 }}
                        whileTap={{ scale: 0.9 }}
                        onClick={() => setRating(star)}
                        onMouseEnter={() => setHoveredRating(star)}
                        onMouseLeave={() => setHoveredRating(0)}
                        className="focus:outline-none"
                      >
                        <Star
                          className={`w-12 h-12 transition-all ${star <= (hoveredRating || rating)
                            ? 'fill-yellow-400 text-yellow-400'
                            : 'text-gray-300 dark:text-gray-600'
                            }`}
                        />
                      </motion.button>
                    ))}
                  </div>
                  {(hoveredRating || rating) > 0 && (
                    <motion.div
                      initial={{ opacity: 0, y: -10 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="text-center"
                    >
                      <span className={`text-lg font-semibold ${ratingLabels[(hoveredRating || rating) - 1].color}`}>
                        {ratingLabels[(hoveredRating || rating) - 1].label}
                      </span>
                    </motion.div>
                  )}
                </div>

                {/* Category Selection */}
                <div>
                  <label className="block text-lg font-semibold text-gray-900 dark:text-white mb-4">
                    What type of feedback is this?
                  </label>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    {categories.map((cat) => {
                      const Icon = cat.icon;
                      const isSelected = category === cat.value;
                      return (
                        <motion.button
                          key={cat.value}
                          type="button"
                          whileHover={{ scale: 1.05 }}
                          whileTap={{ scale: 0.95 }}
                          onClick={() => setCategory(cat.value)}
                          className={`p-4 rounded-xl border-2 transition-all ${isSelected
                            ? `border-${cat.color}-500 bg-${cat.color}-50 dark:bg-${cat.color}-900/20`
                            : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'
                            }`}
                        >
                          <Icon className={`w-6 h-6 mx-auto mb-2 ${isSelected ? `text-${cat.color}-600` : 'text-gray-400'
                            }`} />
                          <div className={`text-sm font-medium ${isSelected ? `text-${cat.color}-700 dark:text-${cat.color}-300` : 'text-gray-600 dark:text-gray-400'
                            }`}>
                            {cat.label}
                          </div>
                        </motion.button>
                      );
                    })}
                  </div>
                </div>

                {/* Message Input */}
                <div>
                  <label className="block text-lg font-semibold text-gray-900 dark:text-white mb-4">
                    Tell us more about your experience
                  </label>
                  <textarea
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Share your thoughts, suggestions, or report any issues..."
                    rows={6}
                    className="w-full px-4 py-3 rounded-xl border-2 border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 focus:border-blue-500 focus:outline-none transition-colors resize-none"
                  />
                  <div className="flex items-center justify-between mt-2">
                    <span className="text-sm text-gray-500 dark:text-gray-400">
                      {message.length} / 500 characters
                    </span>
                    {message.length > 500 && (
                      <span className="text-sm text-red-500">
                        Too long! Please keep it under 500 characters.
                      </span>
                    )}
                  </div>
                </div>

                {/* User Info Display */}
                {user && (
                  <div className="p-4 bg-gray-50 dark:bg-gray-700 rounded-xl">
                    <div className="text-sm text-gray-600 dark:text-gray-400">
                      Submitting as: <span className="font-semibold text-gray-900 dark:text-white">{user.name}</span>
                    </div>
                  </div>
                )}

                {/* Submit Button */}
                <motion.button
                  type="submit"
                  disabled={loading || message.length > 500}
                  whileHover={{ scale: loading ? 1 : 1.02 }}
                  whileTap={{ scale: loading ? 1 : 0.98 }}
                  className="w-full py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl font-bold text-lg hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-3"
                >
                  {loading ? (
                    <>
                      <motion.div
                        animate={{ rotate: 360 }}
                        transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                        className="w-6 h-6 border-3 border-white border-t-transparent rounded-full"
                      />
                      Submitting...
                    </>
                  ) : (
                    <>
                      <Send className="w-6 h-6" />
                      Submit Feedback
                    </>
                  )}
                </motion.button>
              </form>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Additional Info */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.5 }}
          className="mt-8 text-center"
        >
          <p className="text-sm text-gray-600 dark:text-gray-400">
            Your feedback helps us build a better experience for everyone. Thank you for taking the time!
          </p>
        </motion.div>
      </div>
    </div>
  );
};

export default Feedback;
