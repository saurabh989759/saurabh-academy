import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { classesApi, ClassInput } from '../api/classes'
import { useToast } from './useToast'

export const useClasses = () => {
  return useQuery({
    queryKey: ['classes'],
    queryFn: () => classesApi.getAll(),
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useClass = (id: number) => {
  return useQuery({
    queryKey: ['classes', id],
    queryFn: () => classesApi.getById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useCreateClass = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (classData: ClassInput) => classesApi.create(classData),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ 
        queryKey: ['classes'],
        refetchType: 'none'
      })
      if (data?.id) {
        queryClient.setQueryData(['classes', data.id], data)
      }
      toast({
        title: 'Success',
        description: 'Class created successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to create class',
        variant: 'error',
      })
    },
  })
}

export const useUpdateClass = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: ({ id, classData }: { id: number; classData: ClassInput }) =>
      classesApi.update(id, classData),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ 
        queryKey: ['classes'],
        refetchType: 'none'
      })
      queryClient.invalidateQueries({ 
        queryKey: ['classes', variables.id],
        refetchType: 'none'
      })
      if (data) {
        queryClient.setQueryData(['classes', variables.id], data)
      }
      toast({
        title: 'Success',
        description: 'Class updated successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update class',
        variant: 'error',
      })
    },
  })
}

export const useDeleteClass = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (id: number) => classesApi.delete(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ 
        queryKey: ['classes'],
        refetchType: 'none'
      })
      queryClient.removeQueries({ queryKey: ['classes', id] })
      toast({
        title: 'Success',
        description: 'Class deleted successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to delete class',
        variant: 'error',
      })
    },
  })
}

