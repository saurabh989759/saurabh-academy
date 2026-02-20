import apiClient from './generated-client'

export interface BatchType {
  id?: number
  name?: string
}

export interface BatchTypeInput {
  name: string
}

export const batchTypesApi = {
  getAll: async (): Promise<BatchType[]> => {
    const response = await apiClient.getAllBatchTypes()
    return response.data as BatchType[]
  },

  getById: async (id: number): Promise<BatchType> => {
    const response = await apiClient.getBatchTypeById(id)
    return response.data as BatchType
  },

  create: async (batchType: BatchTypeInput): Promise<BatchType> => {
    const response = await apiClient.createBatchType(batchType as any)
    return response.data as BatchType
  },

  update: async (id: number, batchType: BatchTypeInput): Promise<BatchType> => {
    const response = await apiClient.updateBatchType(id, batchType as any)
    return response.data as BatchType
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.deleteBatchType(id)
  },
}
