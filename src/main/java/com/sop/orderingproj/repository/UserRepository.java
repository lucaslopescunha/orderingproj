package com.sop.orderingproj.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sop.orderingproj.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String username);

    Boolean existsByEmail(String username);

    Long countByEmail(String username);

}
