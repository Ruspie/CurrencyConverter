package org.example.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NBRBExchangeRateDto {

    @JsonProperty("Cur_ID")
    private int exchangeRateId;
    @JsonProperty("Date")
    private String date;
    @JsonProperty("Cur_Abbreviation")
    private String fromCurrency;
    @JsonProperty("Cur_Scale")
    private double scale;
    @JsonProperty("Cur_Name")
    private String currencyName;
    @JsonProperty("Cur_OfficialRate")
    private double exchangeRate;

}
