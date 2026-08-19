import axios from 'axios'
import { useAuthStore } from '../store/auth-store'
import type { ApiResponse } from '../types'

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
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        await axios.post(`${API_BASE}/auth/refresh`, {}, { withCredentials: true })
        return apiClient(originalRequest)
      } catch {
        try {
          await axios.post(`${API_BASE}/auth/logout`, {}, { withCredentials: true })
        } catch {}
        useAuthStore.getState().logout()
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
  return apiClient.get<ApiResponse<T>>(endpoint).then((res) => res.data.data)
}

export function apiPost<T>(endpoint: string, body: unknown): Promise<T> {
  return apiClient.post<ApiResponse<T>>(endpoint, body).then((res) => res.data.data)
}

export function apiPut<T>(endpoint: string, body: unknown): Promise<T> {
  return apiClient.put<ApiResponse<T>>(endpoint, body).then((res) => res.data.data)
}

export function apiUpload(
  endpoint: string,
  file: File,
  folder?: string,
  onProgress?: (progress: number) => void,
): Promise<{ url: string }> {
  const formData = new FormData()
  formData.append('file', file)
  if (folder) {
    formData.append('folder', folder)
  }

  return apiClient.post(
    endpoint,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total && onProgress) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(progress)
        }
      },
    },
  ).then(res => res.data.data)
}

export function apiDelete<T>(endpoint: string): Promise<T> {
  return apiClient.delete<ApiResponse<T>>(endpoint).then((res) => res.data.data)
}
