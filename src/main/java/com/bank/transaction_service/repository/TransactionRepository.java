package com.bank.transaction_service.repository;

import com.bank.transaction_service.model.ExpenseCategory;
import com.bank.transaction_service.model.Transaction;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t " +
            "JOIN Limit l ON t.expenseCategory = l.expenseCategory " +
            "WHERE t.dateTime >= l.setDate " +
            "AND (t.amount / (SELECT e.rate FROM ExchangeRate e " +
            "                 WHERE e.currencyFrom = t.currencyShortName " +
            "                 AND e.currencyTo = 'USD' " +
            "                 ORDER BY e.date DESC LIMIT 1)) > l.amount")
    List<Transaction> findTransactionsExceedingLimits();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.expenseCategory = :category AND t.dateTime > :setDate")
    BigDecimal calculateTotalSpentAfterLimit(
            @Param("category") ExpenseCategory category,
            @Param("setDate") LocalDateTime setDate
    );

}
