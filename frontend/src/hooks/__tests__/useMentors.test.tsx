import React from 'react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useMentors, useMentor, useCreateMentor, useUpdateMentor, useDeleteMentor } from '../useMentors'
import { mentorsApi } from '../../api/mentors'
import { useToast } from '../useToast'

vi.mock('../../api/mentors')
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

describe('useMentors hooks', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    ;(useToast as any).mockReturnValue({
      toast: vi.fn(),
    })
  })

  describe('useMentors', () => {
    it('should fetch all mentors', async () => {
      const mockMentors = [
        { id: 1, name: 'Mentor 1', currentCompany: 'Company 1' },
      ]

      ;(mentorsApi.getAll as any).mockResolvedValue(mockMentors)

      const { result } = renderHook(() => useMentors(), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockMentors)
      expect(mentorsApi.getAll).toHaveBeenCalled()
    })
  })

  describe('useMentor', () => {
    it('should fetch mentor by id', async () => {
      const mockMentor = { id: 1, name: 'Mentor 1', currentCompany: 'Company 1' }

      ;(mentorsApi.getById as any).mockResolvedValue(mockMentor)

      const { result } = renderHook(() => useMentor(1), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockMentor)
      expect(mentorsApi.getById).toHaveBeenCalledWith(1)
    })
  })

  describe('useCreateMentor', () => {
    it('should create a mentor and invalidate queries', async () => {
      const mockMentor = { id: 1, name: 'New Mentor' }
      const mentorInput = {
        name: 'New Mentor',
        currentCompany: 'Tech Corp',
      }

      ;(mentorsApi.create as any).mockResolvedValue(mockMentor)

      const { result } = renderHook(() => useCreateMentor(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(mentorInput)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(mentorsApi.create).toHaveBeenCalledWith(mentorInput)
    })
  })

  describe('useUpdateMentor', () => {
    it('should update a mentor and invalidate queries', async () => {
      const mockUpdated = { id: 1, name: 'Updated Mentor' }
      const mentorInput = {
        name: 'Updated Mentor',
        currentCompany: 'New Tech Corp',
      }

      ;(mentorsApi.update as any).mockResolvedValue(mockUpdated)

      const { result } = renderHook(() => useUpdateMentor(), {
        wrapper: createWrapper(),
      })

      result.current.mutate({ id: 1, mentor: mentorInput })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(mentorsApi.update).toHaveBeenCalledWith(1, mentorInput)
    })
  })

  describe('useDeleteMentor', () => {
    it('should delete a mentor and invalidate queries', async () => {
      ;(mentorsApi.delete as any).mockResolvedValue(undefined)

      const { result } = renderHook(() => useDeleteMentor(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(1)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(mentorsApi.delete).toHaveBeenCalledWith(1)
    })
  })
})

