import { createBrowserRouter, Navigate } from 'react-router'
import AppLayout from '../components/layout/AppLayout'
import LoginPage from '../pages/auth/LoginPage'
import RegisterPage from '../pages/auth/RegisterPage'
import RegisterBusinessPage from '../pages/auth/RegisterBusinessPage'
import LandingPage from '../pages/landing/LandingPage'
import DashboardPage from '../pages/dashboard/DashboardPage'
import InventoryPage from '../pages/inventory/InventoryPage'
import SalesPage from '../pages/transactions/SalesPage'
import SubscriptionPage from '../pages/subscription/SubscriptionPage'
import AdminPlansPage from '../pages/admin/AdminPlansPage'
import PurchasesPage from '../pages/purchases/PurchasesPage'
import SuppliersPage from '../pages/suppliers/SuppliersPage'
import CustomersPage from '../pages/customers/CustomersPage'
import ExpensesPage from '../pages/expenses/ExpensesPage'
import ReportsPage from '../pages/reports/ReportsPage'
import StockMovementsPage from '../pages/stock-movements/StockMovementsPage'
import TransfersPage from '../pages/transfers/TransfersPage'
import BranchesPage from '../pages/branches/BranchesPage'
import AuditLogsPage from '../pages/audit-logs/AuditLogsPage'
import POSPage from '../pages/pos/POSPage'
import BeksTechPage from '../pages/beks-tech/BeksTechPage'
import ReturnsPage from '../pages/returns/ReturnsPage'
import NotificationsPage from '../pages/notifications/NotificationsPage'
import PricingPage from '../pages/pricing/PricingPage'
import SettingsPage from '../pages/settings/SettingsPage'
import { useAuth } from '../contexts/AuthContext'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading, user } = useAuth()

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="w-12 h-12 border-4 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (user && !user.hasStore) {
    return <Navigate to="/register-business" replace />
  }

  return <>{children}</>
}

export const router = createBrowserRouter([
  {
    path: '/',
    element: <LandingPage />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    path: '/register-business',
    element: <RegisterBusinessPage />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: <DashboardPage /> },
      { path: 'inventory', element: <InventoryPage /> },
      { path: 'transactions', element: <SalesPage /> },
      { path: 'purchases', element: <PurchasesPage /> },
      { path: 'suppliers', element: <SuppliersPage /> },
      { path: 'customers', element: <CustomersPage /> },
      { path: 'expenses', element: <ExpensesPage /> },
      { path: 'reports', element: <ReportsPage /> },
      { path: 'subscription', element: <SubscriptionPage /> },
      { path: 'pricing', element: <PricingPage /> },
      { path: 'admin/plans', element: <AdminPlansPage /> },
      { path: 'stock-movements', element: <StockMovementsPage /> },
      { path: 'transfers', element: <TransfersPage /> },
      { path: 'branches', element: <BranchesPage /> },
      { path: 'audit-logs', element: <AuditLogsPage /> },
      { path: 'pos', element: <POSPage /> },
      { path: 'returns', element: <ReturnsPage /> },
      { path: 'notifications', element: <NotificationsPage /> },
      { path: 'beks-tech', element: <BeksTechPage /> },
      { path: 'settings', element: <SettingsPage /> },
    ],
  },
])

export default router
