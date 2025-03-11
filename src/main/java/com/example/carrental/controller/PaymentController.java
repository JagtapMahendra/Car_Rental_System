package com.example.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.carrental.model.Booking;
import com.example.carrental.model.Payment;
import com.example.carrental.service.BookingService;
import com.example.carrental.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private BookingService bookingService;

    // Updated method to accept total amount from the booking page
    @GetMapping("/form")
    public String showPaymentForm(@RequestParam("bookingId") Long bookingId,@RequestParam("amount") double amount, Model model) {
        Payment payment = new Payment();
        payment.setAmount(amount); // Set amount in Payment object
        
        
        model.addAttribute("bookingId", bookingId);

        model.addAttribute("payment", payment);
        model.addAttribute("amount", amount); // Pass amount to the frontend
        return "payment-form"; // This should match your Thymeleaf template name
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam("bookingId") Long bookingId,@ModelAttribute Payment payment, Model model) {
    	
    	
    	Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            model.addAttribute("error", "Booking not found!");
            return "payment-form";
        }

        payment.setBooking(booking);  // Associate payment with the correct booking
    	
        payment.setAmountPaid(payment.getAmount()); // Set the paid amount
        payment.setPaymentDate(LocalDateTime.now()); // Set the payment date

    	
        Payment savedPayment = paymentService.processPayment(payment);
        model.addAttribute("message", "Payment Successful!");
        model.addAttribute("payment", savedPayment);
        return "payment-success";
    }

    @GetMapping("/list")
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payment-list";
    }
}
