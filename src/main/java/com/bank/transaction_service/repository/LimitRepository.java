package com.bank.transaction_service.repository;

import com.bank.transaction_service.model.Limit;
import com.bank.transaction_service.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    List<Limit> findByExpenseCategory(ExpenseCategory category);
}
