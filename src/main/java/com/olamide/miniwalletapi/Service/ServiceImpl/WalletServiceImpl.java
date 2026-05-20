package com.olamide.miniwalletapi.Service.ServiceImpl;

import com.olamide.miniwalletapi.DTO.UserRequestDTO;
import com.olamide.miniwalletapi.DTO.UserResponseDTO;

import com.olamide.miniwalletapi.Exceptions.InvalidWalletDetailsException;
import com.olamide.miniwalletapi.Exceptions.WalletDeactivatedException;
import com.olamide.miniwalletapi.Exceptions.WalletNotFoundException;
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

    public UserResponseDTO createWallet(UserRequestDTO request) throws InvalidWalletDetailsException {
        Wallet wallet = new Wallet();
        wallet.setOwnerName(request.ownerName());
        Wallet saved =  walletRepository.save(wallet);

        return new  UserResponseDTO(
                saved.getOwnerName(),
                saved.getWalletNumber(),
                saved.getBalance(),
                saved.isActive()
        );


    }
    public List<UserResponseDTO> getWallets() {
       return walletRepository.findAll().stream().map(
               wallet -> new UserResponseDTO(
                       wallet.getOwnerName(),
                       wallet.getWalletNumber(),
                       wallet.getBalance(),
                       wallet.isActive()
               )
       ).toList();

    }

    public UserResponseDTO findWalletById(Long id){
        return walletRepository.findById(id).map(
                wallet -> new UserResponseDTO(
                        wallet.getOwnerName(),
                        wallet.getWalletNumber(),
                        wallet.getBalance(),
                        wallet.isActive()
                )

        ).orElseThrow(() -> new WalletNotFoundException("Wallet with id " +id +" not found"));
    }

    public UserResponseDTO findByWalletNumber(UUID walletNumber){
        return walletRepository.findByWalletNumber(walletNumber).map(
                        wallet -> new UserResponseDTO(
                                wallet.getOwnerName(),
                                wallet.getWalletNumber(),
                                wallet.getBalance(),
                                wallet.isActive()
                        )

                ).orElseThrow(() -> new WalletNotFoundException("Wallet with wallet number " +walletNumber +"not found"));
    }

    public UserResponseDTO deleteWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet with id " + id + " not found"));

        if (!wallet.isActive()) {
            throw new WalletDeactivatedException("Wallet with id " + id + " is already deactivated.");
        }

        wallet.setActive(false);
        Wallet saved = walletRepository.save(wallet);

        return new UserResponseDTO(
                saved.getOwnerName(),
                saved.getWalletNumber(),
                saved.getBalance(),
                saved.isActive()
        );
    }
}
