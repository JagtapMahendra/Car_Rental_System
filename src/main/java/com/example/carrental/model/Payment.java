package com.example.carrental.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentMethod; // UPI, Credit Card, Debit Card, Visa

    private String upiId;         // Only for UPI payments
    private String qrCode;        // QR code path if needed

    private String cardNumber;    // Only for Card payments
    private String cardHolderName;
    private String cvv;
    private String expiryDate;

    @Column(nullable = false)
    private double amount;
    
    @Column(name = "amount_paid", nullable = false)
    private double amountPaid;  // Ensure this field is populated in the controller/service

    @Column(nullable = false)
    private String paymentStatus; // Success, Pending, Failed

	private LocalDateTime paymentDate;
    
    @PrePersist
    protected void onCreate() {
        this.paymentDate = LocalDateTime.now(); // Automatically sets the payment date
    }
    
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;  // Ensure booking is assigned before saving
    
    

}
