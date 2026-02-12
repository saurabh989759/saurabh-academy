import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useBatch, useUpdateBatch } from '../hooks/useBatches'
import { BatchInput } from '../api/batches'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const EditBatch = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const batchId = id ? parseInt(id) : 0
  const { data: batch, isLoading, error } = useBatch(batchId)
  const updateBatch = useUpdateBatch()
  const [formData, setFormData] = useState<BatchInput>({
    name: '',
    startMonth: '',
    currentInstructor: '',
    batchTypeId: 1,
    classIds: [],
  })

  useEffect(() => {
    if (batch) {
      setFormData({
        name: batch.name || '',
        startMonth: batch.startMonth ? batch.startMonth.split('T')[0] : '',
        currentInstructor: batch.currentInstructor || '',
        batchTypeId: batch.batchTypeId || 1,
        classIds: batch.classIds || [],
      })
    }
  }, [batch])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await updateBatch.mutateAsync({ id: batchId, batch: formData })
      navigate(`/batches/${batchId}`)
    } catch (error) {
      // Error handling is done in the mutation
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading batch..." />
  }

  if (error || !batch) {
    return <ErrorDisplay error={error || new Error('Batch not found')} title="Failed to load batch" />
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">Edit Batch</h1>
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
          <label htmlFor="startMonth" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Start Month *
          </label>
          <input
            id="startMonth"
            type="date"
            required
            value={formData.startMonth}
            onChange={(e) => setFormData({ ...formData, startMonth: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="currentInstructor" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Instructor *
          </label>
          <input
            id="currentInstructor"
            type="text"
            required
            value={formData.currentInstructor}
            onChange={(e) => setFormData({ ...formData, currentInstructor: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="batchTypeId" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Batch Type ID *
          </label>
          <input
            id="batchTypeId"
            type="number"
            required
            min="1"
            value={formData.batchTypeId}
            onChange={(e) => setFormData({ ...formData, batchTypeId: parseInt(e.target.value) })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div>
          <label htmlFor="classIds" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Class IDs (comma-separated)
          </label>
          <input
            id="classIds"
            type="text"
            value={formData.classIds?.join(',') || ''}
            onChange={(e) => {
              const ids = e.target.value
                .split(',')
                .map((id) => parseInt(id.trim()))
                .filter((id) => !isNaN(id))
              setFormData({ ...formData, classIds: ids })
            }}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />
        </div>
        <div className="flex space-x-4 pt-4">
          <button
            type="submit"
            disabled={updateBatch.isPending}
            className="flex-1 bg-primary-600 text-white py-2 px-4 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {updateBatch.isPending ? 'Updating...' : 'Update Batch'}
          </button>
          <button
            type="button"
            onClick={() => navigate(`/batches/${batchId}`)}
            className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 dark:bg-gray-700 dark:text-white"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditBatch

