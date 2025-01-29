package com.bank.transaction_service.service;

import com.bank.transaction_service.client.ExchangeRateClient;
import com.bank.transaction_service.model.ExchangeRate;
import com.bank.transaction_service.repository.ExchangeRateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;


@Service
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateClient exchangeRateClient;


    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, ExchangeRateClient exchangeRateClient) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.exchangeRateClient = exchangeRateClient;
    }

    public ExchangeRate saveExchangeRate(String baseCurrency, String targetCurrency, BigDecimal rate, LocalDate date) {
        Optional<ExchangeRate> existingRate = exchangeRateRepository.findByCurrencyFromAndCurrencyToAndDate(baseCurrency, targetCurrency, date);

        if (existingRate.isEmpty()) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurrencyFrom(baseCurrency);
            exchangeRate.setCurrencyTo(targetCurrency);
            exchangeRate.setRate(rate);
            exchangeRate.setDate(date);

             return exchangeRateRepository.save(exchangeRate);
        }
        throw new RuntimeException("This rate is already exists!");
    }

    public Optional<ExchangeRate> getExchangeRate(String currencyFrom, String currencyTo, LocalDate date) {
        return exchangeRateRepository.findByCurrencyFromAndCurrencyToAndDate(currencyFrom, currencyTo, date);
    }

    public Optional<ExchangeRate> getLatestExchangeRate(String currencyFrom, String currencyTo) {
        return exchangeRateRepository.findFirstByCurrencyFromAndCurrencyToOrderByDateDesc(currencyFrom, currencyTo);
    }

    public void fetchAndSaveExchangeRates(String baseCurrency) {
        try {
            Map<String, Object> response = exchangeRateClient.getExchangeRates(baseCurrency);

            if (!"success".equals(response.get("result"))) {
                throw new RuntimeException("Failed to fetch exchange rates");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> rates = (Map<String, Object>) response.get("conversion_rates");

            BigDecimal rate = extractRate(rates, "USD");
            saveExchangeRate(baseCurrency, "USD", rate, LocalDate.now());
        } catch (Exception e) {
            Optional<ExchangeRate> latestRate = getLatestExchangeRate(baseCurrency, "USD");
            latestRate.ifPresent(rate -> saveExchangeRate(baseCurrency, "USD", rate.getRate(), LocalDate.now()));
        }
    }

    private BigDecimal extractRate(Map<String, Object> rates, String targetCurrency) {
        Object value = rates.get(targetCurrency);
        if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        } else if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        } else {
            throw new IllegalArgumentException("Unexpected rate type for " + targetCurrency);
        }
    }
    @Scheduled(cron = "0 0 0 * * *")
    public void updateDailyExchangeRates() {
        fetchAndSaveExchangeRates("KZT");
        fetchAndSaveExchangeRates("RUB");
    }



}

