import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mentorsApi } from '../mentors'
import apiClient from '../generated-client'

vi.mock('../generated-client')

describe('mentorsApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getAll', () => {
    it('should fetch all mentors', async () => {
      const mockMentors = [
        { id: 1, name: 'Mentor 1', currentCompany: 'Company 1' },
        { id: 2, name: 'Mentor 2', currentCompany: 'Company 2' },
      ]

      ;(apiClient.getAllMentors as any).mockResolvedValue({ data: mockMentors })

      const result = await mentorsApi.getAll()

      expect(result).toEqual(mockMentors)
      expect(apiClient.getAllMentors).toHaveBeenCalled()
    })
  })

  describe('getById', () => {
    it('should fetch mentor by id', async () => {
      const mockMentor = { id: 1, name: 'Mentor 1', currentCompany: 'Company 1' }

      ;(apiClient.getMentorById as any).mockResolvedValue({ data: mockMentor })

      const result = await mentorsApi.getById(1)

      expect(result).toEqual(mockMentor)
      expect(apiClient.getMentorById).toHaveBeenCalledWith(1)
    })
  })

  describe('create', () => {
    it('should create a new mentor', async () => {
      const mentorInput = {
        name: 'New Mentor',
        currentCompany: 'Tech Corp',
      }
      const mockCreated = { id: 1, ...mentorInput }

      ;(apiClient.createMentor as any).mockResolvedValue({ data: mockCreated })

      const result = await mentorsApi.create(mentorInput)

      expect(result).toEqual(mockCreated)
      expect(apiClient.createMentor).toHaveBeenCalled()
    })
  })

  describe('update', () => {
    it('should update an existing mentor', async () => {
      const mentorInput = {
        name: 'Updated Mentor',
        currentCompany: 'New Tech Corp',
      }
      const mockUpdated = { id: 1, ...mentorInput }

      ;(apiClient.updateMentor as any).mockResolvedValue({ data: mockUpdated })

      const result = await mentorsApi.update(1, mentorInput)

      expect(result).toEqual(mockUpdated)
      expect(apiClient.updateMentor).toHaveBeenCalledWith(1, expect.anything())
    })
  })

  describe('delete', () => {
    it('should delete a mentor', async () => {
      ;(apiClient.deleteMentor as any).mockResolvedValue({})

      await mentorsApi.delete(1)

      expect(apiClient.deleteMentor).toHaveBeenCalledWith(1)
    })
  })
})

