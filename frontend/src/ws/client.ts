import { Client, IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

// SockJS uses http/https, not ws:// - it handles the WebSocket upgrade internally
const getWebSocketUrl = () => {
  const envUrl = import.meta.env.VITE_WS_URL
  if (envUrl) {
    // If VITE_WS_URL is set, use it (convert ws:// to http:// if needed)
    return envUrl.replace(/^ws:/, 'http:').replace(/^wss:/, 'https:')
  }
  // For development, check if we're on localhost (nginx proxy) or direct backend
  if (typeof window !== 'undefined' && window.location.origin === 'http://localhost') {
    return 'http://localhost/ws' // Use relative URL for nginx proxy
  }
  return 'http://localhost:8080/ws' // Direct backend access
}

const WS_URL = getWebSocketUrl()

export interface WebSocketEvent {
  type:
    | 'STUDENT_CREATED'
    | 'STUDENT_UPDATED'
    | 'STUDENT_DELETED'
    | 'BATCH_CREATED'
    | 'BATCH_UPDATED'
    | 'BATCH_DELETED'
    | 'CLASS_CREATED'
    | 'CLASS_UPDATED'
    | 'CLASS_DELETED'
    | 'MENTOR_CREATED'
    | 'MENTOR_UPDATED'
    | 'MENTOR_DELETED'
    | 'MENTOR_SESSION_CREATED'
    | 'MENTOR_SESSION_UPDATED'
    | 'MENTOR_SESSION_DELETED'
  payload: {
    id: number
    [key: string]: any
  }
  timestamp: string
}

class WebSocketClient {
  private client: Client | null = null
  private subscribers: Map<string, (event: WebSocketEvent) => void> = new Map()
  private reconnectAttempts = 0
  private maxReconnectAttempts = 10
  private reconnectDelay = 1000

  connect(token?: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.client?.connected) {
        resolve()
        return
      }

      // WS_URL is already converted to http/https in getWebSocketUrl()
      console.log('🔌 Connecting to WebSocket:', WS_URL)

      this.client = new Client({
        webSocketFactory: () => {
          try {
            return new SockJS(WS_URL) as any
          } catch (error) {
            console.error('❌ WebSocket factory error:', error)
            // Don't throw - allow app to continue without WebSocket
            throw error
          }
        },
        reconnectDelay: this.reconnectDelay,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log('✅ WebSocket connected')
          this.reconnectAttempts = 0
          this.subscribeToTopics()
          resolve()
        },
        onStompError: (frame) => {
          console.error('❌ STOMP error:', frame)
          // Don't reject - allow app to continue without WebSocket
          console.warn('⚠️ WebSocket connection failed, continuing without real-time updates')
          resolve() // Resolve instead of reject to allow app to continue
        },
        onWebSocketClose: () => {
          console.log('WebSocket closed')
          this.handleReconnect()
        },
        onDisconnect: () => {
          console.log('WebSocket disconnected')
        },
      })

      // Add authentication header if token provided
      if (token) {
        this.client.configure({
          connectHeaders: {
            Authorization: `Bearer ${token}`,
          },
        })
      }

      this.client.activate()
    })
  }

  private subscribeToTopics() {
    if (!this.client?.connected) return

    // Subscribe to student events
    this.client.subscribe('/topic/students', (message: IMessage) => {
      try {
        const event: WebSocketEvent = JSON.parse(message.body)
        const handler = this.subscribers.get('students')
        if (handler) {
          handler(event)
        }
      } catch (error) {
        console.error('Error parsing student event:', error)
      }
    })

    // Subscribe to batch events
    this.client.subscribe('/topic/batches', (message: IMessage) => {
      try {
        const event: WebSocketEvent = JSON.parse(message.body)
        const handler = this.subscribers.get('batches')
        if (handler) {
          handler(event)
        }
      } catch (error) {
        console.error('Error parsing batch event:', error)
      }
    })

    // Subscribe to class events
    this.client.subscribe('/topic/classes', (message: IMessage) => {
      try {
        const event: WebSocketEvent = JSON.parse(message.body)
        const handler = this.subscribers.get('classes')
        if (handler) {
          handler(event)
        }
      } catch (error) {
        console.error('Error parsing class event:', error)
      }
    })

    // Subscribe to mentor events
    this.client.subscribe('/topic/mentors', (message: IMessage) => {
      try {
        const event: WebSocketEvent = JSON.parse(message.body)
        const handler = this.subscribers.get('mentors')
        if (handler) {
          handler(event)
        }
      } catch (error) {
        console.error('Error parsing mentor event:', error)
      }
    })

    // Subscribe to mentor session events
    this.client.subscribe('/topic/mentorSessions', (message: IMessage) => {
      try {
        const event: WebSocketEvent = JSON.parse(message.body)
        const handler = this.subscribers.get('mentorSessions')
        if (handler) {
          handler(event)
        }
      } catch (error) {
        console.error('Error parsing mentor session event:', error)
      }
    })
  }

  private handleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnection attempts reached')
      return
    }

    this.reconnectAttempts++
    const delay = Math.min(this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1), 30000)

    setTimeout(() => {
      const token = localStorage.getItem('jwt_token')
      this.connect(token || undefined).catch((error) => {
        console.error('Reconnection failed:', error)
      })
    }, delay)
  }

  subscribe(
    topic: 'students' | 'batches' | 'classes' | 'mentors' | 'mentorSessions',
    handler: (event: WebSocketEvent) => void
  ) {
    this.subscribers.set(topic, handler)
  }

  unsubscribe(topic: 'students' | 'batches' | 'classes' | 'mentors' | 'mentorSessions') {
    this.subscribers.delete(topic)
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate()
      this.client = null
    }
    this.subscribers.clear()
  }

  isConnected(): boolean {
    return this.client?.connected || false
  }
}

export const wsClient = new WebSocketClient()

