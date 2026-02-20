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
      } catch (err) {
        console.error('Failed to delete student:', err)
      }
    }
  }

  if (isLoading) return <LoadingSpinner text="Loading students..." />
  if (error) return <ErrorDisplay error={error} title="Failed to load students" onRetry={() => refetch()} />

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Students</h1>
          <p className="text-sm text-slate-500 mt-0.5">Manage and view all enrolled students</p>
        </div>
        <Link
          to="/students/new"
          className="inline-flex items-center gap-2 bg-violet-600 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-violet-700 transition-colors shadow-sm"
        >
          <PlusIcon className="w-4 h-4" />
          Add Student
        </Link>
      </div>

      {data && data.content && data.content.length > 0 ? (
        <>
          <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
            <table className="min-w-full">
              <thead>
                <tr className="border-b border-slate-100">
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Name</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Email</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">University</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-50">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {data.content.map((student: Student) => (
                  <tr key={student.id} className="hover:bg-violet-50/40 transition-colors">
                    <td className="px-6 py-4 text-sm font-semibold text-slate-900">{student.name}</td>
                    <td className="px-6 py-4 text-sm text-slate-500">{student.email}</td>
                    <td className="px-6 py-4 text-sm text-slate-500">{student.universityName || '—'}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-4">
                        <Link to={`/students/${student.id}`} className="inline-flex items-center gap-1 text-violet-600 hover:text-violet-800 text-sm font-medium transition-colors">
                          <EyeIcon className="w-4 h-4" /> View
                        </Link>
                        <Link to={`/students/${student.id}/edit`} className="inline-flex items-center gap-1 text-slate-500 hover:text-slate-800 text-sm font-medium transition-colors">
                          <PencilIcon className="w-4 h-4" /> Edit
                        </Link>
                        <button
                          onClick={() => student.id && handleDelete(student.id)}
                          disabled={!student.id || deleteStudent.isPending}
                          className="inline-flex items-center gap-1 text-red-500 hover:text-red-700 text-sm font-medium transition-colors disabled:opacity-40"
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
            <div className="mt-4 flex items-center justify-between">
              <span className="text-sm text-slate-500">
                Page {data.number + 1} of {data.totalPages}
              </span>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={data.first}
                  className="px-4 py-2 text-sm font-medium bg-white border border-slate-200 rounded-xl hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                  Previous
                </button>
                <button
                  onClick={() => setPage((p) => p + 1)}
                  disabled={data.last}
                  className="px-4 py-2 text-sm font-medium bg-white border border-slate-200 rounded-xl hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </>
      ) : (
        <EmptyState
          title="No students found"
          description="Get started by adding your first student."
          actionLabel="Add Student"
          onAction={() => (window.location.href = '/students/new')}
        />
      )}
    </div>
  )
}

export default StudentsList
