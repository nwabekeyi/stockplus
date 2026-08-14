export const API_BASE = '/api/v1'

export type UserRole = 'ROLE_USER' | 'ROLE_ADMIN'
export type SubscriptionStatus = 'ACTIVE' | 'CANCELLED' | 'EXPIRED' | 'PENDING' | 'TRIAL'
export type BillingInterval = 'MONTHLY' | 'YEARLY'
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED'
export type PurchaseStatus = 'PENDING' | 'RECEIVED' | 'CANCELLED'
export type SupplierStatus = 'ACTIVE' | 'INACTIVE'
export type CustomerStatus = 'ACTIVE' | 'INACTIVE'
export type TransferStatus = 'PENDING' | 'IN_TRANSIT' | 'RECEIVED' | 'CANCELLED'
export type ExpenseCategory = 'RENT' | 'ELECTRICITY' | 'TRANSPORT' | 'SALARY' | 'INTERNET' | 'REPAIRS' | 'PACKAGING' | 'DELIVERY' | 'FUEL' | 'OTHER'
export type MovementType = 'OPENING' | 'PURCHASE' | 'SALE' | 'DAMAGED' | 'RETURN' | 'ADJUSTMENT' | 'TRANSFER'
export type UnitOfMeasure = 'PIECE' | 'CARTON' | 'PACK' | 'KILOGRAM' | 'LITRE' | 'KEG' | 'BAG' | 'BOX' | 'DOZEN' | 'METER' | 'OTHER'
export type ProductLocationType = 'SHELF' | 'WHOLESALE' | 'WAREHOUSE' | 'OTHER'

export interface User {
  id: string
  email: string
  firstName: string
  lastName: string
  role: UserRole
  hasStore: boolean
  storeId?: string
  accessToken?: string
}

export interface AuthResponse {
  id: string
  email: string
  firstName: string
  lastName: string
  role: UserRole
  hasStore: boolean
  storeId?: string
  storeName?: string
  storeCurrency?: string
  accessToken: string
}

export interface Store {
  id: string
  name: string
  logo?: string
  addressNumber?: string
  addressStreet?: string
  addressArea?: string
  addressLga?: string
  addressState?: string
  addressCountry?: string
  phoneNumber?: string
  contactInfo?: string
  operatingHours?: string
  taxNumber?: string
  currency: string
  active: boolean
}

export interface SubscriptionPlan {
  id: string
  name: string
  description?: string
  price: number
  billingInterval: BillingInterval
  maxProducts: number
  maxUsers: number
  maxBranches: number
  trialDays: number
  annualPrice?: number
  heroPlan: boolean
  whatsappEnabled: boolean
  whatsappCommerceEnabled: boolean
  whatsappCommerceCommissionPercent: number
  advancedReportsEnabled: boolean
  apiEnabled: boolean
  active: boolean
  features?: string
  createdAt?: string
  updatedAt?: string
}

export interface Subscription {
  id: string
  status: SubscriptionStatus
  paystackSubscriptionCode?: string
  startDate: string
  endDate: string
  autoRenew: boolean
  plan: SubscriptionPlan
}

export interface Category {
  id: string
  name: string
  description?: string
}

export interface Stock {
  quantity: number
  lowStockThreshold: number
  unit: UnitOfMeasure
  trackInventory: boolean
  batchNumber?: string
  expiryDate?: string
  minStockLevel: number
  maxStockLevel?: number
}

export interface Product {
  id: string
  name: string
  description?: string
  sellingPrice: number
  costPrice: number
  wholesalePrice: number
  sku: string
  barcode?: string
  image?: string
  active: boolean
  archived: boolean
  minStockLevel: number
  maxStockLevel?: number
  batchNumber?: string
  expiryDate?: string
  category?: Category
  supplier?: Supplier
  stock?: Stock
  images?: ProductImage[]
  locations?: ProductLocation[]
  wholesaleRules?: WholesalePriceRule[]
}

export interface ProductImage {
  id: string
  url: string
  altText?: string
  sortOrder: number
}

export interface ProductLocation {
  id: string
  locationType: ProductLocationType
  locationName: string
  quantity: number
}

export interface WholesalePriceRule {
  id: string
  minQuantity: number
  maxQuantity?: number
  price: number
}

export interface SaleItem {
  productId: string
  productName: string
  quantity: number
  unitPrice: number
  costPrice: number
  subtotal: number
}

export interface Sale {
  id: string
  customerName?: string
  customerPhone?: string
  totalAmount: number
  totalCost: number
  profit: number
  saleDate: string
  paymentMethod?: string
  notes?: string
  items: SaleItem[]
}

export interface DashboardStats {
  totalProducts: number
  lowStockCount: number
  totalSalesToday: number
  revenueToday: number
  revenueThisMonth: number
  totalSalesThisMonth: number
  customerDebt: number
  supplierDebt: number
  expensesToday: number
  totalCustomers: number
  totalSuppliers: number
}

export interface PaystackInitResponse {
  authorizationUrl: string
  accessCode: string
  reference: string
}

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

export interface Purchase {
  id: string
  reference: string
  supplierId?: string
  totalAmount: number
  amountPaid: number
  outstanding: number
  status: string
  purchaseDate: string
  items: { productName: string; quantity: number; costPrice: number; subtotal: number }[]
}

export interface Expense {
  id: string
  category: string
  amount: number
  description?: string
  expenseDate: string
}

export interface StockMovement {
  id: string
  productId: string
  productName: string
  quantity: number
  movementType: string
  previousQuantity: number
  newQuantity: number
  reference?: string
  reason?: string
  createdAt: string
}

export interface Transfer {
  id: string
  reference: string
  fromStoreId: string
  toStoreId: string
  productId: string
  productName: string
  quantity: number
  status: string
  notes?: string
  createdAt: string
  receivedAt?: string
}

export interface Branch {
  id: string
  name: string
  address?: string
  phone?: string
  manager?: string
  active: boolean
  createdAt: string
}

export interface AuditLog {
  id: string
  userId: string
  userName: string
  action: string
  entityType: string
  entityId: string
  oldValue?: string
  newValue?: string
  createdAt: string
}

export interface FinancialSummary {
  totalSales: number
  totalCost: number
  grossProfit: number
  totalExpenses: number
  netProfit: number
  customerDebt: number
  supplierDebt: number
  date: string
}

export interface CartItem {
  productId: string
  productName: string
  quantity: number
  unitPrice: number
  costPrice: number
}

export interface ReturnItem {
  productId: string
  productName: string
  quantity: number
  unitPrice: number
  restock: boolean
}

export interface ReturnRecord {
  id: string
  saleId?: string
  reference: string
  reason: string
  refundAmount: number
  status: string
  refundMethod?: string
  approvedBy?: string
  createdAt: string
  items: ReturnItem[]
}

export interface Notification {
  id: string
  title: string
  message: string
  channel: 'DASHBOARD' | 'EMAIL' | 'PUSH' | 'WHATSAPP'
  status: 'UNREAD' | 'READ' | 'SENT' | 'FAILED'
  target?: string
  createdAt: string
}
