package com.example.carrental.controller;

import com.example.carrental.model.Admin;
import com.example.carrental.model.Booking;
import com.example.carrental.model.Car;
import com.example.carrental.model.Car.FuelType;
import com.example.carrental.model.Car.TransmissionType;
import com.example.carrental.repository.AdminRepository;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    // Show Registration Page
    @GetMapping("/register")
    public String showAdminRegistrationForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin-register";
    }

    // Process Registration for Admin
    @PostMapping("/register")
    public String registerAdmin(@ModelAttribute("admin") Admin admin, Model model) {
        Optional<Admin> existingAdmin = adminRepository.findByEmail(admin.getEmail());
        if (existingAdmin.isPresent()) {
            model.addAttribute("error", "Email is already registered!");
            return "admin-register";
        }

        if (admin.getFullName() == null || admin.getFullName().isEmpty()) {
            model.addAttribute("error", "Full Name is required!");
            return "admin-register";
        }
        if (admin.getEmail() == null || admin.getEmail().isEmpty()) {
            model.addAttribute("error", "Email is required!");
            return "admin-register";
        }
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            model.addAttribute("error", "Password is required!");
            return "admin-register";
        }

        admin.setRole("ADMIN");
        adminRepository.save(admin);

        return "redirect:/admin/login?success=Registered Successfully! Please login.";
    }

    @GetMapping("/login")
    public String showAdminLoginForm(@RequestParam(value = "success", required = false) String success, Model model) {
        if (success != null) {
            model.addAttribute("successMessage", success);
        }
        return "admin-login";
    }

    @PostMapping("/login")
    public String loginAdmin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            if (admin.getPassword().equals(password)) {
                session.setAttribute("admin", admin);
                return "redirect:/admin/dashboard";
            }
        }

        model.addAttribute("error", "Invalid email or password");
        return "admin-login";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");

        if (admin != null) {
            model.addAttribute("admin", admin);
            return "admin-dashboard";
        } else {
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/add-car")
    public String showAddCarForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("transmissionTypes", TransmissionType.values());
        return "admin-add-car";
    }

    @PostMapping("/add-car")
    public String addCar(@ModelAttribute("car") Car car, 
                         @RequestParam("carImage") MultipartFile file, 
                         @RequestParam("seater") int seater,
                         @RequestParam("fuelType") FuelType fuelType,
                         @RequestParam("transmissionType") TransmissionType transmissionType,
                         Model model) {
        if (car.getModel() == null || car.getModel().isEmpty()) {
            model.addAttribute("error", "Car Model is required!");
            return "admin-add-car";
        }
        if (car.getBrand() == null || car.getBrand().isEmpty()) {
            model.addAttribute("error", "Car Brand is required!");
            return "admin-add-car";
        }
        if (car.getPricePerDay() == null) {
            model.addAttribute("error", "Car Price per Day is required!");
            return "admin-add-car";
        }
        if (seater < 1 || seater > 15) {
            model.addAttribute("error", "Seater must be between 1 and 15!");
            return "admin-add-car";
        }

        car.setSeater(seater);
        car.setFuelType(fuelType);
        car.setTransmissionType(transmissionType);

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                System.out.println(path);
                car.setImagePath(file.getOriginalFilename());
            } catch (IOException e) {
                model.addAttribute("error", "Failed to upload image!");
                return "admin-add-car";
            }
        }
        
        carRepository.save(car);
        return "redirect:/admin/dashboard?success=Car added successfully!";
    }

    @GetMapping("/update-car/{id}")
    public String showUpdateCarForm(@PathVariable("id") Long id, Model model) {
        Optional<Car> car = carRepository.findById(id);

        if (car.isPresent()) {
            model.addAttribute("car", car.get());
            model.addAttribute("fuelTypes", FuelType.values());
            model.addAttribute("transmissionTypes", TransmissionType.values());
            return "admin-update-car";
        } else {
            model.addAttribute("error", "Car not found!");
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/update-car")
    public String updateCar(@ModelAttribute("car") Car car, 
                            @RequestParam("carImage") MultipartFile file,
                            @RequestParam("seater") int seater,
                            @RequestParam("fuelType") FuelType fuelType,
                            @RequestParam("transmissionType") TransmissionType transmissionType,
                            Model model) {
        Optional<Car> existingCar = carRepository.findById(car.getId());
        if (existingCar.isPresent()) {
            Car carToUpdate = existingCar.get();
            carToUpdate.setBrand(car.getBrand());
            carToUpdate.setModel(car.getModel());
            carToUpdate.setPricePerDay(car.getPricePerDay());
            carToUpdate.setAvailability(car.isAvailability());
            carToUpdate.setSeater(seater);
            carToUpdate.setFuelType(fuelType);
            carToUpdate.setTransmissionType(transmissionType);

            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                    Files.write(path, bytes);
                    carToUpdate.setImagePath(file.getOriginalFilename());
                } catch (IOException e) {
                    model.addAttribute("error", "Failed to upload image!");
                    return "admin-update-car";
                }
            }
            
            carRepository.save(carToUpdate);
            return "redirect:/admin/car-list";
        }
        return "redirect:/admin/dashboard?error=Car not found";
    }
    
    @GetMapping("/car-list")
    public String viewCarList(Model model) {
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "car-list";
    }
    
    @GetMapping("/bookings")
    public String getUserBookings(Model model) {
        List<Booking> userBookings = bookingRepository.findAll();
        
        model.addAttribute("bookings", userBookings);
        return "customer-bookings";
    }
}
