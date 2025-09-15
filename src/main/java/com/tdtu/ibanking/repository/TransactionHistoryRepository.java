package com.tdtu.ibanking.repository;

import com.tdtu.ibanking.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findByUserIdOrderByTransactionDateDesc(Long userId);

}