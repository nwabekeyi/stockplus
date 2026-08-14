import { useEffect, useState } from 'react'
import { apiGet, apiPost, apiPut, apiDelete } from '../../services/api-client'
import { useStoreId } from '../../hooks/use-store-id'
import { FiPlus, FiTrash2, FiEdit } from 'react-icons/fi'
import AddBranchModal from '../../components/branches/AddBranchModal'

export interface Branch {
  id: string
  name: string
  address?: string
  phone?: string
  manager?: string
  active: boolean
  createdAt: string
}

export default function BranchesPage() {
  const storeId = useStoreId()
  const [branches, setBranches] = useState<Branch[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingBranch, setEditingBranch] = useState<Branch | null>(null)

  useEffect(() => {
    if (!storeId) return
    apiGet<Branch[]>(`/stores/${storeId}/branches`)
      .then(setBranches)
      .finally(() => setLoading(false))
  }, [storeId])

  const handleSave = async (branch: any) => {
    if (!storeId) return
    if (editingBranch) {
      await apiPut(`/stores/${storeId}/branches/${editingBranch.id}`, branch)
    } else {
      await apiPost(`/stores/${storeId}/branches`, branch)
    }
    const data = await apiGet<Branch[]>(`/stores/${storeId}/branches`)
    setBranches(data)
    setShowModal(false)
    setEditingBranch(null)
  }

  const handleDelete = async (id: string) => {
    if (!storeId) return
    if (!confirm('Delete this branch?')) return
    await apiDelete(`/stores/${storeId}/branches/${id}`)
    const data = await apiGet<Branch[]>(`/stores/${storeId}/branches`)
    setBranches(data)
  }

  const handleEdit = (branch: Branch) => {
    setEditingBranch(branch)
    setShowModal(true)
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Branches</h2>
        <button onClick={() => { setEditingBranch(null); setShowModal(true) }} className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2">
          <FiPlus size={18} /> Add Branch
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {branches.length === 0 ? (
          <div className="bg-white rounded-xl shadow-sm p-12 text-center col-span-full">
            <p className="text-base text-gray-500 mb-3">No branches yet</p>
            <button onClick={() => setShowModal(true)} className="text-primary-600 text-sm font-semibold">Add your first branch</button>
          </div>
        ) : branches.map((branch) => (
          <div key={branch.id} className="bg-white rounded-xl shadow-sm p-5">
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <h3 className="text-base font-semibold text-gray-900">{branch.name}</h3>
                <p className="text-sm text-gray-500">{branch.address || 'No address'}</p>
                <p className="text-sm text-gray-500">{branch.phone || 'No phone'}</p>
                <p className="text-sm text-gray-500">Manager: {branch.manager || 'Unassigned'}</p>
              </div>
              <div className="flex gap-2">
                <button onClick={() => handleEdit(branch)} className="p-2 text-gray-400 hover:text-primary-600"><FiEdit size={16} /></button>
                <button onClick={() => handleDelete(branch.id)} className="p-2 text-gray-400 hover:text-red-600"><FiTrash2 size={16} /></button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <AddBranchModal branch={editingBranch} onClose={() => { setShowModal(false); setEditingBranch(null) }} onSave={handleSave} />
      )}
    </div>
  )
}
