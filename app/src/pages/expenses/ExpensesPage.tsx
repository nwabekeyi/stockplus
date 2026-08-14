import { useEffect, useState } from 'react'
import { apiGet, apiPost, apiDelete } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'
import { FiPlus } from 'react-icons/fi'
import AddExpenseModal from '../../components/expenses/AddExpenseModal'

export interface Expense {
  id: string
  category: string
  amount: number
  description?: string
  expenseDate: string
}

export default function ExpensesPage() {
  const storeId = useStoreId()
  const [expenses, setExpenses] = useState<Expense[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    if (!storeId) return
    apiGet<Expense[]>(`/stores/${storeId}/expenses`)
      .then(setExpenses)
      .finally(() => setLoading(false))
  }, [storeId])

  const handleAdd = async (expense: any) => {
    if (!storeId) return
    await apiPost(`/stores/${storeId}/expenses`, expense)
    const data = await apiGet<Expense[]>(`/stores/${storeId}/expenses`)
    setExpenses(data)
    setShowModal(false)
  }

  const handleDelete = async (id: string) => {
    if (!storeId) return
    if (!confirm('Delete this expense?')) return
    await apiDelete(`/stores/${storeId}/expenses/${id}`)
    const data = await apiGet<Expense[]>(`/stores/${storeId}/expenses`)
    setExpenses(data)
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  const totalExpenses = expenses.reduce((sum, e) => sum + e.amount, 0)

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Expenses</h2>
        <button onClick={() => setShowModal(true)} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2">
          <FiPlus size={18} /> Add Expense
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6">
        <p className="text-sm text-gray-500">Total Expenses</p>
        <p className="text-3xl font-bold text-gray-900">N{totalExpenses.toLocaleString()}</p>
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Category</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Amount</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Description</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700">Date</th>
                <th className="px-6 py-3 text-sm font-semibold text-gray-700 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {expenses.length === 0 ? (
                <tr><td colSpan={5} className="px-6 py-8 text-center text-gray-500">No expenses recorded yet</td></tr>
              ) : expenses.map((expense) => (
                <tr key={expense.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{expense.category}</td>
                  <td className="px-6 py-4 text-sm font-semibold text-red-600">N{expense.amount.toLocaleString()}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{expense.description || '-'}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{new Date(expense.expenseDate).toLocaleDateString()}</td>
                  <td className="px-6 py-4 text-right">
                    <button onClick={() => handleDelete(expense.id)} className="text-red-600 text-sm font-medium hover:underline">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <AddExpenseModal onClose={() => setShowModal(false)} onSave={handleAdd} />
      )}
    </div>
  )
}
