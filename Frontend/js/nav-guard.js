/**
 * ============================================================
 *  nav-guard.js — Role-Based Access Control (Frontend)
 *  Reads the logged-in user's role from localStorage and:
 *    1. Hides nav links that Staff members cannot access
 *    2. Redirects Staff members if they navigate directly to
 *       a restricted page (Users, Reports, Dashboard)
 * ============================================================
 *
 * WHY: The backend already enforces business rules, but showing
 *      inaccessible links in the sidebar creates a confusing UX.
 *      This script provides a clean, role-aware navigation layer
 *      on the frontend — matching what the system design specifies:
 *        Admin    → Full access to all pages
 *        Staff    → Categories, Products, Stock Transactions, Suppliers only
 *
 * NOTE: This is a CLIENT-SIDE guard for UI purposes only.
 *       It does NOT replace server-side security — it only keeps
 *       the UI clean and consistent with the access rules.
 */

(function () {
    /* ── Read session ─────────────────────────────────────── */
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    const role        = (currentUser.role || '').toLowerCase();  // "admin" or "staff"

    /* ── Pages that Staff members are NOT allowed to see ──── */
    const ADMIN_ONLY_PAGES = ['dashboard.html', 'users.html', 'reports.html'];

    /* ── Nav links that should be hidden from Staff ────────── */
    // We identify links by their href attribute (filename only)
    const ADMIN_ONLY_LINKS = ['dashboard.html', 'users.html', 'reports.html'];

    /* ── If the user is not logged in, send to login ────────── */
    if (!currentUser.role) {
        // Only redirect if we're not already on login page
        if (!window.location.pathname.endsWith('login.html')) {
            window.location.href = 'login.html';
        }
        return;
    }

    /* ── Staff-only redirect guard ──────────────────────────── */
    if (role === 'staff') {
        const currentPage = window.location.pathname.split('/').pop();
        if (ADMIN_ONLY_PAGES.includes(currentPage)) {
            // Redirect Staff away from restricted pages to the first allowed page
            window.location.href = 'categories.html';
            return;
        }
    }

    /* ── Run after DOM is ready ─────────────────────────────── */
    document.addEventListener('DOMContentLoaded', function () {
        if (role === 'staff') {
            /* Hide admin-only nav links */
            ADMIN_ONLY_LINKS.forEach(function (page) {
                const link = document.querySelector(`.nav-item[href="${page}"]`);
                if (link) {
                    link.style.display = 'none';
                }
                // Also hide the nav-section label "Main" if Dashboard is hidden
                // We hide it only if ALL its children are hidden
            });

            /* Hide the "Main" section label if dashboard is hidden */
            hideSectionIfEmpty();

            /* On the dashboard: hide admin-only module cards */
            ['users.html', 'reports.html'].forEach(function (page) {
                const card = document.querySelector(`.module-card[href="${page}"]`);
                if (card) card.style.display = 'none';
            });
        }
    });

    /**
     * WHY: The "Main" section label sits above the Dashboard link.
     *      If we hide Dashboard for Staff, the label hangs orphaned with nothing
     *      beneath it. This function hides any nav-section label whose following
     *      nav-items are all hidden.
     */
    function hideSectionIfEmpty() {
        const sections = document.querySelectorAll('.nav-section');
        sections.forEach(function (section) {
            // Collect all nav-items between this section and the next section
            let sibling  = section.nextElementSibling;
            let hasVisible = false;
            while (sibling && !sibling.classList.contains('nav-section') && !sibling.classList.contains('sidebar-footer')) {
                if (sibling.classList.contains('nav-item') && sibling.style.display !== 'none') {
                    hasVisible = true;
                    break;
                }
                sibling = sibling.nextElementSibling;
            }
            if (!hasVisible) {
                section.style.display = 'none';
            }
        });
    }
})();
