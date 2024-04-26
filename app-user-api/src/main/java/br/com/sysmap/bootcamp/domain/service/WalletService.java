package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final UsersService usersService;
    private final WalletRepository walletRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void debit(WalletDto walletDto) {
        Users users = usersService.findByEmail(walletDto.getEmail());
        if(users == null) {
            throw new UsernameNotFoundException("User not found for this email" +walletDto.getEmail());
        }
        Wallet wallet = walletRepository.findByUsers(users).orElseThrow(()->new RuntimeException("Wallet not found"));

        BigDecimal newValue = wallet.getBalance().subtract(walletDto.getValue());
        wallet.setBalance(newValue);

        Long pointsToAdd = calculatePointsForCurrentDay(LocalDate.now());
        Long currentPoints = wallet.getPoints() != null ? calculatePointsForCurrentDay(LocalDate.now()) : 0L;
        wallet.setPoints(currentPoints + pointsToAdd);

        wallet.setLastUpdate(LocalDateTime.now());

        walletRepository.save(wallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet credit(WalletDto walletDto) {
        Users users = usersService.findByEmail(walletDto.getEmail());
        if(users == null) {
            throw new UsernameNotFoundException("User not found for this email " +walletDto.getEmail());
        }

        Wallet wallet = walletRepository.findByUsers(users).orElseThrow(()->new RuntimeException("Wallet not found"));

        BigDecimal newValue = wallet.getBalance().add(walletDto.getValue());
        wallet.setBalance(newValue);

        wallet.setLastUpdate(LocalDateTime.now());

        return walletRepository.save(wallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet getBalance(String email){
        Users users = usersService.findByEmail(email);
        if(users != null) {
            return walletRepository.findByUsers(users).orElse(null);
        }else {
            throw new UsernameNotFoundException("User not found for this email" +email);
        }
    }

    public Long calculatePointsForCurrentDay(LocalDate dateOfPurchase) {
//        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        DayOfWeek dayOfWeek = dateOfPurchase.getDayOfWeek();
        return switch (dayOfWeek) {
            case SUNDAY -> 25L;
            case MONDAY -> 7L;
            case TUESDAY -> 6L;
            case WEDNESDAY -> 2L;
            case THURSDAY, SATURDAY -> 20L;
            case FRIDAY -> 15L;
        };
    }
}