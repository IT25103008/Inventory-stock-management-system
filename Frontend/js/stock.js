/**
 * ============================================================
 *  stock.js — Stock Transaction CRUD
 *  Endpoints:
 *    GET    /transactions        →  StockTransactionController.getAll()
 *    POST   /transactions        →  StockTransactionController.record()
 *    PUT    /transactions/{id}   →  StockTransactionController.update()
 *    DELETE /transactions/{id}   →  StockTransactionController.delete()
 *    GET    /products            →  (for product dropdown)
 *
 *  Note: POST auto-updates product quantity in backend via
 *        StockService.recordTransaction()
 * ============================================================
 */

const API = 'http://localhost:8080';
let allTxns       = [];
let products      = [];
let editingId     = null;
let currentFilter = 'ALL';

/* ── Session: populate sidebar user chip ─────────────────── */
const u = JSON.parse(localStorage.getItem('currentUser') || '{}');
document.getElementById('userName').textContent    = u.name  || '—';
document.getElementById('userRole').textContent    = u.role  || '—';
document.getElementById('userInitial').textContent = (u.name || 'G')[0].toUpperCase();

/* ── Default date = today ────────────────────────────────── */
document.getElementById('fDate').value = new Date().toISOString().split('T')[0];

/* ── Load transactions + products ────────────────────────── */
async function loadData() {
    try {
        const [txns, prods] = await Promise.all([
            fetch(`${API}/transactions`).then(r => r.json()),
            fetch(`${API}/products`).then(r => r.json())
        ]);

        allTxns  = txns;
        products = prods;

        populateProducts();
        applyFilters();
    } catch (e) {
        showToast('Cannot connect to server', 'error');
    }
}

/* ── Populate product dropdown in modal ──────────────────── */
function populateProducts() {
    const sel = document.getElementById('fProduct');
    sel.innerHTML = '<option value="">Select product…</option>' +
        products.map(p => `<option value="${p.productId}">${p.name} (${p.productId})</option>`).join('');
}

/* ── Product name lookup ─────────────────────────────────── */
function getProductName(id) {
    return (products.find(p => p.productId === id) || {}).name || id || '—';
}

/* ── Filter: ALL / IN / OUT ──────────────────────────────── */
function setFilter(f) {
    currentFilter = f;
    document.getElementById('filterAll').className = 'filter-chip' + (f === 'ALL' ? ' active-in' : '');
    document.getElementById('filterIn').className  = 'filter-chip' + (f === 'IN'  ? ' active-in' : '');
    document.getElementById('filterOut').className  = 'filter-chip' + (f === 'OUT' ? ' active-out' : '');
    applyFilters();
}

/* ── Apply search + type filter ──────────────────────────── */
function applyFilters() {
    const q = document.getElementById('searchInput').value.toLowerCase();
    let data = allTxns;

    if (currentFilter !== 'ALL') {
        data = data.filter(t => t.type === currentFilter);
    }
    if (q) {
        data = data.filter(t =>
            (t.transactionId || '').toLowerCase().includes(q) ||
            (t.productId || '').toLowerCase().includes(q) ||
            getProductName(t.productId).toLowerCase().includes(q)
        );
    }

    renderTable(data);
}

/* ── Render transactions into table ──────────────────────── */
function renderTable(data) {
    const tbody = document.getElementById('tableBody');

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state">
            <span class="material-symbols-outlined">swap_vert</span>No transactions found
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = data.map(t => `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${t.transactionId || ''}</td>
            <td style="font-weight:500">${getProductName(t.productId)}</td>
            <td><span class="type-chip type-${(t.type || '').toLowerCase()}">
                <span class="material-symbols-outlined" style="font-size:13px">${t.type === 'IN' ? 'arrow_downward' : 'arrow_upward'}</span>
                ${t.type || ''}
            </span></td>
            <td style="font-variant-numeric:tabular-nums;font-weight:500">${t.quantity || 0}</td>
            <td style="color:var(--on-muted)">${t.date || '—'}</td>
            <td style="color:var(--on-muted);font-size:13px">${t.notes || '—'}</td>
            <td><div class="actions">
                <button class="icon-btn" onclick="editTxn('${t.transactionId}')"><span class="material-symbols-outlined">edit</span></button>
                <button class="icon-btn" onclick="deleteTxn('${t.transactionId}')"><span class="material-symbols-outlined">delete</span></button>
            </div></td>
        </tr>`).join('');
}

