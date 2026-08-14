import { useEffect, useState } from 'react'
import { apiGet } from '../../services/api-client'
import { DashboardStats, Sale, Subscription, Customer, Product } from '../../types'
import { useStoreId } from '../../hooks/use-store-id'
import { IconShoppingCart, IconAlertTriangle, IconPlus } from '../../components/common/icons'

export default function DashboardPage() {
  const storeId = useStoreId()
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [recentSales, setRecentSales] = useState<Sale[]>([])
  const [subscription, setSubscription] = useState<Subscription | null>(null)
  const [loading, setLoading] = useState(true)
  const [lowStockProducts, setLowStockProducts] = useState<Product[]>([])
  const [totalCustomerDebt, setTotalCustomerDebt] = useState(0)

  useEffect(() => {
    const fetchData = async () => {
      if (!storeId) return
      try {
        const [statsData, salesData, subData, customersData, productsData] = await Promise.all([
          apiGet<DashboardStats>(`/stores/${storeId}/dashboard/stats`),
          apiGet<Sale[]>(`/stores/${storeId}/sales`),
          apiGet<Subscription>(`/subscriptions/current?storeId=${storeId}`).catch(() => null),
          apiGet<Customer[]>(`/stores/${storeId}/customers`).catch(() => []),
          apiGet<Product[]>(`/stores/${storeId}/products`).catch(() => []),
        ])
        setStats(statsData)
        setRecentSales(salesData.slice(0, 5))
        setSubscription(subData)
        const debt = (customersData as Customer[]).reduce((sum, c) => sum + c.outstandingBalance, 0)
        setTotalCustomerDebt(debt)
        setLowStockProducts((productsData as Product[]).filter(p => p.stock && p.stock.quantity <= p.stock.lowStockThreshold).slice(0, 5))
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
      
      <div className="space-y-4">
        {subscription && subscription.status !== 'ACTIVE' && (
          <div className="bg-gradient-to-r from-amber-50 to-amber-100/40 border border-amber-200/60 rounded-2xl p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 shadow-sm">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-amber-100 flex items-center justify-center shrink-0 shadow-inner">
                <IconAlertTriangle className="w-6 h-6 text-amber-600" />
              </div>
              <div>
                <p className="text-base font-bold text-amber-900 tracking-tight">Subscription required</p>
                <p className="text-sm font-medium text-amber-700/80 mt-0.5">Subscribe to a plan to access all features.</p>
              </div>
            </div>
          </div>
        )}
      </div>

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

      {lowStockProducts.length > 0 && (
        <div className="bg-white rounded-2xl border border-amber-200/70 shadow-sm p-6">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center shrink-0">
              <IconAlertTriangle className="w-5 h-5 text-amber-600" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-gray-900 tracking-tight">Low Stock Alert</h3>
              <p className="text-sm font-medium text-gray-500">These products are running low.</p>
            </div>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {lowStockProducts.map((product) => (
              <div key={product.id} className="flex items-center justify-between p-3 rounded-xl bg-gray-50/80 border border-gray-100">
                <div>
                  <p className="text-sm font-bold text-gray-900">{product.name}</p>
                  <p className="text-xs text-gray-500">SKU: {product.sku}</p>
                </div>
                <div className="text-right">
                  <span className="text-sm font-bold text-amber-600">{product.stock?.quantity} {product.stock?.unit}</span>
                  <p className="text-xs text-gray-500">Min: {product.stock?.lowStockThreshold}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {subscription && (
        <div className="bg-white rounded-2xl border border-gray-200/70 shadow-sm p-6">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-1">Subscription</p>
              <p className="text-sm font-medium text-gray-900">{subscription.plan.name}</p>
              <p className="text-xs text-gray-500">Renews {new Date(subscription.endDate).toLocaleDateString()}</p>
            </div>
            <span className={`px-2 py-1 rounded-full text-xs font-medium ${
              subscription.status === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'
            }`}>
              {subscription.status}
            </span>
          </div>
        </div>
      )}
    </div>
  )
}
