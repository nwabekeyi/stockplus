import axios from 'axios'
import { useAuthStore } from '../store/auth-store'

const API_BASE = import.meta.env.VITE_API_URL || '/api/v1'

export class ApiError extends Error {
  errors: string[]
  constructor(message: string, errors: string[] = []) {
    super(errors.length > 0 ? errors.join(', ') : message)
    this.errors = errors
    this.name = 'ApiError'
  }
}

export const apiClient = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token || localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const { data } = await axios.post(
            `${API_BASE}/auth/refresh`,
            {},
            {
              headers: {
                'X-Refresh-Token': refreshToken,
              },
            }
          )

          const newToken = data.data.accessToken
          useAuthStore.getState().login(newToken, useAuthStore.getState().user!)

          originalRequest.headers.Authorization = `Bearer ${newToken}`
          return apiClient(originalRequest)
        } catch {
          useAuthStore.getState().logout()
          window.location.href = '/login'
        }
      }
    }

    if (error.response?.data) {
      const apiError = new ApiError(
        error.response.data.message || 'Request failed',
        error.response.data.error || []
      )
      return Promise.reject(apiError)
    }

    return Promise.reject(new ApiError(error.message || 'Request failed'))
  }
)

export function apiGet<T>(endpoint: string): Promise<T> {
  return apiClient.get<T>(endpoint).then((res) => res.data.data)
}

export function apiPost<T>(endpoint: string, body: unknown): Promise<T> {
  return apiClient.post<T>(endpoint, body).then((res) => res.data.data)
}

export function apiPut<T>(endpoint: string, body: unknown): Promise<T> {
  return apiClient.put<T>(endpoint, body).then((res) => res.data.data)
}

export function apiDelete<T>(endpoint: string): Promise<T> {
  return apiClient.delete<T>(endpoint).then((res) => res.data.data)
}
