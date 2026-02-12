import React from 'react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import {
  useMentorSessions,
  useMentorSession,
  useCreateMentorSession,
  useUpdateMentorSession,
  useDeleteMentorSession,
} from '../useMentorSessions'
import { mentorSessionsApi } from '../../api/mentorSessions'
import { useToast } from '../useToast'

vi.mock('../../api/mentorSessions')
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

describe('useMentorSessions hooks', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    ;(useToast as any).mockReturnValue({
      toast: vi.fn(),
    })
  })

  describe('useMentorSessions', () => {
    it('should fetch all mentor sessions', async () => {
      const mockSessions = [
        {
          id: 1,
          time: '2024-03-20T15:00:00Z',
          durationMinutes: 60,
          studentId: 1,
          mentorId: 1,
        },
      ]

      ;(mentorSessionsApi.getAll as any).mockResolvedValue(mockSessions)

      const { result } = renderHook(() => useMentorSessions(), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockSessions)
      expect(mentorSessionsApi.getAll).toHaveBeenCalled()
    })
  })

  describe('useMentorSession', () => {
    it('should fetch mentor session by id', async () => {
      const mockSession = {
        id: 1,
        time: '2024-03-20T15:00:00Z',
        durationMinutes: 60,
        studentId: 1,
        mentorId: 1,
      }

      ;(mentorSessionsApi.getById as any).mockResolvedValue(mockSession)

      const { result } = renderHook(() => useMentorSession(1), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockSession)
      expect(mentorSessionsApi.getById).toHaveBeenCalledWith(1)
    })
  })

  describe('useCreateMentorSession', () => {
    it('should create a mentor session and invalidate queries', async () => {
      const mockSession = { id: 1, time: '2024-03-20T15:00:00Z', durationMinutes: 60 }
      const sessionInput = {
        time: '2024-03-20T15:00:00Z',
        durationMinutes: 60,
        studentId: 1,
        mentorId: 1,
      }

      ;(mentorSessionsApi.create as any).mockResolvedValue(mockSession)

      const { result } = renderHook(() => useCreateMentorSession(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(sessionInput)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(mentorSessionsApi.create).toHaveBeenCalledWith(sessionInput)
    })
  })

  describe('useUpdateMentorSession', () => {
    it('should update a mentor session and invalidate queries', async () => {
      const mockUpdated = { id: 1, durationMinutes: 90 }
      const sessionInput = {
        time: '2024-03-21T16:00:00Z',
        durationMinutes: 90,
        studentId: 1,
        mentorId: 1,
      }

      ;(mentorSessionsApi.update as any).mockResolvedValue(mockUpdated)

      const { result } = renderHook(() => useUpdateMentorSession(), {
        wrapper: createWrapper(),
      })

      result.current.mutate({ id: 1, session: sessionInput })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(mentorSessionsApi.update).toHaveBeenCalledWith(1, sessionInput)
    })
  })

  describe('useDeleteMentorSession', () => {
    it('should delete a mentor session and invalidate queries', async () => {
      ;(mentorSessionsApi.delete as any).mockResolvedValue(undefined)

      const { result } = renderHook(() => useDeleteMentorSession(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(1)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(mentorSessionsApi.delete).toHaveBeenCalledWith(1)
    })
  })
})

