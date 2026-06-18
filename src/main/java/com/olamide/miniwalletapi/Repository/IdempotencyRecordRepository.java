package com.olamide.miniwalletapi.Repository;

import com.olamide.miniwalletapi.Models.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord,String> {
}
