package com.olamide.miniwalletapi.Repository;

import com.olamide.miniwalletapi.Models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByWalletNumber(UUID walletNumber);

}
