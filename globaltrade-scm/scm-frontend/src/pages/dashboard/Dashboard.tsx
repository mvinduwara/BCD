import { useEffect, useState } from 'react';
import { shipmentsApi } from '../../api/shipments';
import { inventoryApi } from '../../api/inventory';
import { customsApi } from '../../api/customs';
import type { Shipment, InventoryItem, CustomsDocument } from '../../api/types';
import { useAuthStore } from '../../store/authStore';

export function Dashboard() {
    const session = useAuthStore((state) => state.session);
    const [shipments, setShipments] = useState<Shipment[]>([]);
    const [lowStock, setLowStock] = useState<InventoryItem[]>([]);
    const [deadlines, setDeadlines] = useState<CustomsDocument[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let cancelled = false;
        async function load() {
            setLoading(true);
            const [shipmentData, lowStockData, deadlineData] = await Promise.all([
                shipmentsApi.list(),
                inventoryApi.lowStock(),
                customsApi.deadlines(),
            ]);
            if (!cancelled) {
                setShipments(shipmentData);
                setLowStock(lowStockData);
                setDeadlines(deadlineData);
                setLoading(false);
            }
        }
        load();
        return () => {
            cancelled = true;
        };
    }, []);

    const inTransit = shipments.filter((shipment) => shipment.status === 'IN_TRANSIT').length;
    const onHold = shipments.filter((shipment) => shipment.status === 'CUSTOMS_HOLD').length;

    return (
        <div>
            <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-1">Overview</p>
            <h1 className="font-sans text-2xl font-medium mb-8">Welcome back, {session?.username}</h1>
            {loading ? (
                <p className="text-sm text-ink-500">Loading</p>
            ) : (
                <div className="grid grid-cols-4 gap-px bg-ink-200 border border-ink-200">
                    <div className="bg-paper p-6">
                        <p className="text-3xl font-mono">{shipments.length}</p>
                        <p className="text-xs font-mono uppercase tracking-widest text-ink-500 mt-2">Active shipments</p>
                    </div>
                    <div className="bg-paper p-6">
                        <p className="text-3xl font-mono">{inTransit}</p>
                        <p className="text-xs font-mono uppercase tracking-widest text-ink-500 mt-2">In transit</p>
                    </div>
                    <div className="bg-paper p-6">
                        <p className="text-3xl font-mono">{onHold}</p>
                        <p className="text-xs font-mono uppercase tracking-widest text-ink-500 mt-2">Customs hold</p>
                    </div>
                    <div className="bg-paper p-6">
                        <p className="text-3xl font-mono">{lowStock.length}</p>
                        <p className="text-xs font-mono uppercase tracking-widest text-ink-500 mt-2">Low stock items</p>
                    </div>
                </div>
            )}

            <div className="grid grid-cols-2 gap-8 mt-10">
                <div>
                    <h2 className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-3">Upcoming customs deadlines</h2>
                    {deadlines.length === 0 ? (
                        <p className="text-sm text-ink-500 border border-ink-200 p-4">Nothing due in the next 3 days</p>
                    ) : (
                        <ul className="divide-y divide-ink-200 border border-ink-200">
                            {deadlines.map((document) => (
                                <li key={document.id} className="p-4 flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium">{document.documentType.replace(/_/g, ' ')}</p>
                                        <p className="text-xs text-ink-500 mt-0.5">
                                            Shipment #{document.shipmentId} · {document.countryCode}
                                        </p>
                                    </div>
                                    <p className="font-mono text-xs text-ink-700">{document.submissionDeadline}</p>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
                <div>
                    <h2 className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-3">Low stock items</h2>
                    {lowStock.length === 0 ? (
                        <p className="text-sm text-ink-500 border border-ink-200 p-4">All items above reorder threshold</p>
                    ) : (
                        <ul className="divide-y divide-ink-200 border border-ink-200">
                            {lowStock.map((item) => (
                                <li key={item.id} className="p-4 flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium font-mono">{item.sku}</p>
                                        <p className="text-xs text-ink-500 mt-0.5">{item.description}</p>
                                    </div>
                                    <p className="font-mono text-xs text-ink-700">
                                        {item.quantityOnHand} / {item.reorderThreshold}
                                    </p>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </div>
    );
}