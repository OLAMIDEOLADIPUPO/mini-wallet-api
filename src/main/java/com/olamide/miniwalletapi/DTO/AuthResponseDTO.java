package com.olamide.miniwalletapi.DTO;

import java.util.UUID;

public record AuthResponseDTO(
        String message,
        String email,
        UUID walletNumber,
        String token
) {
}
