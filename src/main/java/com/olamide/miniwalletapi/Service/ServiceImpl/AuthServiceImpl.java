package com.olamide.miniwalletapi.Service.ServiceImpl;

import com.olamide.miniwalletapi.DTO.AuthResponseDTO;
import com.olamide.miniwalletapi.DTO.RegisterUserDTO;
import com.olamide.miniwalletapi.Exceptions.InvalidWalletDetailsException;
import com.olamide.miniwalletapi.Models.Role;
import com.olamide.miniwalletapi.Models.User;
import com.olamide.miniwalletapi.Models.Wallet;
import com.olamide.miniwalletapi.Repository.UserRepository;
import com.olamide.miniwalletapi.Repository.WalletRepository;
import com.olamide.miniwalletapi.Service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, WalletRepository walletRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public AuthResponseDTO register(RegisterUserDTO request) {

        if(userRepository.findByEmail(request.email()).isPresent()) {
            throw new InvalidWalletDetailsException("Email already exists");
        }
        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                hashedPassword,
                Role.USER
        );
        User savedUser = userRepository.save(user);
        Wallet newWallet =new Wallet(savedUser);
        walletRepository.save(newWallet);
        return new AuthResponseDTO(
                "Registration successful! Wallet Created",
                savedUser.getEmail(),
                newWallet.getWalletNumber()

        );

    }
}
