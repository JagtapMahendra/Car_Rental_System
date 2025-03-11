package com.example.carrental.controller;

import com.example.carrental.model.Booking;
import com.example.carrental.model.Car;
import com.example.carrental.model.User;
import com.example.carrental.repository.BookingRepository;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;
    
    
    @GetMapping("/book")
    public String showBookingForm(@RequestParam Long carId, @RequestParam Long userId, Model model) {
        Optional<Car> carOpt = carRepository.findById(carId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (carOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/user/home"; 
        }

        model.addAttribute("car", carOpt.get());
        
        model.addAttribute("user", userOpt.get());

        return "booking"; 
    }

    @PostMapping("/confirm")
    public String confirmBooking(@RequestParam Long carId,
                                 @RequestParam String pickupDate,
                                 @RequestParam String returnDate,
                                 @RequestParam String driverLicenseNumber,
                                 @RequestParam String address,
                                 @RequestParam("idProofImage") MultipartFile idProofImage,
                                 @RequestParam Long userId,
                                 Model model) throws IOException {

        Optional<Car> optionalCar = carRepository.findById(carId);
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalCar.isEmpty() || optionalUser.isEmpty()) {
            return "redirect:/user/home";
        }

        Car car = optionalCar.get();
        User user = optionalUser.get();
        LocalDate start = LocalDate.parse(pickupDate);
        LocalDate end = LocalDate.parse(returnDate);

        if (!end.isAfter(start)) {
            model.addAttribute("error", "Return date must be after pickup date.");
            return "redirect:/booking/book?carId=" + carId + "&userId=" + userId;
        }

        if (!car.isAvailability()) {
            model.addAttribute("error", "Car is not available.");
            return "redirect:/user/home";
        }

        long days = ChronoUnit.DAYS.between(start, end);
        BigDecimal totalPrice = BigDecimal.valueOf(days * car.getPricePerDay().doubleValue());

        // Ensure uploads directory exists
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Save ID proof image
        String fileName = System.currentTimeMillis() + "_" + idProofImage.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(idProofImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create and save booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setTotalAmount(totalPrice);
        booking.setDriverLicenseNumber(driverLicenseNumber);
        booking.setAddress(address);
        booking.setIdProofImagePath(fileName);

        bookingRepository.save(booking);

        // Mark car as unavailable
        carRepository.save(car);

        // Redirect to confirmation page with booking ID
        return "redirect:/booking/confirmation";
    }
    
    @GetMapping("/confirmation")
    public String showBookingConfirmation() {
        return "booking-confirmation"; 
    }


    
   


}
