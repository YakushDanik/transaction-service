package com.bank.transaction_service.dto;

import com.bank.transaction_service.model.Limit;
import com.bank.transaction_service.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionWithLimitDTO {
    private Transaction transaction;
    private Limit limit;
}
