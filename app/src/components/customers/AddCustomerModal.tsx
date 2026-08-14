import { useState } from 'react'
import { FiX } from 'react-icons/fi'

interface AddCustomerModalProps {
  customer: any
  onClose: () => void
  onSave: (customer: any) => void
}

export default function AddCustomerModal({ customer, onClose, onSave }: AddCustomerModalProps) {
  const [form, setForm] = useState({
    name: customer?.name || '',
    phone: customer?.phone || '',
    email: customer?.email || '',
    address: customer?.address || '',
    creditLimit: customer?.creditLimit || '0',
    status: customer?.status || 'ACTIVE',
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      await onSave({
        ...form,
        creditLimit: parseFloat(form.creditLimit) || 0,
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
          <h3 className="text-lg font-semibold">{customer ? 'Edit Customer' : 'Add Customer'}</h3>
          <button onClick={onClose} className="p-1 text-gray-400 hover:text-gray-600"><FiX size={24} /></button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
            <input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
            <textarea value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" rows={2} />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Credit Limit (N)</label>
            <input type="number" value={form.creditLimit} onChange={(e) => setForm({ ...form, creditLimit: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
          </div>

          <button type="submit" disabled={loading} className="w-full bg-primary-600 text-white py-3 rounded-lg font-semibold hover:bg-primary-700 disabled:opacity-50">
            {loading ? 'Saving...' : customer ? 'Update Customer' : 'Add Customer'}
          </button>
        </form>
      </div>
    </div>
  )
}
