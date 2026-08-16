import { create } from 'zustand';
import type { Session } from '../api/types';

interface AuthState {
    session: Session | null;
    status: 'idle' | 'checking' | 'ready';
    setSession: (session: Session) => void;
    clearSession: () => void;
    setStatus: (status: AuthState['status']) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
    session: null,
    status: 'idle',
    setSession: (session) => set({ session, status: 'ready' }),
    clearSession: () => set({ session: null, status: 'ready' }),
    setStatus: (status) => set({ status }),
}));