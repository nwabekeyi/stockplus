import { useEffect, useState } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { SubscriptionPlan, Subscription } from '../../types'
import { getPlansWithFallback, isFreePlan } from '../../services/offline-db'
import { useStoreId } from '../../hooks/use-store-id'
import { FiCheck } from 'react-icons/fi'

export default function SubscriptionPage() {
  const storeId = useStoreId()
  const [plans, setPlans] = useState<SubscriptionPlan[]>([])
  const [subscription, setSubscription] = useState<Subscription | null>(null)
  const [loading, setLoading] = useState(true)
  const [initiating, setInitiating] = useState(false)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      const [plansData, subData] = await Promise.all([
        getPlansWithFallback(() => apiGet<SubscriptionPlan[]>('/subscriptions/plans')),
        apiGet<Subscription>(`/subscriptions/current?storeId=${storeId}`).catch(() => null),
      ])
      setPlans(plansData)
      setSubscription(subData)
    } finally {
      setLoading(false)
    }
  }

  const handleStartTrial = async () => {
    if (!storeId) return
    await apiPost(`/subscriptions/trial?storeId=${storeId}`, {})
    await fetchData()
  }

  const handleSubscribe = async (plan: SubscriptionPlan) => {
    if (!storeId || isFreePlan(plan.id)) return
    setInitiating(true)
    try {
      const data = await apiPost<{ authorizationUrl: string }>(`/subscriptions/initiate?storeId=${storeId}`, {
        planId: plan.id,
        billingInterval: plan.billingInterval,
      })

      if (data.authorizationUrl) {
        window.location.href = data.authorizationUrl
      } else {
        alert('Subscription initiated. Please complete payment.')
      }
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to initiate subscription')
    } finally {
      setInitiating(false)
    }
  }

  if (loading) return <div className="text-center text-gray-500">Loading...</div>

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"><div><h2 className="text-xl font-bold text-gray-900">Subscription Plans</h2><p className="text-sm text-gray-500">Start with a 14-day Business trial, then choose Starter, Business, Growth, or Enterprise.</p></div>{!subscription && <button onClick={handleStartTrial} className="rounded-lg bg-primary-600 px-4 py-2.5 text-sm font-bold text-white">Start 14-Day Trial</button>}</div>

      {subscription && subscription.status === 'ACTIVE' && (
        <div className="bg-green-50 border border-green-200 rounded-xl p-5 flex items-center gap-4">
          <div className="p-3 bg-green-100 rounded-lg">
            <FiCheck className="text-green-600" size={24} />
          </div>
          <div>
            <p className="text-base font-semibold text-green-800">Active: {subscription.plan.name}</p>
            <p className="text-sm text-green-600">Renews {new Date(subscription.endDate).toLocaleDateString()}</p>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {plans.map((plan) => (
          <div key={plan.id} className="bg-white rounded-xl shadow-sm p-6 border border-gray-100 flex flex-col">
            <div className="flex justify-between items-start mb-3">
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{plan.name}</h3>
                <p className="text-sm text-gray-500">{plan.billingInterval}</p>
              </div>
              <div className="text-right"><p className="text-xl font-bold text-primary-600">₦{plan.price.toLocaleString()}</p>{plan.heroPlan && <p className="text-xs font-bold text-emerald-600">Recommended</p>}</div>
            </div>

            <p className="text-sm text-gray-600 mb-2">{plan.description}</p><p className="mb-4 text-xs font-semibold text-emerald-700">Annual: {plan.annualPrice ? `₦${plan.annualPrice.toLocaleString()}` : 'Custom'} · Trial: {plan.trialDays} days</p>

            <div className="flex flex-wrap gap-2 mb-4">
              <span className="px-2 py-1 bg-gray-100 rounded-full text-xs text-gray-600">
                {plan.maxProducts} products
              </span>
              <span className="px-2 py-1 bg-gray-100 rounded-full text-xs text-gray-600">
                {plan.maxUsers} users
              </span>
              <span className="px-2 py-1 bg-gray-100 rounded-full text-xs text-gray-600">
                {plan.maxBranches} branches
              </span>
            </div>

            <div className="flex flex-wrap gap-2 mb-4">
              {plan.whatsappEnabled && (
                <span className="px-2 py-1 bg-blue-50 rounded-full text-xs text-blue-700">WhatsApp</span>
              )}
              {plan.whatsappCommerceEnabled && (
                <span className="px-2 py-1 bg-emerald-50 rounded-full text-xs text-emerald-700">Commerce {plan.whatsappCommerceCommissionPercent}%</span>
              )}
              {plan.advancedReportsEnabled && (
                <span className="px-2 py-1 bg-purple-50 rounded-full text-xs text-purple-700">Advanced Reports</span>
              )}
              {plan.apiEnabled && (
                <span className="px-2 py-1 bg-gray-100 rounded-full text-xs text-gray-700">API Access</span>
              )}
            </div>

            {plan.features && (
              <p className="text-sm text-gray-500 mb-4 flex-1">{plan.features}</p>
            )}

            {subscription?.status !== 'ACTIVE' && (
              <button
                onClick={() => handleSubscribe(plan)}
                disabled={initiating || isFreePlan(plan.id)}
                className="w-full bg-primary-600 text-white py-2.5 rounded-lg font-medium hover:bg-primary-700 disabled:opacity-50 mt-auto"
              >
                {isFreePlan(plan.id) ? 'Offline only' : initiating ? 'Opening Paystack checkout...' : 'Subscribe with Paystack'}
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}