import axios from 'axios'
import { useAuthStore } from '../store/auth-store'
import type { ApiResponse } from '../types'
import { isFreePlan, queueOfflineMutation } from './offline-db'

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

function cloudSyncEnabled() {
  const user = useAuthStore.getState().user
  return user?.canUseCloudSync !== false && !isFreePlan(user?.planId)
}

export async function apiPost<T>(endpoint: string, body: unknown): Promise<T> {
  if (!cloudSyncEnabled() && !endpoint.startsWith('/auth') && !endpoint.startsWith('/subscriptions/plans')) {
    await queueOfflineMutation({ endpoint, method: 'POST', body, storeId: useAuthStore.getState().user?.storeId })
    return { offlineQueued: true } as T
  }

  try {
    return await apiClient.post<ApiResponse<T>>(endpoint, body).then((res) => res.data.data)
  } catch (error) {
    if (!navigator.onLine && !endpoint.startsWith('/auth')) {
      await queueOfflineMutation({ endpoint, method: 'POST', body, storeId: useAuthStore.getState().user?.storeId })
      return { offlineQueued: true } as T
    }
    throw error
  }
}

export async function apiPut<T>(endpoint: string, body: unknown): Promise<T> {
  if (!cloudSyncEnabled()) {
    await queueOfflineMutation({ endpoint, method: 'PUT', body, storeId: useAuthStore.getState().user?.storeId })
    return { offlineQueued: true } as T
  }
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

export async function apiDelete<T>(endpoint: string): Promise<T> {
  if (!cloudSyncEnabled()) {
    await queueOfflineMutation({ endpoint, method: 'DELETE', storeId: useAuthStore.getState().user?.storeId })
    return { offlineQueued: true } as T
  }
  return apiClient.delete<ApiResponse<T>>(endpoint).then((res) => res.data.data)
}

export async function syncOfflineQueue() {
  if (!navigator.onLine || !cloudSyncEnabled()) return
  const { offlineDb } = await import('./offline-db')
  const user = useAuthStore.getState().user
  if (!user?.storeId) return
  const pending = await offlineDb.mutations.where('status').equals('pending').toArray()
  if (pending.length === 0) return

  for (const mutation of pending) {
    if (mutation.id) await offlineDb.mutations.update(mutation.id, { status: 'syncing' })
  }

  try {
    const response = await apiClient.post<ApiResponse<{ results: { clientMutationId: string; status: string; error?: string }[] }>>(`/offline-sync/${user.storeId}`, {
      mutations: pending.map((mutation) => ({
        clientMutationId: mutation.clientMutationId,
        method: mutation.method,
        endpoint: mutation.endpoint,
        body: mutation.body,
      })),
    })

    const results = response.data.data.results
    for (const mutation of pending) {
      if (!mutation.id) continue
      const result = results.find((item) => item.clientMutationId === mutation.clientMutationId)
      if (result?.status === 'accepted' || result?.status === 'duplicate') {
        await offlineDb.mutations.delete(mutation.id)
      } else {
        await offlineDb.mutations.update(mutation.id, { status: 'failed', error: result?.error || 'Sync failed' })
      }
    }
  } catch (error) {
    for (const mutation of pending) {
      if (mutation.id) {
        await offlineDb.mutations.update(mutation.id, { status: 'pending', error: error instanceof Error ? error.message : 'Sync failed' })
      }
    }
  }
}
