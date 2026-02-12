import apiClient from './generated-client'
import type { Mentor, MentorInput } from '../generated'

export type { Mentor, MentorInput }

export const mentorsApi = {
  getAll: async (): Promise<Mentor[]> => {
    const response = await apiClient.getAllMentors()
    return response.data as Mentor[]
  },

  getById: async (id: number): Promise<Mentor> => {
    const response = await apiClient.getMentorById(id)
    return response.data as Mentor
  },

  create: async (mentor: MentorInput): Promise<Mentor> => {
    const response = await apiClient.createMentor(mentor as any)
    return response.data as Mentor
  },

  update: async (id: number, mentor: MentorInput): Promise<Mentor> => {
    const response = await apiClient.updateMentor(id, mentor as any)
    return response.data as Mentor
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.deleteMentor(id)
  },
}

