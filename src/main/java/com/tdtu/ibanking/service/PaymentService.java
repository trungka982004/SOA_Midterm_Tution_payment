package com.tdtu.ibanking.service;

import com.tdtu.ibanking.entity.OtpCode;
import com.tdtu.ibanking.entity.Student;
import com.tdtu.ibanking.entity.TransactionHistory;
import com.tdtu.ibanking.entity.User;
import com.tdtu.ibanking.repository.OtpCodeRepository;
import com.tdtu.ibanking.repository.StudentRepository;
import com.tdtu.ibanking.repository.TransactionHistoryRepository;
import com.tdtu.ibanking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final int OTP_VALIDITY_MINUTES = 5;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OtpCodeRepository otpCodeRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private EmailService emailService;

    public void initiatePayment(String username, String studentId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        if (student.getTuitionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("This student's tuition has already been paid.");
        }
        if (user.getAvailableBalance().compareTo(student.getTuitionAmount()) < 0) {
            throw new RuntimeException("Insufficient balance.");
        }

        String otp = generateOtp();
        OtpCode otpCode = new OtpCode();

        otpCode.setUser(user);
        
        otpCode.setCode(otp);
        otpCode.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otpCode.setIsUsed(false);
        otpCodeRepository.save(otpCode);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Transactional
    public void confirmPayment(String username, String studentId, String otpCode) {
        User lockedPayer = userRepository.findAndLockByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        validateOtp(lockedPayer.getEmail(), otpCode);

        Student lockedStudent = studentRepository.findAndLockByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        BigDecimal tuitionAmount = lockedStudent.getTuitionAmount();

        if (lockedPayer.getAvailableBalance().compareTo(tuitionAmount) < 0) {
            saveFailedTransaction(lockedPayer, lockedStudent, tuitionAmount, "Insufficient funds after lock.");
            throw new RuntimeException("Insufficient funds.");
        }
        
        if (tuitionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            saveFailedTransaction(lockedPayer, lockedStudent, tuitionAmount, "Tuition already paid by another transaction.");
            throw new RuntimeException("Tuition has already been paid by another transaction.");
        }

        lockedPayer.setAvailableBalance(lockedPayer.getAvailableBalance().subtract(tuitionAmount));
        userRepository.save(lockedPayer);

        lockedStudent.setTuitionAmount(BigDecimal.ZERO);
        studentRepository.save(lockedStudent);

        TransactionHistory history = new TransactionHistory();
        history.setUser(lockedPayer);
        
        history.setStudent(lockedStudent);
        
        history.setAmount(tuitionAmount);
        history.setTransactionDate(LocalDateTime.now());
        history.setStatus("SUCCESS");
        history.setDescription("Successfully paid tuition for student " + lockedStudent.getFullName());
        transactionHistoryRepository.save(history);

        emailService.sendTransactionSuccessEmail(lockedPayer.getEmail(), lockedStudent.getFullName(), tuitionAmount.toString());
    }

    private void validateOtp(String email, String code) {
        OtpCode otp = otpCodeRepository.findByCodeAndUser_Email(code, email)
                .orElseThrow(() -> new RuntimeException("Invalid OTP code."));

        if (otp.getIsUsed()) {
            throw new RuntimeException("This OTP has already been used.");
        }
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }
        
        otp.setIsUsed(true);
        otpCodeRepository.save(otp);
    }

    private void saveFailedTransaction(User user, Student student, BigDecimal amount, String reason) {
        TransactionHistory history = new TransactionHistory();
        history.setUser(user);
        
        history.setStudent(student);
        
        history.setAmount(amount);
        history.setTransactionDate(LocalDateTime.now());
        history.setStatus("FAILED");
        history.setDescription(reason);
        transactionHistoryRepository.save(history);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000); // 0-999999
        return String.format("%06d", num);
    }
}