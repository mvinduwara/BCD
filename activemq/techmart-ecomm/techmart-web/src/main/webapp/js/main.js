'use strict';

// ─── Metrics Auto Refresh ────────────────────────────────────────────────────

const METRICS_REFRESH_INTERVAL = 10000;

function isMetricsPage() {
    return window.location.pathname.includes('/metrics');
}

function updateMetricsFromJson(data) {
    const set = (id, value) => {
        const el = document.getElementById(id);
        if (el) el.textContent = value;
    };

    if (data.inventory) {
        set('live-cache-size',      data.inventory.cacheSize);
        set('live-cache-hits',      data.inventory.cacheHits);
        set('live-cache-misses',    data.inventory.cacheMisses);
        set('live-cache-init',      data.inventory.cacheInitTimeMs + 'ms');

        const total = data.inventory.cacheHits + data.inventory.cacheMisses;
        const hitRate = total === 0 ? 0 : Math.round((data.inventory.cacheHits / total) * 100);
        const bar = document.getElementById('live-hit-rate-bar');
        const pct = document.getElementById('live-hit-rate-pct');
        if (bar) bar.style.width = hitRate + '%';
        if (pct) pct.textContent = hitRate + '%';
    }

    if (data.messaging) {
        set('live-orders-processed',    data.messaging.ordersProcessed);
        set('live-inventory-updates',   data.messaging.inventoryUpdates);
        set('live-notifications-sent',  data.messaging.notificationsSent);
        set('live-avg-notification',    data.messaging.avgNotificationMs.toFixed(2) + 'ms');
    }

    if (data.performance) {
        set('live-total-requests',  data.performance.totalRequests);
        set('live-avg-response',    data.performance.avgResponseMs.toFixed(2) + 'ms');
        set('live-uptime',          data.performance.uptimeSeconds + 's');
    }

    const ts = document.getElementById('live-timestamp');
    if (ts) ts.textContent = data.timestamp;
}

function fetchMetrics() {
    const base = document.querySelector('meta[name="context-path"]')
        ? document.querySelector('meta[name="context-path"]').getAttribute('content')
        : '';

    fetch(base + '/metrics/json')
        .then(res => {
            if (!res.ok) throw new Error('Metrics fetch failed: ' + res.status);
            return res.json();
        })
        .then(data => updateMetricsFromJson(data))
        .catch(err => console.warn('Metrics refresh error:', err));
}

if (isMetricsPage()) {
    setInterval(fetchMetrics, METRICS_REFRESH_INTERVAL);
}

// ─── Cart Quantity Validation ─────────────────────────────────────────────────

function initCartValidation() {
    const qtyInputs = document.querySelectorAll('.qty-input, .qty-input-sm');
    qtyInputs.forEach(input => {
        input.addEventListener('change', function () {
            const min = parseInt(this.min) || 1;
            const max = parseInt(this.max) || 9999;
            let val   = parseInt(this.value) || min;
            if (val < min) val = min;
            if (val > max) val = max;
            this.value = val;
        });
    });
}

// ─── Alert Auto Dismiss ───────────────────────────────────────────────────────

function initAlertDismiss() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity    = '0';
            setTimeout(() => alert.remove(), 500);
        }, 4000);
    });
}

// ─── Add to Cart Feedback ─────────────────────────────────────────────────────

function initCartFeedback() {
    const cartForms = document.querySelectorAll('form[action*="/cart/"]');
    cartForms.forEach(form => {
        form.addEventListener('submit', function () {
            const btn = this.querySelector('button[type="submit"]');
            if (btn && !btn.disabled) {
                btn.textContent = 'Adding...';
                btn.disabled    = true;
            }
        });
    });
}

// ─── Confirm Cancel Order ─────────────────────────────────────────────────────

function initOrderCancelConfirm() {
    const cancelForms = document.querySelectorAll('form[action*="/cancel/"]');
    cancelForms.forEach(form => {
        form.addEventListener('submit', function (e) {
            if (!confirm('Are you sure you want to cancel this order?')) {
                e.preventDefault();
            }
        });
    });
}

// ─── Checkout Form Toggle ─────────────────────────────────────────────────────

function initCheckoutToggle() {
    const checkoutBtn = document.getElementById('checkout-toggle-btn');
    const checkoutForm = document.getElementById('checkoutForm');

    if (checkoutBtn && checkoutForm) {
        checkoutBtn.addEventListener('click', function () {
            const isHidden = checkoutForm.style.display === 'none'
                || checkoutForm.style.display === '';
            checkoutForm.style.display = isHidden ? 'block' : 'none';
            checkoutBtn.textContent    = isHidden
                ? 'Hide Checkout'
                : 'Proceed to Checkout';

            if (isHidden) {
                checkoutForm.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    }
}

// ─── Performance Timing ───────────────────────────────────────────────────────

function logPageLoadTime() {
    window.addEventListener('load', function () {
        if (window.performance && window.performance.timing) {
            const timing  = window.performance.timing;
            const loadTime = timing.loadEventEnd - timing.navigationStart;
            console.info('[TechMart] Page load time: ' + loadTime + 'ms');
        }
    });
}

// ─── Init All ─────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function () {
    initCartValidation();
    initAlertDismiss();
    initCartFeedback();
    initOrderCancelConfirm();
    initCheckoutToggle();
    logPageLoadTime();
});