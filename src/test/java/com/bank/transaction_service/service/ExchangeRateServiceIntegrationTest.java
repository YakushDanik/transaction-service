package com.bank.transaction_service.service;

import com.bank.transaction_service.BaseIntegrationTest;
import com.bank.transaction_service.model.ExchangeRate;
import com.bank.transaction_service.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

//Может отработать не правильно из-за смены курса.
class ExchangeRateServiceIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        exchangeRateRepository.deleteAll();
    }
    @Test
    void testFetchAndSaveExchangeRates() {
        BigDecimal expectedRate = BigDecimal.valueOf(516.0848);

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/v6/c16b390176fb7c060c20e5d8/latest/USD"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("{\"result\":\"success\",\"conversion_rates\":{\"KZT\":516.0848}}"));

        assertDoesNotThrow(() -> exchangeRateService.fetchAndSaveExchangeRates("USD"));

        Optional<ExchangeRate> savedRate = exchangeRateRepository.findByCurrencyFromAndCurrencyToAndDate("USD", "KZT", LocalDate.now());
        assertTrue(savedRate.isPresent(), "Exchange rate for USD -> KZT should be present in the database");
        assertEquals(0, savedRate.get().getRate().compareTo(expectedRate),
                "The saved exchange rate does not match the expected value");
    }

}
