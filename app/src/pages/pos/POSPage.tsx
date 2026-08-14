import { useState, useEffect } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { Product } from '../../types'
import { useStoreId } from '../../hooks/use-store-id'
import { FiX, FiMinus, FiPlus, FiShoppingCart, FiSearch } from 'react-icons/fi'

interface OfflineQueuedSale {
  id: string
  payload: {
    offlineReference: string
    customerName: string
    customerPhone: string
    paymentMethod: string
    paymentStatus: string
    discount: number
    items: { productId: string; quantity: number; unitPrice: number }[]
  }
}

export interface CartItem {
  productId: string
  productName: string
  quantity: number
  unitPrice: number
  costPrice: number
}

export default function POSPage() {
  const storeId = useStoreId()
  const [products, setProducts] = useState<Product[]>([])
  const [search, setSearch] = useState('')
  const [cart, setCart] = useState<CartItem[]>([])
  const [paymentMethod, setPaymentMethod] = useState('Cash')
  const [customerName, setCustomerName] = useState('')
  const [customerPhone, setCustomerPhone] = useState('')
  const [loading, setLoading] = useState(false)
  const [showCart, setShowCart] = useState(false)
  const [isOnline, setIsOnline] = useState(navigator.onLine)
  const [queuedSales, setQueuedSales] = useState(() => (JSON.parse(localStorage.getItem('offlineSalesQueue') || '[]') as OfflineQueuedSale[]).length)

  useEffect(() => {
    if (!storeId) return
    apiGet<Product[]>(`/stores/${storeId}/products`)
      .then((data) => {
        setProducts(data)
        localStorage.setItem('posProductCache', JSON.stringify(data))
      })
      .catch(() => {
        setProducts(JSON.parse(localStorage.getItem('posProductCache') || '[]'))
      })
  }, [storeId])

  useEffect(() => {
    const syncQueuedSales = async () => {
      if (!storeId || !navigator.onLine) return
      const queue = JSON.parse(localStorage.getItem('offlineSalesQueue') || '[]') as OfflineQueuedSale[]
      const remaining: OfflineQueuedSale[] = []
      for (const sale of queue) {
        try {
          await apiPost(`/stores/${storeId}/sales`, sale.payload)
        } catch {
          remaining.push(sale)
        }
      }
      localStorage.setItem('offlineSalesQueue', JSON.stringify(remaining))
      setQueuedSales(remaining.length)
    }
    const updateStatus = () => {
      setIsOnline(navigator.onLine)
      syncQueuedSales()
    }
    window.addEventListener('online', updateStatus)
    window.addEventListener('offline', updateStatus)
    syncQueuedSales()
    return () => {
      window.removeEventListener('online', updateStatus)
      window.removeEventListener('offline', updateStatus)
    }
  }, [storeId])

  const addToCart = (product: Product) => {
    setCart(prev => {
      const existing = prev.find(item => item.productId === product.id)
      if (existing) {
        return prev.map(item =>
          item.productId === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        )
      }
      return [...prev, {
        productId: product.id,
        productName: product.name,
        quantity: 1,
        unitPrice: product.sellingPrice,
        costPrice: product.costPrice,
      }]
    })
  }

  const updateCartQty = (productId: string, delta: number) => {
    setCart(prev => prev.map(item => {
      if (item.productId === productId) {
        const newQty = Math.max(1, item.quantity + delta)
        return { ...item, quantity: newQty }
      }
      return item
    }).filter(item => item.quantity > 0))
  }

  const removeFromCart = (productId: string) => {
    setCart(prev => prev.filter(item => item.productId !== productId))
  }

  const totalAmount = cart.reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0)
  const totalCost = cart.reduce((sum, item) => sum + (item.costPrice * item.quantity), 0)

  const handleCheckout = async () => {
    if (!storeId || cart.length === 0) return
    setLoading(true)
    const payload = {
      offlineReference: `OFF-${Date.now()}`,
      customerName,
      customerPhone,
      paymentMethod,
      paymentStatus: paymentMethod === 'Credit' ? 'PENDING' : 'SUCCESS',
      discount: 0,
      items: cart.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
      })),
    }
    try {
      if (!navigator.onLine) {
        const queue = JSON.parse(localStorage.getItem('offlineSalesQueue') || '[]') as OfflineQueuedSale[]
        queue.push({ id: payload.offlineReference, payload })
        localStorage.setItem('offlineSalesQueue', JSON.stringify(queue))
        setQueuedSales(queue.length)
        alert('Sale saved offline and will sync when internet is restored.')
      } else {
        await apiPost(`/stores/${storeId}/sales`, payload)
        alert('Sale completed successfully!')
      }
      setCart([])
      setCustomerName('')
      setCustomerPhone('')
      setShowCart(false)
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to complete sale')
    } finally {
      setLoading(false)
    }
  }

  const filteredProducts = products.filter(p =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    p.sku.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="flex flex-col lg:flex-row gap-4 h-[calc(100vh-8rem)]">
      <div className="flex-1 space-y-4 min-w-0">
        <div className={`rounded-xl px-4 py-3 text-sm font-bold ${isOnline ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'}`}>
          {isOnline ? 'Online POS' : 'Offline POS'} · {queuedSales} queued sale{queuedSales === 1 ? '' : 's'}
        </div>
        <div className="relative">
          <FiSearch className="absolute left-3 top-3 text-gray-400" size={20} />
          <input
            type="text"
            placeholder="Search products..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-3 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
          />
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 overflow-y-auto max-h-[calc(100vh-12rem)]">
          {filteredProducts.map((product) => (
            <button
              key={product.id}
              onClick={() => addToCart(product)}
              className="bg-white rounded-xl shadow-sm p-3 md:p-4 text-left hover:shadow-md transition-shadow border border-gray-100"
            >
              <h3 className="text-sm font-semibold text-gray-900 truncate">{product.name}</h3>
              <p className="text-xs text-gray-500 mb-2">{product.sku}</p>
              <div className="flex justify-between items-center">
                <span className="text-sm font-bold text-primary-600">N{product.sellingPrice.toLocaleString()}</span>
                <span className="text-xs text-gray-500">Stock: {product.stock?.quantity || 0}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      <div className={`lg:w-96 bg-white rounded-xl shadow-sm flex flex-col ${
        showCart ? 'fixed inset-0 z-50 lg:relative lg:inset-auto' : 'hidden lg:flex'
      }`}>
        <div className="p-4 border-b border-gray-200 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <FiShoppingCart size={20} /> Cart ({cart.length})
          </h3>
          <button onClick={() => setShowCart(false)} className="lg:hidden p-1 text-gray-400 hover:text-gray-600">
            <FiX size={24} />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-4 space-y-2">
          {cart.length === 0 ? (
            <p className="text-sm text-gray-500 text-center py-8">Cart is empty</p>
          ) : cart.map((item) => (
            <div key={item.productId} className="flex items-center gap-2 p-2 bg-gray-50 rounded-lg">
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">{item.productName}</p>
                <p className="text-xs text-gray-500">N{item.unitPrice.toLocaleString()} each</p>
              </div>
              <div className="flex items-center gap-1">
                <button onClick={() => updateCartQty(item.productId, -1)} className="p-1 text-gray-400 hover:text-gray-600">
                  <FiMinus size={14} />
                </button>
                <span className="text-sm font-medium w-6 text-center">{item.quantity}</span>
                <button onClick={() => updateCartQty(item.productId, 1)} className="p-1 text-gray-400 hover:text-gray-600">
                  <FiPlus size={14} />
                </button>
              </div>
              <button onClick={() => removeFromCart(item.productId)} className="p-1 text-red-400 hover:text-red-600">
                <FiX size={14} />
              </button>
            </div>
          ))}
        </div>

        <div className="border-t border-gray-200 p-4 space-y-3">
          <div className="space-y-2">
            <input
              type="text"
              placeholder="Customer name"
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
              className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
            />
            <input
              type="text"
              placeholder="Customer phone"
              value={customerPhone}
              onChange={(e) => setCustomerPhone(e.target.value)}
              className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
            />
            <select
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value)}
              className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
            >
              <option value="Cash">Cash</option>
              <option value="Transfer">Transfer</option>
              <option value="POS">POS</option>
              <option value="Card">Card</option>
              <option value="USSD">USSD</option>
              <option value="Credit">Credit</option>
            </select>
          </div>

          <div className="space-y-1">
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Total</span>
              <span className="font-semibold text-gray-900">N{totalAmount.toLocaleString()}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Profit</span>
              <span className="font-semibold text-green-600">N{(totalAmount - totalCost).toLocaleString()}</span>
            </div>
          </div>

          <button
            onClick={handleCheckout}
            disabled={loading || cart.length === 0}
            className="w-full bg-primary-600 text-white py-3 rounded-lg font-semibold hover:bg-primary-700 disabled:opacity-50"
          >
            {loading ? 'Processing...' : 'Checkout'}
          </button>
        </div>
      </div>

      {!showCart && cart.length > 0 && (
        <button
          onClick={() => setShowCart(true)}
          className="lg:hidden fixed bottom-6 right-6 bg-primary-600 text-white p-4 rounded-full shadow-lg flex items-center gap-2 z-40"
        >
          <FiShoppingCart size={20} />
          <span className="font-semibold">{cart.length}</span>
        </button>
      )}
    </div>
  )
}
