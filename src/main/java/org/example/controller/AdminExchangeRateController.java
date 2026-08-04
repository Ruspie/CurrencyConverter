package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.config.OpenApiConfig;
import org.example.dto.ErrorResponseDto;
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
@Tag(name = "Admin Exchange Rates", description = "CRUD курсов валют (требуется роль ADMIN)")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminExchangeRateController {

    private final AdminExchangeRateService adminExchangeRateService;

    @GetMapping
    @Operation(summary = "Список курсов", description = "Возвращает все курсы или курсы на указанную дату")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список курсов",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExchangeRateDto.class)))),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<List<ExchangeRateDto>> getAllRates(
            @Parameter(description = "Фильтр по дате (опционально)", example = "2026-08-04")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(adminExchangeRateService.getAllExchangeRates(date));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Курс по ID", description = "Возвращает курс валют по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Курс найден",
                    content = @Content(schema = @Schema(implementation = ExchangeRateDto.class))),
            @ApiResponse(responseCode = "400", description = "Курс не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<ExchangeRateDto> getRateById(
            @Parameter(description = "Идентификатор курса", required = true, example = "1")
            @PathVariable Long id
    ) throws DataNotFoundException {
        return ResponseEntity.ok(adminExchangeRateService.getExchangeRateById(id));
    }

    @PostMapping
    @Operation(summary = "Создать курс", description = "Создаёт новый курс валют")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Курс создан",
                    content = @Content(schema = @Schema(implementation = ExchangeRateDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<ExchangeRateDto> createRate(@Valid @RequestBody ExchangeRateDto exchangeRateDto) {
        return new ResponseEntity<>(adminExchangeRateService.createExchangeRate(exchangeRateDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить курс", description = "Обновляет существующий курс валют")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Курс обновлён",
                    content = @Content(schema = @Schema(implementation = ExchangeRateDto.class))),
            @ApiResponse(responseCode = "400", description = "Курс не найден или ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<ExchangeRateDto> updateRate(
            @Parameter(description = "Идентификатор курса", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ExchangeRateDto exchangeRateDto
    ) throws DataNotFoundException {
        return ResponseEntity.ok(adminExchangeRateService.updateExchangeRate(id, exchangeRateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить курс", description = "Удаляет курс валют по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Курс удалён"),
            @ApiResponse(responseCode = "400", description = "Курс не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<Void> deleteRate(
            @Parameter(description = "Идентификатор курса", required = true, example = "1")
            @PathVariable Long id
    ) throws DataNotFoundException {
        adminExchangeRateService.deleteExchangeRate(id);
        return ResponseEntity.noContent().build();
    }

}
