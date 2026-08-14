import { useState, type FormEvent, useEffect } from "react";
import { Link, Navigate, useNavigate } from "react-router";
import { useAuth } from "../../contexts/AuthContext";
import { useAuthStore } from "../../store/auth-store";
import { IconArrowRight, IconPackage } from "../../components/common/icons";
import { APP_NAME } from "../../constants";
import { apiPost, ApiError } from "../../services/api-client";

export default function RegisterBusinessPage() {
  const navigate = useNavigate();
  const auth = useAuth();
  const authStore = useAuthStore();
  const [error, setError] = useState<string[] | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const [form, setForm] = useState({
    name: "",
    logo: "",
    addressNumber: "",
    addressStreet: "",
    addressArea: "",
    addressLga: "",
    addressState: "",
    addressCountry: "",
    phoneNumber: "",
    contactInfo: "",
    operatingHours: "",
    taxNumber: "",
    currency: "NGN",
  });

  useEffect(() => {
    if (!auth.isAuthenticated) {
      navigate("/register", { replace: true });
    }
  }, [auth.isAuthenticated, navigate]);

  if (!auth.isAuthenticated) {
    return <Navigate to="/register" replace />;
  }

  if (auth.user?.hasStore) {
    return <Navigate to="/" replace />;
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    try {
      const store = await apiPost<{ id: string; name: string; currency: string }>('/stores', {
        name: form.name,
        logo: form.logo,
        addressNumber: form.addressNumber,
        addressStreet: form.addressStreet,
        addressArea: form.addressArea,
        addressLga: form.addressLga,
        addressState: form.addressState,
        addressCountry: form.addressCountry,
        phoneNumber: form.phoneNumber,
        contactInfo: form.contactInfo,
        operatingHours: form.operatingHours,
        taxNumber: form.taxNumber,
        currency: form.currency,
      });

      const updatedUser = {
        ...(authStore.getState().user || auth.user),
        hasStore: true,
        storeId: store.id,
        storeName: store.name,
        storeCurrency: store.currency,
      } as any;

      const currentToken = authStore.getState().token || auth.token;
      if (currentToken) {
        auth.login(currentToken, updatedUser);
        authStore.login(currentToken, updatedUser);
      }

      navigate("/", { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.errors.length > 0 ? err.errors : [err.message]);
      } else {
        setError(['Business registration failed']);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const displayError = error;

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-[#F8FAFC]">
      <div className="w-full max-w-[1000px] bg-white rounded-[2rem] shadow-xl overflow-hidden flex flex-col md:flex-row border border-gray-100">
        
        {/* Left — Form */}
        <div className="w-full md:w-1/2 p-10 lg:p-14 flex flex-col justify-center relative z-10">
          
          <div className="mb-8 text-center md:text-left">
            <Link to="/" className="inline-flex items-center gap-2 mb-6">
              <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center shadow-sm">
                <IconPackage className="w-4 h-4 text-white" />
              </div>
              <span className="text-xl font-bold text-gray-900 tracking-tight">{APP_NAME}</span>
            </Link>
            <h1 className="text-3xl lg:text-4xl font-extrabold text-gray-900 tracking-tight leading-tight">
              Register your business
            </h1>
            <p className="text-base text-gray-500 mt-2 font-medium">
              Set up your business profile to get started.
            </p>
          </div>

          {displayError && (
            <div className="px-4 py-3 rounded-xl bg-red-50 border border-red-100/50 text-red-600 text-sm font-medium mb-6 animate-fade-in shadow-sm">
              {Array.isArray(displayError) ? (
                displayError.map((err, i) => (
                  <div key={i} className="flex items-center">
                    <span className="mr-2">⚠</span> {err}
                  </div>
                ))
              ) : (
                <div className="flex items-center">
                  <span className="mr-2">⚠</span> {displayError}
                </div>
              )}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            
            <div className="space-y-1.5">
              <label htmlFor="name" className="block text-sm font-semibold text-gray-700">Business Name</label>
              <input
                id="name"
                type="text"
                value={form.name}
                onChange={(e) => { setForm({ ...form, name: e.target.value }); }}
                required
                placeholder="Acme Trading Ltd"
                className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
              />
            </div>

            <div className="space-y-1.5">
              <label htmlFor="logo" className="block text-sm font-semibold text-gray-700">Logo URL <span className="text-gray-400 font-normal">(optional)</span></label>
              <input
                id="logo"
                type="text"
                value={form.logo}
                onChange={(e) => { setForm({ ...form, logo: e.target.value }); }}
                placeholder="https://example.com/logo.png"
                className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label htmlFor="addressNumber" className="block text-sm font-semibold text-gray-700">House / Office Number</label>
                <input
                  id="addressNumber"
                  type="text"
                  value={form.addressNumber}
                  onChange={(e) => { setForm({ ...form, addressNumber: e.target.value }); }}
                  placeholder="12A"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
              <div className="space-y-1.5">
                <label htmlFor="addressStreet" className="block text-sm font-semibold text-gray-700">Street</label>
                <input
                  id="addressStreet"
                  type="text"
                  value={form.addressStreet}
                  onChange={(e) => { setForm({ ...form, addressStreet: e.target.value }); }}
                  placeholder="Broad Street"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <label htmlFor="addressArea" className="block text-sm font-semibold text-gray-700">Area</label>
              <input
                id="addressArea"
                type="text"
                value={form.addressArea}
                onChange={(e) => { setForm({ ...form, addressArea: e.target.value }); }}
                placeholder="Victoria Island"
                className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label htmlFor="addressLga" className="block text-sm font-semibold text-gray-700">LGA</label>
                <input
                  id="addressLga"
                  type="text"
                  value={form.addressLga}
                  onChange={(e) => { setForm({ ...form, addressLga: e.target.value }); }}
                  placeholder="Eti-Osa"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
              <div className="space-y-1.5">
                <label htmlFor="addressState" className="block text-sm font-semibold text-gray-700">State</label>
                <input
                  id="addressState"
                  type="text"
                  value={form.addressState}
                  onChange={(e) => { setForm({ ...form, addressState: e.target.value }); }}
                  placeholder="Lagos"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label htmlFor="addressCountry" className="block text-sm font-semibold text-gray-700">Country</label>
                <input
                  id="addressCountry"
                  type="text"
                  value={form.addressCountry}
                  onChange={(e) => { setForm({ ...form, addressCountry: e.target.value }); }}
                  placeholder="Nigeria"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
              <div className="space-y-1.5">
                <label htmlFor="phoneNumber" className="block text-sm font-semibold text-gray-700">Business Phone</label>
                <input
                  id="phoneNumber"
                  type="tel"
                  value={form.phoneNumber}
                  onChange={(e) => { setForm({ ...form, phoneNumber: e.target.value }); }}
                  placeholder="+234 801 234 5678"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label htmlFor="operatingHours" className="block text-sm font-semibold text-gray-700">Operating Hours</label>
                <input
                  id="operatingHours"
                  type="text"
                  value={form.operatingHours}
                  onChange={(e) => { setForm({ ...form, operatingHours: e.target.value }); }}
                  placeholder="Mon-Fri 8am - 6pm"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
              <div className="space-y-1.5">
                <label htmlFor="taxNumber" className="block text-sm font-semibold text-gray-700">Tax Number</label>
                <input
                  id="taxNumber"
                  type="text"
                  value={form.taxNumber}
                  onChange={(e) => { setForm({ ...form, taxNumber: e.target.value }); }}
                  placeholder="TIN / VAT ID"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label htmlFor="contactInfo" className="block text-sm font-semibold text-gray-700">Contact Info</label>
                <input
                  id="contactInfo"
                  type="text"
                  value={form.contactInfo}
                  onChange={(e) => { setForm({ ...form, contactInfo: e.target.value }); }}
                  placeholder="support@example.com"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
              <div className="space-y-1.5">
                <label htmlFor="currency" className="block text-sm font-semibold text-gray-700">Currency</label>
                <select
                  id="currency"
                  value={form.currency}
                  onChange={(e) => { setForm({ ...form, currency: e.target.value }); }}
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                >
                  <option value="NGN">NGN - ₦</option>
                  <option value="USD">USD - $</option>
                  <option value="EUR">EUR - €</option>
                  <option value="GBP">GBP - £</option>
                  <option value="GHS">GHS - GH₵</option>
                  <option value="KES">KES - KSh</option>
                  <option value="ZAR">ZAR - R</option>
                </select>
              </div>
            </div>

            <div className="pt-2">
              <button
                type="submit"
                disabled={isLoading}
                className="w-full justify-center py-6 text-base shadow-md bg-primary-500 text-white rounded-xl font-medium hover:bg-primary-600 active:bg-primary-700 transition-all disabled:opacity-40 disabled:pointer-events-none inline-flex items-center justify-center gap-2"
              >
                {isLoading ? "Setting up business..." : "Complete Setup"}
                <IconArrowRight className="w-5 h-5" />
              </button>
            </div>
          </form>

          <p className="text-center text-sm font-medium text-gray-500 mt-8">
            Skip for now?{" "}
            <button
              type="button"
              onClick={() => navigate("/", { replace: true })}
              className="text-primary-600 font-bold hover:text-primary-700"
            >
              Go to dashboard
            </button>
          </p>
        </div>

        {/* Right — Image */}
        <div className="hidden md:flex w-1/2 bg-gray-900 relative p-12 items-center justify-center overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-br from-primary-900/50 to-gray-900/80 pointer-events-none" />
          <div className="relative z-10 text-white max-w-sm">
            <div className="w-16 h-16 rounded-2xl bg-white/10 backdrop-blur border border-white/20 flex items-center justify-center mb-6">
               <IconPackage className="w-8 h-8 text-primary-400" />
            </div>
            <h2 className="text-3xl font-extrabold tracking-tight mb-4">
              Set up your store
            </h2>
            <p className="text-lg text-gray-300 font-medium leading-relaxed">
              Add your business details and start managing inventory with StockPulse.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
}
