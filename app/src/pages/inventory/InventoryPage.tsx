import { useEffect, useState } from 'react'
import { apiGet, apiPost, apiPut, apiDelete } from '../../services/api-client'
import { Product, Category, Supplier } from '../../types'
import { useStoreId } from '../../hooks/use-store-id'
import { FiPlus, FiTrash2, FiEdit, FiPackage, FiArchive, FiRefreshCcw, FiSearch, FiFilter } from 'react-icons/fi'
import AddProductModal from '../../components/inventory/AddProductModal'

export default function InventoryPage() {
  const storeId = useStoreId()
  const [products, setProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [suppliers, setSuppliers] = useState<Supplier[]>([])
  const [loading, setLoading] = useState(true)
  const [showAddModal, setShowAddModal] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [searchQuery, setSearchQuery] = useState('')
  const [filterCategory, setFilterCategory] = useState('')
  const [filterStatus, setFilterStatus] = useState('')
  const [showFilters, setShowFilters] = useState(false)

  useEffect(() => {
    if (!storeId) return
    fetchData()
  }, [storeId])

  const fetchData = async () => {
    if (!storeId) return
    try {
      const [prods, cats, sups] = await Promise.all([
        apiGet<Product[]>(`/stores/${storeId}/products`),
        apiGet<Category[]>(`/stores/${storeId}/categories`),
        apiGet<Supplier[]>(`/stores/${storeId}/suppliers`),
      ])
      setProducts(prods)
      setCategories(cats)
      setSuppliers(sups)
    } finally {
      setLoading(false)
    }
  }

  const handleAddProduct = async (product: any) => {
    if (!storeId) return
    if (editingProduct) {
      await apiPut(`/stores/${storeId}/products/${editingProduct.id}`, product)
    } else {
      await apiPost(`/stores/${storeId}/products`, product)
    }
    fetchData()
    setEditingProduct(null)
  }

  const handleDelete = async (id: string) => {
    if (!storeId) return
    if (!confirm('Delete this product?')) return
    await apiDelete(`/stores/${storeId}/products/${id}`)
    fetchData()
  }

  const handleArchive = async (id: string) => {
    if (!storeId) return
    await apiPut(`/stores/${storeId}/products/${id}/archive`, {})
    fetchData()
  }

  const handleActivate = async (id: string) => {
    if (!storeId) return
    await apiPut(`/stores/${storeId}/products/${id}/activate`, {})
    fetchData()
  }

  const filteredProducts = products.filter((product) => {
    const matchesSearch = !searchQuery || 
      product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      product.sku.toLowerCase().includes(searchQuery.toLowerCase()) ||
      product.barcode?.toLowerCase().includes(searchQuery.toLowerCase())
    
    const matchesCategory = !filterCategory || (product.category?.id === filterCategory)
    const matchesStatus = !filterStatus || 
      (filterStatus === 'active' && product.active && !product.archived) ||
      (filterStatus === 'inactive' && !product.active && !product.archived) ||
      (filterStatus === 'archived' && product.archived)
    
    return matchesSearch && matchesCategory && matchesStatus
  })

  const sortedProducts = [...filteredProducts].sort((a, b) => {
    return a.name.localeCompare(b.name)
  })

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-4 md:space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h2 className="text-xl font-bold text-gray-900">Inventory</h2>
        <button
          onClick={() => { setEditingProduct(null); setShowAddModal(true) }}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2 text-sm md:text-base"
        >
          <FiPlus size={18} />
          <span className="hidden sm:inline">Add Product</span>
          <span className="sm:hidden">Add</span>
        </button>
      </div>

      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Search products..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
          />
        </div>
        <button
          onClick={() => setShowFilters(!showFilters)}
          className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg text-sm hover:bg-gray-50"
        >
          <FiFilter size={16} />
          Filters
        </button>
      </div>

      {showFilters && (
        <div className="bg-white rounded-xl shadow-sm p-4 flex flex-wrap gap-3">
          <select
            value={filterCategory}
            onChange={(e) => setFilterCategory(e.target.value)}
            className="px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
          >
            <option value="">All Categories</option>
            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm"
          >
            <option value="">All Status</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
            <option value="archived">Archived</option>
          </select>
          <button
            onClick={() => { setFilterCategory(''); setFilterStatus(''); setSearchQuery('') }}
            className="text-sm text-primary-600 hover:text-primary-700"
          >
            Clear filters
          </button>
        </div>
      )}

      {sortedProducts.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm p-8 md:p-12 text-center">
          <FiPackage className="mx-auto text-gray-300 mb-3" size={48} />
          <p className="text-base text-gray-500 mb-3">No products found</p>
          <button
            onClick={() => { setEditingProduct(null); setShowAddModal(true) }}
            className="text-primary-600 text-sm font-semibold"
          >
            Add your first product
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {sortedProducts.map((product) => (
            <div key={product.id} className={`bg-white rounded-xl shadow-sm p-4 md:p-5 ${product.archived ? 'opacity-60' : ''}`}>
              <div className="flex justify-between items-start">
                <div className="flex-1 min-w-0">
                  <h3 className="text-base font-semibold text-gray-900 truncate">{product.name}</h3>
                  <p className="text-sm text-gray-500">SKU: {product.sku}</p>
                  {product.barcode && <p className="text-xs text-gray-400">Barcode: {product.barcode}</p>}
                  <div className="mt-2 flex items-center gap-3">
                    <span className="text-sm font-bold text-primary-600">{product.sellingPrice.toLocaleString()}</span>
                    <span className="text-xs text-gray-500">Cost: {product.costPrice.toLocaleString()}</span>
                  </div>
                  {product.wholesalePrice > 0 && (
                    <p className="text-xs text-gray-500">Wholesale: {product.wholesalePrice.toLocaleString()}</p>
                  )}
                  {product.stock && (
                    <div className="mt-2 flex items-center gap-2">
                      <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                        product.stock.quantity <= product.stock.lowStockThreshold
                          ? 'bg-red-100 text-red-700'
                          : 'bg-green-100 text-green-700'
                      }`}>
                        Stock: {product.stock.quantity}
                      </span>
                      <span className="text-xs text-gray-500">{product.stock.unit}</span>
                    </div>
                  )}
                  <div className="mt-2 flex flex-wrap gap-1">
                    {product.category && (
                      <span className="px-2 py-0.5 bg-gray-100 rounded-full text-xs text-gray-600">{product.category.name}</span>
                    )}
                    {product.archived && (
                      <span className="px-2 py-0.5 bg-gray-100 rounded-full text-xs text-gray-500">Archived</span>
                    )}
                    {!product.active && !product.archived && (
                      <span className="px-2 py-0.5 bg-yellow-100 rounded-full text-xs text-yellow-700">Inactive</span>
                    )}
                  </div>
                </div>
                <div className="flex gap-2 ml-2">
                  <button onClick={() => { setEditingProduct(product); setShowAddModal(true) }} className="p-2 text-gray-400 hover:text-primary-600">
                    <FiEdit size={16} />
                  </button>
                  {!product.archived && product.active && (
                    <button onClick={() => handleArchive(product.id)} className="p-2 text-gray-400 hover:text-yellow-600" title="Archive">
                      <FiArchive size={16} />
                    </button>
                  )}
                  {product.archived && (
                    <button onClick={() => handleActivate(product.id)} className="p-2 text-gray-400 hover:text-green-600" title="Activate">
                      <FiRefreshCcw size={16} />
                    </button>
                  )}
                  <button onClick={() => handleDelete(product.id)} className="p-2 text-gray-400 hover:text-red-600">
                    <FiTrash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {showAddModal && storeId && (
        <AddProductModal
          categories={categories}
          suppliers={suppliers}
          onClose={() => { setShowAddModal(false); setEditingProduct(null) }}
          onSave={handleAddProduct}
          editProduct={editingProduct}
        />
      )}
    </div>
  )
}
