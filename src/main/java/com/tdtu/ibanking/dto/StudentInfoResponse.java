package com.tdtu.ibanking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoResponse {
    private String studentId;
    private String fullName;
    private BigDecimal tuitionAmount;
}