package com.olamide.miniwalletapi.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequestDTO( @NotNull(message = "WalletId cannot be null") Long sourceWalletId,
                                  @NotNull Long destinationWalletId,
                                  @Positive(message = "Amount must be greater than zero")
         BigDecimal amount) {


}
