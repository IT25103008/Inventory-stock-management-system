/**
 * ============================================================
 *  suppliers.js — Supplier CRUD
 *  Endpoints:
 *    GET    /suppliers        →  SupplierController.getAll()
 *    GET    /suppliers/{id}   →  SupplierController.getOne()
 *    POST   /suppliers        →  SupplierController.add()
 *    PUT    /suppliers/{id}   →  SupplierController.update()
 *    DELETE /suppliers/{id}   →  SupplierController.delete()
 * ============================================================
 */

const API = 'http://localhost:8080';
let allSuppliers = [];
let editingId    = null;
let ignoreSearch = false;

/* ── Session: populate sidebar user chip ─────────────────── */
const u = JSON.parse(localStorage.getItem('currentUser') || '{}');
document.getElementById('userName').textContent    = u.name  || '—';
document.getElementById('userRole').textContent    = u.role  || '—';
document.getElementById('userInitial').textContent = (u.name || 'G')[0].toUpperCase();

/* ── Load all suppliers ──────────────────────────────────── */
async function loadData() {
    try {
        const res    = await fetch(`${API}/suppliers`);
        allSuppliers = await res.json();
        filterTable();
    } catch (e) {
        showToast('Cannot connect to server', 'error');
    }
}

/* ── Render suppliers into table ─────────────────────────── */
function renderTable(data) {
    const tbody = document.getElementById('tableBody');

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state">
            <span class="material-symbols-outlined">local_shipping</span>No suppliers found
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = data.map(s => `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${s.supplierId || ''}</td>
            <td style="font-weight:500">${s.name || ''}</td>
            <td style="color:var(--on-muted)">${s.contact || '—'}</td>
            <td><a class="contact-link" href="mailto:${s.email || ''}">${s.email || '—'}</a></td>
            <td style="color:var(--on-muted);font-size:13px">${s.address || '—'}</td>
            <td><div class="actions">
                <button class="icon-btn" onclick="editSupplier('${s.supplierId}')"><span class="material-symbols-outlined">edit</span></button>
                <button class="icon-btn" onclick="deleteSupplier('${s.supplierId}')"><span class="material-symbols-outlined">delete</span></button>
            </div></td>
        </tr>`).join('');
}

/* ── Search / filter ─────────────────────────────────────── */
function filterTable() {
    if (ignoreSearch) {
        document.getElementById('searchInput').value = '';
        return;
    }
    const q = document.getElementById('searchInput').value.toLowerCase();
    renderTable(allSuppliers.filter(s =>
        (s.name || '').toLowerCase().includes(q) ||
        (s.supplierId || '').toLowerCase().includes(q) ||
        (s.email || '').toLowerCase().includes(q)
    ));
}

/* ── Modal open / close ──────────────────────────────────── */
function openModal(reset = true) {
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    if (reset) {
        editingId = null;
        document.getElementById('modalTitle').textContent = 'Add Supplier';
        ['fId', 'fName', 'fContact', 'fEmail', 'fAddress'].forEach(id =>
            document.getElementById(id).value = ''
        );
        document.getElementById('fId').disabled = false;
    }
    document.getElementById('overlay').classList.add('open');
}

function closeModal() {
    document.getElementById('overlay').classList.remove('open');
}

/* ── Edit: pre-fill modal ────────────────────────────────── */
function editSupplier(id) {
    const s = allSuppliers.find(x => x.supplierId === id);
    if (!s) return;

    editingId = id;
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    document.getElementById('modalTitle').textContent = 'Edit Supplier';
    document.getElementById('fId').value      = s.supplierId || '';
    document.getElementById('fId').disabled   = true;
    document.getElementById('fName').value    = s.name       || '';
    document.getElementById('fContact').value = s.contact    || '';
    document.getElementById('fEmail').value   = s.email      || '';
    document.getElementById('fAddress').value = s.address    || '';
    openModal(false);
}

/* ── Save (Create or Update) ─────────────────────────────── */
async function saveSupplier() {
    const body = {
        supplierId: document.getElementById('fId').value.trim(),
        name:       document.getElementById('fName').value.trim(),
        contact:    document.getElementById('fContact').value.trim(),
        email:      document.getElementById('fEmail').value.trim(),
        address:    document.getElementById('fAddress').value.trim()
    };

    if (!body.name) {
        showToast('Supplier name is required', 'error');
        return;
    }

    const url    = editingId ? `${API}/suppliers/${editingId}` : `${API}/suppliers`;
    const method = editingId ? 'PUT' : 'POST';

    try {
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(body)
        });

        if (res.ok) {
            closeModal();
            loadData();
            showToast(editingId ? 'Supplier updated' : 'Supplier added');
        } else {
            showToast('Error saving', 'error');
        }
    } catch (e) {
        showToast('Server error', 'error');
    }
}

/* ── Delete ───────────────────────────────────────────────── */
async function deleteSupplier(id) {
    if (!confirm('Delete this supplier?')) return;

    try {
        await fetch(`${API}/suppliers/${id}`, { method: 'DELETE' });
        loadData();
        showToast('Supplier deleted');
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
    const s = document.getElementById('searchInput');
    if (s) {
        s.value = '';
        setTimeout(() => s.value = '', 200);
    }
    loadData();
});
