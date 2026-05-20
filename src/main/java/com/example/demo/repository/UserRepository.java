package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long>{
    // Search by name (partial match)
    List<User> findByNameContainingIgnoreCase(String name);
    // Find by email
    Optional<User> findByEmail(String email);
}
