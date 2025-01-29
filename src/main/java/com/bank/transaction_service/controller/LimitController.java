package com.bank.transaction_service.controller;

import com.bank.transaction_service.model.Limit;
import com.bank.transaction_service.model.ExpenseCategory;
import com.bank.transaction_service.service.LimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/limits")
@Tag(name = "Limit API", description = "API для работы с лимитами")
public class LimitController {
    private final LimitService limitService;

    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @Operation(
            summary = "Создать новый лимит",
            description = "Создает новый лимит для указанной категории расходов."
    )
    @ApiResponse(responseCode = "200", description = "Лимит успешно создан.")
    @PostMapping
    public ResponseEntity<Limit> createLimit(
            @RequestParam ExpenseCategory category,
            @RequestParam BigDecimal amount,
            @RequestParam String currency
    ) {
        return ResponseEntity.ok(limitService.createLimit(category, amount, currency));
    }

    @Operation(
            summary = "Получить лимиты по категории",
            description = "Возвращает список лимитов для указанной категории расходов."
    )
    @ApiResponse(responseCode = "200", description = "Список лимитов успешно получен.")
    @GetMapping("/{category}")
    public ResponseEntity<List<Limit>> getLimits(@PathVariable ExpenseCategory category) {
        return ResponseEntity.ok(limitService.getLimitsByCategory(category));
    }

}

