import apiClient from './generated-client'
import type { Batch, BatchInput, PageBatch } from '../generated'

export type { Batch, BatchInput, PageBatch }

export const batchesApi = {
  getAllPaged: async (page = 0, size = 20, sort?: string): Promise<PageBatch> => {
    const response = await apiClient.getAllBatches(page, size, sort)
    return response.data as PageBatch
  },

  getById: async (id: number): Promise<Batch> => {
    const response = await apiClient.getBatchById(id)
    return response.data as Batch
  },

  create: async (batch: BatchInput): Promise<Batch> => {
    const response = await apiClient.createBatch(batch as any)
    return response.data as Batch
  },

  update: async (id: number, batch: BatchInput): Promise<Batch> => {
    const response = await apiClient.updateBatch(id, batch as any)
    return response.data as Batch
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.deleteBatch(id)
  },

  assignClassToBatch: async (id: number, classId: number): Promise<Batch> => {
    const response = await apiClient.assignClassToBatch(id, classId)
    return response.data as Batch
  },
}
