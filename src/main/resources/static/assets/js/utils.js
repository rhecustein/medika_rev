/* ─── Utility Functions ───────────────────────────────────── */

// Toast notification
function showToast(message, type = 'success') {
  let container = document.querySelector('.toast-container-custom');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container-custom';
    document.body.appendChild(container);
  }

  const colors = {
    success: '#27AE60',
    error:   '#C0392B',
    warning: '#E67E22',
    info:    '#2980B9'
  };

  const toast = document.createElement('div');
  toast.style.cssText = `
    background:${colors[type]||colors.info};
    color:#fff;
    padding:12px 20px;
    border-radius:8px;
    margin-bottom:8px;
    font-size:.875rem;
    box-shadow:0 4px 12px rgba(0,0,0,.2);
    animation:slideIn .3s ease;
    max-width:360px;
    display:flex;
    align-items:center;
    gap:10px;
  `;

  const icons = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };
  toast.innerHTML = `<span style="font-size:1.1rem">${icons[type]||icons.info}</span><span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity .3s';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// Loading button state
function setLoading(btn, loading, originalText) {
  if (loading) {
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Loading...';
  } else {
    btn.disabled = false;
    btn.innerHTML = originalText;
  }
}

// Format date
function formatDate(dateStr) {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  return d.toLocaleDateString('id-ID', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

// Format number 4 decimal
function fmt4(val) {
  if (val === null || val === undefined) return '0.0000';
  return parseFloat(val).toFixed(4);
}

// Status badge HTML
function statusBadge(status) {
  const map = {
    PROSES:   { cls: 'badge-proses',   label: 'Proses' },
    DITERIMA: { cls: 'badge-diterima', label: 'Diterima' },
    DITOLAK:  { cls: 'badge-ditolak',  label: 'Ditolak' }
  };
  const s = map[status] || { cls: 'bg-secondary', label: status };
  return `<span class="badge ${s.cls} px-2 py-1">${s.label}</span>`;
}

// Role badge
function roleBadge(role) {
  const map = {
    ADMIN:   'bg-danger',
    HRD:     'bg-primary',
    PENILAI: 'bg-secondary'
  };
  return `<span class="badge ${map[role]||'bg-secondary'}">${role}</span>`;
}

// Confirm dialog (simple)
function confirmDialog(message) {
  return confirm(message);
}

// Pagination render
function renderPagination(containerId, page, totalPages, onPageChange) {
  const el = document.getElementById(containerId);
  if (!el) return;
  if (totalPages <= 1) { el.innerHTML = ''; return; }

  let html = '<ul class="pagination pagination-sm mb-0">';
  html += `<li class="page-item ${page===0?'disabled':''}">
    <a class="page-link" href="#" onclick="return false;" data-p="${page-1}">‹</a></li>`;

  for (let i = 0; i < totalPages; i++) {
    if (totalPages > 7 && Math.abs(i - page) > 2 && i !== 0 && i !== totalPages-1) {
      if (i === 1 || i === totalPages-2) {
        html += '<li class="page-item disabled"><span class="page-link">…</span></li>';
      }
      continue;
    }
    html += `<li class="page-item ${i===page?'active':''}">
      <a class="page-link" href="#" onclick="return false;" data-p="${i}">${i+1}</a></li>`;
  }

  html += `<li class="page-item ${page===totalPages-1?'disabled':''}">
    <a class="page-link" href="#" onclick="return false;" data-p="${page+1}">›</a></li>`;
  html += '</ul>';

  el.innerHTML = html;
  el.querySelectorAll('[data-p]').forEach(a => {
    a.addEventListener('click', () => {
      const p = parseInt(a.dataset.p);
      if (p >= 0 && p < totalPages) onPageChange(p);
    });
  });
}

// Add CSS animation
const style = document.createElement('style');
style.textContent = `@keyframes slideIn {
  from { opacity:0; transform: translateX(30px); }
  to   { opacity:1; transform: translateX(0);    }
}`;
document.head.appendChild(style);
