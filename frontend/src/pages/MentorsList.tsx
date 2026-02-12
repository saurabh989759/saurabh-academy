import { Link } from 'react-router-dom'
import { useMentors, useDeleteMentor } from '../hooks/useMentors'
import { Mentor } from '../api/mentors'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { EmptyState } from '../components/ui/EmptyState'
import { EyeIcon, PencilIcon, PlusIcon, TrashIcon } from '../components/icons/Icons'

const MentorsList = () => {
  const { data: mentors, isLoading, error, refetch } = useMentors()
  const deleteMentor = useDeleteMentor()

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this mentor?')) {
      try {
        await deleteMentor.mutateAsync(id)
        // List will automatically refresh due to query invalidation in the hook
      } catch (err) {
        // Error handled by toast notification
        console.error('Failed to delete mentor:', err)
      }
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading mentors..." />
  }

  if (error) {
    return <ErrorDisplay error={error} title="Failed to load mentors" onRetry={() => refetch()} />
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Mentors</h1>
        <Link
          to="/mentors/new"
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 transition-colors"
        >
          <PlusIcon className="w-5 h-5" />
          Create Mentor
        </Link>
      </div>

      {mentors && mentors.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {mentors.map((mentor: Mentor) => (
            <div
              key={mentor.id}
              className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 border border-gray-200 dark:border-gray-700"
            >
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                {mentor.name}
              </h3>
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">
                Company: {mentor.currentCompany || 'Not specified'}
              </p>
              <div className="flex space-x-2">
                <Link
                  to={`/mentors/${mentor.id}`}
                  className="flex items-center justify-center gap-1 flex-1 text-center bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 transition-colors text-sm"
                  onClick={(e) => e.stopPropagation()}
                >
                  <EyeIcon className="w-4 h-4" />
                  View
                </Link>
                <Link
                  to={`/mentors/${mentor.id}/edit`}
                  className="flex items-center justify-center gap-1 flex-1 text-center bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors text-sm"
                  onClick={(e) => e.stopPropagation()}
                >
                  <PencilIcon className="w-4 h-4" />
                  Edit
                </Link>
                <button
                  onClick={(e) => {
                    e.stopPropagation()
                    if (mentor.id) handleDelete(mentor.id)
                  }}
                  className="flex items-center justify-center gap-1 flex-1 text-center bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                  disabled={!mentor.id || deleteMentor.isPending}
                >
                  <TrashIcon className="w-4 h-4" />
                  {deleteMentor.isPending ? 'Deleting...' : 'Delete'}
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No mentors found"
          description="Get started by creating your first mentor."
          actionLabel="Create Mentor"
          onAction={() => (window.location.href = '/mentors/new')}
        />
      )}
    </div>
  )
}

export default MentorsList

