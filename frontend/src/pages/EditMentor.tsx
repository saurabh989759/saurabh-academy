import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useMentor, useUpdateMentor } from '../hooks/useMentors'
import { MentorInput } from '../api/mentors'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const EditMentor = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const mentorId = id ? parseInt(id) : 0
  const { data: mentor, isLoading, error } = useMentor(mentorId)
  const updateMentor = useUpdateMentor()
  const [formData, setFormData] = useState<MentorInput>({ name: '', currentCompany: '' })

  useEffect(() => {
    if (mentor) {
      setFormData({ name: mentor.name || '', currentCompany: mentor.currentCompany || '' })
    }
  }, [mentor])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await updateMentor.mutateAsync({ id: mentorId, mentor: formData })
      navigate(`/mentors/${mentorId}`)
    } catch (error) {}
  }

  if (isLoading) return <LoadingSpinner text="Loading mentor..." />
  if (error || !mentor) return <ErrorDisplay error={error || new Error('Mentor not found')} title="Failed to load mentor" />

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Edit Mentor</h1>
        <p className="text-sm text-slate-500 mt-0.5">Update mentor information</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="name" className={labelClass}>Name <span className="text-red-500">*</span></label>
          <input id="name" type="text" required value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })} className={inputClass} />
        </div>
        <div>
          <label htmlFor="currentCompany" className={labelClass}>Current Company</label>
          <input id="currentCompany" type="text" value={formData.currentCompany}
            onChange={(e) => setFormData({ ...formData, currentCompany: e.target.value })} className={inputClass} />
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={updateMentor.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {updateMentor.isPending ? 'Updating...' : 'Update Mentor'}
          </button>
          <button type="button" onClick={() => navigate(`/mentors/${mentorId}`)}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditMentor
