package com.sop.orderingproj.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sop.orderingproj.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    Optional<Role> findByName(String name);

}
