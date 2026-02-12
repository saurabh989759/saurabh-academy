import apiClient from './generated-client'
import type { Student, StudentInput, PageStudent } from '../generated'

export type { Student, StudentInput, PageStudent }

export const studentsApi = {
  getAll: async (batchId?: number): Promise<Student[]> => {
    const response = await apiClient.getAllStudents(batchId)
    return response.data as Student[]
  },

  getAllPaged: async (page = 0, size = 20, sort?: string): Promise<PageStudent> => {
    const response = await apiClient.getAllStudentsPaged(page, size, sort)
    return response.data as PageStudent
  },

  getById: async (id: number): Promise<Student> => {
    const response = await apiClient.getStudentById(id)
    return response.data as Student
  },

  create: async (student: StudentInput): Promise<Student> => {
    const response = await apiClient.createStudent(student as any)
    return response.data as Student
  },

  update: async (id: number, student: StudentInput): Promise<Student> => {
    const response = await apiClient.updateStudent(id, student as any)
    return response.data as Student
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.deleteStudent(id)
  },
}
