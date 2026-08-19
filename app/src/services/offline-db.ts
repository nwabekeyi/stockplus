import Dexie, { type Table } from 'dexie'
import type { SubscriptionPlan } from '../types'

export type WorkerRole = 'MANAGER' | 'CASHIER' | 'INVENTORY' | 'ACCOUNTANT' | 'VIEWER'
export type WorkerPermission = 'dashboard:view' | 'products:write' | 'sales:write' | 'purchases:write' | 'expenses:write' | 'reports:view' | 'workers:manage' | 'settings:manage'
export type PlanTier = 'FREE' | 'STARTER' | 'BUSINESS' | 'GROWTH' | 'ENTERPRISE'

export interface OfflineMutation {
  id?: number
  clientMutationId: string
  endpoint: string
  method: 'POST' | 'PUT' | 'DELETE'
  body?: unknown
  storeId?: string
  createdAt: string
  status: 'pending' | 'syncing' | 'failed'
  error?: string
}

export interface OfflineRecord<T = unknown> {
  id: string
  collection: string
  storeId?: string
  data: T
  updatedAt: string
}

export interface WorkerAccount {
  id: string
  ownerId: string
  storeId?: string
  firstName: string
  lastName: string
  email: string
  role: WorkerRole
  permissions: WorkerPermission[]
  createdAt: string
  synced: boolean
}

class StockPlusOfflineDb extends Dexie {
  mutations!: Table<OfflineMutation, number>
  records!: Table<OfflineRecord, string>
  plans!: Table<SubscriptionPlan, string>
  workers!: Table<WorkerAccount, string>

  constructor() {
    super('stockplus-offline')
    this.version(1).stores({
      mutations: '++id, status, storeId, createdAt',
      records: 'id, collection, storeId, updatedAt',
      plans: 'id, name, active, price',
      workers: 'id, ownerId, storeId, email, role, synced',
    })
  }
}

export const offlineDb = new StockPlusOfflineDb()

export const defaultSubscriptionPlans: SubscriptionPlan[] = [
  {
    id: 'free-offline', name: 'Free Offline', description: 'Frontend-only IndexedDB workspace. Data stays on this device until you upgrade.', price: 0, billingInterval: 'MONTHLY', maxProducts: 100, maxUsers: 1, maxBranches: 1, trialDays: 0, annualPrice: 0, heroPlan: false, whatsappEnabled: false, whatsappCommerceEnabled: false, whatsappCommerceCommissionPercent: 0, advancedReportsEnabled: false, apiEnabled: false, active: true, features: 'IndexedDB backup, offline POS, manual export, no cloud sync',
  },
  {
    id: 'starter-cloud', name: 'Starter', description: 'Cloud database sync for small shops with core inventory and POS.', price: 5000, billingInterval: 'MONTHLY', maxProducts: 500, maxUsers: 3, maxBranches: 1, trialDays: 14, annualPrice: 50000, heroPlan: false, whatsappEnabled: false, whatsappCommerceEnabled: false, whatsappCommerceCommissionPercent: 0, advancedReportsEnabled: false, apiEnabled: false, active: true, features: 'Cloud sync, backups, inventory, POS, basic reports',
  },
  {
    id: 'business-cloud', name: 'Business', description: 'Recommended cloud plan with staff permissions and advanced reports.', price: 12000, billingInterval: 'MONTHLY', maxProducts: 5000, maxUsers: 10, maxBranches: 3, trialDays: 14, annualPrice: 120000, heroPlan: true, whatsappEnabled: true, whatsappCommerceEnabled: true, whatsappCommerceCommissionPercent: 1.5, advancedReportsEnabled: true, apiEnabled: false, active: true, features: 'Cloud sync, staff permissions, advanced reports, WhatsApp commerce',
  },
]

export const workerPermissionPresets: Record<WorkerRole, WorkerPermission[]> = {
  MANAGER: ['dashboard:view', 'products:write', 'sales:write', 'purchases:write', 'expenses:write', 'reports:view', 'workers:manage', 'settings:manage'],
  CASHIER: ['dashboard:view', 'sales:write'],
  INVENTORY: ['dashboard:view', 'products:write', 'purchases:write'],
  ACCOUNTANT: ['dashboard:view', 'expenses:write', 'reports:view'],
  VIEWER: ['dashboard:view', 'reports:view'],
}

export function isFreePlan(planId?: string) {
  return !planId || planId === 'free-offline'
}

export async function getPlansWithFallback(fetcher: () => Promise<SubscriptionPlan[]>) {
  try {
    const plans = await fetcher()
    const usablePlans = plans.length > 0 ? plans : defaultSubscriptionPlans
    await offlineDb.plans.bulkPut(usablePlans)
    return usablePlans
  } catch {
    const cached = await offlineDb.plans.toArray()
    return cached.length > 0 ? cached : defaultSubscriptionPlans
  }
}

export async function queueOfflineMutation(mutation: Omit<OfflineMutation, 'createdAt' | 'status' | 'clientMutationId'> & { clientMutationId?: string }) {
  return offlineDb.mutations.add({ ...mutation, clientMutationId: mutation.clientMutationId || crypto.randomUUID(), createdAt: new Date().toISOString(), status: 'pending' })
}

export async function cacheRecord<T>(collection: string, id: string, data: T, storeId?: string) {
  await offlineDb.records.put({ id: `${collection}:${id}`, collection, storeId, data, updatedAt: new Date().toISOString() })
}
