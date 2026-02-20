import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateMentorSession } from '../hooks/useMentorSessions'
import { MentorSessionInput } from '../api/mentorSessions'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all placeholder:text-slate-400"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const CreateMentorSession = () => {
  const navigate = useNavigate()
  const createSession = useCreateMentorSession()
  const [formData, setFormData] = useState<MentorSessionInput>({
    time: '', durationMinutes: 60, studentId: 1, mentorId: 1,
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const result = await createSession.mutateAsync(formData)
      navigate(`/mentor-sessions/${result.id}`)
    } catch (error) {}
  }

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Book Mentor Session</h1>
        <p className="text-sm text-slate-500 mt-0.5">Schedule a new mentor session</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="time" className={labelClass}>Session Time <span className="text-red-500">*</span></label>
          <input id="time" type="datetime-local" required value={formData.time}
            onChange={(e) => setFormData({ ...formData, time: e.target.value })} className={inputClass} />
        </div>
        <div>
          <label htmlFor="durationMinutes" className={labelClass}>Duration (minutes) <span className="text-red-500">*</span></label>
          <input id="durationMinutes" type="number" required min="15" step="15" value={formData.durationMinutes}
            onChange={(e) => setFormData({ ...formData, durationMinutes: parseInt(e.target.value) })} className={inputClass} />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="studentId" className={labelClass}>Student ID <span className="text-red-500">*</span></label>
            <input id="studentId" type="number" required min="1" value={formData.studentId}
              onChange={(e) => setFormData({ ...formData, studentId: parseInt(e.target.value) })} className={inputClass} />
          </div>
          <div>
            <label htmlFor="mentorId" className={labelClass}>Mentor ID <span className="text-red-500">*</span></label>
            <input id="mentorId" type="number" required min="1" value={formData.mentorId}
              onChange={(e) => setFormData({ ...formData, mentorId: parseInt(e.target.value) })} className={inputClass} />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="studentRating" className={labelClass}>Student Rating <span className="text-slate-400 font-normal">(optional 1–5)</span></label>
            <input id="studentRating" type="number" min="1" max="5" value={formData.studentRating || ''}
              onChange={(e) => setFormData({ ...formData, studentRating: e.target.value ? parseInt(e.target.value) : undefined })}
              className={inputClass} placeholder="1–5" />
          </div>
          <div>
            <label htmlFor="mentorRating" className={labelClass}>Mentor Rating <span className="text-slate-400 font-normal">(optional 1–5)</span></label>
            <input id="mentorRating" type="number" min="1" max="5" value={formData.mentorRating || ''}
              onChange={(e) => setFormData({ ...formData, mentorRating: e.target.value ? parseInt(e.target.value) : undefined })}
              className={inputClass} placeholder="1–5" />
          </div>
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={createSession.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {createSession.isPending ? 'Booking...' : 'Book Session'}
          </button>
          <button type="button" onClick={() => navigate('/mentor-sessions')}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateMentorSession
