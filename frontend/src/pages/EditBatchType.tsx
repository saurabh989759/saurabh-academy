import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useBatchType, useUpdateBatchType } from '../hooks/useBatchTypes'
import { BatchTypeInput } from '../api/batchTypes'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const EditBatchType = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const batchTypeId = id ? parseInt(id) : 0
  const { data: batchType, isLoading, error } = useBatchType(batchTypeId)
  const updateBatchType = useUpdateBatchType()
  const [formData, setFormData] = useState<BatchTypeInput>({ name: '' })

  useEffect(() => {
    if (batchType) {
      setFormData({ name: batchType.name || '' })
    }
  }, [batchType])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await updateBatchType.mutateAsync({ id: batchTypeId, batchType: formData })
      navigate(`/batch-types/${batchTypeId}`)
    } catch (error) {}
  }

  if (isLoading) return <LoadingSpinner text="Loading batch type..." />
  if (error || !batchType) return <ErrorDisplay error={error || new Error('Batch type not found')} title="Failed to load batch type" />

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Edit Batch Type</h1>
        <p className="text-sm text-slate-500 mt-0.5">Update batch type information</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="name" className={labelClass}>Name <span className="text-red-500">*</span></label>
          <input
            id="name"
            type="text"
            required
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className={inputClass}
          />
        </div>
        <div className="flex gap-3 pt-2">
          <button
            type="submit"
            disabled={updateBatchType.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50"
          >
            {updateBatchType.isPending ? 'Updating...' : 'Update Batch Type'}
          </button>
          <button
            type="button"
            onClick={() => navigate(`/batch-types/${batchTypeId}`)}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditBatchType
