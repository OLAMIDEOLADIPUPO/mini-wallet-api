package com.olamide.miniwalletapi.Controller;

import com.olamide.miniwalletapi.DTO.*;
import com.olamide.miniwalletapi.Service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
            @Valid @RequestBody DepositRequestDTO request,@RequestHeader("Idempotency-Key")String idempotencyKey) {
        return ResponseEntity.ok(transactionService.deposit(request,idempotencyKey));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @Valid @RequestBody WithdrawRequestDTO request,@RequestHeader("Idempotency-Key")String idempotencyKey) {
        return ResponseEntity.ok(transactionService.withdraw(request,idempotencyKey));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO transferRequestDTO,@RequestHeader("Idempotency-Key")String idempotencyKey) {
        return ResponseEntity.ok(transactionService.transfer(transferRequestDTO,idempotencyKey));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PagedResponseDTO<TransactionResponseDTO>> getMyTransactionHistory(@PageableDefault(sort = "timestamp") Pageable pageable) {
        return ResponseEntity.ok(transactionService.getMyTransactionHistory(pageable));
    }

    @GetMapping("/{walletId}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponseDTO<TransactionResponseDTO>> getTransactionHistory(@PathVariable Long walletId, @PageableDefault(sort = "timestamp") Pageable pageable) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(walletId,pageable));
    }

    @GetMapping("details/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponseDTO> getTransactionbyId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.findById(transactionId));

    }
}