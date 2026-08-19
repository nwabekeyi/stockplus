import { useState, useRef, useEffect } from "react";
import { Link, useLocation, Outlet } from "react-router";
import {
  IconBell,
  IconSettings,
  IconSun,
  IconMoon,
  IconUser,
  IconChevronDown,
  IconLogout,
  IconX,
  IconHome,
  IconPackage2,
  IconDollarSign,
  IconTruck,
  IconUsers,
  IconUserPlus,
  IconCreditCard,
  IconBarChart2,
  IconMenu,
  IconPackage,
  IconAlertTriangle,
  IconReceipt,
} from "../common/icons";
import Logo from "../common/Logo";
import { useAuth } from "../../contexts/AuthContext";
import { useTheme } from "../../contexts/ThemeContext";

function TopBar({ onMenuClick }: { onMenuClick?: () => void }) {
  const [showSettings, setShowSettings] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const settingsRef = useRef<HTMLDivElement>(null);
  const notifRef = useRef<HTMLDivElement>(null);
  const { user, logout } = useAuth();
  const { toggleTheme, isDark } = useTheme();

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (settingsRef.current && !settingsRef.current.contains(e.target as Node)) {
        setShowSettings(false);
      }
      if (notifRef.current && !notifRef.current.contains(e.target as Node)) {
        setShowNotifications(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = async () => {
    setShowSettings(false);
    await logout();
  };

  return (
    <div className="sticky top-0 z-30 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200/60 dark:border-slate-700/60">
      <div className="flex items-center justify-between h-14 px-4 sm:px-8">
        <div className="flex items-center gap-3">
          {/* Mobile menu button */}
          <button
            onClick={onMenuClick}
            className="p-2 rounded-xl text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors lg:hidden"
          >
            <IconMenu className="w-5 h-5" />
          </button>
          <h2 className="text-sm font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
            Workspace
          </h2>
        </div>

        <div className="flex items-center gap-2">
          {/* Notifications */}
          <div className="relative" ref={notifRef}>
            <button
              onClick={() => {
                setShowNotifications(!showNotifications);
                setShowSettings(false);
              }}
              className="relative p-2 rounded-xl text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            >
              <IconBell className="w-5 h-5" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full ring-2 ring-white dark:ring-slate-900" />
            </button>

            {showNotifications && (
              <div className="absolute right-0 top-full mt-2 w-80 bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-xl overflow-hidden">
                <div className="px-4 py-3 border-b border-slate-100 dark:border-slate-700 flex items-center justify-between">
                  <span className="text-sm font-bold text-slate-900 dark:text-white">
                    Notifications
                  </span>
                  <button
                    onClick={() => setShowNotifications(false)}
                    className="text-slate-400 hover:text-slate-600"
                  >
                    <IconX className="w-4 h-4" />
                  </button>
                </div>
                <div className="p-4 text-sm text-slate-500 dark:text-slate-400 text-center py-8">
                  No new notifications
                </div>
              </div>
            )}
          </div>

          {/* Settings / Profile */}
          <div className="relative" ref={settingsRef}>
            <button
              onClick={() => {
                setShowSettings(!showSettings);
                setShowNotifications(false);
              }}
              className="flex items-center gap-2 p-2 rounded-xl text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            >
              <IconSettings className="w-5 h-5" />
              <IconChevronDown className="w-4 h-4 hidden sm:block" />
            </button>

            {showSettings && (
              <div className="absolute right-0 top-full mt-2 w-64 bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-xl overflow-hidden">
                <div className="px-4 py-3 border-b border-slate-100 dark:border-slate-700">
                  <p className="text-sm font-bold text-slate-900 dark:text-white">
                    {user?.firstName} {user?.lastName}
                  </p>
                  <p className="text-xs text-slate-500 dark:text-slate-400 truncate">
                    {user?.email}
                  </p>
                </div>
                <div className="py-1.5">
                  <Link
                    to="/settings"
                    onClick={() => setShowSettings(false)}
                    className="flex items-center gap-3 px-4 py-2.5 text-sm font-medium text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors"
                  >
                    <IconUser className="w-4 h-4 text-slate-400" /> Profile & Settings
                  </Link>
                  <button
                    onClick={toggleTheme}
                    className="w-full flex items-center gap-3 px-4 py-2.5 text-sm font-medium text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors"
                  >
                    {isDark ? (
                      <IconSun className="w-4 h-4 text-amber-500" />
                    ) : (
                      <IconMoon className="w-4 h-4 text-slate-400" />
                    )}
                    {isDark ? "Light Mode" : "Dark Mode"}
                  </button>
                  <button
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-4 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
                  >
                    <IconLogout className="w-4 h-4" /> Sign out
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function Sidebar({ isOpen, onClose }: { isOpen: boolean; onClose: () => void }) {
  const location = useLocation();
  const { user, logout } = useAuth();

  const navItems = [
    { to: "/dashboard", icon: IconHome, label: "Dashboard", match: (p: string) => p === "/dashboard" },
    { to: "/inventory", icon: IconPackage2, label: "Inventory", match: (p: string) => p.startsWith("/inventory") },
    { to: "/transactions", icon: IconDollarSign, label: "Sales", match: (p: string) => p.startsWith("/transactions") },
    { to: "/purchases", icon: IconTruck, label: "Purchases", match: (p: string) => p.startsWith("/purchases") },
    { to: "/suppliers", icon: IconUsers, label: "Suppliers", match: (p: string) => p.startsWith("/suppliers") },
    { to: "/customers", icon: IconUserPlus, label: "Customers", match: (p: string) => p.startsWith("/customers") },
    { to: "/expenses", icon: IconCreditCard, label: "Expenses", match: (p: string) => p.startsWith("/expenses") },
    { to: "/reports", icon: IconBarChart2, label: "Reports", match: (p: string) => p.startsWith("/reports") },
    { to: "/returns", icon: IconReceipt, label: "Returns", match: (p: string) => p.startsWith("/returns") },
    { to: "/notifications", icon: IconAlertTriangle, label: "Alerts", match: (p: string) => p.startsWith("/notifications") },
    { to: "/beks-tech", icon: IconPackage, label: "Beks Tech", match: (p: string) => p.startsWith("/beks-tech") },
  ];

  // Close sidebar on route change (mobile)
  useEffect(() => {
    onClose();
  }, [location.pathname, onClose]);

  const userName = user ? `${user.firstName} ${user.lastName}`.trim() : "User";
  const userInitials = user
    ? `${user.firstName?.[0] ?? ""}${user.lastName?.[0] ?? ""}`.toUpperCase() || "?"
    : "?";

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-gray-900/40 backdrop-blur-sm z-40 lg:hidden transition-opacity"
          onClick={onClose}
        />
      )}

      <aside
        className={`fixed top-0 left-0 h-full w-64 bg-white/80 dark:bg-slate-900/80 backdrop-blur-xl border-r border-slate-200/50 dark:border-slate-700/50 z-50 flex flex-col transform transition-transform duration-300 ease-in-out lg:translate-x-0 ${
          isOpen ? "translate-x-0 shadow-2xl" : "-translate-x-full"
        }`}
      >
        {/* Logo */}
        <div className="px-5 py-5 border-b border-slate-100/50 dark:border-slate-700/50 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-3">
            <Logo textClassName="text-base font-bold text-gray-900 dark:text-white tracking-tight" />
          </Link>
          <button
            onClick={onClose}
            className="p-2 rounded-lg text-gray-500 hover:bg-gray-100 lg:hidden"
          >
            <IconX className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto">
          {navItems.map(({ to, icon: Icon, label, match }) => {
            const active = match(location.pathname);
            return (
              <Link
                key={to}
                to={to}
                className={`flex items-center gap-3.5 px-3.5 py-3 rounded-xl text-sm font-medium transition-all ${
                  active
                    ? "bg-primary-50 dark:bg-primary-900/40 text-primary-700 dark:text-primary-300 font-semibold"
                    : "text-gray-500 dark:text-slate-400 hover:bg-gray-50 dark:hover:bg-slate-800 hover:text-gray-900 dark:hover:text-slate-200"
                }`}
              >
                <Icon
                  className={`w-5 h-5 ${
                    active
                      ? "text-primary-600 dark:text-primary-400"
                      : "text-gray-400 dark:text-slate-500"
                  }`}
                />
                {label}
              </Link>
            );
          })}
        </nav>

        {/* User info + logout */}
        <div className="border-t border-slate-100/50 dark:border-slate-700/50 p-4 space-y-3">
          <div className="flex items-center gap-3 p-2 rounded-xl bg-slate-50 dark:bg-slate-800/60">
            <div className="w-10 h-10 rounded-full bg-primary-100 dark:bg-primary-900/60 flex items-center justify-center text-sm font-bold text-primary-700 dark:text-primary-300 shrink-0 uppercase shadow-sm">
              {userInitials}
            </div>
            <div className="min-w-0">
              <p className="text-sm font-semibold text-gray-900 dark:text-white truncate">
                {userName}
              </p>
              <p className="text-xs font-medium text-gray-500 dark:text-slate-400 truncate">
                {user?.email ?? ""}
              </p>
            </div>
          </div>
          <button
            onClick={logout}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm font-medium text-gray-500 dark:text-slate-400 hover:bg-red-50 dark:hover:bg-red-900/20 hover:text-red-600 transition-colors cursor-pointer"
          >
            <IconLogout className="w-4.5 h-4.5" />
            Sign out
          </button>
        </div>
      </aside>
    </>
  );
}

function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <main className="flex-1 min-h-screen pt-0 lg:ml-64 transition-all duration-300">
        <TopBar onMenuClick={() => setSidebarOpen(true)} />
        <div className="max-w-[1400px] mx-auto px-4 sm:px-8 py-8 lg:py-10">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default AppLayout;