import { useEffect, useState } from 'react'
import { apiGet, apiPost, apiPut, apiDelete } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'
import { FiPlus, FiTrash2, FiEdit } from 'react-icons/fi'
import AddCustomerModal from '../../components/customers/AddCustomerModal'

export interface Customer {
  id: string
  name: string
  phone: string
  email?: string
  address?: string
  creditLimit: number
  outstandingBalance: number
  status: string
  createdAt: string
}

export default function CustomersPage() {
  const storeId = useStoreId()
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null)

  useEffect(() => {
    if (!storeId) return
    apiGet<Customer[]>(`/stores/${storeId}/customers`)
      .then(setCustomers)
      .finally(() => setLoading(false))
  }, [storeId])

  const handleSave = async (customer: any) => {
    if (!storeId) return
    if (editingCustomer) {
      await apiPut(`/stores/${storeId}/customers/${editingCustomer.id}`, customer)
    } else {
      await apiPost(`/stores/${storeId}/customers`, customer)
    }
    const data = await apiGet<Customer[]>(`/stores/${storeId}/customers`)
    setCustomers(data)
    setShowModal(false)
    setEditingCustomer(null)
  }

  const handleDelete = async (id: string) => {
    if (!storeId) return
    if (!confirm('Delete this customer?')) return
    await apiDelete(`/stores/${storeId}/customers/${id}`)
    const data = await apiGet<Customer[]>(`/stores/${storeId}/customers`)
    setCustomers(data)
  }

  const handleEdit = (customer: Customer) => {
    setEditingCustomer(customer)
    setShowModal(true)
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Customers</h2>
        <button onClick={() => { setEditingCustomer(null); setShowModal(true) }} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2">
          <FiPlus size={18} /> Add Customer
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {customers.length === 0 ? (
          <div className="bg-white rounded-xl shadow-sm p-12 text-center col-span-full">
            <p className="text-base text-gray-500 mb-3">No customers yet</p>
            <button onClick={() => setShowModal(true)} className="text-primary-600 text-sm font-semibold">Add your first customer</button>
          </div>
        ) : customers.map((customer) => (
          <div key={customer.id} className="bg-white rounded-xl shadow-sm p-5">
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <h3 className="text-base font-semibold text-gray-900">{customer.name}</h3>
                <p className="text-sm text-gray-500">{customer.phone}</p>
                {customer.email && <p className="text-sm text-gray-500">{customer.email}</p>}
                <div className="mt-2">
                  <span className="text-sm font-bold text-red-600">Outstanding: N{customer.outstandingBalance.toLocaleString()}</span>
                </div>
              </div>
              <div className="flex gap-2">
                <button onClick={() => handleEdit(customer)} className="p-2 text-gray-400 hover:text-primary-600"><FiEdit size={16} /></button>
                <button onClick={() => handleDelete(customer.id)} className="p-2 text-gray-400 hover:text-red-600"><FiTrash2 size={16} /></button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <AddCustomerModal customer={editingCustomer} onClose={() => { setShowModal(false); setEditingCustomer(null) }} onSave={handleSave} />
      )}
    </div>
  )
}
