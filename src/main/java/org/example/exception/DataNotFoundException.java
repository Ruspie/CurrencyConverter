package org.example.exception;

import org.example.dto.CurrencyCodeEnum;

public class DataNotFoundException extends Exception {

    CurrencyCodeEnum fromCurrency;
    CurrencyCodeEnum toCurrency;

    public DataNotFoundException(String message, CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency) {
        super(message);
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    @Override
    public String toString() {
        return super.toString() + " fromCurrency=" + fromCurrency +
               ", toCurrency=" + toCurrency;
    }

}
