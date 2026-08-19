import { useEffect, useState } from 'react'
import { apiGet } from '../../services/api-client'
import { DashboardStats, Sale, Customer } from '../../types'
import { useStoreId } from '../../hooks/use-store-id'
import { IconShoppingCart, IconPlus } from '../../components/common/icons'

export default function DashboardPage() {
  const storeId = useStoreId()
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [recentSales, setRecentSales] = useState<Sale[]>([])
  const [loading, setLoading] = useState(true)
  const [totalCustomerDebt, setTotalCustomerDebt] = useState(0)

  useEffect(() => {
    const fetchData = async () => {
      if (!storeId) return
      try {
        const [statsData, salesData, customersData] = await Promise.all([
          apiGet<DashboardStats>(`/stores/${storeId}/dashboard/stats`),
          apiGet<Sale[]>(`/stores/${storeId}/sales`),
          apiGet<Customer[]>(`/stores/${storeId}/customers`).catch(() => []),
        ])
        setStats(statsData)
        setRecentSales(salesData.slice(0, 5))
        const debt = (customersData as Customer[]).reduce((sum, c) => sum + c.outstandingBalance, 0)
        setTotalCustomerDebt(debt)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [storeId])

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] gap-4">
        <div className="w-10 h-10 border-4 border-gray-100 border-t-primary-600 rounded-full animate-spin shadow-sm" />
        <p className="text-sm font-semibold text-gray-500 animate-pulse">Loading workspace...</p>
      </div>
    )
  }

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
        <div className="bg-white rounded-2xl border border-gray-200/70 shadow-sm p-5 flex flex-col">
          <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-1.5">Today's Sales</p>
          <p className="text-2xl font-extrabold text-gray-900 tracking-tight leading-none">{(stats?.revenueToday || 0).toLocaleString()}</p>
          <p className="text-sm font-medium text-gray-500 mt-2">{stats?.totalSalesToday || 0} transactions</p>
        </div>
        <div className="bg-white rounded-2xl border border-gray-200/70 shadow-sm p-5 flex flex-col">
          <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-1.5">Products</p>
          <p className="text-2xl font-extrabold text-gray-900 tracking-tight leading-none">{stats?.totalProducts || 0}</p>
          <p className="text-sm font-medium text-gray-500 mt-2">{stats?.lowStockCount || 0} low stock</p>
        </div>
        <div className="bg-white rounded-2xl border border-gray-200/70 shadow-sm p-5 flex flex-col">
          <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-1.5">Customer Debt</p>
          <p className="text-2xl font-extrabold text-gray-900 tracking-tight leading-none">N{totalCustomerDebt.toLocaleString()}</p>
          <p className="text-sm font-medium text-gray-500 mt-2">Outstanding balance</p>
        </div>
        <div className="bg-white rounded-2xl border border-gray-200/70 shadow-sm p-5 flex flex-col">
          <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-1.5">This Month</p>
          <p className="text-2xl font-extrabold text-gray-900 tracking-tight leading-none">{(stats?.revenueThisMonth || 0).toLocaleString()}</p>
          <p className="text-sm font-medium text-gray-500 mt-2">{stats?.totalSalesThisMonth || 0} sales</p>
        </div>
        <div className="bg-white rounded-2xl border border-gray-200/70 shadow-sm p-5 flex flex-col">
          <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-1.5">Expenses Today</p>
          <p className="text-2xl font-extrabold text-gray-900 tracking-tight leading-none">N{(stats?.expensesToday || 0).toLocaleString()}</p>
          <p className="text-sm font-medium text-gray-500 mt-2">Tracked costs</p>
        </div>
      </div>

      <div className="space-y-5">
        <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
          <div>
            <h2 className="text-2xl font-extrabold text-gray-900 tracking-tight">Recent Sales</h2>
            <p className="text-sm font-medium text-gray-500 mt-1">Your latest transactions.</p>
          </div>
          <button 
            onClick={() => window.location.href = '/transactions'}
            className="bg-primary-500 text-white px-4 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-600 transition-all shadow-sm flex items-center gap-2 w-full sm:w-auto justify-center"
          >
            <IconPlus className="w-4 h-4" /> New Sale
          </button>
        </div>

        {recentSales.length === 0 ? (
          <div className="text-center py-20 px-6 bg-white rounded-3xl border border-gray-100 shadow-sm">
            <div className="w-20 h-20 rounded-full bg-gray-50/80 border border-gray-100 flex items-center justify-center mx-auto mb-5 shadow-sm">
              <IconShoppingCart className="w-8 h-8 text-gray-400" />
            </div>
            <h3 className="text-lg font-bold text-gray-900">No sales yet</h3>
            <p className="text-sm font-medium text-gray-500 mt-1.5 max-w-sm mx-auto">
              Record your first sale to start tracking your business performance.
            </p>
            <button 
              onClick={() => window.location.href = '/transactions'}
              className="mt-6 bg-primary-500 text-white px-6 py-3 rounded-xl text-sm font-medium hover:bg-primary-600 transition-all shadow-sm inline-flex items-center gap-2"
            >
              <IconPlus className="w-4 h-4" /> Record Sale
            </button>
          </div>
        ) : (
          <div className="bg-white rounded-2xl border border-gray-200/70 shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-gray-200/70 bg-gray-50/50">
                    <th className="text-left px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-widest whitespace-nowrap">Customer</th>
                    <th className="text-left px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-widest whitespace-nowrap">Date</th>
                    <th className="hidden md:table-cell text-left px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-widest whitespace-nowrap">Items</th>
                    <th className="text-right px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-widest whitespace-nowrap">Total</th>
                    <th className="hidden sm:table-cell text-right px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-widest whitespace-nowrap">Profit</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {recentSales.map((sale) => (
                    <tr key={sale.id} className="hover:bg-gray-50 transition-colors duration-200 group">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="font-bold text-gray-900 tracking-tight">{sale.customerName || 'Walk-in'}</span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-500">
                        {new Date(sale.saleDate).toLocaleDateString()}
                      </td>
                      <td className="hidden md:table-cell px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-500">
                        {sale.items.length} items
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-extrabold text-gray-900 tracking-tight text-right">
                        N{sale.totalAmount.toLocaleString()}
                      </td>
                      <td className="hidden sm:table-cell px-6 py-4 whitespace-nowrap text-sm font-medium text-green-600 text-right">
                        +N{sale.profit.toLocaleString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
