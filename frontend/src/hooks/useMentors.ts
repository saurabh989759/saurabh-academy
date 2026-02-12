import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { mentorsApi, MentorInput } from '../api/mentors'
import { useToast } from './useToast'

export const useMentors = () => {
  return useQuery({
    queryKey: ['mentors'],
    queryFn: () => mentorsApi.getAll(),
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useMentor = (id: number) => {
  return useQuery({
    queryKey: ['mentors', id],
    queryFn: () => mentorsApi.getById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useCreateMentor = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (mentor: MentorInput) => mentorsApi.create(mentor),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ 
        queryKey: ['mentors'],
        refetchType: 'none'
      })
      if (data?.id) {
        queryClient.setQueryData(['mentors', data.id], data)
      }
      toast({
        title: 'Success',
        description: 'Mentor created successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to create mentor',
        variant: 'error',
      })
    },
  })
}

export const useUpdateMentor = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: ({ id, mentor }: { id: number; mentor: MentorInput }) =>
      mentorsApi.update(id, mentor),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ 
        queryKey: ['mentors'],
        refetchType: 'none'
      })
      queryClient.invalidateQueries({ 
        queryKey: ['mentors', variables.id],
        refetchType: 'none'
      })
      if (data) {
        queryClient.setQueryData(['mentors', variables.id], data)
      }
      toast({
        title: 'Success',
        description: 'Mentor updated successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update mentor',
        variant: 'error',
      })
    },
  })
}

export const useDeleteMentor = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (id: number) => mentorsApi.delete(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ 
        queryKey: ['mentors'],
        refetchType: 'none'
      })
      queryClient.removeQueries({ queryKey: ['mentors', id] })
      toast({
        title: 'Success',
        description: 'Mentor deleted successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to delete mentor',
        variant: 'error',
      })
    },
  })
}

