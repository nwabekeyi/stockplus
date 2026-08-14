import { useEffect, useMemo, useState } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { Product, ReturnRecord } from '../../types'
import { useStoreId } from '../../hooks/use-store-id'

export default function ReturnsPage() {
  const storeId = useStoreId()
  const [returns, setReturns] = useState<ReturnRecord[]>([])
  const [products, setProducts] = useState<Product[]>([])
  const [productId, setProductId] = useState('')
  const [quantity, setQuantity] = useState(1)
  const [reason, setReason] = useState('Customer return')
  const [refundMethod, setRefundMethod] = useState('CASH')
  const selectedProduct = useMemo(() => products.find((product) => product.id === productId), [products, productId])

  const load = async () => {
    if (!storeId) return
    const [returnData, productData] = await Promise.all([
      apiGet<ReturnRecord[]>(`/stores/${storeId}/returns`).catch(() => []),
      apiGet<Product[]>(`/stores/${storeId}/products`).catch(() => []),
    ])
    setReturns(returnData)
    setProducts(productData)
    if (!productId && productData[0]) setProductId(productData[0].id)
  }

  useEffect(() => { load() }, [storeId])

  const submitReturn = async () => {
    if (!storeId || !selectedProduct) return
    await apiPost(`/stores/${storeId}/returns`, {
      reason,
      refundMethod,
      refundAmount: selectedProduct.sellingPrice * quantity,
      items: [{ productId: selectedProduct.id, quantity, unitPrice: selectedProduct.sellingPrice, restock: true }],
    })
    setQuantity(1)
    await load()
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-extrabold text-gray-900">Returns & Refunds</h2>
        <p className="text-sm font-medium text-gray-500">Record customer returns, refund method, reasons and restock decisions.</p>
      </div>
      <div className="grid gap-4 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm md:grid-cols-4">
        <select value={productId} onChange={(e) => setProductId(e.target.value)} className="rounded-xl border border-gray-200 px-3 py-2 text-sm md:col-span-2">
          {products.map((product) => <option key={product.id} value={product.id}>{product.name}</option>)}
        </select>
        <input type="number" min="1" value={quantity} onChange={(e) => setQuantity(Number(e.target.value))} className="rounded-xl border border-gray-200 px-3 py-2 text-sm" />
        <select value={refundMethod} onChange={(e) => setRefundMethod(e.target.value)} className="rounded-xl border border-gray-200 px-3 py-2 text-sm">
          {['CASH', 'TRANSFER', 'POS_CARD', 'STORE_CREDIT', 'EXCHANGE'].map((method) => <option key={method}>{method}</option>)}
        </select>
        <input value={reason} onChange={(e) => setReason(e.target.value)} className="rounded-xl border border-gray-200 px-3 py-2 text-sm md:col-span-3" />
        <button onClick={submitReturn} className="rounded-xl bg-primary-600 px-4 py-2 text-sm font-bold text-white">Record return</button>
      </div>
      <div className="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-xs uppercase tracking-widest text-gray-500"><tr><th className="p-4 text-left">Reference</th><th className="p-4 text-left">Reason</th><th className="p-4 text-right">Refund</th><th className="p-4 text-right">Status</th></tr></thead>
          <tbody className="divide-y divide-gray-100">
            {returns.map((item) => <tr key={item.id}><td className="p-4 font-bold">{item.reference}</td><td className="p-4">{item.reason}</td><td className="p-4 text-right">₦{item.refundAmount.toLocaleString()}</td><td className="p-4 text-right">{item.status}</td></tr>)}
          </tbody>
        </table>
      </div>
    </div>
  )
}
