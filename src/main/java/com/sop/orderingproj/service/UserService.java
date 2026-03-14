package com.sop.orderingproj.service;

import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sop.orderingproj.dto.CreateUserDto;
import com.sop.orderingproj.entities.Role;
import com.sop.orderingproj.entities.User;
import com.sop.orderingproj.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, 
        PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Integer id) {
        return this.repository
            .findById(id)
            .orElseThrow( () -> new IllegalArgumentException("User not found"));
    }

    public void createUser(CreateUserDto dto) {
        if(this.repository.countByEmail(dto.username()) == 0l) {
            User user = new User.Builder()
            .email(dto.username())
            .password(passwordEncoder.encode(dto.password()))
            .roles(Set.of(new Role.Builder().name(dto.role()).build()) )
            .build();
            this.repository.save(user);
        }
    }
}
