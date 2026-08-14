export const APP_NAME = 'Beks Tech'
export const API_BASE = '/api/v1'

export const TAB_ITEMS = [
  { id: 'dashboard', label: 'Home', icon: 'FiHome' },
  { id: 'inventory', label: 'Stock', icon: 'FiPackage' },
  { id: 'transactions', label: 'Sales', icon: 'FiDollarSign' },
  { id: 'purchases', label: 'Purchases', icon: 'FiTruck' },
  { id: 'suppliers', label: 'Suppliers', icon: 'FiUsers' },
  { id: 'customers', label: 'Customers', icon: 'FiUser' },
  { id: 'expenses', label: 'Expenses', icon: 'FiCreditCard' },
  { id: 'reports', label: 'Reports', icon: 'FiBarChart' },
  { id: 'subscription', label: 'Plan', icon: 'FiCreditCard' },
] as const

export type TabId = typeof TAB_ITEMS[number]['id']
