// Базовый URL вашего REST API (измените порт и путь под ваш сервер)
//const API_BASE_URL = '/api';
const API_BASE_URL = 'http://localhost:8080/api';

// Элементы DOM
const amountInput = document.getElementById('amount');
const fromCurrency = document.getElementById('from-currency');
const toCurrency = document.getElementById('to-currency');
const convertBtn = document.getElementById('convert-btn');
const resultDiv = document.getElementById('result');
const swapBtn = document.getElementById('swap-btn');
const ratesContainer = document.getElementById('rates-container');
const ratesCountText = document.getElementById('rates-count');

// 1. Загрузка валют для выпадающих списков
async function loadCurrencies() {
    try {
        const response = await fetch(`${API_BASE_URL}/currencies`);
        const currencies = await response.json(); // Ожидаем массив строк, например: ["USD", "EUR", "RUB", "GBP", "JPY"]

        fromCurrency.innerHTML = '';
        toCurrency.innerHTML = '';

        currencies.forEach(code => {
            const optFrom = new Option(code, code);
            const optTo = new Option(code, code);

            // Устанавливаем дефолтные значения из вашего старого кода
            if (code === 'EUR') optFrom.selected = true;
            if (code === 'RUB') optTo.selected = true;

            fromCurrency.add(optFrom);
            toCurrency.add(optTo);
        });
    } catch (error) {
        console.error('Ошибка загрузки списка валют:', error);
    }
}

// 2. Загрузка карточек с популярными курсами
async function loadPopularRates() {
    try {
        const response = await fetch(`${API_BASE_URL}/rates`);
        const rates = await response.json(); // Ожидаем массив объектов exchangeRateList

        ratesContainer.innerHTML = '';
        ratesCountText.textContent = `Найдено записей: ${rates.length}`;

        rates.forEach(rate => {
            const card = document.createElement('div');
            card.className = 'col-md-4';
            card.innerHTML = `
                <div class="rate-card text-center">
                    <div class="rate-currency">${rate.fromCurrency} / ${rate.toCurrency}</div>
                    <div class="rate-value">${Number(rate.exchangeRate).toFixed(4)}</div>
                </div>
            `;
            ratesContainer.appendChild(card);
        });
    } catch (error) {
        console.error('Ошибка загрузки курсов валют:', error);
        ratesContainer.innerHTML = '<p class="text-danger">Не удалось загрузить курсы валют</p>';
    }
}

// 3. Запрос на конвертацию через сервер
async function convertCurrency() {
    const amount = parseFloat(amountInput.value);
    const from = fromCurrency.value;
    const to = toCurrency.value;

    if (isNaN(amount) || amount <= 0) {
        resultDiv.textContent = 'Пожалуйста, введите корректную сумму';
        resultDiv.className = 'alert alert-warning text-center';
        return;
    }

    try {
        // Отправляем GET запрос вида: /api/convert?amount=100&from=EUR&to=RUB
        const response = await fetch(`${API_BASE_URL}/convert?amount=${amount}&from=${from}&to=${to}`);
        const data = await response.json(); // Ожидаем объект { result: X.XX }

        resultDiv.textContent = `${amount.toFixed(2)} ${from} = ${Number(data.result).toFixed(2)} ${to}`;
        resultDiv.className = 'alert alert-info text-center';
    } catch (error) {
        console.error('Ошибка при конвертации:', error);
        resultDiv.textContent = 'Ошибка сервера при расчете';
        resultDiv.className = 'alert alert-danger text-center';
    }
}

// Изменение направления обмена
function swapCurrencies() {
    const temp = fromCurrency.value;
    fromCurrency.value = toCurrency.value;
    toCurrency.value = temp;
    convertCurrency();
}

// Слушатели событий
convertBtn.addEventListener('click', convertCurrency);
swapBtn.addEventListener('click', swapCurrencies);
amountInput.addEventListener('input', convertCurrency);
fromCurrency.addEventListener('change', convertCurrency);
toCurrency.addEventListener('change', convertCurrency);

// Старт приложения
document.addEventListener('DOMContentLoaded', async () => {
    await loadCurrencies();   // Сначала загружаем списки валют
    await loadPopularRates(); // Затем карточки курсов
    convertCurrency();        // Считаем начальное значение
});