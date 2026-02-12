import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const Home = () => {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return (
      <div className="text-center py-12">
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
          Welcome to Academy Management System
        </h1>
        <p className="text-gray-600 dark:text-gray-400 mb-8">
          Please login to access the system
        </p>
        <Link
          to="/login"
          className="inline-block bg-primary-600 text-white px-6 py-3 rounded-md hover:bg-primary-700 transition-colors"
        >
          Go to Login
        </Link>
      </div>
    )
  }

  return (
    <div className="max-w-6xl mx-auto">
      <div className="text-center py-12">
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
          Welcome to Academy Management System
        </h1>
        <p className="text-gray-600 dark:text-gray-400 mb-8">
          Manage students, batches, and classes efficiently
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-8">
        <Link
          to="/students"
          className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-all duration-200 border border-gray-200 dark:border-gray-700 cursor-pointer transform hover:scale-105"
        >
          <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Students</h2>
          <p className="text-gray-600 dark:text-gray-400">
            View and manage student records, enrollments, and information
          </p>
        </Link>

        <Link
          to="/batches"
          className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-all duration-200 border border-gray-200 dark:border-gray-700 cursor-pointer transform hover:scale-105"
        >
          <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Batches</h2>
          <p className="text-gray-600 dark:text-gray-400">
            Manage batches, classes, and instructor assignments
          </p>
        </Link>

        <Link
          to="/classes"
          className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-all duration-200 border border-gray-200 dark:border-gray-700 cursor-pointer transform hover:scale-105"
        >
          <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Classes</h2>
          <p className="text-gray-600 dark:text-gray-400">
            Manage class schedules, dates, and instructors
          </p>
        </Link>

        <Link
          to="/mentors"
          className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-all duration-200 border border-gray-200 dark:border-gray-700 cursor-pointer transform hover:scale-105"
        >
          <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Mentors</h2>
          <p className="text-gray-600 dark:text-gray-400">
            View and manage mentor profiles and information
          </p>
        </Link>

        <Link
          to="/mentor-sessions"
          className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-all duration-200 border border-gray-200 dark:border-gray-700 cursor-pointer transform hover:scale-105"
        >
          <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Mentor Sessions</h2>
          <p className="text-gray-600 dark:text-gray-400">
            Book and manage mentor sessions with students
          </p>
        </Link>
      </div>
    </div>
  )
}

export default Home

