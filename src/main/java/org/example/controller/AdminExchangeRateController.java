package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.exception.DataNotFoundException;
import org.example.service.ExchangeRateAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rates")
@RequiredArgsConstructor
public class AdminExchangeRateController {

    private final ExchangeRateAdminService exchangeRateAdminService;

    @GetMapping
    public ResponseEntity<List<ExchangeRateDto>> getRates(@RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(exchangeRateAdminService.getAllExchangeRates(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExchangeRateDto> getRate(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(exchangeRateAdminService.getExchangeRateById(id));
    }

    @PostMapping
    public ResponseEntity<ExchangeRateDto> createRate(@RequestBody ExchangeRateDto exchangeRateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exchangeRateAdminService.createExchangeRate(exchangeRateDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExchangeRateDto> updateRate(
            @PathVariable Long id,
            @RequestBody ExchangeRateDto exchangeRateDto
    ) throws DataNotFoundException {
        return ResponseEntity.ok(exchangeRateAdminService.updateExchangeRate(id, exchangeRateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Long id) throws DataNotFoundException {
        exchangeRateAdminService.deleteExchangeRate(id);
        return ResponseEntity.noContent().build();
    }
}
