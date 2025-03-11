package com.example.carrental.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.carrental.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	void deleteByBookingId(Long bookingId);
}
