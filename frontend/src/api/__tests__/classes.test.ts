import { describe, it, expect, vi, beforeEach } from 'vitest'
import { classesApi } from '../classes'
import apiClient from '../generated-client'

vi.mock('../generated-client')

describe('classesApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getAll', () => {
    it('should fetch all classes', async () => {
      const mockClasses = [
        { id: 1, name: 'Class 1', date: '2024-01-01', time: '10:00:00', instructor: 'Instructor 1' },
      ]

      ;(apiClient.getAllClasses as any).mockResolvedValue({ data: mockClasses })

      const result = await classesApi.getAll()

      expect(result).toEqual(mockClasses)
      expect(apiClient.getAllClasses).toHaveBeenCalled()
    })
  })

  describe('getById', () => {
    it('should fetch class by id', async () => {
      const mockClass = { id: 1, name: 'Class 1', date: '2024-01-01', time: '10:00:00' }

      ;(apiClient.getClassById as any).mockResolvedValue({ data: mockClass })

      const result = await classesApi.getById(1)

      expect(result).toEqual(mockClass)
      expect(apiClient.getClassById).toHaveBeenCalledWith(1)
    })
  })

  describe('create', () => {
    it('should create a new class', async () => {
      const classInput = {
        name: 'New Class',
        date: '2024-03-15',
        time: '10:00:00',
        instructor: 'Jane Smith',
      }
      const mockCreated = { id: 1, ...classInput }

      ;(apiClient.createClass as any).mockResolvedValue({ data: mockCreated })

      const result = await classesApi.create(classInput)

      expect(result).toEqual(mockCreated)
      expect(apiClient.createClass).toHaveBeenCalled()
    })
  })

  describe('update', () => {
    it('should update an existing class', async () => {
      const classInput = {
        name: 'Updated Class',
        date: '2024-03-20',
        time: '14:00:00',
        instructor: 'John Doe',
      }
      const mockUpdated = { id: 1, ...classInput }

      ;(apiClient.updateClass as any).mockResolvedValue({ data: mockUpdated })

      const result = await classesApi.update(1, classInput)

      expect(result).toEqual(mockUpdated)
      expect(apiClient.updateClass).toHaveBeenCalledWith(1, expect.anything())
    })
  })

  describe('delete', () => {
    it('should delete a class', async () => {
      ;(apiClient.deleteClass as any).mockResolvedValue({})

      await classesApi.delete(1)

      expect(apiClient.deleteClass).toHaveBeenCalledWith(1)
    })
  })
})

