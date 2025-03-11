package com.example.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.carrental.model.EmailEntity;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	
	public void contactMail(String userEmail, String messageContent) {
	    try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message);
	        
	        helper.setFrom("jagtapmahendram@gmail.com");  // Sender email
	        helper.setTo(userEmail);  // Use dynamic recipient email
	        helper.setSubject("Booking Update Notification");
	        helper.setText(messageContent);

	        javaMailSender.send(message);
	        System.out.println("Email sent to: " + userEmail);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
}
