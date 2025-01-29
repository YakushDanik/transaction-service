package com.bank.transaction_service.repository;

import com.bank.transaction_service.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByCurrencyFromAndCurrencyToAndDate(String currencyFrom, String currencyTo, LocalDate date);
    Optional<ExchangeRate> findFirstByCurrencyFromAndCurrencyToOrderByDateDesc(String currencyFrom, String currencyTo);
}
