import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { renderHook, act, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { AuthProvider, useAuth } from '../AuthContext'
import { authApi } from '../../api/auth'

vi.mock('../../api/auth')
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => vi.fn(),
  }
})

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  })
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProvider>{children}</AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  )
}

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
  })

  it('should initialize with token from localStorage', () => {
    localStorage.setItem('jwt_token', 'test-token')

    const { result } = renderHook(() => useAuth(), {
      wrapper: createWrapper(),
    })

    expect(result.current.isAuthenticated).toBe(true)
    expect(result.current.token).toBe('test-token')
  })

  it('should initialize without token when localStorage is empty', () => {
    const { result } = renderHook(() => useAuth(), {
      wrapper: createWrapper(),
    })

    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.token).toBeNull()
  })

  it('should login successfully and save token', async () => {
    const mockResponse = {
      token: 'new-jwt-token',
      type: 'Bearer',
    }

    ;(authApi.login as any).mockResolvedValue(mockResponse)

    const { result } = renderHook(() => useAuth(), {
      wrapper: createWrapper(),
    })

    await act(async () => {
      await result.current.login({
        username: 'admin@academy.com',
        password: 'password123',
      })
    })

    await waitFor(() => {
      expect(result.current.isAuthenticated).toBe(true)
      expect(result.current.token).toBe('new-jwt-token')
      expect(localStorage.getItem('jwt_token')).toBe('new-jwt-token')
    })
  })

  it('should logout and clear token', async () => {
    localStorage.setItem('jwt_token', 'test-token')

    const { result } = renderHook(() => useAuth(), {
      wrapper: createWrapper(),
    })

    expect(result.current.isAuthenticated).toBe(true)

    act(() => {
      result.current.logout()
    })

    await waitFor(() => {
      expect(result.current.isAuthenticated).toBe(false)
      expect(result.current.token).toBeNull()
      expect(localStorage.getItem('jwt_token')).toBeNull()
    })
  })

  it('should handle login error', async () => {
    const mockError = new Error('Invalid credentials')
    ;(authApi.login as any).mockRejectedValue(mockError)

    const { result } = renderHook(() => useAuth(), {
      wrapper: createWrapper(),
    })

    await act(async () => {
      try {
        await result.current.login({
          username: 'wrong@example.com',
          password: 'wrong',
        })
      } catch (error) {
        // Expected to throw
      }
    })

    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.token).toBeNull()
  })
})

