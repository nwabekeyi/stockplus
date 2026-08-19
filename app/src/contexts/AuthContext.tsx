import { createContext, useContext, useEffect, useState, ReactNode } from 'react'
import axios from 'axios'
import { apiGet } from '../services/api-client'
import { useAuthStore } from '../store/auth-store'
import type { User } from '../types'

interface AuthContextType {
  user: User | null
  login: (user: User) => void
  logout: () => Promise<void>
  isAuthenticated: boolean
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const user = useAuthStore((s) => s.user)
  const loginStore = useAuthStore((s) => s.login)
  const logoutStore = useAuthStore((s) => s.logout)

  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const initAuth = async () => {
      try {
        const data = await apiGet<User>('/auth/me')
        loginStore(data)
      } catch {
        logoutStore()
      } finally {
        setIsLoading(false)
      }
    }
    initAuth()
  }, [])

  const login = (userData: User) => {
    loginStore(userData)
  }

  const logout = async () => {
    const API_BASE = import.meta.env.VITE_API_URL || '/api/v1'
    try {
      await axios.post(`${API_BASE}/auth/logout`, {}, { withCredentials: true })
    } catch {}
    logoutStore()
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
