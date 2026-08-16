import { apiClient } from './client';
import type { Vendor } from './types';

export const vendorsApi = {
    list: () => apiClient.get<Vendor[]>('/vendors'),
    get: (id: number) => apiClient.get<Vendor>(`/vendors/${id}`),
    create: (vendor: Omit<Vendor, 'id' | 'performanceScore' | 'status'>) => apiClient.post<Vendor>('/vendors', vendor),
    performance: (id: number) => apiClient.get<{ vendorId: number; score: number }>(`/vendors/${id}/performance`),
};