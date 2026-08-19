import { useEffect, useState, type FormEvent } from 'react';
import { purchaseOrdersApi } from '../../api/purchaseOrders';
import { vendorsApi } from '../../api/vendors';
import { inventoryApi } from '../../api/inventory';
import type { PurchaseOrder, Vendor, InventoryItem } from '../../api/types';
import { StatusIndicator, type StatusTone } from '../../components/StatusIndicator';
import { useAuthStore } from '../../store/authStore';
import { ApiError } from '../../api/client';

function toneForStatus(status: PurchaseOrder['status']): StatusTone {
    switch (status) {
        case 'CANCELLED':
            return 'attention';
        case 'PLACED':
        case 'CONFIRMED':
            return 'progress';
        case 'FULFILLED':
            return 'resolved';
        default:
            return 'neutral';
    }
}

export function PurchaseOrdersPage() {
    const session = useAuthStore((state) => state.session);
    const [orders, setOrders] = useState<PurchaseOrder[]>([]);
    const [vendors, setVendors] = useState<Vendor[]>([]);
    const [items, setItems] = useState<InventoryItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [formError, setFormError] = useState<string | null>(null);
    const [vendorId, setVendorId] = useState('');
    const [inventoryItemId, setInventoryItemId] = useState('');
    const [quantity, setQuantity] = useState('');
    const [actionError, setActionError] = useState<string | null>(null);

    async function refresh() {
        setLoading(true);
        const [orderData, vendorData, itemData] = await Promise.all([
            purchaseOrdersApi.list(),
            vendorsApi.list(),
            inventoryApi.list(),
        ]);
        setOrders(orderData);
        setVendors(vendorData);
        setItems(itemData);
        setLoading(false);
    }

    useEffect(() => {
        refresh();
    }, []);

    function vendorName(id: number) {
        return vendors.find((vendor) => vendor.id === id)?.name ?? `Vendor #${id}`;
    }

    function itemSku(id: number) {
        return items.find((item) => item.id === id)?.sku ?? `Item #${id}`;
    }

    async function handlePlace(event: FormEvent) {
        event.preventDefault();
        setFormError(null);
        const parsedVendorId = Number.parseInt(vendorId, 10);
        const parsedItemId = Number.parseInt(inventoryItemId, 10);
        const parsedQuantity = Number.parseInt(quantity, 10);
        if (Number.isNaN(parsedVendorId) || Number.isNaN(parsedItemId) || Number.isNaN(parsedQuantity) || parsedQuantity <= 0) {
            setFormError('Choose a vendor, an item, and a positive quantity');
            return;
        }
        await purchaseOrdersApi.place({ vendorId: parsedVendorId, inventoryItemId: parsedItemId, quantity: parsedQuantity });
        setVendorId('');
        setInventoryItemId('');
        setQuantity('');
        setShowForm(false);
        refresh();
    }

    async function handleConfirm(id: number) {
        setActionError(null);
        try {
            await purchaseOrdersApi.confirm(id);
            refresh();
        } catch (err) {
            if (err instanceof ApiError && err.status === 403) {
                setActionError('You can only confirm purchase orders for your own vendor');
            } else {
                setActionError('Could not confirm that order');
            }
        }
    }

    async function handleFulfill(id: number) {
        setActionError(null);
        try {
            await purchaseOrdersApi.fulfill(id);
            refresh();
        } catch {
            setActionError('Could not fulfill that order');
        }
    }

    const canPlace = session?.role === 'COORDINATOR' || session?.role === 'WAREHOUSE_MANAGER';
    const canConfirm = session?.role === 'COORDINATOR' || session?.role === 'VENDOR_REPRESENTATIVE';
    const canFulfill = session?.role === 'COORDINATOR' || session?.role === 'WAREHOUSE_MANAGER';

    return (
        <div>
            <div className="flex items-center justify-between mb-8">
                <div>
                    <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-1">Procurement</p>
                    <h1 className="font-sans text-2xl font-medium">Purchase orders</h1>
                </div>
                {canPlace && (
                    <button
                        type="button"
                        onClick={() => setShowForm((value) => !value)}
                        className="border border-ink-900 px-4 py-2 text-sm font-medium hover:bg-ink-900 hover:text-paper"
                    >
                        {showForm ? 'Cancel' : 'New order'}
                    </button>
                )}
            </div>

            {showForm && (
                <form onSubmit={handlePlace} className="border border-ink-200 p-6 mb-8 grid grid-cols-3 gap-4">
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Vendor</label>
                        <select
                            value={vendorId}
                            onChange={(event) => setVendorId(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900 bg-paper"
                        >
                            <option value="">Select a vendor</option>
                            {vendors.map((vendor) => (
                                <option key={vendor.id} value={vendor.id}>
                                    {vendor.name}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Item</label>
                        <select
                            value={inventoryItemId}
                            onChange={(event) => setInventoryItemId(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900 bg-paper"
                        >
                            <option value="">Select an item</option>
                            {items.map((item) => (
                                <option key={item.id} value={item.id}>
                                    {item.sku} — {item.description}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Quantity</label>
                        <input
                            value={quantity}
                            onChange={(event) => setQuantity(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    {formError && <p className="col-span-3 text-sm text-ink-900 border-l-2 border-ink-900 pl-3">{formError}</p>}
                    <button type="submit" className="col-span-3 bg-ink-900 text-paper py-2.5 text-sm font-medium">
                        Place order
                    </button>
                </form>
            )}

            {actionError && <p className="text-sm text-ink-900 border-l-2 border-ink-900 pl-3 mb-4">{actionError}</p>}

            {loading ? (
                <p className="text-sm text-ink-500">Loading</p>
            ) : orders.length === 0 ? (
                <p className="text-sm text-ink-500 border border-ink-200 p-6">No purchase orders yet</p>
            ) : (
                <table className="w-full text-sm border border-ink-200">
                    <thead>
                    <tr className="border-b border-ink-200 text-left">
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Vendor</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Item</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Qty</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Ordered</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Status</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-ink-200">
                    {orders.map((order) => (
                        <tr key={order.id}>
                            <td className="p-3">{vendorName(order.vendorId)}</td>
                            <td className="p-3 font-mono">{itemSku(order.inventoryItemId)}</td>
                            <td className="p-3 font-mono">{order.quantity}</td>
                            <td className="p-3 font-mono text-ink-700">{order.orderDate}</td>
                            <td className="p-3">
                                <StatusIndicator label={order.status} tone={toneForStatus(order.status)} />
                            </td>
                            <td className="p-3">
                                <div className="flex gap-3">
                                    {canConfirm && order.status === 'PLACED' && (
                                        <button
                                            type="button"
                                            onClick={() => handleConfirm(order.id)}
                                            className="text-xs font-mono uppercase tracking-widest text-ink-700 hover:text-ink-900 hover:underline"
                                        >
                                            Confirm
                                        </button>
                                    )}
                                    {canFulfill && order.status === 'CONFIRMED' && (
                                        <button
                                            type="button"
                                            onClick={() => handleFulfill(order.id)}
                                            className="text-xs font-mono uppercase tracking-widest text-ink-700 hover:text-ink-900 hover:underline"
                                        >
                                            Fulfill
                                        </button>
                                    )}
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}