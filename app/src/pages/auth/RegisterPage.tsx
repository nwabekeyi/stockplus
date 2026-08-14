import { useState, type FormEvent } from "react";
import { Link, Navigate, useNavigate } from "react-router";
import { useAuth } from "../../contexts/AuthContext";
import { useAuthStore } from "../../store/auth-store";
import { IconEye, IconEyeOff, IconPackage } from "../../components/common/icons";
import { APP_NAME } from "../../constants";
import { apiPost, ApiError } from "../../services/api-client";

export default function RegisterPage() {
  const navigate = useNavigate();
  const auth = useAuth();
  const authStore = useAuthStore();
  const [error, setError] = useState<string[] | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [validationError, setValidationError] = useState("");

  if (auth.isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setValidationError("");

    if (form.password !== form.confirmPassword) {
      setValidationError("Passwords do not match.");
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      const data = await apiPost<{ accessToken: string; user: any }>('/auth/register', {
        firstName: form.firstName,
        lastName: form.lastName,
        email: form.email,
        password: form.password,
      });
      const token = data.accessToken || data.token;
      const user = data.user || data;
      auth.login(token, user);
      authStore.login(token, user);
      navigate("/login", { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.errors.length > 0 ? err.errors : [err.message]);
      } else {
        setError(['Registration failed']);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const displayError = validationError || error;

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
              Create your account
            </h1>
            <p className="text-base text-gray-500 mt-2 font-medium">
              Get started with StockPulse today.
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
            
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label htmlFor="firstName" className="block text-sm font-semibold text-gray-700">First Name</label>
                <input
                  id="firstName"
                  type="text"
                  value={form.firstName}
                  onChange={(e) => { setForm({ ...form, firstName: e.target.value }); }}
                  required
                  autoComplete="given-name"
                  placeholder="John"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
              <div className="space-y-1.5">
                <label htmlFor="lastName" className="block text-sm font-semibold text-gray-700">Last Name</label>
                <input
                  id="lastName"
                  type="text"
                  value={form.lastName}
                  onChange={(e) => { setForm({ ...form, lastName: e.target.value }); }}
                  required
                  autoComplete="family-name"
                  placeholder="Doe"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <label htmlFor="email" className="block text-sm font-semibold text-gray-700">Email Address</label>
              <input
                id="email"
                type="email"
                value={form.email}
                onChange={(e) => { setForm({ ...form, email: e.target.value }); }}
                required
                autoComplete="email"
                placeholder="you@example.com"
                className="w-full px-4 py-3 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
              />
            </div>

            <div className="space-y-1.5">
              <label htmlFor="password" className="block text-sm font-semibold text-gray-700">Password</label>
              <div className="relative">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={form.password}
                  onChange={(e) => { setForm({ ...form, password: e.target.value }); setValidationError(""); }}
                  required
                  autoComplete="new-password"
                  placeholder="••••••••"
                  minLength={8}
                  className="w-full px-4 py-3 pr-12 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer p-1 rounded-md transition-colors"
                  tabIndex={-1}
                >
                  {showPassword ? <IconEyeOff className="w-5 h-5" /> : <IconEye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            <div className="space-y-1.5">
              <label htmlFor="confirmPassword" className="block text-sm font-semibold text-gray-700">Confirm Password</label>
              <div className="relative">
                <input
                  id="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  value={form.confirmPassword}
                  onChange={(e) => { setForm({ ...form, confirmPassword: e.target.value }); setValidationError(""); }}
                  required
                  autoComplete="new-password"
                  placeholder="••••••••"
                  minLength={8}
                  className="w-full px-4 py-3 pr-12 rounded-xl border border-gray-200/80 bg-gray-50/50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all font-medium"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer p-1 rounded-md transition-colors"
                  tabIndex={-1}
                >
                  {showConfirmPassword ? <IconEyeOff className="w-5 h-5" /> : <IconEye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            <div className="pt-2">
              <button
                type="submit"
                disabled={isLoading}
                className="w-full justify-center py-6 text-base shadow-md bg-primary-500 text-white rounded-xl font-medium hover:bg-primary-600 active:bg-primary-700 transition-all disabled:opacity-40 disabled:pointer-events-none"
              >
                {isLoading ? "Creating account..." : "Create Account"}
              </button>
            </div>
          </form>

          <p className="text-center text-sm font-medium text-gray-500 mt-8">
            Already have an account?{" "}
            <Link to="/login" className="text-primary-600 font-bold hover:text-primary-700">
              Sign in
            </Link>
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
              Your business, organized.
            </h2>
            <p className="text-lg text-gray-300 font-medium leading-relaxed">
              Join thousands of stores managing inventory with StockPulse.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
}
