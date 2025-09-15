package com.tdtu.ibanking.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoResponse {
    private String studentId;
    private String fullName;
    private BigDecimal tuitionAmount;
}