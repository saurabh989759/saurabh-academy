import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateMentor } from '../hooks/useMentors'
import { MentorInput } from '../api/mentors'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all placeholder:text-slate-400"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const CreateMentor = () => {
  const navigate = useNavigate()
  const createMentor = useCreateMentor()
  const [formData, setFormData] = useState<MentorInput>({ name: '', currentCompany: '' })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const result = await createMentor.mutateAsync(formData)
      navigate(`/mentors/${result.id}`)
    } catch (error) {}
  }

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Add Mentor</h1>
        <p className="text-sm text-slate-500 mt-0.5">Fill in the details to create a new mentor</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="name" className={labelClass}>Name <span className="text-red-500">*</span></label>
          <input id="name" type="text" required value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className={inputClass} placeholder="Alice Johnson" />
        </div>
        <div>
          <label htmlFor="currentCompany" className={labelClass}>Current Company</label>
          <input id="currentCompany" type="text" value={formData.currentCompany}
            onChange={(e) => setFormData({ ...formData, currentCompany: e.target.value })}
            className={inputClass} placeholder="Tech Corp" />
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={createMentor.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {createMentor.isPending ? 'Creating...' : 'Create Mentor'}
          </button>
          <button type="button" onClick={() => navigate('/mentors')}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateMentor
