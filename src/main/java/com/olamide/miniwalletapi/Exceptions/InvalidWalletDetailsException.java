package com.olamide.miniwalletapi.Exceptions;

public class InvalidWalletDetailsException extends RuntimeException {
    public InvalidWalletDetailsException(String message) {
        super(message);
    }
}
