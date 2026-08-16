import { useEffect, useState, type FormEvent } from 'react';
import { vendorsApi } from '../../api/vendors';
import type { Vendor } from '../../api/types';
import { StatusIndicator, type StatusTone } from '../../components/StatusIndicator';

function toneForStatus(status: Vendor['status']): StatusTone {
    switch (status) {
        case 'SUSPENDED':
            return 'attention';
        case 'PENDING_REVIEW':
            return 'progress';
        case 'ACTIVE':
            return 'resolved';
        default:
            return 'neutral';
    }
}

export function VendorsPage() {
    const [vendors, setVendors] = useState<Vendor[]>([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [formError, setFormError] = useState<string | null>(null);
    const [name, setName] = useState('');
    const [contactEmail, setContactEmail] = useState('');
    const [country, setCountry] = useState('');

    async function refresh() {
        setLoading(true);
        const data = await vendorsApi.list();
        setVendors(data);
        setLoading(false);
    }

    useEffect(() => {
        refresh();
    }, []);

    async function handleCreate(event: FormEvent) {
        event.preventDefault();
        setFormError(null);
        if (!name.trim() || !contactEmail.trim() || !country.trim()) {
            setFormError('Fill in name, contact email, and country');
            return;
        }
        await vendorsApi.create({ name: name.trim(), contactEmail: contactEmail.trim(), country: country.trim() });
        setName('');
        setContactEmail('');
        setCountry('');
        setShowForm(false);
        refresh();
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-8">
                <div>
                    <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-1">Partners</p>
                    <h1 className="font-sans text-2xl font-medium">Vendors</h1>
                </div>
                <button
                    type="button"
                    onClick={() => setShowForm((value) => !value)}
                    className="border border-ink-900 px-4 py-2 text-sm font-medium hover:bg-ink-900 hover:text-paper"
                >
                    {showForm ? 'Cancel' : 'New vendor'}
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleCreate} className="border border-ink-200 p-6 mb-8 grid grid-cols-3 gap-4">
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Name</label>
                        <input
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Contact email</label>
                        <input
                            value={contactEmail}
                            onChange={(event) => setContactEmail(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Country</label>
                        <input
                            value={country}
                            onChange={(event) => setCountry(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    {formError && <p className="col-span-3 text-sm text-ink-900 border-l-2 border-ink-900 pl-3">{formError}</p>}
                    <button type="submit" className="col-span-3 bg-ink-900 text-paper py-2.5 text-sm font-medium">
                        Add vendor
                    </button>
                </form>
            )}

            {loading ? (
                <p className="text-sm text-ink-500">Loading</p>
            ) : vendors.length === 0 ? (
                <p className="text-sm text-ink-500 border border-ink-200 p-6">No vendors yet</p>
            ) : (
                <table className="w-full text-sm border border-ink-200">
                    <thead>
                    <tr className="border-b border-ink-200 text-left">
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Name</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Country</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Status</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Performance</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-ink-200">
                    {vendors.map((vendor) => (
                        <tr key={vendor.id}>
                            <td className="p-3">
                                <p className="font-medium">{vendor.name}</p>
                                <p className="text-xs text-ink-500 mt-0.5">{vendor.contactEmail}</p>
                            </td>
                            <td className="p-3 font-mono">{vendor.country}</td>
                            <td className="p-3">
                                <StatusIndicator label={vendor.status} tone={toneForStatus(vendor.status)} />
                            </td>
                            <td className="p-3 font-mono text-ink-700">{vendor.performanceScore.toFixed(1)} / 5.0</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}