/**
 * ============================================================
 *  dashboard.js — Dashboard Statistics & User Greeting
 *  Endpoints:
 *    GET /products           →  ProductController.getAll()
 *    GET /categories         →  CategoryController.getAll()
 *    GET /suppliers          →  SupplierController.getAll()
 *    GET /reports/low-stock  →  ReportController.getLowStock()
 * ============================================================
 */

const API = 'http://localhost:8080';

/* ── Session: populate sidebar user chip ─────────────────── */
const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
document.getElementById('userName').textContent    = user.name  || 'Guest';
document.getElementById('userRole').textContent    = user.role  || 'Unknown';
document.getElementById('userInitial').textContent = (user.name || 'G')[0].toUpperCase();

/* ── Greeting based on time of day ───────────────────────── */
const now  = new Date();
const hour = now.getHours();
const greet = hour < 12 ? 'Good morning' : hour < 18 ? 'Good afternoon' : 'Good evening';
document.getElementById('greeting').textContent =
    `${greet}, ${user.name || 'there'} — here's your inventory overview`;
document.getElementById('todayDate').textContent =
    now.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' });

/* ── Load dashboard stats from backend ───────────────────── */
async function loadStats() {
    try {
        const [prods, cats, sups, low] = await Promise.all([
            fetch(`${API}/products`).then(r => r.json()),
            fetch(`${API}/categories`).then(r => r.json()),
            fetch(`${API}/suppliers`).then(r => r.json()),
            fetch(`${API}/reports/low-stock`).then(r => r.json())
        ]);

        document.getElementById('statProducts').textContent   = prods.length;
        document.getElementById('statCategories').textContent  = cats.length;
        document.getElementById('statSuppliers').textContent   = sups.length;
        document.getElementById('statLow').textContent         = low.length;
    } catch (e) {
        /* Graceful fallback when backend is offline */
        ['statProducts', 'statCategories', 'statSuppliers', 'statLow'].forEach(id => {
            document.getElementById(id).textContent = '—';
        });
    }
}

/* ── Init ────────────────────────────────────────────────── */
loadStats();
