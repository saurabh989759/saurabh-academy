import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { batchesApi, BatchInput } from '../api/batches'
import { useToast } from './useToast'

export const useBatchesPaged = (page = 0, size = 20, sort?: string) => {
  return useQuery({
    queryKey: ['batches', 'paged', page, size, sort],
    queryFn: () => batchesApi.getAllPaged(page, size, sort),
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useBatch = (id: number) => {
  return useQuery({
    queryKey: ['batches', id],
    queryFn: () => batchesApi.getById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useCreateBatch = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (batch: BatchInput) => batchesApi.create(batch),
    onSuccess: (data) => {
      // Invalidate but don't auto-refetch
      queryClient.invalidateQueries({ 
        queryKey: ['batches', 'paged'],
        refetchType: 'none'
      })
      // Set new data in cache if available
      if (data?.id) {
        queryClient.setQueryData(['batches', data.id], data)
      }
      toast({
        title: 'Success',
        description: 'Batch created successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to create batch',
        variant: 'error',
      })
    },
  })
}

export const useUpdateBatch = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: ({ id, batch }: { id: number; batch: BatchInput }) =>
      batchesApi.update(id, batch),
    onSuccess: (data, variables) => {
      // Invalidate but don't auto-refetch
      queryClient.invalidateQueries({ 
        queryKey: ['batches', 'paged'],
        refetchType: 'none'
      })
      queryClient.invalidateQueries({ 
        queryKey: ['batches', variables.id],
        refetchType: 'none'
      })
      // Update cache with new data
      if (data) {
        queryClient.setQueryData(['batches', variables.id], data)
      }
      toast({
        title: 'Success',
        description: 'Batch updated successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update batch',
        variant: 'error',
      })
    },
  })
}

export const useDeleteBatch = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (id: number) => batchesApi.delete(id),
    onSuccess: (_, id) => {
      // Invalidate but don't auto-refetch
      queryClient.invalidateQueries({ 
        queryKey: ['batches', 'paged'],
        refetchType: 'none'
      })
      // Remove deleted item from cache
      queryClient.removeQueries({ queryKey: ['batches', id] })
      toast({
        title: 'Success',
        description: 'Batch deleted successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to delete batch',
        variant: 'error',
      })
    },
  })
}

export const useAssignClassToBatch = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: ({ batchId, classId }: { batchId: number; classId: number }) =>
      batchesApi.assignClassToBatch(batchId, classId),
    onSuccess: (_, variables) => {
      // Invalidate but don't auto-refetch
      queryClient.invalidateQueries({ 
        queryKey: ['batches', 'paged'],
        refetchType: 'none'
      })
      queryClient.invalidateQueries({ 
        queryKey: ['batches', variables.batchId],
        refetchType: 'none'
      })
      toast({
        title: 'Success',
        description: 'Class assigned to batch successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to assign class to batch',
        variant: 'error',
      })
    },
  })
}

