import { describe, it, expect, vi, beforeEach } from 'vitest'
import { authApi } from '../auth'
import axios from 'axios'

vi.mock('axios')
const mockedAxios = axios as any

describe('authApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('should successfully login and return token', async () => {
      const mockResponse = {
        data: {
          token: 'test-jwt-token',
          type: 'Bearer',
        },
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await authApi.login({
        username: 'admin@academy.com',
        password: 'password123',
      })

      expect(result).toEqual(mockResponse.data)
      expect(mockedAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/api/auth/login'),
        { username: 'admin@academy.com', password: 'password123' },
        expect.objectContaining({
          headers: { 'Content-Type': 'application/json' },
        })
      )
    })

    it('should handle login error with server response', async () => {
      const mockError = {
        response: {
          status: 401,
          data: { detail: 'Invalid credentials' },
        },
      }

      mockedAxios.post.mockRejectedValue(mockError)

      await expect(
        authApi.login({
          username: 'wrong@example.com',
          password: 'wrong',
        })
      ).rejects.toThrow('Invalid credentials')
    })

    it('should handle network error', async () => {
      const mockError = {
        request: {},
      }

      mockedAxios.post.mockRejectedValue(mockError)

      await expect(
        authApi.login({
          username: 'admin@academy.com',
          password: 'password123',
        })
      ).rejects.toThrow('Network error: Could not reach server')
    })

    it('should handle missing token in response', async () => {
      const mockResponse = {
        data: {
          type: 'Bearer',
        },
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      await expect(
        authApi.login({
          username: 'admin@academy.com',
          password: 'password123',
        })
      ).rejects.toThrow('Invalid response from server: missing token')
    })
  })

  describe('validateToken', () => {
    it('should successfully validate token', async () => {
      const mockResponse = {
        data: {
          valid: true,
          username: 'admin@academy.com',
        },
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await authApi.validateToken('test-token')

      expect(result).toEqual(mockResponse.data)
      expect(mockedAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/api/auth/validate'),
        { token: 'test-token' },
        expect.objectContaining({
          headers: { 'Content-Type': 'application/json' },
        })
      )
    })

    it('should handle validation error', async () => {
      const mockError = {
        response: {
          status: 400,
          data: { error: 'Invalid token' },
        },
      }

      mockedAxios.post.mockRejectedValue(mockError)

      await expect(authApi.validateToken('invalid-token')).rejects.toEqual(mockError)
    })
  })
})

