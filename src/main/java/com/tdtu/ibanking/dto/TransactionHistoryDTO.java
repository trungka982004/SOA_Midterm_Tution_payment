package com.tdtu.ibanking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {
    private Long id;
    private String studentIdPaidFor;
    private String studentNamePaidFor;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String status;
    private String description;
}