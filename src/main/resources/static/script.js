const BASE = (location.protocol.startsWith('http') && location.port === '8080')
    ? '/api'
    : 'http://localhost:8080/api';
let accessToken = null;
let refreshToken = localStorage.getItem('rt');
let currentUser = null;
let currentRoles = [];

function rolesFromAccessToken(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
        return Array.isArray(payload.roles) ? payload.roles : [];
    } catch (e) {
        return [];
    }
}

// -------- Auth --------
async function authLogin(username, password) {
    const r = await fetch(`${BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
    if (!r.ok) {
        const e = await r.json().catch(() => ({}));
        throw new Error(e.error || 'Ошибка входа');
    }
    applyAuth(await r.json());
}

async function authRefresh() {
    if (!refreshToken) throw new Error('no token');
    const r = await fetch(`${BASE}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
    });
    if (!r.ok) {
        authLogoutLocal();
        throw new Error('expired');
    }
    applyAuth(await r.json());
}

function applyAuth(data) {
    accessToken = data.accessToken;
    refreshToken = data.refreshToken;
    currentUser = data.username || null;
    currentRoles = (data.roles && data.roles.length) ? data.roles : rolesFromAccessToken(accessToken);
    if (!currentUser && accessToken) {
        try {
            const payload = JSON.parse(atob(accessToken.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
            currentUser = payload.sub || null;
        } catch (e) {}
    }
    localStorage.setItem('rt', refreshToken);
}

async function authLogout() {
    try {
        if (refreshToken) {
            await fetch(`${BASE}/auth/logout`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken })
            });
        }
    } catch (e) {}
    authLogoutLocal();
}

function authLogoutLocal() {
    accessToken = null;
    refreshToken = null;
    currentUser = null;
    currentRoles = [];
    localStorage.removeItem('rt');
}

function isAuth() {
    return !!accessToken;
}

function isAdmin() {
    return currentRoles.includes('ROLE_ADMIN') || currentRoles.includes('ADMIN');
}

// -------- API --------
async function apiRequest(url, options = {}) {
    const headers = { ...(options.headers || {}) };
    if (accessToken) headers['Authorization'] = `Bearer ${accessToken}`;
    if (options.body && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }

    let r = await fetch(`${BASE}${url}`, { ...options, headers });
    if (r.status === 401 && refreshToken) {
        try {
            await authRefresh();
            headers['Authorization'] = `Bearer ${accessToken}`;
            r = await fetch(`${BASE}${url}`, { ...options, headers });
        } catch (e) {
            showLoginModal();
            throw e;
        }
    }
    return r;
}

async function apiGet(url) {
    return apiRequest(url);
}

async function readError(response) {
    try {
        const body = await response.json();
        return body.error || body.message || `Ошибка ${response.status}`;
    } catch (e) {
        return `Ошибка ${response.status}`;
    }
}

// -------- UI --------
let el = {};
function $(id) {
    return document.getElementById(id);
}

function showLoginModal() {
    el.loginModal.classList.remove('d-none');
    el.loginModal.style.display = 'block';
}

function hideLoginModal() {
    el.loginModal.classList.add('d-none');
    el.loginModal.style.display = 'none';
}

function updateUI() {
    if (isAuth()) {
        const roleLabel = isAdmin() ? ' (ADMIN)' : '';
        el.authStatus.textContent = `Авторизован: ${currentUser || ''}${roleLabel}`;
        el.authStatus.className = 'text-success me-3';
        el.loginBtn.classList.add('d-none');
        el.logoutBtn.classList.remove('d-none');
        hideLoginModal();
    } else {
        el.authStatus.textContent = 'Не авторизован';
        el.authStatus.className = 'text-danger me-3';
        el.loginBtn.classList.remove('d-none');
        el.logoutBtn.classList.add('d-none');
    }

    if (isAdmin()) {
        el.adminPanel.classList.remove('d-none');
        loadAdminRates();
    } else {
        el.adminPanel.classList.add('d-none');
    }
}

