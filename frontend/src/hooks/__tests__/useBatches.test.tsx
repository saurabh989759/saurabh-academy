import React from 'react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import {
  useBatchesPaged,
  useBatch,
  useCreateBatch,
  useUpdateBatch,
  useDeleteBatch,
  useAssignClassToBatch,
} from '../useBatches'
import { batchesApi } from '../../api/batches'
import { useToast } from '../useToast'

vi.mock('../../api/batches')
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

describe('useBatches hooks', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    ;(useToast as any).mockReturnValue({
      toast: vi.fn(),
    })
  })

  describe('useBatchesPaged', () => {
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

      ;(batchesApi.getAllPaged as any).mockResolvedValue(mockPage)

      const { result } = renderHook(() => useBatchesPaged(0, 20), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockPage)
      expect(batchesApi.getAllPaged).toHaveBeenCalledWith(0, 20, undefined)
    })
  })

  describe('useBatch', () => {
    it('should fetch batch by id', async () => {
      const mockBatch = { id: 1, name: 'Batch 1', startMonth: '2024-01-01' }

      ;(batchesApi.getById as any).mockResolvedValue(mockBatch)

      const { result } = renderHook(() => useBatch(1), {
        wrapper: createWrapper(),
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(result.current.data).toEqual(mockBatch)
      expect(batchesApi.getById).toHaveBeenCalledWith(1)
    })
  })

  describe('useCreateBatch', () => {
    it('should create a batch and invalidate queries', async () => {
      const mockBatch = { id: 1, name: 'New Batch' }
      const batchInput = {
        name: 'New Batch',
        startMonth: '2024-03-01',
        currentInstructor: 'John Instructor',
        batchTypeId: 1,
      }

      ;(batchesApi.create as any).mockResolvedValue(mockBatch)

      const { result } = renderHook(() => useCreateBatch(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(batchInput)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(batchesApi.create).toHaveBeenCalledWith(batchInput)
    })
  })

  describe('useUpdateBatch', () => {
    it('should update a batch and invalidate queries', async () => {
      const mockUpdated = { id: 1, name: 'Updated Batch' }
      const batchInput = {
        name: 'Updated Batch',
        startMonth: '2024-03-01',
        currentInstructor: 'Jane Instructor',
        batchTypeId: 1,
      }

      ;(batchesApi.update as any).mockResolvedValue(mockUpdated)

      const { result } = renderHook(() => useUpdateBatch(), {
        wrapper: createWrapper(),
      })

      result.current.mutate({ id: 1, batch: batchInput })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(batchesApi.update).toHaveBeenCalledWith(1, batchInput)
    })
  })

  describe('useDeleteBatch', () => {
    it('should delete a batch and invalidate queries', async () => {
      ;(batchesApi.delete as any).mockResolvedValue(undefined)

      const { result } = renderHook(() => useDeleteBatch(), {
        wrapper: createWrapper(),
      })

      result.current.mutate(1)

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(batchesApi.delete).toHaveBeenCalledWith(1)
    })
  })

  describe('useAssignClassToBatch', () => {
    it('should assign class to batch and invalidate queries', async () => {
      const mockBatch = { id: 1, name: 'Batch 1', classIds: [1, 2] }

      ;(batchesApi.assignClassToBatch as any).mockResolvedValue(mockBatch)

      const { result } = renderHook(() => useAssignClassToBatch(), {
        wrapper: createWrapper(),
      })

      result.current.mutate({ batchId: 1, classId: 2 })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))

      expect(batchesApi.assignClassToBatch).toHaveBeenCalledWith(1, 2)
    })
  })
})

