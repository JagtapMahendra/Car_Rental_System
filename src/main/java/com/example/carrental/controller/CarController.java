package com.example.carrental.controller;

import com.example.carrental.model.Admin;
import com.example.carrental.model.Car;
import com.example.carrental.model.User;
import com.example.carrental.repository.AdminRepository;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class CarController {

    @Autowired
    private CarService carService;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @GetMapping("/home")
    public String showAvailableCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer minSeater,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean acType,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String transmission,
            Model model) {
        try {
            List<Car> cars = carService.getFilteredCars(brand, minSeater, maxPrice, acType, fuelType, transmission);
            model.addAttribute("cars", cars);
            model.addAttribute("brands", carService.getAllBrands()); // Fetch distinct brands
            model.addAttribute("fuelTypes", carService.getAllFuelTypes()); // Fetch distinct fuel types
            model.addAttribute("transmissions", carService.getAllTransmissions()); // Fetch distinct transmission types
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching car list. Please try again later.");
        }
        return "home";
    }


    
    @GetMapping("/admin/delete-car/{id}")
    public String deleteCar(@PathVariable Long id) {
        carService.deleteCarById(id);
        return "redirect:/admin/car-list"; // Redirect to the car list page
    }
    
    @GetMapping("/cars/details/{id}")
    public String carDetails(@PathVariable Long id, @RequestParam Long userId, Model model) {
        Optional<Car> carOpt = carRepository.findById(id);
        Optional<User> userOpt = userRepository.findById(userId);
        List<Admin> adminList = adminRepository.findAll();

        if (carOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/home"; // Redirect if car or user not found
        }

        Admin admin = adminList.isEmpty() ? null : adminList.get(0); // Get the first admin if available

        model.addAttribute("car", carOpt.get());
        model.addAttribute("user", userOpt.get());
        model.addAttribute("admin", admin); // Pass a single admin

        return "car-details"; // Load car-details.html
    }


   

}
