import { useState, useEffect } from 'react'
import { FiX, FiPlus } from 'react-icons/fi'
import { apiGet } from '../../services/api-client'
import { Product } from '../../types'

interface AddPurchaseModalProps {
  storeId: string
  onClose: () => void
  onSave: (purchase: any) => void
}

export default function AddPurchaseModal({ storeId, onClose, onSave }: AddPurchaseModalProps) {
  const [products, setProducts] = useState<Product[]>([])
  const [reference, setReference] = useState('')
  const [items, setItems] = useState<any[]>([])
  const [amountPaid, setAmountPaid] = useState('0')
  const [notes, setNotes] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    apiGet<Product[]>(`/stores/${storeId}/products`).then(setProducts).catch(() => {})
  }, [storeId])

  const addItem = () => {
    setItems([...items, { productId: '', quantity: 1, costPrice: 0 }])
  }

  const updateItem = (index: number, field: string, value: any) => {
    const newItems = [...items]
    newItems[index] = { ...newItems[index], [field]: value }
    setItems(newItems)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      const totalAmount = items.reduce((sum, item) => sum + (item.costPrice * item.quantity), 0)
      const totalCost = totalAmount
      await onSave({
        reference,
        totalAmount,
        totalCost,
        amountPaid: parseFloat(amountPaid) || 0,
        status: 'PENDING',
        items,
        notes,
      })
      onClose()
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to create purchase')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-lg max-h-[85vh] overflow-y-auto">
        <div className="flex justify-between items-center p-6 border-b">
          <h3 className="text-lg font-semibold">New Purchase</h3>
          <button onClick={onClose} className="p-1 text-gray-400 hover:text-gray-600"><FiX size={24} /></button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Reference</label>
            <input value={reference} onChange={(e) => setReference(e.target.value)} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-gray-700">Items</label>
            {items.map((item, index) => (
              <div key={index} className="grid grid-cols-12 gap-3">
                <select value={item.productId} onChange={(e) => updateItem(index, 'productId', e.target.value)} className="col-span-5 px-2 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required>
                  <option value="">Select product</option>
                  {products.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
                </select>
                <input type="number" placeholder="Qty" value={item.quantity} onChange={(e) => updateItem(index, 'quantity', parseInt(e.target.value))} className="col-span-3 px-2 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
                <input type="number" placeholder="Cost" value={item.costPrice} onChange={(e) => updateItem(index, 'costPrice', parseFloat(e.target.value))} className="col-span-4 px-2 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
              </div>
            ))}
            <button type="button" onClick={addItem} className="text-primary-600 text-sm font-semibold flex items-center gap-1">
              <FiPlus size={16} /> Add Item
            </button>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Amount Paid</label>
              <input type="number" value={amountPaid} onChange={(e) => setAmountPaid(e.target.value)} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Notes</label>
              <input value={notes} onChange={(e) => setNotes(e.target.value)} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
          </div>

          <button type="submit" disabled={loading || items.length === 0} className="w-full bg-primary-600 text-white py-3 rounded-lg font-semibold hover:bg-primary-700 disabled:opacity-50">
            {loading ? 'Creating...' : 'Create Purchase'}
          </button>
        </form>
      </div>
    </div>
  )
}
