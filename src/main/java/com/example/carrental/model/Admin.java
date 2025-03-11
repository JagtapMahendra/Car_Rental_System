package com.example.carrental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatically generate the ID
    private Long id;
    
    private String fullName;
    private String email;
    private String password;
    private String role; // This should be "ADMIN" for admin users
    private String phoneNumber; // Added phone number field
}
