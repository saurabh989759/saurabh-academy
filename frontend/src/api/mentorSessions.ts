import apiClient from './generated-client'
import type { MentorSession, MentorSessionInput } from '../generated'

export type { MentorSession, MentorSessionInput }

export const mentorSessionsApi = {
  getAll: async (): Promise<MentorSession[]> => {
    const response = await apiClient.getAllMentorSessions()
    return response.data as MentorSession[]
  },

  getById: async (id: number): Promise<MentorSession> => {
    const response = await apiClient.getMentorSessionById(id)
    return response.data as MentorSession
  },

  create: async (session: MentorSessionInput): Promise<MentorSession> => {
    const response = await apiClient.createMentorSession(session as any)
    return response.data as MentorSession
  },

  update: async (id: number, session: MentorSessionInput): Promise<MentorSession> => {
    const response = await apiClient.updateMentorSession(id, session as any)
    return response.data as MentorSession
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.deleteMentorSession(id)
  },
}

