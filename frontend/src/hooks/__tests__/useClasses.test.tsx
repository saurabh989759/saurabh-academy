import React from 'react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useClasses, useClass, useCreateClass, useUpdateClass, useDeleteClass } from '../useClasses'
import { classesApi } from '../../api/classes'
import { useToast } from '../useToast'

vi.mock('../../api/classes')
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

describe('useClasses hooks', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    ;(useToast as any).mockReturnValue({
      toast: vi.fn(),
    })
  })

  describe('useClasses', () => {
    it('should fetch all classes', async () => {
      const mockClasses = [
        { id: 1, name: 'Class 1', date: '2024-01-01', time: '10:00:00', instructor: 'Instructor 1' },
      ]

      ;(classesApi.getAll as any).mockResolvedValue(mockClasses)

      const { result } = renderHook(() => useClasses(), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockClasses)
      expect(classesApi.getAll).toHaveBeenCalled()
    })
  })

  describe('useClass', () => {
    it('should fetch class by id', async () => {
      const mockClass = { id: 1, name: 'Class 1', date: '2024-01-01', time: '10:00:00' }

      ;(classesApi.getById as any).mockResolvedValue(mockClass)

      const { result } = renderHook(() => useClass(1), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockClass)
      expect(classesApi.getById).toHaveBeenCalledWith(1)
    })
  })

  describe('useCreateClass', () => {
    it('should create a class and invalidate queries', async () => {
      const mockClass = { id: 1, name: 'New Class' }
      const classInput = {
        name: 'New Class',
        date: '2024-03-15',
        time: '10:00:00',
        instructor: 'Jane Smith',
      }

      ;(classesApi.create as any).mockResolvedValue(mockClass)

      const { result } = renderHook(() => useCreateClass(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(classInput)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(classesApi.create).toHaveBeenCalledWith(classInput)
    })
  })

  describe('useUpdateClass', () => {
    it('should update a class and invalidate queries', async () => {
      const mockUpdated = { id: 1, name: 'Updated Class' }
      const classInput = {
        name: 'Updated Class',
        date: '2024-03-20',
        time: '14:00:00',
        instructor: 'John Doe',
      }

      ;(classesApi.update as any).mockResolvedValue(mockUpdated)

      const { result } = renderHook(() => useUpdateClass(), {
        wrapper: createWrapper(),
      })

      result.current.mutate({ id: 1, classData: classInput })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(classesApi.update).toHaveBeenCalledWith(1, classInput)
    })
  })

  describe('useDeleteClass', () => {
    it('should delete a class and invalidate queries', async () => {
      ;(classesApi.delete as any).mockResolvedValue(undefined)

      const { result } = renderHook(() => useDeleteClass(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(1)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(classesApi.delete).toHaveBeenCalledWith(1)
    })
  })
})

