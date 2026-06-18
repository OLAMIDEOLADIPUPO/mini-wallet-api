package com.olamide.miniwalletapi.Service;

import com.olamide.miniwalletapi.DTO.*;
import org.springframework.data.domain.Pageable;

public interface TransactionService {


    TransactionResponseDTO deposit(DepositRequestDTO request,String idempotencyKey);

    TransactionResponseDTO withdraw(WithdrawRequestDTO request,String idempotencyKey);

    TransactionResponseDTO transfer(TransferRequestDTO transferRequest,String idempotencyKey);

    TransactionResponseDTO findById(Long id);

    PagedResponseDTO<TransactionResponseDTO> getTransactionHistory(Long walletId, Pageable pageable);

    PagedResponseDTO<TransactionResponseDTO> getMyTransactionHistory(Pageable pageable);
}