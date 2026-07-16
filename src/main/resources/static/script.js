const BASE = 'http://localhost:8080/api';
let accessToken = null;
let refreshToken = localStorage.getItem('rt');

// -------- Auth --------
async function authLogin(username, password) {
    const r = await fetch(`${BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
    if (!r.ok) { const e = await r.json(); throw new Error(e.error || 'Ошибка'); }
    const d = await r.json();
    accessToken = d.accessToken;
    refreshToken = d.refreshToken;
    localStorage.setItem('rt', refreshToken);
}

async function authRefresh() {
    if (!refreshToken) throw new Error('no token');
    const r = await fetch(`${BASE}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
    });
    if (!r.ok) { authLogout(); throw new Error('expired'); }
    const d = await r.json();
    accessToken = d.accessToken;
    refreshToken = d.refreshToken;
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
    } catch(e) {}
    accessToken = null;
    refreshToken = null;
    localStorage.removeItem('rt');
}

function isAuth() { return !!accessToken; }

// -------- API --------
async function apiGet(url) {
    const headers = {};
    if (accessToken) headers['Authorization'] = `Bearer ${accessToken}`;
    let r = await fetch(`${BASE}${url}`, { headers });
    if (r.status === 401 && refreshToken) {
        try { await authRefresh(); headers['Authorization'] = `Bearer ${accessToken}`; r = await fetch(`${BASE}${url}`, { headers }); }
        catch(e) { showLoginModal(); throw e; }
    }
    return r;
}

// -------- UI --------
let el = {};
function $(id) { return document.getElementById(id); }

function showLoginModal() {
    const modal = new bootstrap.Modal(el.loginModal);
    modal.show();
}

function hideLoginModal() {
    const modal = bootstrap.Modal.getInstance(el.loginModal);
    if (modal) modal.hide();
}

function updateUI() {
    if (isAuth()) {
        el.authStatus.textContent = 'Авторизован';
        el.authStatus.className = 'text-success';
        el.loginBtn.classList.add('d-none');
        el.logoutBtn.classList.remove('d-none');
        hideLoginModal();
    } else {
        el.authStatus.textContent = 'Не авторизован';
        el.authStatus.className = 'text-danger';
        el.loginBtn.classList.remove('d-none');
        el.logoutBtn.classList.add('d-none');
    }
}

function populateSelect(select, currencies) {
    select.innerHTML = '';
    currencies.forEach(c => select.add(new Option(c, c)));
}

let allCurrencies = [];

async function loadRateDates() {
    const r = await fetch(`${BASE}/rates/dates`);
    if (!r.ok) throw new Error('Не удалось загрузить даты курсов');

    const dates = await r.json();
    populateSelect(el.rateDate, dates);
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
        }
    } catch(e) {
        console.error('Ошибка загрузки валют:', e);
    }
}

function updateToSelect() {
    const selectedFrom = el.from.value;
    const currentTo = el.to.value;
    populateSelect(el.to, allCurrencies.filter(c => c !== selectedFrom));
    el.to.value = allCurrencies.includes(currentTo) && currentTo !== selectedFrom ? currentTo : el.to.options[0]?.value || '';
}

function updateFromSelect() {
    const selectedTo = el.to.value;
    const currentFrom = el.from.value;
    populateSelect(el.from, allCurrencies.filter(c => c !== selectedTo));
    el.from.value = allCurrencies.includes(currentFrom) && currentFrom !== selectedTo ? currentFrom : el.from.options[0]?.value || '';
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
    } catch(e) {
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
            let errorText = 'Ошибка конвертации';
            try {
                const errBody = await r.json();
                errorText = errBody.error || errBody.message || errorText;
            } catch(e) {}
            el.result.textContent = errorText;
            el.result.className = 'alert alert-warning text-center';
            //if (r.status === 401) showLoginModal();
            return;
        }
        const d = await r.json();
        el.result.textContent = `${amount.toFixed(2)} ${el.from.value} = ${Number(d.sum).toFixed(2)} ${d.currency}`;
        el.result.className = 'alert alert-info text-center';
    } catch(e) {
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

    el.convertBtn.onclick = convert;
    el.swapBtn.onclick = swap;
    el.amount.oninput = convert;
    el.rateDate.onchange = async () => { await loadRates(); convert(); };
    el.from.onchange = () => { updateToSelect(); convert(); };
    el.to.onchange = () => { updateFromSelect(); convert(); };

    el.loginBtn.onclick = () => { showLoginModal(); };
    el.logoutBtn.onclick = async () => { await authLogout(); updateUI(); };

    el.loginForm.onsubmit = async (e) => {
        e.preventDefault();
        const u = $('username').value;
        const p = $('password').value;
        try {
            await authLogin(u, p);
            updateUI();
            convert();
        } catch(err) {
            el.loginError.textContent = err.message;
        }
    };

    if (refreshToken) {
        try {
            await authRefresh();
        } catch(e) {
            accessToken = null;
        }
    }

    try {
        await loadRateDates();
        await loadCurrencies();
        await loadRates();
    } catch(e) {
        el.result.textContent = e.message;
        el.result.className = 'alert alert-danger text-center';
    }
    updateUI();
});