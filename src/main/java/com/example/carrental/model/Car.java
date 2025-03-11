package com.example.carrental.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(nullable = false)
    private boolean availability = true; // Default availability to true

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcType acType; // AC or NON-AC

    @Column(nullable = false, unique = true)
    private String carNumber; // Car number plate

    // Four separate image path variables
    @Column(name = "image_path")
    private String imagePath;

   

    @Column(nullable = false)
    private int seater; // Number of seats (1 to 15)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType; // Fuel type (Diesel, Petrol, Electric, CNG)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmissionType; // Transmission (Manual, Automatic)

    // Enums for AC Type, FuelType, and TransmissionType
    public enum AcType {
        AC, NON_AC
    }

    public enum FuelType {
        DIESEL, PETROL, ELECTRIC, CNG
    }

    public enum TransmissionType {
        MANUAL, AUTOMATIC
    }
}
