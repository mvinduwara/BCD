import { apiClient } from './client';
import type { CustomsDocument } from './types';

export const customsApi = {
    list: () => apiClient.get<CustomsDocument[]>('/customs/documents'),
    get: (id: number) => apiClient.get<CustomsDocument>(`/customs/documents/${id}`),
    create: (document: Omit<CustomsDocument, 'id' | 'status'>) =>
        apiClient.post<CustomsDocument>('/customs/documents', document),
    deadlines: () => apiClient.get<CustomsDocument[]>('/customs/deadlines'),
};