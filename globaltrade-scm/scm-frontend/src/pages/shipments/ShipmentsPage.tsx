import { useEffect, useState, type FormEvent } from 'react';
import { shipmentsApi } from '../../api/shipments';
import type { Shipment, ShipmentStatus } from '../../api/types';
import { StatusIndicator, type StatusTone } from '../../components/StatusIndicator';

function toneForStatus(status: ShipmentStatus): StatusTone {
    switch (status) {
        case 'CUSTOMS_HOLD':
        case 'DELAYED':
            return 'attention';
        case 'IN_TRANSIT':
        case 'PENDING':
            return 'progress';
        case 'DELIVERED':
            return 'resolved';
        default:
            return 'neutral';
    }
}

export function ShipmentsPage() {
    const [shipments, setShipments] = useState<Shipment[]>([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [formError, setFormError] = useState<string | null>(null);
    const [trackingNumber, setTrackingNumber] = useState('');
    const [origin, setOrigin] = useState('');
    const [destination, setDestination] = useState('');

    async function refresh() {
        setLoading(true);
        const data = await shipmentsApi.list();
        setShipments(data);
        setLoading(false);
    }

    useEffect(() => {
        refresh();
    }, []);

    async function handleCreate(event: FormEvent) {
        event.preventDefault();
        setFormError(null);
        if (!trackingNumber.trim() || !origin.trim() || !destination.trim()) {
            setFormError('Fill in tracking number, origin, and destination');
            return;
        }
        await shipmentsApi.create({
            trackingNumber: trackingNumber.trim(),
            origin: origin.trim(),
            destination: destination.trim(),
            estimatedDeparture: new Date().toISOString(),
            estimatedArrival: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
            vendorId: 1,
            carrierId: 1,
        });
        setTrackingNumber('');
        setOrigin('');
        setDestination('');
        setShowForm(false);
        refresh();
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-8">
                <div>
                    <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-1">Logistics</p>
                    <h1 className="font-sans text-2xl font-medium">Shipments</h1>
                </div>
                <button
                    type="button"
                    onClick={() => setShowForm((value) => !value)}
                    className="border border-ink-900 px-4 py-2 text-sm font-medium hover:bg-ink-900 hover:text-paper"
                >
                    {showForm ? 'Cancel' : 'New shipment'}
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleCreate} className="border border-ink-200 p-6 mb-8 grid grid-cols-3 gap-4">
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">
                            Tracking number
                        </label>
                        <input
                            value={trackingNumber}
                            onChange={(event) => setTrackingNumber(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Origin</label>
                        <input
                            value={origin}
                            onChange={(event) => setOrigin(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Destination</label>
                        <input
                            value={destination}
                            onChange={(event) => setDestination(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    {formError && <p className="col-span-3 text-sm text-ink-900 border-l-2 border-ink-900 pl-3">{formError}</p>}
                    <button type="submit" className="col-span-3 bg-ink-900 text-paper py-2.5 text-sm font-medium">
                        Create shipment
                    </button>
                </form>
            )}

            {loading ? (
                <p className="text-sm text-ink-500">Loading</p>
            ) : shipments.length === 0 ? (
                <p className="text-sm text-ink-500 border border-ink-200 p-6">No shipments yet</p>
            ) : (
                <table className="w-full text-sm border border-ink-200">
                    <thead>
                    <tr className="border-b border-ink-200 text-left">
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Tracking</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Route</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Status</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">ETA</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-ink-200">
                    {shipments.map((shipment) => (
                        <tr key={shipment.id}>
                            <td className="p-3 font-mono">{shipment.trackingNumber}</td>
                            <td className="p-3">
                                {shipment.origin} → {shipment.destination}
                            </td>
                            <td className="p-3">
                                <StatusIndicator label={shipment.status} tone={toneForStatus(shipment.status)} />
                            </td>
                            <td className="p-3 font-mono text-ink-700">{new Date(shipment.estimatedArrival).toLocaleDateString()}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}