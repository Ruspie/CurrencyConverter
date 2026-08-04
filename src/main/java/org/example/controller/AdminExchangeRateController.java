package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.exception.DataNotFoundException;
import org.example.service.AdminExchangeRateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rates")
@RequiredArgsConstructor
public class AdminExchangeRateController {

    private final AdminExchangeRateService adminExchangeRateService;

    @GetMapping
    public ResponseEntity<List<ExchangeRateDto>> getAllRates(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(adminExchangeRateService.getAllExchangeRates(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExchangeRateDto> getRateById(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(adminExchangeRateService.getExchangeRateById(id));
    }

    @PostMapping
    public ResponseEntity<ExchangeRateDto> createRate(@Valid @RequestBody ExchangeRateDto exchangeRateDto) {
        return new ResponseEntity<>(adminExchangeRateService.createExchangeRate(exchangeRateDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExchangeRateDto> updateRate(
            @PathVariable Long id,
            @Valid @RequestBody ExchangeRateDto exchangeRateDto
    ) throws DataNotFoundException {
        return ResponseEntity.ok(adminExchangeRateService.updateExchangeRate(id, exchangeRateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Long id) throws DataNotFoundException {
        adminExchangeRateService.deleteExchangeRate(id);
        return ResponseEntity.noContent().build();
    }

}
