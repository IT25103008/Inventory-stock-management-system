/**
 * ============================================================
 *  auth.js — Login Page Logic
 *  Endpoint: POST /login
 *  Matches: UserController.login()
 * ============================================================
 */

const API = 'http://localhost:8080';

/* ── Password visibility toggle ─────────────────────────── */
document.getElementById('togglePwd').addEventListener('click', function () {
    const pwd    = document.getElementById('password');
    const isText = pwd.type === 'text';
    pwd.type          = isText ? 'password' : 'text';
    this.textContent  = isText ? 'visibility' : 'visibility_off';
});

/* ── Enter-key shortcut on password field ────────────────── */
document.getElementById('password').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') doLogin();
});

/* ── Login handler ───────────────────────────────────────── */
async function doLogin() {
    const email    = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const errEl    = document.getElementById('errorMsg');
    errEl.innerHTML = '';

    /* Client-side validation */
    if (!email || !password) {
        errEl.innerHTML =
            '<span class="material-symbols-outlined">error</span>Please fill in all fields';
        return;
    }

    document.querySelector('.card').classList.add('loading');

    try {
        /* POST /login  —  { email, password }  →  Employee JSON or 401 */
        const res = await fetch(`${API}/login`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ email, password })
        });

        const data = await res.json();

        if (res.ok) {
            /* Store user session in localStorage for other pages */
            localStorage.setItem('currentUser', JSON.stringify(data));
            window.location.href = 'dashboard.html';
        } else {
            errEl.innerHTML =
                '<span class="material-symbols-outlined">error</span>' +
                (data.message || 'Invalid credentials');
        }
    } catch (err) {
        errEl.innerHTML =
            '<span class="material-symbols-outlined">wifi_off</span>Cannot connect to server';
    } finally {
        document.querySelector('.card').classList.remove('loading');
    }
}
