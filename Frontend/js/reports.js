/**
 * ============================================================
 *  reports.js — Reports & Low Stock Analytics
 *  Endpoints:
 *    GET /reports/low-stock        →  ReportController.getLowStock()
 *    GET /reports/inventory-value  →  ReportController.getInventoryValue()
 *    GET /reports/stock-movement   →  ReportController.getMovement()
 *    GET /products                 →  (for product name lookup)
 *    POST /reports                 →  ReportController.save()
 *    DELETE /reports/{id}          →  ReportController.delete()
 * ============================================================
 */

const API = 'http://localhost:8080';
let productMap = {};

/* ── Cached report data (for TXT export) ─────────────────── */
let cachedLowStock  = [];
let cachedInvValue  = {};
let cachedMovement  = [];

/* ── Session: populate sidebar user chip ─────────────────── */
const u = JSON.parse(localStorage.getItem('currentUser') || '{}');
document.getElementById('userName').textContent    = u.name  || '—';
document.getElementById('userRole').textContent    = u.role  || '—';
document.getElementById('userInitial').textContent = (u.name || 'G')[0].toUpperCase();

/* ── Load all report data ────────────────────────────────── */
async function loadAll() {
    try {
        const [lowStock, invValue, movement, prods] = await Promise.all([
            fetch(`${API}/reports/low-stock`).then(r => r.json()),
            fetch(`${API}/reports/inventory-value`).then(r => r.json()),
            fetch(`${API}/reports/stock-movement`).then(r => r.json()),
            fetch(`${API}/products`).then(r => r.json())
        ]);

        /* Build product name lookup map */
        productMap = {};
        prods.forEach(p => { productMap[p.productId] = p.name; });

        /* Cache for export */
        cachedLowStock = lowStock;
        cachedInvValue = invValue;
        cachedMovement = movement;

        /* Summary cards */
        document.getElementById('sumLow').textContent  = lowStock.length;
        document.getElementById('sumTxns').textContent = movement.length;
        const tv = invValue.totalValue || 0;
        document.getElementById('sumValue').textContent =
            'LKR ' + Number(tv).toLocaleString('en-LK', { minimumFractionDigits: 0, maximumFractionDigits: 0 });

        /* Render report sections */
        renderLowStock(lowStock);
        renderInventoryValue(invValue);
        renderMovement(movement);
    } catch (e) {
        showToast('Cannot connect to server', 'error');
    }
}

/* ── Section 1: Low Stock Alert ──────────────────────────── */
function renderLowStock(data) {
    const tbody = document.getElementById('lowStockBody');

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state" style="color:#82d282">
            <span class="material-symbols-outlined" style="color:#82d282">check_circle</span>
            All products are sufficiently stocked!
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = data.map(p => `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${p.productId || ''}</td>
            <td style="font-weight:500">${p.name || ''}</td>
            <td><span class="alert-badge"><span class="material-symbols-outlined">warning</span>${p.quantity}</span></td>
            <td style="font-variant-numeric:tabular-nums">LKR ${Number(p.price || 0).toLocaleString('en-LK', { minimumFractionDigits: 2 })}</td>
            <td><span class="alert-badge">Critical</span></td>
        </tr>`).join('');
}

