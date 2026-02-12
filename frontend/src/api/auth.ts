import axios from 'axios'
import type { LoginRequest, AuthResponse, TokenValidationResponse } from '../generated'

export type { LoginRequest, AuthResponse, TokenValidationResponse }

// Use relative URL when running behind nginx proxy, or absolute URL for direct backend access
// VITE_API_URL is set to 'http://localhost/api' in Docker, so we need to handle it properly
const getApiUrl = () => {
  const envUrl = import.meta.env.VITE_API_URL
  if (envUrl) {
    // If VITE_API_URL is set, use it (but remove /api suffix if present since we add it)
    return envUrl.replace(/\/api\/?$/, '')
  }
  // For development, check if we're on localhost (nginx proxy) or direct backend
  if (typeof window !== 'undefined' && window.location.origin === 'http://localhost') {
    return '' // Use relative URLs for nginx proxy
  }
  return 'http://localhost:8080' // Direct backend access
}

const API_URL = getApiUrl()

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    try {
      // Make direct API call using axios
      // If API_URL is empty, use relative path (for nginx proxy)
      // Otherwise use absolute URL
      const loginUrl = API_URL ? `${API_URL}/api/auth/login` : '/api/auth/login'
      const response = await axios.post<AuthResponse>(
        loginUrl,
        credentials,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      )
      
      if (response.data && response.data.token) {
        return response.data
      }
      
      throw new Error('Invalid response from server: missing token')
    } catch (error: any) {
      console.error('Login API error:', error)
      if (error.response) {
        // Server responded with error
        const errorMessage = error.response.data?.detail || 
                           error.response.data?.message ||
                           `Login failed: ${error.response.status}`
        throw new Error(errorMessage)
      } else if (error.request) {
        // Request made but no response
        throw new Error('Network error: Could not reach server')
      } else {
        // Something else happened
        throw error
      }
    }
  },

  validateToken: async (token: string): Promise<TokenValidationResponse> => {
    try {
      const validateUrl = API_URL ? `${API_URL}/api/auth/validate` : '/api/auth/validate'
      const response = await axios.post<TokenValidationResponse>(
        validateUrl,
        { token },
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      )
      return response.data
    } catch (error: any) {
      console.error('Token validation error:', error)
      throw error
    }
  },
}
