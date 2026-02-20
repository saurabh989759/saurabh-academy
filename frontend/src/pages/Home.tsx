import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const cards = [
  {
    to: '/students',
    label: 'Students',
    description: 'View and manage student records, enrollments, and information',
    icon: '👩‍🎓',
    color: 'from-violet-500 to-purple-600',
    bg: 'bg-violet-50',
    text: 'text-violet-700',
  },
  {
    to: '/batches',
    label: 'Batches',
    description: 'Manage batches, classes, and instructor assignments',
    icon: '📦',
    color: 'from-blue-500 to-indigo-600',
    bg: 'bg-blue-50',
    text: 'text-blue-700',
  },
  {
    to: '/classes',
    label: 'Classes',
    description: 'Manage class schedules, dates, and instructors',
    icon: '📚',
    color: 'from-emerald-500 to-teal-600',
    bg: 'bg-emerald-50',
    text: 'text-emerald-700',
  },
  {
    to: '/mentors',
    label: 'Mentors',
    description: 'View and manage mentor profiles and information',
    icon: '🧑‍💼',
    color: 'from-orange-500 to-amber-600',
    bg: 'bg-orange-50',
    text: 'text-orange-700',
  },
  {
    to: '/mentor-sessions',
    label: 'Mentor Sessions',
    description: 'Book and manage mentor sessions with students',
    icon: '🗓️',
    color: 'from-rose-500 to-pink-600',
    bg: 'bg-rose-50',
    text: 'text-rose-700',
  },
]

const Home = () => {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-slate-900 mb-3">Academy Management System</h1>
          <p className="text-slate-500 mb-8">Please sign in to access the system</p>
          <Link
            to="/login"
            className="inline-block bg-violet-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-violet-700 transition-colors"
          >
            Go to Login
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-slate-900">Dashboard</h1>
        <p className="text-slate-500 mt-1">Welcome back! Here's an overview of your academy.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {cards.map((card) => (
          <Link
            key={card.to}
            to={card.to}
            className="group block bg-white rounded-2xl border border-slate-100 shadow-sm hover:shadow-md transition-all duration-200 overflow-hidden"
          >
            <div className={`h-2 bg-gradient-to-r ${card.color}`} />
            <div className="p-6">
              <div className={`inline-flex items-center justify-center w-12 h-12 ${card.bg} rounded-xl mb-4`}>
                <span className="text-2xl">{card.icon}</span>
              </div>
              <h2 className="text-lg font-bold text-slate-900 mb-1 group-hover:text-violet-600 transition-colors">
                {card.label}
              </h2>
              <p className="text-sm text-slate-500">{card.description}</p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}

export default Home
