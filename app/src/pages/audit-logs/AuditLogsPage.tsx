import { useEffect, useState } from 'react'
import { apiGet } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'

export interface AuditLog {
  id: string
  userId: string
  userName: string
  action: string
  entityType: string
  entityId: string
  oldValue?: string
  newValue?: string
  createdAt: string
}

export default function AuditLogsPage() {
  const storeId = useStoreId()
  const [logs, setLogs] = useState<AuditLog[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!storeId) return
    apiGet<AuditLog[]>(`/stores/${storeId}/audit-logs`)
      .then(setLogs)
      .finally(() => setLoading(false))
  }, [storeId])

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-900">Audit Logs</h2>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">User</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Action</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Entity</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Old Value</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">New Value</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {logs.length === 0 ? (
                <tr><td colSpan={6} className="px-6 py-8 text-center text-gray-500">No audit logs yet</td></tr>
              ) : logs.map((log) => (
                <tr key={log.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{log.userName}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{log.action}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{log.entityType} ({log.entityId})</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{log.oldValue || '-'}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{log.newValue || '-'}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{new Date(log.createdAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
