import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { mentorSessionsApi, MentorSessionInput } from '../api/mentorSessions'
import { useToast } from './useToast'

export const useMentorSessions = () => {
  return useQuery({
    queryKey: ['mentorSessions'],
    queryFn: () => mentorSessionsApi.getAll(),
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useMentorSession = (id: number) => {
  return useQuery({
    queryKey: ['mentorSessions', id],
    queryFn: () => mentorSessionsApi.getById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes - prevent duplicate calls
    refetchOnMount: false, // Don't refetch if data is fresh
  })
}

export const useCreateMentorSession = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (session: MentorSessionInput) => mentorSessionsApi.create(session),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ 
        queryKey: ['mentorSessions'],
        refetchType: 'none'
      })
      if (data?.id) {
        queryClient.setQueryData(['mentorSessions', data.id], data)
      }
      toast({
        title: 'Success',
        description: 'Mentor session created successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to create mentor session',
        variant: 'error',
      })
    },
  })
}

export const useUpdateMentorSession = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: ({ id, session }: { id: number; session: MentorSessionInput }) =>
      mentorSessionsApi.update(id, session),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ 
        queryKey: ['mentorSessions'],
        refetchType: 'none'
      })
      queryClient.invalidateQueries({ 
        queryKey: ['mentorSessions', variables.id],
        refetchType: 'none'
      })
      if (data) {
        queryClient.setQueryData(['mentorSessions', variables.id], data)
      }
      toast({
        title: 'Success',
        description: 'Mentor session updated successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update mentor session',
        variant: 'error',
      })
    },
  })
}

export const useDeleteMentorSession = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (id: number) => mentorSessionsApi.delete(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ 
        queryKey: ['mentorSessions'],
        refetchType: 'none'
      })
      queryClient.removeQueries({ queryKey: ['mentorSessions', id] })
      toast({
        title: 'Success',
        description: 'Mentor session deleted successfully',
        variant: 'success',
      })
    },
    onError: (error: any) => {
      toast({
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to delete mentor session',
        variant: 'error',
      })
    },
  })
}

