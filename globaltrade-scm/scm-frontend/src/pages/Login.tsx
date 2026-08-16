import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';
import { useAuthStore } from '../store/authStore';
import { ApiError } from '../api/client';

export function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const setSession = useAuthStore((state) => state.setSession);
    const navigate = useNavigate();

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setError(null);
        if (!username.trim() || !password.trim()) {
            setError('Enter a username and password');
            return;
        }
        setLoading(true);
        try {
            const session = await authApi.login(username.trim(), password);
            setSession(session);
            navigate('/');
        } catch (err) {
            if (err instanceof ApiError && err.status === 401) {
                setError('Those credentials were not recognized');
            } else {
                setError('Could not reach the server');
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="min-h-screen bg-paper text-ink-900 flex items-center justify-center px-6">
            <div className="w-full max-w-sm">
                <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-1">GlobalTrade Logistics</p>
                <h1 className="font-sans text-2xl font-medium mb-8">Supply chain console</h1>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="username" className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">
                            Username
                        </label>
                        <input
                            id="username"
                            value={username}
                            onChange={(event) => setUsername(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                            autoComplete="username"
                        />
                    </div>
                    <div>
                        <label htmlFor="password" className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                            autoComplete="current-password"
                        />
                    </div>
                    {error && <p className="text-sm text-ink-900 border-l-2 border-ink-900 pl-3">{error}</p>}
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-ink-900 text-paper py-2.5 text-sm font-medium disabled:opacity-50"
                    >
                        {loading ? 'Signing in' : 'Sign in'}
                    </button>
                </form>
                <p className="mt-6 text-xs text-ink-500">
                    Demo accounts: coordinator1, customs1, warehouse1, vendor1 — any password
                </p>
            </div>
        </div>
    );
}