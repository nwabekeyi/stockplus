import { useEffect, useState } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { Notification } from '../../types'
import { useStoreId } from '../../hooks/use-store-id'

export default function NotificationsPage() {
  const storeId = useStoreId()
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [title, setTitle] = useState('Low stock reminder')
  const [message, setMessage] = useState('Review products below reorder level today.')
  const [channel, setChannel] = useState<Notification['channel']>('DASHBOARD')

  const load = async () => {
    if (!storeId) return
    setNotifications(await apiGet<Notification[]>(`/stores/${storeId}/notifications`).catch(() => []))
  }

  useEffect(() => { load() }, [storeId])

  const create = async () => {
    if (!storeId) return
    await apiPost(`/stores/${storeId}/notifications`, { title, message, channel })
    await load()
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-extrabold text-gray-900">Notifications & Alerts</h2>
        <p className="text-sm font-medium text-gray-500">Manage dashboard, WhatsApp, email and push-ready alerts for stock, expiry and debt workflows.</p>
      </div>
      <div className="grid gap-4 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm md:grid-cols-4">
        <input value={title} onChange={(e) => setTitle(e.target.value)} className="rounded-xl border border-gray-200 px-3 py-2 text-sm" />
        <input value={message} onChange={(e) => setMessage(e.target.value)} className="rounded-xl border border-gray-200 px-3 py-2 text-sm md:col-span-2" />
        <select value={channel} onChange={(e) => setChannel(e.target.value as Notification['channel'])} className="rounded-xl border border-gray-200 px-3 py-2 text-sm">
          {['DASHBOARD', 'EMAIL', 'PUSH', 'WHATSAPP'].map((item) => <option key={item}>{item}</option>)}
        </select>
        <button onClick={create} className="rounded-xl bg-primary-600 px-4 py-2 text-sm font-bold text-white md:col-start-4">Create alert</button>
      </div>
      <div className="grid gap-3 md:grid-cols-2">
        {notifications.map((notification) => (
          <article key={notification.id} className="rounded-2xl border border-gray-200 bg-white p-4 shadow-sm">
            <div className="flex items-center justify-between gap-3"><h3 className="font-bold text-gray-900">{notification.title}</h3><span className="rounded-full bg-gray-100 px-2 py-1 text-xs font-bold text-gray-600">{notification.channel}</span></div>
            <p className="mt-2 text-sm text-gray-500">{notification.message}</p>
            <p className="mt-3 text-xs font-semibold text-gray-400">{notification.status} · {new Date(notification.createdAt).toLocaleString()}</p>
          </article>
        ))}
      </div>
    </div>
  )
}
