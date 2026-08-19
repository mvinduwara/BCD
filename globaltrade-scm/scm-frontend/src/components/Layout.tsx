import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { authApi } from '../api/auth';
import type { Role } from '../api/types';

const NAV_ITEMS: { to: string; label: string; roles: Role[] }[] = [
    { to: '/', label: 'Dashboard', roles: ['COORDINATOR', 'CUSTOMS_AGENT', 'WAREHOUSE_MANAGER', 'VENDOR_REPRESENTATIVE'] },
    { to: '/shipments', label: 'Shipments', roles: ['COORDINATOR', 'CUSTOMS_AGENT', 'WAREHOUSE_MANAGER', 'VENDOR_REPRESENTATIVE'] },
    { to: '/vendors', label: 'Vendors', roles: ['COORDINATOR', 'WAREHOUSE_MANAGER', 'VENDOR_REPRESENTATIVE'] },
    { to: '/inventory', label: 'Inventory', roles: ['COORDINATOR', 'WAREHOUSE_MANAGER'] },
    { to: '/customs', label: 'Customs', roles: ['COORDINATOR', 'CUSTOMS_AGENT'] },
    { to: '/purchase-orders', label: 'Purchase orders', roles: ['COORDINATOR', 'WAREHOUSE_MANAGER', 'VENDOR_REPRESENTATIVE'] },
];

export function Layout() {
    const session = useAuthStore((state) => state.session);
    const clearSession = useAuthStore((state) => state.clearSession);
    const navigate = useNavigate();

    async function handleLogout() {
        await authApi.logout();
        clearSession();
        navigate('/login');
    }

    const visibleItems = NAV_ITEMS.filter((item) => session && item.roles.includes(session.role));

    return (
        <div className="flex min-h-screen bg-paper text-ink-900">
            <aside className="w-60 shrink-0 border-r border-ink-200 flex flex-col">
                <div className="px-6 py-5 border-b border-ink-200">
                    <p className="font-mono text-xs tracking-widest uppercase text-ink-500">GlobalTrade Logistics</p>
                    <p className="font-sans text-lg font-medium">Supply Chain</p>
                </div>
                <nav className="flex-1 px-3 py-4 space-y-1">
                    {visibleItems.map((item) => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            end={item.to === '/'}
                            className={({ isActive }) =>
                                `block px-3 py-2 text-sm font-medium ${isActive ? 'bg-ink-900 text-paper' : 'text-ink-700 hover:bg-ink-50'}`
                            }
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </nav>
                <div className="px-6 py-4 border-t border-ink-200">
                    <p className="text-sm font-medium">{session?.username}</p>
                    <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mt-0.5">
                        {session?.role.replace(/_/g, ' ')}
                    </p>
                    <button
                        type="button"
                        onClick={handleLogout}
                        className="mt-3 text-xs font-mono uppercase tracking-widest text-ink-500 hover:text-ink-900"
                    >
                        Sign out
                    </button>
                </div>
            </aside>
            <main className="flex-1 px-10 py-8">
                <Outlet />
            </main>
        </div>
    );
}