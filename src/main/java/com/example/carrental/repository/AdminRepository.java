package com.example.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.carrental.model.Admin;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email); // Ensure this returns Optional<Admin>
    
    
}
