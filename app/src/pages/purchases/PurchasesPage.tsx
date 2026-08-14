import { useEffect, useState } from 'react'
import { apiGet, apiPost, apiDelete } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'
import { FiPlus } from 'react-icons/fi'
import AddPurchaseModal from '../../components/purchases/AddPurchaseModal'

export interface Purchase {
  id: string
  reference: string
  supplierId?: string
  totalAmount: number
  amountPaid: number
  outstanding: number
  status: string
  purchaseDate: string
  items: { productName: string; quantity: number; costPrice: number; subtotal: number }[]
}

export default function PurchasesPage() {
  const storeId = useStoreId()
  const [purchases, setPurchases] = useState<Purchase[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    if (!storeId) return
    apiGet<Purchase[]>(`/stores/${storeId}/purchases`)
      .then(setPurchases)
      .finally(() => setLoading(false))
  }, [storeId])

  const handleAdd = async (purchase: any) => {
    if (!storeId) return
    await apiPost(`/stores/${storeId}/purchases`, purchase)
    const data = await apiGet<Purchase[]>(`/stores/${storeId}/purchases`)
    setPurchases(data)
  }

  const handleDelete = async (id: string) => {
    if (!storeId) return
    if (!confirm('Delete this purchase?')) return
    await apiDelete(`/stores/${storeId}/purchases/${id}`)
    const data = await apiGet<Purchase[]>(`/stores/${storeId}/purchases`)
    setPurchases(data)
  }

  const handleReceive = async (id: string) => {
    if (!storeId) return
    await apiPost(`/stores/${storeId}/purchases/${id}/receive`, {})
    const data = await apiGet<Purchase[]>(`/stores/${storeId}/purchases`)
    setPurchases(data)
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Purchases</h2>
        <button onClick={() => setShowModal(true)} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2">
          <FiPlus size={18} /> New Purchase
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Reference</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Date</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Total</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Paid</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Outstanding</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Status</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {purchases.length === 0 ? (
                <tr><td colSpan={7} className="px-6 py-8 text-center text-gray-500">No purchases yet</td></tr>
              ) : purchases.map((purchase) => (
                <tr key={purchase.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{purchase.reference}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{new Date(purchase.purchaseDate).toLocaleDateString()}</td>
                  <td className="px-6 py-4 text-sm font-semibold text-gray-900">{purchase.totalAmount.toLocaleString()}</td>
                  <td className="px-6 py-4 text-sm text-green-600">{purchase.amountPaid.toLocaleString()}</td>
                  <td className="px-6 py-4 text-sm text-red-600">{purchase.outstanding.toLocaleString()}</td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      purchase.status === 'RECEIVED' ? 'bg-green-100 text-green-700' :
                      purchase.status === 'PENDING' ? 'bg-amber-100 text-amber-700' :
                      'bg-red-100 text-red-700'
                    }`}>{purchase.status}</span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    {purchase.status !== 'RECEIVED' && (
                      <button onClick={() => handleReceive(purchase.id)} className="text-primary-600 text-sm font-medium mr-3 hover:underline">Receive</button>
                    )}
                    <button onClick={() => handleDelete(purchase.id)} className="text-red-600 text-sm font-medium hover:underline">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && storeId && (
        <AddPurchaseModal storeId={storeId} onClose={() => setShowModal(false)} onSave={handleAdd} />
      )}
    </div>
  )
}
