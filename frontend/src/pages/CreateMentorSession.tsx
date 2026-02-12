import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateMentorSession } from '../hooks/useMentorSessions'
import { MentorSessionInput } from '../api/mentorSessions'

const CreateMentorSession = () => {
  const navigate = useNavigate()
  const createSession = useCreateMentorSession()
  const [formData, setFormData] = useState<MentorSessionInput>({
    time: '',
    durationMinutes: 60,
    studentId: 1,
    mentorId: 1,
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const result = await createSession.mutateAsync(formData)
      navigate(`/mentor-sessions/${result.id}`)
    } catch (error) {
      // Error handling is done in the mutation
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">Book Mentor Session</h1>
      <form onSubmit={handleSubmit} className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 space-y-4">
        <div>
          <label htmlFor="time" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Session Time *
          </label>
          <input
            id="time"
            type="datetime-local"
            required
            value={formData.time}
            onChange={(e) => setFormData({ ...formData, time: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="durationMinutes" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Duration (minutes) *
          </label>
          <input
            id="durationMinutes"
            type="number"
            required
            min="15"
            step="15"
            value={formData.durationMinutes}
            onChange={(e) => setFormData({ ...formData, durationMinutes: parseInt(e.target.value) })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="studentId" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Student ID *
          </label>
          <input
            id="studentId"
            type="number"
            required
            min="1"
            value={formData.studentId}
            onChange={(e) => setFormData({ ...formData, studentId: parseInt(e.target.value) })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="mentorId" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Mentor ID *
          </label>
          <input
            id="mentorId"
            type="number"
            required
            min="1"
            value={formData.mentorId}
            onChange={(e) => setFormData({ ...formData, mentorId: parseInt(e.target.value) })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="studentRating" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Student Rating (optional)
          </label>
          <input
            id="studentRating"
            type="number"
            min="1"
            max="5"
            value={formData.studentRating || ''}
            onChange={(e) =>
              setFormData({ ...formData, studentRating: e.target.value ? parseInt(e.target.value) : undefined })
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="mentorRating" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Mentor Rating (optional)
          </label>
          <input
            id="mentorRating"
            type="number"
            min="1"
            max="5"
            value={formData.mentorRating || ''}
            onChange={(e) =>
              setFormData({ ...formData, mentorRating: e.target.value ? parseInt(e.target.value) : undefined })
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div className="flex space-x-4 pt-4">
          <button
            type="submit"
            disabled={createSession.isPending}
            className="flex-1 bg-primary-600 text-white py-2 px-4 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {createSession.isPending ? 'Booking...' : 'Book Session'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/mentor-sessions')}
            className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 dark:bg-gray-700 dark:text-white"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateMentorSession

