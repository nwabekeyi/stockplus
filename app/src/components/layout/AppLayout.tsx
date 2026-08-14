import { useState, useEffect } from "react";
import { Outlet, useLocation, Link } from "react-router";
import {
  IconHome,
  IconPackage2,
  IconDollarSign,
  IconTruck,
  IconUsers,
  IconUserPlus,
  IconCreditCard,
  IconBarChart2,
  IconMenu,
  IconX,
  IconLogout,
  IconPackage,
} from "../common/icons";
import { useAuth } from "../../contexts/AuthContext";
import { APP_NAME } from "../../constants";

function Sidebar({ isOpen, onClose }: { isOpen: boolean; onClose: () => void }) {
  const location = useLocation();
  const { user, logout } = useAuth();

  const navItems = [
    { to: "/", icon: IconHome, label: "Dashboard", match: (p: string) => p === "/" },
    { to: "/inventory", icon: IconPackage2, label: "Inventory", match: (p: string) => p.startsWith("/inventory") },
    { to: "/transactions", icon: IconDollarSign, label: "Sales", match: (p: string) => p.startsWith("/transactions") },
    { to: "/purchases", icon: IconTruck, label: "Purchases", match: (p: string) => p.startsWith("/purchases") },
    { to: "/suppliers", icon: IconUsers, label: "Suppliers", match: (p: string) => p.startsWith("/suppliers") },
    { to: "/customers", icon: IconUserPlus, label: "Customers", match: (p: string) => p.startsWith("/customers") },
    { to: "/expenses", icon: IconCreditCard, label: "Expenses", match: (p: string) => p.startsWith("/expenses") },
    { to: "/reports", icon: IconBarChart2, label: "Reports", match: (p: string) => p.startsWith("/reports") },
    { to: "/pricing", icon: IconCreditCard, label: "Pricing", match: (p: string) => p.startsWith("/pricing") },
    { to: "/returns", icon: IconCreditCard, label: "Returns", match: (p: string) => p.startsWith("/returns") },
    { to: "/notifications", icon: IconAlertTriangle, label: "Alerts", match: (p: string) => p.startsWith("/notifications") },
    { to: "/beks-tech", icon: IconPackage, label: "Beks Tech", match: (p: string) => p.startsWith("/beks-tech") },
  ];

  useEffect(() => {
    onClose();
  }, [location.pathname]);

  const userName = user ? `${user.firstName} ${user.lastName}`.trim() : "User";
  const userInitials = user ? `${user.firstName[0]}${user.lastName[0]}`.toUpperCase() : "?";

  return (
    <>
      {isOpen && (
        <div 
          className="fixed inset-0 bg-gray-900/40 backdrop-blur-sm z-40 lg:hidden transition-opacity"
          onClick={onClose}
        />
      )}

      <aside 
        className={`fixed top-0 left-0 h-full w-64 bg-white/80 backdrop-blur-xl border-r border-gray-200/50 z-50 flex flex-col transform transition-transform duration-300 ease-in-out lg:translate-x-0 ${isOpen ? "translate-x-0 shadow-2xl" : "-translate-x-full"}`}
      >
        <div className="px-5 py-5 border-b border-gray-100/50 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center shadow-sm">
              <IconPackage className="w-4.5 h-4.5 text-white" />
            </div>
            <span className="text-base font-bold text-gray-900 tracking-tight">
              {APP_NAME}
            </span>
          </Link>
          <button onClick={onClose} className="p-2 rounded-lg text-gray-500 hover:bg-gray-100 lg:hidden">
            <IconX className="w-5 h-5" />
          </button>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto">
          {navItems.map(({ to, icon: Icon, label, match }) => {
            const active = match(location.pathname);
            return (
              <Link
                key={to}
                to={to}
                className={`flex items-center gap-3.5 px-3.5 py-3 rounded-xl text-sm font-medium transition-all ${
                  active
                    ? "bg-primary-50 text-primary-700 font-semibold"
                    : "text-gray-500 hover:bg-gray-50/80 hover:text-gray-900"
                }`}
              >
                <Icon className={`w-5 h-5 ${active ? "text-primary-600" : "text-gray-400"}`} />
                {label}
              </Link>
            );
          })}
        </nav>

        <div className="border-t border-gray-100/50 p-4 space-y-3">
          <div className="flex items-center gap-3 p-2 rounded-xl bg-gray-50/50">
            <div className="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-sm font-bold text-primary-700 shrink-0 uppercase shadow-sm">
              {userInitials}
            </div>
            <div className="min-w-0">
              <p className="text-sm font-semibold text-gray-900 truncate">
                {userName}
              </p>
              <p className="text-xs font-medium text-gray-500 truncate">
                {user?.email ?? ""}
              </p>
            </div>
          </div>
          <button
            onClick={logout}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm font-medium text-gray-500 hover:bg-red-50 hover:text-red-600 transition-colors cursor-pointer"
          >
            <IconLogout className="w-4.5 h-4.5" />
            Sign out
          </button>
        </div>
      </aside>
    </>
  );
}

export default function AppLayout() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-[#F8FAFC] font-sans text-gray-900 antialiased flex flex-col lg:flex-row">
      
      <header className="lg:hidden fixed top-0 w-full h-16 bg-white/80 backdrop-blur-md border-b border-gray-200/50 z-30 flex items-center justify-between px-4">
        <Link to="/" className="flex items-center gap-2.5">
           <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center shadow-sm">
            <IconPackage className="w-4 h-4 text-white" />
          </div>
          <span className="text-sm font-bold text-gray-900 tracking-tight">{APP_NAME}</span>
        </Link>
        <button 
          onClick={() => setIsMobileMenuOpen(true)}
          className="p-2 rounded-md text-gray-600 hover:bg-gray-100 transition-colors"
        >
          <IconMenu className="w-6 h-6" />
        </button>
      </header>

      <Sidebar isOpen={isMobileMenuOpen} onClose={() => setIsMobileMenuOpen(false)} />

      <main className="flex-1 min-h-screen pt-16 lg:pt-0 lg:ml-64 transition-all duration-300">
        <div className="max-w-[1400px] mx-auto px-4 sm:px-8 py-8 lg:py-10">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
