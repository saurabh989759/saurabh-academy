import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useMentorSessions, useDeleteMentorSession } from '../hooks/useMentorSessions'
import { MentorSession } from '../api/mentorSessions'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { EmptyState } from '../components/ui/EmptyState'
import { EyeIcon, PencilIcon, PlusIcon, TrashIcon } from '../components/icons/Icons'

const MentorSessionsList = () => {
  const navigate = useNavigate()
  const { data: sessions, isLoading, error, refetch } = useMentorSessions()
  const deleteSession = useDeleteMentorSession() // Move hook before conditional returns

  // Debug logging
  React.useEffect(() => {
    if (sessions) {
      console.log('📦 Mentor sessions data received:', sessions)
      console.log('📦 Total sessions:', sessions.length)
    }
    if (error) {
      console.error('❌ Mentor sessions error:', error)
    }
  }, [sessions, error])

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this mentor session?')) {
      try {
        await deleteSession.mutateAsync(id)
        // List will automatically refresh due to query invalidation in the hook
      } catch (err) {
        // Error handled by toast notification
        console.error('Failed to delete mentor session:', err)
      }
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading mentor sessions..." />
  }

  if (error) {
    return (
      <ErrorDisplay error={error} title="Failed to load mentor sessions" onRetry={() => refetch()} />
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Mentor Sessions</h1>
        <button
          onClick={() => navigate('/mentor-sessions/new')}
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 transition-colors"
          type="button"
        >
          <PlusIcon className="w-5 h-5" />
          Book Session
        </button>
      </div>

      {sessions && sessions.length > 0 ? (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-700">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Time
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Duration
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Student ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Mentor ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {sessions.map((session: MentorSession) => (
                <tr key={session.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                    {session.time ? new Date(session.time).toLocaleString() : '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {session.durationMinutes ? `${session.durationMinutes} min` : '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {session.studentId || '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {session.mentorId || '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center gap-3">
                      <button
                        onClick={() => {
                          if (session.id) navigate(`/mentor-sessions/${session.id}`)
                        }}
                        className="flex items-center gap-1 text-primary-600 hover:text-primary-900 dark:text-primary-400 transition-colors"
                        title="View"
                        type="button"
                      >
                        <EyeIcon className="w-4 h-4" />
                        View
                      </button>
                      <button
                        onClick={() => {
                          if (session.id) navigate(`/mentor-sessions/${session.id}/edit`)
                        }}
                        className="flex items-center gap-1 text-blue-600 hover:text-blue-900 dark:text-blue-400 transition-colors"
                        title="Edit"
                        type="button"
                      >
                        <PencilIcon className="w-4 h-4" />
                        Edit
                      </button>
                      <button
                        onClick={() => session.id && handleDelete(session.id)}
                        className="flex items-center gap-1 text-red-600 hover:text-red-900 dark:text-red-400 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        disabled={!session.id || deleteSession.isPending}
                        title="Delete"
                        type="button"
                      >
                        <TrashIcon className="w-4 h-4" />
                        {deleteSession.isPending ? 'Deleting...' : 'Delete'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <EmptyState
          title="No mentor sessions found"
          description="Get started by booking your first mentor session."
          actionLabel="Book Session"
          onAction={() => navigate('/mentor-sessions/new')}
        />
      )}
    </div>
  )
}

export default MentorSessionsList

