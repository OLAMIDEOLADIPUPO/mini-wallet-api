package com.olamide.miniwalletapi.Service;

import com.olamide.miniwalletapi.DTO.AuthResponseDTO;
import com.olamide.miniwalletapi.DTO.RegisterUserDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterUserDTO request);
}
