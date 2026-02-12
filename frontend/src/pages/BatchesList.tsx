import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useBatchesPaged, useDeleteBatch } from '../hooks/useBatches'
import { Batch } from '../api/batches'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { EmptyState } from '../components/ui/EmptyState'
import { EyeIcon, PencilIcon, PlusIcon, TrashIcon } from '../components/icons/Icons'

const BatchesList = () => {
  const navigate = useNavigate()
  const [page, setPage] = useState(0)
  const [size] = useState(20)
  const { data, isLoading, error, refetch } = useBatchesPaged(page, size)
  const deleteBatch = useDeleteBatch() // Move hook before conditional returns

  // Debug logging
  React.useEffect(() => {
    if (data) {
      console.log('📦 Batches data received:', data)
      console.log('📦 Batches content:', data.content)
      console.log('📦 Total batches:', data.totalElements)
    }
    if (error) {
      console.error('❌ Batches error:', error)
    }
  }, [data, error])

  if (isLoading) {
    return <LoadingSpinner text="Loading batches..." />
  }

  if (error) {
    return <ErrorDisplay error={error} title="Failed to load batches" onRetry={() => refetch()} />
  }

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this batch?')) {
      try {
        await deleteBatch.mutateAsync(id)
        // List will automatically refresh due to query invalidation in the hook
      } catch (err) {
        // Error handled by toast notification
        console.error('Failed to delete batch:', err)
      }
    }
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Batches</h1>
        <Link
          to="/batches/new"
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 transition-colors"
        >
          <PlusIcon className="w-5 h-5" />
          Create Batch
        </Link>
      </div>

      {data && data.content && Array.isArray(data.content) && data.content.length > 0 ? (
        <>
          <div className="mb-4 text-sm text-gray-600 dark:text-gray-400">
            Showing {data.content.length} of {data.totalElements} batches
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.content.map((batch: Batch) => {
              if (!batch || !batch.id) {
                console.warn('⚠️ Invalid batch data:', batch)
                return null
              }
              return (
                <div
                  key={batch.id}
                  className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 border border-gray-200 dark:border-gray-700 hover:shadow-lg transition-shadow"
                >
                  <div className="mb-4">
                    <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                      {batch.name || 'Unnamed Batch'}
                    </h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      Instructor: {batch.currentInstructor || 'Not specified'}
                    </p>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      Start: {batch.startMonth ? new Date(batch.startMonth).toLocaleDateString() : '-'}
                    </p>
                    {batch.batchTypeName && (
                      <p className="text-sm text-gray-600 dark:text-gray-400">
                        Type: {batch.batchTypeName}
                      </p>
                    )}
                  </div>
                  <div className="flex space-x-2">
                    <button
                      onClick={(e) => {
                        e.preventDefault()
                        e.stopPropagation()
                        console.log('👁️ Navigating to batch:', batch.id)
                        navigate(`/batches/${batch.id}`)
                      }}
                      className="flex items-center justify-center gap-1 flex-1 text-center bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 transition-colors text-sm"
                      type="button"
                    >
                      <EyeIcon className="w-4 h-4" />
                      View
                    </button>
                    <button
                      onClick={(e) => {
                        e.preventDefault()
                        e.stopPropagation()
                        console.log('✏️ Navigating to edit batch:', batch.id)
                        navigate(`/batches/${batch.id}/edit`)
                      }}
                      className="flex items-center justify-center gap-1 flex-1 text-center bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors text-sm"
                      type="button"
                    >
                      <PencilIcon className="w-4 h-4" />
                      Edit
                    </button>
                    <button
                      onClick={(e) => {
                        e.preventDefault()
                        e.stopPropagation()
                        if (batch.id) {
                          console.log('🗑️ Deleting batch:', batch.id)
                          handleDelete(batch.id)
                        }
                      }}
                      className="flex items-center justify-center gap-1 flex-1 text-center bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      disabled={!batch.id || deleteBatch.isPending}
                      type="button"
                    >
                      <TrashIcon className="w-4 h-4" />
                      {deleteBatch.isPending ? 'Deleting...' : 'Delete'}
                    </button>
                  </div>
                </div>
              )
            })}
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
          title="No batches found"
          description={data ? 'No batches match your criteria.' : 'Get started by creating your first batch.'}
          actionLabel="Create Batch"
          onAction={() => (window.location.href = '/batches/new')}
        />
      )}
    </div>
  )
}

export default BatchesList

