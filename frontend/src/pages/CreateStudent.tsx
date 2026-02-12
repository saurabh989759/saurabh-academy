import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateStudent } from '../hooks/useStudents'
import { StudentInput } from '../api/students'

const CreateStudent = () => {
  const navigate = useNavigate()
  const createStudent = useCreateStudent()
  const [formData, setFormData] = useState<StudentInput>({
    name: '',
    email: '',
    graduationYear: undefined,
    universityName: '',
    phoneNumber: '',
    batchId: undefined,
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    // Prevent double submission
    if (createStudent.isPending) {
      return
    }
    try {
      const result = await createStudent.mutateAsync(formData)
      // Navigate after successful creation
      navigate('/students')
    } catch (error) {
      // Error handling is done in the mutation
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">Create Student</h1>
      <form onSubmit={handleSubmit} className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 space-y-4">
        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Name *
          </label>
          <input
            id="name"
            type="text"
            required
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Email *
          </label>
          <input
            id="email"
            type="email"
            required
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="graduationYear" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Graduation Year
          </label>
          <input
            id="graduationYear"
            type="number"
            value={formData.graduationYear || ''}
            onChange={(e) =>
              setFormData({ ...formData, graduationYear: e.target.value ? parseInt(e.target.value) : undefined })
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="universityName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            University
          </label>
          <input
            id="universityName"
            type="text"
            value={formData.universityName}
            onChange={(e) => setFormData({ ...formData, universityName: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Phone Number
          </label>
          <input
            id="phoneNumber"
            type="tel"
            value={formData.phoneNumber}
            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="batchId" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Batch ID
          </label>
          <input
            id="batchId"
            type="number"
            value={formData.batchId || ''}
            onChange={(e) =>
              setFormData({ ...formData, batchId: e.target.value ? parseInt(e.target.value) : undefined })
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div className="flex space-x-4 pt-4">
          <button
            type="submit"
            disabled={createStudent.isPending}
            className="flex-1 bg-primary-600 text-white py-2 px-4 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {createStudent.isPending ? 'Creating...' : 'Create Student'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/students')}
            className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 dark:bg-gray-700 dark:text-white"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateStudent

