package org.example.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeRatesFileLoaderServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void readsUtf8FileAndSkipsComments() throws Exception {
        Path file = tempDir.resolve("rates.txt");
        Files.writeString(file, "# test\nRUB;BYN;3.5;1;100\n");
        ExchangeRatesFileLoaderService loader = loader(file);

        var rates = loader.loadRates(LocalDate.of(2026, 7, 15));

        assertThat(rates).hasSize(1);
        assertThat(rates.get(0).getExchangeRate()).isEqualByComparingTo("3.5");
        assertThat(rates.get(0).getScale()).isEqualByComparingTo("100");
    }

    @Test
    void reportsInvalidLineNumber() throws Exception {
        Path file = tempDir.resolve("rates.txt");
        Files.writeString(file, "USD;BYN\n");

        assertThatThrownBy(() -> loader(file).loadRates(LocalDate.of(2026, 7, 15)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Строка 1");
    }

    private ExchangeRatesFileLoaderService loader(Path path) {
        ExchangeRatesFileLoaderService loader = new ExchangeRatesFileLoaderService();
        ReflectionTestUtils.setField(loader, "loadingPath", path.toString());
        return loader;
    }
}
