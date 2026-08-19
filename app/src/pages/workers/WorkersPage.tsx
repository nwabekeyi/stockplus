import { useEffect, useMemo, useState, type FormEvent } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { useAuth } from '../../contexts/AuthContext'
import { isFreePlan, offlineDb, workerPermissionPresets, type WorkerAccount, type WorkerPermission, type WorkerRole } from '../../services/offline-db'

const permissionLabels: Record<WorkerPermission, string> = {
  'dashboard:view': 'View dashboard',
  'products:write': 'Manage products',
  'sales:write': 'Create sales',
  'purchases:write': 'Manage purchases',
  'expenses:write': 'Record expenses',
  'reports:view': 'View reports',
  'workers:manage': 'Manage workers',
  'settings:manage': 'Manage settings',
}

type WorkerLimit = { maxUsers: number; usedUsers: number; remainingWorkers: number; canAddWorker: boolean; planName?: string }
type BackendWorker = Omit<WorkerAccount, 'ownerId' | 'synced'> & { userId: string; active: boolean }

const roles = Object.keys(workerPermissionPresets) as WorkerRole[]

export default function WorkersPage() {
  const { user } = useAuth()
  const [workers, setWorkers] = useState<WorkerAccount[]>([])
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', role: 'CASHIER' as WorkerRole })
  const [limits, setLimits] = useState<WorkerLimit | null>(null)
  const permissions = useMemo(() => workerPermissionPresets[form.role], [form.role])
  const isOwner = user?.hasStore && !user.workerRole
  const freePlan = isFreePlan(user?.planId)

  const loadWorkers = async () => {
    if (!user?.id) return
    const localWorkers = await offlineDb.workers.where('ownerId').equals(user.id).toArray()
    setWorkers(localWorkers)
    if (user.storeId && !freePlan) {
      apiGet<BackendWorker[]>(`/stores/${user.storeId}/workers`)
        .then((data) => setWorkers(data.map((worker) => ({ ...worker, ownerId: user.id, synced: true }))))
        .catch(() => undefined)
      apiGet<WorkerLimit>(`/stores/${user.storeId}/workers/limits`).then(setLimits).catch(() => undefined)
    } else {
      setLimits({ maxUsers: 1, usedUsers: 1, remainingWorkers: 0, canAddWorker: false, planName: 'Free Offline' })
    }
  }

  useEffect(() => { loadWorkers() }, [user?.id])

  const addWorker = async (event: FormEvent) => {
    event.preventDefault()
    if (!user?.id || !user.storeId || freePlan || limits?.canAddWorker === false) return
    const worker: WorkerAccount = {
      id: crypto.randomUUID(),
      ownerId: user.id,
      storeId: user.storeId,
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      role: form.role,
      permissions,
      createdAt: new Date().toISOString(),
      synced: false,
    }
    await offlineDb.workers.put(worker)
    await apiPost(`/stores/${user.storeId}/workers`, worker)
    setForm({ firstName: '', lastName: '', email: '', role: 'CASHIER' })
    loadWorkers()
  }

  if (!isOwner) {
    return <div className="rounded-3xl border border-amber-200 bg-amber-50 p-6 text-amber-800">Only business owners can add workers and assign permissions.</div>
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <section className="rounded-3xl bg-white p-6 shadow-sm border border-gray-200">
        <h1 className="text-2xl font-black text-gray-900">Workers & permissions</h1>
        <p className="mt-2 text-sm text-gray-500">Owners can invite workers by role only when their subscription includes staff seats. Free Offline workspaces are limited to the owner account.</p>
        {limits && <p className="mt-3 rounded-2xl bg-primary-50 px-4 py-3 text-sm font-bold text-primary-700">{limits.planName}: {limits.usedUsers}/{limits.maxUsers < 0 ? 'Unlimited' : limits.maxUsers} user seats used · {limits.maxUsers < 0 ? 'Unlimited' : limits.remainingWorkers} worker seats left</p>}
      </section>

      <form onSubmit={addWorker} className="grid gap-4 rounded-3xl bg-white p-6 shadow-sm border border-gray-200 md:grid-cols-2">
        <input required placeholder="First name" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} className="rounded-xl border border-gray-200 px-4 py-3 text-sm" />
        <input required placeholder="Last name" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} className="rounded-xl border border-gray-200 px-4 py-3 text-sm" />
        <input required type="email" placeholder="worker@example.com" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="rounded-xl border border-gray-200 px-4 py-3 text-sm" />
        <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value as WorkerRole })} className="rounded-xl border border-gray-200 px-4 py-3 text-sm">
          {roles.map((role) => <option key={role} value={role}>{role}</option>)}
        </select>
        <div className="md:col-span-2 rounded-2xl bg-gray-50 p-4">
          <p className="text-xs font-black uppercase tracking-widest text-gray-500">Permissions</p>
          <div className="mt-3 flex flex-wrap gap-2">{permissions.map((permission) => <span key={permission} className="rounded-full bg-primary-50 px-3 py-1 text-xs font-bold text-primary-700">{permissionLabels[permission]}</span>)}</div>
        </div>
        <button disabled={freePlan || limits?.canAddWorker === false} className="md:col-span-2 rounded-xl bg-primary-600 px-4 py-3 text-sm font-bold text-white disabled:opacity-50">{freePlan ? 'Upgrade to add workers' : limits?.canAddWorker === false ? 'Worker limit reached' : 'Add worker'}</button>
      </form>

      <section className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {workers.map((worker) => (
          <article key={worker.id} className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
            <h2 className="font-black text-gray-900">{worker.firstName} {worker.lastName}</h2>
            <p className="text-sm text-gray-500">{worker.email}</p>
            <p className="mt-3 text-xs font-black text-primary-700">{worker.role}</p>
            <p className="mt-2 text-xs text-gray-500">{worker.permissions.length} permissions · {worker.synced ? 'Synced' : 'Queued for sync'}</p>
          </article>
        ))}
      </section>
    </div>
  )
}
