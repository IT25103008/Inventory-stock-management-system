/**
 * ============================================================
 *  products.js — Product CRUD
 *  Endpoints:
 *    GET    /products        →  ProductController.getAll()
 *    GET    /products/{id}   →  ProductController.getOne()
 *    POST   /products        →  ProductController.add()
 *    PUT    /products/{id}   →  ProductController.update()
 *    DELETE /products/{id}   →  ProductController.delete()
 *    GET    /categories      →  (for dropdown)
 *    GET    /suppliers       →  (for dropdown)
 * ============================================================
 */

const API = 'http://localhost:8080';
let allProducts = [];
let categories  = [];
let suppliers   = [];
let editingId   = null;
let ignoreSearch = false;

/* ── Session: populate sidebar user chip ─────────────────── */
const u = JSON.parse(localStorage.getItem('currentUser') || '{}');
document.getElementById('userName').textContent    = u.name  || '—';
document.getElementById('userRole').textContent    = u.role  || '—';
document.getElementById('userInitial').textContent = (u.name || 'G')[0].toUpperCase();

/* ── Load products + related data ────────────────────────── */
async function loadData() {
    try {
        const [prods, cats, sups] = await Promise.all([
            fetch(`${API}/products`).then(r => r.json()),
            fetch(`${API}/categories`).then(r => r.json()),
            fetch(`${API}/suppliers`).then(r => r.json())
        ]);

        allProducts = prods;
        categories  = cats;
        suppliers   = sups;

        filterTable();
        populateDropdowns();
    } catch (e) {
        showToast('Cannot connect to server', 'error');
    }
}

/* ── Populate category & supplier dropdowns in modal ─────── */
function populateDropdowns() {
    const catSel = document.getElementById('fCategory');
    const supSel = document.getElementById('fSupplier');

    catSel.innerHTML = '<option value="">Select category…</option>' +
        categories.map(c => `<option value="${c.categoryId}">${c.name}</option>`).join('');
    supSel.innerHTML = '<option value="">Select supplier…</option>' +
        suppliers.map(s => `<option value="${s.supplierId}">${s.name}</option>`).join('');
}

/* ── Name lookups for table display ──────────────────────── */
function getCatName(id) { return (categories.find(c => c.categoryId === id) || {}).name || id || '—'; }
function getSupName(id) { return (suppliers.find(s => s.supplierId === id) || {}).name || id || '—'; }

/* ── Render products into table ──────────────────────────── */
function renderTable(data) {
    const tbody = document.getElementById('tableBody');

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state">
            <span class="material-symbols-outlined">inventory</span>No products found
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = data.map(p => `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${p.productId || ''}</td>
            <td style="font-weight:500">${p.name || ''}</td>
            <td style="color:var(--on-muted)">${getCatName(p.categoryId)}</td>
            <td style="color:var(--on-muted)">${getSupName(p.supplierId)}</td>
            <td><span class="qty-badge ${p.quantity < 5 ? 'qty-low' : 'qty-ok'}">${p.quantity}</span></td>
            <td style="font-variant-numeric:tabular-nums">${Number(p.price || 0).toLocaleString('en-LK', { minimumFractionDigits: 2 })}</td>
            <td><div class="actions">
                <button class="icon-btn" onclick="editProduct('${p.productId}')"><span class="material-symbols-outlined">edit</span></button>
                <button class="icon-btn" onclick="deleteProduct('${p.productId}')"><span class="material-symbols-outlined">delete</span></button>
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
    renderTable(allProducts.filter(p =>
        (p.name || '').toLowerCase().includes(q) ||
        (p.productId || '').toLowerCase().includes(q)
    ));
}

/* ── Modal open / close ──────────────────────────────────── */
function openModal(reset = true) {
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    if (reset) {
        editingId = null;
        document.getElementById('modalTitle').textContent = 'Add Product';
        ['fId', 'fName', 'fQty', 'fPrice'].forEach(id =>
            document.getElementById(id).value = ''
        );
        document.getElementById('fCategory').value = '';
        document.getElementById('fSupplier').value = '';
        document.getElementById('fId').disabled    = false;
    }
    document.getElementById('overlay').classList.add('open');
}

function closeModal() {
    document.getElementById('overlay').classList.remove('open');
}

/* ── Edit: pre-fill modal ────────────────────────────────── */
function editProduct(id) {
    const p = allProducts.find(x => x.productId === id);
    if (!p) return;

    editingId = id;
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    document.getElementById('modalTitle').textContent = 'Edit Product';
    document.getElementById('fId').value       = p.productId  || '';
    document.getElementById('fId').disabled    = true;
    document.getElementById('fName').value     = p.name       || '';
    document.getElementById('fQty').value      = p.quantity   || 0;
    document.getElementById('fPrice').value    = p.price      || 0;
    document.getElementById('fCategory').value = p.categoryId || '';
    document.getElementById('fSupplier').value = p.supplierId || '';
    openModal(false);
}

/* ── Save (Create or Update) ─────────────────────────────── */
async function saveProduct() {
    const body = {
        productId:  document.getElementById('fId').value.trim(),
        name:       document.getElementById('fName').value.trim(),
        categoryId: document.getElementById('fCategory').value,
        supplierId: document.getElementById('fSupplier').value,
        quantity:   parseInt(document.getElementById('fQty').value) || 0,
        price:      parseFloat(document.getElementById('fPrice').value) || 0
    };

    if (!body.name) {
        showToast('Product name is required', 'error');
        return;
    }

    const url    = editingId ? `${API}/products/${editingId}` : `${API}/products`;
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
            showToast(editingId ? 'Product updated' : 'Product added');
        } else {
            showToast('Error saving product', 'error');
        }
    } catch (e) {
        showToast('Server error', 'error');
    }
}

/* ── Delete ───────────────────────────────────────────────── */
async function deleteProduct(id) {
    if (!confirm('Delete this product?')) return;

    try {
        await fetch(`${API}/products/${id}`, { method: 'DELETE' });
        loadData();
        showToast('Product deleted');
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
