package com.bank.transaction_service.service;

import com.bank.transaction_service.model.ExpenseCategory;
import com.bank.transaction_service.model.Limit;
import com.bank.transaction_service.repository.LimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LimitServiceTest {

    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private LimitService limitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLimit() {
        // Arrange
        ExpenseCategory category = ExpenseCategory.PRODUCT;
        BigDecimal amount = BigDecimal.valueOf(1000);
        String currency = "USD";

        Limit limit = new Limit();
        limit.setExpenseCategory(category);
        limit.setAmount(amount);
        limit.setCurrencyShortName(currency);
        limit.setSetDate(LocalDateTime.now());

        when(limitRepository.save(any(Limit.class))).thenReturn(limit);

        // Act
        Limit createdLimit = limitService.createLimit(category, amount, currency);

        // Assert
        assertNotNull(createdLimit);
        assertEquals(category, createdLimit.getExpenseCategory());
        assertEquals(amount, createdLimit.getAmount());
        assertEquals(currency, createdLimit.getCurrencyShortName());
        verify(limitRepository, times(1)).save(any(Limit.class));
    }

    @Test
    void testGetLimitsByCategory() {
        // Arrange
        ExpenseCategory category = ExpenseCategory.SERVICE;
        Limit limit = new Limit();
        limit.setExpenseCategory(category);
        limit.setAmount(BigDecimal.valueOf(500));
        limit.setCurrencyShortName("USD");
        limit.setSetDate(LocalDateTime.now());

        when(limitRepository.findByExpenseCategory(category)).thenReturn(List.of(limit));

        // Act
        List<Limit> limits = limitService.getLimitsByCategory(category);

        // Assert
        assertNotNull(limits);
        assertEquals(1, limits.size());
        assertEquals(category, limits.get(0).getExpenseCategory());
        verify(limitRepository, times(1)).findByExpenseCategory(category);
    }
}