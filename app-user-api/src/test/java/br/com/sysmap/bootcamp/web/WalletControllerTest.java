package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WalletControllerTest {

    @Mock
    private WalletService walletService;

    private WalletController walletController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        walletController = new WalletController(walletService);
    }

    private static class WalletDtoMatcher implements ArgumentMatcher<WalletDto> {
        private final WalletDto expected;

        public WalletDtoMatcher(WalletDto expected) {
            this.expected = expected;
        }
        @Override
        public boolean matches(WalletDto actual) {
            return expected.getEmail().equals(actual.getEmail()) && expected.getValue().compareTo(actual.getValue()) == 0;
        }
    }

    @Test
    public void testCreditWallet() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        BigDecimal value = BigDecimal.TEN;
        WalletDto walletDto = new WalletDto("test@example.com", value);

        Wallet wallet = new Wallet();
        when(walletService.credit(argThat(new WalletDtoMatcher(walletDto)))).thenReturn(wallet);

        ResponseEntity<Wallet> response = walletController.creditWallet(value);

        verify(walletService).credit(argThat(new WalletDtoMatcher(walletDto)));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(wallet, response.getBody());
    }
    @Test
    public void testGetBalance() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Wallet wallet = new Wallet();
        when(walletService.getBalance("test@example.com")).thenReturn(wallet);

        ResponseEntity<Wallet> response = walletController.getBalance();

        verify(walletService).getBalance("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(wallet, response.getBody());
    }
}

