package com.olamide.miniwalletapi.Repository;

import com.olamide.miniwalletapi.Models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        @Query("SELECT t from Transaction t WHERE t.sourceWalletId = :walletId OR t.destinationWalletId = :walletId" )
        List<Transaction> findAllByWalletId(@Param("walletId")Long walletId);
}