/* ── Section 2: Inventory Value ──────────────────────────── */
function renderInventoryValue(data) {
    const tbody    = document.getElementById('valueBody');
    const tvEl     = document.getElementById('totalValue');
    const products = data.products || [];
    const total    = data.totalValue || 0;

    tvEl.textContent = 'LKR ' + Number(total).toLocaleString('en-LK', { minimumFractionDigits: 2 });

    if (!products.length) {
        tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state">
            <span class="material-symbols-outlined">inventory</span>No products found
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = products.map(p => {
        const lineVal = (p.price || 0) * (p.quantity || 0);
        return `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${p.productId || ''}</td>
            <td style="font-weight:500">${p.name || ''}</td>
            <td style="font-variant-numeric:tabular-nums">${p.quantity || 0}</td>
            <td style="font-variant-numeric:tabular-nums;color:var(--on-muted)">LKR ${Number(p.price || 0).toLocaleString('en-LK', { minimumFractionDigits: 2 })}</td>
            <td style="font-variant-numeric:tabular-nums;font-weight:500">LKR ${Number(lineVal).toLocaleString('en-LK', { minimumFractionDigits: 2 })}</td>
        </tr>`;
    }).join('');
}

/* ── Section 3: Stock Movement History ───────────────────── */
function renderMovement(data) {
    const tbody = document.getElementById('movementBody');

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state">
            <span class="material-symbols-outlined">receipt_long</span>No transactions found
        </div></td></tr>`;
        return;
    }

    /* Sort by date descending */
    const sorted = [...data].sort((a, b) => (b.date || '').localeCompare(a.date || ''));

    tbody.innerHTML = sorted.map(t => `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${t.transactionId || ''}</td>
            <td style="font-weight:500">${productMap[t.productId] || t.productId || '—'}</td>
            <td><span class="type-chip type-${(t.type || '').toLowerCase()}">
                <span class="material-symbols-outlined" style="font-size:13px">${t.type === 'IN' ? 'arrow_downward' : 'arrow_upward'}</span>
                ${t.type || ''}
            </span></td>
            <td style="font-variant-numeric:tabular-nums;font-weight:500">${t.quantity || 0}</td>
            <td style="color:var(--on-muted)">${t.date || '—'}</td>
            <td style="color:var(--on-muted);font-size:13px">${t.notes || '—'}</td>
        </tr>`).join('');
}

/* ── Export full report as .txt ───────────────────────────── */
function exportReport() {
    const hasData = cachedLowStock.length || (cachedInvValue.products && cachedInvValue.products.length) || cachedMovement.length;
    if (!hasData) {
        showToast('No report data to export. Click Refresh first.', 'error');
        return;
    }

    const now       = new Date();
    const timestamp = now.toLocaleString('en-LK', { dateStyle: 'full', timeStyle: 'medium' });
    const divider   = '='.repeat(80);
    const subDiv    = '-'.repeat(80);
    const lines     = [];

    /* Helper: pad string to fixed width */
    const pad = (str, len) => String(str).padEnd(len).slice(0, len);
    const padR = (str, len) => String(str).padStart(len).slice(-len);
    const fmtLKR = (n) => 'LKR ' + Number(n).toLocaleString('en-LK', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

    /* ── Header ──────────────────────────── */
    lines.push(divider);
    lines.push('  PAPOL - Inventory & Stock Management System');
    lines.push('  FULL INVENTORY REPORT');
    lines.push('  Generated: ' + timestamp);
    if (u.name) lines.push('  Generated by: ' + u.name + (u.role ? ' (' + u.role + ')' : ''));
    lines.push(divider);
    lines.push('');

    /* ── Section 1: Summary ──────────────── */
    const totalVal = cachedInvValue.totalValue || 0;
    lines.push('  SUMMARY');
    lines.push(subDiv);
    lines.push('  Low Stock Items      : ' + cachedLowStock.length);
    lines.push('  Total Inventory Value : ' + fmtLKR(totalVal));
    lines.push('  Total Transactions   : ' + cachedMovement.length);
    lines.push('');

    /* ── Section 2: Low Stock Alert ──────── */
    lines.push('  LOW STOCK ALERT (Quantity < 5)');
    lines.push(subDiv);
    if (cachedLowStock.length === 0) {
        lines.push('  All products are sufficiently stocked.');
    } else {
        lines.push('  ' + pad('ID', 12) + pad('Name', 28) + padR('Qty', 8) + padR('Price (LKR)', 16) + '  Status');
        lines.push('  ' + pad('-', 12).replace(/ /g, '-') + pad('-', 28).replace(/ /g, '-') + pad('-', 8).replace(/ /g, '-') + pad('-', 16).replace(/ /g, '-') + '  ' + '-'.repeat(10));
        cachedLowStock.forEach(p => {
            lines.push('  ' + pad(p.productId || '', 12) + pad(p.name || '', 28) + padR(p.quantity, 8) + padR(Number(p.price || 0).toLocaleString('en-LK', { minimumFractionDigits: 2 }), 16) + '  Critical');
        });
    }
    lines.push('');

    /* ── Section 3: Inventory Value ──────── */
    const products = cachedInvValue.products || [];
    lines.push('  INVENTORY VALUE');
    lines.push(subDiv);
    if (products.length === 0) {
        lines.push('  No products found.');
    } else {
        lines.push('  ' + pad('ID', 12) + pad('Name', 28) + padR('Qty', 8) + padR('Unit Price', 16) + padR('Total Value', 16));
        lines.push('  ' + pad('-', 12).replace(/ /g, '-') + pad('-', 28).replace(/ /g, '-') + pad('-', 8).replace(/ /g, '-') + pad('-', 16).replace(/ /g, '-') + pad('-', 16).replace(/ /g, '-'));
        products.forEach(p => {
            const lineVal = (p.price || 0) * (p.quantity || 0);
            lines.push('  ' + pad(p.productId || '', 12) + pad(p.name || '', 28) + padR(p.quantity || 0, 8) + padR(Number(p.price || 0).toLocaleString('en-LK', { minimumFractionDigits: 2 }), 16) + padR(Number(lineVal).toLocaleString('en-LK', { minimumFractionDigits: 2 }), 16));
        });
        lines.push('  ' + ' '.repeat(48) + padR('GRAND TOTAL: ' + fmtLKR(totalVal), 32));
    }
    lines.push('');

    /* ── Section 4: Stock Movement ────────── */
    lines.push('  STOCK MOVEMENT HISTORY');
    lines.push(subDiv);
    if (cachedMovement.length === 0) {
        lines.push('  No transactions found.');
    } else {
        lines.push('  ' + pad('Txn ID', 12) + pad('Product', 26) + pad('Type', 8) + padR('Qty', 8) + '  ' + pad('Date', 14) + 'Notes');
        lines.push('  ' + pad('-', 12).replace(/ /g, '-') + pad('-', 26).replace(/ /g, '-') + pad('-', 8).replace(/ /g, '-') + pad('-', 8).replace(/ /g, '-') + '  ' + pad('-', 14).replace(/ /g, '-') + '-'.repeat(18));
        const sorted = [...cachedMovement].sort((a, b) => (b.date || '').localeCompare(a.date || ''));
        sorted.forEach(t => {
            const prodName = productMap[t.productId] || t.productId || '-';
            lines.push('  ' + pad(t.transactionId || '', 12) + pad(prodName, 26) + pad(t.type || '', 8) + padR(t.quantity || 0, 8) + '  ' + pad(t.date || '-', 14) + (t.notes || '-'));
        });
    }
    lines.push('');

    /* ── Footer ──────────────────────────── */
    lines.push(divider);
    lines.push('  End of Report');
    lines.push(divider);

    /* ── Trigger download ────────────────── */
    const blob = new Blob([lines.join('\n')], { type: 'text/plain' });
    const url  = URL.createObjectURL(blob);
    const a    = document.createElement('a');
    a.href     = url;
    a.download = 'Papol_Report_' + now.toISOString().slice(0, 10) + '.txt';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    showToast('Report exported successfully!');
}

/* ── Toast notification ──────────────────────────────────── */
function showToast(msg, type = 'success') {
    const t    = document.getElementById('toast');
    const icon = type === 'error' ? 'error' : 'check_circle';
    t.innerHTML = `<span class="material-symbols-outlined" style="font-size:16px">${icon}</span>${msg}`;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 3000);
}

/* ── Init ────────────────────────────────────────────────── */
window.addEventListener('DOMContentLoaded', loadAll);
