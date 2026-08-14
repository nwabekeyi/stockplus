import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { apiGet } from '../services/api-client'

interface User {
  id: string
  email: string
  firstName: string
  lastName: string
  role: string
  hasStore: boolean
  storeId?: string
  storeName?: string
  storeCurrency?: string
}

interface AuthContextType {
  user: User | null
  token: string | null
  login: (token: string, user: User) => void
  logout: () => void
  isAuthenticated: boolean
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(localStorage.getItem('accessToken'))
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const initAuth = async () => {
      const stored = localStorage.getItem('accessToken')
      if (stored) {
        try {
          const data = await apiGet<User>('/auth/me')
          setUser(data)
          setToken(stored)
        } catch {
          localStorage.removeItem('accessToken')
          setToken(null)
        }
      }
      setIsLoading(false)
    }
    initAuth()
  }, [])

  const login = (newToken: string, userData: User) => {
    localStorage.setItem('accessToken', newToken)
    setToken(newToken)
    setUser(userData)
  }

  const logout = () => {
    localStorage.removeItem('accessToken')
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!user, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}