import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mentorSessionsApi } from '../mentorSessions'
import apiClient from '../generated-client'

vi.mock('../generated-client')

describe('mentorSessionsApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getAll', () => {
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

      ;(apiClient.getAllMentorSessions as any).mockResolvedValue({ data: mockSessions })

      const result = await mentorSessionsApi.getAll()

      expect(result).toEqual(mockSessions)
      expect(apiClient.getAllMentorSessions).toHaveBeenCalled()
    })
  })

  describe('getById', () => {
    it('should fetch mentor session by id', async () => {
      const mockSession = {
        id: 1,
        time: '2024-03-20T15:00:00Z',
        durationMinutes: 60,
        studentId: 1,
        mentorId: 1,
      }

      ;(apiClient.getMentorSessionById as any).mockResolvedValue({ data: mockSession })

      const result = await mentorSessionsApi.getById(1)

      expect(result).toEqual(mockSession)
      expect(apiClient.getMentorSessionById).toHaveBeenCalledWith(1)
    })
  })

  describe('create', () => {
    it('should create a new mentor session', async () => {
      const sessionInput = {
        time: '2024-03-20T15:00:00Z',
        durationMinutes: 60,
        studentId: 1,
        mentorId: 1,
      }
      const mockCreated = { id: 1, ...sessionInput }

      ;(apiClient.createMentorSession as any).mockResolvedValue({ data: mockCreated })

      const result = await mentorSessionsApi.create(sessionInput)

      expect(result).toEqual(mockCreated)
      expect(apiClient.createMentorSession).toHaveBeenCalled()
    })
  })

  describe('update', () => {
    it('should update an existing mentor session', async () => {
      const sessionInput = {
        time: '2024-03-21T16:00:00Z',
        durationMinutes: 90,
        studentId: 1,
        mentorId: 1,
        studentRating: 5,
        mentorRating: 4,
      }
      const mockUpdated = { id: 1, ...sessionInput }

      ;(apiClient.updateMentorSession as any).mockResolvedValue({ data: mockUpdated })

      const result = await mentorSessionsApi.update(1, sessionInput)

      expect(result).toEqual(mockUpdated)
      expect(apiClient.updateMentorSession).toHaveBeenCalledWith(1, expect.anything())
    })
  })

  describe('delete', () => {
    it('should delete a mentor session', async () => {
      ;(apiClient.deleteMentorSession as any).mockResolvedValue({})

      await mentorSessionsApi.delete(1)

      expect(apiClient.deleteMentorSession).toHaveBeenCalledWith(1)
    })
  })
})

