import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { studentsApi, StudentInput } from '../api/students'
import { useToast } from '../hooks/useToast'

export const useStudents = (batchId?: number) => {
  return useQuery({
    queryKey: ['students', batchId],
    queryFn: () => studentsApi.getAll(batchId),
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useStudentsPaged = (page = 0, size = 20, sort?: string) => {
  return useQuery({
    queryKey: ['students', 'paged', page, size, sort],
    queryFn: () => studentsApi.getAllPaged(page, size, sort),
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useStudent = (id: number) => {
  return useQuery({
    queryKey: ['students', id],
    queryFn: () => studentsApi.getById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useCreateStudent = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (student: StudentInput) => studentsApi.create(student),
    onSuccess: (data) => {
      // Invalidate queries but don't automatically refetch - let components decide when to refetch
      queryClient.invalidateQueries({ 
        queryKey: ['students', 'paged'],
        refetchType: 'none' // Don't auto-refetch, just mark as stale
      })
      queryClient.invalidateQueries({ 
        queryKey: ['students'],
        refetchType: 'none'
      })
      // Set the new student in cache if we have the data
      if (data?.id) {
        queryClient.setQueryData(['students', data.id], data)
      }
      toast({
        title: 'Success',
        description: 'Student created successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to create student',
        variant: 'error',
      })
    },
  })
}

export const useUpdateStudent = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: ({ id, student }: { id: number; student: StudentInput }) =>
      studentsApi.update(id, student),
    onSuccess: (data, variables) => {
      // Invalidate queries but don't automatically refetch
      queryClient.invalidateQueries({ 
        queryKey: ['students', 'paged'],
        refetchType: 'none'
      })
      queryClient.invalidateQueries({ 
        queryKey: ['students', variables.id],
        refetchType: 'none'
      })
      // Update cache with new data
      if (data) {
        queryClient.setQueryData(['students', variables.id], data)
      }
      toast({
        title: 'Success',
        description: 'Student updated successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update student',
        variant: 'error',
      })
    },
  })
}

export const useDeleteStudent = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (id: number) => studentsApi.delete(id),
    onSuccess: (_, id) => {
      // Invalidate queries but don't automatically refetch
      queryClient.invalidateQueries({ 
        queryKey: ['students', 'paged'],
        refetchType: 'none'
      })
      queryClient.invalidateQueries({ 
        queryKey: ['students'],
        refetchType: 'none'
      })
      // Remove deleted item from cache
      queryClient.removeQueries({ queryKey: ['students', id] })
      toast({
        title: 'Success',
        description: 'Student deleted successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to delete student',
        variant: 'error',
      })
    },
  })
}

