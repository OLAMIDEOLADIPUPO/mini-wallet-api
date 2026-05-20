package com.olamide.miniwalletapi.Exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        String error,
        String message,
        LocalDateTime timestamp
) {}