/* ── Modal open / close ──────────────────────────────────── */
function openModal(reset = true) {
    if (reset) {
        editingId = null;
        document.getElementById('modalTitle').textContent        = 'Record Transaction';
        document.getElementById('infoBanner').style.display      = 'none';
        ['fId', 'fNotes'].forEach(id => document.getElementById(id).value = '');
        document.getElementById('fQty').value     = '';
        document.getElementById('fType').value    = 'IN';
        document.getElementById('fProduct').value = '';
        document.getElementById('fDate').value    = new Date().toISOString().split('T')[0];
        ['fId', 'fType', 'fProduct', 'fQty'].forEach(id =>
            document.getElementById(id).disabled = false
        );
    }
    document.getElementById('overlay').classList.add('open');
}

function closeModal() {
    document.getElementById('overlay').classList.remove('open');
}

/* ── Edit: pre-fill (only notes + date editable) ─────────── */
function editTxn(id) {
    const t = allTxns.find(x => x.transactionId === id);
    if (!t) return;

    editingId = id;
    document.getElementById('modalTitle').textContent   = 'Edit Transaction';
    document.getElementById('infoBanner').style.display = 'flex';

    document.getElementById('fId').value      = t.transactionId || '';
    document.getElementById('fId').disabled   = true;
    document.getElementById('fType').value    = t.type      || 'IN';
    document.getElementById('fType').disabled = true;
    document.getElementById('fProduct').value    = t.productId || '';
    document.getElementById('fProduct').disabled = true;
    document.getElementById('fQty').value    = t.quantity || 0;
    document.getElementById('fQty').disabled = true;
    document.getElementById('fDate').value   = t.date  || '';
    document.getElementById('fNotes').value  = t.notes || '';

    openModal(false);
}

/* ── Save (Create or Update) ─────────────────────────────── */
async function saveTransaction() {
    const body = {
        transactionId: document.getElementById('fId').value.trim(),
        productId:     document.getElementById('fProduct').value,
        type:          document.getElementById('fType').value,
        quantity:      parseInt(document.getElementById('fQty').value) || 0,
        date:          document.getElementById('fDate').value,
        notes:         document.getElementById('fNotes').value.trim()
    };

    if (!editingId && (!body.productId || !body.quantity || !body.date)) {
        showToast('Product, quantity and date are required', 'error');
        return;
    }

    const url    = editingId ? `${API}/transactions/${editingId}` : `${API}/transactions`;
    const method = editingId ? 'PUT' : 'POST';

    /* For PUT, only send editable fields (notes and date) */
    const payload = editingId ? { notes: body.notes, date: body.date } : body;

    try {
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        });

        if (res.ok) {
            closeModal();
            loadData();
            showToast(editingId ? 'Transaction updated' : 'Transaction recorded');
        } else {
            showToast('Error saving', 'error');
        }
    } catch (e) {
        showToast('Server error', 'error');
    }
}

/* ── Delete ───────────────────────────────────────────────── */
async function deleteTxn(id) {
    if (!confirm('Delete this transaction? Note: product quantity will NOT be reversed.')) return;

    try {
        await fetch(`${API}/transactions/${id}`, { method: 'DELETE' });
        loadData();
        showToast('Transaction deleted');
    } catch (e) {
        showToast('Server error', 'error');
    }
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
window.addEventListener('DOMContentLoaded', () => {
    setFilter('ALL');
    const s = document.getElementById('searchInput');
    if (s) {
        s.value = '';
        setTimeout(() => s.value = '', 200);
    }
    loadData();
});
