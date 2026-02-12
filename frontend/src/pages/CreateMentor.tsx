import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateMentor } from '../hooks/useMentors'
import { MentorInput } from '../api/mentors'

const CreateMentor = () => {
  const navigate = useNavigate()
  const createMentor = useCreateMentor()
  const [formData, setFormData] = useState<MentorInput>({
    name: '',
    currentCompany: '',
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const result = await createMentor.mutateAsync(formData)
      navigate(`/mentors/${result.id}`)
    } catch (error) {
      // Error handling is done in the mutation
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">Create Mentor</h1>
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
            placeholder="Alice Johnson"
          />
        </div>
        <div>
          <label htmlFor="currentCompany" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Current Company
          </label>
          <input
            id="currentCompany"
            type="text"
            value={formData.currentCompany}
            onChange={(e) => setFormData({ ...formData, currentCompany: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            placeholder="Tech Corp"
          />
        </div>
        <div className="flex space-x-4 pt-4">
          <button
            type="submit"
            disabled={createMentor.isPending}
            className="flex-1 bg-primary-600 text-white py-2 px-4 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {createMentor.isPending ? 'Creating...' : 'Create Mentor'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/mentors')}
            className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 dark:bg-gray-700 dark:text-white"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateMentor

