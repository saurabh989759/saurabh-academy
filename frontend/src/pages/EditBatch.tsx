import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useBatch, useUpdateBatch } from '../hooks/useBatches'
import { BatchInput } from '../api/batches'
import { useBatchTypes } from '../hooks/useBatchTypes'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const EditBatch = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const batchId = id ? parseInt(id) : 0
  const { data: batch, isLoading, error } = useBatch(batchId)
  const updateBatch = useUpdateBatch()
  const { data: batchTypes } = useBatchTypes()
  const [formData, setFormData] = useState<BatchInput>({
    name: '', startMonth: '', currentInstructor: '', batchTypeId: 1, classIds: [],
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
    } catch (error) {}
  }

  if (isLoading) return <LoadingSpinner text="Loading batch..." />
  if (error || !batch) return <ErrorDisplay error={error || new Error('Batch not found')} title="Failed to load batch" />

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Edit Batch</h1>
        <p className="text-sm text-slate-500 mt-0.5">Update batch information</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="name" className={labelClass}>Name <span className="text-red-500">*</span></label>
          <input id="name" type="text" required value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })} className={inputClass} />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="startMonth" className={labelClass}>Start Month <span className="text-red-500">*</span></label>
            <input id="startMonth" type="date" required value={formData.startMonth}
              onChange={(e) => setFormData({ ...formData, startMonth: e.target.value })} className={inputClass} />
          </div>
          <div>
            <label htmlFor="batchTypeId" className={labelClass}>Batch Type <span className="text-red-500">*</span></label>
            <select id="batchTypeId" required value={formData.batchTypeId}
              onChange={(e) => setFormData({ ...formData, batchTypeId: parseInt(e.target.value) })}
              className={inputClass}>
              <option value="">Select a batch type</option>
              {batchTypes?.map((bt) => (
                <option key={bt.id} value={bt.id}>{bt.name}</option>
              ))}
            </select>
          </div>
        </div>
        <div>
          <label htmlFor="currentInstructor" className={labelClass}>Instructor <span className="text-red-500">*</span></label>
          <input id="currentInstructor" type="text" required value={formData.currentInstructor}
            onChange={(e) => setFormData({ ...formData, currentInstructor: e.target.value })} className={inputClass} />
        </div>
        <div>
          <label htmlFor="classIds" className={labelClass}>Class IDs <span className="text-slate-400 font-normal">(comma-separated)</span></label>
          <input id="classIds" type="text" value={formData.classIds?.join(',') || ''}
            onChange={(e) => {
              const ids = e.target.value.split(',').map((id) => parseInt(id.trim())).filter((id) => !isNaN(id))
              setFormData({ ...formData, classIds: ids })
            }}
            className={inputClass} />
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={updateBatch.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {updateBatch.isPending ? 'Updating...' : 'Update Batch'}
          </button>
          <button type="button" onClick={() => navigate(`/batches/${batchId}`)}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditBatch
