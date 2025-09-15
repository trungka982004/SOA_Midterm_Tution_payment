package com.tdtu.ibanking.controller;

import com.tdtu.ibanking.dto.PaymentConfirmRequest;
import com.tdtu.ibanking.dto.PaymentInitiateRequest;
import com.tdtu.ibanking.dto.PayerInfoResponse;
import com.tdtu.ibanking.dto.StudentInfoResponse;
import com.tdtu.ibanking.entity.Student;
import com.tdtu.ibanking.entity.User;
import com.tdtu.ibanking.repository.StudentRepository;
import com.tdtu.ibanking.repository.UserRepository;
import com.tdtu.ibanking.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/payer-info")
    public ResponseEntity<?> getPayerInfo(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        PayerInfoResponse response = new PayerInfoResponse(
            user.getFullName(),
            user.getPhoneNumber(),
            user.getEmail(),
            user.getAvailableBalance()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student-info/{studentId}")
    public ResponseEntity<?> getStudentInfo(@PathVariable String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        StudentInfoResponse response = new StudentInfoResponse(
            student.getStudentId(),
            student.getFullName(),
            student.getTuitionAmount()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentInitiateRequest request) {
        try {
            paymentService.initiatePayment(request.getUsername(), request.getStudentId());
            return ResponseEntity.ok("OTP has been sent to your registered email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        try {
            paymentService.confirmPayment(
            	request.getUsername(), 
                request.getStudentId(), 
                request.getOtpCode()
            );
            return ResponseEntity.ok("Payment successful!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}