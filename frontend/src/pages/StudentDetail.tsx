import { useParams, Link, useNavigate } from 'react-router-dom'
import { useStudent, useDeleteStudent } from '../hooks/useStudents'
import { PencilIcon, TrashIcon } from '../components/icons/Icons'

const StudentDetail = () => {
  const { id } = useParams<{ id: string }>()
  const studentId = id ? parseInt(id) : 0
  const { data: student, isLoading, error } = useStudent(studentId)
  const deleteStudent = useDeleteStudent()
  const navigate = useNavigate()

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      await deleteStudent.mutateAsync(studentId)
      navigate('/students')
    }
  }

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }

  if (error || !student) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-md">
        {error instanceof Error ? error.message : 'Student not found'}
      </div>
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Student Details</h1>
        <div className="space-x-2">
          <Link
            to={`/students/${student.id}/edit`}
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
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">{student.name}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Email</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">{student.email}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Graduation Year</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {student.graduationYear || '-'}
            </dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">University</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {student.universityName || '-'}
            </dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Phone</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {student.phoneNumber || '-'}
            </dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Batch ID</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {student.batchId || '-'}
            </dd>
          </div>
        </dl>
      </div>
    </div>
  )
}

export default StudentDetail

