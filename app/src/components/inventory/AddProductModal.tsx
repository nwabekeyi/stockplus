import { useState, useEffect } from 'react'
import { Category, Supplier, UnitOfMeasure } from '../../types'
import { FiX } from 'react-icons/fi'

interface AddProductModalProps {
  categories: Category[]
  suppliers: Supplier[]
  onClose: () => void
  onSave: (product: any) => void
  editProduct?: any
}

export default function AddProductModal({ categories, suppliers, onClose, onSave, editProduct }: AddProductModalProps) {
  const [form, setForm] = useState({
    name: '',
    description: '',
    sellingPrice: '',
    costPrice: '',
    wholesalePrice: '',
    sku: '',
    barcode: '',
    image: '',
    categoryId: '',
    supplierId: '',
    batchNumber: '',
    expiryDate: '',
    minStockLevel: '0',
    maxStockLevel: '100',
    initialQuantity: '0',
    lowStockThreshold: '10',
    unit: 'PIECE' as UnitOfMeasure,
    trackInventory: true,
    active: true,
    wholesaleRules: [] as { minQuantity: number; maxQuantity?: number; price: number }[],
  })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (editProduct) {
      setForm({
        name: editProduct.name || '',
        description: editProduct.description || '',
        sellingPrice: editProduct.sellingPrice?.toString() || '',
        costPrice: editProduct.costPrice?.toString() || '',
        wholesalePrice: editProduct.wholesalePrice?.toString() || '',
        sku: editProduct.sku || '',
        barcode: editProduct.barcode || '',
        image: editProduct.image || '',
        categoryId: editProduct.category?.id || '',
        supplierId: editProduct.supplier?.id || '',
        batchNumber: editProduct.batchNumber || '',
        expiryDate: editProduct.expiryDate || '',
        minStockLevel: editProduct.minStockLevel?.toString() || '0',
        maxStockLevel: editProduct.maxStockLevel?.toString() || '100',
        initialQuantity: editProduct.stock?.quantity?.toString() || '0',
        lowStockThreshold: editProduct.stock?.lowStockThreshold?.toString() || '10',
        unit: editProduct.stock?.unit || 'PIECE',
        trackInventory: editProduct.stock?.trackInventory ?? true,
        active: editProduct.active ?? true,
        wholesaleRules: editProduct.wholesaleRules?.map((r: any) => ({
          minQuantity: r.minQuantity,
          maxQuantity: r.maxQuantity,
          price: parseFloat(r.price),
        })) || [],
      })
    }
  }, [editProduct])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target
    setForm({
      ...form,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : value,
    })
  }

  const addWholesaleRule = () => {
    setForm({
      ...form,
      wholesaleRules: [...form.wholesaleRules, { minQuantity: 1, maxQuantity: undefined, price: 0 }],
    })
  }

  const updateWholesaleRule = (index: number, field: string, value: any) => {
    const updated = [...form.wholesaleRules]
    updated[index] = { ...updated[index], [field]: value }
    setForm({ ...form, wholesaleRules: updated })
  }

  const removeWholesaleRule = (index: number) => {
    setForm({
      ...form,
      wholesaleRules: form.wholesaleRules.filter((_, i) => i !== index),
    })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      const payload: any = {
        name: form.name,
        description: form.description,
        sellingPrice: parseFloat(form.sellingPrice),
        costPrice: parseFloat(form.costPrice),
        wholesalePrice: form.wholesalePrice ? parseFloat(form.wholesalePrice) : undefined,
        sku: form.sku || undefined,
        barcode: form.barcode || undefined,
        image: form.image || undefined,
        categoryId: form.categoryId || null,
        supplierId: form.supplierId || null,
        batchNumber: form.batchNumber || undefined,
        expiryDate: form.expiryDate || undefined,
        minStockLevel: parseInt(form.minStockLevel),
        maxStockLevel: form.maxStockLevel ? parseInt(form.maxStockLevel) : undefined,
        initialQuantity: parseInt(form.initialQuantity),
        lowStockThreshold: parseInt(form.lowStockThreshold),
        unit: form.unit,
        trackInventory: form.trackInventory,
        active: form.active,
        wholesaleRules: form.wholesaleRules.map(r => ({
          minQuantity: r.minQuantity,
          maxQuantity: r.maxQuantity,
          price: r.price,
        })),
      }
      await onSave(payload)
      onClose()
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to save product')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-2xl max-h-[85vh] overflow-y-auto">
        <div className="flex justify-between items-center p-6 border-b">
          <h3 className="text-lg font-semibold">{editProduct ? 'Edit Product' : 'Add Product'}</h3>
          <button onClick={onClose} className="p-1 text-gray-400 hover:text-gray-600">
            <FiX size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Product Name</label>
            <input name="name" value={form.name} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">SKU</label>
              <input name="sku" value={form.sku} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" placeholder="Auto-generated if empty" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Barcode</label>
              <input name="barcode" value={form.barcode} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
            <input name="image" value={form.image} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" placeholder="https://example.com/image.png" />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Selling Price</label>
              <input type="number" step="0.01" name="sellingPrice" value={form.sellingPrice} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Cost Price</label>
              <input type="number" step="0.01" name="costPrice" value={form.costPrice} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Wholesale Price</label>
              <input type="number" step="0.01" name="wholesalePrice" value={form.wholesalePrice} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
              <select name="categoryId" value={form.categoryId} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required>
                <option value="">None</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Supplier</label>
              <select name="supplierId" value={form.supplierId} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm">
                <option value="">None</option>
                {suppliers.map((sup) => (
                  <option key={sup.id} value={sup.id}>{sup.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Unit of Measure</label>
              <select name="unit" value={form.unit} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required>
                <option value="PIECE">Piece</option>
                <option value="CARTON">Carton</option>
                <option value="PACK">Pack</option>
                <option value="KILOGRAM">Kilogram</option>
                <option value="LITRE">Litre</option>
                <option value="KEG">Keg</option>
                <option value="BAG">Bag</option>
                <option value="BOX">Box</option>
                <option value="DOZEN">Dozen</option>
                <option value="METER">Meter</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Min Stock Level</label>
              <input type="number" name="minStockLevel" value={form.minStockLevel} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Stock Level</label>
              <input type="number" name="maxStockLevel" value={form.maxStockLevel} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Initial Qty</label>
              <input type="number" name="initialQuantity" value={form.initialQuantity} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Low Stock Threshold</label>
              <input type="number" name="lowStockThreshold" value={form.lowStockThreshold} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Batch Number</label>
              <input name="batchNumber" value={form.batchNumber} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Expiry Date</label>
              <input type="date" name="expiryDate" value={form.expiryDate} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea name="description" value={form.description} onChange={handleChange} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" rows={2} />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Wholesale Pricing Rules</label>
            {form.wholesaleRules.map((rule, idx) => (
              <div key={idx} className="grid grid-cols-4 gap-2 mb-2">
                <input
                  type="number"
                  placeholder="Min Qty"
                  value={rule.minQuantity}
                  onChange={(e) => updateWholesaleRule(idx, 'minQuantity', parseInt(e.target.value))}
                  className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
                />
                <input
                  type="number"
                  placeholder="Max Qty"
                  value={rule.maxQuantity || ''}
                  onChange={(e) => updateWholesaleRule(idx, 'maxQuantity', e.target.value ? parseInt(e.target.value) : undefined)}
                  className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
                />
                <input
                  type="number"
                  step="0.01"
                  placeholder="Price"
                  value={rule.price}
                  onChange={(e) => updateWholesaleRule(idx, 'price', parseFloat(e.target.value))}
                  className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
                />
                <button type="button" onClick={() => removeWholesaleRule(idx)} className="text-red-600 hover:text-red-700 text-sm">Remove</button>
              </div>
            ))}
            <button type="button" onClick={addWholesaleRule} className="text-primary-600 text-sm font-medium hover:text-primary-700">
              + Add Wholesale Rule
            </button>
          </div>

          <div className="flex items-center gap-2">
            <input type="checkbox" id="trackInventory" name="trackInventory" checked={form.trackInventory} onChange={(e) => setForm({ ...form, trackInventory: e.target.checked })} />
            <label htmlFor="trackInventory" className="text-sm text-gray-700">Track Inventory</label>
          </div>

          <div className="flex items-center gap-2">
            <input type="checkbox" id="active" name="active" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} />
            <label htmlFor="active" className="text-sm text-gray-700">Active</label>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary-600 text-white py-3 rounded-lg font-semibold hover:bg-primary-700 disabled:opacity-50"
          >
            {loading ? 'Saving...' : editProduct ? 'Update Product' : 'Add Product'}
          </button>
        </form>
      </div>
    </div>
  )
}
