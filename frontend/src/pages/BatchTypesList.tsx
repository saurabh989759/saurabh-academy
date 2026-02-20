import { Link } from 'react-router-dom'
import { useBatchTypes, useDeleteBatchType } from '../hooks/useBatchTypes'
import { BatchType } from '../api/batchTypes'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { EmptyState } from '../components/ui/EmptyState'
import { EyeIcon, PencilIcon, PlusIcon, TrashIcon } from '../components/icons/Icons'

const BatchTypesList = () => {
  const { data: batchTypes, isLoading, error, refetch } = useBatchTypes()
  const deleteBatchType = useDeleteBatchType()

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this batch type?')) {
      try {
        await deleteBatchType.mutateAsync(id)
      } catch (err) {
        console.error('Failed to delete batch type:', err)
      }
    }
  }

  if (isLoading) return <LoadingSpinner text="Loading batch types..." />
  if (error) return <ErrorDisplay error={error} title="Failed to load batch types" onRetry={() => refetch()} />

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Batch Types</h1>
          <p className="text-sm text-slate-500 mt-0.5">Manage program and course categories</p>
        </div>
        <Link
          to="/batch-types/new"
          className="inline-flex items-center gap-2 bg-violet-600 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-violet-700 transition-colors shadow-sm"
        >
          <PlusIcon className="w-4 h-4" />
          Add Batch Type
        </Link>
      </div>

      {batchTypes && batchTypes.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {batchTypes.map((batchType: BatchType) => (
            <div key={batchType.id} className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
              <div className="h-1.5 bg-gradient-to-r from-teal-500 to-cyan-600" />
              <div className="p-5">
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-10 h-10 bg-teal-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <svg className="w-5 h-5 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                    </svg>
                  </div>
                  <div>
                    <h3 className="text-base font-bold text-slate-900">{batchType.name}</h3>
                    <p className="text-xs text-slate-400">ID: {batchType.id}</p>
                  </div>
                </div>
                <div className="flex gap-2 pt-3 border-t border-slate-50">
                  <Link
                    to={`/batch-types/${batchType.id}`}
                    className="flex-1 inline-flex items-center justify-center gap-1.5 bg-violet-600 text-white px-3 py-2 rounded-xl text-sm font-medium hover:bg-violet-700 transition-colors"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <EyeIcon className="w-4 h-4" /> View
                  </Link>
                  <Link
                    to={`/batch-types/${batchType.id}/edit`}
                    className="flex-1 inline-flex items-center justify-center gap-1.5 bg-slate-100 text-slate-700 px-3 py-2 rounded-xl text-sm font-medium hover:bg-slate-200 transition-colors"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <PencilIcon className="w-4 h-4" /> Edit
                  </Link>
                  <button
                    onClick={(e) => { e.stopPropagation(); if (batchType.id) handleDelete(batchType.id) }}
                    disabled={!batchType.id || deleteBatchType.isPending}
                    className="flex-1 inline-flex items-center justify-center gap-1.5 bg-red-50 text-red-600 px-3 py-2 rounded-xl text-sm font-medium hover:bg-red-100 transition-colors disabled:opacity-40"
                  >
                    <TrashIcon className="w-4 h-4" />
                    {deleteBatchType.isPending ? '...' : 'Delete'}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No batch types found"
          description="Get started by adding your first batch type."
          actionLabel="Add Batch Type"
          onAction={() => (window.location.href = '/batch-types/new')}
        />
      )}
    </div>
  )
}

export default BatchTypesList
