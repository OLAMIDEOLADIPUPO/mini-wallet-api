package com.olamide.miniwalletapi.Service;

import com.olamide.miniwalletapi.DTO.DepositRequestDTO;
import com.olamide.miniwalletapi.DTO.TransactionResponseDTO;
import com.olamide.miniwalletapi.DTO.TransferRequestDTO;
import com.olamide.miniwalletapi.DTO.WithdrawRequestDTO;
import com.olamide.miniwalletapi.Models.Transaction;

import java.util.List;

public interface TransactionService {

    TransactionResponseDTO save(Transaction transaction);

    TransactionResponseDTO deposit(DepositRequestDTO request);

    TransactionResponseDTO withdraw(WithdrawRequestDTO request);

    TransactionResponseDTO transfer(TransferRequestDTO transferRequest);

    TransactionResponseDTO findById(Long id);

    List<TransactionResponseDTO> getTransactionHistory(Long walletId);
}