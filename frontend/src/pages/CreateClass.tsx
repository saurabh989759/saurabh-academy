import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateClass } from '../hooks/useClasses'
import { ClassInput } from '../api/classes'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all placeholder:text-slate-400"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const CreateClass = () => {
  const navigate = useNavigate()
  const createClass = useCreateClass()
  const [formData, setFormData] = useState<ClassInput>({ name: '', date: '', time: '', instructor: '' })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const result = await createClass.mutateAsync(formData)
      navigate(`/classes/${result.id}`)
    } catch (error) {}
  }

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Add Class</h1>
        <p className="text-sm text-slate-500 mt-0.5">Fill in the details to create a new class</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="name" className={labelClass}>Name <span className="text-red-500">*</span></label>
          <input id="name" type="text" required value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className={inputClass} placeholder="Introduction to Java" />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="date" className={labelClass}>Date <span className="text-red-500">*</span></label>
            <input id="date" type="date" required value={formData.date}
              onChange={(e) => setFormData({ ...formData, date: e.target.value })} className={inputClass} />
          </div>
          <div>
            <label htmlFor="time" className={labelClass}>Time <span className="text-red-500">*</span></label>
            <input id="time" type="time" required value={formData.time}
              onChange={(e) => setFormData({ ...formData, time: e.target.value })} className={inputClass} />
          </div>
        </div>
        <div>
          <label htmlFor="instructor" className={labelClass}>Instructor <span className="text-red-500">*</span></label>
          <input id="instructor" type="text" required value={formData.instructor}
            onChange={(e) => setFormData({ ...formData, instructor: e.target.value })}
            className={inputClass} placeholder="Jane Smith" />
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={createClass.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {createClass.isPending ? 'Creating...' : 'Create Class'}
          </button>
          <button type="button" onClick={() => navigate('/classes')}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateClass
