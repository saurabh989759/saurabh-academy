import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useMentorSession, useUpdateMentorSession } from '../hooks/useMentorSessions'
import { MentorSessionInput } from '../api/mentorSessions'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const EditMentorSession = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const sessionId = id ? parseInt(id) : 0
  const { data: session, isLoading, error } = useMentorSession(sessionId)
  const updateSession = useUpdateMentorSession()
  const [formData, setFormData] = useState<MentorSessionInput>({
    time: '', durationMinutes: 60, studentId: 1, mentorId: 1,
  })

  useEffect(() => {
    if (session) {
      const timeValue = session.time ? new Date(session.time).toISOString().slice(0, 16) : ''
      setFormData({
        time: timeValue,
        durationMinutes: session.durationMinutes || 60,
        studentId: session.studentId || 1,
        mentorId: session.mentorId || 1,
        studentRating: session.studentRating,
        mentorRating: session.mentorRating,
      })
    }
  }, [session])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const isoTime = formData.time ? new Date(formData.time).toISOString() : ''
      await updateSession.mutateAsync({ id: sessionId, session: { ...formData, time: isoTime } })
      navigate(`/mentor-sessions/${sessionId}`)
    } catch (error) {}
  }

  if (isLoading) return <LoadingSpinner text="Loading mentor session..." />
  if (error || !session) return <ErrorDisplay error={error || new Error('Mentor session not found')} title="Failed to load mentor session" />

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Edit Mentor Session</h1>
        <p className="text-sm text-slate-500 mt-0.5">Update session information</p>
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
              className={inputClass} />
          </div>
          <div>
            <label htmlFor="mentorRating" className={labelClass}>Mentor Rating <span className="text-slate-400 font-normal">(optional 1–5)</span></label>
            <input id="mentorRating" type="number" min="1" max="5" value={formData.mentorRating || ''}
              onChange={(e) => setFormData({ ...formData, mentorRating: e.target.value ? parseInt(e.target.value) : undefined })}
              className={inputClass} />
          </div>
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={updateSession.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {updateSession.isPending ? 'Updating...' : 'Update Session'}
          </button>
          <button type="button" onClick={() => navigate(`/mentor-sessions/${sessionId}`)}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditMentorSession
