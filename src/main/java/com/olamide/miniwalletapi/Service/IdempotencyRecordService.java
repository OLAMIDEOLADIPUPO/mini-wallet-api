package com.olamide.miniwalletapi.Service;

import com.olamide.miniwalletapi.DTO.TransactionResponseDTO;
import com.olamide.miniwalletapi.Models.IdempotencyRecord;

import java.util.Optional;

public interface IdempotencyRecordService {
    Optional<TransactionResponseDTO> findExistingResponse(String idempotencyKey);
    void saveResponse(String idempotencyKey, TransactionResponseDTO dto,Long transactionId);
}
