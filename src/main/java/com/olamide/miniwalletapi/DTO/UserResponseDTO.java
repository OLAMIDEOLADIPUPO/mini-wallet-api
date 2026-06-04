package com.olamide.miniwalletapi.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record UserResponseDTO(
        String ownerEmail,
        UUID walletNumber,
        BigDecimal balance,
        boolean isActive

) {
}
