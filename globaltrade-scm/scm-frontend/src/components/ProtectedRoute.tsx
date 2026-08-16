import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import type { Role } from '../api/types';

interface ProtectedRouteProps {
    children: ReactNode;
    roles?: Role[];
}

export function ProtectedRoute({ children, roles }: ProtectedRouteProps) {
    const session = useAuthStore((state) => state.session);
    if (!session) {
        return <Navigate to="/login" replace />;
    }
    if (roles && !roles.includes(session.role)) {
        return <Navigate to="/" replace />;
    }
    return <>{children}</>;
}