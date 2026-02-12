import { useParams, Link, useNavigate } from 'react-router-dom'
import { useBatch, useDeleteBatch } from '../hooks/useBatches'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { PencilIcon, TrashIcon } from '../components/icons/Icons'

const BatchDetail = () => {
  const { id } = useParams<{ id: string }>()
  const batchId = id ? parseInt(id) : 0
  const { data: batch, isLoading, error } = useBatch(batchId)
  const deleteBatch = useDeleteBatch()
  const navigate = useNavigate()

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this batch?')) {
      try {
        await deleteBatch.mutateAsync(batchId)
        navigate('/batches')
      } catch (err) {
        // Error handled by toast notification
      }
    }
  }

  if (isLoading) {
    return <LoadingSpinner text="Loading batch..." />
  }

  if (error || !batch) {
    return <ErrorDisplay error={error || new Error('Batch not found')} title="Failed to load batch" />
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Batch Details</h1>
        <div className="space-x-2">
          <Link
            to={`/batches/${batch.id}/edit`}
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
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">{batch.name}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Start Month</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {batch.startMonth ? new Date(batch.startMonth).toLocaleDateString() : '-'}
            </dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Instructor</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">{batch.currentInstructor}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Batch Type</dt>
            <dd className="mt-1 text-sm text-gray-900 dark:text-white">
              {batch.batchTypeName || `Type ${batch.batchTypeId}`}
            </dd>
          </div>
          {batch.classIds && batch.classIds.length > 0 && (
            <div>
              <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Class IDs</dt>
              <dd className="mt-1 text-sm text-gray-900 dark:text-white">{batch.classIds.join(', ')}</dd>
            </div>
          )}
        </dl>
      </div>
    </div>
  )
}

export default BatchDetail

