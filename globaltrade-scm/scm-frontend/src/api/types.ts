export type ShipmentStatus = 'PENDING' | 'IN_TRANSIT' | 'CUSTOMS_HOLD' | 'DELIVERED' | 'DELAYED' | 'CANCELLED';

export interface Shipment {
    id: number;
    trackingNumber: string;
    origin: string;
    destination: string;
    status: ShipmentStatus;
    estimatedDeparture: string;
    estimatedArrival: string;
    vendorId: number;
    carrierId: number;
}

export interface Vendor {
    id: number;
    name: string;
    contactEmail: string;
    country: string;
    performanceScore: number;
    status: 'ACTIVE' | 'PENDING_REVIEW' | 'SUSPENDED';
}

export interface InventoryItem {
    id: number;
    sku: string;
    description: string;
    quantityOnHand: number;
    reorderThreshold: number;
    warehouseLocation: string;
}

export interface CustomsDocument {
    id: number;
    shipmentId: number;
    documentType: string;
    status: 'PENDING' | 'SUBMITTED' | 'APPROVED' | 'REJECTED';
    submissionDeadline: string;
    countryCode: string;
}

export interface PurchaseOrder {
    id: number;
    vendorId: number;
    inventoryItemId: number;
    quantity: number;
    status: 'PLACED' | 'CONFIRMED' | 'FULFILLED' | 'CANCELLED';
    orderDate: string;
}

export interface PlacePurchaseOrderRequest {
    vendorId: number;
    inventoryItemId: number;
    quantity: number;
}

export interface BatchUpdateResult {
    succeeded: number[];
    failed: { id: number; reason: string }[];
}

export type Role = 'COORDINATOR' | 'CUSTOMS_AGENT' | 'WAREHOUSE_MANAGER' | 'VENDOR_REPRESENTATIVE';

export interface Session {
    username: string;
    role: Role;
}