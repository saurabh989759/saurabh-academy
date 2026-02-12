import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useMentor, useUpdateMentor } from '../hooks/useMentors'
import { MentorInput } from '../api/mentors'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const EditMentor = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const mentorId = id ? parseInt(id) : 0
  const { data: mentor, isLoading, error } = useMentor(mentorId)
  const updateMentor = useUpdateMentor()
  const [formData, setFormData] = useState<MentorInput>({
    name: '',
    currentCompany: '',
  })

  useEffect(() => {
    if (mentor) {
      setFormData({
        name: mentor.name || '',
        currentCompany: mentor.currentCompany || '',
      })
    }
  }, [mentor])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await updateMentor.mutateAsync({ id: mentorId, mentor: formData })
      navigate(`/mentors/${mentorId}`)
    } catch (error) {
      // Error handling is done in the mutation
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading mentor..." />
  }

  if (error || !mentor) {
    return <ErrorDisplay error={error || new Error('Mentor not found')} title="Failed to load mentor" />
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">Edit Mentor</h1>
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
          <label htmlFor="currentCompany" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Current Company
          </label>
          <input
            id="currentCompany"
            type="text"
            value={formData.currentCompany}
            onChange={(e) => setFormData({ ...formData, currentCompany: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div className="flex space-x-4 pt-4">
          <button
            type="submit"
            disabled={updateMentor.isPending}
            className="flex-1 bg-primary-600 text-white py-2 px-4 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {updateMentor.isPending ? 'Updating...' : 'Update Mentor'}
          </button>
          <button
            type="button"
            onClick={() => navigate(`/mentors/${mentorId}`)}
            className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 dark:bg-gray-700 dark:text-white"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditMentor

