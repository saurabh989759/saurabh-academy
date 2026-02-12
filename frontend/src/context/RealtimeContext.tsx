import { createContext, useContext, useEffect, useState, ReactNode } from 'react'
import { wsClient, WebSocketEvent } from '../ws/client'
import { useAuth } from './AuthContext'
import { useQueryClient } from '@tanstack/react-query'
import { useToast } from '../hooks/useToast'

interface RealtimeContextType {
  isConnected: boolean
}

const RealtimeContext = createContext<RealtimeContextType | undefined>(undefined)

export const RealtimeProvider = ({ children }: { children: ReactNode }) => {
  const { token, isAuthenticated } = useAuth()
  const queryClient = useQueryClient()
  const { toast } = useToast()
  const [isConnected, setIsConnected] = useState(false)

  useEffect(() => {
    if (!isAuthenticated || !token) {
      wsClient.disconnect()
      setIsConnected(false)
      return
    }

    // Connect to WebSocket (non-blocking - app works without it)
    wsClient
      .connect(token)
      .then(() => {
        setIsConnected(true)
        console.log('✅ WebSocket connected successfully')

        // Subscribe to student events
        wsClient.subscribe('students', (event: WebSocketEvent) => {
          queryClient.invalidateQueries({ queryKey: ['students'] })

          switch (event.type) {
            case 'STUDENT_CREATED':
              toast({
                title: 'New Student',
                description: `Student ${event.payload.id} has been created`,
                variant: 'info',
              })
              break
            case 'STUDENT_UPDATED':
              queryClient.invalidateQueries({ queryKey: ['students', event.payload.id] })
              toast({
                title: 'Student Updated',
                description: `Student ${event.payload.id} has been updated`,
                variant: 'info',
              })
              break
            case 'STUDENT_DELETED':
              toast({
                title: 'Student Deleted',
                description: `Student ${event.payload.id} has been deleted`,
                variant: 'info',
              })
              break
          }
        })

        // Subscribe to batch events
        wsClient.subscribe('batches', (event: WebSocketEvent) => {
          queryClient.invalidateQueries({ queryKey: ['batches'] })

          switch (event.type) {
            case 'BATCH_CREATED':
              toast({
                title: 'New Batch',
                description: `Batch ${event.payload.id} has been created`,
                variant: 'info',
              })
              break
            case 'BATCH_UPDATED':
              queryClient.invalidateQueries({ queryKey: ['batches', event.payload.id] })
              break
            case 'BATCH_DELETED':
              toast({
                title: 'Batch Deleted',
                description: `Batch ${event.payload.id} has been deleted`,
                variant: 'info',
              })
              break
          }
        })

        // Subscribe to class events
        wsClient.subscribe('classes', (event: WebSocketEvent) => {
          queryClient.invalidateQueries({ queryKey: ['classes'] })

          switch (event.type) {
            case 'CLASS_CREATED':
              toast({
                title: 'New Class',
                description: `Class ${event.payload.id} has been created`,
                variant: 'info',
              })
              break
            case 'CLASS_UPDATED':
              queryClient.invalidateQueries({ queryKey: ['classes', event.payload.id] })
              break
            case 'CLASS_DELETED':
              toast({
                title: 'Class Deleted',
                description: `Class ${event.payload.id} has been deleted`,
                variant: 'info',
              })
              break
          }
        })

        // Subscribe to mentor events
        wsClient.subscribe('mentors', (event: WebSocketEvent) => {
          queryClient.invalidateQueries({ queryKey: ['mentors'] })

          switch (event.type) {
            case 'MENTOR_CREATED':
              toast({
                title: 'New Mentor',
                description: `Mentor ${event.payload.id} has been created`,
                variant: 'info',
              })
              break
            case 'MENTOR_UPDATED':
              queryClient.invalidateQueries({ queryKey: ['mentors', event.payload.id] })
              break
            case 'MENTOR_DELETED':
              toast({
                title: 'Mentor Deleted',
                description: `Mentor ${event.payload.id} has been deleted`,
                variant: 'info',
              })
              break
          }
        })

        // Subscribe to mentor session events
        wsClient.subscribe('mentorSessions', (event: WebSocketEvent) => {
          queryClient.invalidateQueries({ queryKey: ['mentorSessions'] })

          switch (event.type) {
            case 'MENTOR_SESSION_CREATED':
              toast({
                title: 'New Session',
                description: `Mentor session ${event.payload.id} has been created`,
                variant: 'info',
              })
              break
            case 'MENTOR_SESSION_UPDATED':
              queryClient.invalidateQueries({ queryKey: ['mentorSessions', event.payload.id] })
              break
            case 'MENTOR_SESSION_DELETED':
              toast({
                title: 'Session Deleted',
                description: `Mentor session ${event.payload.id} has been deleted`,
                variant: 'info',
              })
              break
          }
        })
      })
      .catch((error) => {
        console.warn('⚠️ WebSocket connection failed (app will continue without real-time updates):', error)
        setIsConnected(false)
        // Don't show error to user - WebSocket is optional for real-time updates
      })

    return () => {
      wsClient.disconnect()
      setIsConnected(false)
    }
  }, [isAuthenticated, token, queryClient, toast])

  return <RealtimeContext.Provider value={{ isConnected }}>{children}</RealtimeContext.Provider>
}

export const useRealtime = () => {
  const context = useContext(RealtimeContext)
  if (!context) {
    throw new Error('useRealtime must be used within RealtimeProvider')
  }
  return context
}

