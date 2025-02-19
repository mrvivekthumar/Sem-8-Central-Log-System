import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, Minus } from "lucide-react";
import toast from "react-hot-toast";

const FullPageProjectForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    status: "OPEN_FOR_APPLICATIONS",
    date: new Date().toISOString().split("T")[0],
    deadline: "",
    applicationDeadline: "",
    faculty: "",
    maxStudents: 4,
    technologies: [""],
    projectType: "Internal",
    budget: "",
    projectUrl: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    toast.success("Project created successfully!");
    navigate(-1);
  };

  return (
    <div className="min-h-screen flex flex-col items-center bg-gray-100 p-6">
      <div className="bg-white shadow-lg rounded-lg w-full max-w-4xl p-6">
        <h2 className="text-3xl font-bold mb-6 text-center">Create New Project</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="font-medium">Title</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded-lg"
              required
            />
          </div>
          
          <div>
            <label className="font-medium">Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded-lg"
              required
            />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="font-medium">Start Date</label>
              <input
                type="date"
                name="date"
                value={formData.date}
                onChange={handleChange}
                className="w-full px-4 py-2 border rounded-lg"
              />
            </div>
            <div>
              <label className="font-medium">Deadline</label>
              <input
                type="date"
                name="deadline"
                value={formData.deadline}
                onChange={handleChange}
                className="w-full px-4 py-2 border rounded-lg"
              />
            </div>
          </div>
          
          <div>
            <label className="font-medium">Faculty</label>
            <input
              type="text"
              name="faculty"
              value={formData.faculty}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded-lg"
            />
          </div>
          
          <div>
            <label className="font-medium">Max Students</label>
            <input
              type="number"
              name="maxStudents"
              value={formData.maxStudents}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded-lg"
            />
          </div>
          
          <div>
            <label className="font-medium">Technologies</label>
            {formData.technologies.map((tech, index) => (
              <div key={index} className="flex items-center space-x-2 mb-2">
                <input
                  type="text"
                  value={tech}
                  onChange={(e) => {
                    const newTech = [...formData.technologies];
                    newTech[index] = e.target.value;
                    setFormData((prev) => ({ ...prev, technologies: newTech }));
                  }}
                  className="flex-grow px-4 py-2 border rounded-lg"
                />
                {index > 0 && (
                  <button
                    type="button"
                    onClick={() => {
                      setFormData((prev) => ({
                        ...prev,
                        technologies: prev.technologies.filter((_, i) => i !== index),
                      }));
                    }}
                    className="text-red-500"
                  >
                    <Minus />
                  </button>
                )}
              </div>
            ))}
            <button
              type="button"
              onClick={() => setFormData((prev) => ({
                ...prev,
                technologies: [...prev.technologies, ""],
              }))}
              className="text-blue-600"
            >
              <Plus /> Add Technology
            </button>
          </div>
          
          <div className="flex justify-between">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="px-4 py-2 bg-gray-500 text-white rounded-lg"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-blue-600 text-white rounded-lg"
            >
              Create Project
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default FullPageProjectForm;
