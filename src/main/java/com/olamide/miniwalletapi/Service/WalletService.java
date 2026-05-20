package com.olamide.miniwalletapi.Service;

import com.olamide.miniwalletapi.DTO.UserRequestDTO;
import com.olamide.miniwalletapi.DTO.UserResponseDTO;
import com.olamide.miniwalletapi.Exceptions.InvalidWalletDetailsException;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    UserResponseDTO createWallet(UserRequestDTO request) throws InvalidWalletDetailsException;

    List<UserResponseDTO> getWallets();

    UserResponseDTO findWalletById(Long id);

    UserResponseDTO findByWalletNumber(UUID walletNumber);

    UserResponseDTO deleteWallet(Long id);
}