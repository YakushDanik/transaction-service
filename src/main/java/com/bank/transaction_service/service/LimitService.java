package com.bank.transaction_service.service;

import com.bank.transaction_service.model.Limit;
import com.bank.transaction_service.model.ExpenseCategory;
import com.bank.transaction_service.model.Transaction;
import com.bank.transaction_service.repository.LimitRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LimitService {
    private final LimitRepository limitRepository;

    public LimitService(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    public Limit createLimit(ExpenseCategory category, BigDecimal amount, String currency) {
        Limit limit = new Limit();
        limit.setExpenseCategory(category);
        limit.setAmount(amount);
        limit.setCurrencyShortName(currency);
        limit.setSetDate(LocalDateTime.now());
        return limitRepository.save(limit);
    }

    public List<Limit> getLimitsByCategory(ExpenseCategory category) {
        return limitRepository.findByExpenseCategory(category);
    }

}
