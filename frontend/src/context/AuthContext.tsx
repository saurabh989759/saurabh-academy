import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { authApi, LoginRequest } from '../api/auth'
import { useNavigate } from 'react-router-dom'

interface AuthContextType {
  isAuthenticated: boolean
  login: (credentials: LoginRequest) => Promise<void>
  logout: () => void
  token: string | null
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem('jwt_token'))
  const navigate = useNavigate()

  useEffect(() => {
    // Sync token with localStorage
    const storedToken = localStorage.getItem('jwt_token')
    if (token && token !== storedToken) {
      localStorage.setItem('jwt_token', token)
    } else if (!token && storedToken) {
      localStorage.removeItem('jwt_token')
    }
  }, [token])

  // Initialize token from localStorage on mount
  useEffect(() => {
    const storedToken = localStorage.getItem('jwt_token')
    if (storedToken && !token) {
      setToken(storedToken)
    }
  }, [])

  const login = async (credentials: LoginRequest) => {
    try {
      const response = await authApi.login(credentials)
      if (response && response.token) {
        setToken(response.token)
        localStorage.setItem('jwt_token', response.token)
        navigate('/')
      } else {
        throw new Error('Invalid response from server')
      }
    } catch (error: any) {
      console.error('Login error:', error)
      throw error
    }
  }

  const logout = () => {
    setToken(null)
    navigate('/login')
  }

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!token,
        login,
        logout,
        token,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}

