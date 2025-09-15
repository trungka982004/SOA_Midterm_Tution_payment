package com.tdtu.ibanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PayerInfoResponse {
    private String fullName;
    private String phoneNumber;
    private String email;
    private BigDecimal availableBalance;
}