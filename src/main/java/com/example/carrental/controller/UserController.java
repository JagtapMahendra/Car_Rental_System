package com.example.carrental.controller;

import com.example.carrental.model.Booking;
import com.example.carrental.model.Car;
import com.example.carrental.model.EmailEntity;
import com.example.carrental.model.User;
import com.example.carrental.repository.BookingRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.service.BookingService;
import com.example.carrental.service.CarService;
import com.example.carrental.service.EmailService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CarService carService; // Inject CarService
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private BookingRepository bookingRepository;

    
    @Autowired
    private EmailService emailService;
    
    // Show Registration Page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Process Registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            model.addAttribute("error", "Email is already registered!");
            return "register";
        }

        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            model.addAttribute("error", "Full Name is required!");
            return "register";
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            model.addAttribute("error", "Email is required!");
            return "register";
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("error", "Password is required!");
            return "register";
        }

        user.setRole("USER");
        userRepository.save(user);

        return "redirect:/user/login?success=Registered Successfully! Please login.";
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "success", required = false) String success, Model model) {
        if (success != null) {
            model.addAttribute("successMessage", success);
        }
        return "user-login";
    }

    // Process Login
    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("user", user);
                return "redirect:/user/home";
            } else {
                model.addAttribute("error", "Incorrect password. Please try again.");
            }
        } else {
            model.addAttribute("error", "User not found. Please register first.");
        }
        return "user-login";
    }

   
    @GetMapping("/home")
    public String userHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user != null) {
            model.addAttribute("user", user);

            // Fetch only available cars from the database
            List<Car> availableCars = carService.getAvailableCars();
            model.addAttribute("cars", availableCars);

            return "home";
        } else {
            return "redirect:/user/login";
        }
    }
    
 // Fetch all bookings for a specific user
    @GetMapping("/bookings")
    public String getUserBookings(@RequestParam("userId") Long userId, Model model) {
        List<Booking> userBookings = bookingService.getBookingsByUserId(userId);
        model.addAttribute("bookings", userBookings);
        return "my-bookings";
    }
    
//    @GetMapping("/cancel-booking")
//    public String cancelBooking(@RequestParam("bookingId") Long bookingId, Model model) {
//        bookingService.cancelBooking(bookingId);
//        model.addAttribute("message", "Your booking has been successfully canceled.");
//        return "booking-cancel-success"; // Redirect to the success page
//    }

    @GetMapping("/cancel-booking")
    public String cancelBooking(@RequestParam("bookingId") Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return "booking-cancel-success"; // Redirect to the bookings page
    }

    @GetMapping("/con")
    public String name() {
    	return "contact";
    }

    
    
    @GetMapping("/contact/{id}")
    public String sendMail(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        
        List<Booking> bookings = bookingRepository.findByUserId(id);

        if (!bookings.isEmpty()) {
            // Get the user's email from the first booking
            String userEmail = bookings.get(0).getUser().getEmail();

            // Define the message content
            String messageContent = "Your car has been booked successfully! Thank you for choosing our service. We appreciate your trust!";

            // Call the email service method directly
            emailService.contactMail(userEmail, messageContent);

            // Add success message to redirect attributes
            redirectAttributes.addFlashAttribute("successMessage", "Booking confirmation email sent successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No bookings found for the selected user.");
        }

        return "redirect:/admin/bookings";
    }

    
    @PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException
	{
		//System.out.println("order fun executed");
		System.out.println(data);
		
		int amt=Integer.parseInt(data.get("amount").toString());
		
		var client =new RazorpayClient("rzp_test_oaCUW6qWKCoV15", "wxT3utsUtKdEmvJOOVA4VgsK");
		
		JSONObject options = new JSONObject();
		options.put("amount", amt*100);
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
		
		
		Order order = client.orders.create(options);
		
		System.out.println(order);

		
		return order.toString();
		
		
	}
    


}
