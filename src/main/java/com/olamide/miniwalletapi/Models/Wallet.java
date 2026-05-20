package com.olamide.miniwalletapi.Models;


import com.olamide.miniwalletapi.Exceptions.InvalidAmountException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ownerName;

    @Column(unique = true,  nullable = false)
    private UUID walletNumber = UUID.randomUUID();
    private BigDecimal balance = BigDecimal.ZERO;
    private Instant createdAt = Instant.now();
    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
    public UUID getWalletNumber() {
        return walletNumber;
    }
    public void setWalletNumber(UUID walletNumber) {
        this.walletNumber = walletNumber;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void debit(BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO)>0 && amount.compareTo(this.balance)<=0) {
            this.balance =  this.balance.subtract(amount);
        } else {
           throw  new InvalidAmountException("Amount greater than balance");
        }

    }
    public void credit(BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO)>0 ){
            this.balance =  this.balance.add(amount);
        }
        else throw  new InvalidAmountException("Invalid amount");

    }



}
