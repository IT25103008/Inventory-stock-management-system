/**
 * ============================================================
 *  users.js — User & Admin CRUD
 *  Endpoints:
 *    GET    /users        →  UserController.getAllUsers()
 *    GET    /users/{id}   →  UserController.getUser()
 *    POST   /users        →  UserController.addUser()
 *    PUT    /users/{id}   →  UserController.updateUser()
 *    DELETE /users/{id}   →  UserController.deleteUser()
 * ============================================================
 */

const API = 'http://localhost:8080';
let allUsers  = [];
let editingId = null;
let ignoreSearch = false;

/* ── Session: populate sidebar user chip ─────────────────── */
const u = JSON.parse(localStorage.getItem('currentUser') || '{}');
document.getElementById('userName').textContent    = u.name  || '—';
document.getElementById('userRole').textContent    = u.role  || '—';
document.getElementById('userInitial').textContent = (u.name || 'G')[0].toUpperCase();

/* ── Load all users from backend ─────────────────────────── */
async function loadData() {
    try {
        const res = await fetch(`${API}/users`);
        allUsers  = await res.json();
        filterTable();
    } catch (e) {
        showToast('Cannot connect to server', 'error');
    }
}

/* ── Render users into table ─────────────────────────────── */
function renderTable(data) {
    const tbody = document.getElementById('tableBody');

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state">
            <span class="material-symbols-outlined">manage_accounts</span>No users found
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = data.map(u => `
        <tr>
            <td style="font-family:monospace;font-size:13px;color:var(--on-muted)">${u.userId || ''}</td>
            <td style="font-weight:500">${u.name || ''}</td>
            <td style="color:var(--on-muted)">${u.email || ''}</td>
            <td style="color:var(--on-muted)">${u.phone || '—'}</td>
            <td><span class="chip chip-${(u.role || '').toLowerCase()}">${u.role || ''}</span></td>
            <td><span class="chip chip-${(u.status || 'Active').toLowerCase()}">${u.status || 'Active'}</span></td>
            <td><div class="actions">
                <button class="icon-btn" onclick="editUser('${u.userId}')"><span class="material-symbols-outlined">edit</span></button>
                <button class="icon-btn" onclick="deleteUser('${u.userId}')"><span class="material-symbols-outlined">delete</span></button>
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
    renderTable(allUsers.filter(u =>
        (u.name || '').toLowerCase().includes(q) ||
        (u.email || '').toLowerCase().includes(q) ||
        (u.userId || '').toLowerCase().includes(q)
    ));
}

/* ── Modal open / close ──────────────────────────────────── */
function openModal(reset = true) {
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    if (reset) {
        editingId = null;
        document.getElementById('modalTitle').textContent = 'Add User';
        ['fId', 'fName', 'fEmail', 'fPassword', 'fPhone'].forEach(id =>
            document.getElementById(id).value = ''
        );
        document.getElementById('fRole').value   = 'Admin';
        document.getElementById('fStatus').value = 'Active';
        document.getElementById('fId').disabled  = false;
    }
    document.getElementById('overlay').classList.add('open');
}

function closeModal() {
    document.getElementById('overlay').classList.remove('open');
}

/* ── Edit: pre-fill modal with existing user data ────────── */
function editUser(id) {
    const u = allUsers.find(x => x.userId === id);
    if (!u) return;

    editingId = id;
    ignoreSearch = true;
    setTimeout(() => { ignoreSearch = false; }, 800);
    document.getElementById('modalTitle').textContent = 'Edit User';
    document.getElementById('fId').value       = u.userId || '';
    document.getElementById('fId').disabled    = true;
    document.getElementById('fName').value     = u.name   || '';
    document.getElementById('fEmail').value    = u.email  || '';
    document.getElementById('fPassword').value = '';
    document.getElementById('fPhone').value    = u.phone  || '';
    document.getElementById('fRole').value     = u.role   || 'Admin';
    document.getElementById('fStatus').value   = u.status || 'Active';
    openModal(false);
}

/* ── Save (Create or Update) ─────────────────────────────── */
async function saveUser() {
    const body = {
        userId:   document.getElementById('fId').value.trim(),
        name:     document.getElementById('fName').value.trim(),
        email:    document.getElementById('fEmail').value.trim(),
        password: document.getElementById('fPassword').value || '1234',
        phone:    document.getElementById('fPhone').value.trim(),
        role:     document.getElementById('fRole').value,
        status:   document.getElementById('fStatus').value
    };

    if (!body.name || !body.email) {
        showToast('Name and email are required', 'error');
        return;
    }

    const url    = editingId ? `${API}/users/${editingId}` : `${API}/users`;
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
            showToast(editingId ? 'User updated' : 'User added');
        } else {
            const d = await res.json();
            showToast(d.message || 'Error saving', 'error');
        }
    } catch (e) {
        showToast('Server error', 'error');
    }
}

/* ── Delete ───────────────────────────────────────────────── */
async function deleteUser(id) {
    if (!confirm('Delete this user?')) return;

    try {
        await fetch(`${API}/users/${id}`, { method: 'DELETE' });
        loadData();
        showToast('User deleted');
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
