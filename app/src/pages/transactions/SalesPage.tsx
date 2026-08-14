import { useEffect, useState } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { Sale } from '../../types'
import { useStoreId } from '../../hooks/use-store-id'
import { FiPlus, FiSearch } from 'react-icons/fi'
import AddSaleModal from '../../components/transactions/AddSaleModal'

export default function SalesPage() {
  const storeId = useStoreId()
  const [sales, setSales] = useState<Sale[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [search, setSearch] = useState('')

  useEffect(() => {
    if (!storeId) return
    fetchSales()
  }, [storeId])

  const fetchSales = async () => {
    if (!storeId) return
    try {
      const data = await apiGet<Sale[]>(`/stores/${storeId}/sales`)
      setSales(data)
    } finally {
      setLoading(false)
    }
  }

  const filteredSales = sales.filter((sale) =>
    sale.customerName?.toLowerCase().includes(search.toLowerCase()) ||
    sale.customerPhone?.includes(search)
  )

  const handleAddSale = async (sale: any) => {
    if (!storeId) return
    await apiPost(`/stores/${storeId}/sales`, sale)
    fetchSales()
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-4 md:space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
        <h2 className="text-xl font-bold text-gray-900">Sales</h2>
        <button
          onClick={() => setShowModal(true)}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2 text-sm md:text-base"
        >
          <FiPlus size={18} />
          <span className="hidden sm:inline">New Sale</span>
          <span className="sm:hidden">New</span>
        </button>
      </div>

      <div className="relative max-w-md">
        <FiSearch className="absolute left-3 top-3 text-gray-400" size={18} />
        <input
          type="text"
          placeholder="Search sales..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
        />
      </div>

      {filteredSales.length === 0 ? (
          <div className="bg-white rounded-xl shadow-sm p-8 md:p-12 text-center">
            <p className="text-base text-gray-500 mb-3">No sales recorded yet</p>
            <button onClick={() => setShowModal(true)} className="text-primary-600 text-sm font-semibold">
              Record your first sale
            </button>
          </div>
        ) : (
          <div className="bg-white rounded-xl shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-4 md:px-6 py-3 text-sm font-semibold text-gray-700">Customer</th>
                    <th className="px-4 md:px-6 py-3 text-sm font-semibold text-gray-700">Date</th>
                    <th className="hidden md:table-cell px-6 py-3 text-sm font-semibold text-gray-700">Items</th>
                    <th className="px-4 md:px-6 py-3 text-sm font-semibold text-gray-700 text-right">Total</th>
                    <th className="hidden sm:table-cell px-6 py-3 text-sm font-semibold text-gray-700 text-right">Profit</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {filteredSales.map((sale) => (
                    <tr key={sale.id} className="hover:bg-gray-50">
                      <td className="px-4 md:px-6 py-4">
                        <p className="text-sm font-medium text-gray-900">{sale.customerName || 'Walk-in Customer'}</p>
                      </td>
                      <td className="px-4 md:px-6 py-4 text-sm text-gray-500">{new Date(sale.saleDate).toLocaleDateString()}</td>
                      <td className="hidden md:table-cell px-6 py-4 text-sm text-gray-500">{sale.items.length} items</td>
                      <td className="px-4 md:px-6 py-4 text-sm font-semibold text-gray-900 text-right">{sale.totalAmount.toLocaleString()}</td>
                      <td className="hidden sm:table-cell px-6 py-4 text-sm text-green-600 text-right">+{sale.profit.toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

      {showModal && storeId && (
        <AddSaleModal
          onClose={() => setShowModal(false)}
          onSave={handleAddSale}
        />
      )}
    </div>
  )
}
