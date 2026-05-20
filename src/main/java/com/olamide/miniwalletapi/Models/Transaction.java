package com.olamide.miniwalletapi.Models;

import com.olamide.miniwalletapi.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = true)
    private Long sourceWalletId;
    @Column(nullable = true)
    private Long destinationWalletId;
    private Instant timestamp;

    public Transaction() {}
    public Transaction(TransactionType type, BigDecimal amount, Long sourceWalletId, Long destinationWalletId) {
        this.type = type;
        this.amount = amount;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.timestamp = Instant.now();

    }

    public Long getId() {
        return id;
    }


    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getSourceWalletId() {
        return sourceWalletId;
    }

    public Long getDestinationWalletId() {
        return destinationWalletId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }


}

