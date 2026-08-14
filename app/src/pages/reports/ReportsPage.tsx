import { useEffect, useState } from 'react'
import { apiGet } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'
import { FiBarChart, FiDollarSign, FiPackage, FiTrendingUp } from 'react-icons/fi'

export interface FinancialSummary {
  totalSales: number
  totalCost: number
  grossProfit: number
  totalExpenses: number
  netProfit: number
  customerDebt: number
  supplierDebt: number
  date: string
}

export default function ReportsPage() {
  const storeId = useStoreId()
  const [financial, setFinancial] = useState<FinancialSummary | null>(null)
  const [loading, setLoading] = useState(true)
  const [startDate, setStartDate] = useState(new Date().toISOString().split('T')[0])
  const [endDate, setEndDate] = useState(new Date().toISOString().split('T')[0])

  useEffect(() => {
    if (!storeId) return
    setLoading(true)
    apiGet<FinancialSummary>(`/stores/${storeId}/reports/financial?start=${startDate}&end=${endDate}`)
      .then(setFinancial)
      .finally(() => setLoading(false))
  }, [storeId, startDate, endDate])

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-900">Financial Reports</h2>

      <div className="flex gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
          <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="px-3 py-2 rounded-lg border border-gray-300 text-sm" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">End Date</label>
          <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="px-3 py-2 rounded-lg border border-gray-300 text-sm" />
        </div>
      </div>

      {financial && (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-white rounded-xl shadow-sm p-5">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-green-100 rounded-lg"><FiDollarSign className="text-green-600" size={24} /></div>
                <div>
                  <p className="text-sm text-gray-500">Total Sales</p>
                  <p className="text-2xl font-bold text-gray-900">N{financial.totalSales.toLocaleString()}</p>
                </div>
              </div>
            </div>
            <div className="bg-white rounded-xl shadow-sm p-5">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-red-100 rounded-lg"><FiTrendingUp className="text-red-600" size={24} /></div>
                <div>
                  <p className="text-sm text-gray-500">Cost of Goods</p>
                  <p className="text-2xl font-bold text-gray-900">N{financial.totalCost.toLocaleString()}</p>
                </div>
              </div>
            </div>
            <div className="bg-white rounded-xl shadow-sm p-5">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-blue-100 rounded-lg"><FiBarChart className="text-blue-600" size={24} /></div>
                <div>
                  <p className="text-sm text-gray-500">Net Profit</p>
                  <p className="text-2xl font-bold text-gray-900">N{financial.netProfit.toLocaleString()}</p>
                </div>
              </div>
            </div>
            <div className="bg-white rounded-xl shadow-sm p-5">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-amber-100 rounded-lg"><FiPackage className="text-amber-600" size={24} /></div>
                <div>
                  <p className="text-sm text-gray-500">Expenses</p>
                  <p className="text-2xl font-bold text-gray-900">N{financial.totalExpenses.toLocaleString()}</p>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6 space-y-4">
            <h3 className="text-lg font-semibold text-gray-900">Profit & Loss</h3>
            <div className="space-y-2">
              <div className="flex justify-between py-2 border-b border-gray-100">
                <span className="text-gray-600">Gross Profit</span>
                <span className="font-semibold text-green-600">N{financial.grossProfit.toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-2 border-b border-gray-100">
                <span className="text-gray-600">Total Expenses</span>
                <span className="font-semibold text-red-600">-N{financial.totalExpenses.toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-gray-900 font-medium">Net Profit</span>
                <span className="font-bold text-primary-600">N{financial.netProfit.toLocaleString()}</span>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