function populateSelect(select, currencies) {
    select.innerHTML = '';
    currencies.forEach(c => select.add(new Option(c, c)));
}

function showAdminMessage(text, type = 'success') {
    el.adminMessage.textContent = text;
    el.adminMessage.className = `alert alert-${type}`;
    el.adminMessage.classList.remove('d-none');
}

function hideAdminMessage() {
    el.adminMessage.classList.add('d-none');
}

let allCurrencies = [];

async function loadRateDates() {
    const r = await fetch(`${BASE}/rates/dates`);
    if (!r.ok) throw new Error('Не удалось загрузить даты курсов');

    const dates = await r.json();
    populateSelect(el.rateDate, dates);
    populateSelect(el.adminFilterDate, ['', ...dates]);
    el.adminFilterDate.options[0].text = 'Все даты';
    if (dates.length === 0) {
        throw new Error('В базе нет доступных дат курсов');
    }
}

async function loadCurrencies() {
    try {
        const r = await fetch(`${BASE}/currencies`);
        const currencies = await r.json();

        if (currencies.length > 1) {
            allCurrencies = currencies.slice().sort();
            const defaultFrom = allCurrencies[0];
            const defaultTo = allCurrencies[1];

            populateSelect(el.from, allCurrencies);
            el.from.value = defaultFrom;

            populateSelect(el.to, allCurrencies.filter(c => c !== defaultFrom));
            el.to.value = defaultTo;

            populateSelect(el.adminFrom, allCurrencies);
            populateSelect(el.adminTo, allCurrencies);
            el.adminFrom.value = defaultFrom;
            el.adminTo.value = defaultTo;
            el.adminDate.value = new Date().toISOString().slice(0, 10);
        }
    } catch (e) {
        console.error('Ошибка загрузки валют:', e);
    }
}

function updateToSelect() {
    const selectedFrom = el.from.value;
    const currentTo = el.to.value;
    populateSelect(el.to, allCurrencies.filter(c => c !== selectedFrom));
    el.to.value = allCurrencies.includes(currentTo) && currentTo !== selectedFrom
        ? currentTo
        : el.to.options[0]?.value || '';
}

function updateFromSelect() {
    const selectedTo = el.to.value;
    const currentFrom = el.from.value;
    populateSelect(el.from, allCurrencies.filter(c => c !== selectedTo));
    el.from.value = allCurrencies.includes(currentFrom) && currentFrom !== selectedTo
        ? currentFrom
        : el.from.options[0]?.value || '';
}

async function loadRates() {
    try {
        if (!el.rateDate.value) return;
        const r = await fetch(`${BASE}/rates?date=${encodeURIComponent(el.rateDate.value)}`);
        const rates = await r.json();
        el.ratesContainer.innerHTML = '';
        el.ratesCount.textContent = `Найдено записей: ${rates.length}`;
        rates.forEach(rate => {
            const d = document.createElement('div');
            d.className = 'col-md-4';
            const scale = Number(rate.scale);
            const formattedScale = Number.isInteger(scale) ? scale.toFixed(0) : scale.toString();
            d.innerHTML = `<div class="rate-card text-center">
                <div class="rate-currency">${formattedScale} ${rate.fromCurrency} =</div>
                <div class="rate-value">${Number(rate.exchangeRate).toFixed(4)} ${rate.toCurrency}</div>
                <div class="text-muted small">Курс на ${rate.rateDate}</div>
            </div>`;
            el.ratesContainer.appendChild(d);
        });
    } catch (e) {
        console.error('Ошибка загрузки курсов:', e);
        el.ratesContainer.innerHTML = '<p class="text-danger">Не удалось загрузить курсы валют</p>';
    }
}

