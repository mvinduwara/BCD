import { apiClient } from './client';
import type { PurchaseOrder, PlacePurchaseOrderRequest } from './types';

export const purchaseOrdersApi = {
    list: () => apiClient.get<PurchaseOrder[]>('/purchase-orders'),
    place: (request: PlacePurchaseOrderRequest) => apiClient.post<PurchaseOrder>('/purchase-orders', request),
    confirm: (id: number) => apiClient.put<PurchaseOrder>(`/purchase-orders/${id}/confirm`, {}),
    fulfill: (id: number) => apiClient.put<PurchaseOrder>(`/purchase-orders/${id}/fulfill`, {}),
};