package com.olamide.miniwalletapi.Repository;

import com.olamide.miniwalletapi.Models.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        @Query("SELECT t from Transaction t WHERE t.sourceWalletId = :walletId OR t.destinationWalletId = :walletId  ORDER BY t.timestamp DESC" )
        Slice<Transaction> findAllByWalletId(@Param("walletId")Long walletId, Pageable pageable);
}
