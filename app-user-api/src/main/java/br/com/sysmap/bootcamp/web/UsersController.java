package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name="Users", description = "Users API")
public class UsersController {

    private final UsersService usersService;

    @Operation(summary = "Saver user")
    @PostMapping("/create")
    public ResponseEntity<Users> save(@RequestBody Users user) {
        try {
            Users savedUser = this.usersService.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Auth user")
    @PostMapping("/auth")
    public ResponseEntity<AuthDto> auth(@RequestBody AuthDto user) {
        return ResponseEntity.ok(this.usersService.auth(user));
    }

    @Operation(summary = "Update user")
    @PutMapping("/uptade/{id}")
    public ResponseEntity<Users> update(@PathVariable Long id, @RequestBody Users updatedUser) {
        try {
            Users updatedUsers = this.usersService.update(id, updatedUser);
            return ResponseEntity.ok(updatedUsers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Users> findByid(@PathVariable Long id) {
        try {
            Users user = this.usersService.findById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "List users")
    @GetMapping()
    public ResponseEntity<List<Users>> findAll() {
        try {
            List<Users> users = this.usersService.findAll();
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
