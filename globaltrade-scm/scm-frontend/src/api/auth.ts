import { apiClient } from './client';
import type { Session } from './types';

export const authApi = {
    login: (username: string, password: string) => apiClient.post<Session>('/auth/login', { username, password }),
    me: () => apiClient.get<Session>('/auth/me'),
    logout: () => apiClient.post<void>('/auth/logout'),
};