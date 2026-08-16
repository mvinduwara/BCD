import { useEffect, useState } from 'react';
import { inventoryApi } from '../../api/inventory';
import type { InventoryItem } from '../../api/types';
import { StatusIndicator, type StatusTone } from '../../components/StatusIndicator';

function toneForStock(item: InventoryItem): StatusTone {
    if (item.quantityOnHand < item.reorderThreshold) return 'attention';
    if (item.quantityOnHand < item.reorderThreshold * 1.5) return 'progress';
    return 'resolved';
}

function labelForStock(item: InventoryItem): string {
    if (item.quantityOnHand < item.reorderThreshold) return 'REORDER NOW';
    if (item.quantityOnHand < item.reorderThreshold * 1.5) return 'RUNNING LOW';
    return 'IN STOCK';
}

export function InventoryPage() {
    const [items, setItems] = useState<InventoryItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [draftQuantity, setDraftQuantity] = useState('');

    async function refresh() {
        setLoading(true);
        const data = await inventoryApi.list();
        setItems(data);
        setLoading(false);
    }

    useEffect(() => {
        refresh();
    }, []);

    function startEdit(item: InventoryItem) {
        setEditingId(item.id);
        setDraftQuantity(String(item.quantityOnHand));
    }

    async function commitEdit(itemId: number) {
        const parsed = Number.parseInt(draftQuantity, 10);
        if (!Number.isNaN(parsed) && parsed >= 0) {
            await inventoryApi.updateQuantity(itemId, parsed);
            await refresh();
        }
        setEditingId(null);
    }

    return (
        <div>
            <div className="mb-8">
                <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-1">Warehouse</p>
                <h1 className="font-sans text-2xl font-medium">Inventory</h1>
            </div>

            {loading ? (
                <p className="text-sm text-ink-500">Loading</p>
            ) : items.length === 0 ? (
                <p className="text-sm text-ink-500 border border-ink-200 p-6">No inventory items yet</p>
            ) : (
                <table className="w-full text-sm border border-ink-200">
                    <thead>
                    <tr className="border-b border-ink-200 text-left">
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">SKU</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Description</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Location</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">On hand</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Status</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-ink-200">
                    {items.map((item) => (
                        <tr key={item.id}>
                            <td className="p-3 font-mono">{item.sku}</td>
                            <td className="p-3">{item.description}</td>
                            <td className="p-3 font-mono text-ink-700">{item.warehouseLocation}</td>
                            <td className="p-3">
                                {editingId === item.id ? (
                                    <input
                                        autoFocus
                                        value={draftQuantity}
                                        onChange={(event) => setDraftQuantity(event.target.value)}
                                        onBlur={() => commitEdit(item.id)}
                                        onKeyDown={(event) => {
                                            if (event.key === 'Enter') commitEdit(item.id);
                                            if (event.key === 'Escape') setEditingId(null);
                                        }}
                                        className="w-20 border border-ink-900 px-2 py-1 font-mono text-sm focus:outline-none"
                                    />
                                ) : (
                                    <button
                                        type="button"
                                        onClick={() => startEdit(item)}
                                        className="font-mono hover:underline decoration-dotted underline-offset-4"
                                    >
                                        {item.quantityOnHand} / {item.reorderThreshold}
                                    </button>
                                )}
                            </td>
                            <td className="p-3">
                                <StatusIndicator label={labelForStock(item)} tone={toneForStock(item)} />
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}