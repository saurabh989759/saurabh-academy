import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useStudent, useUpdateStudent } from '../hooks/useStudents'
import { StudentInput } from '../api/students'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const EditStudent = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const studentId = id ? parseInt(id) : 0
  const { data: student, isLoading, error } = useStudent(studentId)
  const updateStudent = useUpdateStudent()
  const [formData, setFormData] = useState<StudentInput>({
    name: '', email: '', graduationYear: undefined, universityName: '', phoneNumber: '', batchId: undefined,
  })

  useEffect(() => {
    if (student) {
      setFormData({
        name: student.name || '', email: student.email || '',
        graduationYear: student.graduationYear, universityName: student.universityName || '',
        phoneNumber: student.phoneNumber || '', batchId: student.batchId,
      })
    }
  }, [student])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await updateStudent.mutateAsync({ id: studentId, student: formData })
      navigate(`/students/${studentId}`)
    } catch (error) {}
  }

  if (isLoading) return <LoadingSpinner text="Loading student..." />
  if (error || !student) return <ErrorDisplay error={error || new Error('Student not found')} title="Failed to load student" />

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Edit Student</h1>
        <p className="text-sm text-slate-500 mt-0.5">Update student information</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="name" className={labelClass}>Name <span className="text-red-500">*</span></label>
          <input id="name" type="text" required value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })} className={inputClass} />
        </div>
        <div>
          <label htmlFor="email" className={labelClass}>Email <span className="text-red-500">*</span></label>
          <input id="email" type="email" required value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })} className={inputClass} />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="graduationYear" className={labelClass}>Graduation Year</label>
            <input id="graduationYear" type="number" value={formData.graduationYear || ''}
              onChange={(e) => setFormData({ ...formData, graduationYear: e.target.value ? parseInt(e.target.value) : undefined })}
              className={inputClass} />
          </div>
          <div>
            <label htmlFor="batchId" className={labelClass}>Batch ID</label>
            <input id="batchId" type="number" value={formData.batchId || ''}
              onChange={(e) => setFormData({ ...formData, batchId: e.target.value ? parseInt(e.target.value) : undefined })}
              className={inputClass} />
          </div>
        </div>
        <div>
          <label htmlFor="universityName" className={labelClass}>University</label>
          <input id="universityName" type="text" value={formData.universityName}
            onChange={(e) => setFormData({ ...formData, universityName: e.target.value })} className={inputClass} />
        </div>
        <div>
          <label htmlFor="phoneNumber" className={labelClass}>Phone Number</label>
          <input id="phoneNumber" type="tel" value={formData.phoneNumber}
            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })} className={inputClass} />
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={updateStudent.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {updateStudent.isPending ? 'Updating...' : 'Update Student'}
          </button>
          <button type="button" onClick={() => navigate(`/students/${studentId}`)}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default EditStudent