async function convert() {
    const amount = parseFloat(el.amount.value);
    if (isNaN(amount) || amount <= 0) {
        el.result.textContent = 'Введите сумму';
        el.result.className = 'alert alert-warning text-center';
        return;
    }
    if (!el.rateDate.value) {
        el.result.textContent = 'Выберите дату курса';
        return;
    }
    try {
        const r = await apiGet(
            `/convert?amount=${amount}&from=${el.from.value}&to=${el.to.value}&date=${encodeURIComponent(el.rateDate.value)}`
        );
        if (!r.ok) {
            el.result.textContent = await readError(r);
            el.result.className = 'alert alert-warning text-center';
            return;
        }
        const d = await r.json();
        el.result.textContent = `${amount.toFixed(2)} ${el.from.value} = ${Number(d.sum).toFixed(2)} ${d.currency}`;
        el.result.className = 'alert alert-info text-center';
    } catch (e) {
        el.result.textContent = 'Ошибка сети или сервер недоступен';
        el.result.className = 'alert alert-danger text-center';
    }
}

function swap() {
    const fromVal = el.from.value;
    const toVal = el.to.value;
    el.from.value = toVal;
    el.to.value = fromVal;
    updateToSelect();
    updateFromSelect();
    convert();
}

// -------- Admin --------
function resetAdminForm() {
    el.adminRateId.value = '';
    el.adminFormTitle.textContent = 'Добавить курс';
    el.adminSubmitBtn.textContent = 'Добавить';
    el.adminCancelBtn.classList.add('d-none');
    el.adminRate.value = '';
    el.adminScale.value = '1';
    el.adminDate.value = new Date().toISOString().slice(0, 10);
    hideAdminMessage();
}

function fillAdminForm(rate) {
    el.adminRateId.value = rate.id;
    el.adminFrom.value = rate.fromCurrency;
    el.adminTo.value = rate.toCurrency;
    el.adminRate.value = rate.exchangeRate;
    el.adminScale.value = rate.scale;
    el.adminDate.value = rate.rateDate;
    el.adminFormTitle.textContent = `Изменить курс #${rate.id}`;
    el.adminSubmitBtn.textContent = 'Сохранить';
    el.adminCancelBtn.classList.remove('d-none');
}

