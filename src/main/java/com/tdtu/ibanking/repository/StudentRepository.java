package com.tdtu.ibanking.repository;

import com.tdtu.ibanking.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Student> findAndLockByStudentId(String studentId);
}