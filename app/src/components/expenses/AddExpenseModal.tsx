import { useState } from 'react'
import { FiX } from 'react-icons/fi'

interface AddExpenseModalProps {
  onClose: () => void
  onSave: (expense: any) => void
}

export default function AddExpenseModal({ onClose, onSave }: AddExpenseModalProps) {
  const [form, setForm] = useState({
    category: 'RENT',
    amount: '',
    description: '',
    createdBy: '',
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      await onSave({
        ...form,
        amount: parseFloat(form.amount) || 0,
      })
      onClose()
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-lg max-h-[85vh] overflow-y-auto">
        <div className="flex justify-between items-center p-6 border-b">
          <h3 className="text-lg font-semibold">Add Expense</h3>
          <button onClick={onClose} className="p-1 text-gray-400 hover:text-gray-600"><FiX size={24} /></button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm">
              <option value="RENT">Rent</option>
              <option value="ELECTRICITY">Electricity</option>
              <option value="TRANSPORT">Transport</option>
              <option value="SALARY">Salary</option>
              <option value="INTERNET">Internet</option>
              <option value="REPAIRS">Repairs</option>
              <option value="PACKAGING">Packaging</option>
              <option value="DELIVERY">Delivery</option>
              <option value="FUEL">Fuel</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Amount (N)</label>
            <input type="number" value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" rows={2} />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Created By</label>
            <input value={form.createdBy} onChange={(e) => setForm({ ...form, createdBy: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
          </div>

          <button type="submit" disabled={loading} className="w-full bg-primary-600 text-white py-3 rounded-lg font-semibold hover:bg-primary-700 disabled:opacity-50">
            {loading ? 'Adding...' : 'Add Expense'}
          </button>
        </form>
      </div>
    </div>
  )
}
