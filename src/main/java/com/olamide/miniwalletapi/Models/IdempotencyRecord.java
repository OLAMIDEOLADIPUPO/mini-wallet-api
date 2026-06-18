package com.olamide.miniwalletapi.Models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class IdempotencyRecord {

    @Id
    private String idempotencyKey;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String responseBody;

    @Column(nullable = true)
    private Long transactionId;

    private Instant timestamp;

    protected IdempotencyRecord() {}
    public IdempotencyRecord(String idempotencyKey, String responseBody, Long transactionId) {
        this.idempotencyKey = idempotencyKey;
        this.responseBody = responseBody;
        this.transactionId = transactionId;
        this.timestamp = Instant.now();
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
