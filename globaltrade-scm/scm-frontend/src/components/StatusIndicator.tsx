export type StatusTone = 'attention' | 'progress' | 'resolved' | 'neutral';

interface StatusIndicatorProps {
    label: string;
    tone: StatusTone;
}

const toneStyles: Record<StatusTone, string> = {
    attention: 'bg-ink-900 border-ink-900',
    progress: 'bg-transparent border-ink-900',
    resolved: 'bg-ink-300 border-ink-300',
    neutral: 'bg-transparent border-ink-300',
};

export function StatusIndicator({ label, tone }: StatusIndicatorProps) {
    return (
        <span className="inline-flex items-center gap-2">
      <span className={`h-2.5 w-2.5 border ${toneStyles[tone]}`} />
      <span className="font-mono text-xs tracking-widest uppercase text-ink-700">{label.replace(/_/g, ' ')}</span>
    </span>
    );
}