import { Link } from 'react-router-dom'
import { useClasses, useDeleteClass } from '../hooks/useClasses'
import { Class } from '../api/classes'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { EmptyState } from '../components/ui/EmptyState'
import { EyeIcon, PencilIcon, PlusIcon, TrashIcon } from '../components/icons/Icons'

const ClassesList = () => {
  const { data: classes, isLoading, error, refetch } = useClasses()
  const deleteClass = useDeleteClass()

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this class?')) {
      try {
        await deleteClass.mutateAsync(id)
      } catch (err) {
        console.error('Failed to delete class:', err)
      }
    }
  }

  if (isLoading) return <LoadingSpinner text="Loading classes..." />
  if (error) return <ErrorDisplay error={error} title="Failed to load classes" onRetry={() => refetch()} />

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Classes</h1>
          <p className="text-sm text-slate-500 mt-0.5">Manage class schedules and instructors</p>
        </div>
        <Link
          to="/classes/new"
          className="inline-flex items-center gap-2 bg-violet-600 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-violet-700 transition-colors shadow-sm"
        >
          <PlusIcon className="w-4 h-4" />
          Add Class
        </Link>
      </div>

      {classes && classes.length > 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <table className="min-w-full">
            <thead>
              <tr className="border-b border-slate-100">
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Name</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Date</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Time</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Instructor</th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {classes.map((classItem: Class) => (
                <tr key={classItem.id} className="hover:bg-violet-50/40 transition-colors">
                  <td className="px-6 py-4 text-sm font-semibold text-slate-900">{classItem.name}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">
                    {classItem.date ? new Date(classItem.date).toLocaleDateString() : '—'}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">{classItem.time || '—'}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{classItem.instructor || '—'}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-4">
                      <Link to={`/classes/${classItem.id}`} className="inline-flex items-center gap-1 text-violet-600 hover:text-violet-800 text-sm font-medium transition-colors">
                        <EyeIcon className="w-4 h-4" /> View
                      </Link>
                      <Link to={`/classes/${classItem.id}/edit`} className="inline-flex items-center gap-1 text-slate-500 hover:text-slate-800 text-sm font-medium transition-colors">
                        <PencilIcon className="w-4 h-4" /> Edit
                      </Link>
                      <button
                        onClick={() => classItem.id && handleDelete(classItem.id)}
                        disabled={!classItem.id || deleteClass.isPending}
                        className="inline-flex items-center gap-1 text-red-500 hover:text-red-700 text-sm font-medium transition-colors disabled:opacity-40"
                      >
                        <TrashIcon className="w-4 h-4" />
                        {deleteClass.isPending ? 'Deleting...' : 'Delete'}
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
          title="No classes found"
          description="Get started by creating your first class."
          actionLabel="Add Class"
          onAction={() => (window.location.href = '/classes/new')}
        />
      )}
    </div>
  )
}

export default ClassesList
