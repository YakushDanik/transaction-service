package com.bank.transaction_service.service;

import com.bank.transaction_service.model.Limit;
import com.bank.transaction_service.model.Transaction;
import com.bank.transaction_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LimitCheckService {
    private final LimitService limitService;
    private final ExchangeRateService exchangeRateService;
    private final TransactionRepository transactionRepository;

    public LimitCheckService(LimitService limitService, ExchangeRateService exchangeRateService, TransactionRepository transactionRepository) {
        this.limitService = limitService;
        this.exchangeRateService = exchangeRateService;
        this.transactionRepository = transactionRepository;
    }

    public void checkAndMarkTransaction(Transaction transaction) {
        List<Limit> limits = limitService.getLimitsByCategory(transaction.getExpenseCategory());

        if (limits.isEmpty()) {
            return;
        }

        for (Limit limit : limits) {
            if (transaction.getDateTime().isAfter(limit.getSetDate())) {
                BigDecimal transactionAmountInUSD = convertToUSD(transaction.getAmount(), transaction.getCurrencyShortName());

                if (transactionAmountInUSD.compareTo(limit.getAmount()) > 0) {
                    transaction.setLimitExceeded(true);
                    transactionRepository.save(transaction);
                    return;
                }
            }
        }
    }

    private BigDecimal convertToUSD(BigDecimal amount, String currency) {
        return exchangeRateService.getLatestExchangeRate(currency, "USD")
                .map(exchangeRate -> amount.divide(exchangeRate.getRate(), 2, BigDecimal.ROUND_HALF_UP))
                .orElse(amount);
    }
}