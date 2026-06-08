package com.olamide.miniwalletapi.Service.ServiceImpl;

import com.olamide.miniwalletapi.Configuration.SecurityUtils;
import com.olamide.miniwalletapi.DTO.UserResponseDTO;

import com.olamide.miniwalletapi.Exceptions.WalletDeactivatedException;
import com.olamide.miniwalletapi.Exceptions.WalletNotFoundException;
import com.olamide.miniwalletapi.Models.User;
import com.olamide.miniwalletapi.Models.Wallet;
import com.olamide.miniwalletapi.Repository.WalletRepository;
import com.olamide.miniwalletapi.Service.WalletService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    public WalletServiceImpl (WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    private UserResponseDTO mapToDTO(Wallet wallet) {
        return new UserResponseDTO(
                wallet.getUser().getEmail(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                wallet.isActive()
        );
    }

    public List<UserResponseDTO> getWallets() {
       return walletRepository.findAll().stream()
               .map(this::mapToDTO)
       .toList();

    }
    @Override
    public UserResponseDTO getMyWallet() {
        User currentUser = SecurityUtils.getAuthenticatedUser();
        Wallet wallet = walletRepository.findByUser(currentUser)
                .orElseThrow(() -> new WalletNotFoundException(
                        "No wallet found for current user"));
        return mapToDTO(wallet);
    }

    public UserResponseDTO findWalletById(Long id){
        return walletRepository.findById(id).map(this::mapToDTO)

        .orElseThrow(() -> new WalletNotFoundException("Wallet with id " +id +" not found"));
    }

    public UserResponseDTO findByWalletNumber(UUID walletNumber){
        return walletRepository.findByWalletNumber(walletNumber).map(this::mapToDTO)

                .orElseThrow(() -> new WalletNotFoundException("Wallet with wallet number " +walletNumber +"not found"));
    }

    public UserResponseDTO deleteWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet with id " + id + " not found"));

        if (!wallet.isActive()) {
            throw new WalletDeactivatedException("Wallet with id " + id + " is already deactivated.");
        }

        wallet.setActive(false);
        return mapToDTO( walletRepository.save(wallet));

    }
}
