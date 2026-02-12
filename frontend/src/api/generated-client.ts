import axios, { AxiosInstance, AxiosRequestConfig } from 'axios'
import { AcademyApi } from '../generated'

// Use relative URL when running behind nginx proxy, or absolute URL for direct backend access
const getApiUrl = () => {
  const envUrl = import.meta.env.VITE_API_URL
  if (envUrl) {
    // If VITE_API_URL is set, use it (but remove /api suffix if present since we add it)
    return envUrl.replace(/\/api\/?$/, '')
  }
  // For development, check if we're on localhost (nginx proxy) or direct backend
  if (typeof window !== 'undefined' && window.location.origin === 'http://localhost') {
    return '' // Use relative URLs for nginx proxy
  }
  return 'http://localhost:8080' // Direct backend access
}

const API_URL = getApiUrl()

// Get base URL for API calls (used by both axios and AcademyApi)
const apiBaseURL = API_URL ? `${API_URL}/api` : '/api'

// Create axios instance with interceptors
const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: apiBaseURL,
    headers: {
      'Content-Type': 'application/json',
    },
  })

  // Request interceptor to add JWT token
  instance.interceptors.request.use(
    (config: any) => {
      const token = localStorage.getItem('jwt_token')
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error: any) => {
      return Promise.reject(error)
    }
  )

  // Response interceptor for error handling
  instance.interceptors.response.use(
    (response: any) => response,
    (error: any) => {
      if (error.response?.status === 401) {
        // Unauthorized - clear token and redirect to login
        localStorage.removeItem('jwt_token')
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
  )

  return instance
}

// Create the API client instance
const axiosInstance = createAxiosInstance()

// Create AcademyApi instance with our configured axios instance
const baseApiClient = new AcademyApi(
  {} as AxiosRequestConfig, // Config object (not used when passing axios instance)
  apiBaseURL, // Base URL
  axiosInstance // Our configured axios instance
)

// Override ALL placeholder methods to make real HTTP calls
// Check if placeholder methods are being used (they contain 'Promise.resolve')
const isPlaceholder = baseApiClient.getAllStudents.toString().includes('Promise.resolve')

if (isPlaceholder) {
  console.log('⚠️ Using placeholder API client - overriding all methods with real HTTP calls')
  
  // Students API
  baseApiClient.getAllStudents = async (batchId?: number) => {
    const params = batchId ? { batchId } : {}
    console.log('🌐 API Call: GET /students', params)
    const response = await axiosInstance.get('/students', { params })
    console.log('✅ API Response: GET /students', response.data)
    return { data: response.data }
  }

  baseApiClient.getAllStudentsPaged = async (page = 0, size = 20, sort?: string) => {
    const params: any = { page, size }
    if (sort) params.sort = sort
    console.log('🌐 API Call: GET /students/paged', params)
    const response = await axiosInstance.get('/students/paged', { params })
    console.log('✅ API Response: GET /students/paged', response.data)
    return { data: response.data }
  }

  baseApiClient.getStudentById = async (id: number) => {
    console.log('🌐 API Call: GET /students/' + id)
    const response = await axiosInstance.get(`/students/${id}`)
    console.log('✅ API Response: GET /students/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.createStudent = async (student: any) => {
    console.log('🌐 API Call: POST /students', student)
    const response = await axiosInstance.post('/students', student)
    console.log('✅ API Response: POST /students', response.data)
    return { data: response.data }
  }

  baseApiClient.updateStudent = async (id: number, student: any) => {
    console.log('🌐 API Call: PUT /students/' + id, student)
    const response = await axiosInstance.put(`/students/${id}`, student)
    console.log('✅ API Response: PUT /students/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.deleteStudent = async (id: number) => {
    console.log('🌐 API Call: DELETE /students/' + id)
    await axiosInstance.delete(`/students/${id}`)
    console.log('✅ API Response: DELETE /students/' + id)
    return {}
  }

  // Batches API
  baseApiClient.getAllBatches = async (page = 0, size = 20, sort?: string) => {
    const params: any = { page, size }
    if (sort) params.sort = sort
    console.log('🌐 API Call: GET /batches', params)
    const response = await axiosInstance.get('/batches', { params })
    console.log('✅ API Response: GET /batches', response.data)
    return { data: response.data }
  }

  baseApiClient.getBatchById = async (id: number) => {
    console.log('🌐 API Call: GET /batches/' + id)
    const response = await axiosInstance.get(`/batches/${id}`)
    console.log('✅ API Response: GET /batches/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.createBatch = async (batch: any) => {
    console.log('🌐 API Call: POST /batches', batch)
    const response = await axiosInstance.post('/batches', batch)
    console.log('✅ API Response: POST /batches', response.data)
    return { data: response.data }
  }

  baseApiClient.updateBatch = async (id: number, batch: any) => {
    console.log('🌐 API Call: PUT /batches/' + id, batch)
    const response = await axiosInstance.put(`/batches/${id}`, batch)
    console.log('✅ API Response: PUT /batches/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.deleteBatch = async (id: number) => {
    console.log('🌐 API Call: DELETE /batches/' + id)
    await axiosInstance.delete(`/batches/${id}`)
    console.log('✅ API Response: DELETE /batches/' + id)
    return {}
  }

  baseApiClient.assignClassToBatch = async (id: number, classId: number) => {
    console.log('🌐 API Call: POST /batches/' + id + '/classes/' + classId)
    const response = await axiosInstance.post(`/batches/${id}/classes/${classId}`)
    console.log('✅ API Response: POST /batches/' + id + '/classes/' + classId, response.data)
    return { data: response.data }
  }

  // Classes API
  baseApiClient.getAllClasses = async () => {
    console.log('🌐 API Call: GET /classes')
    const response = await axiosInstance.get('/classes')
    console.log('✅ API Response: GET /classes', response.data)
    return { data: response.data }
  }

  baseApiClient.getClassById = async (id: number) => {
    console.log('🌐 API Call: GET /classes/' + id)
    const response = await axiosInstance.get(`/classes/${id}`)
    console.log('✅ API Response: GET /classes/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.createClass = async (classData: any) => {
    console.log('🌐 API Call: POST /classes', classData)
    const response = await axiosInstance.post('/classes', classData)
    console.log('✅ API Response: POST /classes', response.data)
    return { data: response.data }
  }

  baseApiClient.updateClass = async (id: number, classData: any) => {
    console.log('🌐 API Call: PUT /classes/' + id, classData)
    const response = await axiosInstance.put(`/classes/${id}`, classData)
    console.log('✅ API Response: PUT /classes/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.deleteClass = async (id: number) => {
    console.log('🌐 API Call: DELETE /classes/' + id)
    await axiosInstance.delete(`/classes/${id}`)
    console.log('✅ API Response: DELETE /classes/' + id)
    return {}
  }

  // Mentors API
  baseApiClient.getAllMentors = async () => {
    console.log('🌐 API Call: GET /mentors')
    const response = await axiosInstance.get('/mentors')
    console.log('✅ API Response: GET /mentors', response.data)
    return { data: response.data }
  }

  baseApiClient.getMentorById = async (id: number) => {
    console.log('🌐 API Call: GET /mentors/' + id)
    const response = await axiosInstance.get(`/mentors/${id}`)
    console.log('✅ API Response: GET /mentors/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.createMentor = async (mentor: any) => {
    console.log('🌐 API Call: POST /mentors', mentor)
    const response = await axiosInstance.post('/mentors', mentor)
    console.log('✅ API Response: POST /mentors', response.data)
    return { data: response.data }
  }

  baseApiClient.updateMentor = async (id: number, mentor: any) => {
    console.log('🌐 API Call: PUT /mentors/' + id, mentor)
    const response = await axiosInstance.put(`/mentors/${id}`, mentor)
    console.log('✅ API Response: PUT /mentors/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.deleteMentor = async (id: number) => {
    console.log('🌐 API Call: DELETE /mentors/' + id)
    await axiosInstance.delete(`/mentors/${id}`)
    console.log('✅ API Response: DELETE /mentors/' + id)
    return {}
  }

  // Mentor Sessions API
  baseApiClient.getAllMentorSessions = async () => {
    console.log('🌐 API Call: GET /mentor-sessions')
    const response = await axiosInstance.get('/mentor-sessions')
    console.log('✅ API Response: GET /mentor-sessions', response.data)
    return { data: response.data }
  }

  baseApiClient.getMentorSessionById = async (id: number) => {
    console.log('🌐 API Call: GET /mentor-sessions/' + id)
    const response = await axiosInstance.get(`/mentor-sessions/${id}`)
    console.log('✅ API Response: GET /mentor-sessions/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.createMentorSession = async (session: any) => {
    console.log('🌐 API Call: POST /mentor-sessions', session)
    const response = await axiosInstance.post('/mentor-sessions', session)
    console.log('✅ API Response: POST /mentor-sessions', response.data)
    return { data: response.data }
  }

  baseApiClient.updateMentorSession = async (id: number, session: any) => {
    console.log('🌐 API Call: PUT /mentor-sessions/' + id, session)
    const response = await axiosInstance.put(`/mentor-sessions/${id}`, session)
    console.log('✅ API Response: PUT /mentor-sessions/' + id, response.data)
    return { data: response.data }
  }

  baseApiClient.deleteMentorSession = async (id: number) => {
    console.log('🌐 API Call: DELETE /mentor-sessions/' + id)
    await axiosInstance.delete(`/mentor-sessions/${id}`)
    console.log('✅ API Response: DELETE /mentor-sessions/' + id)
    return {}
  }

  // Auth API
  baseApiClient.login = async (credentials: any) => {
    console.log('🌐 API Call: POST /auth/login')
    const response = await axiosInstance.post('/auth/login', credentials)
    console.log('✅ API Response: POST /auth/login', response.data)
    return { data: response.data }
  }

  baseApiClient.validateToken = async (request: any) => {
    console.log('🌐 API Call: POST /auth/validate')
    const response = await axiosInstance.post('/auth/validate', request)
    console.log('✅ API Response: POST /auth/validate', response.data)
    return { data: response.data }
  }
}

export const apiClient = baseApiClient

export default apiClient
