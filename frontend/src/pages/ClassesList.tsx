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
        // List will automatically refresh due to query invalidation in the hook
      } catch (err) {
        // Error handled by toast notification
        console.error('Failed to delete class:', err)
      }
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading classes..." />
  }

  if (error) {
    return <ErrorDisplay error={error} title="Failed to load classes" onRetry={() => refetch()} />
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Classes</h1>
        <Link
          to="/classes/new"
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 transition-colors"
        >
          <PlusIcon className="w-5 h-5" />
          Create Class
        </Link>
      </div>

      {classes && classes.length > 0 ? (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-700">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Name
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Time
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Instructor
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {classes.map((classItem: Class) => (
                <tr key={classItem.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                    {classItem.name}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {classItem.date ? new Date(classItem.date).toLocaleDateString() : '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {classItem.time || '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {classItem.instructor || '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center gap-3">
                      <Link
                        to={`/classes/${classItem.id}`}
                        className="flex items-center gap-1 text-primary-600 hover:text-primary-900 dark:text-primary-400 transition-colors"
                        title="View"
                      >
                        <EyeIcon className="w-4 h-4" />
                        View
                      </Link>
                      <Link
                        to={`/classes/${classItem.id}/edit`}
                        className="flex items-center gap-1 text-blue-600 hover:text-blue-900 dark:text-blue-400 transition-colors"
                        title="Edit"
                      >
                        <PencilIcon className="w-4 h-4" />
                        Edit
                      </Link>
                      <button
                        onClick={() => classItem.id && handleDelete(classItem.id)}
                        className="flex items-center gap-1 text-red-600 hover:text-red-900 dark:text-red-400 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        disabled={!classItem.id || deleteClass.isPending}
                        title="Delete"
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
          actionLabel="Create Class"
          onAction={() => (window.location.href = '/classes/new')}
        />
      )}
    </div>
  )
}

export default ClassesList

