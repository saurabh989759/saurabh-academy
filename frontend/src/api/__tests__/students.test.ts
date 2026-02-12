import { describe, it, expect, vi, beforeEach } from 'vitest'
import { studentsApi } from '../students'
import apiClient from '../generated-client'

vi.mock('../generated-client')

describe('studentsApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getAll', () => {
    it('should fetch all students', async () => {
      const mockStudents = [
        { id: 1, name: 'Student 1', email: 'student1@example.com' },
        { id: 2, name: 'Student 2', email: 'student2@example.com' },
      ]

      ;(apiClient.getAllStudents as any).mockResolvedValue({ data: mockStudents })

      const result = await studentsApi.getAll()

      expect(result).toEqual(mockStudents)
      expect(apiClient.getAllStudents).toHaveBeenCalledWith(undefined)
    })

    it('should fetch students filtered by batchId', async () => {
      const mockStudents = [{ id: 1, name: 'Student 1', email: 'student1@example.com' }]

      ;(apiClient.getAllStudents as any).mockResolvedValue({ data: mockStudents })

      const result = await studentsApi.getAll(1)

      expect(result).toEqual(mockStudents)
      expect(apiClient.getAllStudents).toHaveBeenCalledWith(1)
    })
  })

  describe('getAllPaged', () => {
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

      ;(apiClient.getAllStudentsPaged as any).mockResolvedValue({ data: mockPage })

      const result = await studentsApi.getAllPaged(0, 20)

      expect(result).toEqual(mockPage)
      expect(apiClient.getAllStudentsPaged).toHaveBeenCalledWith(0, 20, undefined)
    })

    it('should fetch paginated students with sort', async () => {
      const mockPage = {
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: 10,
        number: 0,
        first: true,
        last: true,
      }

      ;(apiClient.getAllStudentsPaged as any).mockResolvedValue({ data: mockPage })

      const result = await studentsApi.getAllPaged(0, 10, 'name,asc')

      expect(result).toEqual(mockPage)
      expect(apiClient.getAllStudentsPaged).toHaveBeenCalledWith(0, 10, 'name,asc')
    })
  })

  describe('getById', () => {
    it('should fetch student by id', async () => {
      const mockStudent = { id: 1, name: 'Student 1', email: 'student1@example.com' }

      ;(apiClient.getStudentById as any).mockResolvedValue({ data: mockStudent })

      const result = await studentsApi.getById(1)

      expect(result).toEqual(mockStudent)
      expect(apiClient.getStudentById).toHaveBeenCalledWith(1)
    })
  })

  describe('create', () => {
    it('should create a new student', async () => {
      const studentInput = {
        name: 'New Student',
        email: 'new@example.com',
        graduationYear: 2024,
      }
      const mockCreated = { id: 1, ...studentInput }

      ;(apiClient.createStudent as any).mockResolvedValue({ data: mockCreated })

      const result = await studentsApi.create(studentInput)

      expect(result).toEqual(mockCreated)
      expect(apiClient.createStudent).toHaveBeenCalled()
    })
  })

  describe('update', () => {
    it('should update an existing student', async () => {
      const studentInput = {
        name: 'Updated Student',
        email: 'updated@example.com',
      }
      const mockUpdated = { id: 1, ...studentInput }

      ;(apiClient.updateStudent as any).mockResolvedValue({ data: mockUpdated })

      const result = await studentsApi.update(1, studentInput)

      expect(result).toEqual(mockUpdated)
      expect(apiClient.updateStudent).toHaveBeenCalledWith(1, expect.anything())
    })
  })

  describe('delete', () => {
    it('should delete a student', async () => {
      ;(apiClient.deleteStudent as any).mockResolvedValue({})

      await studentsApi.delete(1)

      expect(apiClient.deleteStudent).toHaveBeenCalledWith(1)
    })
  })
})

