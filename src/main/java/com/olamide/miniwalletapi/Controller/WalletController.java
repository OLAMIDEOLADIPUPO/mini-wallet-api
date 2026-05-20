package com.olamide.miniwalletapi.Controller;

import com.olamide.miniwalletapi.DTO.UserRequestDTO;
import com.olamide.miniwalletapi.DTO.UserResponseDTO;
import com.olamide.miniwalletapi.Service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("mini_wallet/api")
public class WalletController {
    private final WalletService walletService;
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    @PostMapping
    public ResponseEntity<UserResponseDTO> createWallet(@Valid @RequestBody UserRequestDTO request){
        UserResponseDTO response = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("{id}")
    public ResponseEntity<UserResponseDTO>findWalletById( @PathVariable Long id){
        UserResponseDTO response =  walletService.findWalletById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("number/{walletNumber}")
    public ResponseEntity<UserResponseDTO>findWalletByWalletNumber( @PathVariable UUID walletNumber){
        UserResponseDTO response =  walletService.findByWalletNumber(walletNumber);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.deleteWallet(id));
    }
}
