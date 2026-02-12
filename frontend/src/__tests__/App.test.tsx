import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import App from '../App'
import { useAuth } from '../context/AuthContext'

vi.mock('../context/AuthContext')
vi.mock('../context/RealtimeContext', () => ({
  RealtimeProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
  useRealtime: () => ({ isConnected: false }),
}))

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  })
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{children}</BrowserRouter>
    </QueryClientProvider>
  )
}

describe('App', () => {
  it('should render navbar', () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
      logout: vi.fn(),
    })

    render(<App />, { wrapper: createWrapper() })

    expect(screen.getByText('Academy')).toBeInTheDocument()
  })

  it('should render routes', () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
      logout: vi.fn(),
    })

    render(<App />, { wrapper: createWrapper() })

    // Navbar should be present
    expect(screen.getByText('Academy')).toBeInTheDocument()
  })
})

