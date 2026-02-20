import { useParams, Link, useNavigate } from 'react-router-dom'
import { useClass, useDeleteClass } from '../hooks/useClasses'
import { LoadingSpinner } from '../components/ui/LoadingSpinner'
import { ErrorDisplay } from '../components/ui/ErrorDisplay'
import { PencilIcon, TrashIcon } from '../components/icons/Icons'

const Field = ({ label, value }: { label: string; value: React.ReactNode }) => (
  <div className="py-3.5 border-b border-slate-50 last:border-0">
    <dt className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-0.5">{label}</dt>
    <dd className="text-sm font-medium text-slate-900">{value || '—'}</dd>
  </div>
)

const ClassDetail = () => {
  const { id } = useParams<{ id: string }>()
  const classId = id ? parseInt(id) : 0
  const { data: classItem, isLoading, error } = useClass(classId)
  const deleteClass = useDeleteClass()
  const navigate = useNavigate()

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this class?')) {
      try {
        await deleteClass.mutateAsync(classId)
        navigate('/classes')
      } catch (err) {}
    }
  }

  if (isLoading) return <LoadingSpinner text="Loading class..." />
  if (error || !classItem) return <ErrorDisplay error={error || new Error('Class not found')} title="Failed to load class" />

  return (
    <div className="max-w-2xl">
      <div className="flex justify-between items-start mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">{classItem.name}</h1>
          <p className="text-sm text-slate-500 mt-0.5">Class details</p>
        </div>
        <div className="flex gap-2">
          <Link
            to={`/classes/${classItem.id}/edit`}
            className="inline-flex items-center gap-2 bg-slate-100 text-slate-700 px-4 py-2 rounded-xl text-sm font-semibold hover:bg-slate-200 transition-colors"
          >
            <PencilIcon className="w-4 h-4" /> Edit
          </Link>
          <button
            onClick={handleDelete}
            className="inline-flex items-center gap-2 bg-red-50 text-red-600 px-4 py-2 rounded-xl text-sm font-semibold hover:bg-red-100 transition-colors"
          >
            <TrashIcon className="w-4 h-4" /> Delete
          </button>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <dl>
          <Field label="Name" value={classItem.name} />
          <Field label="Date" value={classItem.date ? new Date(classItem.date).toLocaleDateString() : null} />
          <Field label="Time" value={classItem.time} />
          <Field label="Instructor" value={classItem.instructor} />
        </dl>
      </div>
    </div>
  )
}

export default ClassDetail
