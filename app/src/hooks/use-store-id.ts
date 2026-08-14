import { useAuth } from '../contexts/AuthContext'

export function useStoreId(): string | null {
  const { user } = useAuth()
  return user?.storeId || null
}