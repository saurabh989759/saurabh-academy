import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useClass, useUpdateClass } from '../hooks/useClasses'
import { ClassInput } from '../api/classes'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const EditClass = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const classId = id ? parseInt(id) : 0
  const { data: classItem, isLoading, error } = useClass(classId)
  const updateClass = useUpdateClass()
  const [formData, setFormData] = useState<ClassInput>({
    name: '',
    date: '',
    time: '',
    instructor: '',
  })

  useEffect(() => {
    if (classItem) {
      setFormData({
        name: classItem.name || '',
        date: classItem.date ? classItem.date.split('T')[0] : '',
        time: classItem.time || '',
        instructor: classItem.instructor || '',
      })
    }
  }, [classItem])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await updateClass.mutateAsync({ id: classId, classData: formData })
      navigate(`/classes/${classId}`)
    } catch (error) {
      // Error handling is done in the mutation
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading class..." />
  }

  if (error || !classItem) {
    return <ErrorDisplay error={error || new Error('Class not found')} title="Failed to load class" />
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">Edit Class</h1>
      <form onSubmit={handleSubmit} className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 space-y-4">
        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Name *
          </label>
          <input
            id="name"
            type="text"
            required
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="date" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Date *
          </label>
          <input
            id="date"
            type="date"
            required
            value={formData.date}
            onChange={(e) => setFormData({ ...formData, date: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="time" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Time *
          </label>
          <input
            id="time"
            type="time"
            required
            value={formData.time}
            onChange={(e) => setFormData({ ...formData, time: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="instructor" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Instructor *
          </label>
          <input
            id="instructor"
            type="text"
            required
            value={formData.instructor}
            onChange={(e) => setFormData({ ...formData, instructor: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div className="flex space-x-4 pt-4">
          <button
            type="submit"
            disabled={updateClass.isPending}
            className="flex-1 bg-primary-600 text-white py-2 px-4 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {updateClass.isPending ? 'Updating...' : 'Update Class'}
          </button>
          <button
            type="button"
            onClick={() => navigate(`/classes/${classId}`)}
            className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 dark:bg-gray-700 dark:text-white"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditClass

