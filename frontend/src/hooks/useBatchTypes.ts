import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { batchTypesApi, BatchTypeInput } from '../api/batchTypes'
import { useToast } from './useToast'

export const useBatchTypes = () => {
  return useQuery({
    queryKey: ['batchTypes'],
    queryFn: () => batchTypesApi.getAll(),
    staleTime: 5 * 60 * 1000,
    refetchOnMount: false,
  })
}

export const useBatchType = (id: number) => {
  return useQuery({
    queryKey: ['batchTypes', id],
    queryFn: () => batchTypesApi.getById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
    refetchOnMount: false,
  })
}

export const useCreateBatchType = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (batchType: BatchTypeInput) => batchTypesApi.create(batchType),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['batchTypes'], refetchType: 'none' })
      if (data?.id) {
        queryClient.setQueryData(['batchTypes', data.id], data)
      }
      toast({ title: 'Success', description: 'Batch type created successfully', variant: 'success' })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to create batch type',
        variant: 'error',
      })
    },
  })
}

export const useUpdateBatchType = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: ({ id, batchType }: { id: number; batchType: BatchTypeInput }) =>
      batchTypesApi.update(id, batchType),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['batchTypes'], refetchType: 'none' })
      queryClient.invalidateQueries({ queryKey: ['batchTypes', variables.id], refetchType: 'none' })
      if (data) {
        queryClient.setQueryData(['batchTypes', variables.id], data)
      }
      toast({ title: 'Success', description: 'Batch type updated successfully', variant: 'success' })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update batch type',
        variant: 'error',
      })
    },
  })
}

export const useDeleteBatchType = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (id: number) => batchTypesApi.delete(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: ['batchTypes'], refetchType: 'none' })
      queryClient.removeQueries({ queryKey: ['batchTypes', id] })
      toast({ title: 'Success', description: 'Batch type deleted successfully', variant: 'success' })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to delete batch type',
        variant: 'error',
      })
    },
  })
}
