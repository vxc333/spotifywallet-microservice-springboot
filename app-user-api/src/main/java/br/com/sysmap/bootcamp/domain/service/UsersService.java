package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user) {

        if(user.getEmail() == null || user.getPassword() == null || user.getName() == null){
            throw new IllegalArgumentException("All fields are mandatory");
        }

        Optional<Users> usersOptional = this.usersRepository.findByEmail(user.getEmail());
        if (usersOptional.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        user = user.toBuilder().password(this.passwordEncoder.encode(user.getPassword())).build();
        log.info("Saving user: {}", user);

        Users savedUser = this.usersRepository.save(user);

        Wallet wallet = Wallet.builder()
                .users(savedUser)
                .balance(BigDecimal.ZERO)
                .points(0L)
                .build();
        walletRepository.save(wallet);


        return savedUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(username);

        return usersOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Users findByEmail(String email) {
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AuthDto auth(AuthDto authDto) {
        Users users = this.findByEmail(authDto.getEmail());

        if (!this.passwordEncoder.matches(authDto.getPassword(), users.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StringBuilder password = new StringBuilder().append(users.getEmail()).append(":").append(users.getPassword());

        return AuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.toString().getBytes())
        ).id(users.getId()).build();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Users update(Long userId, Users updatedUser) {
        Users existingUser = this.usersRepository.findById(userId).orElse(null);
        if (existingUser == null) {
            log.info("User not found for update with ID: {}", userId);
            throw new IllegalArgumentException("User not found: " + userId);
        } else {
            Users updatedExistingUser = existingUser.toBuilder().name(updatedUser.getName()).email(updatedUser.getEmail()).build();
            log.info("Updating user: {}", updatedExistingUser);
            return usersRepository.save(updatedExistingUser);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users findById(Long userId) {
        Users user = this.usersRepository.findById(userId).orElse(null);
        if (user == null) {
            log.info("User not found with ID: {}", userId);
            throw new IllegalArgumentException("User not found: " + userId);
        } else {
            log.info("Found user: {}", userId);
            return user;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Users> findAll() {
        List<Users> users = usersRepository.findAll();
        if (users.isEmpty()) {
            log.info("No users found");
            throw new IllegalArgumentException("No users found");
        } else {
            log.info("Found {} users", users.size());
            return users;
        }
    }
}
