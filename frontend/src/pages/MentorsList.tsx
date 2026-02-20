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
      } catch (err) {
        console.error('Failed to delete mentor:', err)
      }
    }
  }

  if (isLoading) return <LoadingSpinner text="Loading mentors..." />
  if (error) return <ErrorDisplay error={error} title="Failed to load mentors" onRetry={() => refetch()} />

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Mentors</h1>
          <p className="text-sm text-slate-500 mt-0.5">Manage mentor profiles and information</p>
        </div>
        <Link
          to="/mentors/new"
          className="inline-flex items-center gap-2 bg-violet-600 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-violet-700 transition-colors shadow-sm"
        >
          <PlusIcon className="w-4 h-4" />
          Add Mentor
        </Link>
      </div>

      {mentors && mentors.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {mentors.map((mentor: Mentor) => (
            <div key={mentor.id} className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
              <div className="h-1.5 bg-gradient-to-r from-orange-500 to-amber-600" />
              <div className="p-5">
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <span className="text-lg">🧑‍💼</span>
                  </div>
                  <div>
                    <h3 className="text-base font-bold text-slate-900">{mentor.name}</h3>
                    <p className="text-sm text-slate-500">{mentor.currentCompany || 'Not specified'}</p>
                  </div>
                </div>
                <div className="flex gap-2 pt-3 border-t border-slate-50">
                  <Link
                    to={`/mentors/${mentor.id}`}
                    className="flex-1 inline-flex items-center justify-center gap-1.5 bg-violet-600 text-white px-3 py-2 rounded-xl text-sm font-medium hover:bg-violet-700 transition-colors"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <EyeIcon className="w-4 h-4" /> View
                  </Link>
                  <Link
                    to={`/mentors/${mentor.id}/edit`}
                    className="flex-1 inline-flex items-center justify-center gap-1.5 bg-slate-100 text-slate-700 px-3 py-2 rounded-xl text-sm font-medium hover:bg-slate-200 transition-colors"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <PencilIcon className="w-4 h-4" /> Edit
                  </Link>
                  <button
                    onClick={(e) => { e.stopPropagation(); if (mentor.id) handleDelete(mentor.id) }}
                    disabled={!mentor.id || deleteMentor.isPending}
                    className="flex-1 inline-flex items-center justify-center gap-1.5 bg-red-50 text-red-600 px-3 py-2 rounded-xl text-sm font-medium hover:bg-red-100 transition-colors disabled:opacity-40"
                  >
                    <TrashIcon className="w-4 h-4" />
                    {deleteMentor.isPending ? '...' : 'Delete'}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No mentors found"
          description="Get started by adding your first mentor."
          actionLabel="Add Mentor"
          onAction={() => (window.location.href = '/mentors/new')}
        />
      )}
    </div>
  )
}

export default MentorsList
