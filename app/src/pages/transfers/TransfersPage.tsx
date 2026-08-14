import { useEffect, useState } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'

export interface Transfer {
  id: string
  reference: string
  fromStoreId: string
  toStoreId: string
  productId: string
  productName: string
  quantity: number
  status: string
  notes?: string
  createdAt: string
  receivedAt?: string
}

export default function TransfersPage() {
  const storeId = useStoreId()
  const [transfers, setTransfers] = useState<Transfer[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!storeId) return
    apiGet<Transfer[]>(`/stores/${storeId}/transfers`)
      .then(setTransfers)
      .finally(() => setLoading(false))
  }, [storeId])

  const handleReceive = async (id: string) => {
    await apiPost(`/transfers/${id}/receive`, {})
    const data = await apiGet<Transfer[]>(`/stores/${storeId}/transfers`)
    setTransfers(data)
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-900">Stock Transfers</h2>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Reference</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Product</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">From</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">To</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Qty</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Status</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {transfers.length === 0 ? (
                <tr><td colSpan={7} className="px-6 py-8 text-center text-gray-500">No transfers yet</td></tr>
              ) : transfers.map((transfer) => (
                <tr key={transfer.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{transfer.reference}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{transfer.productName}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{transfer.fromStoreId === storeId ? 'This Store' : transfer.fromStoreId}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{transfer.toStoreId === storeId ? 'This Store' : transfer.toStoreId}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{transfer.quantity}</td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      transfer.status === 'RECEIVED' ? 'bg-green-100 text-green-700' :
                      transfer.status === 'PENDING' ? 'bg-amber-100 text-amber-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>{transfer.status}</span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    {transfer.toStoreId === storeId && transfer.status !== 'RECEIVED' && (
                      <button onClick={() => handleReceive(transfer.id)} className="text-primary-600 text-sm font-medium hover:underline">Receive</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