async function loadAdminRates() {
    if (!isAdmin()) return;
    try {
        const date = el.adminFilterDate.value;
        const url = date ? `/admin/rates?date=${encodeURIComponent(date)}` : '/admin/rates';
        const r = await apiRequest(url);
        if (!r.ok) {
            showAdminMessage(await readError(r), 'danger');
            return;
        }
        const rates = await r.json();
        el.adminRatesTbody.innerHTML = '';
        rates.forEach(rate => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${rate.id}</td>
                <td>${rate.fromCurrency}</td>
                <td>${rate.toCurrency}</td>
                <td>${Number(rate.exchangeRate).toFixed(4)}</td>
                <td>${rate.scale}</td>
                <td>${rate.rateDate}</td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary me-1" data-action="edit">Изменить</button>
                    <button class="btn btn-sm btn-outline-danger" data-action="delete">Удалить</button>
                </td>`;
            tr.querySelector('[data-action="edit"]').onclick = () => fillAdminForm(rate);
            tr.querySelector('[data-action="delete"]').onclick = () => deleteAdminRate(rate.id);
            el.adminRatesTbody.appendChild(tr);
        });
    } catch (e) {
        showAdminMessage('Не удалось загрузить курсы для админки', 'danger');
    }
}

async function deleteAdminRate(id) {
    if (!confirm(`Удалить курс #${id}?`)) return;
    try {
        const r = await apiRequest(`/admin/rates/${id}`, { method: 'DELETE' });
        if (!r.ok) {
            showAdminMessage(await readError(r), 'danger');
            return;
        }
        showAdminMessage(`Курс #${id} удалён`);
        resetAdminForm();
        await Promise.all([loadRateDates(), loadRates(), loadAdminRates()]);
    } catch (e) {
        showAdminMessage('Ошибка удаления', 'danger');
    }
}

async function submitAdminRate(e) {
    e.preventDefault();
    const payload = {
        fromCurrency: el.adminFrom.value,
        toCurrency: el.adminTo.value,
        exchangeRate: Number(el.adminRate.value),
        scale: Number(el.adminScale.value),
        rateDate: el.adminDate.value
    };
    const id = el.adminRateId.value;
    const isEdit = !!id;

    try {
        const r = await apiRequest(isEdit ? `/admin/rates/${id}` : '/admin/rates', {
            method: isEdit ? 'PUT' : 'POST',
            body: JSON.stringify(payload)
        });
        if (!r.ok) {
            showAdminMessage(await readError(r), 'danger');
            return;
        }
        showAdminMessage(isEdit ? `Курс #${id} обновлён` : 'Курс добавлен');
        resetAdminForm();
        await Promise.all([loadRateDates(), loadRates(), loadAdminRates()]);
    } catch (err) {
        showAdminMessage('Ошибка сохранения', 'danger');
    }
}

// -------- Старт --------
document.addEventListener('DOMContentLoaded', async () => {
    el.amount = $('amount');
    el.rateDate = $('rate-date');
    el.from = $('from-currency');
    el.to = $('to-currency');
    el.convertBtn = $('convert-btn');
    el.result = $('result');
    el.swapBtn = $('swap-btn');
    el.ratesContainer = $('rates-container');
    el.ratesCount = $('rates-count');
    el.loginBtn = $('login-btn');
    el.logoutBtn = $('logout-btn');
    el.loginModal = $('login-modal');
    el.loginForm = $('login-form');
    el.loginError = $('login-error');
    el.authStatus = $('auth-status');
    el.adminPanel = $('admin-panel');
    el.adminMessage = $('admin-message');
    el.adminRateForm = $('admin-rate-form');
    el.adminFormTitle = $('admin-form-title');
    el.adminRateId = $('admin-rate-id');
    el.adminFrom = $('admin-from');
    el.adminTo = $('admin-to');
    el.adminRate = $('admin-rate');
    el.adminScale = $('admin-scale');
    el.adminDate = $('admin-date');
    el.adminSubmitBtn = $('admin-submit-btn');
    el.adminCancelBtn = $('admin-cancel-btn');
    el.adminFilterDate = $('admin-filter-date');
    el.adminReloadBtn = $('admin-reload-btn');
    el.adminRatesTbody = $('admin-rates-tbody');

    el.convertBtn.onclick = convert;
    el.swapBtn.onclick = swap;
    el.amount.oninput = convert;
    el.rateDate.onchange = async () => {
        await loadRates();
        convert();
    };
    el.from.onchange = () => {
        updateToSelect();
        convert();
    };
    el.to.onchange = () => {
        updateFromSelect();
        convert();
    };

    el.loginBtn.onclick = () => showLoginModal();
    el.logoutBtn.onclick = async () => {
        await authLogout();
        updateUI();
    };

    el.loginForm.onsubmit = async (e) => {
        e.preventDefault();
        const u = $('username').value;
        const p = $('password').value;
        try {
            await authLogin(u, p);
            updateUI();
            convert();
        } catch (err) {
            el.loginError.textContent = err.message;
        }
    };

    el.adminRateForm.onsubmit = submitAdminRate;
    el.adminCancelBtn.onclick = resetAdminForm;
    el.adminReloadBtn.onclick = loadAdminRates;
    el.adminFilterDate.onchange = loadAdminRates;

    if (refreshToken) {
        try {
            await authRefresh();
        } catch (e) {
            accessToken = null;
        }
    }

    try {
        await loadRateDates();
        await loadCurrencies();
        await loadRates();
    } catch (e) {
        el.result.textContent = e.message;
        el.result.className = 'alert alert-danger text-center';
    }
    updateUI();
});
