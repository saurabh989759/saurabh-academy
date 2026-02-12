import { describe, it, expect, vi, beforeEach } from 'vitest'
import { batchesApi } from '../batches'
import apiClient from '../generated-client'

vi.mock('../generated-client')

describe('batchesApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getAllPaged', () => {
    it('should fetch paginated batches', async () => {
      const mockPage = {
        content: [{ id: 1, name: 'Batch 1' }],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true,
      }

      ;(apiClient.getAllBatches as any).mockResolvedValue({ data: mockPage })

      const result = await batchesApi.getAllPaged(0, 20)

      expect(result).toEqual(mockPage)
      expect(apiClient.getAllBatches).toHaveBeenCalledWith(0, 20, undefined)
    })
  })

  describe('getById', () => {
    it('should fetch batch by id', async () => {
      const mockBatch = { id: 1, name: 'Batch 1', startMonth: '2024-01-01' }

      ;(apiClient.getBatchById as any).mockResolvedValue({ data: mockBatch })

      const result = await batchesApi.getById(1)

      expect(result).toEqual(mockBatch)
      expect(apiClient.getBatchById).toHaveBeenCalledWith(1)
    })
  })

  describe('create', () => {
    it('should create a new batch', async () => {
      const batchInput = {
        name: 'New Batch',
        startMonth: '2024-03-01',
        currentInstructor: 'John Instructor',
        batchTypeId: 1,
      }
      const mockCreated = { id: 1, ...batchInput }

      ;(apiClient.createBatch as any).mockResolvedValue({ data: mockCreated })

      const result = await batchesApi.create(batchInput)

      expect(result).toEqual(mockCreated)
      expect(apiClient.createBatch).toHaveBeenCalled()
    })
  })

  describe('update', () => {
    it('should update an existing batch', async () => {
      const batchInput = {
        name: 'Updated Batch',
        startMonth: '2024-03-01',
        currentInstructor: 'Jane Instructor',
        batchTypeId: 1,
      }
      const mockUpdated = { id: 1, ...batchInput }

      ;(apiClient.updateBatch as any).mockResolvedValue({ data: mockUpdated })

      const result = await batchesApi.update(1, batchInput)

      expect(result).toEqual(mockUpdated)
      expect(apiClient.updateBatch).toHaveBeenCalledWith(1, expect.anything())
    })
  })

  describe('delete', () => {
    it('should delete a batch', async () => {
      ;(apiClient.deleteBatch as any).mockResolvedValue({})

      await batchesApi.delete(1)

      expect(apiClient.deleteBatch).toHaveBeenCalledWith(1)
    })
  })

  describe('assignClassToBatch', () => {
    it('should assign a class to a batch', async () => {
      const mockBatch = { id: 1, name: 'Batch 1', classIds: [1, 2] }

      ;(apiClient.assignClassToBatch as any).mockResolvedValue({ data: mockBatch })

      const result = await batchesApi.assignClassToBatch(1, 2)

      expect(result).toEqual(mockBatch)
      expect(apiClient.assignClassToBatch).toHaveBeenCalledWith(1, 2)
    })
  })
})

