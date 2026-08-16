import { useEffect, useState, type FormEvent } from 'react';
import { customsApi } from '../../api/customs';
import type { CustomsDocument } from '../../api/types';
import { StatusIndicator, type StatusTone } from '../../components/StatusIndicator';

function toneForDocument(document: CustomsDocument): StatusTone {
    if (document.status === 'REJECTED') return 'attention';
    if (document.status === 'APPROVED') return 'resolved';
    const daysUntilDeadline = (new Date(document.submissionDeadline).getTime() - Date.now()) / (1000 * 60 * 60 * 24);
    if (daysUntilDeadline < 2) return 'attention';
    return 'progress';
}

export function CustomsPage() {
    const [documents, setDocuments] = useState<CustomsDocument[]>([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [formError, setFormError] = useState<string | null>(null);
    const [shipmentId, setShipmentId] = useState('');
    const [documentType, setDocumentType] = useState('');
    const [countryCode, setCountryCode] = useState('');
    const [submissionDeadline, setSubmissionDeadline] = useState('');

    async function refresh() {
        setLoading(true);
        const data = await customsApi.list();
        setDocuments(data);
        setLoading(false);
    }

    useEffect(() => {
        refresh();
    }, []);

    async function handleCreate(event: FormEvent) {
        event.preventDefault();
        setFormError(null);
        const parsedShipmentId = Number.parseInt(shipmentId, 10);
        if (Number.isNaN(parsedShipmentId) || !documentType.trim() || !countryCode.trim() || !submissionDeadline) {
            setFormError('Fill in shipment ID, document type, country, and deadline');
            return;
        }
        await customsApi.create({
            shipmentId: parsedShipmentId,
            documentType: documentType.trim().toUpperCase().replace(/\s+/g, '_'),
            countryCode: countryCode.trim().toUpperCase(),
            submissionDeadline,
        });
        setShipmentId('');
        setDocumentType('');
        setCountryCode('');
        setSubmissionDeadline('');
        setShowForm(false);
        refresh();
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-8">
                <div>
                    <p className="font-mono text-xs tracking-widest uppercase text-ink-500 mb-1">Compliance</p>
                    <h1 className="font-sans text-2xl font-medium">Customs documents</h1>
                </div>
                <button
                    type="button"
                    onClick={() => setShowForm((value) => !value)}
                    className="border border-ink-900 px-4 py-2 text-sm font-medium hover:bg-ink-900 hover:text-paper"
                >
                    {showForm ? 'Cancel' : 'New document'}
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleCreate} className="border border-ink-200 p-6 mb-8 grid grid-cols-4 gap-4">
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Shipment ID</label>
                        <input
                            value={shipmentId}
                            onChange={(event) => setShipmentId(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Document type</label>
                        <input
                            value={documentType}
                            onChange={(event) => setDocumentType(event.target.value)}
                            placeholder="BILL OF LADING"
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Country</label>
                        <input
                            value={countryCode}
                            onChange={(event) => setCountryCode(event.target.value)}
                            placeholder="NL"
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-mono uppercase tracking-widest text-ink-500 mb-1.5">Deadline</label>
                        <input
                            type="date"
                            value={submissionDeadline}
                            onChange={(event) => setSubmissionDeadline(event.target.value)}
                            className="w-full border border-ink-300 px-3 py-2 text-sm focus:outline-none focus:border-ink-900"
                        />
                    </div>
                    {formError && <p className="col-span-4 text-sm text-ink-900 border-l-2 border-ink-900 pl-3">{formError}</p>}
                    <button type="submit" className="col-span-4 bg-ink-900 text-paper py-2.5 text-sm font-medium">
                        Create document
                    </button>
                </form>
            )}

            {loading ? (
                <p className="text-sm text-ink-500">Loading</p>
            ) : documents.length === 0 ? (
                <p className="text-sm text-ink-500 border border-ink-200 p-6">No customs documents yet</p>
            ) : (
                <table className="w-full text-sm border border-ink-200">
                    <thead>
                    <tr className="border-b border-ink-200 text-left">
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Shipment</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Type</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Country</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Deadline</th>
                        <th className="p-3 font-mono text-xs uppercase tracking-widest text-ink-500 font-medium">Status</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-ink-200">
                    {documents.map((document) => (
                        <tr key={document.id}>
                            <td className="p-3 font-mono">#{document.shipmentId}</td>
                            <td className="p-3">{document.documentType.replace(/_/g, ' ')}</td>
                            <td className="p-3 font-mono">{document.countryCode}</td>
                            <td className="p-3 font-mono text-ink-700">{document.submissionDeadline}</td>
                            <td className="p-3">
                                <StatusIndicator label={document.status} tone={toneForDocument(document)} />
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}