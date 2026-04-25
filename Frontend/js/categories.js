/**
 * ============================================================
 *  categories.js — Category CRUD
 *  Endpoints:
 *    GET    /categories        →  CategoryController.getAll()
 *    GET    /categories/{id}   →  CategoryController.getOne()
 *    POST   /categories        →  CategoryController.add()
 *    PUT    /categories/{id}   →  CategoryController.update()
 *    DELETE /categories/{id}   →  CategoryController.delete()
 * ============================================================
 */

const API = 'http://localhost:8080';
let allCategories = [];
let editingId     = null;
let ignoreSearch = false;

/* ── Session: populate sidebar user chip ─────────────────── */
const u = JSON.parse(localStorage.getItem('currentUser') || '{}');
document.getElementById('userName').textContent    = u.name  || '—';
document.getElementById('userRole').textContent    = u.role  || '—';
document.getElementById('userInitial').textContent = (u.name || 'G')[0].toUpperCase();

/* ── Load all categories ─────────────────────────────────── */
async function loadData() {
    try {
        const res     = await fetch(`${API}/categories`);
        allCategories = await res.json();
        filterTable();
    } catch (e) {
        showToast('Cannot connect to server', 'error');
    }
}

/* ── Render categories into table ────────────────────────── */
function renderTable(data) {
    const tbody = document.getElementById('tableBody');

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="4"><div class="empty-state">
            <span class="material-symbols-outlined">category</span>No categories found
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = data.map(c => `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${c.categoryId || ''}</td>
            <td style="font-weight:500">${c.name || ''}</td>
            <td style="color:var(--on-muted)">${c.description || '—'}</td>
            <td><div class="actions">
                <button class="icon-btn" onclick="editCategory('${c.categoryId}')"><span class="material-symbols-outlined">edit</span></button>
                <button class="icon-btn" onclick="deleteCategory('${c.categoryId}')"><span class="material-symbols-outlined">delete</span></button>
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
    renderTable(allCategories.filter(c =>
        (c.name || '').toLowerCase().includes(q) ||
        (c.categoryId || '').toLowerCase().includes(q)
    ));
}

/* ── Modal open / close ──────────────────────────────────── */
function openModal(reset = true) {
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    if (reset) {
        editingId = null;
        document.getElementById('modalTitle').textContent = 'Add Category';
        ['fId', 'fName', 'fDesc'].forEach(id =>
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
function editCategory(id) {
    const c = allCategories.find(x => x.categoryId === id);
    if (!c) return;

    editingId = id;
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    document.getElementById('modalTitle').textContent = 'Edit Category';
    document.getElementById('fId').value     = c.categoryId  || '';
    document.getElementById('fId').disabled  = true;
    document.getElementById('fName').value   = c.name        || '';
    document.getElementById('fDesc').value   = c.description || '';
    openModal(false);
}

/* ── Save (Create or Update) ─────────────────────────────── */
async function saveCategory() {
    const body = {
        categoryId:  document.getElementById('fId').value.trim(),
        name:        document.getElementById('fName').value.trim(),
        description: document.getElementById('fDesc').value.trim()
    };

    if (!body.name) {
        showToast('Category name is required', 'error');
        return;
    }

    const url    = editingId ? `${API}/categories/${editingId}` : `${API}/categories`;
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
            showToast(editingId ? 'Category updated' : 'Category added');
        } else {
            showToast('Error saving', 'error');
        }
    } catch (e) {
        showToast('Server error', 'error');
    }
}

/* ── Delete ───────────────────────────────────────────────── */
async function deleteCategory(id) {
    if (!confirm('Delete this category?')) return;

    try {
        await fetch(`${API}/categories/${id}`, { method: 'DELETE' });
        loadData();
        showToast('Category deleted');
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
