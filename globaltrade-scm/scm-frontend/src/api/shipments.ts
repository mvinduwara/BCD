import { apiClient } from './client';
import type { Shipment } from './types';

export const shipmentsApi = {
    list: () => apiClient.get<Shipment[]>('/shipments'),
    get: (id: number) => apiClient.get<Shipment>(`/shipments/${id}`),
    create: (shipment: Omit<Shipment, 'id' | 'status'>) => apiClient.post<Shipment>('/shipments', shipment),
    updateStatus: (id: number, status: ShipmentStatus) => apiClient.put<Shipment>(`/shipments/${id}/status`, { status }),
};

type ShipmentStatus = Shipment['status'];