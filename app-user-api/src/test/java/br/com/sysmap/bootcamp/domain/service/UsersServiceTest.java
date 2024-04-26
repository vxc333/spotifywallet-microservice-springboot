package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.BootcampSysmapApplication;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private UsersService usersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ValidUser_Success() {
        Users user = Users.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        Users savedUserEntity = Users.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .password("encodedPassword")
                .build();

        when(usersRepository.save(any(Users.class))).thenReturn(savedUserEntity);

        Users savedUser = usersService.save(user);

        assertNotNull(savedUser );
        assertNotNull(savedUser .getId());
        assertEquals("encodedPassword", savedUser .getPassword());
        verify(usersRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(usersRepository, times(1)).save(any(Users.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void testSave_ExistingUser() {
        Users user = Users.builder()
                .email("test@example.com")
                .name("Test User")
                .password("password")
                .build();

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> usersService.save(user));

        verify(usersRepository, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(usersRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(walletRepository);
    }

    @Test
    void testLoadUserByUsername_Success() {
        Users user = Users.builder()
                .email("test@example.com")
                .name("Test User")
                .password("password")
                .build();

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = usersService.loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(usersRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String email = "nonexistent@example.com";

        when(usersRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usersService.loadUserByUsername(email));

        verify(usersRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindByEmail_Success() {
        Users user = Users.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .password("password")
                .build();

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Users result = usersService.findByEmail(user.getEmail());

        assertEquals(user, result);

        verify(usersRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testFindByEmail_UserNotFound() {
        String email = "nonexistent@example.com";

        when(usersRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usersService.findByEmail(email));

        verify(usersRepository, times(1)).findByEmail(email);
    }

    @Test
    void testAuth_Success() {
        AuthDto authDto = AuthDto.builder()
                .email("test@example.com")
                .password("password")
                .build();
        Users user = Users.builder()
                .email("test@example.com")
                .name("Test User")
                .password("password")
                .build();

        when(usersRepository.findByEmail(authDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authDto.getPassword(), user.getPassword())).thenReturn(true);

        AuthDto result = usersService.auth(authDto);

        assertEquals(user.getEmail(), result.getEmail());
        assertNotNull(result.getToken());
        assertEquals(user.getId(), result.getId());

        verify(usersRepository, times(1)).findByEmail(authDto.getEmail());
        verify(passwordEncoder, times(1)).matches(authDto.getPassword(), user.getPassword());
    }

    @Test
    void testAuth_InvalidPassword() {
        AuthDto authDto = AuthDto.builder()
                .password("password")
                .email("test@example.com")
                .build();
        Users user = Users.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .password("password")
                .build();

        when(usersRepository.findByEmail(authDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authDto.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> usersService.auth(authDto));

        verify(usersRepository, times(1)).findByEmail(authDto.getEmail());
        verify(passwordEncoder, times(1)).matches(authDto.getPassword(), user.getPassword());
    }

    @Test
    void testUpdate_Success() {
        Long userId = 1L;
        String existingEmail = "test@example.com";
        String existingName = "Test User";
        String existingPassword = "password";

        Users existingUser = Users.builder()
                .id(userId)
                .email(existingEmail)
                .name(existingName)
                .password(existingPassword)
                .build();

        String updatedName = "Updated User";

        Users updatedUser = Users.builder()
                .id(userId)
                .email(existingEmail)
                .name(updatedName)
                .password(existingPassword)
                .build();

        when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users result = usersService.update(userId, updatedUser);

        assertEquals(updatedName, result.getName());
        assertEquals(existingEmail, result.getEmail());
        assertEquals(existingPassword, result.getPassword());

        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void testUpdate_UserNotFound() {
        Long userId = 1L;

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> usersService.update(userId, Users.builder().build()));

        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_Success() {
        Long userId = 1L;
        Users user = Users.builder()
                .id(userId)
                .email("test@example.com")
                .name("Test User")
                .password("password")
                .build();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        Users result = usersService.findById(userId);

        assertEquals(user, result);

        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_UserNotFound() {
        Long userId = 1L;

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> usersService.findById(userId));

        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
    void testFindAll_Success() {
        List<Users> users = new ArrayList<>();
        users.add(Users.builder().id(1L).name("test1").build());

        when(usersRepository.findAll()).thenReturn(users);

        List<Users> result = usersService.findAll();

        assertEquals(users.size(), result.size());
        assertEquals(users.get(0).getName(), result.get(0).getName());

        verify(usersRepository, times(1)).findAll();
    }
}