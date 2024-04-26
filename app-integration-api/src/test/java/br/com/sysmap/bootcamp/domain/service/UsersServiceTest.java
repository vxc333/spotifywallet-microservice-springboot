package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UsersServiceTest {

    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersRepository usersRepository;

    public UsersServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String username = "test@example.com";
        Users user = Users.builder()
                .email(username)
                .password("password").build();

        when(usersRepository.findByEmail(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = usersService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistent@example.com";
        when(usersRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usersService.loadUserByUsername(username));
    }

    @Test
    void testFindByEmail_UserFound() {
        String username = "test@example.com";
        Users user = Users.builder()
                .email(username)
                .build();
        user.setEmail(username);
        when(usersRepository.findByEmail(username)).thenReturn(Optional.of(user));

        Users foundUser = usersService.findByEmail(username);

        assertNotNull(foundUser);
        assertEquals(username, foundUser.getEmail());
    }

    @Test
    void testFindByEmail_UserNotFound() {
        String username = "nonexistent@example.com";
        when(usersRepository.findByEmail(username)).thenReturn(Optional.empty());

        Users foundUser = usersService.findByEmail(username);

        assertNull(foundUser);
    }
}
