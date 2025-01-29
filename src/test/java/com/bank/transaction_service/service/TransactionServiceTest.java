package com.bank.transaction_service.service;

import com.bank.transaction_service.model.*;
import com.bank.transaction_service.repository.LimitRepository;
import com.bank.transaction_service.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LimitRepository limitRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(transactionRepository, limitRepository, exchangeRateService);
    }

    @Test
    void testSaveTransactionWithDefaultLimit() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(1500));
        transaction.setCurrencyShortName("USD");
        transaction.setExpenseCategory(ExpenseCategory.SERVICE);
        transaction.setDateTime(LocalDateTime.of(2023, 10, 1, 12, 0));

        when(limitRepository.findByExpenseCategory(ExpenseCategory.SERVICE))
                .thenReturn(List.of());
        when(exchangeRateService.getLatestExchangeRate("USD", "USD"))
                .thenReturn(Optional.of(new ExchangeRate("USD", "USD", BigDecimal.ONE, LocalDateTime.now().toLocalDate())));
        when(transactionRepository.calculateTotalSpentAfterLimit(ExpenseCategory.SERVICE, transaction.getDateTime()))
                .thenReturn(BigDecimal.ZERO);
        when(limitRepository.save(any(Limit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.saveTransaction(transaction);

        assertNotNull(savedTransaction);
        assertTrue(savedTransaction.isLimitExceeded(), "Transaction should exceed the default limit");
        verify(limitRepository).save(any(Limit.class));
        verify(transactionRepository).save(transaction);
    }


    @Test
    void testSaveTransactionWithoutLimitExceeded() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(500));
        transaction.setCurrencyShortName("USD");
        transaction.setExpenseCategory(ExpenseCategory.SERVICE);
        transaction.setDateTime(LocalDateTime.now());

        Limit limit = new Limit();
        limit.setAmount(BigDecimal.valueOf(1000));
        limit.setCurrencyShortName("USD");
        limit.setExpenseCategory(ExpenseCategory.SERVICE);
        limit.setSetDate(LocalDateTime.now());

        when(limitRepository.findByExpenseCategory(ExpenseCategory.SERVICE))
                .thenReturn(List.of(limit));
        when(exchangeRateService.getLatestExchangeRate("USD", "USD"))
                .thenReturn(Optional.of(new ExchangeRate("USD", "USD", BigDecimal.ONE, LocalDateTime.now().toLocalDate())));
        when(transactionRepository.calculateTotalSpentAfterLimit(ExpenseCategory.SERVICE, transaction.getDateTime()))
                .thenReturn(BigDecimal.valueOf(300)); // Замокировать сумму уже потраченных средств
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.saveTransaction(transaction);

        assertNotNull(savedTransaction);
        assertFalse(savedTransaction.isLimitExceeded(), "Transaction should not exceed the limit");
    }



}
