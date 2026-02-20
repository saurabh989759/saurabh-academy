import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateBatchType } from '../hooks/useBatchTypes'
import { BatchTypeInput } from '../api/batchTypes'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all placeholder:text-slate-400"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const CreateBatchType = () => {
  const navigate = useNavigate()
  const createBatchType = useCreateBatchType()
  const [formData, setFormData] = useState<BatchTypeInput>({ name: '' })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const result = await createBatchType.mutateAsync(formData)
      navigate(`/batch-types/${result.id}`)
    } catch (error) {}
  }

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Add Batch Type</h1>
        <p className="text-sm text-slate-500 mt-0.5">Fill in the details to create a new batch type</p>
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
            placeholder="Full Stack Development"
          />
        </div>
        <div className="flex gap-3 pt-2">
          <button
            type="submit"
            disabled={createBatchType.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50"
          >
            {createBatchType.isPending ? 'Creating...' : 'Create Batch Type'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/batch-types')}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateBatchType
