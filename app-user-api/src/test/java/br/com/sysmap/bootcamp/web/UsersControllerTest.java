package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTest {

    private final UsersService usersService = Mockito.mock(UsersService.class);
    private final UsersController usersController = new UsersController(usersService);


    @Test
    void testSaveUser() {
        Users user = Users.builder()
                .id(1L)
                .name("testUser")
                .password("password")
                .build();
        when(usersService.save(user)).thenReturn(user);

        ResponseEntity<Users> response = usersController.save(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }


    @Test
    void testAuthUser() {
        AuthDto authDto = AuthDto.builder()
                .email("test@test.com")
                .password("password")
                .build();
        when(usersService.auth(authDto)).thenReturn(authDto);

        ResponseEntity<AuthDto> response = usersController.auth(authDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authDto, response.getBody());
    }

    @Test
    void testUpdateUser() {
        Long id = 1L;
        Users updatedUser = Users.builder()
                .id(id)
                .name("testUser")
                .password("password")
                .build();
        when(usersService.update(id, updatedUser)).thenReturn(updatedUser);

        ResponseEntity<Users> response = usersController.update(id, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Users user = Users.builder()
                .id(id)
                .name("testUser")
                .password("password")
                .build();
        when(usersService.findById(id)).thenReturn(user);

        ResponseEntity<Users> response = usersController.findByid(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testFindAllUsers() {
        List<Users> userList = new ArrayList<>();
        userList.add(Users.builder()
                .id(1L)
                .name("testUser1")
                .password("password1")
                .build());
        userList.add(Users.builder()
                .id(2L)
                .name("testUser2")
                .password("password2")
                .build());
        when(usersService.findAll()).thenReturn(userList);

        ResponseEntity<List<Users>> response = usersController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
        assertTrue(Objects.requireNonNull(response.getBody()).containsAll(userList));
    }
}