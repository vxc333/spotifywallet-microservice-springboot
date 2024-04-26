package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet", description = "Wallet API")
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Credit value in wallet")
    @PostMapping("/credit/{value}")
    public ResponseEntity<Wallet> creditWallet( @PathVariable BigDecimal value){
        String userEmail = getCurrentUserEmail();

        try {
            WalletDto walletDto = new WalletDto(userEmail,value);
            Wallet wallet = walletService.credit(walletDto);
            return ResponseEntity.ok(wallet);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "My Wallet")
    @GetMapping("")
    public ResponseEntity<Wallet> getBalance() {
        String email = getCurrentUserEmail();
        Wallet userWallet = walletService.getBalance(email);

        if(userWallet != null){
            return ResponseEntity.ok(userWallet);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
