package com.example.carrental.repository;

import com.example.carrental.model.Booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByUserId(Long userId);
	
	
}
