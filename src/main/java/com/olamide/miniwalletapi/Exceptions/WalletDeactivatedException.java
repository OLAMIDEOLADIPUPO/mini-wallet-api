package com.olamide.miniwalletapi.Exceptions;

public class WalletDeactivatedException extends RuntimeException {
    public WalletDeactivatedException(String message) {
        super(message);
    }
}
