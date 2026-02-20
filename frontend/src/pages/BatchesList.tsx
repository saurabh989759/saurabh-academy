import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Link } from 'react-router-dom'
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
  const deleteBatch = useDeleteBatch()

  React.useEffect(() => {
    if (data) {
      console.log('📦 Batches data received:', data)
      console.log('📦 Total batches:', data.totalElements)
    }
    if (error) {
      console.error('❌ Batches error:', error)
    }
  }, [data, error])

  if (isLoading) return <LoadingSpinner text="Loading batches..." />
  if (error) return <ErrorDisplay error={error} title="Failed to load batches" onRetry={() => refetch()} />

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this batch?')) {
      try {
        await deleteBatch.mutateAsync(id)
      } catch (err) {
        console.error('Failed to delete batch:', err)
      }
    }
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Batches</h1>
          <p className="text-sm text-slate-500 mt-0.5">Manage batches and instructor assignments</p>
        </div>
        <Link
          to="/batches/new"
          className="inline-flex items-center gap-2 bg-violet-600 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-violet-700 transition-colors shadow-sm"
        >
          <PlusIcon className="w-4 h-4" />
          Add Batch
        </Link>
      </div>

      {data && data.content && Array.isArray(data.content) && data.content.length > 0 ? (
        <>
          <p className="text-sm text-slate-500 mb-4">
            Showing {data.content.length} of {data.totalElements} batches
          </p>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.content.map((batch: Batch) => {
              if (!batch || !batch.id) return null
              return (
                <div key={batch.id} className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
                  <div className="h-1.5 bg-gradient-to-r from-violet-500 to-purple-600" />
                  <div className="p-5">
                    <h3 className="text-base font-bold text-slate-900 mb-3">
                      {batch.name || 'Unnamed Batch'}
                    </h3>
                    <div className="space-y-1.5 mb-4">
                      <p className="text-sm text-slate-500 flex items-center gap-1.5">
                        <span className="text-slate-300">Instructor:</span>
                        <span className="text-slate-700 font-medium">{batch.currentInstructor || 'Not specified'}</span>
                      </p>
                      <p className="text-sm text-slate-500 flex items-center gap-1.5">
                        <span className="text-slate-300">Start:</span>
                        <span className="text-slate-700 font-medium">
                          {batch.startMonth ? new Date(batch.startMonth).toLocaleDateString() : '—'}
                        </span>
                      </p>
                      {batch.batchTypeName && (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-lg bg-violet-50 text-violet-700 text-xs font-medium">
                          {batch.batchTypeName}
                        </span>
                      )}
                    </div>
                    <div className="flex gap-2 pt-3 border-t border-slate-50">
                      <button
                        onClick={(e) => { e.preventDefault(); navigate(`/batches/${batch.id}`) }}
                        className="flex-1 inline-flex items-center justify-center gap-1.5 bg-violet-600 text-white px-3 py-2 rounded-xl text-sm font-medium hover:bg-violet-700 transition-colors"
                        type="button"
                      >
                        <EyeIcon className="w-4 h-4" /> View
                      </button>
                      <button
                        onClick={(e) => { e.preventDefault(); navigate(`/batches/${batch.id}/edit`) }}
                        className="flex-1 inline-flex items-center justify-center gap-1.5 bg-slate-100 text-slate-700 px-3 py-2 rounded-xl text-sm font-medium hover:bg-slate-200 transition-colors"
                        type="button"
                      >
                        <PencilIcon className="w-4 h-4" /> Edit
                      </button>
                      <button
                        onClick={(e) => { e.preventDefault(); if (batch.id) handleDelete(batch.id) }}
                        disabled={!batch.id || deleteBatch.isPending}
                        className="flex-1 inline-flex items-center justify-center gap-1.5 bg-red-50 text-red-600 px-3 py-2 rounded-xl text-sm font-medium hover:bg-red-100 transition-colors disabled:opacity-40"
                        type="button"
                      >
                        <TrashIcon className="w-4 h-4" />
                        {deleteBatch.isPending ? '...' : 'Delete'}
                      </button>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>

          {data.totalPages > 1 && (
            <div className="mt-6 flex items-center justify-between">
              <span className="text-sm text-slate-500">Page {data.number + 1} of {data.totalPages}</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={data.first}
                  className="px-4 py-2 text-sm font-medium bg-white border border-slate-200 rounded-xl hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  Previous
                </button>
                <button
                  onClick={() => setPage((p) => p + 1)}
                  disabled={data.last}
                  className="px-4 py-2 text-sm font-medium bg-white border border-slate-200 rounded-xl hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </>
      ) : (
        <EmptyState
          title="No batches found"
          description={data ? 'No batches match your criteria.' : 'Get started by creating your first batch.'}
          actionLabel="Add Batch"
          onAction={() => (window.location.href = '/batches/new')}
        />
      )}
    </div>
  )
}

export default BatchesList
