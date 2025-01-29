package com.bank.transaction_service.controller;

import com.bank.transaction_service.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange-rates")
@Tag(name = "Exchange Rate API", description = "API для работы с курсами валют")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Operation(
            summary = "Обновить курсы валют",
            description = "Получает и сохраняет актуальные курсы валют для KZT и RUB."
    )
    @ApiResponse(responseCode = "200", description = "Курсы валют успешно обновлены.")
    @PostMapping("/fetch")
    public void fetchRates() {
        exchangeRateService.fetchAndSaveExchangeRates("KZT");
        exchangeRateService.fetchAndSaveExchangeRates("RUB");

    }
}
