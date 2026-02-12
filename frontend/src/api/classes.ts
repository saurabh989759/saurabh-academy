import apiClient from './generated-client'
import type { ModelClass, ClassInput } from '../generated'

export type { ModelClass as Class, ClassInput }

export const classesApi = {
  getAll: async (): Promise<ModelClass[]> => {
    const response = await apiClient.getAllClasses()
    return response.data as ModelClass[]
  },

  getById: async (id: number): Promise<ModelClass> => {
    const response = await apiClient.getClassById(id)
    return response.data as ModelClass
  },

  create: async (classData: ClassInput): Promise<ModelClass> => {
    const response = await apiClient.createClass(classData as any)
    return response.data as ModelClass
  },

  update: async (id: number, classData: ClassInput): Promise<ModelClass> => {
    const response = await apiClient.updateClass(id, classData as any)
    return response.data as ModelClass
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.deleteClass(id)
  },
}

