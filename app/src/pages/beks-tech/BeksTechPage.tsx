const mvpModules = [
  'Authentication and business onboarding',
  'Business/store management with Nigerian Naira defaults',
  'Users and basic owner/manager/cashier/storekeeper roles',
  'Products, categories, units, SKU/barcode and reorder levels',
  'Inventory quantities, adjustments and auditable stock movements',
  'Suppliers, purchases and stock receiving',
  'Sales, customers and customer credit/debt tracking',
  'Dashboard, basic reports, low-stock alerts and critical audit logs',
]

const featureAreas = [
  ['Inventory ledger', 'Opening stock, purchases, sales, returns, losses, damage, expiry, adjustments and transfers with previous/new quantities.'],
  ['POS & Nigerian payments', 'Fast cart workflow with cash, bank transfer, POS/card, USSD, partial payment and credit statuses.'],
  ['Customer credit', 'Credit limits, outstanding balances, due dates, partial repayments and reminder-ready records.'],
  ['Purchasing & suppliers', 'Purchase orders, receiving, delivery costs, supplier balances, payment history and supplier summaries.'],
  ['Profitability', 'Revenue, COGS, gross profit, expenses, net profit, receivables, payables and inventory value.'],
  ['Branches & transfers', 'Branch-specific inventory, staff, sales, expenses and request/approve/dispatch/receive transfer workflow.'],
  ['Expiry & batches', 'Batch numbers, expiry dates, expiring-soon views, expired stock and FEFO-ready stock selection.'],
  ['Audit & permissions', 'Trace price changes, stock adjustments, voids, discounts, returns and user/permission changes.'],
]

const roadmap = [
  ['Phase 1', 'Foundation', 'Auth, business setup, products, categories, inventory, stock movements, users and roles.'],
  ['Phase 2', 'Commerce', 'Sales, purchases, customers, suppliers, credit and dashboard.'],
  ['Phase 3', 'Operations', 'POS, barcode, returns, expenses, branches, transfers and advanced permissions.'],
  ['Phase 4', 'Intelligence', 'Advanced reports, profit/loss, inventory valuation, alerts and analytics.'],
  ['Phase 5', 'Connectivity', 'Offline POS, WhatsApp, payment integrations, reconciliation and public APIs.'],
  ['Phase 6', 'SaaS Scale', 'Subscriptions, billing, enterprise controls, integrations and advanced multi-tenant capabilities.'],
]

const providers = [
  ['Paystack', 'Subscription billing and future card/bank payment collection for Nigerian businesses.'],
  ['WhatsApp Business Platform', 'Planned debt reminders, expiry alerts, low-stock nudges and receipt sharing where commercially appropriate.'],
  ['Email/SMPP or push provider', 'Fallback operational notifications for alerts, invitations and reminders.'],
  ['PostgreSQL', 'Primary transactional database for auditable inventory and financial records.'],
  ['Redis', 'Caching/session support for fast dashboards and low-bandwidth API usage.'],
]

export default function BeksTechPage() {
  return (
    <div className="space-y-8 animate-fade-in">
      <section className="overflow-hidden rounded-3xl bg-gradient-to-br from-emerald-950 via-slate-900 to-primary-900 text-white shadow-sm">
        <div className="px-6 py-8 sm:px-10 sm:py-12">
          <p className="text-xs font-bold uppercase tracking-[0.3em] text-emerald-200">.BEKS TECH · Version 1.0 · August 2026</p>
          <div className="mt-5 max-w-4xl space-y-4">
            <h1 className="text-3xl font-black tracking-tight sm:text-5xl">Inventory & Retail Management System</h1>
            <p className="text-base leading-7 text-emerald-50 sm:text-lg">
              A Nigerian SME operating system for products, inventory, purchases, sales, customers, supplier credit, expenses, branches, staff permissions, reporting and profitability.
            </p>
          </div>
          <div className="mt-8 grid gap-3 sm:grid-cols-2 lg:grid-cols-5">
            {['What do I have?', 'What did I buy?', 'What did I sell?', 'What do I owe?', 'How profitable am I?'].map((question) => (
              <div key={question} className="rounded-2xl border border-white/15 bg-white/10 p-4 text-sm font-semibold backdrop-blur">
                {question}
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="grid gap-5 lg:grid-cols-3">
        <div className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm lg:col-span-2">
          <h2 className="text-xl font-extrabold text-gray-900">Recommended MVP scope</h2>
          <p className="mt-2 text-sm font-medium text-gray-500">The first release focuses on the workflows that immediately reduce stock loss and improve sales/debt visibility.</p>
          <div className="mt-5 grid gap-3 sm:grid-cols-2">
            {mvpModules.map((module, index) => (
              <div key={module} className="flex gap-3 rounded-xl border border-gray-100 bg-gray-50/70 p-3">
                <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-primary-100 text-xs font-black text-primary-700">{index + 1}</span>
                <span className="text-sm font-semibold text-gray-700">{module}</span>
              </div>
            ))}
          </div>
        </div>
        <div className="rounded-2xl border border-emerald-200 bg-emerald-50 p-6 shadow-sm">
          <h2 className="text-xl font-extrabold text-emerald-950">Nigeria-first defaults</h2>
          <ul className="mt-4 space-y-3 text-sm font-semibold text-emerald-900">
            <li>₦ Nigerian Naira currency formatting.</li>
            <li>Cash, bank transfer, POS/card and USSD payments.</li>
            <li>Piece, pack, carton, bag, kilogram and litre units.</li>
            <li>Customer/supplier credit workflows for informal retail.</li>
            <li>Low-bandwidth UI and efficient API usage.</li>
          </ul>
        </div>
      </section>

      <section className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-extrabold text-gray-900">Core feature modules</h2>
        <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {featureAreas.map(([title, copy]) => (
            <article key={title} className="rounded-2xl border border-gray-100 p-4">
              <h3 className="font-bold text-gray-900">{title}</h3>
              <p className="mt-2 text-sm leading-6 text-gray-500">{copy}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="grid gap-5 lg:grid-cols-2">
        <div className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-extrabold text-gray-900">Product roadmap</h2>
          <div className="mt-5 space-y-3">
            {roadmap.map(([phase, theme, scope]) => (
              <div key={phase} className="rounded-xl border border-gray-100 p-4">
                <p className="text-xs font-black uppercase tracking-widest text-primary-600">{phase} · {theme}</p>
                <p className="mt-1 text-sm font-medium leading-6 text-gray-600">{scope}</p>
              </div>
            ))}
          </div>
        </div>
        <div className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-extrabold text-gray-900">Providers used / planned</h2>
          <p className="mt-2 text-sm font-medium text-gray-500">These providers align the platform with Nigerian payment, notification and SaaS operations.</p>
          <div className="mt-5 space-y-3">
            {providers.map(([name, use]) => (
              <div key={name} className="rounded-xl bg-gray-50 p-4">
                <p className="font-bold text-gray-900">{name}</p>
                <p className="mt-1 text-sm leading-6 text-gray-500">{use}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  )
}
