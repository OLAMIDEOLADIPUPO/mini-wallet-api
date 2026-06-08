package com.olamide.miniwalletapi.Service;

import com.olamide.miniwalletapi.DTO.UserResponseDTO;
import com.olamide.miniwalletapi.Exceptions.InvalidWalletDetailsException;

import java.util.List;
import java.util.UUID;

public interface WalletService {


    List<UserResponseDTO> getWallets();

    UserResponseDTO getMyWallet();

    UserResponseDTO findWalletById(Long id);

    UserResponseDTO findByWalletNumber(UUID walletNumber);

    UserResponseDTO deleteWallet(Long id);

}