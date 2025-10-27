package com.tdtu.ibanking.repository;

import com.tdtu.ibanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findAndLockByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findById(Long id);
}