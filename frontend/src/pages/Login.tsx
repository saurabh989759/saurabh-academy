import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { LoginRequest } from '../api/auth'

const Login = () => {
  const { login, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const [credentials, setCredentials] = useState<LoginRequest>({
    username: '',
    password: '',
  })
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/', { replace: true })
    }
  }, [isAuthenticated, navigate])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      await login(credentials)
    } catch (err: any) {
      console.error('Login error:', err)
      let errorMessage = 'Login failed. Please check your credentials.'
      if (err.response) {
        errorMessage = err.response.data?.detail ||
                      err.response.data?.message ||
                      `Login failed: ${err.response.status}`
      } else if (err.message) {
        errorMessage = err.message
      }
      setError(errorMessage)
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex">
      {/* Left panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-slate-900 flex-col justify-between p-12">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-violet-600 rounded-xl flex items-center justify-center">
            <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 14l9-5-9-5-9 5 9 5z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
            </svg>
          </div>
          <span className="text-white font-bold text-xl">Academy</span>
        </div>

        <div>
          <h1 className="text-4xl font-bold text-white leading-tight mb-4">
            Manage your<br />
            <span className="text-violet-400">academy</span> with ease
          </h1>
          <p className="text-slate-400 text-lg">
            Students, batches, classes, mentors — everything in one place.
          </p>
          <div className="mt-10 grid grid-cols-2 gap-4">
            {[
              { label: 'Students', icon: '👩‍🎓' },
              { label: 'Batches', icon: '📦' },
              { label: 'Classes', icon: '📚' },
              { label: 'Sessions', icon: '🗓️' },
            ].map((item) => (
              <div key={item.label} className="bg-slate-800 rounded-xl p-4 flex items-center gap-3">
                <span className="text-2xl">{item.icon}</span>
                <span className="text-slate-300 font-medium">{item.label}</span>
              </div>
            ))}
          </div>
        </div>

        <p className="text-slate-600 text-sm">© 2024 Academy Management System</p>
      </div>

      {/* Right panel */}
      <div className="flex-1 flex items-center justify-center p-8 bg-slate-50">
        <div className="w-full max-w-md">
          <div className="mb-8">
            <h2 className="text-2xl font-bold text-slate-900">Welcome back</h2>
            <p className="text-slate-500 mt-1">Sign in to your account to continue</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label htmlFor="username" className="block text-sm font-semibold text-slate-700 mb-1.5">
                Email address
              </label>
              <input
                id="username"
                type="email"
                required
                value={credentials.username}
                onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                className="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                placeholder="admin@academy.com"
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-semibold text-slate-700 mb-1.5">
                Password
              </label>
              <input
                id="password"
                type="password"
                required
                value={credentials.password}
                onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                className="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                placeholder="••••••••••"
              />
            </div>

            {error && (
              <div className="bg-red-50 border border-red-100 text-red-700 px-4 py-3 rounded-xl text-sm">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-violet-600 text-white py-2.5 px-4 rounded-xl font-semibold hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          <div className="mt-8 p-4 bg-violet-50 border border-violet-100 rounded-xl">
            <p className="text-sm font-semibold text-violet-700 mb-1">Demo Credentials</p>
            <p className="text-sm text-violet-600">Email: admin@academy.com</p>
            <p className="text-sm text-violet-600">Password: password123</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Login
