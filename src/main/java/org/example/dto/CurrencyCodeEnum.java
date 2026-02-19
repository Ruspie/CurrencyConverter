package org.example.dto;

public enum CurrencyCodeEnum {

    BYN("Белорусский рубль"),
    EUR("ЕВРО"),
    USD("Доллар США"),
    RUB("Российский рубль"),
    ZL("Польский злотый");

    private final String description;

    CurrencyCodeEnum(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

}
