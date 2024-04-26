package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WalletServiceTest {

    @Mock
    private UsersService usersService;

    @Mock
    private WalletRepository walletRepository;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        walletService = new WalletService(usersService, walletRepository);
    }

    @Test
    void testDebit_UserNotFound() {

        String userEmail = "test@example.com";
        WalletDto walletDto = new WalletDto(userEmail, BigDecimal.TEN);
        when(usersService.findByEmail(userEmail)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> walletService.debit(walletDto));
    }

    @Test
    void testDebit_WalletNotFound() {
        String userEmail = "test@example.com";
        WalletDto walletDto = new WalletDto(userEmail, BigDecimal.TEN);
        Users user = Users.builder().email(userEmail).build();
        when(usersService.findByEmail(userEmail)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> walletService.debit(walletDto));
    }

    @Test
    void testDebit_Success() {
        String userEmail = "test@example.com";
        WalletDto walletDto = new WalletDto(userEmail, BigDecimal.TEN);
        Users user = Users.builder().email(userEmail).build();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));

        when(usersService.findByEmail(userEmail)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));

        walletService.debit(walletDto);

        verify(usersService).findByEmail(userEmail);
        verify(walletRepository).findByUsers(user);
        verify(walletRepository).save(wallet);
        assertEquals(BigDecimal.valueOf(90), wallet.getBalance());
    }

    @Test
    void testCredit_Success() {
        String userEmail = "test@example.com";
        WalletDto walletDto = new WalletDto(userEmail, BigDecimal.TEN);
        Users user = Users.builder().email(userEmail).build();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));

        when(usersService.findByEmail(userEmail)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));

        walletService.credit(walletDto);

        verify(usersService).findByEmail(userEmail);
        verify(walletRepository).findByUsers(user);
        verify(walletRepository).save(wallet);
        assertEquals(BigDecimal.valueOf(110), wallet.getBalance());
    }

    @Test
    void testCredit_WalletNotFound() {
        String userEmail = "test@example.com";
        WalletDto walletDto = new WalletDto(userEmail, BigDecimal.TEN);
        Users user = Users.builder().email(userEmail).build();
        when(usersService.findByEmail(userEmail)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> walletService.credit(walletDto));
    }

    @Test
    void testCredit_UserNotFound() {

        String userEmail = "test@example.com";
        WalletDto walletDto = new WalletDto(userEmail, BigDecimal.TEN);
        when(usersService.findByEmail(userEmail)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            walletService.credit(walletDto);
        });
    }

    @Test
    void testGetBalance_UserNotFound() {
        String userEmail = "test@example.com";
        when(usersService.findByEmail(userEmail)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> walletService.getBalance(userEmail));
    }

    @Test
    void testGetBalance_WalletNotFound() {
        String userEmail = "test@example.com";
        Users user = Users.builder().email(userEmail).build();
        when(usersService.findByEmail(userEmail)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.empty());

        Wallet result = walletService.getBalance(userEmail);

        assertNull(result);
    }

    @Test
    void testGetBalance_Success() {
        String userEmail = "test@example.com";
        Users user = Users.builder().email(userEmail).build();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));

        when(usersService.findByEmail(userEmail)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getBalance(userEmail);

        assertNotNull(result);
        assertEquals(wallet.getBalance(), result.getBalance());
    }

    @Test
    void testCalculatePointsForCurrentDay() {
        LocalDate currentDate = LocalDate.now();
        assertEquals(25, walletService.calculatePointsForCurrentDay(currentDate.with(DayOfWeek.SUNDAY)).longValue());
        assertEquals(7, walletService.calculatePointsForCurrentDay(currentDate.with(DayOfWeek.MONDAY)).longValue());
        assertEquals(6, walletService.calculatePointsForCurrentDay(currentDate.with(DayOfWeek.TUESDAY)).longValue());
        assertEquals(2, walletService.calculatePointsForCurrentDay(currentDate.with(DayOfWeek.WEDNESDAY)).longValue());
        assertEquals(20, walletService.calculatePointsForCurrentDay(currentDate.with(DayOfWeek.THURSDAY)).longValue());
        assertEquals(15, walletService.calculatePointsForCurrentDay(currentDate.with(DayOfWeek.FRIDAY)).longValue());
        assertEquals(20, walletService.calculatePointsForCurrentDay(currentDate.with(DayOfWeek.SATURDAY)).longValue());
    }
}

