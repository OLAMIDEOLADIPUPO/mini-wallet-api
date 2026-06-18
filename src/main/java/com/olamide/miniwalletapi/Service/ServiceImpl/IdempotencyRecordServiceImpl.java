package com.olamide.miniwalletapi.Service.ServiceImpl;

import com.olamide.miniwalletapi.DTO.TransactionResponseDTO;
import com.olamide.miniwalletapi.Models.IdempotencyRecord;
import com.olamide.miniwalletapi.Repository.IdempotencyRecordRepository;
import com.olamide.miniwalletapi.Service.IdempotencyRecordService;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
@Service
public class IdempotencyRecordServiceImpl implements IdempotencyRecordService {
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ObjectMapper objectMapper;

    public IdempotencyRecordServiceImpl(IdempotencyRecordRepository idempotencyRecordRepository, ObjectMapper objectMapper) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.objectMapper = objectMapper;
    }
    @Override
    public Optional<TransactionResponseDTO> findExistingResponse(String idempotencyKey) {
        return idempotencyRecordRepository.findById(idempotencyKey).map(
                record ->{
                    try{
                        return objectMapper.readValue(record.getResponseBody(), TransactionResponseDTO.class);
                    }
                    catch(Exception e){
                        throw new RuntimeException("Failed to read response from idempotency record ",e);
                    }
                }
        );
    }

    @Override
    public void saveResponse(String key, TransactionResponseDTO dto, Long transactionId) {
        try{
            String json = objectMapper.writeValueAsString(dto);
            idempotencyRecordRepository.save(new IdempotencyRecord(key, json, transactionId));
        }
        catch(Exception e){
            throw new RuntimeException("Failed to save response to idempotency record ",e);
        }

    }
}
