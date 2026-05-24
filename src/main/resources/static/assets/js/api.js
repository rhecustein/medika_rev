/* ─── API Wrapper ─────────────────────────────────────────── */
const BASE_URL = '';

async function apiRequest(method, url, body = null, isBlob = false) {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = 'Bearer ' + token;

  const options = { method, headers };
  if (body) options.body = JSON.stringify(body);

  try {
    const res = await fetch(BASE_URL + url, options);

    if (res.status === 401) {
      localStorage.clear();
      window.location.href = '/login.html';
      return null;
    }

    if (isBlob) {
      if (res.ok) return await res.blob();
      throw new Error('Download gagal');
    }

    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Terjadi kesalahan');
    return data;
  } catch (e) {
    throw e;
  }
}

const api = {
  get:    (url)           => apiRequest('GET',    url),
  post:   (url, body)     => apiRequest('POST',   url, body),
  put:    (url, body)     => apiRequest('PUT',    url, body),
  delete: (url)           => apiRequest('DELETE', url),
  blob:   (url)           => apiRequest('GET',    url, null, true),
};
