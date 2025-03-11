package com.example.carrental.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    // 🔹 Additional Booking Fields
    @Column(nullable = false, length = 20)
    private String driverLicenseNumber;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private String idProofImagePath;

    @Column(nullable = false)
    private LocalDate bookingDate = LocalDate.now(); // Set automatically at the time of booking
}
