import { useEffect, useState } from 'react'
import { apiGet } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'

export interface StockMovement {
  id: string
  productId: string
  productName: string
  quantity: number
  movementType: string
  previousQuantity: number
  newQuantity: number
  reference?: string
  reason?: string
  createdAt: string
}

export default function StockMovementsPage() {
  const storeId = useStoreId()
  const [movements, setMovements] = useState<StockMovement[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!storeId) return
    apiGet<StockMovement[]>(`/stores/${storeId}/stock-movements`)
      .then(setMovements)
      .finally(() => setLoading(false))
  }, [storeId])

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-900">Stock Movements</h2>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Product</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Type</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Qty</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Previous</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">New</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Reference</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {movements.length === 0 ? (
                <tr><td colSpan={7} className="px-6 py-8 text-center text-gray-500">No movements recorded yet</td></tr>
              ) : movements.map((movement) => (
                <tr key={movement.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{movement.productName}</td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      movement.movementType === 'PURCHASE' ? 'bg-green-100 text-green-700' :
                      movement.movementType === 'SALE' ? 'bg-red-100 text-red-700' :
                      movement.movementType === 'TRANSFER' ? 'bg-blue-100 text-blue-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>{movement.movementType}</span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">{movement.quantity}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{movement.previousQuantity}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{movement.newQuantity}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{movement.reference || '-'}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{new Date(movement.createdAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
