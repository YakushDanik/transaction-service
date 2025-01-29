package com.bank.transaction_service.service;

import com.bank.transaction_service.dto.TransactionWithLimitDTO;
import com.bank.transaction_service.model.*;
import com.bank.transaction_service.repository.LimitRepository;
import com.bank.transaction_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final LimitRepository limitRepository;
    private final ExchangeRateService exchangeRateService;

    public TransactionService(TransactionRepository transactionRepository,
                              LimitRepository limitRepository,
                              ExchangeRateService exchangeRateService) {
        this.transactionRepository = transactionRepository;
        this.limitRepository = limitRepository;
        this.exchangeRateService = exchangeRateService;
    }
    public Transaction saveTransaction(Transaction transaction) {
        Limit limit = limitRepository.findByExpenseCategory(transaction.getExpenseCategory())
                .stream()
                .findFirst()
                .orElseGet(() -> createDefaultLimit(transaction.getExpenseCategory()));

        BigDecimal exchangeRate = exchangeRateService.getLatestExchangeRate(transaction.getCurrencyShortName(), "USD")
                .map(ExchangeRate::getRate)
                .orElseThrow(() -> new RuntimeException("Exchange rate not available"));

        BigDecimal transactionInUSD = transaction.getAmount().divide(exchangeRate, 2, RoundingMode.HALF_UP);

        BigDecimal totalSpentInUSD = Optional.ofNullable(transactionRepository.calculateTotalSpentAfterLimit(
                        transaction.getExpenseCategory(),
                        limit.getSetDate()
                ))
                .orElse(BigDecimal.ZERO)
                .divide(exchangeRate, 2, RoundingMode.HALF_UP);

        boolean limitExceeded = totalSpentInUSD.add(transactionInUSD).compareTo(limit.getAmount()) > 0;
        transaction.setLimitExceeded(limitExceeded);

        return transactionRepository.save(transaction);
    }


    private Limit createDefaultLimit(ExpenseCategory category) {
        Limit defaultLimit = new Limit();
        defaultLimit.setExpenseCategory(category);
        defaultLimit.setAmount(BigDecimal.valueOf(1000));
        defaultLimit.setCurrencyShortName("USD");
        defaultLimit.setSetDate(LocalDateTime.now());
        return limitRepository.save(defaultLimit);
    }

    public List<Transaction> getTransactionsWithLimitExceeded() {
        return transactionRepository.findTransactionsExceedingLimits();
    }

    public List<TransactionWithLimitDTO> getTransactionsWithLimitDetails() {
        List<Transaction> transactions = transactionRepository.findTransactionsExceedingLimits();
        return transactions.stream()
                .map(transaction -> {
                    Optional<Limit> limit = limitRepository.findByExpenseCategory(transaction.getExpenseCategory())
                            .stream()
                            .findFirst();

                    return new TransactionWithLimitDTO(
                            transaction,
                            limit.orElse(null)
                    );
                })
                .toList();
    }
}
