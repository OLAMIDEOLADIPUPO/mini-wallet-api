package com.olamide.miniwalletapi.Controller;

import com.olamide.miniwalletapi.Configuration.SecurityUtils;
import com.olamide.miniwalletapi.DTO.UserResponseDTO;
import com.olamide.miniwalletapi.Models.Role;
import com.olamide.miniwalletapi.Models.User;
import com.olamide.miniwalletapi.Service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("mini_wallet/api")
public class WalletController {
    private final WalletService walletService;
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    // ADMIN only — get all wallets
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllWallets() {
        return ResponseEntity.ok(walletService.getWallets());
    }



    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDTO> getMyWallet() {
        return ResponseEntity.ok(walletService.getMyWallet());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO>findWalletById( @PathVariable Long id){
        UserResponseDTO response =  walletService.findWalletById(id);
        User currentUser = SecurityUtils.getAuthenticatedUser();

        boolean isOwner = SecurityUtils.isOwner((response.ownerEmail()));
        boolean isAdmin = currentUser.getRole()== Role.ADMIN;

        if(!isOwner && !isAdmin){
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("number/{walletNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO>findWalletByWalletNumber( @PathVariable UUID walletNumber){
        UserResponseDTO response =  walletService.findByWalletNumber(walletNumber);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> deleteWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.deleteWallet(id));
    }
}
