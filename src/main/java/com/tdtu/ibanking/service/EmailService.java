package com.tdtu.ibanking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code for Tuition Payment");
        message.setText("Your OTP code is: " + otp + ". It is valid for 5 minutes.");
        mailSender.send(message);
    }

    public void sendTransactionSuccessEmail(String toEmail, String studentName, String amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Tuition Payment Successful");
        message.setText("You have successfully paid " + amount + " VND for student " + studentName + ".");
        mailSender.send(message);
    }
}