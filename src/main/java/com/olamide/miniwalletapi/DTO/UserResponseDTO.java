package com.olamide.miniwalletapi.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record UserResponseDTO(
        String ownerName,
        UUID walletNumber,
        BigDecimal balance,
        boolean isActive

) {
}
