import { Link } from 'react-router'
import { IconArrowUpRight, IconShoppingCart, IconPackage2, IconBarChart2, IconUsers, IconDollarSign } from '../../components/common/icons'
import Logo from '../../components/common/Logo'
import { APP_NAME } from '../../constants'

const features = [
  {
    icon: IconPackage2,
    title: 'Inventory Management',
    description: 'Track stock levels, manage products, and get low-stock alerts in real-time across all your locations.',
  },
  {
    icon: IconShoppingCart,
    title: 'Point of Sale',
    description: 'Fast, intuitive checkout with receipt printing, multiple payment methods, and instant inventory updates.',
  },
  {
    icon: IconBarChart2,
    title: 'Financial Reports',
    description: 'Daily, monthly, and custom reports showing revenue, profit, expenses, and customer debt at a glance.',
  },
  {
    icon: IconUsers,
    title: 'Customer & Supplier Hub',
    description: 'Manage relationships, track outstanding balances, and maintain complete contact histories.',
  },
  {
    icon: IconDollarSign,
    title: 'Expense Tracking',
    description: 'Record and categorize every business expense to understand where your money is going.',
  },
  {
    icon: IconPackage2,
    title: 'Multi-Branch Support',
    description: 'Run multiple branches from one account with role-based access and branch-level reporting.',
  },
]

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-white dark:bg-slate-900 font-sans text-slate-900 dark:text-slate-100 antialiased">
      {/* Navigation */}
      <nav className="fixed top-0 w-full z-50 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200/60 dark:border-slate-700/60">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <Link to="/" className="flex items-center gap-2.5">
              <Logo textClassName="text-lg font-bold text-slate-900 dark:text-white tracking-tight" />
            </Link>

            <div className="hidden md:flex items-center gap-8">
              <Link to="#features" className="text-sm font-medium text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400 transition-colors">Features</Link>
              <Link to="#how-it-works" className="text-sm font-medium text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400 transition-colors">How It Works</Link>
              <Link to="#pricing" className="text-sm font-medium text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400 transition-colors">Pricing</Link>
            </div>

            <div className="flex items-center gap-3">
              <Link
                to="/login"
                className="text-sm font-semibold text-slate-700 dark:text-slate-200 hover:text-slate-900 dark:hover:text-white px-4 py-2 transition-colors"
              >
                Sign in
              </Link>
              <Link
                to="/register"
                className="text-sm font-semibold text-white bg-primary-600 hover:bg-primary-700 px-5 py-2.5 rounded-xl shadow-sm transition-all"
              >
                Get Started
              </Link>
            </div>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <section className="relative pt-32 pb-20 lg:pt-40 lg:pb-28 overflow-hidden">
        <div className="absolute inset-0 -z-10">
          <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[500px] bg-primary-500/10 dark:bg-primary-400/5 rounded-full blur-3xl" />
        </div>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary-50 dark:bg-primary-900/30 border border-primary-100 dark:border-primary-700/50 text-primary-700 dark:text-primary-300 text-xs font-bold uppercase tracking-wider mb-6">
            <span className="w-1.5 h-1.5 rounded-full bg-primary-500 animate-pulse" />
            Now in public beta
          </div>
          <h1 className="text-4xl sm:text-5xl lg:text-7xl font-extrabold tracking-tight text-slate-900 dark:text-white leading-[1.1] mb-6">
            Manage your store<br className="hidden sm:block" />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary-600 to-primary-400">without the chaos</span>
          </h1>
          <p className="text-lg sm:text-xl text-slate-600 dark:text-slate-300 max-w-2xl mx-auto mb-10 leading-relaxed">
            {APP_NAME} brings inventory, sales, customers, and finances into one clean workspace. Built for modern businesses that want clarity and speed.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link
              to="/register"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 bg-primary-600 text-white px-8 py-4 rounded-2xl text-base font-bold hover:bg-primary-700 transition-all shadow-lg shadow-primary-500/20"
            >
              Start for free <IconArrowUpRight className="w-5 h-5" />
            </Link>
            <Link
              to="/login"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-200 border border-slate-200 dark:border-slate-700 px-8 py-4 rounded-2xl text-base font-bold hover:bg-slate-50 dark:hover:bg-slate-700 transition-all"
            >
              Sign in
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section id="features" className="py-20 bg-slate-50 dark:bg-slate-800/50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900 dark:text-white mb-4">Everything you need to run your store</h2>
            <p className="text-lg text-slate-600 dark:text-slate-300 max-w-xl mx-auto">From checkout to cash flow, {APP_NAME} handles the details so you can focus on growing.</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature) => (
              <div key={feature.title} className="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-700 p-6 shadow-sm hover:shadow-md transition-shadow">
                <div className="w-12 h-12 rounded-xl bg-primary-50 dark:bg-primary-900/40 flex items-center justify-center mb-4">
                  <feature.icon className="w-6 h-6 text-primary-600 dark:text-primary-400" />
                </div>
                <h3 className="text-lg font-bold text-slate-900 dark:text-white mb-2">{feature.title}</h3>
                <p className="text-sm text-slate-600 dark:text-slate-300 leading-relaxed">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900 dark:text-white mb-4">Ready to simplify your store?</h2>
          <p className="text-lg text-slate-600 dark:text-slate-300 mb-8">Join hundreds of businesses already using {APP_NAME} to manage their operations.</p>
          <Link
            to="/register"
            className="inline-flex items-center gap-2 bg-primary-600 text-white px-8 py-4 rounded-2xl text-base font-bold hover:bg-primary-700 transition-all shadow-lg shadow-primary-500/20"
          >
            Get Started Free <IconArrowUpRight className="w-5 h-5" />
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mb-8">
            <div>
              <h4 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider mb-4">Product</h4>
              <ul className="space-y-2">
                <li><Link to="#features" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Features</Link></li>
                <li><Link to="#pricing" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Pricing</Link></li>
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Integrations</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider mb-4">Company</h4>
              <ul className="space-y-2">
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">About</Link></li>
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Blog</Link></li>
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Careers</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider mb-4">Support</h4>
              <ul className="space-y-2">
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Help Center</Link></li>
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Contact</Link></li>
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Status</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider mb-4">Legal</h4>
              <ul className="space-y-2">
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Privacy</Link></li>
                <li><Link to="#" className="text-sm text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Terms</Link></li>
              </ul>
            </div>
          </div>
          <div className="border-t border-slate-200 dark:border-slate-700 pt-8 flex flex-col sm:flex-row items-center justify-between gap-4">
            <p className="text-sm text-slate-500 dark:text-slate-400">© {new Date().getFullYear()} {APP_NAME}. All rights reserved.</p>
            <div className="flex items-center gap-6">
              <Link to="/login" className="text-sm font-medium text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Sign in</Link>
              <Link to="/register" className="text-sm font-medium text-slate-600 dark:text-slate-300 hover:text-primary-600 dark:hover:text-primary-400">Get Started</Link>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}
