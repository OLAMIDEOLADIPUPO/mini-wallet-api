package com.olamide.miniwalletapi.Controller;

import com.olamide.miniwalletapi.Configuration.SecurityUtils;
import com.olamide.miniwalletapi.DTO.UserResponseDTO;
import com.olamide.miniwalletapi.Models.Role;
import com.olamide.miniwalletapi.Models.User;
import com.olamide.miniwalletapi.Service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Wallet", description = "Wallet management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {
    private final WalletService walletService;
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    // ADMIN only — get all wallets
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all wallets", description = "Admin only — returns every wallet in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallets retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — Admin role required")
    })
    public ResponseEntity<List<UserResponseDTO>> getAllWallets() {
        return ResponseEntity.ok(walletService.getWallets());
    }



    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get my wallet", description = "Returns the authenticated user's own wallet details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — token missing or expired")
    })
    public ResponseEntity<UserResponseDTO> getMyWallet() {
        return ResponseEntity.ok(walletService.getMyWallet());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Find wallet by ID", description = "Admin only — retrieves a wallet by its internal database ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found"),
            @ApiResponse(responseCode = "403", description = "Access denied — not owner or Admin"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
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
    @Operation(summary = "Find wallet by wallet number", description = "Admin only — retrieves a wallet by its UUID wallet number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<UserResponseDTO>findWalletByWalletNumber( @PathVariable UUID walletNumber){
        UserResponseDTO response =  walletService.findByWalletNumber(walletNumber);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate wallet", description = "Admin only — soft deletes a wallet by setting isActive to false. Transaction history is preserved.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<UserResponseDTO> deleteWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.deleteWallet(id));
    }
}
