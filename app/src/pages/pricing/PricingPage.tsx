import { useEffect, useState } from 'react'
import { apiGet, apiPost } from '../../services/api-client'
import { SubscriptionPlan } from '../../types'
import { getPlansWithFallback } from '../../services/offline-db'
import { useStoreId } from '../../hooks/use-store-id'

const featureRows = [
  ['Products', '500', '5,000', 'Unlimited'],
  ['POS, inventory, barcode, purchasing, suppliers, expenses', '✓', '✓', '✓'],
  ['Profit reports', 'Basic', 'Advanced', 'Advanced'],
  ['Staff accounts', '1', '5', '15'],
  ['Offline PWA and PDF receipts', '✓', '✓', '✓'],
  ['WhatsApp Commerce and Business Inbox', '—', '✓', '✓'],
  ['Automated and human WhatsApp conversations', '—', '✓', '✓'],
  ['Advanced permissions and analytics', '—', '✓', '✓'],
  ['Multiple branches and API access', '—', '—', '✓'],
  ['Priority support', '—', '✓', '✓'],
]

export default function PricingPage() {
  const storeId = useStoreId()
  const [plans, setPlans] = useState<SubscriptionPlan[]>([])
  const [quotePlanId, setQuotePlanId] = useState('')
  const [orderAmount, setOrderAmount] = useState(100000)
  const [processingFee, setProcessingFee] = useState(1600)
  const [quote, setQuote] = useState<{ platformCommission: number; merchantSettlement: number; commissionPercent: number } | null>(null)

  useEffect(() => {
    getPlansWithFallback(() => apiGet<SubscriptionPlan[]>('/subscriptions/plans')).then((data) => {
      setPlans(data)
      setQuotePlanId(data.find((plan) => plan.heroPlan)?.id || data[0]?.id || '')
    }).catch(() => {})
  }, [])

  const startTrial = async () => {
    if (!storeId) return
    await apiPost(`/subscriptions/trial?storeId=${storeId}`, {})
    alert('Your 14-day Business trial has started.')
  }

  const calculateQuote = async () => {
    const data = await apiPost<{ platformCommission: number; merchantSettlement: number; commissionPercent: number }>('/subscriptions/commerce-fee-quote', {
      planId: quotePlanId,
      orderAmount,
      paymentProcessingFee: processingFee,
    })
    setQuote(data)
  }

  return (
    <div className="space-y-8 animate-fade-in">
      <section className="rounded-3xl bg-gradient-to-br from-primary-950 via-slate-900 to-emerald-900 p-8 text-white shadow-sm">
        <p className="text-xs font-black uppercase tracking-[0.3em] text-emerald-200">Pricing & Monetization</p>
        <div className="mt-5 max-w-4xl space-y-4">
          <h1 className="text-3xl font-black tracking-tight sm:text-5xl">14-day Business trial, then affordable SaaS pricing.</h1>
          <p className="text-lg leading-8 text-emerald-50">No permanent free operating tier: businesses get full Business-plan value first, then choose Starter, Business, Growth or Enterprise. WhatsApp Commerce creates usage-based revenue without mixing platform commission with Paystack fees.</p>
        </div>
        <button onClick={startTrial} className="mt-8 rounded-2xl bg-white px-6 py-3 text-sm font-black text-primary-800 shadow-sm">Start 14-Day Free Trial</button>
      </section>

      <section className="grid gap-5 lg:grid-cols-3">
        {plans.filter((plan) => plan.name !== 'Enterprise').map((plan) => (
          <article key={plan.id} className={`rounded-3xl border bg-white p-6 shadow-sm ${plan.heroPlan ? 'border-primary-300 ring-4 ring-primary-100' : 'border-gray-200'}`}>
            {plan.heroPlan && <p className="mb-3 inline-flex rounded-full bg-primary-100 px-3 py-1 text-xs font-black uppercase tracking-widest text-primary-700">Recommended</p>}
            <h2 className="text-2xl font-black text-gray-900">{plan.name}</h2>
            <p className="mt-2 min-h-12 text-sm font-medium text-gray-500">{plan.description}</p>
            <p className="mt-5 text-4xl font-black text-gray-900">₦{plan.price.toLocaleString()}<span className="text-sm font-bold text-gray-400">/month</span></p>
            <p className="mt-1 text-sm font-semibold text-emerald-700">₦{plan.annualPrice?.toLocaleString()} yearly · about 2 months free</p>
            <div className="mt-5 space-y-2 text-sm font-semibold text-gray-600">
              <p>{plan.maxProducts < 0 ? 'Unlimited' : plan.maxProducts.toLocaleString()} products</p>
              <p>{plan.maxUsers < 0 ? 'Unlimited' : plan.maxUsers} staff accounts</p>
              <p>{plan.maxBranches < 0 ? 'Unlimited' : plan.maxBranches} branch{plan.maxBranches === 1 ? '' : 'es'}</p>
              <p>{plan.whatsappCommerceEnabled ? `${plan.whatsappCommerceCommissionPercent}% WhatsApp Commerce commission` : 'No WhatsApp Commerce'}</p>
            </div>
          </article>
        ))}
      </section>

      <section className="overflow-hidden rounded-3xl border border-gray-200 bg-white shadow-sm">
        <div className="border-b border-gray-100 p-6"><h2 className="text-xl font-black text-gray-900">Launch pricing feature matrix</h2></div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50"><tr><th className="p-4 text-left">Feature</th><th className="p-4">Starter</th><th className="p-4">Business</th><th className="p-4">Growth</th></tr></thead>
            <tbody className="divide-y divide-gray-100">
              {featureRows.map(([feature, starter, business, growth]) => <tr key={feature}><td className="p-4 font-semibold text-gray-700">{feature}</td><td className="p-4 text-center">{starter}</td><td className="p-4 text-center font-bold text-primary-700">{business}</td><td className="p-4 text-center">{growth}</td></tr>)}
            </tbody>
          </table>
        </div>
      </section>

      <section className="grid gap-5 lg:grid-cols-2">
        <div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-gray-900">WhatsApp Commerce fee quote</h2>
          <p className="mt-2 text-sm text-gray-500">Platform commission is calculated separately from payment-processing fees.</p>
          <div className="mt-5 grid gap-3">
            <select value={quotePlanId} onChange={(event) => setQuotePlanId(event.target.value)} className="rounded-xl border border-gray-200 px-3 py-2 text-sm">
              {plans.map((plan) => <option key={plan.id} value={plan.id}>{plan.name}</option>)}
            </select>
            <input type="number" value={orderAmount} onChange={(event) => setOrderAmount(Number(event.target.value))} className="rounded-xl border border-gray-200 px-3 py-2 text-sm" />
            <input type="number" value={processingFee} onChange={(event) => setProcessingFee(Number(event.target.value))} className="rounded-xl border border-gray-200 px-3 py-2 text-sm" />
            <button onClick={calculateQuote} className="rounded-xl bg-primary-600 px-4 py-2 text-sm font-bold text-white">Calculate settlement</button>
          </div>
          {quote && <div className="mt-5 rounded-2xl bg-gray-50 p-4 text-sm font-semibold text-gray-700"><p>Commission: {quote.commissionPercent}% = ₦{quote.platformCommission.toLocaleString()}</p><p>Merchant settlement: ₦{quote.merchantSettlement.toLocaleString()}</p></div>}
        </div>
        <div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-gray-900">Enterprise and future monetization</h2>
          <p className="mt-2 text-sm leading-6 text-gray-500">Enterprise is custom for large staff counts, branches, API integrations, custom reports, accounting/ERP integrations, higher transaction volumes, dedicated onboarding, SLAs and account management.</p>
          <div className="mt-5 grid gap-2 text-sm font-semibold text-gray-700 sm:grid-cols-2">
            {['Premium analytics', 'AI business assistant', 'Advanced WhatsApp automation', 'Extra staff/branches', 'Accounting integrations', 'ERP integrations', 'Supplier integrations', 'Premium support'].map((item) => <span key={item} className="rounded-xl bg-gray-50 p-3">{item}</span>)}
          </div>
        </div>
      </section>
    </div>
  )
}
