import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import Home from '../Home'
import { useAuth } from '../../context/AuthContext'

vi.mock('../../context/AuthContext')

describe('Home Page', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should redirect to login when not authenticated', () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: false,
    })

    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    )

    expect(screen.getByText(/please login/i)).toBeInTheDocument()
    expect(screen.getByText(/go to login/i)).toBeInTheDocument()
  })

  it('should render dashboard cards when authenticated', () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
    })

    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    )

    expect(screen.getByText('Welcome to Academy Management System')).toBeInTheDocument()
    expect(screen.getByText('Students')).toBeInTheDocument()
    expect(screen.getByText('Batches')).toBeInTheDocument()
    expect(screen.getByText('Classes')).toBeInTheDocument()
    expect(screen.getByText('Mentors')).toBeInTheDocument()
    expect(screen.getByText('Mentor Sessions')).toBeInTheDocument()
  })

  it('should navigate to students page when Students card is clicked', async () => {
    const user = userEvent.setup()
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
    })

    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    )

    const studentsCard = screen.getByText('Students').closest('a')
    expect(studentsCard).toHaveAttribute('href', '/students')
  })

  it('should navigate to batches page when Batches card is clicked', async () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
    })

    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    )

    const batchesCard = screen.getByText('Batches').closest('a')
    expect(batchesCard).toHaveAttribute('href', '/batches')
  })

  it('should navigate to classes page when Classes card is clicked', async () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
    })

    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    )

    const classesCard = screen.getByText('Classes').closest('a')
    expect(classesCard).toHaveAttribute('href', '/classes')
  })

  it('should navigate to mentors page when Mentors card is clicked', async () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
    })

    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    )

    const mentorsCard = screen.getByText('Mentors').closest('a')
    expect(mentorsCard).toHaveAttribute('href', '/mentors')
  })

  it('should navigate to mentor-sessions page when Mentor Sessions card is clicked', async () => {
    ;(useAuth as any).mockReturnValue({
      isAuthenticated: true,
    })

    render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    )

    const sessionsCard = screen.getByText('Mentor Sessions').closest('a')
    expect(sessionsCard).toHaveAttribute('href', '/mentor-sessions')
  })
})

