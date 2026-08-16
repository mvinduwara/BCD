import { apiClient } from './client';
import type { InventoryItem } from './types';

export const inventoryApi = {
    list: () => apiClient.get<InventoryItem[]>('/inventory'),
    get: (itemId: number) => apiClient.get<InventoryItem>(`/inventory/${itemId}`),
    updateQuantity: (itemId: number, quantityOnHand: number) =>
        apiClient.put<InventoryItem>(`/inventory/${itemId}/quantity`, { quantityOnHand }),
    lowStock: () => apiClient.get<InventoryItem[]>('/inventory/low-stock'),
};