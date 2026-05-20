package com.olamide.miniwalletapi.DTO;

import com.olamide.miniwalletapi.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDTO (
        Long sourceWalletId,
        Long destinationWalletId, BigDecimal amount,
        TransactionType transactionType,
        Instant timestamp){

}
