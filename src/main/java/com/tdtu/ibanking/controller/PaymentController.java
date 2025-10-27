package com.tdtu.ibanking.controller;

import com.tdtu.ibanking.dto.PaymentConfirmRequest;
import com.tdtu.ibanking.dto.PaymentInitiateRequest;
import com.tdtu.ibanking.dto.PayerInfoResponse;
import com.tdtu.ibanking.dto.StudentInfoResponse;
import com.tdtu.ibanking.dto.TransactionHistoryDTO;


import com.tdtu.ibanking.entity.Student;
import com.tdtu.ibanking.entity.User;
import com.tdtu.ibanking.entity.TransactionHistory;

import com.tdtu.ibanking.repository.StudentRepository;
import com.tdtu.ibanking.repository.UserRepository;
import com.tdtu.ibanking.repository.TransactionHistoryRepository;

import com.tdtu.ibanking.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;
	
	@Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @GetMapping("/payer-info")
    public ResponseEntity<?> getPayerInfo(Authentication authentication) {
    	String username = authentication.getName();
    	
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
	
	@GetMapping("/history")
    public ResponseEntity<?> getTransactionHistory(Authentication authentication) {
		String username = authentication.getName();
		
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<TransactionHistory> historyList = transactionHistoryRepository.findByUserIdOrderByTransactionDateDesc(user.getId());

        List<TransactionHistoryDTO> responseList = historyList.stream().map(history -> 
            new TransactionHistoryDTO(
                history.getId(),
                history.getStudent().getStudentId(),
                history.getStudent().getFullName(),
                history.getAmount(),
                history.getTransactionDate(),
                history.getStatus(),
                history.getDescription()
            )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(Authentication authentication, @RequestBody PaymentInitiateRequest request) {
    	String username = authentication.getName();
    	
    	try {
            paymentService.initiatePayment(username, request.getStudentId());
            return ResponseEntity.ok("OTP has been sent to your registered email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(Authentication authentication, @RequestBody PaymentConfirmRequest request) {
    	String username = authentication.getName();
    	
    	try {
            paymentService.confirmPayment(
            	username,
                request.getStudentId(),
                request.getOtpCode()
            );
            return ResponseEntity.ok("Payment successful!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}