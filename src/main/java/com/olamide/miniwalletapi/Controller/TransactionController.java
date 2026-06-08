package com.olamide.miniwalletapi.Controller;

import com.olamide.miniwalletapi.DTO.DepositRequestDTO;
import com.olamide.miniwalletapi.DTO.TransactionResponseDTO;
import com.olamide.miniwalletapi.DTO.TransferRequestDTO;
import com.olamide.miniwalletapi.DTO.WithdrawRequestDTO;
import com.olamide.miniwalletapi.Service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @Valid @RequestBody DepositRequestDTO request) {
        return ResponseEntity.ok(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @Valid @RequestBody WithdrawRequestDTO request) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO transferRequestDTO) {
        return ResponseEntity.ok(transactionService.transfer(transferRequestDTO));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactionHistory() {
        List<TransactionResponseDTO> history = transactionService.getMyTransactionHistory();
        return ResponseEntity.ok(history);
    }
    @GetMapping("/{walletId}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable Long walletId) {
        List<TransactionResponseDTO> history = transactionService.getTransactionHistory(walletId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("details/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponseDTO> getTransactionbyId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.findById(transactionId));

    }
}