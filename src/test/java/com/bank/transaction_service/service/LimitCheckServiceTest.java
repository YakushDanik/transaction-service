package com.bank.transaction_service.service;

import com.bank.transaction_service.model.ExchangeRate;
import com.bank.transaction_service.model.ExpenseCategory;
import com.bank.transaction_service.model.Limit;
import com.bank.transaction_service.model.Transaction;
import com.bank.transaction_service.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LimitCheckServiceTest {

    @Mock
    private LimitService limitService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private LimitCheckService limitCheckService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransactionBelowLimit() {
        Transaction transaction = createTransaction("KZT", new BigDecimal("100"));
        Limit limit = createLimit(new BigDecimal("150"));

        when(limitService.getLimitsByCategory(transaction.getExpenseCategory())).thenReturn(List.of(limit));
        when(exchangeRateService.getLatestExchangeRate("KZT", "USD"))
                .thenReturn(Optional.of(createExchangeRate("KZT", "USD", new BigDecimal("500"))));

        limitCheckService.checkAndMarkTransaction(transaction);

        assertFalse(transaction.isLimitExceeded());
        verify(transactionRepository, never()).save(transaction);
    }

    @Test
    void testTransactionExceedsLimit() {
        Transaction transaction = createTransaction("KZT", new BigDecimal("800"));
        Limit limit = createLimit(new BigDecimal("1.5"));

        when(limitService.getLimitsByCategory(transaction.getExpenseCategory())).thenReturn(List.of(limit));
        when(exchangeRateService.getLatestExchangeRate("KZT", "USD"))
                .thenReturn(Optional.of(createExchangeRate("KZT", "USD", new BigDecimal("500"))));

        limitCheckService.checkAndMarkTransaction(transaction);

        assertTrue(transaction.isLimitExceeded());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testNoLimitsConfigured() {
        Transaction transaction = createTransaction("KZT", new BigDecimal("100"));

        when(limitService.getLimitsByCategory(transaction.getExpenseCategory())).thenReturn(List.of());

        limitCheckService.checkAndMarkTransaction(transaction);

        assertFalse(transaction.isLimitExceeded());
        verify(transactionRepository, never()).save(transaction);
    }

    private Transaction createTransaction(String currency, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setCurrencyShortName(currency);
        transaction.setAmount(amount);
        transaction.setExpenseCategory(ExpenseCategory.PRODUCT);
        transaction.setDateTime(LocalDateTime.now());
        return transaction;
    }


    private Limit createLimit(BigDecimal amount) {
        Limit limit = new Limit();
        limit.setAmount(amount);
        limit.setCurrencyShortName("USD");
        limit.setExpenseCategory(ExpenseCategory.PRODUCT);
        limit.setSetDate(LocalDateTime.now().minusDays(1));
        return limit;
    }

    private ExchangeRate createExchangeRate(String from, String to, BigDecimal rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrencyFrom(from);
        exchangeRate.setCurrencyTo(to);
        exchangeRate.setRate(rate);
        return exchangeRate;
    }



}