import { useEffect, useState } from 'react'
import { apiGet, apiPost, apiPut, apiDelete } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'
import { FiPlus, FiTrash2, FiEdit } from 'react-icons/fi'
import AddSupplierModal from '../../components/suppliers/AddSupplierModal'

export interface Supplier {
  id: string
  name: string
  phone: string
  email?: string
  address?: string
  outstandingBalance: number
  status: string
  createdAt: string
}

export default function SuppliersPage() {
  const storeId = useStoreId()
  const [suppliers, setSuppliers] = useState<Supplier[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingSupplier, setEditingSupplier] = useState<Supplier | null>(null)

  useEffect(() => {
    if (!storeId) return
    apiGet<Supplier[]>(`/stores/${storeId}/suppliers`)
      .then(setSuppliers)
      .finally(() => setLoading(false))
  }, [storeId])

  const handleSave = async (supplier: any) => {
    if (!storeId) return
    if (editingSupplier) {
      await apiPut(`/stores/${storeId}/suppliers/${editingSupplier.id}`, supplier)
    } else {
      await apiPost(`/stores/${storeId}/suppliers`, supplier)
    }
    const data = await apiGet<Supplier[]>(`/stores/${storeId}/suppliers`)
    setSuppliers(data)
    setShowModal(false)
    setEditingSupplier(null)
  }

  const handleDelete = async (id: string) => {
    if (!storeId) return
    if (!confirm('Delete this supplier?')) return
    await apiDelete(`/stores/${storeId}/suppliers/${id}`)
    const data = await apiGet<Supplier[]>(`/stores/${storeId}/suppliers`)
    setSuppliers(data)
  }

  const handleEdit = (supplier: Supplier) => {
    setEditingSupplier(supplier)
    setShowModal(true)
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Suppliers</h2>
        <button onClick={() => { setEditingSupplier(null); setShowModal(true) }} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2">
          <FiPlus size={18} /> Add Supplier
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {suppliers.length === 0 ? (
          <div className="bg-white rounded-xl shadow-sm p-12 text-center col-span-full">
            <p className="text-base text-gray-500 mb-3">No suppliers yet</p>
            <button onClick={() => setShowModal(true)} className="text-primary-600 text-sm font-semibold">Add your first supplier</button>
          </div>
        ) : suppliers.map((supplier) => (
          <div key={supplier.id} className="bg-white rounded-xl shadow-sm p-5">
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <h3 className="text-base font-semibold text-gray-900">{supplier.name}</h3>
                <p className="text-sm text-gray-500">{supplier.phone}</p>
                {supplier.email && <p className="text-sm text-gray-500">{supplier.email}</p>}
                <div className="mt-2">
                  <span className="text-sm font-bold text-primary-600">Outstanding: N{supplier.outstandingBalance.toLocaleString()}</span>
                </div>
              </div>
              <div className="flex gap-2">
                <button onClick={() => handleEdit(supplier)} className="p-2 text-gray-400 hover:text-primary-600"><FiEdit size={16} /></button>
                <button onClick={() => handleDelete(supplier.id)} className="p-2 text-gray-400 hover:text-red-600"><FiTrash2 size={16} /></button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <AddSupplierModal supplier={editingSupplier} onClose={() => { setShowModal(false); setEditingSupplier(null) }} onSave={handleSave} />
      )}
    </div>
  )
}
