import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useStudentsPaged, useDeleteStudent } from '../hooks/useStudents'
import { Student } from '../api/students'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { EmptyState } from '../components/ui/EmptyState'
import { EyeIcon, PencilIcon, PlusIcon, TrashIcon } from '../components/icons/Icons'

const StudentsList = () => {
  const [page, setPage] = useState(0)
  const [size] = useState(20)
  const { data, isLoading, error, refetch } = useStudentsPaged(page, size)
  const deleteStudent = useDeleteStudent()

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      try {
        await deleteStudent.mutateAsync(id)
        // List will automatically refresh due to query invalidation in the hook
      } catch (err) {
        // Error handled by toast notification
        console.error('Failed to delete student:', err)
      }
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading students..." />
  }

  if (error) {
    return <ErrorDisplay error={error} title="Failed to load students" onRetry={() => refetch()} />
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Students</h1>
        <Link
          to="/students/new"
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 transition-colors"
        >
          <PlusIcon className="w-5 h-5" />
          Create Student
        </Link>
      </div>

      {data && data.content && data.content.length > 0 ? (
        <>
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
              <thead className="bg-gray-50 dark:bg-gray-700">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Name
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Email
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    University
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                {data.content.map((student: Student) => (
                  <tr key={student.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                      {student.name}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                      {student.email}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                      {student.universityName || '-'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center gap-3">
                        <Link
                          to={`/students/${student.id}`}
                          className="flex items-center gap-1 text-primary-600 hover:text-primary-900 dark:text-primary-400 transition-colors"
                          title="View"
                        >
                          <EyeIcon className="w-4 h-4" />
                          View
                        </Link>
                        <Link
                          to={`/students/${student.id}/edit`}
                          className="flex items-center gap-1 text-blue-600 hover:text-blue-900 dark:text-blue-400 transition-colors"
                          title="Edit"
                        >
                          <PencilIcon className="w-4 h-4" />
                          Edit
                        </Link>
                        <button
                          onClick={() => student.id && handleDelete(student.id)}
                          className="flex items-center gap-1 text-red-600 hover:text-red-900 dark:text-red-400 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                          disabled={!student.id || deleteStudent.isPending}
                          title="Delete"
                        >
                          <TrashIcon className="w-4 h-4" />
                          {deleteStudent.isPending ? 'Deleting...' : 'Delete'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {data.totalPages > 1 && (
            <div className="mt-4 flex justify-center space-x-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={data.first}
                className="px-4 py-2 border rounded-md disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Previous
              </button>
              <span className="px-4 py-2">
                Page {data.number + 1} of {data.totalPages}
              </span>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={data.last}
                className="px-4 py-2 border rounded-md disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Next
              </button>
            </div>
          )}
        </>
      ) : (
        <EmptyState
          title="No students found"
          description="Get started by creating your first student."
          actionLabel="Create Student"
          onAction={() => (window.location.href = '/students/new')}
        />
      )}
    </div>
  )
}

export default StudentsList

