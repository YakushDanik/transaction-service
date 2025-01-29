package com.bank.transaction_service.service;

import com.bank.transaction_service.client.ExchangeRateClient;
import com.bank.transaction_service.model.ExchangeRate;
import com.bank.transaction_service.repository.ExchangeRateRepository;
import com.bank.transaction_service.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    void testFetchAndSaveExchangeRates() {
        Map<String, Object> mockResponse = Map.of(
                "result", "success",
                "conversion_rates", Map.of("USD", 400.5)
        );
        when(exchangeRateClient.getExchangeRates("KZT")).thenReturn(mockResponse);

        exchangeRateService.fetchAndSaveExchangeRates("KZT");

        verify(exchangeRateRepository).save(any(ExchangeRate.class));
    }

    @Test
    void testFallbackToLatestRate() {
        when(exchangeRateClient.getExchangeRates("RUB"))
                .thenThrow(new RuntimeException("API not available"));

        ExchangeRate latestRate = new ExchangeRate();
        latestRate.setCurrencyFrom("RUB");
        latestRate.setCurrencyTo("USD");
        latestRate.setRate(BigDecimal.valueOf(75.0));
        latestRate.setDate(LocalDate.now().minusDays(1));

        when(exchangeRateRepository.findFirstByCurrencyFromAndCurrencyToOrderByDateDesc("RUB", "USD"))
                .thenReturn(Optional.of(latestRate));

        exchangeRateService.fetchAndSaveExchangeRates("RUB");

        verify(exchangeRateRepository).save(any(ExchangeRate.class));
    }
}
