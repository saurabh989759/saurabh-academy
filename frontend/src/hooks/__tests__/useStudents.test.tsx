import React from 'react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useStudents, useStudentsPaged, useStudent, useCreateStudent, useUpdateStudent, useDeleteStudent } from '../useStudents'
import { studentsApi } from '../../api/students'
import { useToast } from '../useToast'

vi.mock('../../api/students')
vi.mock('../useToast')

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  })
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  )
}

describe('useStudents hooks', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    ;(useToast as any).mockReturnValue({
      toast: vi.fn(),
    })
  })

  describe('useStudents', () => {
    it('should fetch all students', async () => {
      const mockStudents = [
        { id: 1, name: 'Student 1', email: 'student1@example.com' },
      ]

      ;(studentsApi.getAll as any).mockResolvedValue(mockStudents)

      const { result } = renderHook(() => useStudents(), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockStudents)
      expect(studentsApi.getAll).toHaveBeenCalledWith(undefined)
    })

    it('should fetch students filtered by batchId', async () => {
      const mockStudents = [{ id: 1, name: 'Student 1' }]

      ;(studentsApi.getAll as any).mockResolvedValue(mockStudents)

      const { result } = renderHook(() => useStudents(1), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(studentsApi.getAll).toHaveBeenCalledWith(1)
    })
  })

  describe('useStudentsPaged', () => {
    it('should fetch paginated students', async () => {
      const mockPage = {
        content: [{ id: 1, name: 'Student 1' }],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true,
      }

      ;(studentsApi.getAllPaged as any).mockResolvedValue(mockPage)

      const { result } = renderHook(() => useStudentsPaged(0, 20), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockPage)
      expect(studentsApi.getAllPaged).toHaveBeenCalledWith(0, 20, undefined)
    })
  })

  describe('useStudent', () => {
    it('should fetch student by id', async () => {
      const mockStudent = { id: 1, name: 'Student 1', email: 'student1@example.com' }

      ;(studentsApi.getById as any).mockResolvedValue(mockStudent)

      const { result } = renderHook(() => useStudent(1), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockStudent)
      expect(studentsApi.getById).toHaveBeenCalledWith(1)
    })

    it('should not fetch when id is 0', () => {
      const { result } = renderHook(() => useStudent(0), {
        wrapper: createWrapper(),
      })

      expect(result.current.isFetching).toBe(false)
      expect(studentsApi.getById).not.toHaveBeenCalled()
    })
  })

  describe('useCreateStudent', () => {
    it('should create a student and invalidate queries', async () => {
      const mockStudent = { id: 1, name: 'New Student', email: 'new@example.com' }
      const studentInput = { name: 'New Student', email: 'new@example.com' }

      ;(studentsApi.create as any).mockResolvedValue(mockStudent)

      const { result } = renderHook(() => useCreateStudent(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(studentInput)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(studentsApi.create).toHaveBeenCalledWith(studentInput)
    })
  })

  describe('useUpdateStudent', () => {
    it('should update a student and invalidate queries', async () => {
      const mockUpdated = { id: 1, name: 'Updated Student' }
      const studentInput = { name: 'Updated Student', email: 'updated@example.com' }

      ;(studentsApi.update as any).mockResolvedValue(mockUpdated)

      const { result } = renderHook(() => useUpdateStudent(), {
        wrapper: createWrapper(),
      })

      result.current.mutate({ id: 1, student: studentInput })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(studentsApi.update).toHaveBeenCalledWith(1, studentInput)
    })
  })

  describe('useDeleteStudent', () => {
    it('should delete a student and invalidate queries', async () => {
      ;(studentsApi.delete as any).mockResolvedValue(undefined)

      const { result } = renderHook(() => useDeleteStudent(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(1)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(studentsApi.delete).toHaveBeenCalledWith(1)
    })
  })
})

