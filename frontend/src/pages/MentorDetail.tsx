import { useParams, Link, useNavigate } from 'react-router-dom'
import { useMentor, useDeleteMentor } from '../hooks/useMentors'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { PencilIcon, TrashIcon } from '../components/icons/Icons'

const MentorDetail = () => {
  const { id } = useParams<{ id: string }>()
  const mentorId = id ? parseInt(id) : 0
  const { data: mentor, isLoading, error } = useMentor(mentorId)
  const deleteMentor = useDeleteMentor()
  const navigate = useNavigate()

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this mentor?')) {
      try {
        await deleteMentor.mutateAsync(mentorId)
        navigate('/mentors')
      } catch (err) {
        // Error handled by toast notification
      }
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading mentor..." />
  }

  if (error || !mentor) {
    return <ErrorDisplay error={error || new Error('Mentor not found')} title="Failed to load mentor" />
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Mentor Details</h1>
        <div className="space-x-2">
          <Link
            to={`/mentors/${mentor.id}/edit`}
            className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            <PencilIcon className="w-4 h-4" />
            Edit
          </Link>
          <button
            onClick={handleDelete}
            className="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700"
          >
            <TrashIcon className="w-4 h-4" />
            Delete
          </button>
        </div>
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
        <dl className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Name</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">{mentor.name}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Current Company</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {mentor.currentCompany || 'Not specified'}
            </dd>
          </div>
        </dl>
      </div>
    </div>
  )
}

export default MentorDetail

