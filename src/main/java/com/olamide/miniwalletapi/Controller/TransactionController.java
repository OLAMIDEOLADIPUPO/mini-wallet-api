package com.olamide.miniwalletapi.Controller;

import com.olamide.miniwalletapi.DTO.DepositRequestDTO;
import com.olamide.miniwalletapi.DTO.TransactionResponseDTO;
import com.olamide.miniwalletapi.DTO.TransferRequestDTO; // Added missing import
import com.olamide.miniwalletapi.DTO.WithdrawRequestDTO;
import com.olamide.miniwalletapi.Service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transactions") // Refactored to standard pluralized REST naming conventions
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody DepositRequestDTO depositRequestDTO) {
        // Returning a ResponseEntity explicitly shows the HTTP Status (201 Created or 200 OK)
        return ResponseEntity.ok(transactionService.deposit(depositRequestDTO));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody WithdrawRequestDTO withdrawRequestDTO) {
        return ResponseEntity.ok(transactionService.withdraw(withdrawRequestDTO));
    }

    @PostMapping("/transfer") // Exposing your transfer service method
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO transferRequestDTO) {
        return ResponseEntity.ok(transactionService.transfer(transferRequestDTO));
    }

    @GetMapping("/{walletId}/history") // Made more explicit: GET api/transactions/5/history
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable Long walletId) {
        List<TransactionResponseDTO> history = transactionService.getTransactionHistory(walletId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionbyId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.findById(transactionId));

    }
}