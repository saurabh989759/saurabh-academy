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
  const deleteSession = useDeleteMentorSession()

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
      } catch (err) {
        console.error('Failed to delete mentor session:', err)
      }
    }
  }

  if (isLoading) return <LoadingSpinner text="Loading mentor sessions..." />
  if (error) return <ErrorDisplay error={error} title="Failed to load mentor sessions" onRetry={() => refetch()} />

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Mentor Sessions</h1>
          <p className="text-sm text-slate-500 mt-0.5">Track and manage all mentor-student sessions</p>
        </div>
        <button
          onClick={() => navigate('/mentor-sessions/new')}
          className="inline-flex items-center gap-2 bg-violet-600 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-violet-700 transition-colors shadow-sm"
          type="button"
        >
          <PlusIcon className="w-4 h-4" />
          Book Session
        </button>
      </div>

      {sessions && sessions.length > 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <table className="min-w-full">
            <thead>
              <tr className="border-b border-slate-100">
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Time</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Duration</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Student ID</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Mentor ID</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {sessions.map((session: MentorSession) => (
                <tr key={session.id} className="hover:bg-violet-50/40 transition-colors">
                  <td className="px-6 py-4 text-sm font-semibold text-slate-900">
                    {session.time ? new Date(session.time).toLocaleString() : '—'}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">
                    {session.durationMinutes ? (
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-lg bg-violet-50 text-violet-700 text-xs font-medium">
                        {session.durationMinutes} min
                      </span>
                    ) : '—'}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">{session.studentId || '—'}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{session.mentorId || '—'}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-4">
                      <button
                        onClick={() => session.id && navigate(`/mentor-sessions/${session.id}`)}
                        className="inline-flex items-center gap-1 text-violet-600 hover:text-violet-800 text-sm font-medium transition-colors"
                        type="button"
                      >
                        <EyeIcon className="w-4 h-4" /> View
                      </button>
                      <button
                        onClick={() => session.id && navigate(`/mentor-sessions/${session.id}/edit`)}
                        className="inline-flex items-center gap-1 text-slate-500 hover:text-slate-800 text-sm font-medium transition-colors"
                        type="button"
                      >
                        <PencilIcon className="w-4 h-4" /> Edit
                      </button>
                      <button
                        onClick={() => session.id && handleDelete(session.id)}
                        disabled={!session.id || deleteSession.isPending}
                        className="inline-flex items-center gap-1 text-red-500 hover:text-red-700 text-sm font-medium transition-colors disabled:opacity-40"
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
