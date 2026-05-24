/* ─── Auth Helpers ────────────────────────────────────────── */

function getUser() {
  try {
    return JSON.parse(localStorage.getItem('user') || 'null');
  } catch { return null; }
}

function getToken() {
  return localStorage.getItem('token');
}

function isLoggedIn() {
  return !!getToken();
}

function logout() {
  localStorage.clear();
  window.location.href = '/login.html';
}

function requireAuth() {
  if (!isLoggedIn()) {
    window.location.href = '/login.html';
    return null;
  }
  return getUser();
}

function requireRole(allowedRoles) {
  const user = requireAuth();
  if (!user) return null;
  if (!allowedRoles.includes(user.role)) {
    alert('Akses ditolak untuk role: ' + user.role);
    window.location.href = '/dashboard.html';
    return null;
  }
  return user;
}

// Render sidebar + topbar
function renderLayout(pageTitle, activeMenu) {
  const user = requireAuth();
  if (!user) return;

  const isAdmin  = user.role === 'ADMIN';
  const isHrd    = user.role === 'HRD';

  const menuItems = [
    { icon: 'bi-speedometer2', label: 'Dashboard',        href: 'dashboard.html',    key: 'dashboard',    roles: ['ADMIN','HRD','PENILAI'] },
    { icon: 'bi-people',       label: 'Kandidat',         href: 'kandidat.html',     key: 'kandidat',     roles: ['ADMIN','HRD'] },
    { icon: 'bi-list-check',   label: 'Kriteria',         href: 'kriteria.html',     key: 'kriteria',     roles: ['ADMIN'] },
    { icon: 'bi-diagram-3',    label: 'Sub Kriteria',     href: 'sub-kriteria.html', key: 'sub-kriteria', roles: ['ADMIN'] },
    { icon: 'bi-pencil-square','label': 'Penilaian',      href: 'penilaian.html',    key: 'penilaian',    roles: ['ADMIN','HRD','PENILAI'] },
    { icon: 'bi-calculator',   label: 'Perhitungan SMART',href: 'smart.html',        key: 'smart',        roles: ['ADMIN','HRD'] },
    { icon: 'bi-trophy',       label: 'Hasil & Ranking',  href: 'hasil.html',        key: 'hasil',        roles: ['ADMIN','HRD','PENILAI'] },
    { icon: 'bi-list-ol',      label: 'Hasil Akhir',      href: 'hasil-akhir.html',  key: 'hasil-akhir',  roles: ['ADMIN','HRD','PENILAI'] },
    { icon: 'bi-file-earmark-text', label: 'Laporan',     href: 'laporan.html',      key: 'laporan',      roles: ['ADMIN','HRD'] },
    { icon: 'bi-person-gear',  label: 'Manajemen User',   href: 'user.html',         key: 'user',         roles: ['ADMIN'] },
  ];

  const menuHtml = menuItems
    .filter(m => m.roles.includes(user.role))
    .map(m => `
      <li class="nav-item">
        <a href="/${m.href}" class="${activeMenu===m.key?'active':''}">
          <i class="bi ${m.icon}"></i>${m.label}
        </a>
      </li>`)
    .join('');

  const initials = user.nama ? user.nama.split(' ').map(w=>w[0]).slice(0,2).join('').toUpperCase() : 'U';

  const sidebar = document.getElementById('sidebar');
  if (sidebar) {
    sidebar.innerHTML = `
      <div class="brand">
        <div class="brand-logo">🏥 SPK Medika</div>
        <div class="brand-sub">Akses Investama</div>
      </div>
      <div class="nav-section">
        <div class="nav-label">Menu</div>
        <ul class="list-unstyled mb-0">${menuHtml}</ul>
      </div>
      <div class="sidebar-footer">
        <button onclick="logout()" class="sidebar-logout-btn">
          <i class="bi bi-box-arrow-left"></i> Logout
        </button>
        <div class="mt-2" style="font-size:.7rem;color:rgba(255,255,255,.4)">v1.0 &nbsp;·&nbsp; SMART DSS</div>
      </div>`;
  }

  const topbar = document.getElementById('topbar');
  if (topbar) {
    topbar.innerHTML = `
      <div class="page-title">${pageTitle}</div>
      <div class="dropdown">
        <div class="user-info dropdown-toggle" data-bs-toggle="dropdown">
          <div class="user-avatar">${initials}</div>
          <div>
            <div class="user-name">${user.nama}</div>
            <div class="user-role">${user.role}</div>
          </div>
        </div>
        <ul class="dropdown-menu dropdown-menu-end">
          <li><span class="dropdown-item-text text-muted small">${user.username}</span></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item text-danger" href="#" onclick="logout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
        </ul>
      </div>`;
  }
}
