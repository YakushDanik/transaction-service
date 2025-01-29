package com.bank.transaction_service.controller;

import com.bank.transaction_service.dto.TransactionWithLimitDTO;
import com.bank.transaction_service.model.Transaction;
import com.bank.transaction_service.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction API", description = "API для работы с транзакциями")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Добавить транзакцию", description = "Создает новую транзакцию")
    @ApiResponse(responseCode = "200", description = "Транзакция успешно создана")
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.saveTransaction(transaction));
    }

    @Operation(summary = "Получить транзакции с превышением лимита", description = "Возвращает список транзакций, превысивших лимит")
    @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен")
    @GetMapping("/limit-exceeded")
    public ResponseEntity<List<Transaction>> getTransactionsWithLimitExceeded() {
        return ResponseEntity.ok(transactionService.getTransactionsWithLimitExceeded());
    }

    @Operation(summary = "Получить транзакции с деталями лимита", description = "Возвращает список транзакций с информацией о лимите")
    @ApiResponse(responseCode = "200", description = "Список транзакций с лимитами успешно получен")
    @GetMapping("/limit-details")
    public ResponseEntity<List<TransactionWithLimitDTO>> getTransactionsWithLimitDetails() {
        return ResponseEntity.ok(transactionService.getTransactionsWithLimitDetails());
    }


}