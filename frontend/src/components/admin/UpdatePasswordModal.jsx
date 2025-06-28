import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Lock, Eye, EyeOff, Check } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import axiosInstance from '../../api/axiosInstance';

const passwordSchema = z.object({
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
}).refine(data => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
});

const UpdatePasswordModal = ({ isOpen, onClose, user, onUpdate }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  const { register, handleSubmit, formState: { errors }, reset } = useForm({
    resolver: zodResolver(passwordSchema),
    defaultValues: {
      password: '',
      confirmPassword: '',
    }
  });

  const updatePassword = async (userData) => {
    try {

      const response = await axiosInstance.post(
        '/auth/updatePassword',
        userData
      );


      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Failed to update password');
      }

      const data = await response.text();
      return data;
    } catch (error) {
      console.error('Error updating password:', error);
      throw error;
    }
  };

  const onSubmit = async (data) => {
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      // Prepare data for API
      const userData = {
        username: user.email, // Assuming user.email contains the email/username
        password: data.password,
        userRole: user.role?.toUpperCase() // Assuming user.role contains 'student' or 'faculty'
      };

      const result = await updatePassword(userData);

      // Display success message
      setMessage({ type: 'success', text: 'Password updated successfully!' });

      // Clear form
      reset();

      // If onUpdate callback exists, call it
      if (onUpdate) {
        onUpdate(user.id, data.password, result);
      }

      // Close modal after delay
      setTimeout(() => {
        onClose();
      }, 2000);
    } catch (error) {
      setMessage({ type: 'error', text: `Failed to update password: ${error.message}` });
    } finally {
      setLoading(false);
    }
  };

  // This prevents event propagation
  const stopPropagation = (e) => {
    e.stopPropagation();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        // Modal container - exactly matching RegisterModal structure
        <div className="fixed inset-0 z-50 overflow-hidden flex items-center justify-center">
          {/* Semi-transparent backdrop */}
          <div
            className="absolute inset-0 bg-black bg-opacity-50"
            onClick={onClose}
          ></div>

          {/* Modal content - stop propagation on this element */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className="relative bg-white dark:bg-gray-800 w-full max-w-md mx-4 rounded-lg shadow-xl z-10"
            onClick={stopPropagation}
          >
            {/* Close button */}
            <div className="absolute right-0 top-0 pr-4 pt-4">
              <button
                onClick={onClose}
                className="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300 transition-colors"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {/* Form content */}
            <div className="p-6">
              <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
                Update Password
              </h3>
              <p className="text-gray-600 dark:text-gray-300 mb-6">
                Set a new password for {user?.name}
              </p>

              {/* Status message */}
              {message.text && (
                <div className={`mb-4 p-3 rounded-lg ${message.type === 'success'
                  ? 'bg-green-100 text-green-800 dark:bg-green-800/20 dark:text-green-400'
                  : 'bg-red-100 text-red-800 dark:bg-red-800/20 dark:text-red-400'
                  }`}>
                  {message.text}
                </div>
              )}

              <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    New Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type={showPassword ? "text" : "password"}
                      {...register('password')}
                      className="w-full pl-10 pr-10 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="Enter new password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-500"
                    >
                      {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                    </button>
                  </div>
                  {errors.password && (
                    <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Confirm Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type={showConfirmPassword ? "text" : "password"}
                      {...register('confirmPassword')}
                      className="w-full pl-10 pr-10 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="Confirm new password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-500"
                    >
                      {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                    </button>
                  </div>
                  {errors.confirmPassword && (
                    <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>
                  )}
                </div>

                <div className="flex justify-end gap-4 pt-4">
                  <button
                    type="button"
                    onClick={onClose}
                    className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
                    disabled={loading}
                  >
                    Cancel
                  </button>
                  <motion.button
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                    type="submit"
                    className={`flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors ${loading ? 'opacity-70 cursor-not-allowed' : ''
                      }`}
                    disabled={loading}
                  >
                    <Check className="h-4 w-4 mr-2" />
                    {loading ? 'Updating...' : 'Update Password'}
                  </motion.button>
                </div>
              </form>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
};

export default UpdatePasswordModal;