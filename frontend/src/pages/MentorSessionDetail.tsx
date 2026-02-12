import { useParams, Link, useNavigate } from 'react-router-dom'
import { useMentorSession, useDeleteMentorSession } from '../hooks/useMentorSessions'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { PencilIcon, TrashIcon } from '../components/icons/Icons'

const MentorSessionDetail = () => {
  const { id } = useParams<{ id: string }>()
  const sessionId = id ? parseInt(id) : 0
  const { data: session, isLoading, error } = useMentorSession(sessionId)
  const deleteSession = useDeleteMentorSession()
  const navigate = useNavigate()

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this mentor session?')) {
      try {
        await deleteSession.mutateAsync(sessionId)
        navigate('/mentor-sessions')
      } catch (err) {
        // Error handled by toast notification
      }
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading mentor session..." />
  }

  if (error || !session) {
    return (
      <ErrorDisplay error={error || new Error('Mentor session not found')} title="Failed to load mentor session" />
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Mentor Session Details</h1>
        <div className="space-x-2">
          <Link
            to={`/mentor-sessions/${session.id}/edit`}
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
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Session Time</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {session.time ? new Date(session.time).toLocaleString() : '-'}
            </dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Duration</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {session.durationMinutes ? `${session.durationMinutes} minutes` : '-'}
            </dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Student ID</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">{session.studentId || '-'}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Mentor ID</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">{session.mentorId || '-'}</dd>
          </div>
          {session.studentRating && (
            <div>
              <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Student Rating</dt>
              <dd className="mt-1 text-sm text-gray-900 dark:text-white">{session.studentRating} / 5</dd>
            </div>
          )}
          {session.mentorRating && (
            <div>
              <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Mentor Rating</dt>
              <dd className="mt-1 text-sm text-gray-900 dark:text-white">{session.mentorRating} / 5</dd>
            </div>
          )}
        </dl>
      </div>
    </div>
  )
}

export default MentorSessionDetail

