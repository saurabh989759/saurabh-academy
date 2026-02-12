import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import StudentsList from '../StudentsList'
import { useStudentsPaged, useDeleteStudent } from '../../hooks/useStudents'

vi.mock('../../hooks/useStudents')

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

describe('StudentsList Page', () => {
  const mockDeleteStudent = {
    mutateAsync: vi.fn(),
    isPending: false,
  }

  beforeEach(() => {
    vi.clearAllMocks()
    ;(useDeleteStudent as any).mockReturnValue(mockDeleteStudent)
  })

  it('should render loading state', () => {
    ;(useStudentsPaged as any).mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    })

    render(<StudentsList />, { wrapper: createWrapper() })

    expect(screen.getByText(/loading students/i)).toBeInTheDocument()
  })

  it('should render error state', () => {
    const mockError = new Error('Failed to load students')
    ;(useStudentsPaged as any).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: mockError,
      refetch: vi.fn(),
    })

    render(<StudentsList />, { wrapper: createWrapper() })

    expect(screen.getByText(/failed to load students/i)).toBeInTheDocument()
  })

  it('should render students list', async () => {
    const mockData = {
      content: [
        { id: 1, name: 'Student 1', email: 'student1@example.com', universityName: 'University 1' },
        { id: 2, name: 'Student 2', email: 'student2@example.com', universityName: 'University 2' },
      ],
      totalElements: 2,
      totalPages: 1,
      size: 20,
      number: 0,
      first: true,
      last: true,
    }

    ;(useStudentsPaged as any).mockReturnValue({
      data: mockData,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    })

    render(<StudentsList />, { wrapper: createWrapper() })

    expect(screen.getByText('Students')).toBeInTheDocument()
    expect(screen.getByText('Student 1')).toBeInTheDocument()
    expect(screen.getByText('Student 2')).toBeInTheDocument()
    expect(screen.getByText('student1@example.com')).toBeInTheDocument()
    expect(screen.getByText('student2@example.com')).toBeInTheDocument()
  })

  it('should render empty state when no students', () => {
    const mockData = {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 20,
      number: 0,
      first: true,
      last: true,
    }

    ;(useStudentsPaged as any).mockReturnValue({
      data: mockData,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    })

    render(<StudentsList />, { wrapper: createWrapper() })

    expect(screen.getByText(/no students found/i)).toBeInTheDocument()
  })

  it('should handle delete student', async () => {
    const user = userEvent.setup()
    window.confirm = vi.fn(() => true)
    mockDeleteStudent.mutateAsync.mockResolvedValue(undefined)

    const mockData = {
      content: [{ id: 1, name: 'Student 1', email: 'student1@example.com' }],
      totalElements: 1,
      totalPages: 1,
      size: 20,
      number: 0,
      first: true,
      last: true,
    }

    ;(useStudentsPaged as any).mockReturnValue({
      data: mockData,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    })

    render(<StudentsList />, { wrapper: createWrapper() })

    const deleteButton = screen.getByText('Delete')
    await user.click(deleteButton)

    await waitFor(() => {
      expect(window.confirm).toHaveBeenCalled()
      expect(mockDeleteStudent.mutateAsync).toHaveBeenCalledWith(1)
    })
  })

  it('should handle pagination', async () => {
    const user = userEvent.setup()
    const mockRefetch = vi.fn()

    const mockData = {
      content: [{ id: 1, name: 'Student 1', email: 'student1@example.com' }],
      totalElements: 25,
      totalPages: 2,
      size: 20,
      number: 0,
      first: true,
      last: false,
    }

    ;(useStudentsPaged as any).mockReturnValue({
      data: mockData,
      isLoading: false,
      error: null,
      refetch: mockRefetch,
    })

    render(<StudentsList />, { wrapper: createWrapper() })

    expect(screen.getByText('Page 1 of 2')).toBeInTheDocument()

    const nextButton = screen.getByText('Next')
    expect(nextButton).not.toBeDisabled()

    const prevButton = screen.getByText('Previous')
    expect(prevButton).toBeDisabled()
  })
})

