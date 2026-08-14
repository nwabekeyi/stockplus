import { useState, useEffect } from 'react'
import { apiGet } from '../../services/api-client'
import { Product, Customer } from '../../types'
import { FiPlus, FiX } from 'react-icons/fi'

interface AddSaleModalProps {
  onClose: () => void
  onSave: (sale: any) => void
}

export default function AddSaleModal({ onClose, onSave }: AddSaleModalProps) {
  const [products, setProducts] = useState<Product[]>([])
  const [customers, setCustomers] = useState<Customer[]>([])
  const [items, setItems] = useState<any[]>([])
  const [customerId, setCustomerId] = useState('')
  const [customerName, setCustomerName] = useState('')
  const [customerPhone, setCustomerPhone] = useState('')
  const [paymentMethod, setPaymentMethod] = useState('Cash')
  const [paymentStatus, setPaymentStatus] = useState('SUCCESS')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    apiGet<Product[]>('/stores/current/products?storeId=current')
      .then(setProducts)
      .catch(() => {})
    apiGet<Customer[]>('/stores/current/customers?storeId=current')
      .then(setCustomers)
      .catch(() => {})
  }, [])

  const handleCustomerChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const cust = customers.find(c => c.id === e.target.value)
    if (cust) {
      setCustomerId(cust.id)
      setCustomerName(cust.name)
      setCustomerPhone(cust.phone)
    } else {
      setCustomerId('')
      setCustomerName('')
      setCustomerPhone('')
    }
  }

  const addItem = () => {
    setItems([...items, { productId: '', quantity: 1, unitPrice: 0 }])
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
      await onSave({
        customerName,
        customerPhone,
        customerId: customerId || null,
        paymentMethod,
        paymentStatus,
        discount: 0,
        items: items.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice,
        })),
      })
      onClose()
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to create sale')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-lg max-h-[85vh] overflow-y-auto">
        <div className="flex justify-between items-center p-6 border-b">
          <h3 className="text-lg font-semibold">New Sale</h3>
          <button onClick={onClose} className="p-1 text-gray-400 hover:text-gray-600">
            <FiX size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Customer</label>
            <select onChange={handleCustomerChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm">
              <option value="">Walk-in Customer</option>
              {customers.map((c) => (
                <option key={c.id} value={c.id}>{c.name} (N{c.outstandingBalance.toLocaleString()} outstanding)</option>
              ))}
            </select>
          </div>

          {!customerId && (
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Customer Name</label>
                <input value={customerName} onChange={(e) => setCustomerName(e.target.value)} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
                <input value={customerPhone} onChange={(e) => setCustomerPhone(e.target.value)} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
              </div>
            </div>
          )}

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Payment Method</label>
              <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm">
                <option value="Cash">Cash</option>
                <option value="Transfer">Transfer</option>
                <option value="POS">POS</option>
                <option value="Card">Card</option>
                <option value="USSD">USSD</option>
                <option value="Credit">Credit</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Payment Status</label>
              <select value={paymentStatus} onChange={(e) => setPaymentStatus(e.target.value)} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm">
                <option value="SUCCESS">Paid</option>
                <option value="PENDING">Pending</option>
              </select>
            </div>
          </div>

          <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-700">Items</label>
              {items.map((item, index) => (
                <div key={index} className="grid grid-cols-12 gap-3">
                <select
                  value={item.productId}
                  onChange={(e) => {
                    const prod = products.find((p) => p.id === e.target.value)
                    updateItem(index, 'productId', e.target.value)
                    if (prod) updateItem(index, 'unitPrice', prod.sellingPrice)
                  }}
                  className="col-span-5 px-2 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
                >
                  <option value="">Select product</option>
                  {products.map((p) => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
                <input
                  type="number"
                  placeholder="Qty"
                  value={item.quantity}
                  onChange={(e) => updateItem(index, 'quantity', parseInt(e.target.value))}
                  className="col-span-3 px-2 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
                />
                <input
                  type="number"
                  placeholder="Price"
                  value={item.unitPrice}
                  onChange={(e) => updateItem(index, 'unitPrice', parseFloat(e.target.value))}
                  className="col-span-4 px-2 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
                />
              </div>
              ))}
              <button type="button" onClick={addItem} className="text-primary-600 text-sm font-semibold flex items-center gap-1">
                <FiPlus size={16} /> Add Item
              </button>
            </div>

          <button
            type="submit"
            disabled={loading || items.length === 0}
            className="w-full bg-primary-600 text-white py-3 rounded-lg font-semibold hover:bg-primary-700 disabled:opacity-50"
          >
            {loading ? 'Recording...' : 'Record Sale'}
          </button>
        </form>
      </div>
    </div>
  )
}