package com.example.carrental.service;

import com.example.carrental.model.Booking;
import com.example.carrental.model.Car;
import com.example.carrental.model.User;
import com.example.carrental.repository.BookingRepository;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.PaymentRepository;
import com.example.carrental.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    public Booking bookCar(Long carId, Long userId, String startDate, String endDate) {
        Optional<Car> carOpt = carRepository.findById(carId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (carOpt.isEmpty() || userOpt.isEmpty()) {
            throw new IllegalArgumentException("Car or User not found.");
        }

        Car car = carOpt.get();
        User user = userOpt.get();

        if (!car.isAvailability()) {
            throw new IllegalStateException("Car is not available for booking.");
        }

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Return date must be after the pickup date.");
        }

        long days = ChronoUnit.DAYS.between(start, end);
        BigDecimal totalPrice = BigDecimal.valueOf(days).multiply(car.getPricePerDay());

        Booking booking = Booking.builder()
                .user(user)
                .car(car)
                .startDate(start)
                .endDate(end)
                .totalAmount(totalPrice)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        car.setAvailability(false);
        carRepository.save(car);

        return savedBooking;
    }
    
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    }
    
 // Fetch bookings for a specific user
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
//    
//    public void cancelBooking(Long bookingId) {
//        bookingRepository.deleteById(bookingId);
//    }

    
    @Transactional
    public void cancelBooking(Long bookingId) {
        // Step 1: Delete related payments first
        paymentRepository.deleteByBookingId(bookingId);

        // Step 2: Delete the booking
        bookingRepository.deleteById(bookingId);
    }

    
    
}
