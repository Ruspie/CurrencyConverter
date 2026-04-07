<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Конвертер валют</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="dop_style.css">
</head>
<body>
<div class="container mt-5">
    <h1 class="text-center mb-4">Конвертер валют</h1>

    <!-- Основной блок конвертера -->
    <div class="card mb-5">
        <div class="card-body p-4">
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="amount" class="form-label">Сумма:</label>
                    <input type="number" class="form-control" id="amount" value="1" min="0.01" step="0.01">
                </div>
            </div>

            <div class="row align-items-end mb-3">
                <div class="col-md-5">
                    <label for="from-currency" class="form-label">Из:</label>
                    <select class="form-select" id="from-currency">
                        <option value="USD">USD — Доллар США</option>
                        <option value="EUR" selected>EUR — Евро</option>
                        <option value="RUB">RUB — Российский рубль</option>
                        <option value="GBP">GBP — Британский фунт</option>
                        <option value="JPY">JPY — Японская иена</option>
                    </select>
                </div>

                <div class="col-md-2 text-center">
                    <button id="swap-btn" class="btn btn-outline-primary">⇄</button>
                </div>

                <div class="col-md-5">
                    <label for="to-currency" class="form-label">В:</label>
                    <select class="form-select" id="to-currency">
                        <option value="USD">USD — Доллар США</option>
                        <option value="EUR">EUR — Евро</option>
                        <option value="RUB" selected>RUB — Российский рубль</option>
                        <option value="GBP">GBP — Британский фунт</option>
                        <option value="JPY">JPY — Японская иена</option>
                    </select>
                </div>
            </div>

            <button id="convert-btn" class="btn btn-success w-100 mb-3">Конвертировать</button>
            <div id="result" class="alert alert-info text-center" role="alert">Результат появится здесь</div>
        </div>
    </div>

    <c:if test="${exchangeRateList == null}">
        <p>Список курсов валют пуст или не передан!</p>
    </c:if>

    <c:if test="${exchangeRateList != null}">
        <p>Найдено записей: ${fn:length(exchangeRateList)}</p>
    </c:if>

    <p>TEST_VALUE = ${TEST_PARAM}</p>
    <p>TEST1_VALUE1 = <%= request.getAttribute("TEST_PARAM") %></p>


    <!-- Блок популярных курсов -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Популярные курсы валют</h2>
        </div>
        <div class="card-body">
            <div id="rates-container" class="row">
                <c:forEach var="exchangeRate" items="${exchangeRateList}">
                    <div class="col-md-4 rate-card">
                        <div class="rate-currency"><span>${exchangeRate.fromCurrency.name}</span>> / <span>${exchangeRate.toCurrency.name}"</span>
                        </div>
                        <div class="rate-value">${exchangeRate.exchangeRate}</div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="script.js"></script>
</body>
</html>