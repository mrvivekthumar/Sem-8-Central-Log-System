"use client"
import { X } from "lucide-react"

export const Modal = ({ title, message, onClose, icon }) => {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6 relative animate-fadeIn">
        <button onClick={onClose} className="absolute top-4 right-4 text-gray-400 hover:text-gray-600">
          <X size={20} />
        </button>

        <div className="text-center">
          {icon && (
            <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-green-100 mb-4">
              {icon}
            </div>
          )}

          <h3 className="text-lg font-medium text-gray-900 mb-2">{title}</h3>

          <p className="text-gray-600">{message}</p>

          <button onClick={onClose} className="mt-6 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
            Close
          </button>
        </div>
      </div>
    </div>
  )
}