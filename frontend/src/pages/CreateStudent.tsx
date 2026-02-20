import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateStudent } from '../hooks/useStudents'
import { StudentInput } from '../api/students'

const inputClass = "w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all placeholder:text-slate-400"
const labelClass = "block text-sm font-semibold text-slate-700 mb-1.5"

const CreateStudent = () => {
  const navigate = useNavigate()
  const createStudent = useCreateStudent()
  const [formData, setFormData] = useState<StudentInput>({
    name: '',
    email: '',
    graduationYear: undefined,
    universityName: '',
    phoneNumber: '',
    batchId: undefined,
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (createStudent.isPending) return
    try {
      await createStudent.mutateAsync(formData)
      navigate('/students')
    } catch (error) {}
  }

  return (
    <div className="max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Add Student</h1>
        <p className="text-sm text-slate-500 mt-0.5">Fill in the details to create a new student</p>
      </div>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8 space-y-5">
        <div>
          <label htmlFor="name" className={labelClass}>Name <span className="text-red-500">*</span></label>
          <input id="name" type="text" required value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className={inputClass} placeholder="Alice Smith" />
        </div>
        <div>
          <label htmlFor="email" className={labelClass}>Email <span className="text-red-500">*</span></label>
          <input id="email" type="email" required value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            className={inputClass} placeholder="alice@example.com" />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="graduationYear" className={labelClass}>Graduation Year</label>
            <input id="graduationYear" type="number" value={formData.graduationYear || ''}
              onChange={(e) => setFormData({ ...formData, graduationYear: e.target.value ? parseInt(e.target.value) : undefined })}
              className={inputClass} placeholder="2024" />
          </div>
          <div>
            <label htmlFor="batchId" className={labelClass}>Batch ID</label>
            <input id="batchId" type="number" value={formData.batchId || ''}
              onChange={(e) => setFormData({ ...formData, batchId: e.target.value ? parseInt(e.target.value) : undefined })}
              className={inputClass} placeholder="1" />
          </div>
        </div>
        <div>
          <label htmlFor="universityName" className={labelClass}>University</label>
          <input id="universityName" type="text" value={formData.universityName}
            onChange={(e) => setFormData({ ...formData, universityName: e.target.value })}
            className={inputClass} placeholder="State University" />
        </div>
        <div>
          <label htmlFor="phoneNumber" className={labelClass}>Phone Number</label>
          <input id="phoneNumber" type="tel" value={formData.phoneNumber}
            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
            className={inputClass} placeholder="123-456-7890" />
        </div>
        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={createStudent.isPending}
            className="flex-1 bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 transition-colors disabled:opacity-50">
            {createStudent.isPending ? 'Creating...' : 'Create Student'}
          </button>
          <button type="button" onClick={() => navigate('/students')}
            className="flex-1 bg-slate-100 text-slate-700 py-2.5 px-4 rounded-xl font-semibold hover:bg-slate-200 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateStudent
