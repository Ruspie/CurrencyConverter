package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ExchangeRateDto;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.service.ExchangeRatesLoaderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@ConditionalOnProperty(name = "loading.mode", havingValue = "file")
@Slf4j
public class ExchangeRatesFileLoaderService implements ExchangeRatesLoaderService {

    @Value("${loading.file-path}")
    private String loadingPath;

    @Override
    public List<ExchangeRateDto> loadRates(LocalDate date) throws IOException {
        List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();
        Path path = Path.of(loadingPath);

        try (BufferedReader fileReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String exchangeRateLine;
            int lineNumber = 0;
            while ((exchangeRateLine = fileReader.readLine()) != null) {
                lineNumber++;
                if (exchangeRateLine.isBlank() || exchangeRateLine.startsWith("#")) {
                    continue;
                }
                exchangeRateDtos.add(parseLine(exchangeRateLine, lineNumber, date));
            }
        }

        log.info("Загружено {} базовых курсов из файла {}", exchangeRateDtos.size(), path);
        return exchangeRateDtos;
    }

    private ExchangeRateDto parseLine(String line, int lineNumber, LocalDate date) {
        String[] fields = line.split(";", -1);
        if (fields.length != 5) {
            throw new IllegalArgumentException("Строка " + lineNumber + ": ожидается 5 полей");
        }

        try {
            BigDecimal sourceScale = new BigDecimal(fields[3].trim());
            if (sourceScale.signum() == 0) {
                throw new IllegalArgumentException("исходный scale не может быть нулём");
            }

            return new ExchangeRateDto(
                    CurrencyCodeEnum.valueOf(fields[0].trim().toUpperCase(Locale.ROOT)),
                    CurrencyCodeEnum.valueOf(fields[1].trim().toUpperCase(Locale.ROOT)),
                    new BigDecimal(fields[2].trim()),
                    new BigDecimal(fields[4].trim()).divide(sourceScale, 10, RoundingMode.HALF_UP),
                    date
            );
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(
                    "Некорректный курс в строке " + lineNumber + ": " + exception.getMessage(),
                    exception
            );
        }
    }
}
