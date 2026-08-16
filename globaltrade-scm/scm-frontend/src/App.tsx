import { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Login } from './pages/Login';
import { Dashboard } from './pages/dashboard/Dashboard';
import { ShipmentsPage } from './pages/shipments/ShipmentsPage';
import { VendorsPage } from './pages/vendors/VendorsPage';
import { InventoryPage } from './pages/inventory/InventoryPage';
import { CustomsPage } from './pages/customs/CustomsPage';
import { authApi } from './api/auth';
import { useAuthStore } from './store/authStore';

export function App() {
    const status = useAuthStore((state) => state.status);
    const setSession = useAuthStore((state) => state.setSession);
    const setStatus = useAuthStore((state) => state.setStatus);

    useEffect(() => {
        let cancelled = false;
        setStatus('checking');
        authApi
            .me()
            .then((session) => {
                if (!cancelled) setSession(session);
            })
            .catch(() => {
                if (!cancelled) setStatus('ready');
            });
        return () => {
            cancelled = true;
        };
    }, [setSession, setStatus]);

    if (status === 'idle' || status === 'checking') {
        return (
            <div className="min-h-screen bg-paper flex items-center justify-center">
                <p className="font-mono text-xs tracking-widest uppercase text-ink-500">Loading</p>
            </div>
        );
    }

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route
                    element={
                        <ProtectedRoute>
                            <Layout />
                        </ProtectedRoute>
                    }
                >
                    <Route path="/" element={<Dashboard />} />
                    <Route
                        path="/shipments"
                        element={
                            <ProtectedRoute roles={['COORDINATOR', 'CUSTOMS_AGENT', 'WAREHOUSE_MANAGER', 'VENDOR_REPRESENTATIVE']}>
                                <ShipmentsPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/vendors"
                        element={
                            <ProtectedRoute roles={['COORDINATOR', 'WAREHOUSE_MANAGER', 'VENDOR_REPRESENTATIVE']}>
                                <VendorsPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/inventory"
                        element={
                            <ProtectedRoute roles={['COORDINATOR', 'WAREHOUSE_MANAGER']}>
                                <InventoryPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/customs"
                        element={
                            <ProtectedRoute roles={['COORDINATOR', 'CUSTOMS_AGENT']}>
                                <CustomsPage />
                            </ProtectedRoute>
                        }
                    />
                </Route>
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </BrowserRouter>
    );
}