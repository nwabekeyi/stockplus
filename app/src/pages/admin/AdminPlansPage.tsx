import { useEffect, useState } from 'react'
import { apiGet, apiPost, apiPut, apiDelete } from '../../services/api-client'
import { SubscriptionPlan } from '../../types'
import { FiPlus } from 'react-icons/fi'

export default function AdminPlansPage() {
  const [plans, setPlans] = useState<SubscriptionPlan[]>([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingPlan, setEditingPlan] = useState<SubscriptionPlan | null>(null)
  const [form, setForm] = useState({
    name: '',
    description: '',
    price: '',
    billingInterval: 'MONTHLY' as 'MONTHLY' | 'YEARLY',
    maxProducts: '100',
    maxUsers: '5',
    maxBranches: '1',
    whatsappEnabled: false,
    advancedReportsEnabled: false,
    apiEnabled: false,
    features: '',
    active: true,
  })

  useEffect(() => {
    fetchPlans()
  }, [])

  const fetchPlans = async () => {
    try {
      const data = await apiGet<SubscriptionPlan[]>('/admin/plans')
      setPlans(data)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const payload = {
        ...form,
        price: parseFloat(form.price),
        maxProducts: parseInt(form.maxProducts),
        maxUsers: parseInt(form.maxUsers),
        maxBranches: parseInt(form.maxBranches),
      }

      if (editingPlan) {
        await apiPut(`/admin/plans/${editingPlan.id}`, payload)
      } else {
        await apiPost('/admin/plans', payload)
      }
      fetchPlans()
      resetForm()
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to save plan')
    }
  }

  const handleEdit = (plan: SubscriptionPlan) => {
    setEditingPlan(plan)
    setForm({
      name: plan.name,
      description: plan.description || '',
      price: plan.price.toString(),
      billingInterval: plan.billingInterval,
      maxProducts: plan.maxProducts.toString(),
      maxUsers: plan.maxUsers.toString(),
      maxBranches: plan.maxBranches.toString(),
      whatsappEnabled: plan.whatsappEnabled,
      advancedReportsEnabled: plan.advancedReportsEnabled,
      apiEnabled: plan.apiEnabled,
      features: plan.features || '',
      active: plan.active,
    })
    setShowForm(true)
  }

  const handleDelete = async (id: string) => {
    if (!confirm('Delete this plan?')) return
    await apiDelete(`/admin/plans/${id}`)
    fetchPlans()
  }

  const resetForm = () => {
    setForm({
      name: '',
      description: '',
      price: '',
      billingInterval: 'MONTHLY',
      maxProducts: '100',
      maxUsers: '5',
      maxBranches: '1',
      whatsappEnabled: false,
      advancedReportsEnabled: false,
      apiEnabled: false,
      features: '',
      active: true,
    })
    setEditingPlan(null)
    setShowForm(false)
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Admin Plans</h2>
        <button
          onClick={() => { resetForm(); setShowForm(!showForm) }}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-primary-700 flex items-center gap-2"
        >
          <FiPlus size={16} /> {showForm ? 'Cancel' : 'New Plan'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-sm p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Plan Name</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" rows={2} />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Price</label>
              <input type="number" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Interval</label>
              <select value={form.billingInterval} onChange={(e) => setForm({ ...form, billingInterval: e.target.value as any })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm">
                <option value="MONTHLY">Monthly</option>
                <option value="YEARLY">Yearly</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Products</label>
              <input type="number" value={form.maxProducts} onChange={(e) => setForm({ ...form, maxProducts: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Users</label>
              <input type="number" value={form.maxUsers} onChange={(e) => setForm({ ...form, maxUsers: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Branches</label>
              <input type="number" value={form.maxBranches} onChange={(e) => setForm({ ...form, maxBranches: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Features</label>
              <textarea value={form.features} onChange={(e) => setForm({ ...form, features: e.target.value })} className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:border-primary-500 outline-none text-sm" rows={2} />
            </div>
          </div>

          <div className="flex flex-wrap gap-4">
            <div className="flex items-center gap-2">
              <input type="checkbox" id="whatsappEnabled" checked={form.whatsappEnabled} onChange={(e) => setForm({ ...form, whatsappEnabled: e.target.checked })} />
              <label htmlFor="whatsappEnabled" className="text-sm text-gray-700">WhatsApp</label>
            </div>
            <div className="flex items-center gap-2">
              <input type="checkbox" id="advancedReportsEnabled" checked={form.advancedReportsEnabled} onChange={(e) => setForm({ ...form, advancedReportsEnabled: e.target.checked })} />
              <label htmlFor="advancedReportsEnabled" className="text-sm text-gray-700">Advanced Reports</label>
            </div>
            <div className="flex items-center gap-2">
              <input type="checkbox" id="apiEnabled" checked={form.apiEnabled} onChange={(e) => setForm({ ...form, apiEnabled: e.target.checked })} />
              <label htmlFor="apiEnabled" className="text-sm text-gray-700">API Access</label>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <input type="checkbox" id="active" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} />
            <label htmlFor="active" className="text-sm text-gray-700">Active</label>
          </div>

          <button type="submit" className="w-full bg-primary-600 text-white py-2.5 rounded-lg font-medium hover:bg-primary-700">
            {editingPlan ? 'Update Plan' : 'Create Plan'}
          </button>
        </form>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {plans.map((plan) => (
          <div key={plan.id} className="bg-white rounded-xl shadow-sm p-5">
            <div className="flex justify-between items-start mb-3">
              <div>
                <h3 className="text-base font-semibold text-gray-900">{plan.name}</h3>
                <p className="text-sm text-gray-500">{plan.billingInterval}</p>
              </div>
              <p className="text-lg font-bold text-primary-600">{plan.price.toLocaleString()}</p>
            </div>

            <div className="flex flex-wrap gap-2 mb-3">
              <span className="px-2 py-1 bg-gray-100 rounded-full text-xs text-gray-600">{plan.maxProducts} products</span>
              <span className="px-2 py-1 bg-gray-100 rounded-full text-xs text-gray-600">{plan.maxUsers} users</span>
              <span className="px-2 py-1 bg-gray-100 rounded-full text-xs text-gray-600">{plan.maxBranches} branches</span>
            </div>

            <div className="flex flex-wrap gap-2 mb-3">
              {plan.whatsappEnabled && <span className="px-2 py-1 bg-green-50 rounded-full text-xs text-green-700">WhatsApp</span>}
              {plan.advancedReportsEnabled && <span className="px-2 py-1 bg-green-50 rounded-full text-xs text-green-700">Reports</span>}
              {plan.apiEnabled && <span className="px-2 py-1 bg-green-50 rounded-full text-xs text-green-700">API</span>}
            </div>

            <div className="flex gap-2 mt-4">
              <button onClick={() => handleEdit(plan)} className="flex-1 bg-primary-50 text-primary-600 py-2 rounded-lg text-sm font-medium hover:bg-primary-100">
                Edit
              </button>
              <button onClick={() => handleDelete(plan.id)} className="flex-1 bg-red-50 text-red-600 py-2 rounded-lg text-sm font-medium hover:bg-red-100">
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}