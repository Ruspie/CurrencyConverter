// Курсы валют относительно USD (для демонстрации)
const exchangeRates = {
    USD: 1,
    EUR: 0.85,
    RUB: 90,
    GBP: 0.73,
    JPY: 140
};

// Элементы DOM
const amountInput = document.getElementById('amount');
const fromCurrency = document.getElementById('from-currency');
const toCurrency = document.getElementById('to-currency');
const convertBtn = document.getElementById('convert-btn');
const resultDiv = document.getElementById('result');
const swapBtn = document.getElementById('swap-btn');
const ratesContainer = document.getElementById('rates-container');

// Инициализация популярных курсов
function initPopularRates() {
    const baseCurrency = 'USD';

    Object.keys(exchangeRates).forEach(currency => {
        if (currency !== baseCurrency) {
            const rate = exchangeRates[currency];
            const card = document.createElement('div');
            card.className = 'col-md-4 rate-card';

            card.innerHTML = `
                <div class="rate-currency">${currency} / ${baseCurrency}</div>
                <div class="rate-value">${rate.toFixed(4)}</div>
            `;

            ratesContainer.appendChild(card);
        }
    });
}

// Конвертация валюты
function convertCurrency() {
    const amount = parseFloat(amountInput.value);
    const from = fromCurrency.value;
    const to = toCurrency.value;

    if (isNaN(amount) || amount <= 0) {
        resultDiv.textContent = 'Пожалуйста, введите корректную сумму';
        resultDiv.className = 'alert alert-warning text-center';
        return;
    }

    // Конвертация через USD как базовую валюту
    const fromRate = exchangeRates[from];
    const toRate = exchangeRates[to];

    const result = (amount / fromRate) * toRate;

    resultDiv.textContent = `${amount.toFixed(2)} ${from} = ${result.toFixed(2)} ${to}`;
    resultDiv.className = 'alert alert-info text-center';
}

// Обмен валют местами
function swapCurrencies() {
    const temp = fromCurrency.value;
    fromCurrency.value = toCurrency.value;
    toCurrency.value = temp;

    // Обновляем результат после смены
    convertCurrency();
}

// Обработчики событий
convertBtn.addEventListener('click', convertCurrency);
swapBtn.addEventListener('click', swapCurrencies);

// Автоконвертация при изменении суммы или валюты
amountInput.addEventListener('input', convertCurrency);
fromCurrency.addEventListener('change', convertCurrency);
toCurrency.addEventListener('change', convertCurrency);

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    initPopularRates();
    convertCurrency(); // Показываем начальный результат
});